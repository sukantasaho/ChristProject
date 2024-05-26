package com.christ.erp.services.dto.account.settings;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class AccSlipBookDTO extends ModelBaseDTO {

	public String id;
	public ExModelBaseDTO slipBookType;
	public String bookNoPrefix;
	public String slipBookNo;
	public String noPrefix;
	public int startSlipNo;
	public int endSlipNo;
	public String issuedDate;
	public String issuedTo;
	public String issuedBy;

}
