package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SelectColorDTO {
    public String value;
    public String label;
    public String color;

    public SelectColorDTO(String id, String label, String color) {
        this.setValue(id);
        this.setLabel(label);
        this.setColor(color);
    }
}
