package com.christ.erp.services.dto.employee.attendance;
import java.time.LocalDate;
import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.common.EmpCampusDeaneryDepartmentDTO;
import com.christ.erp.services.dto.employee.common.EmpDeaneryDepartmentDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HolidaysAndEventsEntryDTO {
	
	public ExModelBaseDTO campus;
	public ExModelBaseDTO types;
	public ExModelBaseDTO location;
	public ExModelBaseDTO empCategory;
	public ExModelBaseDTO employee;
	public ExModelBaseDTO academicYear;
	public String[] checked;
	public String description;
	public LocalDate date;
	public LocalDate startDate;
	public LocalDate endDate;
	public Boolean isEmployeeWise;
	public String isFullDay;
	public String isOneTimeSignIn;
	public List<EmpDeaneryDepartmentDTO> cmpDeaneryDepartmentDTO;
	public String selected;
	public List<String> expanded;
	public List<ExModelBaseDTO> emply;
	public List<EmployeeApplicationDTO>  msSelectedItems;
	public List<ExModelBaseDTO> employeeList;
	public Boolean isHolidayOrEvent;
	public List<EmployeeApplicationDTO>  emp;
	public Boolean isEdit;
	public List<EmpCampusDeaneryDepartmentDTO> empCampusDeaneryDepartmentDTO;
	public String id;
	public String tag;
	private Boolean isException;

}
