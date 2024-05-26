package com.christ.erp.services.dto.account.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LogoImageDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class AccAccountsDTO extends ModelBaseDTO {

	public ExModelBaseDTO campus;
	public String accountNo;
	public String accountName;
	public String universityAccount;
	public String sibAccountCode;
	public String swiftCode;
	public String printName;
	public String logoFileName;
	public List<LogoImageDTO> logoImages;
	public String verifiedBy;
	public String description1;
	public String description2;
	public String bankInformation;
	public String code;
	public String displayname;
	public String ifscCode;
	public String codeSibPayment;
	public String refundFromAccount;
	public String senderName;
	public String senderEmail;
	public String fileCode;
	public String address1;
	public String address2;
	public String address3;
	public String address4;
}
