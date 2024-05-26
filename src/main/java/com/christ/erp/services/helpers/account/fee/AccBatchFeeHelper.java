package com.christ.erp.services.helpers.account.fee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeAccountDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeHeadDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.AcaSessionDBO;
import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryCampusMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeAccountDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeCategoryDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDetailsDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeHeadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.account.fee.AccBatchFeeTransaction;

@Service
public class AccBatchFeeHelper {
	@Autowired
	AccBatchFeeTransaction accBatchFeeTransaction;

	public List<AccBatchFeeDurationsDTO> convertDBOsToAccBatchFeeDurationsDTO(List<AccBatchFeeDurationsDTO> durationDetailsDTOList, 
			List<ErpAdmissionCategoryCampusMappingDBO> categoryList, List<AccAccountsDBO> accountNoList, List<AccFeeHeadsDBO> feeHeadsList) {
		durationDetailsDTOList.forEach(durationDetails->{
			List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList = new ArrayList<AccBatchFeeCategoryDTO>();
			List<AccBatchFeeHeadDTO> accBatchFeeHeadsDTOList = new ArrayList<AccBatchFeeHeadDTO>();

			//setting accounts
			List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
				accountNoList.forEach(account->{
					AccBatchFeeAccountDTO accBatchFeeAccountDTO = new AccBatchFeeAccountDTO();
					SelectDTO accAccountsDTO = new SelectDTO();
					accAccountsDTO.setValue(Integer.toString(account.getId()));
					accAccountsDTO.setLabel(account.getAccountNo());
					accBatchFeeAccountDTO.setAccAccountsDTO(accAccountsDTO);
					accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
				});
				//setting fee heads and accounts
				List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOListSorted = accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList());
				feeHeadsList.forEach(feehead->{
					AccBatchFeeHeadDTO accBatchFeeHeadDTO = new AccBatchFeeHeadDTO();	
					SelectDTO accHeadsDTO = new SelectDTO();
					accHeadsDTO.setValue(Integer.toString(feehead.getId()));
					accHeadsDTO.setLabel(feehead.getHeading());
					accBatchFeeHeadDTO.setAccFeeHeadsDTO(accHeadsDTO);
					accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOListSorted);
					accBatchFeeHeadsDTOList.add(accBatchFeeHeadDTO);
				});
				List<AccBatchFeeHeadDTO> accBatchFeeHeadsDTOListSorted = accBatchFeeHeadsDTOList.stream().sorted(Comparator.comparing(o -> o.getAccFeeHeadsDTO().getLabel())).collect(Collectors.toList());
				categoryList.forEach(adm->{
					AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = new AccBatchFeeCategoryDTO();
					SelectDTO admCategoryDTo = new SelectDTO();
					if(!Utils.isNullOrEmpty(adm.getId())) {
						admCategoryDTo.setValue(Integer.toString(adm.getErpAdmissionCategoryDBO().getId()));
					}
					admCategoryDTo.setLabel(adm.getErpAdmissionCategoryDBO().getAdmissionCategoryName());
					accBatchFeeCategoryDTO.setErpAdmissionCategoryDTO(admCategoryDTo);
					accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadsDTOListSorted);
					accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
				});
				if(!Utils.isNullOrEmpty(durationDetails.getAccBatchFeeDurationsDetailsDTOList())) {
					durationDetails.getAccBatchFeeDurationsDetailsDTOList().forEach(det->{
						det.setAccBatchFeeCategoryDTOList(accBatchFeeCategoryDTOList.stream().sorted(Comparator.comparing(o -> o.getErpAdmissionCategoryDTO().getLabel())).collect(Collectors.toList()));
					});
					
				}
		});
		return durationDetailsDTOList;
	}
	public AccBatchFeeDTO convertAccBatchFeeDboToDto(AccBatchFeeDBO accBatchFeeDBO) {
		AccBatchFeeDTO accBatchFeeDTO = new AccBatchFeeDTO();
		String type = "";
		if(!Utils.isNullOrEmpty(accBatchFeeDBO)) {
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO()) && !Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO().getAcaSessionTypeDBO())) {
				type = accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO().getAcaSessionTypeDBO().getCurriculumCompletionType();
			}
		}
		//for CBS and Phd only one year payment
		List<AcaDurationDetailDBO> durationList = new ArrayList<AcaDurationDetailDBO>();
		int campusId = 0;
		if(type.equalsIgnoreCase("CREDIT") || type.equalsIgnoreCase("SUBMISSION")) {
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getBatchStartingAcaDurationDetail())) {
				AcaDurationDetailDBO acaDurationDetailDBO = accBatchFeeDBO.getAcaBatchDBO().getBatchStartingAcaDurationDetail();
				AcaSessionDBO acaSessionDBO = acaDurationDetailDBO.getAcaSessionDBO();
				acaSessionDBO.setTermNumber(1);
				acaSessionDBO.setYearNumber(1);
				acaDurationDetailDBO.setAcaSessionDBO(acaSessionDBO);
				durationList.add(accBatchFeeDBO.getAcaBatchDBO().getBatchStartingAcaDurationDetail());
			}
		}
		else {
			durationList = accBatchFeeTransaction.getDurationsByBatch(accBatchFeeDBO.getAcaBatchDBO().getId());
			if(!Utils.isNullOrEmpty(durationList) && durationList.size() > 0) {
				campusId = durationList.get(0).getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId();
			}
		}

		List<AccBatchFeeDurationsDTO> durationDTOList = getAccBatchFeeDurationDTOList(durationList);
		List<ErpAdmissionCategoryCampusMappingDBO> admissionCategoryList = accBatchFeeTransaction.getAdmissionCategoryByCampus(campusId);
		List<AccFeeHeadsDBO> feeHeadsList  = accBatchFeeTransaction.getFeeHeads();
		List<AccAccountsDBO> accountNoList= accBatchFeeTransaction.getAccountNosByCampus(campusId);
		//master list created for adding newly created master or the master which are not saved.
		Map<Integer, Map<Integer,String>> durationMasterMap = new HashMap<Integer, Map<Integer,String>>();
		Map<Integer,  Map<Integer,String>> durationDetailMasterMap = new HashMap<Integer,  Map<Integer,String>>();
		Map<Integer,SelectDTO> yearMap = new HashMap<>();
		if(!Utils.isNullOrEmpty(durationDTOList)) {
			durationDTOList.forEach(durTo->{
				Map<Integer,String> durListMap = new HashMap<>();
				Map<Integer,String> durDetListMap = new HashMap<>();
				SelectDTO academicYearDTO = new SelectDTO();
				academicYearDTO.setValue(Integer.toString(durTo.getYearNo()));
				academicYearDTO.setLabel(durTo.getAcademicYearDTO().getLabel());
				yearMap.put(Integer.parseInt(durTo.getAcaDurationDTO().getValue()), academicYearDTO);
				durTo.getAccBatchFeeDurationsDetailsDTOList().forEach(ddTo->{
					if(!Utils.isNullOrEmpty(ddTo.getAcaDurationDetailDTO()) && !Utils.isNullOrEmpty(ddTo.getAcaDurationDetailDTO().getValue())){
						durDetListMap.put(Integer.parseInt(ddTo.getAcaDurationDetailDTO().getValue()),Integer.toString(ddTo.getSem()));
					}
					else {
						durDetListMap.put(null, Integer.toString(ddTo.getYear()) );	
					}
				});
				if(!Utils.isNullOrEmpty(durTo.getAcaDurationDTO())) {
					durListMap.put(Integer.parseInt(durTo.getAcaDurationDTO().getValue()), durTo.getAcaDurationDTO().getLabel());
				}
				durationMasterMap.put(Integer.parseInt(durTo.getAcaDurationDTO().getValue()), durDetListMap);
				durationDetailMasterMap.put(Integer.parseInt(durTo.getAcaDurationDTO().getValue()), durDetListMap);
			});
		}
		Map<Integer, String> accBatchFeeCategoryMap = admissionCategoryList.stream()
				.collect(Collectors.toMap(s -> (s.getErpAdmissionCategoryDBO().getId()),s -> s.getErpAdmissionCategoryDBO().getAdmissionCategoryName()));

		Map<Integer, String> accBatchFeeHeadMap = feeHeadsList.stream()
				.collect(Collectors.toMap(s -> (s.getId()),s -> s.getHeading()));

		Map<Integer, String> accBatchFeeAccountMap = accountNoList.stream().collect(Collectors.toMap(s -> (s.getId()),s -> s.getAccountNo()));
		
		Map<Integer, String> accAccountMasterMap = new HashMap<Integer, String>();
		accAccountMasterMap.putAll(accBatchFeeAccountMap);
		
		Map<Integer, String> accHeadMasterMap = new HashMap<Integer, String>(); 
		accHeadMasterMap.putAll(accBatchFeeHeadMap);
		
		Map<Integer, String> erpAdmCategoryMasterMap = new HashMap<Integer, String>(); 
		erpAdmCategoryMasterMap.putAll(accBatchFeeCategoryMap);

		if(!Utils.isNullOrEmpty(accBatchFeeDBO)) {
			accBatchFeeDTO.setId(accBatchFeeDBO.getId());
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO())) {
				SelectDTO programmeDTO = new SelectDTO();
				programmeDTO.setValue(Integer.toString(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getId()));
				programmeDTO.setLabel(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
				accBatchFeeDTO.setProgrammeDTO(programmeDTO);
				SelectDTO accBatchDTO = new SelectDTO();
				accBatchDTO.setValue(Integer.toString(accBatchFeeDBO.getAcaBatchDBO().getId()));
				accBatchDTO.setLabel(accBatchFeeDBO.getAcaBatchDBO().getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
				accBatchFeeDTO.setAcaBatchDTO(accBatchDTO);
			}
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO())) {
				SelectDTO batchYearDTO = new SelectDTO();
				batchYearDTO.setValue(Integer.toString(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO().getErpAcademicYearDBO().getId()));
				batchYearDTO.setLabel(accBatchFeeDBO.getAcaBatchDBO().getErpProgrammeBatchwiseSettingsDBO().getErpAcademicYearDBO().getAcademicYearName());
				accBatchFeeDTO.setBatchYearDTO(batchYearDTO);
			}
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getFeeCollectionSet())) {
				SelectDTO feeCollectionSet = new SelectDTO();
				feeCollectionSet.setValue(accBatchFeeDBO.getFeeCollectionSet());
				feeCollectionSet.setLabel(accBatchFeeDBO.getFeeCollectionSet());
				accBatchFeeDTO.setFeeCollectionSet(feeCollectionSet);
			}
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getErpSpecializationDBO())) {
				SelectDTO erpSpecializationDto = new SelectDTO();
				erpSpecializationDto.setValue(Integer.toString(accBatchFeeDBO.getErpSpecializationDBO().getId()));
				erpSpecializationDto.setLabel(accBatchFeeDBO.getErpSpecializationDBO().getSpecializationName());
				accBatchFeeDTO.setErpSpecializationDTO(erpSpecializationDto);
			}
			
			List<AccBatchFeeDurationsDTO> accBatchFeeDurationsDTOList = new ArrayList<AccBatchFeeDurationsDTO>();
			
			if(!Utils.isNullOrEmpty(accBatchFeeDBO.getAccBatchFeeDurationsDBOSet())) {
				accBatchFeeDBO.getAccBatchFeeDurationsDBOSet().forEach(durationYears->{
					
					AccBatchFeeDurationsDTO accBatchFeeDurationsDTO = new AccBatchFeeDurationsDTO();
					AcaSessionDBO acaSessionDBO = durationYears.getAcaDurationDBO().getAcaDurationDetailDBOSet().stream().filter(ac->ac.getAcaBatchDBO()!=null && ac.getAcaBatchDBO().getId() == accBatchFeeDBO.getAcaBatchDBO().getId()).findFirst().get().getAcaSessionDBO();
					accBatchFeeDurationsDTO.setYearNo(acaSessionDBO.getYearNumber());
					if(!Utils.isNullOrEmpty(durationMasterMap) && durationMasterMap.containsKey(durationYears.getAcaDurationDBO().getId())){
						durationMasterMap.remove(durationYears.getAcaDurationDBO().getId());
					} //remove year from master map. already saved
					SelectDTO durationDTO = new SelectDTO();
					durationDTO.setValue(Integer.toString(durationYears.getAcaDurationDBO().getId()));
					accBatchFeeDurationsDTO.setAcaDurationDTO(durationDTO);
					
					SelectDTO academicYearDTO = yearMap.get(durationYears.getAcaDurationDBO().getId());

					if(!Utils.isNullOrEmpty(durationYears.getAcaDurationDBO())) {
						accBatchFeeDurationsDTO.setAcademicYearDTO(academicYearDTO);
					}
					List<AccBatchFeeDurationsDetailsDTO> accBatchFeeDurationsDetailsDTOList = new ArrayList<AccBatchFeeDurationsDetailsDTO>();
					
					durationYears.getAccBatchFeeDurationsDetailDBOSet().forEach(durationDetDbo->{
						AccBatchFeeDurationsDetailsDTO accBatchFeeDurationsDetailsDTO = new AccBatchFeeDurationsDetailsDTO();
						accBatchFeeDurationsDTO.setId(durationDetDbo.getId());
					
					if(!Utils.isNullOrEmpty(durationDetDbo.getAcaDurationDetailDBO())) {
						SelectDTO acaDurationDetailDTO = new SelectDTO();
						acaDurationDetailDTO.setValue(Integer.toString(durationDetDbo.getAcaDurationDetailDBO().getId()));
						acaDurationDetailDTO.setLabel(academicYearDTO.getValue());
						accBatchFeeDurationsDetailsDTO.setAcaDurationDetailDTO(acaDurationDetailDTO);
					}
					if(!Utils.isNullOrEmpty(durationDetDbo.getAcaDurationDetailDBO())) {
						durationDetailMasterMap.get(durationYears.getAcaDurationDBO().getId()).remove(durationDetDbo.getAcaDurationDetailDBO().getId());
					}
					else {
						durationDetailMasterMap.get(durationYears.getAcaDurationDBO().getId()).remove(null); //yearwise doesn't have detail id
					}
					//----------------
					accBatchFeeCategoryMap.clear();
					accBatchFeeCategoryMap.putAll(erpAdmCategoryMasterMap);
					
					if(!Utils.isNullOrEmpty(durationDetDbo.getAcaDurationDetailDBO()) &&  !Utils.isNullOrEmpty(acaSessionDBO) && !Utils.isNullOrEmpty(acaSessionDBO.getYearNumber()) ) {
						accBatchFeeDurationsDetailsDTO.setSem(durationDetDbo.getAcaDurationDetailDBO().getAcaSessionDBO().getTermNumber());
						accBatchFeeDurationsDetailsDTO.setOrder(durationDetDbo.getAcaDurationDetailDBO().getAcaSessionDBO().getTermNumber());
					}
					else {
						accBatchFeeDurationsDetailsDTO.setYear(acaSessionDBO.getYearNumber());
						accBatchFeeDurationsDetailsDTO.setOrder(0);
					}
					if(!Utils.isNullOrEmpty(durationDetDbo.getAccBatchFeeCategoryDBOSet())) {
						List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList = new ArrayList<AccBatchFeeCategoryDTO>(); 
						durationDetDbo.getAccBatchFeeCategoryDBOSet().stream()
						.filter(category->category.getRecordStatus() == 'A').collect(Collectors.toList()).forEach(category->{

							AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = createAccBatchFeeCategoryDTO(category.getErpAdmissionCategoryDBO().getId(),
									category.getErpAdmissionCategoryDBO().getAdmissionCategoryName(), category.getId());
							accBatchFeeCategoryMap.remove(category.getErpAdmissionCategoryDBO().getId());
							
							if(!Utils.isNullOrEmpty(category.getAccBatchFeeHeadDBOSet())) {
								List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList = new ArrayList<AccBatchFeeHeadDTO>();
								accBatchFeeHeadMap.clear();
								accBatchFeeHeadMap.putAll(accHeadMasterMap);
								category.getAccBatchFeeHeadDBOSet().stream().filter(head->head.getRecordStatus() == 'A').collect(Collectors.toList()).forEach(head->{
									AccBatchFeeHeadDTO accBatchFeeHeadDTO =  createAccBatchFeeHeadDTO(head.getAccFeeHeadsDBO().getId(), head.getAccFeeHeadsDBO().getHeading(), head.getId());
									accBatchFeeHeadMap.remove(head.getAccFeeHeadsDBO().getId());
									if(!Utils.isNullOrEmpty(head.getAccBatchFeeAccountDBOSet())) {
										List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
										accBatchFeeAccountMap.clear();
										accBatchFeeAccountMap.putAll(accAccountMasterMap);
										head.getAccBatchFeeAccountDBOSet().stream().filter(account->account.getRecordStatus() == 'A').collect(Collectors.toList()).forEach(account->{
											AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(account.getAccAccountsDBO().getId(), account.getAccAccountsDBO().getAccountNo(), account.getId());
											accBatchFeeAccountMap.remove(account.getAccAccountsDBO().getId());
											SelectDTO erpCurrencyDTO = new SelectDTO();
											erpCurrencyDTO.setValue(Integer.toString(account.getErpCurrencyDBO().getId()));
											erpCurrencyDTO.setLabel(account.getErpCurrencyDBO().getCurrencyCode());
											accBatchFeeAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
											
											if(!Utils.isNullOrEmpty(account.getAccFeeDemandAdjustmentCategoryDBO())) {
												SelectDTO accFeeDemandAdjustmentCategoryDTO = new SelectDTO();
												accFeeDemandAdjustmentCategoryDTO.setValue(Integer.toString(account.getAccFeeDemandAdjustmentCategoryDBO().getId()));
												accFeeDemandAdjustmentCategoryDTO.setLabel(account.getAccFeeDemandAdjustmentCategoryDBO().getAdjustmentCategory());
												accBatchFeeAccountDTO.setAccFeeAdjustmentCategoryDTO(accFeeDemandAdjustmentCategoryDTO);
											}
											
											if(!Utils.isNullOrEmpty(account.getFeeAccountAmount())) {
												accBatchFeeAccountDTO.setFeeAccountAmount(account.getFeeAccountAmount());
											}
											if(!Utils.isNullOrEmpty(account.getFeeScholarshipAmount())) {
												accBatchFeeAccountDTO.setFeeScholarshipAmount(account.getFeeScholarshipAmount());
											}
											accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
										});
										//---------for the accounts which are not saved previously but some items saved
										accBatchFeeAccountMap.forEach((accountId, accountName)->{
											accBatchFeeAccountMap.forEach((accId,accNo)->{
												AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(accId, accNo, null);
												accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
											});
										});
										//------------------
										List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOListSorted = accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList());
										accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOListSorted);
									}
									accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
								});
								//-------------for the heads which are not saved previously but some items saved
								accBatchFeeHeadMap.forEach((headId,headName)->{
									AccBatchFeeHeadDTO accBatchFeeHeadDTO = createAccBatchFeeHeadDTO(headId, headName, null);
									List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
									accAccountMasterMap.forEach((accId,accNo)->{
										AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(accId, accNo, null); 
										accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
									});
									accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList()));
									accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
								});
								List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOListSorted = accBatchFeeHeadDTOList.stream().sorted(Comparator.comparing(o -> o.getAccFeeHeadsDTO().getLabel())).collect(Collectors.toList());
								accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadDTOListSorted);
								//-------------------
							}
							accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
						});
						//----------for the category which are not saved but some items saved
						accBatchFeeCategoryMap.forEach((catId, catName)->{
							AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = createAccBatchFeeCategoryDTO(catId, catName, null); 
							List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList = new ArrayList<AccBatchFeeHeadDTO>();
							accHeadMasterMap.forEach((hId, hName)->{
								AccBatchFeeHeadDTO accBatchFeeHeadDTO = createAccBatchFeeHeadDTO(hId, hName, null);
								List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
								accAccountMasterMap.forEach((accId,accNo)->{
									AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(accId, accNo, null);
									accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
								});
								accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList()));
								accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
							});
							accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadDTOList.stream().sorted(Comparator.comparing(o -> o.getAccFeeHeadsDTO().getLabel())).collect(Collectors.toList()));
							accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
						});
						//---------
						List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOListSorted = accBatchFeeCategoryDTOList.stream().sorted(Comparator.comparing(o -> o.getErpAdmissionCategoryDTO().getLabel())).collect(Collectors.toList());
						accBatchFeeDurationsDetailsDTO.setAccBatchFeeCategoryDTOList(accBatchFeeCategoryDTOListSorted);
						accBatchFeeDurationsDetailsDTOList.add(accBatchFeeDurationsDetailsDTO);
					}
				});
				if(!Utils.isNullOrEmpty(durationDetailMasterMap)) {
				   Map<Integer, String> detMap = durationDetailMasterMap.get(Integer.parseInt(accBatchFeeDurationsDTO.getAcaDurationDTO().getValue()));
				   detMap.forEach((detailId, yearorSem)->{
					   AccBatchFeeDurationsDetailsDTO accBatchFeeDurationsDetailsDTO = new AccBatchFeeDurationsDetailsDTO();
					   SelectDTO accDurationDetDTO = new SelectDTO();
					   if(Utils.isNullOrEmpty(detailId)) {
						   accDurationDetDTO.setValue(null);
						   accBatchFeeDurationsDetailsDTO.setYear(Integer.parseInt(yearorSem));
						   accBatchFeeDurationsDetailsDTO.setOrder(0);
					   }
					   else {
						   accDurationDetDTO.setValue(Integer.toString(detailId)); 
						   accBatchFeeDurationsDetailsDTO.setSem(Integer.parseInt(yearorSem));
						   accBatchFeeDurationsDetailsDTO.setOrder(Integer.parseInt(yearorSem));
					   }
					   accDurationDetDTO.setLabel(yearorSem);
					   accBatchFeeDurationsDetailsDTO.setAcaDurationDetailDTO(accDurationDetDTO);
					   List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList = new ArrayList<AccBatchFeeCategoryDTO>(); 
						erpAdmCategoryMasterMap.forEach((catId, catName)->{
							AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = createAccBatchFeeCategoryDTO(catId, catName, null); 
							List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList = new ArrayList<AccBatchFeeHeadDTO>();
							accHeadMasterMap.forEach((hId, hName)->{
								AccBatchFeeHeadDTO accBatchFeeHeadDTO = createAccBatchFeeHeadDTO(hId, hName, null);
								List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
								accAccountMasterMap.forEach((accId,accNo)->{
									AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(accId, accNo, null);
									accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
								});
								accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList()));
								accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
							});
							accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadDTOList.stream().sorted(Comparator.comparing(o -> o.getAccFeeHeadsDTO().getLabel())).collect(Collectors.toList()));
							accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
						});
						List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOListSorted = accBatchFeeCategoryDTOList.stream().sorted(Comparator.comparing(o -> o.getErpAdmissionCategoryDTO().getLabel())).collect(Collectors.toList());
						accBatchFeeDurationsDetailsDTO.setAccBatchFeeCategoryDTOList(accBatchFeeCategoryDTOListSorted);	
					   accBatchFeeDurationsDetailsDTOList.add(accBatchFeeDurationsDetailsDTO);
					});
				}
				accBatchFeeDurationsDTO.setAccBatchFeeDurationsDetailsDTOList(accBatchFeeDurationsDetailsDTOList.stream().sorted(Comparator.comparing(o -> o.getOrder())).collect(Collectors.toList()));
				accBatchFeeDurationsDTOList.add(accBatchFeeDurationsDTO);
				});
			}
			
			if(!Utils.isNullOrEmpty(durationMasterMap)) {
				   durationMasterMap.forEach((durationId, durDetMasterMap)->{
					   AccBatchFeeDurationsDTO accBatchFeeDurationsDTO = new AccBatchFeeDurationsDTO();
					   SelectDTO academicDTO = yearMap.get(durationId);
						accBatchFeeDurationsDTO.setYearNo(Integer.parseInt(academicDTO.getValue()));
						
						SelectDTO acaDTO = new SelectDTO();
						acaDTO.setLabel(academicDTO.getLabel());
						accBatchFeeDurationsDTO.setAcademicYearDTO(acaDTO);
						
						SelectDTO acaDurationDTO = new SelectDTO();
						acaDurationDTO.setValue( Integer.toString(durationId));
						accBatchFeeDurationsDTO.setAcaDurationDTO(acaDurationDTO);
						
						List<AccBatchFeeDurationsDetailsDTO> accBatchFeeDurationsDetailsDTOList = new ArrayList<AccBatchFeeDurationsDetailsDTO>();
					   durDetMasterMap.forEach((detailId, yearOrSem)->{
						   AccBatchFeeDurationsDetailsDTO accBatchFeeDurationsDetailsDTO = new AccBatchFeeDurationsDetailsDTO();
						   if(Utils.isNullOrEmpty(detailId)) {
							   accBatchFeeDurationsDetailsDTO.setYear(Integer.parseInt(academicDTO.getValue()));
							   accBatchFeeDurationsDetailsDTO.setOrder(0);
						   }
						   else {
							   SelectDTO accDurationDetDTO = new SelectDTO();
							   accDurationDetDTO.setValue(Integer.toString(detailId)); 
							   accDurationDetDTO.setLabel(yearOrSem);
							   accBatchFeeDurationsDetailsDTO.setSem(Integer.parseInt(yearOrSem));
							   accBatchFeeDurationsDetailsDTO.setOrder(Integer.parseInt(yearOrSem));
							   accBatchFeeDurationsDetailsDTO.setAcaDurationDetailDTO(accDurationDetDTO);
						   }
						   
						   List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList = new ArrayList<AccBatchFeeCategoryDTO>(); 
							erpAdmCategoryMasterMap.forEach((catId, catName)->{
								AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = createAccBatchFeeCategoryDTO(catId, catName, null); 
								List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList = new ArrayList<AccBatchFeeHeadDTO>();
								accHeadMasterMap.forEach((hId, hName)->{
									AccBatchFeeHeadDTO accBatchFeeHeadDTO = createAccBatchFeeHeadDTO(hId, hName, null);
									List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
									accAccountMasterMap.forEach((accId,accNo)->{
										AccBatchFeeAccountDTO accBatchFeeAccountDTO = createAccBatchfeeAccountDTO(accId, accNo, null);
										accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
									});
									accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList()));
									accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
								});
								accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadDTOList.stream().sorted(Comparator.comparing(o -> o.getAccFeeHeadsDTO().getLabel())).collect(Collectors.toList()));
								accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
							});
							List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOListSorted = accBatchFeeCategoryDTOList.stream().sorted(Comparator.comparing(o -> o.getErpAdmissionCategoryDTO().getLabel())).collect(Collectors.toList());
							accBatchFeeDurationsDetailsDTO.setAccBatchFeeCategoryDTOList(accBatchFeeCategoryDTOListSorted);
						   accBatchFeeDurationsDetailsDTOList.add(accBatchFeeDurationsDetailsDTO);
						});
					   accBatchFeeDurationsDTO.setAccBatchFeeDurationsDetailsDTOList(accBatchFeeDurationsDetailsDTOList.stream().sorted(Comparator.comparing(o -> o.getOrder())).collect(Collectors.toList()));
					   accBatchFeeDurationsDTOList.add(accBatchFeeDurationsDTO);
				   });
				}
			
			List<AccBatchFeeDurationsDTO> accBatchFeeDurationsDTOListSorted = accBatchFeeDurationsDTOList.stream().sorted(Comparator.comparing(o -> o.getYearNo())).collect(Collectors.toList());
			accBatchFeeDTO.setAccBatchFeeDurationsDTOList(accBatchFeeDurationsDTOListSorted);
		}
		return accBatchFeeDTO;
	}
	public String ordinalNo(int value) {
		int hunRem = value % 100;
		int tenRem = value % 10;
		if (hunRem - tenRem == 10) {
			return "th";
		}
		switch (tenRem) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
	
	public AccBatchFeeCategoryDTO createAccBatchFeeCategoryDTO(int catId, String catName, Integer accBatchFeecatTableId) {
		AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = new AccBatchFeeCategoryDTO();
		if(!Utils.isNullOrEmpty(accBatchFeecatTableId)) {
			accBatchFeeCategoryDTO.setId(accBatchFeecatTableId);
		}
		SelectDTO erpAdmissionCategoryDTO = new SelectDTO();
		erpAdmissionCategoryDTO.setValue(Integer.toString(catId));
		erpAdmissionCategoryDTO.setLabel(catName);
		accBatchFeeCategoryDTO.setErpAdmissionCategoryDTO(erpAdmissionCategoryDTO);		
		return accBatchFeeCategoryDTO;
	}
	
	public AccBatchFeeHeadDTO createAccBatchFeeHeadDTO(int headId, String headName, Integer accBatchFeeHeadTableId) {
		AccBatchFeeHeadDTO accBatchFeeHeadDTO = new AccBatchFeeHeadDTO();
		if(!Utils.isNullOrEmpty(accBatchFeeHeadTableId)) {
			accBatchFeeHeadDTO.setId(accBatchFeeHeadTableId);
		}
		SelectDTO accFeeHeadDTO = new SelectDTO();
		accFeeHeadDTO.setValue(Integer.toString(headId));
		accFeeHeadDTO.setLabel(headName);
		accBatchFeeHeadDTO.setAccFeeHeadsDTO(accFeeHeadDTO);
		return accBatchFeeHeadDTO;
	}
	
	public AccBatchFeeAccountDTO createAccBatchfeeAccountDTO(int accId, String accNo, Integer accBatchAccountTableId) {
		AccBatchFeeAccountDTO accBatchFeeAccountDTO = new AccBatchFeeAccountDTO();
		if(!Utils.isNullOrEmpty(accBatchAccountTableId)) {
			accBatchFeeAccountDTO.setId(accBatchAccountTableId);
		}
		SelectDTO accAccountDTO = new SelectDTO();
		accAccountDTO.setValue(Integer.toString(accId));
		accAccountDTO.setLabel(accNo);
		accBatchFeeAccountDTO.setAccAccountsDTO(accAccountDTO);
		return accBatchFeeAccountDTO;
		
	}

	public AccBatchFeeDurationsDTO createAccBatchFeeDurations(int acaDurationDetailId, String durationName, Integer accBatchFeeDurationTableId) {
		AccBatchFeeDurationsDTO accBatchFeeDurationsDTO = new AccBatchFeeDurationsDTO();
		if(!Utils.isNullOrEmpty(accBatchFeeDurationTableId)) {
			accBatchFeeDurationsDTO.setId(accBatchFeeDurationTableId);
		}
		SelectDTO acaDurationDetailDTO = new SelectDTO();
		acaDurationDetailDTO.setValue(Integer.toString(acaDurationDetailId));
		acaDurationDetailDTO.setLabel(durationName);
		accBatchFeeDurationsDTO.setAcaDurationDTO(acaDurationDetailDTO);
		//accBatchFeeDurationsDTO.setAcaDurationDetailDTO(acaDurationDetailDTO);
		return accBatchFeeDurationsDTO;
	}
	public List<AccBatchFeeDurationsDTO> getAccBatchFeeDurationDTOListOld(List<AcaDurationDetailDBO> durationList){
		List<AccBatchFeeDurationsDTO> batchFeeDurationDTOList = new ArrayList<AccBatchFeeDurationsDTO>();
		if(!Utils.isNullOrEmpty(durationList)) {
			durationList.forEach(duration->{
				AccBatchFeeDurationsDTO batchFeeDurationDTO = new AccBatchFeeDurationsDTO();
				SelectDTO acaDurationDetailDTO = new SelectDTO();
				acaDurationDetailDTO.setValue(Integer.toString(duration.getId()));
				acaDurationDetailDTO.setLabel((!Utils.isNullOrEmpty(duration.getAcaSessionDBO().getYearNumber()) ? duration.getAcaSessionDBO().getYearNumber() + ordinalNo(duration.getAcaSessionDBO().getYearNumber())
					+ " Year - ":"")  + duration.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
				batchFeeDurationDTO.setAcaDurationDTO(acaDurationDetailDTO);
				if(!Utils.isNullOrEmpty(duration.getAcaSessionDBO().getYearNumber())) {
					batchFeeDurationDTO.setYearNo(duration.getAcaSessionDBO().getYearNumber()); //year number
				}
				SelectDTO academicYearDTO = new SelectDTO();
				academicYearDTO.setValue(Integer.toString(duration.getAcaDurationDBO().getErpAcademicYearDBO().getId()));
				academicYearDTO.setLabel(duration.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
				batchFeeDurationDTO.setAcademicYearDTO(academicYearDTO);
				batchFeeDurationDTOList.add(batchFeeDurationDTO);
			});
		}
		Collections.sort(batchFeeDurationDTOList, new Comparator<AccBatchFeeDurationsDTO>() {
			@Override
			public int compare(AccBatchFeeDurationsDTO o1, AccBatchFeeDurationsDTO o2) {
				return (o1.getYearNo() - o2.getYearNo());
			}
		});
		return batchFeeDurationDTOList;
	}
	public List<AccBatchFeeDurationsDTO> getAccBatchFeeDurationDTOList(List<AcaDurationDetailDBO> durationList){
		List<AccBatchFeeDurationsDTO> batchFeeDurationDTOList = new ArrayList<AccBatchFeeDurationsDTO>();

		Map<Integer, List<AcaDurationDetailDBO>> durationMap = !Utils.isNullOrEmpty(durationList)?durationList.stream().collect(Collectors.groupingBy(d->d.getAcaSessionDBO().getYearNumber())):new HashMap<Integer, List<AcaDurationDetailDBO>>();
		
		durationMap.forEach((yearNo, duration)->{
			AccBatchFeeDurationsDTO accBatchFeeDurationsDTO = new AccBatchFeeDurationsDTO();
			accBatchFeeDurationsDTO.setYearNo(yearNo);
			List<AccBatchFeeDurationsDetailsDTO> accBatchFeeDurationsDetailsDTOList = new ArrayList<AccBatchFeeDurationsDetailsDTO>();
			
			AtomicReference<Integer> count = new AtomicReference<>(0);
			duration.forEach(durDet->{
				count.set(1);
				if(durDet.getAcaDurationDBO().getAcaSessionGroupDBO().getSessionNumber() == 1) {
					count.set(2);
				}
				for(int i = 1; i<=count.get();i++) {
					AccBatchFeeDurationsDetailsDTO accBatchFeeDurationsDetailsDTO = new AccBatchFeeDurationsDetailsDTO();
					SelectDTO academicYearDTO = new SelectDTO();
					academicYearDTO.setLabel((!Utils.isNullOrEmpty(durDet.getAcaSessionDBO().getYearNumber()) ? durDet.getAcaSessionDBO().getYearNumber() + ordinalNo(durDet.getAcaSessionDBO().getYearNumber())
					+ " Year - ":"")  + durDet.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
				
					academicYearDTO.setValue(durDet.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
					accBatchFeeDurationsDTO.setAcademicYearDTO(academicYearDTO);
					if(durDet.getAcaDurationDBO().getAcaSessionGroupDBO().getSessionNumber() == 1 && i==1) {
						SelectDTO acaDurationDTO = new SelectDTO();
						acaDurationDTO.setValue(Integer.toString(durDet.getAcaDurationDBO().getId()));
						acaDurationDTO.setLabel(durDet.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
						accBatchFeeDurationsDTO.setAcaDurationDTO(acaDurationDTO);
						accBatchFeeDurationsDTO.setYearNo(durDet.getAcaSessionDBO().getYearNumber());
						accBatchFeeDurationsDetailsDTO.setYear(durDet.getAcaSessionDBO().getYearNumber());
						accBatchFeeDurationsDetailsDTO.setOrder(0);
					}else {
						SelectDTO acaDurationDetailDTO = new SelectDTO();
						acaDurationDetailDTO.setValue(Integer.toString(durDet.getId()));
						acaDurationDetailDTO.setLabel(durDet.getAcaDurationDBO().getErpAcademicYearDBO().getAcademicYearName());
						accBatchFeeDurationsDetailsDTO.setAcaDurationDetailDTO(acaDurationDetailDTO);
						accBatchFeeDurationsDetailsDTO.setOrder(durDet.getAcaSessionDBO().getTermNumber());
						accBatchFeeDurationsDetailsDTO.setSem(durDet.getAcaSessionDBO().getTermNumber());
					}
					accBatchFeeDurationsDetailsDTOList.add(accBatchFeeDurationsDetailsDTO);
				}
			});
			accBatchFeeDurationsDTO.setAccBatchFeeDurationsDetailsDTOList(accBatchFeeDurationsDetailsDTOList);
			batchFeeDurationDTOList.add(accBatchFeeDurationsDTO);
		});
		Collections.sort(batchFeeDurationDTOList, new Comparator<AccBatchFeeDurationsDTO>() {
			@Override
			public int compare(AccBatchFeeDurationsDTO o1, AccBatchFeeDurationsDTO o2) {
				return (o1.getYearNo() - o2.getYearNo());
			}
		});
		return batchFeeDurationDTOList;
	}
	public AccBatchFeeDBO convertAccBatchFeeDTOToDbo(AccBatchFeeDTO accBatchFeeDTO, String userId) {
		AccBatchFeeDBO accBatchFeeDBO = !Utils.isNullOrEmpty(accBatchFeeDTO.getId())?accBatchFeeTransaction.getAccBatchFeeById(accBatchFeeDTO.getId()): new AccBatchFeeDBO();
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getFeeCollectionSet())) {
			accBatchFeeDBO.setFeeCollectionSet(accBatchFeeDTO.getFeeCollectionSet().getValue());
		}
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getErpSpecializationDTO())) {
			ErpSpecializationDBO erpSpecializationDBO = new ErpSpecializationDBO();
			erpSpecializationDBO.setId(Integer.parseInt(accBatchFeeDTO.getErpSpecializationDTO().getValue()));
			accBatchFeeDBO.setErpSpecializationDBO(erpSpecializationDBO);
		}
		accBatchFeeDBO.setCreatedUsersId(Integer.parseInt(userId));
		accBatchFeeDBO.setModifiedUsersId(Integer.parseInt(userId));
		accBatchFeeDBO.setRecordStatus('A');
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getAcaBatchDTO())) {
			AcaBatchDBO acaBatchDBO = new AcaBatchDBO();
			acaBatchDBO.setId(Integer.parseInt(accBatchFeeDTO.getAcaBatchDTO().getValue()));
			accBatchFeeDBO.setAcaBatchDBO(acaBatchDBO);
		}
		List<AccFinancialYearDBO> accFinancialYearList = accBatchFeeTransaction.getFinancialYear();
		Map<String, Integer> finMap = accFinancialYearList.stream().collect(Collectors.toMap(fin->fin.getFinancialYear(), fin->fin.getId()));
		
		Map<Integer, AccBatchFeeDurationsDBO> accBatchFeeYearsDBOMap = !Utils.isNullOrEmpty(accBatchFeeDBO.getAccBatchFeeDurationsDBOSet())
				?accBatchFeeDBO.getAccBatchFeeDurationsDBOSet().stream().collect(Collectors.toMap(duration->duration.getAcaDurationDBO().getId(), duration->duration)):new HashMap<Integer, AccBatchFeeDurationsDBO>(); 
		 
			
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getAccBatchFeeDurationsDTOList())) {
			//AtomicReference<Double> durationTotal = new AtomicReference<>((double) 0);
			Set<AccBatchFeeDurationsDBO> accBatchFeeDurationsDBOSet = new HashSet<AccBatchFeeDurationsDBO>();
			
			accBatchFeeDTO.getAccBatchFeeDurationsDTOList().forEach(durations->{
				AtomicReference<BigDecimal> yearTotal = new AtomicReference<>((BigDecimal) new BigDecimal(0));
				AccBatchFeeDurationsDBO accBatchFeeDurationsDBO = accBatchFeeYearsDBOMap.containsKey(Integer.parseInt(durations.getAcaDurationDTO().getValue()))?
						accBatchFeeYearsDBOMap.get(Integer.parseInt(durations.getAcaDurationDTO().getValue())):new AccBatchFeeDurationsDBO();
				AcaDurationDBO acaDurationDBO = new AcaDurationDBO();
				acaDurationDBO.setId(Integer.parseInt(durations.getAcaDurationDTO().getValue()));
				accBatchFeeDurationsDBO.setAcaDurationDBO(acaDurationDBO);
				accBatchFeeDurationsDBO.setAccBatchFeeDBO(accBatchFeeDBO);
				accBatchFeeDurationsDBO.setCreatedUsersId(Integer.parseInt(userId));
				accBatchFeeDurationsDBO.setModifiedUsersId(Integer.parseInt(userId));
				accBatchFeeDurationsDBO.setRecordStatus('A');
				
				Map<Integer, AccBatchFeeDurationsDetailDBO> accDurationDetailDboMap =  !Utils.isNullOrEmpty(accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet())?accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet().stream()
						.collect(HashMap::new, (m,v)->m.put(v.getAcaDurationDetailDBO() == null?null: v.getAcaDurationDetailDBO().getId(), v), HashMap::putAll):new HashMap<Integer, AccBatchFeeDurationsDetailDBO>();
				
						
				AtomicReference<BigDecimal> durationTotal = new AtomicReference<>((BigDecimal) new BigDecimal(0));
				Set<AccBatchFeeDurationsDetailDBO> accBatchFeeDurationsDetailDBOSet = new HashSet<AccBatchFeeDurationsDetailDBO>();
				durations.getAccBatchFeeDurationsDetailsDTOList().forEach(durDet->{
					durationTotal.set(new BigDecimal(0));
					AccBatchFeeDurationsDetailDBO accBatchFeeDurationsDetailDBO = accDurationDetailDboMap.containsKey(durDet.getAcaDurationDetailDTO() == null?null: Integer.parseInt(durDet.getAcaDurationDetailDTO().getValue()))?
							accDurationDetailDboMap.get(durDet.getAcaDurationDetailDTO() == null?null:Integer.parseInt(durDet.getAcaDurationDetailDTO().getValue())):new AccBatchFeeDurationsDetailDBO();
					accBatchFeeDurationsDetailDBO.setAccBatchFeeDurationsDBO(accBatchFeeDurationsDBO);
					
					if(!Utils.isNullOrEmpty(finMap.get(durations.getAcademicYearDTO().getValue()))){
						AccFinancialYearDBO accFinancialYearDBO = new AccFinancialYearDBO();
						accFinancialYearDBO.setId(finMap.get(durations.getAcademicYearDTO().getLabel().substring(11)));
						accBatchFeeDurationsDetailDBO.setAccFinancialYearDBO(accFinancialYearDBO);
					}
					//---------
					//semester
					if(!Utils.isNullOrEmpty(durDet.getAcaDurationDetailDTO()) && !Utils.isNullOrEmpty(durDet.getAcaDurationDetailDTO().getValue())) {
						AcaDurationDetailDBO acaDurationDetailDBO = new AcaDurationDetailDBO();
						acaDurationDetailDBO.setId(Integer.parseInt(durDet.getAcaDurationDetailDTO().getValue()));
						accBatchFeeDurationsDetailDBO.setAcaDurationDetailDBO(acaDurationDetailDBO);
						//------
						AccFinancialYearDBO accFinancialYearDBO = new AccFinancialYearDBO();
						accFinancialYearDBO.setId(finMap.get(durations.getAcademicYearDTO().getLabel().substring(11)));
						accBatchFeeDurationsDetailDBO.setAccFinancialYearDBO(accFinancialYearDBO);
						//---------
					}
					if(!Utils.isNullOrEmpty(durDet.getAccBatchFeeCategoryDTOList())) {
						AtomicReference<BigDecimal> categoryTotal = new AtomicReference<>((BigDecimal) new BigDecimal(0));
						AtomicReference<BigDecimal> headwiseTotal = new AtomicReference<>((BigDecimal) new BigDecimal(0));
		                
						Set<AccBatchFeeCategoryDBO> accBatchFeeCategoryDBOSet = new HashSet<AccBatchFeeCategoryDBO>(); 
						Map<Integer, AccBatchFeeCategoryDBO> accBatchFeeCategoryDboMap =  !Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet())
								?accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet().stream()
								.collect(Collectors.toMap(catOrig->catOrig.getErpAdmissionCategoryDBO().getId(), catOrig->catOrig)):new HashMap<Integer, AccBatchFeeCategoryDBO>();
						
						durDet.getAccBatchFeeCategoryDTOList().forEach(category->{
							 categoryTotal.set(new BigDecimal(0));
							 AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = accBatchFeeCategoryDboMap.containsKey(Integer.parseInt(category.getErpAdmissionCategoryDTO().getValue()))?
								accBatchFeeCategoryDboMap.get(Integer.parseInt(category.getErpAdmissionCategoryDTO().getValue())):new AccBatchFeeCategoryDBO();
							if(!Utils.isNullOrEmpty(category.getErpAdmissionCategoryDTO())){
								ErpAdmissionCategoryDBO erpAdmissionCategoryDBO = new ErpAdmissionCategoryDBO();
								erpAdmissionCategoryDBO.setId(Integer.parseInt(category.getErpAdmissionCategoryDTO().getValue()));
								accBatchFeeCategoryDBO.setErpAdmissionCategoryDBO(erpAdmissionCategoryDBO);
							}
							accBatchFeeCategoryDBO.setAccBatchFeeDurationsDetailDBO(accBatchFeeDurationsDetailDBO);
							if(!Utils.isNullOrEmpty(category.getAccBatchFeeHeadDTOList())) {
								Set<AccBatchFeeHeadDBO> acBatchFeeHeadDBOSet = new HashSet<AccBatchFeeHeadDBO>();
								Map<Integer, AccBatchFeeHeadDBO> accBatchFeeHeadDboMap =  !Utils.isNullOrEmpty(accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet())?
										accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet().stream().collect(Collectors.toMap(headOrig->headOrig.getAccFeeHeadsDBO().getId(), headOrig->headOrig)):new HashMap<Integer, AccBatchFeeHeadDBO>();
								category.getAccBatchFeeHeadDTOList().forEach(feeHead->{
									headwiseTotal.set(new BigDecimal(0));
									AccBatchFeeHeadDBO accBatchFeeHeadDBO = accBatchFeeHeadDboMap.containsKey(Integer.parseInt(feeHead.getAccFeeHeadsDTO().getValue()))?
											accBatchFeeHeadDboMap.get(Integer.parseInt(feeHead.getAccFeeHeadsDTO().getValue())):new AccBatchFeeHeadDBO();
									if(!Utils.isNullOrEmpty(feeHead.getAccFeeHeadsDTO())) {
										AccFeeHeadsDBO accFeeHeadsDBO = new AccFeeHeadsDBO();
										accFeeHeadsDBO.setId(Integer.parseInt(feeHead.getAccFeeHeadsDTO().getValue()));
										accBatchFeeHeadDBO.setAccFeeHeadsDBO(accFeeHeadsDBO);
										accBatchFeeHeadDBO.setAccBatchFeeCategoryDBO(accBatchFeeCategoryDBO);
										Map<Integer, AccBatchFeeAccountDBO> accBatchFeeAccountDboMap =  !Utils.isNullOrEmpty(accBatchFeeHeadDBO.getAccBatchFeeAccountDBOSet())?
												accBatchFeeHeadDBO.getAccBatchFeeAccountDBOSet().stream()
												.collect(Collectors.toMap(accOrig->accOrig.getAccAccountsDBO().getId(), accOrig->accOrig)):new HashMap<Integer, AccBatchFeeAccountDBO>();
										if(!Utils.isNullOrEmpty(feeHead.getAccBatchFeeAccountDTOList())){
											Set<AccBatchFeeAccountDBO> accBatchFeeAccountDBOSet = new HashSet<AccBatchFeeAccountDBO>();
											feeHead.getAccBatchFeeAccountDTOList().forEach(account->{
												AccBatchFeeAccountDBO accBatchFeeAccountDBO = accBatchFeeAccountDboMap.containsKey(Integer.parseInt(account.getAccAccountsDTO().getValue()))?
													accBatchFeeAccountDboMap.get(Integer.parseInt(account.getAccAccountsDTO().getValue())):new AccBatchFeeAccountDBO();
												accBatchFeeAccountDBO.setAccBatchFeeHeadDBO(accBatchFeeHeadDBO);
												AccAccountsDBO accAccountsDBO = new AccAccountsDBO();
												accAccountsDBO.setId(Integer.parseInt(account.getAccAccountsDTO().getValue()));
												accBatchFeeAccountDBO.setAccAccountsDBO(accAccountsDBO);
												accBatchFeeAccountDBO.setFeeAccountAmount(account.getFeeAccountAmount());
												accBatchFeeAccountDBO.setFeeScholarshipAmount(account.getFeeScholarshipAmount());
												if(!Utils.isNullOrEmpty(account.getAccFeeAdjustmentCategoryDTO())) {
													AccFeeDemandAdjustmentCategoryDBO accFeeDemandAdjustmentCategoryDBO = new AccFeeDemandAdjustmentCategoryDBO();
													accFeeDemandAdjustmentCategoryDBO.setId(Integer.parseInt(account.getAccFeeAdjustmentCategoryDTO().getValue()));
													accBatchFeeAccountDBO.setAccFeeDemandAdjustmentCategoryDBO(accFeeDemandAdjustmentCategoryDBO);
												}
												if(!Utils.isNullOrEmpty(account.getErpCurrencyDTO())) {
													ErpCurrencyDBO erpCurrencyDBO = new ErpCurrencyDBO();
													erpCurrencyDBO.setId(Integer.parseInt(account.getErpCurrencyDTO().getValue()));
													accBatchFeeAccountDBO.setErpCurrencyDBO(erpCurrencyDBO);
												}
												if(!Utils.isNullOrEmpty(account.getFeeAccountAmount())) {
													categoryTotal.set(categoryTotal.get().add(account.getFeeAccountAmount()));
													headwiseTotal.set(headwiseTotal.get().add(account.getFeeAccountAmount()));
													durationTotal.set(durationTotal.get().add(account.getFeeAccountAmount()));
													yearTotal.set(yearTotal.get().add(account.getFeeAccountAmount()));
												}
												if(Utils.isNullOrEmpty(account.getFeeAccountAmount())) {
													if(Utils.isNullOrEmpty(accBatchFeeAccountDBO.getId())) {
														return;
													}
													accBatchFeeAccountDBO.setRecordStatus('D');	
													accBatchFeeAccountDBO.setModifiedUsersId(Integer.parseInt(userId));
												}
												else {
													accBatchFeeAccountDBO.setCreatedUsersId(Integer.parseInt(userId));
													accBatchFeeAccountDBO.setModifiedUsersId(Integer.parseInt(userId));
													accBatchFeeAccountDBO.setRecordStatus('A');
												}
												accBatchFeeAccountDBOSet.add(accBatchFeeAccountDBO);
											});
											accBatchFeeHeadDBO.setAccBatchFeeAccountDBOSet(accBatchFeeAccountDBOSet);
										}
										accBatchFeeHeadDBO.setCreatedUsersId(Integer.parseInt(userId));
										accBatchFeeHeadDBO.setModifiedUsersId(Integer.parseInt(userId));
										accBatchFeeHeadDBO.setRecordStatus('A');
										
										if(Utils.isNullOrEmpty(headwiseTotal.get())) {
											if(!Utils.isNullOrEmpty(accBatchFeeHeadDBO.getId())) {
												accBatchFeeHeadDBO.setRecordStatus('D');
												accBatchFeeHeadDBO.setModifiedUsersId(Integer.parseInt(userId));
												acBatchFeeHeadDBOSet.add(accBatchFeeHeadDBO);
											}
										}
										else {
											acBatchFeeHeadDBOSet.add(accBatchFeeHeadDBO);
										}
									}
								});
								accBatchFeeCategoryDBO.setAccBatchFeeHeadDBOSet(acBatchFeeHeadDBOSet);
							}
							accBatchFeeCategoryDBO.setCreatedUsersId(Integer.parseInt(userId));
							accBatchFeeCategoryDBO.setModifiedUsersId(Integer.parseInt(userId));
							accBatchFeeCategoryDBO.setRecordStatus('A');
							if(Utils.isNullOrEmpty(categoryTotal.get())) {
								if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO.getId())) {
									accBatchFeeCategoryDBO.setRecordStatus('D');
									accBatchFeeCategoryDBO.setModifiedUsersId(Integer.parseInt(userId));
									accBatchFeeCategoryDBOSet.add(accBatchFeeCategoryDBO);
								}
							}
							else {
								accBatchFeeCategoryDBOSet.add(accBatchFeeCategoryDBO);
							}
						});
						accBatchFeeDurationsDetailDBO.setAccBatchFeeCategoryDBOSet(accBatchFeeCategoryDBOSet);
					}
					accBatchFeeDurationsDetailDBO.setCreatedUsersId(Integer.parseInt(userId));
					accBatchFeeDurationsDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
					accBatchFeeDurationsDetailDBO.setRecordStatus('A');
					if(Utils.isNullOrEmpty(durationTotal.get())) {
						if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBO.getId())) {
							accBatchFeeDurationsDetailDBO.setRecordStatus('D');
							accBatchFeeDurationsDetailDBO.setModifiedUsersId(Integer.parseInt(userId));
							accBatchFeeDurationsDetailDBOSet.add(accBatchFeeDurationsDetailDBO);
						}
					}
					else {
						accBatchFeeDurationsDetailDBOSet.add(accBatchFeeDurationsDetailDBO);
					}
				});
				if(Utils.isNullOrEmpty(yearTotal.get())) {
					if(!Utils.isNullOrEmpty(accBatchFeeDurationsDBO.getId())) {
						accBatchFeeDurationsDBO.setRecordStatus('D');
						accBatchFeeDurationsDBO.setModifiedUsersId(Integer.parseInt(userId));
						accBatchFeeDurationsDBOSet.add(accBatchFeeDurationsDBO);
					}
				}
				else { 
					accBatchFeeDurationsDBO.setAccBatchFeeDurationsDetailDBOSet(accBatchFeeDurationsDetailDBOSet);
					accBatchFeeDurationsDBOSet.add(accBatchFeeDurationsDBO);
				}
			});
			accBatchFeeDBO.setAccBatchFeeDurationsDBOSet(accBatchFeeDurationsDBOSet);
			}
 		return accBatchFeeDBO;
	}
}
