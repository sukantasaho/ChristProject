package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.common.ErpCampusDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.ErpLocationDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.transactions.admission.settings.ApplicationFeeAccountSettingsTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApplicationFeeAccountSettingsHandler {

	@Autowired
	private ApplicationFeeAccountSettingsTransaction applicationFeeAccountSettingsTransaction;

	public Flux<ErpCampusProgrammeMappingDTO> getGridData() {
		List<Tuple> list = applicationFeeAccountSettingsTransaction.getGridData();
		return this.convertDboToDto(list); 
	}

	private Flux<ErpCampusProgrammeMappingDTO> convertDboToDto(List<Tuple> list) {
		List<ErpCampusProgrammeMappingDTO> erpCampusProgrammeMappingDTOList = new ArrayList<ErpCampusProgrammeMappingDTO>();
		Flux<ErpCampusProgrammeMappingDTO> erpCampusProgrammeMappingDTOFlux = Flux.fromIterable(erpCampusProgrammeMappingDTOList);	
		Map<Integer,ErpCampusProgrammeMappingDTO> erpCampusProgrammeMappingDTOMap = new HashMap<Integer, ErpCampusProgrammeMappingDTO>();
		Map<Integer,ErpCampusProgrammeMappingDTO> erpCampusProgrammeMappingDTOMap1 = new HashMap<Integer, ErpCampusProgrammeMappingDTO>();
		Map<Integer,Integer> acc = new HashMap<Integer, Integer>();
		List<SelectDTO> account = new ArrayList<SelectDTO>();
		list.forEach(dbo -> {
			ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO = new ErpCampusProgrammeMappingDTO();
			erpCampusProgrammeMappingDTO.setId(Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString()));
			if((!Utils.isNullOrEmpty(dbo.get("erp_location_id"))) &(Utils.isNullOrEmpty(dbo.get("erp_campus_id")))) {
				Map<Integer,List<SelectDTO>> accMap = new HashMap<Integer, List<SelectDTO>>();
				if(!erpCampusProgrammeMappingDTOMap1.containsKey(Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString()))) {
					erpCampusProgrammeMappingDTO.setErpProgrammeDTO(new ErpProgrammeDTO());
					erpCampusProgrammeMappingDTO.getErpProgrammeDTO().setId(Integer.parseInt(dbo.get("erp_programme_id").toString()));
					erpCampusProgrammeMappingDTO.getErpProgrammeDTO().setProgrammeName(dbo.get("programme_name").toString() + " (" + dbo.get("programme_code").toString()+")");
					erpCampusProgrammeMappingDTO.setErpLocationDTO(new ErpLocationDTO());
					erpCampusProgrammeMappingDTO.getErpLocationDTO().setLocationName(dbo.get("location_name").toString());
					erpCampusProgrammeMappingDTO.getErpLocationDTO().setId(Integer.parseInt(dbo.get("erp_location_id").toString()));
					erpCampusProgrammeMappingDTO.setCampusOrLocation(dbo.get("location_name").toString());
					if(!acc.containsKey(Integer.parseInt(dbo.get("acc_accounts_id").toString()))) {
						SelectDTO acclist = new SelectDTO();
						acclist.setValue(dbo.get("acc_accounts_id").toString());
						acclist.setLabel(dbo.get("account_no").toString());
						account.add(acclist);
						erpCampusProgrammeMappingDTO.setAccountNoList(account);
//						account.clear();
						Map<Integer,Integer> acc1 = new HashMap<Integer, Integer>();
						acc.put(Integer.parseInt(dbo.get("acc_accounts_id").toString()), Integer.parseInt(dbo.get("erp_location_id").toString()));
					}
					if(!Utils.isNullOrEmpty(dbo.get("erp_campus_programme_mapping_acc_account_id"))) {
						SelectDTO acclist1 = new SelectDTO();
						acclist1.setValue(dbo.get("erp_campus_programme_mapping_acc_account_id").toString());
						acclist1.setLabel(dbo.get("erp_campus_programme_mapping_account_no").toString());
						erpCampusProgrammeMappingDTO.setAccountNoSelect(acclist1);
					}
					erpCampusProgrammeMappingDTOMap1.put(Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString()), erpCampusProgrammeMappingDTO);						
					erpCampusProgrammeMappingDTOList.add(erpCampusProgrammeMappingDTO);				
				}else {
					ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO1 = erpCampusProgrammeMappingDTOMap1.get(Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString()));
					if(erpCampusProgrammeMappingDTO1.getId() == Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString())) {
						if(!acc.containsKey(Integer.parseInt(dbo.get("acc_accounts_id").toString()))) {
							SelectDTO acclist = new SelectDTO();
							acclist.setValue(dbo.get("acc_accounts_id").toString());
							acclist.setLabel(dbo.get("account_no").toString());
							account.add(acclist);
							acc.put(Integer.parseInt(dbo.get("acc_accounts_id").toString()), Integer.parseInt(dbo.get("erp_location_id").toString()));
							erpCampusProgrammeMappingDTO1.setAccountNoList(account);	
						}
					}
				}
			}
			if((Utils.isNullOrEmpty(dbo.get("erp_location_id"))) &(!Utils.isNullOrEmpty(dbo.get("erp_campus_id")))) {
				if(!erpCampusProgrammeMappingDTOMap.containsKey(Integer.parseInt(dbo.get("erp_campus_programme_mapping_id").toString()))) {
					erpCampusProgrammeMappingDTO.setErpProgrammeDTO(new ErpProgrammeDTO());
					if(!Utils.isNullOrEmpty(dbo.get("erp_programme_id"))) {
						erpCampusProgrammeMappingDTO.getErpProgrammeDTO().setId(Integer.parseInt(dbo.get("erp_programme_id").toString()));
						erpCampusProgrammeMappingDTO.getErpProgrammeDTO().setProgrammeName(dbo.get("programme_name").toString() + " (" + dbo.get("programme_code").toString() +")");
					}
					erpCampusProgrammeMappingDTO.setCampusDTO(new ErpCampusDTO());
					erpCampusProgrammeMappingDTO.getCampusDTO().setCampusName((dbo.get("campus_name").toString()));
					erpCampusProgrammeMappingDTO.getCampusDTO().setId(dbo.get("erp_campus_id").toString());
					erpCampusProgrammeMappingDTO.setCampusOrLocation(dbo.get("campus_name").toString());
					erpCampusProgrammeMappingDTO.setAccountNoList(new ArrayList<SelectDTO>());
					SelectDTO acclist = new SelectDTO();
					acclist.setValue(dbo.get("acc_accounts_id").toString());
					acclist.setLabel(dbo.get("account_no").toString());
					erpCampusProgrammeMappingDTO.getAccountNoList().add(acclist);
					if(!Utils.isNullOrEmpty(dbo.get("erp_campus_programme_mapping_acc_account_id"))) {
						acclist.setValue(dbo.get("erp_campus_programme_mapping_acc_account_id").toString());
						acclist.setLabel(dbo.get("erp_campus_programme_mapping_account_no").toString());
						erpCampusProgrammeMappingDTO.setAccountNoSelect(acclist);
					}
					if(!Utils.isNullOrEmpty(dbo.get("erp_programme_id"))) {
						erpCampusProgrammeMappingDTOMap.put(Integer.parseInt(dbo.get("erp_programme_id").toString()), erpCampusProgrammeMappingDTO);
					}
					erpCampusProgrammeMappingDTOList.add(erpCampusProgrammeMappingDTO);
				}else {
					ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO1 = erpCampusProgrammeMappingDTOMap.get(Integer.parseInt(dbo.get("erp_programme_id").toString()));
					SelectDTO acclist = new SelectDTO();
					acclist.setValue(dbo.get("acc_accounts_id").toString());
					acclist.setLabel(dbo.get("account_no").toString());
					if(!Utils.isNullOrEmpty(erpCampusProgrammeMappingDTO1)) {
						erpCampusProgrammeMappingDTO1.setAccountNoList(new ArrayList<SelectDTO>());
						erpCampusProgrammeMappingDTO1.getAccountNoList().add(acclist);
						erpCampusProgrammeMappingDTOMap.replace(Integer.parseInt(dbo.get("erp_programme_id").toString()), erpCampusProgrammeMappingDTO1);
					}
				}
			}
			if(!Utils.isNullOrEmpty(account)) {
				erpCampusProgrammeMappingDTO.setAccountNoList(account);
			}
		});
		return erpCampusProgrammeMappingDTOFlux;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> update(Mono<List<ErpCampusProgrammeMappingDTO>> dto, String userId) {
		return dto.map(data -> convertDtoDbo(data,userId))
				.flatMap( s -> { applicationFeeAccountSettingsTransaction.merge(s);
				return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<ErpCampusProgrammeMappingDBO> convertDtoDbo(List<ErpCampusProgrammeMappingDTO> dto, String userId) {
		List<Integer> erpCampusProgrammeMappingIds = new ArrayList<Integer>();
		dto.forEach(dtoIds -> {
			erpCampusProgrammeMappingIds.add(dtoIds.getId());
		});
		List<ErpCampusProgrammeMappingDBO> erpCampusProgrammeMappingDBOList = applicationFeeAccountSettingsTransaction.getData(erpCampusProgrammeMappingIds);
		Map<Integer,ErpCampusProgrammeMappingDBO> erpCampusProgrammeMappingDBOMap = new HashMap<Integer, ErpCampusProgrammeMappingDBO>();
		erpCampusProgrammeMappingDBOList.forEach(exist -> {
			erpCampusProgrammeMappingDBOMap.put(exist.getId(), exist);
		});
		dto.forEach(erpCampusProgrammeDtos -> {
			if(erpCampusProgrammeMappingDBOMap.containsKey(erpCampusProgrammeDtos.getId())) {
				ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO = erpCampusProgrammeMappingDBOMap.get(erpCampusProgrammeDtos.getId());
//				if(Utils.isNullOrEmpty(erpCampusProgrammeDtos.getAccountNoSelect())) { // sambath
//					erpCampusProgrammeMappingDBO.setAccAccountsDBO(null);
//				}else {
//					AccAccountsDBO accAccountsDBO = new AccAccountsDBO();
//					accAccountsDBO.setId(Integer.parseInt(erpCampusProgrammeDtos.getAccountNoSelect().getValue()));
//					erpCampusProgrammeMappingDBO.setAccAccountsDBO(accAccountsDBO);
//				}
				erpCampusProgrammeMappingDBO.setModifiedUsersId(Integer.parseInt(userId));
			}
		});
		return erpCampusProgrammeMappingDBOList;
	}
}