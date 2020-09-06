package com.ampaschal.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailRequest {

    private String recipientAddress;

    private String subject;

    private String message;

    private String senderName;

    private String senderAddress;


}
