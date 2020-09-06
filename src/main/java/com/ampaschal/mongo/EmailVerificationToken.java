package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Amusuo Paschal C.
 * @since 7/22/2020 10:28 PM
 */

@Getter
@Setter
public class EmailVerificationToken extends BaseEntity {

    private String userId;
    private String token;
    private Date expiry;
    private Date dateCreated;
    private boolean tokenUsed;
    private Date dateVerified;
}
