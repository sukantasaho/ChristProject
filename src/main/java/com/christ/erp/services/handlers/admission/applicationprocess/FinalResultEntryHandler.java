package com.christ.erp.services.handlers.admission.applicationprocess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultEntryDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.admission.applicationprocess.FinalResultEntryTransaction;
import com.christ.erp.services.transactions.admission.applicationprocess.UploadFinalResultTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Service
public class FinalResultEntryHandler {
	
	@Autowired
	private FinalResultEntryTransaction finalResultEntryTransaction;
	
	@Autowired
	private UploadFinalResultTransaction uploadFinalResultTransaction;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	public Mono<List<FinalResultEntryDTO>> getActiveProgrammeByYearValue(String yearValue) {
		List<ErpCampusProgrammeMappingDBO> a = finalResultEntryTransaction.getActiveProgrammeByYearValue(yearValue);
		return this.convertDBOToDTO(a);
	}
	
	public Mono<List<FinalResultEntryDTO>> convertDBOToDTO(List<ErpCampusProgrammeMappingDBO> dbos){
		Map<Integer,FinalResultEntryDTO>  map = new HashMap<Integer, FinalResultEntryDTO>();
		List<FinalResultEntryDTO> dtos = new ArrayList<FinalResultEntryDTO>();
		dbos.forEach(dbo -> {
			if(!map.containsKey(dbo.getErpProgrammeDBO().getId())) {
				FinalResultEntryDTO dto = new FinalResultEntryDTO();
				dto.setProgramme(new SelectDTO());
				dto.getProgramme().setValue(String.valueOf(dbo.getErpProgrammeDBO().getId()));
				dto.getProgramme().setLabel(dbo.getErpProgrammeDBO().getProgrammeName());
				dto.setCampusList(new ArrayList<SelectDTO>());
				if(!Utils.isNullOrEmpty(dbo.getErpCampusDBO()) && dbo.getRecordStatus() == 'A') {
					if(dbo.getErpCampusDBO().getRecordStatus() == 'A') {
						SelectDTO campus = new SelectDTO();
						campus.setValue(String.valueOf(dbo.getId()));
						campus.setLabel(dbo.getErpCampusDBO().getCampusName());
						dto.getCampusList().add(campus);
					}
				}
				map.put(dbo.getErpProgrammeDBO().getId(), dto);
			}
			else {
				FinalResultEntryDTO dto = map.get(dbo.getErpProgrammeDBO().getId());
				if(!Utils.isNullOrEmpty(dbo.getErpCampusDBO()) && dbo.getRecordStatus() == 'A') {
					if(dbo.getErpCampusDBO().getRecordStatus() == 'A') {
						SelectDTO campus = new SelectDTO();
						campus.setValue(String.valueOf(dbo.getId()));
						campus.setLabel(dbo.getErpCampusDBO().getCampusName());
						dto.getCampusList().add(campus);
					}
				}
				map.replace(dbo.getErpProgrammeDBO().getId(), dto);
			}
		});
		map.forEach((Key,value) -> {
			dtos.add(value);
		});
		return Mono.just(dtos);
	}
	
	public Mono<List<FinalResultEntryDTO>> getGridData(Mono<FinalResultEntryDTO> data1) {
		return   data1.handle((finalResultEntryDTO, synchronousSink) -> {
			StudentApplnEntriesDBO value = null ;
			if(!Utils.isNullOrEmpty(finalResultEntryDTO.getApplicationNo())) {
				 value = finalResultEntryTransaction.applicationCheck(finalResultEntryDTO);
			}
			if(!Utils.isNullOrEmpty(value)) {
				synchronousSink.error(new GeneralException("This application is already “Admitted” "));
			} else {
				synchronousSink.next(finalResultEntryDTO);
			}
		}).cast(FinalResultEntryDTO.class).map(s -> finalResultEntryTransaction.getGridData(s)).map(data -> convertDBOToDTO1((data)))
				.flatMap( i -> {
					if(Utils.isNullOrEmpty(i)) {
						return Mono.error(new NotFoundException(null));
					} else {
						return Mono.just(i);
					}
				});
	}
		
	public List<FinalResultEntryDTO> convertDBOToDTO1(List<Tuple> list){
		List<FinalResultEntryDTO> values = new ArrayList<FinalResultEntryDTO>();
		if (!Utils.isNullOrEmpty(list)) {
				list.forEach(dbo -> {
					FinalResultEntryDTO dto = new FinalResultEntryDTO();
					dto.setApplicationNo(Integer.parseInt(dbo.get(0).toString()));
					dto.setApplicantName(dbo.get(1).toString());
					dto.setGender(dbo.get(2).toString());
					dto.setTotalWeightage(dbo.get(3).toString());
					dto.setResidentCategory(dbo.get(4).toString());
					dto.setAppliedProgrammeCampus(dbo.get(5).toString());
					dto.setStatus(dbo.get(6).toString());
					dto.setProcessCode(dbo.get(7).toString());
					values.add(dto);
				});
		}
		return values;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Mono<ApiResult> saveApplicantes(Mono<List<FinalResultEntryDTO>> data, String userId) {
		List<String> selectedProgramme = new ArrayList<String>();
		List<String> selectedCampus = new ArrayList<String>();
		List<String> lastDateTimeFeePayment = new ArrayList<String>();
		List<String> admissionCategory  = new ArrayList<String>();
		List<Integer> applicationNos  = new ArrayList<Integer>();
	    return	data.handle((finalResultEntryDTO,synchronousSink) -> {
	    	    	finalResultEntryDTO.forEach(dto -> {
		    			applicationNos.add(dto.getApplicationNo());
						if(Utils.isNullOrEmpty(dto.getSelectedProgramme().getValue())) {
							selectedProgramme.add(dto.getApplicationNo().toString());
						} else if(Utils.isNullOrEmpty(dto.getSelectedCampus().getValue())) {
							selectedCampus.add(dto.getApplicationNo().toString());
						} else if(Utils.isNullOrEmpty(dto.getLastDateAndTimeFeepayment())) {
							lastDateTimeFeePayment.add(dto.getApplicationNo().toString());
						} else if(Utils.isNullOrEmpty(dto.getAdmissionCategory().getValue())) {
							admissionCategory.add(dto.getApplicationNo().toString());
						}
	    	    	});
				if(!Utils.isNullOrEmpty(selectedProgramme)) {
					synchronousSink.error(new GeneralException("Warning  Below Application Number Selected Programme is Empty" + selectedProgramme));
				} else if(!Utils.isNullOrEmpty(selectedCampus)) {
					synchronousSink.error(new GeneralException("Warning  Below Application Number Selected Campus is Empty" + selectedCampus));
				} else if(!Utils.isNullOrEmpty(lastDateTimeFeePayment)) {
					synchronousSink.error(new GeneralException("Warning  Below Application Number Last Date and Time for Fee Payment is Empty" + lastDateTimeFeePayment));
				} else if(!Utils.isNullOrEmpty(admissionCategory)) {
					synchronousSink.error(new GeneralException("Warning  Below Application Number Admission Category is Empty" + admissionCategory));
				} else {
					synchronousSink.next(finalResultEntryDTO);
				}
		        }).map(s -> convertDBOToDTO2((List<FinalResultEntryDTO>) s,applicationNos,userId))
			      .flatMap( s ->{ 
					 uploadFinalResultTransaction.update(s);	
					 return Mono.just(Boolean.TRUE);
				   }).map(Utils::responseResult);   
	}
	
	public List<Object> convertDBOToDTO2(List<FinalResultEntryDTO> dtoData, List<Integer> applicationNos, String userId){
		List<Object> data = new ArrayList<Object>();
		Map<Integer, StudentApplnEntriesDBO> studentsDboMap  =  uploadFinalResultTransaction.getApplicantsDetails(applicationNos).stream().collect(Collectors.toMap(s -> s.getApplicationNo(), s -> s));
		List<StudentApplnEntriesDBO> values = new ArrayList<StudentApplnEntriesDBO>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_SELECTED_UPLOADED");
		if (!Utils.isNullOrEmpty(dtoData)) {
			dtoData.forEach(dto -> {
				StudentApplnEntriesDBO studentData = studentsDboMap.get(dto.getApplicationNo());
				if(Utils.isNullOrEmpty(studentData)) {
					if(!studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_SELECTED") 
							&& !studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_NOT_SELECTED") 
							&& !studentData.getApplicationCurrentProcessStatus().processCode.equalsIgnoreCase("ADM_APPLN_WAITLISTED" )) {
						if(studentData.getApplicationCurrentProcessStatus().getId() != Integer.parseInt(selected.get("erp_work_flow_process_id").toString())) {
							studentData.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							studentData.getApplicationCurrentProcessStatus().setId(Integer.parseInt(selected.get("erp_work_flow_process_id").toString()));
							studentData.setFeePaymentFinalDateTime(dto.getLastDateAndTimeFeepayment());
							studentData.setErpCampusProgrammeMappingDBO(new ErpCampusProgrammeMappingDBO());
							studentData.getErpCampusProgrammeMappingDBO().setId(Integer.parseInt(dto.getSelectedCampus().getValue()));
							studentData.setErpAdmissionCategoryDBO(new ErpAdmissionCategoryDBO());
							studentData.getErpAdmissionCategoryDBO().setId(Integer.parseInt(dto.getAdmissionCategory().getValue()));
							LocalDateTime statusDateTime = LocalDateTime.now();
							studentData.setApplicationStatusTime(statusDateTime);
							studentData.setModifiedUsersId(Integer.parseInt(userId));
							values.add(studentData);
							ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
							erpWorkFlowProcessStatusLogDBO.setEntryId(studentData.getId());
							erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
							erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(studentData.getApplicationCurrentProcessStatus().getId());
							erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
							erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
							statusLogList.add(erpWorkFlowProcessStatusLogDBO);
						}
					}
				}
			});
		}
		data.addAll(values);
		data.addAll(statusLogList);
		//commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
		return data;
	}

}
