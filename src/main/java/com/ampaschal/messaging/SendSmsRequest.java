package com.ampaschal.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSmsRequest {

    private String recipient;

    private String message;
}
