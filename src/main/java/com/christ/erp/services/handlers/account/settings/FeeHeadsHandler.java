package com.christ.erp.services.handlers.account.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeGroupDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dto.account.AccFeeHeadsAccountDTO;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.account.settings.FeeHeadsTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeeHeadsHandler {
	@Autowired
	FeeHeadsTransaction feeHeadsTransaction;

	public Flux<AccFeeHeadsDTO> getGridData() {
		return feeHeadsTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertAccFeeHeadsDboToDto);
	}
	
	public AccFeeHeadsDTO convertAccFeeHeadsDboToDto(AccFeeHeadsDBO accFeeHeadsDBO) {
		AccFeeHeadsDTO accFeeHeadsDTO = new AccFeeHeadsDTO();
		BeanUtils.copyProperties(accFeeHeadsDBO, accFeeHeadsDTO);
		accFeeHeadsDTO.setHeading(accFeeHeadsDBO.getHeading());
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getHostelDBO())){
			accFeeHeadsDTO.setHeading(accFeeHeadsDBO.getHeading() + " (" + accFeeHeadsDBO.getHostelDBO().getHostelName() + ")");
		}
		SelectDTO feeHeadsTypeDTO = new SelectDTO();
		feeHeadsTypeDTO.setValue(accFeeHeadsDBO.getFeeHeadsType());
		feeHeadsTypeDTO.setLabel(accFeeHeadsDBO.getFeeHeadsType());
		
		accFeeHeadsDTO.setFeeHeadsTypeDTO(feeHeadsTypeDTO);
		return accFeeHeadsDTO;
		
	}
	
	public Flux<AccFeeHeadsAccountDTO> getCampusAndAccountNoDTO(){
		return feeHeadsTransaction.getCampusAndAccountNumberBoList().flatMapMany(Flux::fromIterable).map(this::convertAccAccountDboToAccFeeHeadsAccountDTO);
	}

	public AccFeeHeadsAccountDTO convertAccAccountDboToAccFeeHeadsAccountDTO(AccAccountsDBO accAccountsDBO) {
		AccFeeHeadsAccountDTO accFeeHeadsAccountDTO = new AccFeeHeadsAccountDTO();
		//campus setting
		SelectDTO erpCampusDTO = new SelectDTO();
		erpCampusDTO.setValue(Integer.toString(accAccountsDBO.getErpCampusDBO().getId()));
		erpCampusDTO.setLabel(accAccountsDBO.getErpCampusDBO().getCampusName());
		accFeeHeadsAccountDTO.setErpCampusDTO(erpCampusDTO);
		//account no setting
		SelectDTO accAccountsDTO = new SelectDTO();
		accAccountsDTO.setValue(Integer.toString(accAccountsDBO.getId()));
		accAccountsDTO.setLabel(accAccountsDBO.getAccountNo());
		accFeeHeadsAccountDTO.setAccAccountsDTO(accAccountsDTO);
		SelectDTO erpCurrencyDTO = new SelectDTO(); 
		accFeeHeadsAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
		return accFeeHeadsAccountDTO;
	}
	
	public Mono<ApiResult> saveOrUpdate(Mono<AccFeeHeadsDTO> dto, String userId){
		return dto
			.handle((accHeadsDto, synchronousink)->{
				boolean isDuplicated = false;
				if(!isDuplicated) {
					if(accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Admission Processing Fee")){
						AccFeeHeadsDBO accFeeHeadsDBO = feeHeadsTransaction.duplicateCheckForAdmProcessFee(accHeadsDto);
						if(accFeeHeadsDBO!=null) {
							synchronousink.error(new DuplicateException("Admission Processing Fee is already defined."));
							isDuplicated = true;
						}
					}
				}
				if(!isDuplicated) {
					if(!Utils.isNullOrEmpty(accHeadsDto.getAccFeeHeadsAccountDTOList())) {
				    	List<String> codeList = accHeadsDto.getAccFeeHeadsAccountDTOList().stream().filter(a->!Utils.isNullOrEmpty(a.getSapCode())).map(a->a.getSapCode()).collect(Collectors.toList());
				    	List<String> dupCodeArray = codeList.stream().filter(i -> Collections.frequency(codeList, i) >1).collect(Collectors.toList());	
				    	if(dupCodeArray.size() > 0) {
				    		synchronousink.error(new DuplicateException("Sap Code Duplicated"));
							isDuplicated = true;
				    	}
				    	if(!isDuplicated) {
							List<AccFeeHeadsAccountDBO> accFeeHeadsAccountDBOList = feeHeadsTransaction.duplicateCheckCode(codeList, accHeadsDto.getId());
							if(accFeeHeadsAccountDBOList!=null) {
								List<String> duplicatedCodeList = accFeeHeadsAccountDBOList.stream().map(a->a.getSapCode()).collect(Collectors.toList());
								synchronousink.error(new DuplicateException("Sap Code " +  String.join(", ", duplicatedCodeList) + " already exits"));
								isDuplicated = true;
							}
				    	}
					}
				}
				if(!isDuplicated) {
					AccFeeHeadsDBO accFeeHeadsDBO1 = feeHeadsTransaction.duplicateCheckWithTypeAndName(accHeadsDto);
					if(!Utils.isNullOrEmpty(accFeeHeadsDBO1)) {
						synchronousink.error(new DuplicateException("Heading " + accFeeHeadsDBO1.getHeading() +" already exists for the type " + accFeeHeadsDBO1.getFeeHeadsType()));
						isDuplicated = true;
					}
				}
				if(!isDuplicated && !Utils.isNullOrEmpty(accHeadsDto.getHostelDTO()) && !Utils.isNullOrEmpty(accHeadsDto.getHostelDTO().getValue()) 
						&& (accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Hostel Application Fee") 
								|| accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Hostel Maintenance Fee") 
								|| accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Hostel Caution Deposit"))) {
					boolean isTypeAndHostelDuplicated = feeHeadsTransaction.duplicateCheckTypeAndHostel(accHeadsDto);
					if(isTypeAndHostelDuplicated) {
						synchronousink.error(new DuplicateException(accHeadsDto.getFeeHeadsTypeDTO().getValue() + " is already defined for the hostel " + accHeadsDto.getHostelDTO().getLabel()));
						isDuplicated = true;
					}	
				}
				if (!isDuplicated && accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Hostel Fee") && !Utils.isNullOrEmpty(accHeadsDto.getHostelDTO())) {
					if(!Utils.isNullOrEmpty(accHeadsDto.getHostelRoomTypeDTO())) {
						AccFeeHeadsDBO accFeeHeadsDBOHostelAndRoom = feeHeadsTransaction.roomTypeAndHostelduplicateCheck(accHeadsDto);
						if(!Utils.isNullOrEmpty(accFeeHeadsDBOHostelAndRoom)) {
							synchronousink.error(new DuplicateException("Hostel Fee is already assigned for the Room Type " + accHeadsDto.getHostelRoomTypeDTO().getLabel() ));
							isDuplicated = true;
						}
					}
				}
				/*
				if(!isDuplicated && !Utils.isNullOrEmpty(accHeadsDto.getErpProgrammeDegreeDTO())	&& !Utils.isNullOrEmpty(accHeadsDto.getErpProgrammeDegreeDTO().value) 
						&& accHeadsDto.getFeeHeadsTypeDTO().getValue().equals("Application Fee")) {
					AccFeeHeadsDBO accFeeHeadsDBODegree = feeHeadsTransaction.applicationFeeAndDegreeDuplicateCheck(accHeadsDto);
					if(!Utils.isNullOrEmpty(accFeeHeadsDBODegree)) {
						synchronousink.error(new DuplicateException("Application Fee is already assigned for the Degree " + accHeadsDto.getErpProgrammeDegreeDTO().getLabel() ));
						isDuplicated = true;
					}
				}*/
				
			if(!isDuplicated) {
				synchronousink.next(accHeadsDto);
			}
			}).cast(AccFeeHeadsDTO.class).map(data->convertAccHeadsDtoToDbo(data, userId))
				.flatMap(accFeeHeadsDbo->{
					if(!Utils.isNullOrEmpty(accFeeHeadsDbo.getId())) {
						feeHeadsTransaction.update(accFeeHeadsDbo);
					}
					else {
						feeHeadsTransaction.save(accFeeHeadsDbo);	
					}
					return Mono.just(Boolean.TRUE);
			 }).map(Utils::responseResult);
	}
	
	public AccFeeHeadsDBO convertAccHeadsDtoToDbo(AccFeeHeadsDTO accFeeHeadsDTO, String userId) {
		AccFeeHeadsDBO accFeeHeadsDBO = !Utils.isNullOrEmpty(accFeeHeadsDTO.getId())?feeHeadsTransaction.getAccFeeHeadBoByIdForEdit(accFeeHeadsDTO.getId()):new AccFeeHeadsDBO();
		accFeeHeadsDBO.setFeeHeadsType(accFeeHeadsDTO.getFeeHeadsTypeDTO().getValue());
		accFeeHeadsDBO.setHeading(accFeeHeadsDTO.getHeading());
		accFeeHeadsDBO.setCgstApplicable(accFeeHeadsDTO.isCgstApplicable());
		accFeeHeadsDBO.setFixedAmount(accFeeHeadsDTO.isFixedAmount());
		accFeeHeadsDBO.setGstApplicable(accFeeHeadsDTO.isGstApplicable());
		accFeeHeadsDBO.setSgstApplicable(accFeeHeadsDTO.isSgstApplicable());
		accFeeHeadsDBO.setIgstApplicable(accFeeHeadsDTO.isIgstApplicable());
		
		//set account fee group to account fee heads
		AccFeeGroupDBO accFeeGroupDBO = null;
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getAccFeeGroupDTO()) && !Utils.isNullOrEmpty(accFeeHeadsDTO.getAccFeeGroupDTO().getValue())) {
			accFeeGroupDBO = new AccFeeGroupDBO();
			accFeeGroupDBO.setId(Integer.parseInt(accFeeHeadsDTO.getAccFeeGroupDTO().getValue()));
		}
		accFeeHeadsDBO.setAccFeeGroupDBO(accFeeGroupDBO);
		
		//setting HostelDBO
		HostelDBO hostelDBO = null;
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getHostelDTO()) && !Utils.isNullOrEmpty(accFeeHeadsDTO.getHostelDTO().getValue())) {
			hostelDBO = new HostelDBO();
			hostelDBO.setId(Integer.parseInt(accFeeHeadsDTO.getHostelDTO().getValue()));
		}
		accFeeHeadsDBO.setHostelDBO(hostelDBO);
		
		HostelRoomTypeDBO hostelRoomTypeDBO = null;
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getHostelRoomTypeDTO()) && !Utils.isNullOrEmpty(accFeeHeadsDTO.getHostelRoomTypeDTO().getValue())){
			hostelRoomTypeDBO = new HostelRoomTypeDBO();
			hostelRoomTypeDBO.setId(Integer.parseInt(accFeeHeadsDTO.getHostelRoomTypeDTO().getValue()));
		}
		accFeeHeadsDBO.setHostelRoomTypeDBO(hostelRoomTypeDBO);
		
		accFeeHeadsDBO.setRecordStatus('A');
		accFeeHeadsDBO.setCreatedUsersId(Integer.parseInt(userId));
		accFeeHeadsDBO.setModifiedUsersId(Integer.parseInt(userId));
		
		Map<Integer, AccFeeHeadsAccountDBO> accFeeHeadsAccountDBOMap = !Utils.isNullOrEmpty(accFeeHeadsDBO.getAccFeeHeadsAccountList())?	
				accFeeHeadsDBO.getAccFeeHeadsAccountList().stream().collect(Collectors.toMap(accHeadAccDBO->accHeadAccDBO.getId(), accHeadAccDBO->accHeadAccDBO)):new HashMap<>();
		
		if(!Utils.isNullOrEmpty(accFeeHeadsDTO.getAccFeeHeadsAccountDTOList())) {
			Set<AccFeeHeadsAccountDBO> accFeeHeadsAccountDBOSet = new HashSet<AccFeeHeadsAccountDBO>();
			accFeeHeadsDTO.getAccFeeHeadsAccountDTOList().forEach(accFeeHeadsAccountDTO->{
				AccFeeHeadsAccountDBO accFeeHeadsAccountDBO = new AccFeeHeadsAccountDBO();
				if(accFeeHeadsAccountDBOMap.containsKey(accFeeHeadsAccountDTO.getId())) {
					accFeeHeadsAccountDBO = accFeeHeadsAccountDBOMap.get(accFeeHeadsAccountDTO.getId());
				}
				accFeeHeadsAccountDBO.setAccFeeHeadsDBO(accFeeHeadsDBO);
				accFeeHeadsAccountDBO.setCreatedUsersId(Integer.parseInt(userId));
				accFeeHeadsAccountDBO.setModifiedUsersId(Integer.parseInt(userId));
				accFeeHeadsAccountDBO.setRecordStatus('A');
				if(Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getAmount()) && Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getAmountInterNational())) {
					if(Utils.isNullOrEmpty(accFeeHeadsAccountDBO.getId())) {
						return;
					}
					accFeeHeadsAccountDBO.setRecordStatus('D');
				}
				accFeeHeadsAccountDBO.setSapCode(accFeeHeadsAccountDTO.getSapCode());
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getAccAccountsDTO().getValue())) {
					AccAccountsDBO accAccountsDBO = new AccAccountsDBO();
					accAccountsDBO.setId(Integer.parseInt(accFeeHeadsAccountDTO.getAccAccountsDTO().getValue()));
					accFeeHeadsAccountDBO.setAccAccountsDBO(accAccountsDBO);
				}
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getErpCampusDTO()) && !Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getErpCampusDTO().getValue())) {
					ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
					erpCampusDBO.setId(Integer.parseInt(accFeeHeadsAccountDTO.getErpCampusDTO().getValue()));
					accFeeHeadsAccountDBO.setErpCampusDBO(erpCampusDBO);
				}
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getErpCurrencyDTO()) && !Utils.isNullOrEmpty(accFeeHeadsAccountDTO.getErpCurrencyDTO().getValue())) {
					ErpCurrencyDBO erpCurrencyDBO = new ErpCurrencyDBO();
					erpCurrencyDBO.setId(Integer.parseInt(accFeeHeadsAccountDTO.getErpCurrencyDTO().getValue()));
					accFeeHeadsAccountDBO.setErpCurrencyDBO(erpCurrencyDBO);
				}
				accFeeHeadsAccountDBO.setSapCode(accFeeHeadsAccountDTO.getSapCode());
				accFeeHeadsAccountDBO.setAmount(accFeeHeadsAccountDTO.getAmount());
				accFeeHeadsAccountDBO.setAmountInternational(accFeeHeadsAccountDTO.getAmountInterNational());
				accFeeHeadsAccountDBOSet.add(accFeeHeadsAccountDBO);

			});
			accFeeHeadsDBO.setAccFeeHeadsAccountList(accFeeHeadsAccountDBOSet);
		}
		
		return accFeeHeadsDBO;
	}

	public Mono<AccFeeHeadsDTO> edit(int id) {
		AccFeeHeadsDBO accFeeHeadsDBO = feeHeadsTransaction.getAccFeeHeadBoByIdForEdit(id);
		return this.convertAccFeeHeadsDboToDtoForEdit(accFeeHeadsDBO);
    }	
	
	public Mono<AccFeeHeadsDTO> convertAccFeeHeadsDboToDtoForEdit(AccFeeHeadsDBO accFeeHeadsDBO) {
		AccFeeHeadsDTO accFeeHeadsDTO = new AccFeeHeadsDTO();
		BeanUtils.copyProperties(accFeeHeadsDBO, accFeeHeadsDTO);
		//setting fee group
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getFeeHeadsType())) {
			SelectDTO feeHeadsTypeDto = new SelectDTO();
			feeHeadsTypeDto.setLabel(accFeeHeadsDBO.getFeeHeadsType());
			feeHeadsTypeDto.setValue(accFeeHeadsDBO.getFeeHeadsType());
			accFeeHeadsDTO.setFeeHeadsTypeDTO(feeHeadsTypeDto);
		}
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getAccFeeGroupDBO())) {
			SelectDTO accFeeGroupDTO = new SelectDTO();
			accFeeGroupDTO.setValue(Integer.toString(accFeeHeadsDBO.getAccFeeGroupDBO().getId()));
			accFeeGroupDTO.setLabel(accFeeHeadsDBO.getAccFeeGroupDBO().getGroupName());
			accFeeHeadsDTO.setAccFeeGroupDTO(accFeeGroupDTO);
		}
		
		//set hostel
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getHostelDBO())) {
			SelectDTO hostelDto = new SelectDTO();
			hostelDto.setValue(Integer.toString(accFeeHeadsDBO.getHostelDBO().getId()));
			hostelDto.setLabel(accFeeHeadsDBO.getHostelDBO().getHostelName());
			accFeeHeadsDTO.setHostelDTO(hostelDto);
		}
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getHostelRoomTypeDBO())) {
			SelectDTO hostelRoomTypeDto = new SelectDTO();
			hostelRoomTypeDto.setValue(Integer.toString(accFeeHeadsDBO.getHostelRoomTypeDBO().getId()));
			hostelRoomTypeDto.setLabel(accFeeHeadsDBO.getHostelRoomTypeDBO().getRoomType());
			accFeeHeadsDTO.setHostelRoomTypeDTO(hostelRoomTypeDto);
		}
		
		List<AccAccountsDBO> accMasterBoList = new ArrayList<AccAccountsDBO>();
		if(accFeeHeadsDBO.getFeeHeadsType().equals("Cash Collection") 
				|| accFeeHeadsDBO.getFeeHeadsType().equals("Additional Fee") 
				|| accFeeHeadsDBO.getFeeHeadsType().equals("Application Fee")
				|| accFeeHeadsDBO.getFeeHeadsType().equals("Admission Processing Fee")) {
			accMasterBoList = feeHeadsTransaction.getAccountNumberAndCampusListForEdit();
		}
		
		Set<Integer> campusIdList = new HashSet<Integer>();
		List<AccFeeHeadsAccountDTO> accFeeHeadsAccountDTOList = new ArrayList<AccFeeHeadsAccountDTO>();
		if(!Utils.isNullOrEmpty(accFeeHeadsDBO.getAccFeeHeadsAccountList())) {
			accFeeHeadsDBO.getAccFeeHeadsAccountList().stream().filter(accFeeHeadsAccountDbo->accFeeHeadsAccountDbo.recordStatus=='A').forEach(accFeeHeadsAccountDbo->{
				AccFeeHeadsAccountDTO accFeeHeadsAccountDTO = new AccFeeHeadsAccountDTO();
				accFeeHeadsAccountDTO.setAccFeeHeadsId(accFeeHeadsDBO.getId()); //parent Id
				accFeeHeadsAccountDTO.setId(accFeeHeadsAccountDbo.getId());
				accFeeHeadsAccountDTO.setSapCode(accFeeHeadsAccountDbo.getSapCode());
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDbo.getAmount())) {
					accFeeHeadsAccountDTO.setAmount(accFeeHeadsAccountDbo.getAmount());
				}
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDbo.getAmountInternational())) {
					accFeeHeadsAccountDTO.setAmountInterNational(accFeeHeadsAccountDbo.getAmountInternational());
				}
				//campus setting
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDbo.getErpCampusDBO())) {
					SelectDTO erpCampusDTO = new SelectDTO();
					erpCampusDTO.setValue(Integer.toString(accFeeHeadsAccountDbo.getErpCampusDBO().getId()));
					erpCampusDTO.setLabel(accFeeHeadsAccountDbo.getErpCampusDBO().getCampusName());
					accFeeHeadsAccountDTO.setErpCampusDTO(erpCampusDTO);
					campusIdList.add(accFeeHeadsAccountDbo.getErpCampusDBO().getId());
				}
				//account no setting
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDbo.getAccAccountsDBO())) {
					SelectDTO accAccountsDTO = new SelectDTO();
					accAccountsDTO.setValue(Integer.toString(accFeeHeadsAccountDbo.getAccAccountsDBO().getId()));
					accAccountsDTO.setLabel(accFeeHeadsAccountDbo.getAccAccountsDBO().getAccountNo());
					accFeeHeadsAccountDTO.setAccAccountsDTO(accAccountsDTO);
				}
				if(!Utils.isNullOrEmpty(accFeeHeadsAccountDbo.getErpCurrencyDBO())) {
					SelectDTO erpCurrencyDTO = new SelectDTO();
					erpCurrencyDTO.setValue(Integer.toString(accFeeHeadsAccountDbo.getErpCurrencyDBO().getId()));
					accFeeHeadsAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
				}
				accFeeHeadsAccountDTOList.add(accFeeHeadsAccountDTO);
			});
		}
		//newly added campus with account
		if(!Utils.isNullOrEmpty(accMasterBoList)) {
			accMasterBoList.forEach(accAccountsBo->{
				if(!campusIdList.contains(accAccountsBo.getErpCampusDBO().getId())) {
					AccFeeHeadsAccountDTO accFeeHeadsAccountDTO = new AccFeeHeadsAccountDTO();	
					accFeeHeadsAccountDTO.setAccFeeHeadsId(accFeeHeadsDBO.getId()); //parent Id
					//new campus
					if(!Utils.isNullOrEmpty(accAccountsBo.getErpCampusDBO())) {
						SelectDTO erpCampusDTO = new SelectDTO();
						erpCampusDTO.setValue(Integer.toString(accAccountsBo.getErpCampusDBO().getId()));
						erpCampusDTO.setLabel(accAccountsBo.getErpCampusDBO().getCampusName());
						accFeeHeadsAccountDTO.setErpCampusDTO(erpCampusDTO);
					}
					//account no setting
					if(!Utils.isNullOrEmpty(accAccountsBo)) {
						SelectDTO accAccountsDTO = new SelectDTO();
						accAccountsDTO.setValue(Integer.toString(accAccountsBo.getId()));
						accAccountsDTO.setLabel(accAccountsBo.getAccountNo());
						accFeeHeadsAccountDTO.setAccAccountsDTO(accAccountsDTO);
					}
					accFeeHeadsAccountDTOList.add(accFeeHeadsAccountDTO);
				}
			});
		}
		List<AccFeeHeadsAccountDTO> accFeeHeadsAccountDTOListSorted = new ArrayList<AccFeeHeadsAccountDTO>();
		if(!Utils.isNullOrEmpty(accFeeHeadsAccountDTOList)) {
			if(accFeeHeadsDBO.getFeeHeadsType().equals("Cash Collection") 
					|| accFeeHeadsDBO.getFeeHeadsType().equals("Additional Fee") 
					|| accFeeHeadsDBO.getFeeHeadsType().equals("Application Fee")
					|| accFeeHeadsDBO.getFeeHeadsType().equals("Admission Processing Fee")) {
				accFeeHeadsAccountDTOListSorted = accFeeHeadsAccountDTOList.stream().sorted(Comparator.comparing(o -> o.getErpCampusDTO().getLabel())).collect(Collectors.toList());
			}
			else {
				accFeeHeadsAccountDTOListSorted = accFeeHeadsAccountDTOList;
			}
		}
		accFeeHeadsDTO.setAccFeeHeadsAccountDTOList(accFeeHeadsAccountDTOListSorted);
		return Mono.just(accFeeHeadsDTO);
	}
	
	public Mono<ApiResult> delete(int id, String userId) {
		return feeHeadsTransaction.delete(id, userId).map(Utils::responseResult);
	}
	
	public Flux<SelectDTO> getFeeGroup() {
		return feeHeadsTransaction.getFeeGroupData().flatMapMany(Flux::fromIterable).map(this::convertFeeGroupDboToDto);
	}
	
	public SelectDTO convertFeeGroupDboToDto(AccFeeGroupDBO feeGroupDBO) {
		SelectDTO feeGroupDto = new SelectDTO();
		if(!Utils.isNullOrEmpty(feeGroupDBO.getId()) && !Utils.isNullOrEmpty(feeGroupDBO.getGroupName())){
			feeGroupDto.setValue(Integer.toString(feeGroupDBO.getId()));
			feeGroupDto.setLabel(feeGroupDBO.getGroupName());
		}
		return feeGroupDto;
		
	}
	
	public Flux<SelectDTO> getAccAccountsByHostel(int hostelId) {
		return feeHeadsTransaction.getAccAccountsByHostel(hostelId).flatMapMany(Flux::fromIterable).map(this::convertHostelDboToAccountsDto);
	}
	
	public SelectDTO convertHostelDboToAccountsDto(AccAccountsDBO accAccountsDBO) {
		SelectDTO accAccountsDTO = new SelectDTO();
		if(!Utils.isNullOrEmpty(accAccountsDBO) && !Utils.isNullOrEmpty(accAccountsDBO.getId())) {
			accAccountsDTO.setValue(Integer.toString(accAccountsDBO.getId()));
			accAccountsDTO.setLabel(accAccountsDBO.getAccountNo());
		}
		return accAccountsDTO;
		
	}
}
