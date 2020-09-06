package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Setting extends BaseEntity {

    private String componentName;
    private String productId;
    private String name;
    private String value;
    private String description;

}
