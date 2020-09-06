package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import java.io.InputStream;

/**
 * @author Amusuo Paschal C.
 * @since 8/6/2020 10:17 AM
 */

@Getter
@Setter
public class BulkUploadRequest {

    @NotBlank
    @FormParam("productId")
    private String productId;

    @FormParam("file")
    private InputStream file;

}
