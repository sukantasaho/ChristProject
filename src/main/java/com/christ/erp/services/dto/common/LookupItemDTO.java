package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupItemDTO {
    public String value;
    public String label;
    public boolean status;

    public LookupItemDTO() {

    }
    public LookupItemDTO(String value, String label) {
        this(value, label, false);
    }
    public LookupItemDTO(String value, String label, boolean status) {
        this.value = value;
        this.label = label;
        this.status = status;
    }
}
