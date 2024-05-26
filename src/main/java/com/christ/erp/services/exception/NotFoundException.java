package com.christ.erp.services.exception;

import com.christ.erp.services.common.Utils;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String generalMessage = "Data not found";
    private final String inputMessage;

    public NotFoundException(String input) {this.inputMessage = !Utils.isNullOrEmpty(input) ? input : generalMessage;}
}
