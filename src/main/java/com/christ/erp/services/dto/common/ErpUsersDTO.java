package com.christ.erp.services.dto.common;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ErpUsersDTO extends ModelBaseDTO {
    public String userName;
	public String newPassword;
	public LookupItemDTO employee;
	public LookupItemDTO department;
	public List<SysUserRoleMapDTO> roleArray;
//	public String validDate;
	private LocalDateTime validDate;
	public Boolean isActive;
	public String passWordError;
	public String showPassword;
	public Integer totalNormalPrivilegeAvailble;
	public Integer totalAdditionalPrivilegeAllowed;
	public Integer totalAdditionalPrivilegeCount;
	public String empName;
	private String erpUserName;
	
	public ErpUsersDTO(String userName, Integer userID, String empName, Integer empId)  {
		this.setEmployee(new LookupItemDTO());
		this.getEmployee().setValue(String.valueOf(empId));
		this.getEmployee().setLabel(empName);
		this.setUserName(userName);
		this.id = String.valueOf(userID);
	}
}
