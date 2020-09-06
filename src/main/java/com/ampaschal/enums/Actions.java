package com.ampaschal.enums;

import lombok.Getter;

/**
 * @author Amusuo Paschal C.
 * @since 7/24/2020 1:20 PM
 */

@Getter
public enum Actions {

    CREATE_USER ("CREATE-USER"),
    UPDATE_USER ("UPDATE-USER"),
    PRODUCT_SIGN_UP ("PRODUCT-SIGN-UP"),
    CREATE_ORGANIZATION ("CREATE-ORGANIZATION"),
    UPDATE_ORGANIZATION ("UPDATE-ORGANIZATION"),
    ;

    String actionName;

    Actions(String actionName) {
        this.actionName = actionName;
    }
}
