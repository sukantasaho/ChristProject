package com.christ.erp.services.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ApiResult<T> {
    public boolean success;
    public String failureMessage;
    public String tag;
    public T dto;
    private List<T> dtoList;
}
