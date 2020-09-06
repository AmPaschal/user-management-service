package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Amusuo Paschal C.
 * @since 8/20/2020 4:28 PM
 */

@Getter
@Setter
public class OrganizationSummary implements Serializable {

    private static final long serialVersionUID = 8725248357302427598L;
    private String name;
    private String rcNumber;
    private boolean activationStatus;
    private String adminEmail;
    private String industry;
}
