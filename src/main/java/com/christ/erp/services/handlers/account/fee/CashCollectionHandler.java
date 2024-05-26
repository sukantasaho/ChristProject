package com.christ.erp.services.handlers.account.fee;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccFeePaymentModeDBO;
import com.christ.erp.services.dbobjects.account.fee.FeeCashCollectionAccountDBO;
import com.christ.erp.services.dbobjects.account.fee.FeeCashCollectionDBO;
import com.christ.erp.services.dbobjects.account.fee.FeeCashCollectionHeadDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.account.settings.AccGSTPercentageDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.account.fee.FeeCashCollectionAccountDTO;
import com.christ.erp.services.dto.account.fee.FeeCashCollectionDTO;
import com.christ.erp.services.dto.account.fee.FeeCashCollectionHeadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.transactions.account.fee.CashCollectionTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

public class CashCollectionHandler {
	private static volatile CashCollectionHandler cashCollectionHandler = null;
	CashCollectionTransaction cashCollectionTransaction = CashCollectionTransaction.getInstance();
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

	public static CashCollectionHandler getInstance() {
		if (cashCollectionHandler == null) {
			cashCollectionHandler = new CashCollectionHandler();
		}
		return cashCollectionHandler;
	}

	public ApiResult<FeeCashCollectionDTO> getCurrent() {
		ApiResult<FeeCashCollectionDTO> current = new ApiResult<FeeCashCollectionDTO>();
		try {
			ErpAcademicYearDBO dbo_academic = cashCollectionTransaction.getCurrentAcademicYear();
			AccFinancialYearDBO dbo_financial = cashCollectionTransaction.getCurrentFinancialYear();
			FeeCashCollectionDTO dto = new FeeCashCollectionDTO();
			dto.academicYear = new ExModelBaseDTO();
			dto.academicYear.id = "" + dbo_academic.id;
			dto.academicYear.text = dbo_academic.academicYearName;
			dto.financialYear = new ExModelBaseDTO();
			dto.financialYear.id = "" + dbo_financial.id;
			dto.financialYear.text = "" + dbo_financial.financialYear;
			current.dto = dto;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return current;
	}
	
	public ApiResult<FeeCashCollectionDTO> getApplicantDetails(String registerNo) {
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		try {
			  StudentDBO dbo = new StudentDBO();
			  dbo= cashCollectionTransaction.getApplicantDetails(registerNo);
			  if(dbo == null) {
					result.dto = null;
			  }
			  else {
					FeeCashCollectionDTO dto = new FeeCashCollectionDTO();
					dto.studentId= ""+dbo.id;
					dto.studentName = dbo.studentName;
					result.dto = dto;
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public ApiResult<FeeCashCollectionDTO> getDetailsByReceiptNo(String receiptNo, String finanacialYearId) {
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		try {
			FeeCashCollectionDBO cashCollectionDbo = new FeeCashCollectionDBO();
			cashCollectionDbo = cashCollectionTransaction.getDetailsByReceiptNo(receiptNo,finanacialYearId);
			if(!Utils.isNullOrEmpty(cashCollectionDbo)) {
				FeeCashCollectionDTO feeCashCollectionDTO = new FeeCashCollectionDTO();
				feeCashCollectionDTO.id = ""+cashCollectionDbo.id;
				feeCashCollectionDTO.receiptNo = ""+cashCollectionDbo.receiptNo;
				feeCashCollectionDTO.academicYear = new ExModelBaseDTO();
				feeCashCollectionDTO.academicYear.id =  cashCollectionDbo.erpAcademicYearDBO.id.toString();
				feeCashCollectionDTO.academicYear.text = cashCollectionDbo.erpAcademicYearDBO.academicYearName;
				feeCashCollectionDTO.financialYear = new ExModelBaseDTO();
				feeCashCollectionDTO.financialYear.id = ""+cashCollectionDbo.accFinancialYearDBO.id;
				feeCashCollectionDTO.financialYear.text = cashCollectionDbo.accFinancialYearDBO.financialYear;
				feeCashCollectionDTO.campus = new ExModelBaseDTO();
				feeCashCollectionDTO.campus.id = ""+cashCollectionDbo.erpCampusDBO.id;
				feeCashCollectionDTO.campus.text = cashCollectionDbo.erpCampusDBO.campusName;
				feeCashCollectionDTO.studentName = cashCollectionDbo.studentName;
				feeCashCollectionDTO.dateandtime = ""+cashCollectionDbo.cashCollectionDateTime;
				feeCashCollectionDTO.isCancelled = cashCollectionDbo.isCancelled;
				feeCashCollectionDTO.cancelledReason =cashCollectionDbo.cancelledReason;
				if(!Utils.isNullOrEmpty(cashCollectionDbo.studentDBO)) {
					feeCashCollectionDTO.studentId =""+cashCollectionDbo.studentDBO.id;
					feeCashCollectionDTO.registerNo = cashCollectionDbo.studentDBO.registerNo;
				}
				feeCashCollectionDTO.payementMode = new ExModelBaseDTO();
				feeCashCollectionDTO.payementMode.id =""+ cashCollectionDbo.accFeePaymentModeDBO.id;
				feeCashCollectionDTO.payementMode.text = cashCollectionDbo.accFeePaymentModeDBO.paymentMode;
				if(!Utils.isNullOrEmpty(cashCollectionDbo.cgstTotalAmount))
					feeCashCollectionDTO.cgstTotalAmount = cashCollectionDbo.cgstTotalAmount.doubleValue();
				if(!Utils.isNullOrEmpty(cashCollectionDbo.igstTotalAmount))
					feeCashCollectionDTO.igstTotalAmount = cashCollectionDbo.igstTotalAmount.doubleValue();
				if(!Utils.isNullOrEmpty(cashCollectionDbo.sgstTotalAmount))
					feeCashCollectionDTO.sgstTotalAmount = cashCollectionDbo.sgstTotalAmount.doubleValue();
				if(!Utils.isNullOrEmpty(cashCollectionDbo.subTotal))
					feeCashCollectionDTO.subTotal = cashCollectionDbo.subTotal.doubleValue();
				if(!Utils.isNullOrEmpty(cashCollectionDbo.totalAmount))
					feeCashCollectionDTO.totalAmount = cashCollectionDbo.totalAmount.doubleValue();				
				feeCashCollectionDTO.isCancelled = cashCollectionDbo.isCancelled;
				if(feeCashCollectionDTO.isCancelled)
				{
					feeCashCollectionDTO.cancelledReason = cashCollectionDbo.cancelledReason;
					feeCashCollectionDTO.cancelledDate =  Utils.convertLocalDateTimeToStringDateTime(cashCollectionDbo.cancelledDate );
				}
				feeCashCollectionDTO.cashCollecionHead = new ArrayList<FeeCashCollectionHeadDTO>();
				if(cashCollectionDbo.feeCashCollectionHeadList.size()>0 && !Utils.isNullOrEmpty(cashCollectionDbo.feeCashCollectionHeadList)) {
					 for(FeeCashCollectionHeadDBO cashCollectDbo : cashCollectionDbo.feeCashCollectionHeadList){
						 if(!Utils.isNullOrEmpty(cashCollectDbo.recordStatus) && cashCollectDbo.recordStatus == 'A') {
							 FeeCashCollectionHeadDTO dto = new FeeCashCollectionHeadDTO();
							 dto.id = ""+cashCollectDbo.id;
							 dto.acctHead = new ExModelBaseDTO();
							 dto.acctHead.id = ""+cashCollectDbo.accFeeHeadsDBO.id;
							 dto.acctHead.text = cashCollectDbo.accFeeHeadsDBO.heading;	
							 dto.isFixedAmt = cashCollectDbo.accFeeHeadsDBO.isFixedAmount;
							 dto.gstApplicable = cashCollectDbo.accFeeHeadsDBO.isGstApplicable;
							 if( dto.gstApplicable) {
								 dto.gstId = cashCollectDbo.accGSTPercentageDBO.id;
								 feeCashCollectionDTO.gstId =""+ cashCollectDbo.accGSTPercentageDBO.id;
								 if(cashCollectDbo.accFeeHeadsDBO.isCgstApplicable) {
									 dto.cgstApplicable = cashCollectDbo.accFeeHeadsDBO.isCgstApplicable;
									 if(!Utils.isNullOrEmpty(cashCollectDbo.accGSTPercentageDBO.CGSTPercentage))
										 dto.cgstPerct = cashCollectDbo.accGSTPercentageDBO.CGSTPercentage.doubleValue();
									 if(!Utils.isNullOrEmpty( cashCollectDbo.cgstAmount))
										 dto.cgstAmt = cashCollectDbo.cgstAmount.doubleValue();
									 feeCashCollectionDTO.cgstPerct =  cashCollectDbo.accGSTPercentageDBO.CGSTPercentage.doubleValue();
								 }
								 if(cashCollectDbo.accFeeHeadsDBO.isSgstApplicable) {
									 dto.sgstApplicable = cashCollectDbo.accFeeHeadsDBO.isSgstApplicable;
									 if(!Utils.isNullOrEmpty(cashCollectDbo.accGSTPercentageDBO.SGSTPercentage))
										 dto.sgstPerct = cashCollectDbo.accGSTPercentageDBO.SGSTPercentage.doubleValue();
									 if(!Utils.isNullOrEmpty( cashCollectDbo.sgstAmount))
										 dto.sgstAmt = cashCollectDbo.sgstAmount.doubleValue();
									 feeCashCollectionDTO.sgstPerct =  cashCollectDbo.accGSTPercentageDBO.SGSTPercentage.doubleValue();
								 }
								 if(cashCollectDbo.accFeeHeadsDBO.isIgstApplicable) {
									 dto.igstApplicable = cashCollectDbo.accFeeHeadsDBO.isIgstApplicable;
									 if(!Utils.isNullOrEmpty(cashCollectDbo.accGSTPercentageDBO.IGSTPercentage))
										 dto.igstPerct = cashCollectDbo.accGSTPercentageDBO.IGSTPercentage.doubleValue();
									 if(!Utils.isNullOrEmpty( cashCollectDbo.igstAmount))
										 dto.igstAmt = cashCollectDbo.igstAmount.doubleValue();
									 feeCashCollectionDTO.igstPerct =  cashCollectDbo.accGSTPercentageDBO.IGSTPercentage.doubleValue();
								 }
							 }
							 dto.collectionAccount = new ArrayList<FeeCashCollectionAccountDTO>();
							 double t_sum = 0;
							 if(cashCollectDbo.feeCashCollectionAccountList.size()>0 && !(Utils.isNullOrEmpty((cashCollectDbo.feeCashCollectionAccountList)))){
								for(FeeCashCollectionAccountDBO cashAccDbo : cashCollectDbo.feeCashCollectionAccountList) {
									 if(!Utils.isNullOrEmpty(cashAccDbo.recordStatus) && cashAccDbo.recordStatus == 'A') {
										 FeeCashCollectionAccountDTO accDto = new FeeCashCollectionAccountDTO();
										 accDto.id = ""+cashAccDbo.id;
										 accDto.cashCollectionHeadId = ""+cashAccDbo.feeCashCollectionHeadDBO.id;
										 accDto.feeHeadsAccid = ""+cashAccDbo.accFeeHeadsAccountDBO.id;
										 accDto.accId=""+cashAccDbo.accFeeHeadsAccountDBO.accAccountsDBO.id;
										 accDto.accountName = cashAccDbo.accFeeHeadsAccountDBO.accAccountsDBO.accountName;//										
									 	 accDto.amount = cashAccDbo.paidAmount.doubleValue();
										 t_sum = t_sum+accDto.amount;
										 dto.collectionAccount.add(accDto);
									 }
								}
								dto.subTotalAmt = t_sum;							
								feeCashCollectionDTO.cashCollecionHead.add(dto);
							 }
						 }
					 }
				}
				result.dto = feeCashCollectionDTO;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public List<FeeCashCollectionDTO> getApplicantDetailsList(String registerNo,String finanacialYearId) {
		List<FeeCashCollectionDTO> list  = null;
		try {
			List<Tuple> gridList = cashCollectionTransaction.getGridData(registerNo,
					finanacialYearId);
			if(gridList.size()>0) {
				list = new ArrayList<>();	
				for (Tuple tuple : gridList) {
					FeeCashCollectionDTO feeCashCollectionDTO = new FeeCashCollectionDTO();
					feeCashCollectionDTO.id =tuple.get("id").toString();
					feeCashCollectionDTO.studentId = tuple.get("studentId").toString();
					feeCashCollectionDTO.studentName = tuple.get("studentName").toString();
					feeCashCollectionDTO.dateandtime = tuple.get("dateandtime").toString();
					feeCashCollectionDTO.totalAmount = Double.parseDouble(tuple.get("totalAmount").toString());
					feeCashCollectionDTO.receiptNo = tuple.get("receiptNo").toString();	
					list.add(feeCashCollectionDTO);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<FeeCashCollectionHeadDTO> getFeeHeadDetails(String feeHeadId, String finanacialYearId,String index, FeeCashCollectionDTO data) {
		List<FeeCashCollectionHeadDTO> result = null;
		boolean flag = true;
		try {
			AccFeeHeadsDBO dbo = cashCollectionTransaction.getFeeHeadDetails(feeHeadId);
			if(data.cashCollecionHead.size()>1) {
				if (!Utils.isNullOrEmpty(dbo)) {					
					for(FeeCashCollectionHeadDTO dtoCashCollectionHead : data.cashCollecionHead) {
						if(!Utils.isNullOrEmpty(dtoCashCollectionHead.acctHead.id) || !Utils.isNullOrWhitespace(dtoCashCollectionHead.acctHead.id)) {
							if(Integer.parseInt(dtoCashCollectionHead.acctHead.id) ==dbo.id) {	
								flag = true;
							}else {
								if(dtoCashCollectionHead.gstApplicable == dbo.isGstApplicable) {
									flag = true;
									if(dtoCashCollectionHead.sgstApplicable == dbo.isSgstApplicable) {
										flag = true;
									}
									else {
										flag = false;
										break;
									}
									if(dtoCashCollectionHead.cgstApplicable == dbo.isCgstApplicable) {
										flag = true;
									}
									else {
										flag = false;
										break;
									}
									if(dtoCashCollectionHead.igstApplicable == dbo.isIgstApplicable) {
										flag = true;
									}
									else {
										flag = false;
										break;
									}
								}
								else {
									flag = false;
									break;
								}
							}
						}
						else {
							flag = true;
							break;
						}
					}
				}
			}
			if(flag) {
				if (!Utils.isNullOrEmpty(dbo)) {
					result = new ArrayList<FeeCashCollectionHeadDTO>();
					FeeCashCollectionHeadDTO dto = new FeeCashCollectionHeadDTO();
					dto.acctHead = new ExModelBaseDTO();
					dto.acctHead.id = feeHeadId;
					dto.acctHead.text = dbo.heading;
					dto.gstApplicable = dbo.isGstApplicable;
					dto.isFixedAmt = dbo.isFixedAmount;
					if (dto.gstApplicable) {
						AccGSTPercentageDBO gstDBO = cashCollectionTransaction.getCurrentGstDetails();
						dto.gstId = gstDBO.id;
						dto.igstApplicable = dbo.isIgstApplicable;
						if(dto.igstApplicable) {
							if(!Utils.isNullOrEmpty(gstDBO.IGSTPercentage))
								dto.igstPerct = gstDBO.IGSTPercentage.doubleValue();
						}							
						dto.cgstApplicable = dbo.isCgstApplicable;
						if(dto.cgstApplicable)
						{
							if(!Utils.isNullOrEmpty(gstDBO.CGSTPercentage))
								dto.cgstPerct = gstDBO.CGSTPercentage.doubleValue();
						}							
						dto.sgstApplicable = dbo.isSgstApplicable;
						if(dto.sgstApplicable) {
							if(!Utils.isNullOrEmpty(gstDBO.SGSTPercentage)) 
								dto.sgstPerct = gstDBO.SGSTPercentage.doubleValue();
						}
					}
					dto.isFixedAmt = dbo.isFixedAmount;
					dto.collectionAccount = new ArrayList<FeeCashCollectionAccountDTO>();
					if(!Utils.isNullOrEmpty(dbo.accFeeHeadsAccountList)) {
						for(AccFeeHeadsAccountDBO accountDBO : dbo.accFeeHeadsAccountList) {
							if(accountDBO.recordStatus == 'A'/*  && !Utils.isNullOrEmpty(accountDBO.accFinancialYearDBO)
									&& !Utils.isNullOrEmpty(accountDBO.accFinancialYearDBO.id) && accountDBO.accFinancialYearDBO.id == Integer.parseInt(finanacialYearId)*/) {
								FeeCashCollectionAccountDTO dto_acc = new FeeCashCollectionAccountDTO();
								dto_acc.feeHeadsAccid = String.valueOf(accountDBO.id);
								dto_acc.accId = String.valueOf(accountDBO.accAccountsDBO.id);
								dto_acc.accountName =accountDBO.accAccountsDBO.accountName;
								dto_acc.amount = accountDBO.amount.doubleValue();
								dto.collectionAccount.add(dto_acc);
							}
						}
					}
					result.add(dto);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void getFeeHeadByCampusId(ApiResult<List<LookupItemDTO>> result, String campusId, String financialYearId)
			throws Exception {
		List<Tuple> mappings = cashCollectionTransaction.getFeeHeadByCampusId(campusId, financialYearId);
		if (mappings.size() > 0) {
			if (mappings != null && mappings.size() > 0) {
				result.success = true;
				result.dto = new ArrayList<>();
				for (Tuple mapping : mappings) {
					LookupItemDTO itemInfo = new LookupItemDTO();
					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
					result.dto.add(itemInfo);
				}
			}
		}
	}	

	public ApiResult<FeeCashCollectionDTO> saveOrUpdate(FeeCashCollectionDTO data, String userId) throws Exception {
		boolean isSaved;
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		Map<Integer, FeeCashCollectionHeadDBO> existingCashCollectionHeadId = new HashMap<Integer, FeeCashCollectionHeadDBO>();		
		if (!Utils.isNullOrEmpty(data)) {
			FeeCashCollectionDBO dbo = null;
			boolean flag = false;
			if (Utils.isNullOrEmpty(data.id)) {
				dbo = new FeeCashCollectionDBO();
				dbo.createdUsersId = Integer.parseInt(userId) ;		
				dbo.modifiedUsersId = Integer.parseInt(userId) ;	
				flag = true ;
			}
			else {
				dbo =  commonApiTransaction.find(FeeCashCollectionDBO.class, Integer.parseInt(data.id));
				if(!Utils.isNullOrEmpty(dbo)) {					
					if(dbo.feeCashCollectionHeadList.size()>0) {						
						for(FeeCashCollectionHeadDBO headDbo1:dbo.feeCashCollectionHeadList) {
							if(headDbo1.recordStatus == 'A' ) {
								existingCashCollectionHeadId.put(headDbo1.id,headDbo1);
							}
						}
					}
				}
				dbo.modifiedUsersId = Integer.parseInt(userId) ;
				dbo.id = Integer.parseInt(data.id);
				flag = true;
			}
			if(flag) {
				dbo.accFinancialYearDBO = new AccFinancialYearDBO();
				dbo.accFinancialYearDBO.id = Integer.parseInt(data.financialYear.id);					
				dbo.cashCollectionDateTime = Utils.convertStringDateTimeToLocalDateTime(data.dateandtime).withSecond(0); 
				dbo.erpAcademicYearDBO = new ErpAcademicYearDBO();
				dbo.erpAcademicYearDBO.id = Integer.parseInt(data.academicYear.id);
				if(!Utils.isNullOrEmpty(data.studentId)) {	
					dbo.studentDBO = new StudentDBO();
					dbo.studentDBO.id = Integer.parseInt(data.studentId);
				}
				dbo.studentName = data.studentName;
				dbo.erpCampusDBO = new ErpCampusDBO();
				dbo.erpCampusDBO.id = Integer.parseInt(data.campus.id);
				dbo.accFeePaymentModeDBO = new AccFeePaymentModeDBO();
				dbo.accFeePaymentModeDBO.id =Integer.parseInt(data.payementMode.id);
				dbo.subTotal = new BigDecimal(data.subTotal);
				dbo.cgstTotalAmount = new BigDecimal(data.cgstTotalAmount);
				dbo.igstTotalAmount = new BigDecimal(data.igstTotalAmount);
				dbo.sgstTotalAmount = new BigDecimal(data.sgstTotalAmount);
				dbo.totalAmount = new BigDecimal(data.totalAmount);		
				dbo.isCancelled = data.isCancelled;
				dbo.recordStatus = 'A';
				if(dbo.isCancelled) {
					dbo.cancelledReason = data.cancelledReason;					 
					dbo.cancelledDate = LocalDateTime.now();
					dbo.cancelledUserId = Integer.parseInt(userId);					
				}
				if(data.cashCollecionHead.size()>0) {
					Set<FeeCashCollectionHeadDBO> cashCollectionHeadSet = new HashSet<FeeCashCollectionHeadDBO>();
					for(FeeCashCollectionHeadDTO dtoCashCollectionHead : data.cashCollecionHead) {
						FeeCashCollectionHeadDBO cashCOllectionHeadDBO = new FeeCashCollectionHeadDBO();												
						if(Utils.isNullOrEmpty(dtoCashCollectionHead.id)) {
							cashCOllectionHeadDBO.createdUsersId = Integer.parseInt(userId);
							cashCOllectionHeadDBO.modifiedUsersId = Integer.parseInt(userId);
						}
						else {	
							if(existingCashCollectionHeadId.size()>0) {
								if(existingCashCollectionHeadId.containsKey(Integer.parseInt(dtoCashCollectionHead.id))) {
									existingCashCollectionHeadId.remove(Integer.parseInt(dtoCashCollectionHead.id));
								}
							}
							cashCOllectionHeadDBO.modifiedUsersId = Integer.parseInt(userId);
							cashCOllectionHeadDBO.id = Integer.parseInt(dtoCashCollectionHead.id);
						}
						cashCOllectionHeadDBO.accFeeHeadsDBO = new AccFeeHeadsDBO();
						cashCOllectionHeadDBO.accFeeHeadsDBO.id = Integer.parseInt(dtoCashCollectionHead.acctHead.id);
						cashCOllectionHeadDBO.feeCashCollectionDBO = new FeeCashCollectionDBO();
						if(!Utils.isNullOrEmpty(data.gstId) || (!Utils.isNullOrWhitespace(data.gstId)))	{
							cashCOllectionHeadDBO.accGSTPercentageDBO = new AccGSTPercentageDBO();												
							cashCOllectionHeadDBO.accGSTPercentageDBO.id = Integer.parseInt(data.gstId);
						}
						cashCOllectionHeadDBO.cgstAmount =  new BigDecimal(dtoCashCollectionHead.cgstAmt);
						cashCOllectionHeadDBO.igstAmount =  new BigDecimal(dtoCashCollectionHead.igstAmt);
						cashCOllectionHeadDBO.sgstAmount =  new BigDecimal(dtoCashCollectionHead.sgstAmt);
						cashCOllectionHeadDBO.recordStatus = 'A';
						if(dtoCashCollectionHead.collectionAccount.size()>0) {
							Set<FeeCashCollectionAccountDBO> cashCollectionAccSet = new HashSet<FeeCashCollectionAccountDBO>();
							for(FeeCashCollectionAccountDTO dtoCashCollectionAccount : dtoCashCollectionHead.collectionAccount) {
								FeeCashCollectionAccountDBO feeCashCollectionAccountDBO = new FeeCashCollectionAccountDBO();
								if(Utils.isNullOrEmpty(dtoCashCollectionAccount.id)) {
									feeCashCollectionAccountDBO.createdUsersId = Integer.parseInt(userId);	
									feeCashCollectionAccountDBO.modifiedUsersId = Integer.parseInt(userId);
								}
								else {
									feeCashCollectionAccountDBO.modifiedUsersId = Integer.parseInt(userId);
									feeCashCollectionAccountDBO.id = Integer.parseInt(dtoCashCollectionAccount.id);								
								}
								feeCashCollectionAccountDBO.feeCashCollectionHeadDBO = new FeeCashCollectionHeadDBO();
								feeCashCollectionAccountDBO.accFeeHeadsAccountDBO = new AccFeeHeadsAccountDBO();
								feeCashCollectionAccountDBO.accFeeHeadsAccountDBO.id =Integer.parseInt(dtoCashCollectionAccount.feeHeadsAccid);
								feeCashCollectionAccountDBO.paidAmount =  new BigDecimal(dtoCashCollectionAccount.amount);
								feeCashCollectionAccountDBO.recordStatus = 'A';	
								feeCashCollectionAccountDBO.feeCashCollectionHeadDBO = cashCOllectionHeadDBO;
								cashCollectionAccSet.add(feeCashCollectionAccountDBO);
							}
							cashCOllectionHeadDBO.feeCashCollectionAccountList = cashCollectionAccSet;
						}
						cashCOllectionHeadDBO.feeCashCollectionDBO = dbo;
						cashCollectionHeadSet.add(cashCOllectionHeadDBO);						
					}
					if(existingCashCollectionHeadId.size()>0) {
						for(Entry<Integer, FeeCashCollectionHeadDBO> entry : existingCashCollectionHeadId.entrySet()) {
							FeeCashCollectionHeadDBO cashCOllectionHeadDBO = new FeeCashCollectionHeadDBO();
							cashCOllectionHeadDBO = entry.getValue();
							cashCOllectionHeadDBO.modifiedUsersId = Integer.parseInt(userId);
							cashCOllectionHeadDBO.recordStatus = 'D';
							if(cashCOllectionHeadDBO.feeCashCollectionAccountList.size()>0) {
								Set<FeeCashCollectionAccountDBO> cashCollectionAccSet = new HashSet<FeeCashCollectionAccountDBO>();
								for(FeeCashCollectionAccountDBO accDBO: cashCOllectionHeadDBO.feeCashCollectionAccountList) {
									accDBO.modifiedUsersId = Integer.parseInt(userId);
									accDBO.recordStatus = 'D';
									cashCollectionAccSet.add(accDBO);
								}
								cashCOllectionHeadDBO.feeCashCollectionAccountList = cashCollectionAccSet;
							}
							cashCollectionHeadSet.add(cashCOllectionHeadDBO);									
						}
					}
				/*	if (Utils.isNullOrEmpty(data.receiptNo)) {					
						int r = cashCollectionTransaction.getReceiptNumber(Integer.parseInt(userId),
								Integer.parseInt(data.financialYear.id));
						dbo.receiptNo = r;
						data.receiptNo =""+ r;
					} */					
					dbo.feeCashCollectionHeadList = cashCollectionHeadSet;
				}
				isSaved= cashCollectionTransaction.saveOrUpdateFeeCashCollection(dbo);
				if(isSaved) {
					result.dto = data;
					result.success = true;
				}
				else {
					result.success = false;
					result.failureMessage = "Something went wrong";
				}
			}
		}
		return result;
	}
	
}
