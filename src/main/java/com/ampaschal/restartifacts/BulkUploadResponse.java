package com.ampaschal.restartifacts;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Amusuo Paschal C.
 * @since 8/7/2020 2:52 PM
 */

@Getter
@Setter
public class BulkUploadResponse extends BaseResponse {

    private int numErrors;
    private int numSavedUsers;
    private int numInvalidRows;
    private String jobId;

}
