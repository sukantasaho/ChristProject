package com.christ.erp.services.dto.employee.salary;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EmpPayScaleGradeMappingDetailDTO implements Comparable<EmpPayScaleGradeMappingDetailDTO>{
	public String id;
    public String payScale;
    public String payScaleLevel;
    public String displayOrder;
	public List<EmpPayScaleMatrixDetailDTO> payScaleMatrixDataDetailList;
    private SelectDTO empPayScaleLevel;

	@Override
	public int compareTo(EmpPayScaleGradeMappingDetailDTO dto) {
		return this.displayOrder.compareTo(dto.displayOrder);
	}
}


