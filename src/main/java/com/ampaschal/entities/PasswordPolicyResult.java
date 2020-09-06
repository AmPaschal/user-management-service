package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PasswordPolicyResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3814991439430201062L;
	
	private Integer minLength = 8;
	private Integer minAllowedAlphabetCount = 1;
	private Integer minAllowedDigitCount = 1;
	private Integer minAllowedSpecialCharacterCount = 1;
	private Integer minAllowedUpperCaseCharacterCount = 1;
	private Integer minAllowedLowerCaseCharacterCount = 1;
	private boolean expiryEnabled = true;
	private Integer expiryDuration = 90; // in days
	private boolean reuseEnabled;
	private Integer historyLimit = 3;

}
