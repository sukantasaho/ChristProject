package com.christ.erp.services.dto.support.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCategoryCampusDTO {
	private int id;
    private List<SupportCategoryCampusDetailsDTO> supportCategoryCampusDetailsDTOList;
	private SelectDTO erpCampusDTO;

}
