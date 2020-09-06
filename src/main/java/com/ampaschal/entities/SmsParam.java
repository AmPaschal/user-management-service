package com.ampaschal.entities;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsParam {

    /**
     * The number this message is to be sent to
     */
    private String recipient;


    /**
     * The Message body for the SMS or Email
     */
    private String message;
}
