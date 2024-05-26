package com.christ.erp.services.handlers.admission;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanProgrammeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpUniversityBoardDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmPrerequisiteExamDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDetailsPeriodDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.helpers.admission.CommonAdmissionHelper;
import com.christ.erp.services.transactions.admission.CommonAdmissionTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.*;

@Service
public class CommonAdmissionHandler {
//	private static volatile CommonAdmissionHandler commonAdmissionHandler = null;
//    CommonAdmissionHelper commonAdmissionHelper = commonAdmissionHelper;
//    CommonAdmissionTransaction commonAdmissionTransaction = CommonAdmissionTransaction.getInstance();
//
//    public static CommonAdmissionHandler getInstance() {
//        if(commonAdmissionHandler==null) {
//        	commonAdmissionHandler = new CommonAdmissionHandler();
//        }
//        return commonAdmissionHandler;
//    }
	@Autowired
	CommonAdmissionHelper commonAdmissionHelper;
	@Autowired
	CommonAdmissionTransaction commonAdmissionTransaction;

    /* methods are not in use
    @SuppressWarnings("static-access")
	public void getAccountHead(ApiResult<List<LookupItemDTO>> result, String offlineApplnNoPrefix,String offlineApplnNo,String academicYear)throws Exception {
		if(!Utils.isNullOrEmpty(offlineApplnNoPrefix) && !Utils.isNullOrEmpty(offlineApplnNo) && !Utils.isNullOrEmpty(academicYear)) {
			List<Tuple> mappings = commonAdmissionTransaction.getAccountHead(offlineApplnNoPrefix,offlineApplnNo,academicYear);
			result = commonAdmissionHelper.convertAccountHeadToLookupItemDTO(result, mappings);
		}
	}
	
	@SuppressWarnings("static-access")
	public void getAmountByAccountHead(ApiResult<List<LookupItemDTO>> result, String accountHeadId)throws Exception {
		List<Tuple> mappings = commonAdmissionTransaction.getAmountByAccountHead(accountHeadId);
		result = commonAdmissionHelper.convertToLookupItemDTO(result,mappings);
	}
	*/
	
	@SuppressWarnings("static-access")
	public void getAccountName(ApiResult<List<LookupItemDTO>> result, String loggedInUserId)throws Exception {
		List<Tuple> mappings = commonAdmissionTransaction.getAccountName(loggedInUserId);
		result = commonAdmissionHelper.convertToLookupItemDTO(result,mappings);
	}
	
	@SuppressWarnings("static-access")
	public void getQualitativeParameterLabelList(ApiResult<List<LookupItemDTO>> result)throws Exception {
		List<Tuple> mappings = commonAdmissionTransaction.getQualitativeParameterLabelList();
		result = commonAdmissionHelper.convertToLookupItemDTO(result,mappings);
	}
	
	public Flux<AdmPrerequisiteExamDTO> getAdmPrerequisiteExam(int acadadmicYearId, int programId) {
		return commonAdmissionTransaction.getAdmPrerequisiteExam(acadadmicYearId, programId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertAdmPrerequisiteExamDBOtoDTO1);
	}

	public Flux<AdmQualificationListDTO> getAdmQualificationList(int programId) {
		return commonAdmissionTransaction.getAdmQualificationList(programId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertAdmQualificationListDBOtoDTO);
	}
	
	public Flux<AdmSelectionProcessPlanDetailDTO> getAdmSelectionProcessPlanDetail(int acadadmicYearId, List<Integer> campusProgramMapingId) {
		
	Set<Integer> integer = new HashSet<Integer>();
		return commonAdmissionTransaction.getAdmSelectionProcessPlanDetail(acadadmicYearId, campusProgramMapingId)
				.flatMapMany(Flux::fromIterable)
				.filter(item->{ 
					if(!integer.contains(item.getId())) {
						int size = 0;
						size = integer.size();
						item.getAdmSelectionProcessPlanDetailDBO().forEach(item2->{
							 item2.getAdmSelectionProcessPlanDetailProgDBOs().forEach(item3->{
								if(!Utils.isNullOrEmpty(item3.getErpCampusProgrammeMappingDBO()) && campusProgramMapingId.contains(item3.getErpCampusProgrammeMappingDBO().getId())
										&& !Utils.isNullOrEmpty(item3.recordStatus) && item3.getRecordStatus() =='A') {
									integer.add(item.getId());
								}
							});
						});
						if(size<integer.size())
							return true;
						else
							return false;
					}
					return false;
					})
				.map(commonAdmissionHelper::convertAdmSelectionProcessPlanDBOtoDTO);
	}
	 public void getPrerequisiteExamMonthsYearByExamId(ApiResult<List<AdmPrerequisiteSettingsDetailsPeriodDTO>> result, String erpCampusProgrammeMappingId, String examId, String erpAcademicYearId) throws Exception{
	        List<Tuple> list = commonAdmissionTransaction.getPrerequisiteExamMonthsByExam(erpCampusProgrammeMappingId,examId,erpAcademicYearId);
	        if(!Utils.isNullOrEmpty(list)) {
	            result.success = true;
	            result.dto = new ArrayList<>();
	            for(Tuple tuple : list) {
					AdmPrerequisiteSettingsDetailsPeriodDTO itemDTO = new AdmPrerequisiteSettingsDetailsPeriodDTO();
					if (!Utils.isNullOrEmpty(tuple.get("periodId"))) {
						itemDTO.setId(Integer.parseInt(tuple.get("periodId").toString()));
					}
	                itemDTO.setYear(!Utils.isNullOrEmpty(tuple.get("examYear")) ? Integer.valueOf(tuple.get("examYear").toString()) : null);
					if (!Utils.isNullOrEmpty(tuple.get("examMonth"))) {
						itemDTO.setMonth(new SelectDTO());
						itemDTO.getMonth().setValue((tuple.get("examMonth").toString()));
						itemDTO.getMonth().setLabel(Utils.getMonthName(Integer.parseInt(tuple.get("examMonth").toString())));
					}
	              result.dto.add(itemDTO);
	            }
	        }
	 }
	 
	public Flux<LookupItemDTO> getAdmWeightageDefinitionWorkExperience() {
		return  commonAdmissionTransaction.getAdmWeightageDefinitionWorkExperience()
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertAdmWeightageDefinitionWorkExperienceDBOtoDTO);
	}

	public Flux<LookupItemDTO> getAdmQualificationDegreeList(String admQualificationListId) {
		return  commonAdmissionTransaction.getAdmQualificationDegreeList(admQualificationListId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertAdmQualificationListDegreeDBOtoDTO);
	}
	
	public Flux<LookupItemDTO> getSelectionProcessDate(int year, int degreeId, String erpCampusProgramId) {
		return commonAdmissionTransaction.getSelectionProcessDate(year,degreeId,erpCampusProgramId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertSelectionProcessDateDBOtoDTO);
	}

	public Flux<LookupItemDTO> getSelectionProcessCenter(String date) {
		return commonAdmissionTransaction.getSelectionProcessCenter(date)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertSelectionProcessCenterDBOtoDTO);
	}
	
	public Flux<LookupItemDTO> getCampusProgrammeMapping(int year, int degreeId) {
		return commonAdmissionTransaction.getCampusProgrammeMapping(year,degreeId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertCampusProgrammeMappingDBOtoDTO);
	}

	public Flux<LookupItemDTO> getSelectionProcessTime(String selectionProcessDate, int selectionProcessVenueId) {
		return commonAdmissionTransaction.getSelectionProcessTime(selectionProcessDate, selectionProcessVenueId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertSelectionProcessTimetoDTO);
				
	}
	
	public Flux<SelectDTO> getQualification() {
		return commonAdmissionTransaction.getQualification().flatMapMany(Flux::fromIterable).map(this::convertQualificationDBOToDTO);
	}
	
	public SelectDTO convertQualificationDBOToDTO(AdmQualificationListDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getQualificationName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getQualificationName());
		}
		return dto;
	}

	public Flux<ProgramPreferenceDTO> getProgrammeBySelectionProcessSession(String selectionProcessSession) {
		return commonAdmissionTransaction.getProgrammeBySelectionProcessSession(selectionProcessSession)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertProgrammePreferenceToDTO);
	}

	public Flux<SelectDTO> getUniversityOrBoard() {
		return commonAdmissionTransaction.getUniversityOrBoard().flatMapMany(Flux::fromIterable).map(this::convertDboToDto);
	}
	
	public SelectDTO convertDboToDto(ErpUniversityBoardDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if(!Utils.isNullOrEmpty(dbo.getUniversityBoardName())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getUniversityBoardName());
		}
		return dto;
	}

	public Flux<SelectDTO> getAdmissionCategory() {
		return commonAdmissionTransaction.getAdmissionCategory().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	
	public SelectDTO convertDBOToDTO(ErpAdmissionCategoryDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAdmissionCategoryName());
		}
		return dto;
	}

	public Flux<SelectDTO> getSelectionProcessSession(String academicYearId) {
		return commonAdmissionTransaction.getSelectionProcessSession(academicYearId)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertSelectionProcessSessionToDTO);
	}

	public Flux<LookupItemDTO> getSelectionProcessTypeBySession(String selectionProcessSession) {
		return commonAdmissionTransaction.getSelectionProcessTypeBySession(selectionProcessSession)
				.flatMapMany(Flux::fromIterable)
				.map(commonAdmissionHelper::convertSelectionProcessTypeToDTO);
	}
	
	public Flux<SelectDTO> getAccHeadsForApplicationFees() {
		return commonAdmissionTransaction.getAccHeadsForApplicationFees().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO); 
	}
	
	public SelectDTO convertDBOToDTO(AccFeeHeadsDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getHeading());
		}
		return dto;
	}

	public Flux<SelectDTO> getSessionForProgramme(Integer programmeId,String yearId) {
		return commonAdmissionTransaction.getSessionForProgramme(programmeId,yearId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	public SelectDTO convertDBOToDTO(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.get("id")));
			dto.setLabel(String.valueOf(dbo.get("name")));
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByYear(Integer yearId,Integer yearValue) {
		return commonAdmissionTransaction.getProgrammeByYear(yearId,yearValue).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO1);
	}
	public SelectDTO convertDBOToDTO1(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.get("progId")));
			dto.setLabel(String.valueOf(dbo.get("progName")));
		}
		return dto;
	}

	public Flux<SelectDTO> getLocationOrCampusByProgrammeAndYear(String yearValue, String programId, Boolean isLocation) {
		return commonAdmissionTransaction.getLocationOrCampusByProgrammeAndYear(yearValue, programId, isLocation)
				.flatMapMany(Flux::fromIterable)
				.map(mapping -> convertLocationDTO(mapping, isLocation));
	}

	private SelectDTO convertLocationDTO(ErpCampusProgrammeMappingDBO dbo, Boolean isLocation) {
		SelectDTO dto = new SelectDTO();
		if(isLocation && !Utils.isNullOrEmpty(dbo.getErpLocationDBO())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getErpLocationDBO().getLocationName());
		} else if(!isLocation && !Utils.isNullOrEmpty(dbo.getErpCampusDBO())) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getErpCampusDBO().getCampusName());
		}
		return dto;
	}

	public Flux<SelectDTO> getAdmissionType() {
		return commonAdmissionTransaction.getAdmissionType().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO1);
	}
	public SelectDTO convertDBOToDTO1(AdmAdmissionTypeDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAdmissionType());
		}
		return dto;
	}

	public Mono<List<SelectDTO>> getProgrammeMode(String yearValue, String programId) {
		Tuple data = commonAdmissionTransaction.getProgrammeMode(yearValue, programId);
		return this.convertModeDBOToDTO(data) ;
	}
	public Mono<List<SelectDTO>> convertModeDBOToDTO(Tuple dbo){
		List<SelectDTO> dtos = new ArrayList<>();
		if (!Utils.isNullOrEmpty(dbo) && String.valueOf(dbo.get("mode")).equals("BOTH")) {
			SelectDTO dto = new SelectDTO();
			dto.setValue("PART_TIME");
			dto.setLabel("PART TIME");
			dtos.add(dto);
			SelectDTO dto1 = new SelectDTO();
			dto1.setValue("FULL_TIME");
			dto1.setLabel("FULL TIME");
			dtos.add(dto1);
			SelectDTO dto2 = new SelectDTO();
			dto2.setValue("BOTH");
			dto2.setLabel("BOTH");
			dtos.add(dto2);
		}
		else if (!Utils.isNullOrEmpty(dbo) && !Utils.isNullOrEmpty(dbo.get("mode"))) {
			SelectDTO dto = new SelectDTO();
			dto.setValue(String.valueOf(dbo.get("mode")));
			dto.setLabel(String.valueOf(dbo.get("mode")).replace("_"," "));
			dtos.add(dto);
		}
		return  dtos.isEmpty() ? Mono.error(new NotFoundException(null)) :  Mono.just(dtos);
	}

	public Flux<SelectDTO> getProgrammeByYearAndIntakeAndType( String yearId,  List<Integer> intakeId,String admissionType) {
		return commonAdmissionTransaction.getProgrammeByYearAndIntakeAndType(yearId,intakeId,admissionType).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO2);
	}
	public SelectDTO convertDBOToDTO2(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			String value = String.valueOf(dbo.get("pname"));
			if(!Utils.isNullOrEmpty(dbo.get("csName"))){
				value += " ("+dbo.get("csName")+")";
				dto.setLabel(value);
			} else {
				value += " ("+dbo.get("locsName")+")";
				dto.setLabel(value);
			}
//			value +="-"+dbo.get("atype");
			dto.setValue(String.valueOf(dbo.get("id")));
			dto.setLabel(value);
		}
		return dto;
	}

	public Flux<SelectDTO> getIntakeBatch() {
		return commonAdmissionTransaction.getIntakeBatch().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO1);
	}
	public SelectDTO convertDBOToDTO1(AdmIntakeBatchDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAdmIntakeBatchName());
		}
		return dto;
	}

	public Flux<SelectDTO> getProgrammeByPlan(Integer planId) {
		return commonAdmissionTransaction.getProgrammeByPlan(planId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO3);
	}
	public SelectDTO convertDBOToDTO3(Tuple dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.get("id")));
			String value = String.valueOf(dbo.get("name"));
			if(!Utils.isNullOrEmpty(dbo.get("cmps"))){
				value += " ("+dbo.get("cmps")+")";
			} else if(!Utils.isNullOrEmpty(dbo.get("locs"))) {
				value += " ("+dbo.get("locs")+")";
			}
			value +="-"+dbo.get("atype");
			dto.setLabel(value);
		}
		return dto;
	}
	public Mono<List<SelectDTO>> getBoardType() {
		List<SelectDTO> dtos = new ArrayList<>();
		dtos.add(new SelectDTO("Board","Board" ));
		dtos.add(new SelectDTO("University","University" ));
		dtos.add(new SelectDTO("Board_X","Board X" ));
		dtos.add(new SelectDTO("Board_XI","Board XI" ));
		dtos.add(new SelectDTO("Board_XII","Board XII" ));
		dtos.sort(Comparator.comparing(SelectDTO::getLabel));
		return Mono.just(dtos) ;
	}

}



