package com.christ.erp.services.handlers.admission.applicationprocess;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreEntryDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterOptionDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQualitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardQuantitativeParameterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessScoreDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessTypeDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessTypeDetailsDTO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDetailsDTO;
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterDTO;
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterOptionDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQualitativeParameterDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQuantitativeParameterDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.transactions.admission.applicationprocess.ScoreEntryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ScoreEntryHandler {
	private static final String  DATE_FORMATTER = "dd/MM/yyyy HH:mm:ss";
	
	@Autowired
	private ScoreEntryTransaction scoreEntryTransaction;

	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;
	
	public Mono<AdmSelectionProcessScoreDTO> getSelectionProcessList(int applicationNumber,String mode) {
		Mono<List<Tuple>> list = scoreEntryTransaction.getSelectionProcessList(applicationNumber,mode);
	    Mono<Map<Integer, List<Tuple>>> map = list.flatMapMany(Flux::fromIterable)
                                                     .filter(objects -> !Utils.isNullOrEmpty(objects.get("adm_selection_process_type_id")))
                                                     .collect(Collectors.groupingBy(r -> Integer.parseInt(r.get("application_no").toString()), Collectors.toList()));
	 	return map.map(this::convertObjectToDTO);
	 }

    public AdmSelectionProcessScoreDTO convertObjectToDTO(Map<Integer,  List<Tuple>> obj) {
    	AdmSelectionProcessScoreDTO mainDto = new AdmSelectionProcessScoreDTO();
    	mainDto.setAdmSelectionProcessDto(null);
        obj.forEach((process,processType) -> {
        	StudentApplnEntriesDTO stdAppDto = new StudentApplnEntriesDTO();
       	    List<AdmSelectionProcessDTO> processTypeList = new ArrayList<>();
       	    processType.forEach(obj1 -> {
       	    	if(!Utils.isNullOrEmpty(obj1.get("adm_selection_process_id"))) {
	        		if(!Utils.isNullOrEmpty(obj1.get("student_appln_entries_id"))) {
	        			stdAppDto.setApplicationNumber(obj1.get("application_no").toString());
		        		stdAppDto.setApplicantName(obj1.get("applicant_name").toString());
		        		stdAppDto.setStudentApplnEntriesId(obj1.get("student_appln_entries_id").toString());
		        		stdAppDto.setProgramme(obj1.get("programme_name").toString());

						//Photo
						if(!Utils.isNullOrEmpty(obj1.get("file_name_unique")) && !Utils.isNullOrEmpty(obj1.get("upload_process_code")) && !Utils.isNullOrEmpty(obj1.get("file_name_original"))) {
							FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
							fileUploadDownloadDTO.setActualPath(obj1.get("file_name_unique").toString());
							fileUploadDownloadDTO.setProcessCode(obj1.get("upload_process_code").toString());
							fileUploadDownloadDTO.setOriginalFileName(obj1.get("file_name_original").toString());
							aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
							stdAppDto.setPhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
						}
//		        		if(!Utils.isNullOrEmpty(obj1.get("profile_photo_url")))
//		        			stdAppDto.setPhotoUrl(obj1.get("profile_photo_url").toString());
		        		if(!Utils.isNullOrEmpty(obj1.get("campus_name"))) {
		        			stdAppDto.setCampusOrLocation(obj1.get("campus_name").toString());
		        		} else {
		        			stdAppDto.setCampusOrLocation(obj1.get("location_name").toString());
		        		} 
	        		}
	        		if(!Utils.isNullOrEmpty(obj1.get("selection_stage_name"))) {
	        			AdmSelectionProcessDTO processDto = new AdmSelectionProcessDTO();
	        			processDto.setAdmSelectionProcessId(Integer.parseInt(obj1.get("adm_selection_process_id").toString()));
	        			AdmSelectionProcessTypeDTO dto1 = new AdmSelectionProcessTypeDTO();       		
	        			dto1.setAdmSelectionProcessTypeId(Integer.parseInt(obj1.get("adm_selection_process_type_id").toString()));
	        		    dto1.setSelectionStageName(obj1.get("selection_stage_name").toString());
	        		    processDto.setAdmselectionProcessTypeDto(dto1);
	        		    processTypeList.add(processDto);
	        		}	
       		    }
	        });	        	 
       	    mainDto.setStudentApplnEntriesDto(stdAppDto);
       		mainDto.setAdmSelectionProcessDto(processTypeList);
       		mainDto.setRound(processTypeList.size());
       		
       		
        });
	 return mainDto;
    }
    	
	public Flux<AdmSelectionProcessTypeDTO> getSubProcess(int processId,int typeId,String userId) { 
		Tuple value =  scoreEntryTransaction.getTypeDetailsId(typeId).get(0);
        String date1 = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(value.get("selection_process_date").toString()));
	    List<Tuple> list = scoreEntryTransaction.duplicateCheck(processId,Integer.parseInt(value.get("adm_selection_process_type_details_id").toString()),userId);
		if(Utils.isNullOrEmpty(list)) {
			return scoreEntryTransaction.getSubprocess(typeId).flatMapMany(Flux::fromIterable).map(data ->subProcessDboToDto(data,date1));
		} else {
		    StringBuilder errormsg = new StringBuilder("Score is already entered for this applicant on ");
			list.forEach(obj1 ->{
				LocalDateTime date = (LocalDateTime) obj1.get("score_entered_time");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
				errormsg.append(date.format(formatter));
			});
			return Flux.error(new GeneralException(errormsg.toString()));
		}
    }
	
    public AdmSelectionProcessTypeDTO subProcessDboToDto(AdmSelectionProcessTypeDBO dbo, String date1) {
    	AdmSelectionProcessTypeDTO processTypeDto = new AdmSelectionProcessTypeDTO();
    	processTypeDto.setAdmSelectionProcessTypeId(dbo.id);
    	processTypeDto.setDate(date1);
    	if(dbo.getAdmissionSelectionProcessTypeDetailsDBOSet().size()>0) {
    		List<AdmSelectionProcessTypeDetailsDTO> subProcessTypeDetailsList = new ArrayList<AdmSelectionProcessTypeDetailsDTO>();
        	dbo.getAdmissionSelectionProcessTypeDetailsDBOSet().forEach(typeDetails ->{
        		AdmSelectionProcessTypeDetailsDTO typeDetailsDto = new AdmSelectionProcessTypeDetailsDTO();
        		typeDetailsDto.setId(typeDetails.id);    		
        		SelectDTO dto = new SelectDTO();
        		dto.setValue(""+typeDetails.id);
        		dto.setLabel(typeDetails.getSubProcessName());
        		typeDetailsDto.setSubProcessName(dto);
        		AdmScoreCardDBO admScoreCardDbo = typeDetails.admissionScoreCardDBO;        		
        		typeDetailsDto.setScoreCardId(""+admScoreCardDbo.id);
        		if(admScoreCardDbo.admScoreCardQuantitativeParameterDBO.size()>0  && admScoreCardDbo.recordStatus == 'A') {
        			List<AdmScoreCardQuantitativeParameterDTO> quanParameters = new ArrayList<AdmScoreCardQuantitativeParameterDTO>();
            		Set<AdmScoreCardQuantitativeParameterDBO> qua = admScoreCardDbo.admScoreCardQuantitativeParameterDBO;
        			qua.forEach(quan ->{
        				if(quan.getRecordStatus() == 'A') {
        					AdmScoreCardQuantitativeParameterDTO quantitativeParameter = new AdmScoreCardQuantitativeParameterDTO();
        					quantitativeParameter.setAdmScoreCardQuantitativeParameterId(quan.getId());
        					quantitativeParameter.setParameterName(quan.getParameterName());
        					quantitativeParameter.setMaxValue(quan.getMaxValue());
        					quantitativeParameter.setValueInterval(quan.getIntervalValue());
        					quantitativeParameter.setOrderNo(quan.getOrderNo());
        					quanParameters.add(quantitativeParameter);
        				}
        			});
        			typeDetailsDto.setQuantitativeParameters(quanParameters);
        		}
        		if(admScoreCardDbo.admScoreCardQualitativeParameterDBO.size()>0 && admScoreCardDbo.recordStatus == 'A') {
        			List<AdmScoreCardQualitativeParameterDTO> qualitativeParameters = new ArrayList<AdmScoreCardQualitativeParameterDTO>();
        			Set<AdmScoreCardQualitativeParameterDBO> quali = admScoreCardDbo.admScoreCardQualitativeParameterDBO;
        			quali.forEach(qualiParameters ->{
        				if(qualiParameters.getRecordStatus() =='A') {
        					AdmScoreCardQualitativeParameterDTO parameterDto = new AdmScoreCardQualitativeParameterDTO();
        				    parameterDto.setAdm_scorecard_qualitative_parameter_id(qualiParameters.getId());
        				    AdmQualitativeParamterDBO parameterDbo = qualiParameters.getAdmQualitativeParamterDBO();
        				    AdmQualitativeParamterDTO qualiParameterDto = new AdmQualitativeParamterDTO();
        				    qualiParameterDto.setQualitativeParameterLabel(parameterDbo.getQualitativeParameterLabel());
        				    qualiParameterDto.setFieldType(parameterDbo.getFieldType());        				
        				    List<AdmQualitativeParamterOptionDTO> optionsList = new ArrayList<AdmQualitativeParamterOptionDTO>();
        				    Set<AdmQualitativeParamterOptionDBO> optionDbo = parameterDbo.getAdmQualitativeParameterOptionSet();
        				    optionDbo.forEach(options ->{
        				    	AdmQualitativeParamterOptionDTO optionDto = new AdmQualitativeParamterOptionDTO();
       					        SelectDTO dtoOptions = new SelectDTO();
       					        dtoOptions.setValue(""+options.getId());
       					        dtoOptions.setLabel(options.getOptionName());
       					        optionDto.setParameterOptions(dtoOptions);
        					    optionsList.add(optionDto);
        				    });
        				    qualiParameterDto.setOptions(optionsList);
        				    parameterDto.setAdmQualitativeParameterDto(qualiParameterDto);
        				    qualitativeParameters.add(parameterDto);
        			    }
        			});
        			typeDetailsDto.setQualitativeParameters(qualitativeParameters);
        		}
        		subProcessTypeDetailsList.add(typeDetailsDto);
        	});
        	processTypeDto.setAdmSelectionProcessTypeDetailsDto(subProcessTypeDetailsList);
    	}
        return processTypeDto;
    }

    @SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveScore(int processId,Mono<AdmSelectionProcessTypeDTO> dto, String userId) {
		 return dto.map(data -> saveScoreDtoToDbo(processId,data, userId))
		           .flatMap( s -> { scoreEntryTransaction.save(s);
	               return Mono.just(Boolean.TRUE);
                   }).map(Utils::responseResult);
	}
 
	public List<AdmSelectionProcessScoreDBO> saveScoreDtoToDbo(int processId,AdmSelectionProcessTypeDTO dto, String userId) {		
		List<AdmSelectionProcessScoreDBO> data = new ArrayList<AdmSelectionProcessScoreDBO>();	
		dto.getAdmSelectionProcessTypeDetailsDto().forEach(typeDetailsDto -> {
			AdmSelectionProcessScoreDBO header = new AdmSelectionProcessScoreDBO();
			header.setCreatedUsersId(Integer.valueOf(userId));
			header.setRecordStatus('A');
			header.setAdmSelectionProcessDBO(new AdmSelectionProcessDBO());
			header.getAdmSelectionProcessDBO().setId(processId);
			header.setAdmSelectionProcessTypeDetailsDBO(new AdmSelectionProcessTypeDetailsDBO());
			header.getAdmSelectionProcessTypeDetailsDBO().setId(typeDetailsDto.getId());
			Set<AdmSelectionProcessScoreEntryDBO> scoreEntrySet = new HashSet<AdmSelectionProcessScoreEntryDBO>();
			BigDecimal maxScore = new BigDecimal("000000.00");
			BigDecimal socreEntered = new BigDecimal("000000.00");
			AdmSelectionProcessScoreEntryDBO subDbo =  new AdmSelectionProcessScoreEntryDBO();
			subDbo.setCreatedUsersId(Integer.parseInt(userId));
			subDbo.setRecordStatus('A');
			subDbo.setAdmSelectionProcessScoreDBO(header);
			subDbo.setAdmScoreCardDBO(new AdmScoreCardDBO());
			subDbo.getAdmScoreCardDBO().setId(Integer.parseInt(typeDetailsDto.getScoreCardId()));
			subDbo.setErpUsersDBO( new ErpUsersDBO());
			subDbo.getErpUsersDBO().setId(Integer.parseInt(userId));
			Set<AdmSelectionProcessScoreEntryDetailsDBO> admSelectionProcessScoreEntryDetailsDBOUpdate = new HashSet<AdmSelectionProcessScoreEntryDetailsDBO>();
			typeDetailsDto.getQualitativeParameters().forEach( qualitative -> {
				AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = new AdmSelectionProcessScoreEntryDetailsDBO();
				if("Textbox".equals(qualitative.getAdmQualitativeParameterDto().getFieldType() )) {
					subDbo1.setQualitativeParameterScoreEnteredText(qualitative.getAdmQualitativeParameterDto().getEnteredValue());
				} else {
					subDbo1.setAdmQualitativeParamterOptionDBO(new AdmQualitativeParamterOptionDBO());
					subDbo1.getAdmQualitativeParamterOptionDBO().setId(Integer.parseInt(qualitative.getAdmQualitativeParameterDto().getEnteredValue()));
				}
				subDbo1.setAdmSelectionProcessScoreEntryDBO(subDbo);
				subDbo1.setCreatedUsersId(Integer.parseInt(userId));
				subDbo1.setRecordStatus('A');
				subDbo1.setAdmScoreCardQualitativeParameterDBO(new AdmScoreCardQualitativeParameterDBO());
				subDbo1.getAdmScoreCardQualitativeParameterDBO().setId(qualitative.getAdm_scorecard_qualitative_parameter_id());
				admSelectionProcessScoreEntryDetailsDBOUpdate.add(subDbo1);
			});
			for( AdmScoreCardQuantitativeParameterDTO   quan : typeDetailsDto.getQuantitativeParameters()) {
				AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = new AdmSelectionProcessScoreEntryDetailsDBO();
				subDbo1.setAdmSelectionProcessScoreEntryDBO(subDbo);
				subDbo1.setCreatedUsersId(Integer.parseInt(userId));
				subDbo1.setRecordStatus('A');
				subDbo1.setAdmScoreCardQuantitativeParameterDBO(new AdmScoreCardQuantitativeParameterDBO());
				subDbo1.getAdmScoreCardQuantitativeParameterDBO().setId(Integer.parseInt(quan.getAdmScoreCardQuantitativeParameterId().toString()));
				subDbo1.setQuantitativeParameterMaxScore(BigDecimal.valueOf(quan.getMaxValue()));
				subDbo1.setQuantitativeParameterScoreEntered(quan.getScoreEntered());
				maxScore = maxScore.add(BigDecimal.valueOf(quan.getMaxValue())) ;
				socreEntered = socreEntered.add(quan.getScoreEntered());
				admSelectionProcessScoreEntryDetailsDBOUpdate.add(subDbo1);
			}
			subDbo.setMaxScore(maxScore);
			subDbo.setScoreEntered(socreEntered);
			subDbo.setAdmSelectionProcessScoreEntryDetailsDBOSet(admSelectionProcessScoreEntryDetailsDBOUpdate);
			scoreEntrySet.add(subDbo);
			header.setAdmSelectionProcessScoreEntryDBOSet(scoreEntrySet);
			data.add(header);
		});
		return data;
	}
	
	public  Mono<List<SelectDTO>> getGroupSelectionProcessList(int timeId) {
		List<Tuple> list = scoreEntryTransaction.getGroupSelectionProcessList(timeId);
		List<SelectDTO> processTypeList = new ArrayList<>();
		list.forEach(data -> {
			SelectDTO dto1= new SelectDTO();  		
			dto1.setValue(data.get("adm_selection_process_type_id").toString());
		    dto1.setLabel(data.get("selection_stage_name").toString());
		    processTypeList.add(dto1);
		});
		return Mono.just(processTypeList);

	 }

	public Mono<List<SelectDTO>> getGroupSubProcessList(int typeId) {
		List<Tuple> list = scoreEntryTransaction.getGroupSubProcessList(typeId);
		List<SelectDTO> sub = new ArrayList<SelectDTO>();
		list.forEach(data -> {
			SelectDTO dto = new SelectDTO();
			dto.setValue(data.get("adm_selection_process_type_details_id").toString());
			dto.setLabel(data.get("sub_process_name").toString());
			sub.add(dto);
		});
		return Mono.just(sub);
	}

	public Mono<List<AdmSelectionProcessScoreDTO>> getGroupSubProcessData(int groupId,int subProcessId, String userId) {
		Tuple  list = scoreEntryTransaction.duplicateGroupScoreCheck(groupId,subProcessId,userId);
		if(!Utils.isNullOrEmpty(list)) {
			LocalDateTime date = (LocalDateTime) list.get("score_entered_time");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
			String errormsg = "" +list.get("sub_process_name")+" Score is already entered for these Group on " + date.format(formatter);
			return Mono.error(new GeneralException(errormsg));
		} else {
			Tuple data1 = scoreEntryTransaction.getSubProcessDate(subProcessId);
			String date1 = Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(data1.get("date").toString()));
			List<Tuple> applicantsList =  scoreEntryTransaction.getGroupApplicantsData(String.valueOf(groupId));
			Mono<List<AdmSelectionProcessScoreDTO>> values = scoreEntryTransaction.getGroupSubProcessData(subProcessId).flatMapMany(Flux::fromIterable).map(data ->groupSubProcessDboToDto(data,date1,applicantsList)).next();
			return  values;
		}
	}
	
	public List<AdmSelectionProcessScoreDTO> groupSubProcessDboToDto(AdmSelectionProcessTypeDBO dbo, String date1, List<Tuple> applicantsList) {	
		List<AdmSelectionProcessScoreDTO>  processScoreDto1 = new ArrayList<AdmSelectionProcessScoreDTO>();
		List<Tuple> obj =  applicantsList;
		obj.forEach(applicant -> {
			AdmSelectionProcessScoreDTO  processScoreDto = new AdmSelectionProcessScoreDTO();
			processScoreDto.setSelectionProcessGroupEditDetailsDTO(new SelectionProcessGroupEditDetailsDTO());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setStudentEntrieId(applicant.get("student_appln_entries_id").toString());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setApplicationNo(applicant.get("application_no").toString());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setApplicantName(applicant.get("applicant_name").toString());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setPrograme(applicant.get("campus_program_name").toString());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setPhotoUrl(applicant.get("profile_photo_url").toString());
			processScoreDto.getSelectionProcessGroupEditDetailsDTO().setSelectionProcessId(applicant.get("adm_selection_process_id").toString());
			if(dbo.getAdmissionSelectionProcessTypeDetailsDBOSet().size()>0) {
				AdmSelectionProcessTypeDetailsDTO typeDetailsDto =  new AdmSelectionProcessTypeDetailsDTO(); ;
				dbo.getAdmissionSelectionProcessTypeDetailsDBOSet().forEach(typeDetails ->{ 
					typeDetailsDto.setId(typeDetails.id);
					SelectDTO dto = new SelectDTO();
					dto.setLabel(typeDetails.getSubProcessName());
	        		typeDetailsDto.setSubProcessName(dto);
	        		AdmScoreCardDBO admScoreCardDbo = typeDetails.admissionScoreCardDBO; 
	        		typeDetailsDto.setScoreCardId(""+admScoreCardDbo.id);
	        		if(admScoreCardDbo.admScoreCardQuantitativeParameterDBO.size()>0  && admScoreCardDbo.recordStatus == 'A') { 
	        			List<AdmScoreCardQuantitativeParameterDTO> quanParameters = new ArrayList<AdmScoreCardQuantitativeParameterDTO>();
	            		Set<AdmScoreCardQuantitativeParameterDBO> qua = admScoreCardDbo.admScoreCardQuantitativeParameterDBO;
	        			qua.forEach(quan ->{
	        				if(quan.getRecordStatus() == 'A') {
	        					AdmScoreCardQuantitativeParameterDTO quantitativeParameter = new AdmScoreCardQuantitativeParameterDTO();
	        					quantitativeParameter.setAdmScoreCardQuantitativeParameterId(quan.getId());
	        					quantitativeParameter.setParameterName(quan.getParameterName());
	        					quantitativeParameter.setMaxValue(quan.getMaxValue());
	        					quantitativeParameter.setValueInterval(quan.getIntervalValue());
	        					quantitativeParameter.setOrderNo(quan.getOrderNo());
	        					quanParameters.add(quantitativeParameter);
	        				}
	        			});
	        			typeDetailsDto.setQuantitativeParameters(quanParameters);
	        		}
				});
				processScoreDto.setAdmSelectionProcessTypeDetailsDto(typeDetailsDto);
		}	
			processScoreDto1.add(processScoreDto);
		});
		processScoreDto1.get(0).setDate(date1);
        return processScoreDto1;
     
	}
	
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveGroupScore(Mono<List<AdmSelectionProcessScoreDTO>> dto, String userId) {
		return dto.map(data -> saveGroupScoreDtoToDbo(data,userId))
				.flatMap( s -> { scoreEntryTransaction.save(s);
	               return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
	}

	public List<AdmSelectionProcessScoreDBO> saveGroupScoreDtoToDbo(List<AdmSelectionProcessScoreDTO> dto1, String userId) {
		List<AdmSelectionProcessScoreDBO> data = new ArrayList<AdmSelectionProcessScoreDBO>();	
	    dto1.forEach(dto -> {
	    	AdmSelectionProcessScoreDBO header = new AdmSelectionProcessScoreDBO();
	    	if(!Utils.isNullOrEmpty(dto.getGroupId())) {
		    	header.setAdmSelectionProcessGroupDBO(new AdmSelectionProcessGroupDBO());
		    	header.getAdmSelectionProcessGroupDBO().setId(Integer.parseInt(dto.getGroupId()));
		    	if(dto.getIsAbsent()) {
		    		header.setIsAbsent(dto.getIsAbsent());
		    	}
	    	}
	    	header.setCreatedUsersId(Integer.valueOf(userId));
		    header.setRecordStatus('A');
		    header.setAdmSelectionProcessDBO(new AdmSelectionProcessDBO());
		    header.getAdmSelectionProcessDBO().setId(Integer.parseInt(dto.getSelectionProcessGroupEditDetailsDTO().getSelectionProcessId()));
		    header.setAdmSelectionProcessTypeDetailsDBO(new AdmSelectionProcessTypeDetailsDBO());
		    header.getAdmSelectionProcessTypeDetailsDBO().setId(dto.getAdmSelectionProcessTypeDetailsDto().getId());
		    header.setAdmSelectionProcessScoreEntryDBOSet(new HashSet<AdmSelectionProcessScoreEntryDBO>());
            BigDecimal maxScore = new BigDecimal("000000.00");
		    BigDecimal socreEntered = new BigDecimal("000000.00");
		    AdmSelectionProcessScoreEntryDBO subDbo =  new AdmSelectionProcessScoreEntryDBO();
		    subDbo.setCreatedUsersId(Integer.parseInt(userId));
		    subDbo.setRecordStatus('A');
		    subDbo.setAdmSelectionProcessScoreDBO(header);
		    subDbo.setAdmScoreCardDBO(new AdmScoreCardDBO());
		    subDbo.getAdmScoreCardDBO().setId(Integer.parseInt(dto.getAdmSelectionProcessTypeDetailsDto().getScoreCardId()));
		    subDbo.setErpUsersDBO( new ErpUsersDBO());
		    subDbo.getErpUsersDBO().setId(Integer.parseInt(userId));	
		    Set<AdmSelectionProcessScoreEntryDetailsDBO> admSelectionProcessScoreEntryDetailsDBOUpdate = new HashSet<AdmSelectionProcessScoreEntryDetailsDBO>();
		    if(dto.getIsAbsent() != true) {
			    if(!Utils.isNullOrEmpty(dto.getAdmSelectionProcessTypeDetailsDto().getQuantitativeParameters())) {
			    	for(AdmScoreCardQuantitativeParameterDTO subDetail : dto.getAdmSelectionProcessTypeDetailsDto().getQuantitativeParameters()) {
			    		AdmSelectionProcessScoreEntryDetailsDBO subDbo1 = new AdmSelectionProcessScoreEntryDetailsDBO();
				    	subDbo1.setAdmSelectionProcessScoreEntryDBO(subDbo);
				    	subDbo1.setCreatedUsersId(Integer.parseInt(userId));
				    	subDbo1.setRecordStatus('A');
				    	if(!Utils.isNullOrEmpty(subDetail)) {
				    		subDbo1.setAdmScoreCardQuantitativeParameterDBO(new AdmScoreCardQuantitativeParameterDBO());
							subDbo1.getAdmScoreCardQuantitativeParameterDBO().setId(subDetail.getAdmScoreCardQuantitativeParameterId());
							subDbo1.setQuantitativeParameterMaxScore(BigDecimal.valueOf(subDetail.getMaxValue()));
							subDbo1.setQuantitativeParameterScoreEntered(subDetail.getScoreEntered());
							maxScore = maxScore.add(BigDecimal.valueOf(subDetail.getMaxValue())) ;
							socreEntered = socreEntered.add(subDetail.scoreEntered);
				    	}
				    	admSelectionProcessScoreEntryDetailsDBOUpdate.add(subDbo1);
				    }
			    }
		    }
			subDbo.setMaxScore(maxScore);
			subDbo.setScoreEntered(socreEntered);
			subDbo.setAdmSelectionProcessScoreEntryDetailsDBOSet(admSelectionProcessScoreEntryDetailsDBOUpdate);
			header.getAdmSelectionProcessScoreEntryDBOSet().add(subDbo);
			data.add(header);
	    });
		return data;
	}
	
}