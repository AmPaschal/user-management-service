package com.ampaschal.utils;

import com.ampaschal.enums.IResponseBundleCode;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
@ApplicationScoped
public class ResourceBundleUtil {

    public String getMessage(IResponseBundleCode responseBundleCode, Locale locale) {
        return this.getMessage(responseBundleCode.getKey(), responseBundleCode.getDefaultTemplate(), locale);
    }

    String getMessage(String key, String defaultTemplate, Locale locale) {
        if (locale == null) {
            locale = getDefaultValidLocale(null);
        }

        String template = this.getMessage(key, locale);
        if (template == null) {
            if (defaultTemplate == null) {
                return null;
            }

            template = defaultTemplate;
        }

        return template;
    }

    private Locale getDefaultValidLocale(Locale locale) {
        if (locale == null) {
            return new Locale("en", "US");
        } else if (!this.isValidLocale(locale)) {
            throw new IllegalArgumentException("invalid default locale specified");
        } else {
            return locale;
        }
    }

    private boolean isValidLocale(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null;
        } catch (MissingResourceException var3) {
            log.warn("Invalid Locale will use default ", var3);
            return false;
        }
    }

    private String getMessage(String key, Locale locale) {
        ResourceBundle bundle;
        try {
            bundle = getResourceBundle(locale);
        } catch (MissingResourceException var6) {
            log.warn("getMessage Error. Default Resource bundle not found. Error message : {}", var6.getMessage());
            return null;
        }

        try {
            return bundle.getString(key);
        } catch (MissingResourceException | NullPointerException var5) {
            log.warn("getMessage Error. Key : {} not found in bundle. Error message : {}", key, var5.getMessage());
            return null;
        }
    }

    private ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle = null;

        try {
            if (this.isValidLocale(locale)) {
                String resourceBundleBaseName = "user_management";
                bundle = ResourceBundle.getBundle(resourceBundleBaseName, locale);
            }
        } catch (MissingResourceException var4) {
            log.warn("Resource bundle not found picking default... {}", var4.getMessage());
        }

        return bundle;
    }


}
