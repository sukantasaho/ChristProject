package com.christ.erp.services.dto.employee.settings;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpApproversDetailsDTO {

	public String id;
	public String empId;
	public String empName;
	public String empDepartment;
	public SelectDTO leaveApproverId;
	public SelectDTO leaveAuthorizerId;
	public SelectDTO levelOneAppraiserId;
	public SelectDTO levelTwoAppraiserId;
	public SelectDTO workDairyApproverId;
	public SelectDTO leaveApprover;
	public SelectDTO leaveAuthorizer;
	public SelectDTO levelOneAppraiser;
	public SelectDTO levelTwoAppraiser;
	public SelectDTO workDairyApprover;
	private int empApproversDetailsId;
	private String empCategoryName;
	
	public EmpApproversDetailsDTO(int empId, String leaveApprover, String leaveAuthorizer, String levelOneAppraiser, String levelTwoAppraiser,String workDairyApprover) {
		this.empId = String.valueOf(empId);
		this.leaveApprover = new  SelectDTO();
		this.leaveApprover.setLabel(leaveApprover);
		this.leaveAuthorizer = new SelectDTO();
		this.leaveAuthorizer.setLabel(leaveAuthorizer);
		this.levelOneAppraiser = new SelectDTO();
		this.levelOneAppraiser.setLabel(levelOneAppraiser);
		this.levelTwoAppraiser = new SelectDTO();
		this.levelTwoAppraiser.setLabel(levelTwoAppraiser);
		this.workDairyApprover = new SelectDTO();
		this.workDairyApprover.setLabel(workDairyApprover);
	}
	
}
