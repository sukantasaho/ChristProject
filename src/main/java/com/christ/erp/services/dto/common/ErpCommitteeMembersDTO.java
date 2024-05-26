package com.christ.erp.services.dto.common;

import java.util.List;
import com.christ.erp.services.dto.student.common.StudentDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCommitteeMembersDTO {
	
	private int id;
    private List<SelectDTO> empList;
    private StudentDTO studentDTO;
    private SelectDTO batchYear;
    private SelectDTO erpExternalsCategory;
    private List<SelectDTO> externals ;
    private List<SelectDTO> cdcMembers;
    private SelectDTO erpCommitteeRole;

}
