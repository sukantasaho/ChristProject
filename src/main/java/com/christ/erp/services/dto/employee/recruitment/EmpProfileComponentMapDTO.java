package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.DisplayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpProfileComponentMapDTO {

    public String code;
    public String displayStatus;

    public EmpProfileComponentMapDTO(String code, DisplayStatus displayStatus) {
        this.code = code;
        if(!Utils.isNullOrEmpty(displayStatus)) {
            this.displayStatus = displayStatus.toString();
        }
    }
}
