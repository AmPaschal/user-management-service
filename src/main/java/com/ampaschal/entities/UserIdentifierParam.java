package com.ampaschal.entities;

import com.ampaschal.mongo.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 8/23/2020 2:44 PM
 */

@Getter
@Setter
@NoArgsConstructor
public class UserIdentifierParam {

    private Users user;
    private String identifier;

    public UserIdentifierParam(Users user, String identifier) {
        this.user = user;
        this.identifier = identifier;
    }
}
