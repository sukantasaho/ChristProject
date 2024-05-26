package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonDTO {

	public String value;
    public String label;
    
    /*---------- erp_country -------------*/
    public String nationality;
    public String phoneCode;
    
    /*---------- erp_religion -------------*/
    public boolean isMinority;
    
}
