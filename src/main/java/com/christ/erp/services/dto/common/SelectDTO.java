package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SelectDTO {
	
    public String value;
    public String label;
    
    public SelectDTO(Integer id, String label) {
    	this.setValue(String.valueOf(id));
    	this.setLabel(label);
    }
    public SelectDTO(String id, String label) {
    	this.setValue(id);
    	this.setLabel(label);
    }
}
