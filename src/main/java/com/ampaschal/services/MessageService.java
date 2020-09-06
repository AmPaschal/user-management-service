package com.ampaschal.services;

import com.ampaschal.entities.EmailParam;
import com.ampaschal.entities.SmsParam;
import com.ampaschal.enums.SettingEnum;
import com.ampaschal.messaging.SendEmailRequest;
import com.ampaschal.messaging.SendSmsRequest;
import com.ampaschal.repositories.SettingsRepository;
import com.ampaschal.restartifacts.BaseResponse;
import com.ampaschal.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.util.Collections;

/**
 * @author Amusuo Paschal C.
 * @since 7/22/2020 10:12 AM
 */

@Slf4j
@RequestScoped
public class MessageService {

    @Inject
    MessageUtils messageUtils;

    @Inject
    SettingsRepository settingsRepository;

    public BaseResponse sendSms(@ObservesAsync SmsParam smsParam) {

        SendSmsRequest request = new SendSmsRequest();
        request.setMessage(smsParam.getMessage());
        request.setRecipient(smsParam.getRecipient());

        String smsUrl = settingsRepository.getSettingValue(SettingEnum.SEND_SMS_URL);
        return messageUtils.post(smsUrl, request, BaseResponse.class, Collections.emptyMap());
    }


    public BaseResponse sendEmail(@ObservesAsync EmailParam emailParam) {

        SendEmailRequest request = new SendEmailRequest();
        request.setMessage(emailParam.getMessage());
        request.setRecipientAddress(emailParam.getToAddress());
        request.setSubject(emailParam.getSubject());
        request.setSenderAddress(emailParam.getSenderAddress());
        request.setSenderName(emailParam.getSenderName());

        String emailUrl = settingsRepository.getSettingValue(SettingEnum.SEND_EMAIL_URL);

        return messageUtils.post(emailUrl, request, BaseResponse.class, Collections.emptyMap());
    }
}
