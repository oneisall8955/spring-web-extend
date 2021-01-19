package com.oneisall.spring.web.extend.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HmacSHA256 签名
 *
 * @author liuzhicong
 **/
@Slf4j
public class HmacUtil {

    public static boolean verifyText(String text, String signedSecret, String requestHmac) {
        return Objects.equals(generateHmac(text, signedSecret), requestHmac);
    }

    public static String generateHmac(String text, String signedSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(signedSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("generateHmac签名失败");
            throw new IllegalArgumentException("cannot generate hmac");
        }
    }

    public static boolean verifyParameters(Map<String, String> parameters, String signedSecret, String requestHmac) {
        return Objects.equals(generateHmac(parameters, signedSecret), requestHmac);
    }

    public static String generateHmac(Map<String, String> parameters, String signedSecret) {
        try {
            String msg = urlParam(parameters);
            Mac instance = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(signedSecret.getBytes(), "HmacSHA256");
            instance.init(secretKeySpec);
            byte[] bytes = instance.doFinal(msg.getBytes());
            return  byteArrayToHexString(bytes);
        } catch (Exception e) {
            log.error("verifyParameters签名失败");
            throw new IllegalArgumentException("cannot generate hmac");
        }
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String urlParam(Map<String, String> params) {
        List<String> paramNames = new ArrayList<String>(params.size());
        paramNames.addAll(params.keySet());
        //升序
        Collections.sort(paramNames);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            String paramName = paramNames.get(i);
            if (i == params.size() - 1) {
                sb.append(paramName).append("=").append(params.get(paramName));
            } else {
                sb.append(paramName).append("=").append(params.get(paramName)).append("&");
            }
        }
        return sb.toString();
    }
}
