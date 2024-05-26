package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import com.christ.erp.services.dto.common.ErpRoomsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.dto.employee.common.EmpGuestContractDetailsDTO;

public class JobDetailsDTO {
	
	public int empApplnEntriesId;
	public String postAppliedFor;
	public Object[] subjectCategoryIds;
	public Object[] specializationIds;
	public List<EmpApplnSubjectCategoryDTO> empApplnSubjectCategoryDTO;
	public List<EmpApplnLocationPrefDTO> preferredLocationIds;
	public EmpDTO empDTO;
	public EmployeeProfileJobDetailsDTO empJobDetails;
	public ErpRoomsDTO erpRoomEmpMappingDTO;
	public EmpGuestContractDetailsDTO empGuestContractDetailsDTO;
	public LookupItemDTO subjectCategory;
	public LookupItemDTO subjectSpecialization;
}
