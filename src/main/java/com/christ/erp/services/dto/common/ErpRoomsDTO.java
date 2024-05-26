package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpRoomsDTO {
	public String erpRoomsId;
	public String roomMappingId;
	public LookupItemDTO room;
	public LookupItemDTO block;
	public LookupItemDTO floor;
	public String telephoneOffice;
	public String telephoneExtention;
}
