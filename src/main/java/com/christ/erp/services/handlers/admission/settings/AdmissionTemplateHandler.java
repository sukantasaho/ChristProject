package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.admission.settings.ErpAdmissionTemplateDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.transactions.admission.settings.AdmissionTemplateTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdmissionTemplateHandler {

	@Autowired
	AdmissionTemplateTransaction admissionTemplateTransaction;

	public Flux<ErpAdmissionTemplateDTO> getGridList() {
		return admissionTemplateTransaction.getGridList().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public ErpAdmissionTemplateDTO convertDBOToDTO(Tuple dbos) {
		ErpAdmissionTemplateDTO dto = new ErpAdmissionTemplateDTO();
		
		//dto.setId(!Utils.isNullOrEmpty(dbos.get("grp_id")) ? Integer.parseInt(dbos.get("grp_id").toString()) : 0);
		dto.setTemplateGroupName(!Utils.isNullOrEmpty(dbos.get("grp_name")) ? dbos.get("grp_name").toString() : "");
		dto.setTemplateGroupCode(!Utils.isNullOrEmpty(dbos.get("grp_code")) ? dbos.get("grp_code").toString() : "");
		dto.setId(!Utils.isNullOrEmpty(dbos.get("template_id")) ? Integer.parseInt(dbos.get("template_id").toString()) : 0);
		dto.setTemplateIdForSms(!Utils.isNullOrEmpty(dbos.get("templateId")) ? String.valueOf(dbos.get("templateId")) : null);
		
		dto.setTemplateName(!Utils.isNullOrEmpty(dbos.get("template_name")) ? dbos.get("template_name").toString() : "");
		dto.setTemplateCode(Utils.isNullOrEmpty(dbos.get("template_code")) ? dbos.get("template_code").toString() : "");
		
		dto.setTemplateType(new SelectDTO());
		dto.getTemplateType().setLabel(!Utils.isNullOrEmpty(dbos.get("grp_name")) ? dbos.get("grp_name").toString() : "");
		dto.getTemplateType().setValue(!Utils.isNullOrEmpty(dbos.get("grp_name")) ? dbos.get("grp_name").toString() : "");
		
		dto.setTemplateFor(new SelectDTO());
		dto.getTemplateFor().setLabel(!Utils.isNullOrEmpty(dbos.get("type")) ? dbos.get("type").toString() : "");
		dto.getTemplateFor().setValue(!Utils.isNullOrEmpty(dbos.get("type")) ? dbos.get("type").toString() : "");
		
		dto.setCampus(new SelectDTO()); 
		dto.getCampus().setLabel(!Utils.isNullOrEmpty(dbos.get("campus")) ? dbos.get("campus").toString() : "");
		dto.getCampus().setValue(!Utils.isNullOrEmpty(dbos.get("campus_id")) ? dbos.get("campus_id").toString() : "");
		
		dto.setProgramGrid(new SelectDTO());
		dto.getProgramGrid().setLabel(!Utils.isNullOrEmpty(dbos.get("program_name")) ? dbos.get("program_name").toString() : "");
		
		dto.setTemplateDescription(!Utils.isNullOrEmpty(dbos.get("description")) ? dbos.get("description").toString() : "");
		
		
//		dto.setId(dbos.getId());
//		dto.setTemplateGroupName(dbos.getTemplateGroupName());
//		dto.setTemplateGroupCode(dbos.getTemplateGroupCode());
//		dto.setErpTemplateDTOSet(new ArrayList<ErpTemplateDTO>());
//		if (!Utils.isNullOrEmpty(dbos.getErpTemplateDBOSet())) {
//			dbos.getErpTemplateDBOSet().forEach(templateDBO -> {
//				ErpTemplateDTO tempDTO = new ErpTemplateDTO();
//				tempDTO.setTemplateCode(templateDBO.getTemplateCode());
//				// tempDTO.setTemplateContent(templateDBO.getTemplateContent());
//				tempDTO.setAvailableTags(templateDBO.getAvailableTags());
//				tempDTO.setProgramName(
//						templateDBO.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
//				dto.getErpTemplateDTOSet().add(tempDTO);
//			});
//		}
		return dto;
	}

	public Mono<ApiResult> saveOrUpdate(Mono<ErpAdmissionTemplateDTO> dto, String userId) {
		return dto.handle((erpTemplateGroupDTO, synchronousSink) -> {
		//	if(!Utils.isNullOrEmpty(erpTemplateGroupDTO.getTemplateId())) {
				List<ErpTemplateDBO> templates = admissionTemplateTransaction.duplicateCheck(erpTemplateGroupDTO);
//				if (templates.size() > 0) {
//					
//				}
//				List<String> programme = new ArrayList<String>();
//				if (templates.size() > 0) {
//					if (!erpTemplateGroupDTO.isVerified()) {
//						templates.forEach(s -> programme.add(s.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeCode()));
//						synchronousSink.error(new DuplicateException("Template already exists for the below programmes " + programme));
//					} else {
//						synchronousSink.next(erpTemplateGroupDTO);
//					}
//				} else {
					synchronousSink.next(erpTemplateGroupDTO);
//				}
//			}
//			else {
//				synchronousSink.next(erpTemplateGroupDTO);
//			}

		}).cast(ErpAdmissionTemplateDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
			if (!Utils.isNullOrEmpty(s.getId())) {
				admissionTemplateTransaction.update(s);
			} else {
				admissionTemplateTransaction.save(s);
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}
	
	
//	public Mono<ApiResult> saveOrUpdate(Mono<ErpAdmissionTemplateDTO> dto, String userId) {
//		return dto.handle((erpTemplateGroupDTO, synchronousSink) -> {
//			if(!Utils.isNullOrEmpty(erpTemplateGroupDTO.getTemplateId())) {
//				
//			}else {
//				List<ErpTemplateDBO> templates = admissionTemplateTransaction.dataCheck(erpTemplateGroupDTO);
//			}
//		}).cast(ErpAdmissionTemplateDTO.class).map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
//			if (!Utils.isNullOrEmpty(s.getId())) {
//			admissionTemplateTransaction.update(s);
//		} else {
//			admissionTemplateTransaction.save(s);
//		}
//		return Mono.just(Boolean.TRUE);
//	}).map(Utils::responseResult);
//	}

	public ErpTemplateGroupDBO convertDtoToDbo(ErpAdmissionTemplateDTO dto, String userId) {
		Map<Integer,ErpTemplateDBO> existDataMap = new HashMap<Integer, ErpTemplateDBO>();
		Map<Integer, String> erpCampusProgrammCodeMap = admissionTemplateTransaction.getErpCampusProgrammingId()
				.stream().filter(s ->  !Utils.isNullOrEmpty(s.getErpProgrammeDBO()) && !Utils.isNullOrEmpty(s.getErpProgrammeDBO().getProgrammeCode()) && !Utils.isNullOrEmpty(s.getErpCampusDBO()))
				.collect(Collectors.toMap(s -> s.getId(), s -> s.getErpProgrammeDBO().getProgrammeCode().toUpperCase()
						+ "_" + s.getErpCampusDBO().getShortName().toUpperCase()));
		ErpTemplateGroupDBO dBO = getErpTemplateGroupDBO(dto);		
		if(Utils.isNullOrEmpty(dBO)) {
			dBO.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			dBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		dBO.setTemplateGroupName(dto.getTemplateGroupName());
		dBO.setTemplateGroupCode(dto.getTemplateGroupCode());
		dBO.setTemplatePurpose("Admission");
		dBO.setRecordStatus('A');
		
		Set<ErpTemplateDBO> erpTemplateDBOSet = !Utils.isNullOrEmpty(dBO.getErpTemplateDBOSet()) ? dBO.getErpTemplateDBOSet() : null;
		if(!Utils.isNullOrEmpty(erpTemplateDBOSet)) {
			erpTemplateDBOSet.forEach(data -> {
				if(data.getRecordStatus() == 'A') {
					existDataMap.put(data.getErpCampusProgrammeMappingDBO().getId(), data);
				}
			});
		}
		
		Set<ErpTemplateDBO> templateDBOSet = new HashSet<ErpTemplateDBO>();
		dto.getProgramSelectedForTemplate().forEach(templateDTO -> {
			
			ErpTemplateDBO templateDBO = null;
			if(!existDataMap.containsKey(Integer.parseInt(templateDTO.getValue()))){
					 templateDBO = new ErpTemplateDBO();
					 getTemplateDBO(dto,templateDTO,templateDBO,erpCampusProgrammCodeMap,userId,dBO);
			}else {
				templateDBO  = existDataMap.get(Integer.parseInt(templateDTO.getValue()));
				//if(templateDBO.getId() == dto.getTemplateId()) {
					getTemplateDBO(dto,templateDTO,templateDBO,erpCampusProgrammCodeMap,userId,dBO);
			//	}
			}
			templateDBOSet.add(templateDBO);
		});
		dBO.setErpTemplateDBOSet(templateDBOSet);
		return dBO;
	}

	private void getTemplateDBO(ErpAdmissionTemplateDTO dto, SelectDTO templateDTO, ErpTemplateDBO templateDBO, Map<Integer, String> erpCampusProgrammCodeMap, String userId, ErpTemplateGroupDBO dBO) {
		templateDBO.setTemplateName(dto.getTemplateGroupName());
		templateDBO.setTemplateCode(dto.getTemplateGroupCode() + "_"
				+ erpCampusProgrammCodeMap.get(Integer.parseInt(templateDTO.getValue())).toUpperCase() + "_" + dto.getTemplateFor().getLabel().toUpperCase());
		templateDBO.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
		templateDBO.getErpCampusProgrammeMappingDBO().setId(Integer.parseInt(templateDTO.getValue()));
		templateDBO.setTemplateType(dto.getTemplateFor().getLabel());
		templateDBO.setTemplateContent(dto.getTemplateContent());
		templateDBO.setTemplateDescription(dto.getTemplateDescription());
		templateDBO.setAvailableTags(dto.getAvailableTags());
		templateDBO.setRecordStatus('A');
		if(Utils.isNullOrEmpty(dBO)) {
			templateDBO.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			templateDBO.setModifiedUsersId(Integer.parseInt(userId));
		}
		templateDBO.setErpTemplateGroupDBO(dBO);
		if (templateDBO.getTemplateType().equals("Mail")) {
			templateDBO.setMailFromName(dto.getFromName());
			templateDBO.setMailSubject(dto.getMailSubject());
		}else {
			templateDBO.setMailFromName(null);
			templateDBO.setMailSubject(null);
		}
		if (templateDBO.getTemplateType().equals("SMS")) {
			if(!Utils.isNullOrEmpty(dto.getTemplateIdForSms())){
				templateDBO.setTemplateId(dto.getTemplateIdForSms());
			}
		}else {
			templateDBO.setTemplateId(null);
		}
}

	private ErpTemplateGroupDBO getErpTemplateGroupDBO(ErpAdmissionTemplateDTO dto) {
		ErpTemplateGroupDBO dBO = null;
		ErpTemplateDBO dbo = null;
		//if (Utils.isNullOrEmpty(dto.getId())) {
			dBO = admissionTemplateTransaction.getGroupTemplateObj(dto);
			if(Utils.isNullOrEmpty(dBO)) {				
			dBO = new ErpTemplateGroupDBO();
			}
//		 else {
//			dBO = admissionTemplateTransaction.editObj(dto.getId());
//		}
		return dBO;
	}

	public Mono<ErpAdmissionTemplateDTO> edit(int id) {
		return admissionTemplateTransaction.edit(id).map(this::convertDboToDto);
	}

	public ErpAdmissionTemplateDTO convertDboToDto(ErpTemplateGroupDBO dbo) {
		ErpAdmissionTemplateDTO dTO = new ErpAdmissionTemplateDTO();
		dTO.setId(dbo.getId());
		dTO.setTemplateType(new SelectDTO());
		dTO.getTemplateType().setLabel(dbo.getTemplateGroupName());
		dTO.getTemplateType().setValue(dbo.getTemplateGroupName());
		dTO.setTemplateGroupName(dbo.getTemplateGroupName());
		dTO.setTemplateGroupCode(dbo.getTemplateGroupCode());
		dTO.setCampus(new SelectDTO());
		
		dTO.setProgramLevel(new SelectDTO());
		dTO.setProgramSelectedForTemplate(new ArrayList<SelectDTO>());
		dbo.getErpTemplateDBOSet().forEach(template ->{
			dTO.setTemplateFor(new SelectDTO());
			dTO.getTemplateFor().setValue(template.getTemplateType());
			dTO.getTemplateFor().setLabel(template.getTemplateType());
			dTO.setTemplateDescription(template.getTemplateDescription());
			dTO.setTemplateContent(template.getTemplateContent());
			dTO.setAvailableTags(template.getAvailableTags());
			dTO.getCampus().setLabel(template.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getCampusName());
			dTO.getCampus().setValue(String.valueOf(template.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId()));
			SelectDTO prgLvlDto = new SelectDTO();
			prgLvlDto.setLabel(template.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpProgrammeLevelDBO().getProgrammeLevel());
			prgLvlDto.setValue(String.valueOf(template.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getErpProgrammeLevelDBO().getId()));			
			dTO.setProgramLevel(prgLvlDto);
			SelectDTO progDto = new SelectDTO();
			progDto.setLabel(template.getErpCampusProgrammeMappingDBO().getErpProgrammeDBO().getProgrammeName());
			progDto.setValue(String.valueOf(template.getErpCampusProgrammeMappingDBO().getId()));
			dTO.getProgramSelectedForTemplate().add(progDto);
			if(template.getTemplateType().equals("Mail")) {
				dTO.setFromName(template.getMailFromName());
				dTO.setMailSubject(template.getMailSubject());
			}
			if(template.getTemplateType().equals("SMS")) {
				if(!Utils.isNullOrEmpty(template.getTemplateId())){
					dTO.setTemplateIdForSms(template.getTemplateId());
				}
			}
			
		});
		return dTO;
	}

	public Mono<ApiResult> delete(int id, String userId) {
		return admissionTemplateTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

}
