package com.christ.erp.services.dto.common;

import java.util.LinkedHashMap;

import com.christ.erp.services.dbobjects.common.ErpCityDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpPincodeDTO {
	public String block;
	public String district;
	public Integer hostelId;
	private SelectDTO erpCity;
	private SelectDTO erpState;
	private SelectDTO state;
	public int id;
	public String pincode;
	public String postoffice;
	public String description;
	public String branchType;
	public String deliveryStatus;
	public String circle;
	public String division;
	public String region;
	public String stateName;
	public String country;
}
