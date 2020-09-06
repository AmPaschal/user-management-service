package com.ampaschal.entities;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailParam {

    /**
     * The email this message is to be sent to
     */
    private String toAddress;


    /**
     * The subject of the message needed for email
     */
    private String subject;


    /**
     * The Message body for the SMS or Email
     */
    private String message;

    /**
     * The display name on the mail
     */
    private String senderName;

    /**
     * The address of the sender
     */
    private String senderAddress;


}
