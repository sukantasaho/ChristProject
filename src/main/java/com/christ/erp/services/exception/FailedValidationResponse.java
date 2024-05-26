package com.christ.erp.services.exception;

import lombok.Data;

@Data
public class FailedValidationResponse {
    private int errorCode;
    private String errorMsg;
    private int input;
    private boolean success;
}
