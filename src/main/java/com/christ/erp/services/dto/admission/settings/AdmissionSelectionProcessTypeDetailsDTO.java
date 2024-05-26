package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionSelectionProcessTypeDetailsDTO implements Comparable <AdmissionSelectionProcessTypeDetailsDTO> {
	
	public String id;
	public String order;
    public String subProcess;
    public ExModelBaseDTO scoreCard;
    public String pannelListCount;
    public char recordStatus;
    private Integer parentId;
    private Integer score;

	@Override
	public int compareTo(AdmissionSelectionProcessTypeDetailsDTO dto) {
		return this.order.compareTo(dto.order);
	}

}
