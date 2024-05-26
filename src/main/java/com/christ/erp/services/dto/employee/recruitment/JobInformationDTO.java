package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import com.christ.erp.services.dto.employee.common.EmpRemarksDTO;
import com.christ.erp.services.dto.employee.common.EmpResignationDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobInformationDTO {
	public JobDetailsDTO jobInformation;
	public EmpApproversDetailsDTO approverDetails;
	public EmpResignationDTO resignationDetails;
	public WorkTimeDetailsDTO workTimeDetails;
	public List<EmployeeProfileAdditionalInformation> additionalInformation;
	public List<EmployeeProfileLetterDTO> promotionDetails;
	public List<EmpRemarksDTO> remarks;
	public EmpRemarksDTO otherInfo;
	private List<EmpLevelsAndPromotionsDTO> empLevelsAndPromotionsDTO;
}
