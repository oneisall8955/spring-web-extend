package com.oneisall.spring.web.extend.common.properties;

import com.oneisall.spring.web.extend.common.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.common.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 签名配置
 *
 * @author liuzhicong
 **/
@Setter
@Getter
@Slf4j
@ConfigurationProperties(prefix = "sign-config")
public class SignProperties {

    /** redis锁定时间集合，刷新时候重置 */
    public static Map<String, SignSetting> CACHE = new LinkedHashMap<>();

    @Setter
    @Getter
    @ToString
    public static class SignSetting {
        /** 业务唯一key */
        private String uniqueName;
        /** 签名密钥 */
        private String signedSecret;
    }

    /** 签名密钥 */
    private List<SignSetting> signedList;

    @Value("${sign-config.paths}")
    private List<String> paths;

    @PostConstruct
    public void init() {
        log.info("即将重新设置签名密钥配置");
        if (CollectionMapUtil.isEmpty(signedList)) {
            CACHE = Collections.emptyMap();
        } else {
            CACHE = signedList.stream().collect(
                    Collectors.toMap(
                            SignSetting::getUniqueName, Function.identity(),
                            (o1, o2) -> {
                                log.warn("签名密钥配置发现重复项，{}，{}。以{}为准", o1, o2, o2);
                                return o2;
                            },
                            LinkedHashMap::new
                    )
            );
        }
        log.info("重新刷新的签名密钥配置为：{}", JsonUtils.objectToJson(CACHE));
    }

    public static SignSetting findUniqueName(String key) {
        return CACHE.get(key);
    }
}
