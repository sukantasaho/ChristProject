package com.christ.erp.services.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralException extends RuntimeException {
    private String inputMessage = "General Exception";
}
