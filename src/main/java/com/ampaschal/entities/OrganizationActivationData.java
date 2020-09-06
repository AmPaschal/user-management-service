package com.ampaschal.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Amusuo Paschal C.
 * @since 8/26/2020 4:51 PM
 */

@Getter
@Setter
public class OrganizationActivationData {

    private String status;
    private Date timeStamp;
    private String admin;
}
