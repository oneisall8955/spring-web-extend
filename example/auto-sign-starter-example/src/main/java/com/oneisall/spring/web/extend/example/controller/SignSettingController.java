package com.oneisall.spring.web.extend.example.controller;

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
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
            uniqueName = "foo-app", signDataFrom = SignDataFrom.PARAM, signKeyFrom = SignKeyFrom.PARAM)
    @GetMapping("/verifyGetUsingProperties")
    public Object verifyGetUsingProperties(@ModelAttribute Foo foo, HttpServletRequest httpServletRequest) {
        log.info("httpServletRequest class:{}", httpServletRequest.getClass());
        return foo;
    }

    @Resource
    private SignHandlerFactory signHandlerFactory;

    @GetMapping("/getSignHandler")
    public Object getSignHandler(){
        List<AbstractSignHandler> instances = signHandlerFactory.getInstances();
        return instances.size();
    }
}
