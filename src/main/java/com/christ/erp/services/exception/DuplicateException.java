package com.christ.erp.services.exception;

import com.christ.erp.services.common.Utils;
import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {
    private final String generalMessage = "Duplicate Records";
    private final String inputMessage;

    public DuplicateException(String input) {
        this.inputMessage = Utils.isNullOrEmpty(input) ? generalMessage : input;
    }
}
