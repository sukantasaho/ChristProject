package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class FeeCashCollectionHeadDTO {
	public String id;
	public ExModelBaseDTO acctHead;
	public int gstId;
	public Boolean gstApplicable = false;
	public Boolean cgstApplicable = false;
	public Boolean sgstApplicable = false;
	public Boolean igstApplicable = false;
	public double cgstPerct;
	public double sgstPerct;
	public double igstPerct;
	public Boolean isFixedAmt;
	public double cgstAmt;
	public double sgstAmt;
	public double igstAmt;
	public double subTotalAmt;
	public List<FeeCashCollectionAccountDTO> collectionAccount;
}

