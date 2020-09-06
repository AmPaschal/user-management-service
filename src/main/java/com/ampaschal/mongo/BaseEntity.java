package com.ampaschal.mongo;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {

    protected ObjectId id;

    private boolean active;

    private boolean deleted;

    protected Date createDate;

    private int version;

}
