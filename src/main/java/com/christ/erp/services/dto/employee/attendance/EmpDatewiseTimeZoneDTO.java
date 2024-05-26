package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.EmpDeaneryDepartmentDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class EmpDatewiseTimeZoneDTO {
    private int id;
    private List<SelectDTO> empSelect;
    private SelectDTO empTimeZoneSelect;
    private LocalDate timeZoneStartDate;
    private LocalDate timeZoneEndDate;
    private Integer numberOfDays;
    private char recordStatus;
    private SelectDTO empCategorySelect;
    private String[] checked;
    private SelectDTO locationSelect;
    private String description;
    private List<EmpDeaneryDepartmentDTO> cmpDeaneryDepartmentDTO;
    private boolean isGenaral;
    private boolean isHoliday;
    private Set<Integer> empIds;
    private Integer campusDeptId;
    private Integer campusId;
    private Integer deptId;
    private Integer denearyId;

    public EmpDatewiseTimeZoneDTO(int id,String description, LocalDate timeZoneStartDate, LocalDate timeZoneEndDate,
                                  boolean isGeneralTimeZone, boolean isHolidayTimeZone, int empTimeZoneId, String empTimeZoneName,int empId,String empName,int locationId,
                                  String locationName,int empCategoryId,String empCategoryName,int campusDeptId,int campusId,int deptId,int denearyId) {
        this.id = id;
        this.description = description;
        this.timeZoneStartDate = timeZoneStartDate;
        this.timeZoneEndDate = timeZoneEndDate;
        this.isGenaral = isGeneralTimeZone;  // Corrected variable names
        this.isHoliday = isHolidayTimeZone;  // Corrected variable names
        this.empTimeZoneSelect = new SelectDTO();
        this.empTimeZoneSelect.setValue(String.valueOf(empTimeZoneId));  // Corrected method name
        this.empTimeZoneSelect.setLabel(empTimeZoneName);  // Corrected method name
        this.empSelect = new ArrayList<>();
        SelectDTO selectDTO = new SelectDTO();
        selectDTO.setValue(String.valueOf(empId));
        selectDTO.setLabel(empName);
        this.empSelect.add(selectDTO);
        this.locationSelect = new SelectDTO();
        this.locationSelect.setValue(String.valueOf(locationId));
        this.locationSelect.setLabel(locationName);
        this.empCategorySelect = new SelectDTO();
        this.empCategorySelect.setValue(String.valueOf(empCategoryId));
        this.empCategorySelect.setLabel(empCategoryName);
        this.campusDeptId = campusDeptId;
        this.campusId = campusId;
        this.deptId = deptId;
        this.denearyId = denearyId;
    }

    public EmpDatewiseTimeZoneDTO() {

    }

}
