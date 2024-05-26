package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OfflineApplicationIssueDTO {

	public String id;
	public ExModelBaseDTO academicYear;
	public ExModelBaseDTO acctHead;
	public ExModelBaseDTO payementMode;
	public ExModelBaseDTO accountName;
	public ExModelBaseDTO financialYear;
	public String applicationNo;
	public String amount;
	public String appicantName;
	public String mobileNo;
	public String receiptDate;
	public String code;
	public String ddNo;
	public String offlineApplnNoPrefix;
	public String offlineApplnNo;
	public String receiptNumber;
	public String printReceiptDate;
	public Boolean isCancelled;
	public String cancelledReason;
	public String templateMsg;
	public String accLogoFileName;
}
