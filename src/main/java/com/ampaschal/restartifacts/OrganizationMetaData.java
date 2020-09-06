package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Amusuo Paschal C.
 * @since 8/28/2020 11:16 PM
 */

@Getter
@Setter
public class OrganizationMetaData implements Serializable {

    private static final long serialVersionUID = -290390713259540409L;
    private long total;
    private long active;
    private long inactive;
}
