package com.ampaschal.repositories;

import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.enums.ISettingEnum;
import com.ampaschal.mongo.Setting;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class SettingsRepository extends MongoDbDataService {

    public static final String SETTINGS_NAME = "name";

    public String getSettingValue(ISettingEnum settingEnum) {
        return this.getSettingValue(settingEnum.getName(), settingEnum.getValue(), settingEnum.getDescription(), null);
    }

    public String getSettingValue(ISettingEnum settingEnum, String productId) {
        return this.getSettingValue(settingEnum.getName(), settingEnum.getValue(), settingEnum.getDescription(), productId);
    }

    private void createSetting(String name, String defaultValue, String description) {
        Setting setting = new Setting();
        setting.setName(name);
        setting.setValue(defaultValue);
        setting.setDescription(description);

        save(setting, DatabaseCollections.SETTINGS.getName(), Setting.class);
    }

    public String getSettingValue(String name, String defaultValue, String description, String productId) {

        Setting setting = null;
         if (productId != null) {
             Bson filter = Filters.and(Filters.eq(SETTINGS_NAME, name),
                     Filters.eq("productId", productId));
             setting = find(filter, DatabaseCollections.PRODUCT_SETTINGS.getName(), Setting.class).first();
         }

         if (setting == null) {
             Bson filter = Filters.and(Filters.eq(SETTINGS_NAME, name), Filters.exists("productId", false));
             setting = find(filter, DatabaseCollections.SETTINGS.getName(), Setting.class).first();
         }

        String settingValue;
        if (setting == null) {
            if (defaultValue != null) {
                this.createSetting(name, "".equals(defaultValue) ? " " : defaultValue, description);
            }
            settingValue = defaultValue;
        } else {
            settingValue = setting.getValue();
        }

        return settingValue;
    }

    public boolean getSettingValueBooleanPrimitive(ISettingEnum settingsEnum, String productId) {
        String settingValue = this.getSettingValue(settingsEnum, productId);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        return Boolean.parseBoolean(settingValue);
    }

    public boolean getSettingValueBooleanPrimitive(ISettingEnum settingsEnum) {
        return getSettingValueBooleanPrimitive(settingsEnum, null);
    }

    public long getSettingValueLongPrimitive(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Long.parseLong(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting long value : {}", settingValue);
            return Long.parseLong(settingsEnum.getValue());
        }
    }

    public Long getSettingValueLong(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Long.valueOf(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting long value : {}", settingValue);
            return Long.valueOf(settingsEnum.getValue());
        }
    }

    public int getSettingValueIntPrimitive(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting int value : {}", settingValue);
            return Integer.parseInt(settingsEnum.getValue());
        }
    }

    public Integer getSettingValueInt(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Integer.valueOf(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting Integer value : {}", settingValue);
            return Integer.valueOf(settingsEnum.getValue());
        }
    }

    public Float getSettingValueFloat(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Float.valueOf(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting Float value : {}", settingValue);
            return Float.valueOf(settingsEnum.getValue());
        }
    }

    public float getSettingValueFloatPrimitive(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return Float.parseFloat(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting float value : {}", settingValue);
            return Float.parseFloat(settingsEnum.getValue());
        }
    }

    public BigDecimal getSettingValueBigDecimal(ISettingEnum settingsEnum) {
        String settingValue = this.getSettingValue(settingsEnum);
        if (StringUtils.isBlank(settingValue)) {
            settingValue = settingsEnum.getValue();
        } else {
            settingValue = settingValue.trim();
        }

        try {
            return new BigDecimal(settingValue);
        } catch (NumberFormatException var4) {
            log.warn("Invalid setting BigDecimal value : {}", settingValue);
            return new BigDecimal(settingsEnum.getValue());
        }
    }

}
