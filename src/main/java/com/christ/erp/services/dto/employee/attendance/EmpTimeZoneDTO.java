package com.christ.erp.services.dto.employee.attendance;

import java.util.List;

import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpTimeZoneDTO {

   private int id;
   private String timeZoneName;
   private boolean isHolidayTimeZone;
   private boolean isVactionTimeZone;
   private boolean isGeneralTimeZone;
   private String selected;
   private boolean isEmployeewise;
   private SelectDTO employeeCategory;
   private List<EmpTimeZoneDetailsDTO> items;
}
