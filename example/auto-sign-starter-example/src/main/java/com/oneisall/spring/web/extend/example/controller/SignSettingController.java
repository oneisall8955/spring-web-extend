package com.oneisall.spring.web.extend.example.controller;

import com.oneisall.spring.web.extend.example.AfterAppendCustomSignHandler;
import com.oneisall.spring.web.extend.example.CustomSignHandler;
import com.oneisall.spring.web.extend.sign.annotation.SignRequired;
import com.oneisall.spring.web.extend.sign.configuration.SignDataFrom;
import com.oneisall.spring.web.extend.sign.configuration.SignKeyFrom;
import com.oneisall.spring.web.extend.sign.handler.AbstractSignHandler;
import com.oneisall.spring.web.extend.sign.handler.SignHandlerFactory;
import com.oneisall.spring.web.extend.sign.properties.SignProperties;
import com.oneisall.spring.web.extend.utils.CollectionMapUtil;
import com.oneisall.spring.web.extend.utils.HmacUtil;
import com.oneisall.spring.web.extend.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
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
                               @PathVariable SignDataFrom from,
                               @PathVariable String uniqueName) {
        SignProperties.SignSetting signSetting = SignProperties.findUniqueName(uniqueName);
        String signedSecret = signSetting.getSignedSecret();
        Map<String, Object> result = new LinkedHashMap<>();
        if (from == SignDataFrom.BODY) {
            String json = JsonUtils.objectToJson(data);
            String hmac = HmacUtil.generateHmac(json, signedSecret);
            result.put("sign", hmac);
            result.put("json", json);
        } else {
            Map<String, String> hashMap = CollectionMapUtil.newLinkedHashMapWithExpectedSize(data.size());
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

    /**
     * 配置的路径不是签名的
     */
    @PostMapping("/notVerify")
    public Object notVerify(@RequestBody Foo foo) {
        return foo;
    }

    /**
     * 配置的路径不是签名的
     */
    @PostMapping("/needVerityBotNoneAnnotation")
    public Object needVerityBotNone(@RequestBody Foo foo) {
        return foo;
    }

    /**
     * 使用指定验签处理器
     */
    @PostMapping("/customSignHandler")
    @SignRequired(title = "使用指定验签处理器",signKey = "oneisall-sign", usingVerifier = CustomSignHandler.class)
    public Object customSignHandler(@RequestBody Foo foo) {
        return foo;
    }

    /**
     * 非容器管理的handler后期反射创建
     */
    @PostMapping("/afterAppendCustomSignHandler")
    @SignRequired(title = "非容器管理的handler后期反射创建",signKey = "oneisall-sign", usingVerifier = AfterAppendCustomSignHandler.class)
    public Object afterAppendCustomSignHandler(@RequestBody Foo foo) {
        return foo;
    }

    /**
     * 签名
     */
    @SignRequired(title = "签名测试使用配置：header_body", usingProperties = true, uniqueName = "foo-app")
    @PostMapping("/verifyPostUsingProperties")
    public Object verifyPostUsingProperties(@RequestBody Foo foo) {
        return foo;
    }

    @SignRequired(title = "签名测试使用配置_param", usingProperties = true,
            uniqueName = "foo-app", signDataFrom = SignDataFrom.PARAM, signKeyFrom = SignKeyFrom.PARAM)
    @GetMapping("/verifyGetUsingProperties")
    public Object verifyGetUsingProperties(@ModelAttribute Foo foo) {
        return foo;
    }

    @SignRequired(title = "签名测试使用配置_param_includes", usingProperties = true,
            uniqueName = "foo-app", signDataFrom = SignDataFrom.PARAM, signKeyFrom = SignKeyFrom.PARAM,
            includes = "name"
    )
    @GetMapping("/verifyGetUsingPropertiesIncludes")
    public Object verifyGetUsingPropertiesIncludes(@ModelAttribute Foo foo) {
        return foo;
    }

    @Resource
    private SignHandlerFactory signHandlerFactory;

    @GetMapping("/getSignHandler")
    public Object getSignHandler() {
        List<AbstractSignHandler> instances = signHandlerFactory.getInstances();
        return instances.size();
    }
}
