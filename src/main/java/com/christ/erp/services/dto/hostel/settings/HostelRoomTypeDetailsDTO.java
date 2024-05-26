package com.christ.erp.services.dto.hostel.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelRoomTypeDetailsDTO {
	public String roomTypeDetailsId;
	public String value;
	public String label;
	public String url;
	public String fileName;
	public String extension;
	public char recordStatus;
	public Boolean newFile;
}
