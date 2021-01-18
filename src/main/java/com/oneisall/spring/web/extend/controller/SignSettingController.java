package com.oneisall.spring.web.extend.controller;

import com.oneisall.spring.web.extend.common.configuration.sign.SignRequired;
import com.oneisall.spring.web.extend.common.properties.SignProperties;
import com.oneisall.spring.web.extend.common.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.common.utils.HmacUtil;
import com.oneisall.spring.web.extend.common.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 签名认证
 *
 * @author liuzhicong
 **/
@RestController
@RequestMapping("/sign")
@Slf4j
public class SignSettingController {

    @Setter
    @Getter
    public static class Foo {
        private String name;
        private Integer age;
    }

    @PostMapping("/generateSign/{from}/{uniqueName}")
    public Object generateSign(@RequestBody Map<String, Object> data,
                               @PathVariable SignRequired.SignDataFrom from,
                               @PathVariable String uniqueName) {
        SignProperties.SignSetting signSetting = SignProperties.findUniqueName(uniqueName);
        String signedSecret = signSetting.getSignedSecret();
        Map<String, Object> result = new LinkedHashMap<>();
        if (from == SignRequired.SignDataFrom.BODY) {
            String json = JsonUtils.objectToJson(data);
            String hmac = HmacUtil.generateHmac(json, signedSecret);
            result.put("sign", hmac);
            result.put("json", json);
        } else {
            HashMap<String, String> hashMap = CollectionMapUtil.initNewHashMap(data.size());
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    hashMap.put(entry.getKey(), value.toString());
                }
            }
            String urlParam = HmacUtil.urlParam(hashMap);
            String hmac = HmacUtil.generateHmac(hashMap, signedSecret);
            result.put("sign", hmac);
            result.put("urlParam", urlParam);
        }
        return result;
    }


    @PostMapping("/notVerify")
    public Object notVerify(@RequestBody Foo foo, HttpServletRequest httpServletRequest) {
        log.info("httpServletRequest class:{}", httpServletRequest.getClass());
        return foo;
    }

    @SignRequired(title = "签名测试使用配置", usingProperties = true, uniqueName = "foo-app")
    @PostMapping("/verifyPostUsingProperties")
    public Object verifyPostUsingProperties(@RequestBody Foo foo, HttpServletRequest httpServletRequest) {
        log.info("httpServletRequest class:{}", httpServletRequest.getClass());
        return foo;
    }

    @SignRequired(title = "签名测试使用配置", usingProperties = true,
            uniqueName = "foo-app", signDataFrom = SignRequired.SignDataFrom.PARAM, requestSignFrom = SignRequired.RequestSignFrom.PARAM)
    @GetMapping("/verifyGetUsingProperties")
    public Object verifyGetUsingProperties(@ModelAttribute Foo foo, HttpServletRequest httpServletRequest) {
        log.info("httpServletRequest class:{}", httpServletRequest.getClass());
        return foo;
    }
}
