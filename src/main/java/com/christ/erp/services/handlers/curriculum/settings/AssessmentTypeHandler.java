package com.christ.erp.services.handlers.curriculum.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaCourseTypeDBO;
import com.christ.erp.services.dbobjects.common.AttTypeDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentModeDBO;
import com.christ.erp.services.dbobjects.common.ExamAssessmentRatioDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExamAssessmentTemplateAttendanceDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExamAssessmentTemplateDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExamAssessmentTemplateDetailsDBO;
import com.christ.erp.services.dto.common.AttTypeDTO;
import com.christ.erp.services.dto.common.ExamAssessmentCategoryDTO;
import com.christ.erp.services.dto.common.ExamAssessmentModeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.ExamAssessmentTemplateAttendanceDTO;
import com.christ.erp.services.dto.curriculum.settings.ExamAssessmentTemplateDTO;
import com.christ.erp.services.dto.curriculum.settings.ExamAssessmentTemplateDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.settings.AssessmentTypeTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentTypeHandler {
	
	@Autowired
	private AssessmentTypeTransaction assessmentTypeTransaction;

	public Flux<SelectDTO> getRatio() {
		return assessmentTypeTransaction.getRatio().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(ExamAssessmentRatioDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAssessmentRatio());
		}
		return dto;
	}

	public Flux<SelectDTO> getAssessmentCategory(String examType) {
		return assessmentTypeTransaction.getAssessmentCategory(examType).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(ExamAssessmentCategoryDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAssessmentCategory());
		}
		return dto;
	}

	public Flux<SelectDTO> getModeOfExam() {
		return assessmentTypeTransaction.getModeOfExam().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(ExamAssessmentModeDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAssessmentMode());
		}
		return dto;
	}

	public Flux<SelectDTO> getAttendanceType() {
		return assessmentTypeTransaction.getAttendanceType().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public SelectDTO convertDBOToDTO(AttTypeDBO dbo){
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getId()));
			dto.setLabel(dbo.getAttendanceType());
		}
		return dto;
	}

	public Flux<ExamAssessmentTemplateDTO> getGridData() {
		return assessmentTypeTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}
	
	public ExamAssessmentTemplateDTO convertDBOToDTO(Tuple  dbo) {
		ExamAssessmentTemplateDTO dto = new ExamAssessmentTemplateDTO();
		if(!Utils.isNullOrEmpty(dbo)) {
			dto.setId(Integer.parseInt(dbo.get("id").toString()));
			dto.setExamAssessmentRatio(new SelectDTO());
			dto.getExamAssessmentRatio().setValue(dbo.get("rid").toString());
			dto.getExamAssessmentRatio().setLabel(dbo.get("ratioName").toString());
			dto.setTemplateName(dbo.get("tempName").toString());
			dto.setAcaCourseType(new SelectDTO());
			dto.getAcaCourseType().setValue(dbo.get("cTypeId").toString());
			dto.getAcaCourseType().setLabel(dbo.get("cType").toString());
			dto.setMaxTotal(dbo.get("totalMarks").toString());
		    dto.setTotalScaleDownMarks(Integer.parseInt(dbo.get("totalSD").toString()));
		}
		return dto;
	}
	
    @SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
        return assessmentTypeTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }

	public Mono<ExamAssessmentTemplateDTO> edit(int id) {
		ExamAssessmentTemplateDBO data = assessmentTypeTransaction.edit(id);
		return this.convertDboToDto(data);
	}
	
	public Mono<ExamAssessmentTemplateDTO> convertDboToDto(ExamAssessmentTemplateDBO dbo) {
		ExamAssessmentTemplateDTO dto = new ExamAssessmentTemplateDTO();
		int[] eseAssessment = {0};
		dto.setId(dbo.getId());
		dto.setExamAssessmentRatio(new SelectDTO());
		dto.getExamAssessmentRatio().setValue(String.valueOf(dbo.getExamAssessmentRatioDBO().getId()));
		dto.getExamAssessmentRatio().setLabel(dbo.getExamAssessmentRatioDBO().getAssessmentRatio());
		dto.setAcaCourseType(new SelectDTO());
		dto.getAcaCourseType().setValue(String.valueOf(dbo.getAcaCourseTypeDBO().getId()));
		dto.getAcaCourseType().setLabel(dbo.getAcaCourseTypeDBO().getCourseType());
		dto.setTemplateName(dbo.getTemplateName());
		if(!Utils.isNullOrEmpty(dbo.getCiaTotalMarks())) {
			dto.setCiaCheck(true);
			dto.setCiaTotalMarks(dbo.getCiaTotalMarks());
			dto.setCiaMinMarks(dbo.getCiaMinMarks());
			dto.setCiaScaleDownTo(dbo.getCiaScaleDownTo());
			dto.setAttendanceTotal(dbo.getAttendanceTotal());
		}
		if(!Utils.isNullOrEmpty(dbo.getEseTotalMarks())) {
			dto.setEseCheck(true);
			dto.setEseTotalMarks(dbo.getEseTotalMarks());
			dto.setEseMinMarks(dbo.getEseMinMarks());
			dto.setEseScaleDownTo(dbo.getEseScaleDownTo());
		}
		dto.setTotalScaleDownMarks(dbo.getTotalScaleDownMarks());
		dto.setTotalMinMarks(dbo.getTotalMinMarks());
		Map<Integer,ExamAssessmentTemplateAttendanceDTO> attendanceMap = new HashMap<Integer, ExamAssessmentTemplateAttendanceDTO>();
		if(!Utils.isNullOrEmpty(dbo.getExamAssessmentTemplateAttendanceDBOSet())) {
			dbo.getExamAssessmentTemplateAttendanceDBOSet().forEach(attendanceDbo -> {
				if(!attendanceMap.containsKey(attendanceDbo.getExamAssessmentTemplateDBO().getId())) {
					ExamAssessmentTemplateAttendanceDTO tempAttendanceDTO = new ExamAssessmentTemplateAttendanceDTO();
					tempAttendanceDTO.setAttTypeDTO(new ArrayList<AttTypeDTO>());
					AttTypeDTO attType = new AttTypeDTO();
					attType.setId(attendanceDbo.getAttTypeDBO().getId());
					attType.setAttendanceType(attendanceDbo.getAttTypeDBO().getAttendanceType());
					tempAttendanceDTO.getAttTypeDTO().add(attType);
					attendanceMap.put(attendanceDbo.getExamAssessmentTemplateDBO().getId(), tempAttendanceDTO);
				} else {
					ExamAssessmentTemplateAttendanceDTO tempAttendanceDTO = attendanceMap.get(attendanceDbo.getExamAssessmentTemplateDBO().getId());
					AttTypeDTO attType = new AttTypeDTO();
					attType.setId(attendanceDbo.getAttTypeDBO().getId());
					attType.setAttendanceType(attendanceDbo.getAttTypeDBO().getAttendanceType());
					tempAttendanceDTO.getAttTypeDTO().add(attType);
					attendanceMap.replace(attendanceDbo.getExamAssessmentTemplateDBO().getId(), tempAttendanceDTO);
				}
			});
			ExamAssessmentTemplateAttendanceDTO tempAttendanceDTO = attendanceMap.get(dbo.getId());
			dto.setExamAssessmentTemplateAttendanceDTO(tempAttendanceDTO);
		}	
		List<ExamAssessmentTemplateDetailsDTO> listUpdate = new ArrayList<ExamAssessmentTemplateDetailsDTO>();
		if(!Utils.isNullOrEmpty(dbo.getExamAssessmentTemplateDetailsDBOSet())) {
			dbo.getExamAssessmentTemplateDetailsDBOSet().forEach(detailsDBO -> {
				ExamAssessmentTemplateDetailsDTO detailsDto = new ExamAssessmentTemplateDetailsDTO();
				detailsDto.setId(detailsDBO.getId());
				detailsDto.setExamAssessmentCategoryDTO(new ExamAssessmentCategoryDTO());
				detailsDto.getExamAssessmentCategoryDTO().setId(detailsDBO.getExamAssessmentCategoryDBO().getId());
				detailsDto.getExamAssessmentCategoryDTO().setExamAssessmentType(detailsDBO.getExamAssessmentCategoryDBO().getExamAssessmentType());
				detailsDto.getExamAssessmentCategoryDTO().setAssessmentCategory(detailsDBO.getExamAssessmentCategoryDBO().getAssessmentCategory());
				detailsDto.setCategoryOrder(detailsDBO.getCategoryOrder());
				if(!Utils.isNullOrEmpty(detailsDBO.getCategoryCiaTotalMarks()))  {
					detailsDto.setCategoryCiaTotalMarks(detailsDBO.getCategoryCiaTotalMarks());
					detailsDto.setCategoryCiaMinMarks(detailsDBO.getCategoryCiaMinMarks());
					detailsDto.setCategoryCiaScaleDownTo(detailsDBO.getCategoryCiaScaleDownTo());
				} 
				detailsDto.setExamAssessmentModeDTO(new ExamAssessmentModeDTO());
				detailsDto.getExamAssessmentModeDTO().setId(detailsDBO.getExamAssessmentModeDBO().getId());
				detailsDto.getExamAssessmentModeDTO().setAssessmentMode(detailsDBO.getExamAssessmentModeDBO().getAssessmentMode());
				if(!Utils.isNullOrEmpty(detailsDBO.getIsQPfromDB())) {
					detailsDto.setQPfromDB(detailsDBO.getIsQPfromDB());
				}
				if(!Utils.isNullOrEmpty(detailsDBO.getDurationOfExam())) {
					detailsDto.setDurationOfExam(detailsDBO.getDurationOfExam());
				}
				if(!Utils.isNullOrEmpty(detailsDBO.getNoOfEvaluators())) {
					detailsDto.setNoOfEvaluators(detailsDBO.getNoOfEvaluators());
				}
				if(!Utils.isNullOrEmpty(detailsDBO.getCategoryEseTotalMarks())) {
					eseAssessment[0]++;
				    detailsDto.setCategoryEseTotalMarks(detailsDBO.getCategoryEseTotalMarks());
					detailsDto.setCategoryEseMinMarks(detailsDBO.getCategoryEseMinMarks());
					detailsDto.setCategoryEseScaleDownTo(detailsDBO.getCategoryEseScaleDownTo());
				}
				listUpdate.add(detailsDto);
			});
		}
		listUpdate.sort(Comparator.comparing(ExamAssessmentTemplateDetailsDTO::getCategoryOrder));
		dto.setExamAssessmentTemplateDetailsDTO(listUpdate);
		dto.setNoOfAssessment(eseAssessment[0]);
		return Mono.just(dto);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ExamAssessmentTemplateDTO> dto, String userId) {
		return dto.handle((examAssessmentTemplateDTO, synchronousSink) ->  {
			boolean list = assessmentTypeTransaction.duplicateCheck(examAssessmentTemplateDTO.getId(),examAssessmentTemplateDTO.getExamAssessmentRatio().getValue()).stream().anyMatch( obj -> examAssessmentTemplateDTO.getTemplateName().replaceAll("\\s", "").equalsIgnoreCase(obj.replaceAll("\\s", "")));
			if(list) {
				synchronousSink.error(new DuplicateException("Already same Template Name is exist for selected Ratio"));
			} else {
				synchronousSink.next(examAssessmentTemplateDTO);
			}
		}).cast(ExamAssessmentTemplateDTO.class)
		  .map(data -> convertDtoToDbo(data, userId))
		  .flatMap( s -> {
	    	  if (!Utils.isNullOrEmpty(s.getId())) {
	    		  assessmentTypeTransaction.update(s);
              } else {
            	  assessmentTypeTransaction.save(s);
              }
		  return Mono.just(Boolean.TRUE);
	      }).map(Utils::responseResult);
	}
	
	public ExamAssessmentTemplateDBO convertDtoToDbo(ExamAssessmentTemplateDTO dto, String userId) {
		ExamAssessmentTemplateDBO header ;
		if(!Utils.isNullOrEmpty(dto.getId())) {
		    header =  assessmentTypeTransaction.edit(dto.getId());
	    } else {
	    	header = new ExamAssessmentTemplateDBO();
	    }
		header.setExamAssessmentRatioDBO(new ExamAssessmentRatioDBO());
		header.getExamAssessmentRatioDBO().setId(Integer.parseInt(dto.getExamAssessmentRatio().getValue()));
		header.setAcaCourseTypeDBO(new AcaCourseTypeDBO());
		header.getAcaCourseTypeDBO().setId(Integer.parseInt(dto.getAcaCourseType().getValue()));
		header.setTemplateName(dto.getTemplateName());
		header.setCiaTotalMarks(dto.getCiaTotalMarks());
		header.setCiaMinMarks(dto.getCiaMinMarks());
		header.setCiaScaleDownTo(dto.getCiaScaleDownTo());
		header.setAttendanceTotal(dto.getAttendanceTotal());
		header.setEseTotalMarks(dto.getEseTotalMarks());
		header.setEseMinMarks(dto.getEseMinMarks());
		header.setEseScaleDownTo(dto.getEseScaleDownTo());
		header.setTotalScaleDownMarks(dto.getTotalScaleDownMarks());
		header.setTotalMinMarks(dto.getTotalMinMarks());
		header.setRecordStatus('A');
		header.setCreatedUsersId(Integer.parseInt(userId));
		if(!Utils.isNullOrEmpty(dto.getId())) {
			header.setModifiedUsersId(Integer.parseInt(userId));
		}
		Set<ExamAssessmentTemplateAttendanceDBO> examAssessmentTemplateAttendanceDBOSet = !Utils.isNullOrEmpty(header.getExamAssessmentTemplateAttendanceDBOSet()) ?  header.getExamAssessmentTemplateAttendanceDBOSet() : null;
		Map<Integer,ExamAssessmentTemplateAttendanceDBO> exmExistDBOMap = new HashMap<Integer, ExamAssessmentTemplateAttendanceDBO>();
		if(!Utils.isNullOrEmpty(examAssessmentTemplateAttendanceDBOSet)) {
			examAssessmentTemplateAttendanceDBOSet.forEach( attDboSet -> {
				if(attDboSet.getExamAssessmentTemplateDBO().getId() == dto.getId() && attDboSet.getRecordStatus() == 'A') {
					exmExistDBOMap.put(attDboSet.getAttTypeDBO().getId(), attDboSet);
				}
			});
		}
		Set<ExamAssessmentTemplateAttendanceDBO> examAssessmentTemplateAttendanceSet = new HashSet<ExamAssessmentTemplateAttendanceDBO>();
		dto.getExamAssessmentTemplateAttendanceDTO().getAttTypeDTO().forEach( attType -> {
			ExamAssessmentTemplateAttendanceDBO tempAttendance = null;
			if(exmExistDBOMap.containsKey(attType.getId())) {
				tempAttendance = exmExistDBOMap.get(attType.getId());
				tempAttendance.setModifiedUsersId(Integer.parseInt(userId));
				exmExistDBOMap.remove(attType.getId());
			} else {
				tempAttendance = new ExamAssessmentTemplateAttendanceDBO();
				tempAttendance.setAttTypeDBO(new AttTypeDBO());
				tempAttendance.getAttTypeDBO().setId(attType.getId());
				tempAttendance.setCreatedUsersId(Integer.parseInt(userId));
			}
			tempAttendance.setExamAssessmentTemplateDBO(header);
			tempAttendance.setRecordStatus('A');
			examAssessmentTemplateAttendanceSet.add(tempAttendance);
		});
		if (!Utils.isNullOrEmpty(exmExistDBOMap)) {
			exmExistDBOMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				examAssessmentTemplateAttendanceSet.add(value);
			});
		}
		header.setExamAssessmentTemplateAttendanceDBOSet(examAssessmentTemplateAttendanceSet);
		Set<ExamAssessmentTemplateDetailsDBO> ExamAssessmentTemplateDetailsDBOSet = !Utils.isNullOrEmpty(header.getExamAssessmentTemplateDetailsDBOSet()) ?  header.getExamAssessmentTemplateDetailsDBOSet() : null;
		Map<Integer,ExamAssessmentTemplateDetailsDBO> tempDetailsDBOMap = new HashMap<Integer, ExamAssessmentTemplateDetailsDBO>();
		if(!Utils.isNullOrEmpty(ExamAssessmentTemplateDetailsDBOSet)) {
			ExamAssessmentTemplateDetailsDBOSet.forEach( tempDetails -> {
				if(tempDetails.getRecordStatus() == 'A') {
					tempDetailsDBOMap.put(tempDetails.getId(), tempDetails);
				}
			});
		}
		Set<ExamAssessmentTemplateDetailsDBO> examAssessmentTemplateDetailsSet = new HashSet<ExamAssessmentTemplateDetailsDBO>();
		int[] count = {0};
		dto.getExamAssessmentTemplateDetailsDTO().forEach( detailsDto -> {
			ExamAssessmentTemplateDetailsDBO detailsDbo = null;
			if(tempDetailsDBOMap.containsKey(detailsDto.getId())) {
				detailsDbo = tempDetailsDBOMap.get(detailsDto.getId());
				detailsDbo.setModifiedUsersId(Integer.parseInt(userId));
				tempDetailsDBOMap.remove(detailsDto.getId());
			} else {
				 detailsDbo = new ExamAssessmentTemplateDetailsDBO();
				 detailsDbo.setCreatedUsersId(Integer.parseInt(userId));
			}
			detailsDbo.setExamAssessmentTemplateDBO(header);
			detailsDbo.setExamAssessmentCategoryDBO(new ExamAssessmentCategoryDBO());
			detailsDbo.getExamAssessmentCategoryDBO().setId(detailsDto.getExamAssessmentCategoryDTO().getId());
			count[0]++;
			detailsDbo.setCategoryOrder(count[0]);
			if(!Utils.isNullOrEmpty(detailsDto.getCategoryCiaTotalMarks())) {
				detailsDbo.setCategoryCiaTotalMarks(detailsDto.getCategoryCiaTotalMarks());
				if(Utils.isNullOrEmpty(dto.getCiaScaleDownTo())) {
					detailsDbo.setCategoryCiaScaleDownTo(detailsDto.getCategoryCiaScaleDownTo());
				} else {
					detailsDbo.setCategoryCiaScaleDownTo(null);
				}
				if(Utils.isNullOrEmpty(dto.getCiaMinMarks())) {
					detailsDbo.setCategoryCiaMinMarks(detailsDto.getCategoryCiaMinMarks());
				} else {
					detailsDbo.setCategoryCiaMinMarks(null);
				}
			} else {
				detailsDbo.setCategoryEseTotalMarks(detailsDto.getCategoryEseTotalMarks());
				if(Utils.isNullOrEmpty(dto.getEseScaleDownTo())) {
					detailsDbo.setCategoryEseScaleDownTo(detailsDto.getCategoryEseScaleDownTo());
				} else {
					detailsDbo.setCategoryEseScaleDownTo(null);
				}
				if(Utils.isNullOrEmpty(dto.getEseMinMarks())) {
					detailsDbo.setCategoryEseMinMarks(detailsDto.getCategoryEseMinMarks());
				} else {
					detailsDbo.setCategoryEseMinMarks(null);
				}
			}
			detailsDbo.setExamAssessmentModeDBO(new ExamAssessmentModeDBO()); 
			detailsDbo.getExamAssessmentModeDBO().setId(detailsDto.getExamAssessmentModeDTO().getId());
			detailsDbo.setIsQPfromDB(detailsDto.isQPfromDB());
			if(detailsDto.isQPfromDB()) {
				detailsDbo.setIsQPfromDB(detailsDto.isQPfromDB());
	    	}
			if(!Utils.isNullOrEmpty(detailsDto.getDurationOfExam())) {
				detailsDbo.setDurationOfExam(detailsDto.getDurationOfExam());
			}
			detailsDbo.setNoOfEvaluators(detailsDto.getNoOfEvaluators());
			detailsDbo.setRecordStatus('A');
			examAssessmentTemplateDetailsSet.add(detailsDbo);
		});
		if(!Utils.isNullOrEmpty(tempDetailsDBOMap)) {
			tempDetailsDBOMap.forEach((entry2, value2)-> {
				value2.setModifiedUsersId( Integer.parseInt(userId));
				value2.setRecordStatus('D');
				examAssessmentTemplateDetailsSet.add(value2);
			});
		}
		header.setExamAssessmentTemplateDetailsDBOSet(examAssessmentTemplateDetailsSet);
		return header;
	}

}