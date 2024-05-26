package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GUIUserViewPreferenceDTO {
	private int id;
	private String viewMode;
	private String menuTheme;
	private String headerTheme;
	private Integer fontSize;
	private SelectDTO erpUsersDTO;
	private SelectDTO guiBackgroundImageDTO;	
}
