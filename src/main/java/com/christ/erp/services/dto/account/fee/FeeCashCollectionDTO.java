package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class FeeCashCollectionDTO {
	public String id;
	public String receiptNo;
	public String dateandtime;
	public ExModelBaseDTO academicYear;
	public ExModelBaseDTO financialYear;
	public String studentId;
	public String registerNo;
	public ExModelBaseDTO campus;
	public String studentName;
	public ExModelBaseDTO payementMode;
	public String gstId;
	public double igstPerct;
	public double sgstPerct;
	public double cgstPerct;
	public double subTotal;
	public double cgstTotalAmount;
	public double sgstTotalAmount;
	public double igstTotalAmount;
	public double totalAmount;
	public Boolean isCancelled;
	public String cancelledReason;
	public String cancelledDate;
	public List<FeeCashCollectionHeadDTO> cashCollecionHead;
}
