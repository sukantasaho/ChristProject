package com.christ.erp.services.dto.hostel.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelImagesDTO {

	private int id;
	private String url;
	private String fileName;
	private String extension;
	private char recordStatus;
	private Boolean newFile;
	
}
