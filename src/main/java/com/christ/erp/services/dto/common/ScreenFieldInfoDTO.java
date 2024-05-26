package com.christ.erp.services.dto.common;

public class ScreenFieldInfoDTO extends ModelBaseDTO {
    public String text;
    public Boolean required;
    public Integer length;
    public Integer width;
    public String type;
    public String loadBy;

    public ScreenFieldInfoDTO() {
    }
    public ScreenFieldInfoDTO(String id, String text, Boolean required, Integer length, Integer width, String type, String loadBy) {
        this.id = id;
        this.text = text;
        this.required = required;
        this.length = length;
        this.width = width;
        this.type = type;
        this.loadBy = loadBy;
    }
}
