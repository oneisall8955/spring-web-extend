package com.oneisall.spring.web.extend.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

/**
 * @author liuzhicong
 **/
@PropertySource(value = {"classpath:i18n/messages*.properties"})
@Slf4j
public class I18nMessage {

    /**
     * 将国际化信息存放在一个map中
     */
    private static final Map<String, ResourceBundle> MESSAGES = new HashMap<String, ResourceBundle>();

    /**
     * 获取国际化信息
     */
    public static String getMessage(String key, String defaultMsg) {
        // 获取语言，这个语言是从header中的Accept-Language中获取的，
        // 会根据Accept-Language的值生成符合规则的locale，如zh、en等，否则获取得是主机语言
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle languageBundle = MESSAGES.get(locale.getLanguage());
        try {
            if (languageBundle == null) {
                synchronized (MESSAGES) {
                    //在这里读取配置信息
                    languageBundle = MESSAGES.get(locale.getLanguage());
                    if (languageBundle == null) {
                        languageBundle = ResourceBundle.getBundle("i18n/messages", locale);
                        MESSAGES.put(locale.getLanguage(), languageBundle);
                    }
                }
            }
        } catch (Exception e) {
            log.error("国际化初始化失败，文件不存在或者格式错误:locale {}", locale);
        }

        return Optional.ofNullable(languageBundle).filter(m -> m.containsKey(key)).map(m -> m.getString(key)).orElse(defaultMsg);
    }
}
