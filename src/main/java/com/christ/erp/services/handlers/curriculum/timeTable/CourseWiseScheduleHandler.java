package com.christ.erp.services.handlers.curriculum.timeTable;

import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.AcaCourseDBO;
import com.christ.erp.services.dbobjects.common.AcaCourseSpecificScheduleDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.timeTable.CourseWiseScheduleDTO;
import com.christ.erp.services.transactions.curriculum.timeTable.CourseWiseScheduleTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@SuppressWarnings("rawtypes")
public class CourseWiseScheduleHandler {

	@Autowired
	CourseWiseScheduleTransaction courseWiseScheduleTransaction;
	
	public Flux<SelectDTO> getCampusesByUser(String userId) {
		return courseWiseScheduleTransaction.getCampusesByUser(userId).flatMapMany(Flux::fromIterable).map(this::convertUserDBOToDTO);
	}

	public SelectDTO convertUserDBOToDTO(Tuple  dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.get("id")));
		dto.setLabel(String.valueOf(dbo.get("campusName")));
		return dto;
	}

	public Flux<SelectDTO> getSession(String yearId) {
		return courseWiseScheduleTransaction.getSession(yearId).flatMapMany(Flux::fromIterable).map(this::convertUserDBOToDTO1);
	}
	
	public SelectDTO convertUserDBOToDTO1(Tuple  dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.get("id")));
		dto.setLabel(String.valueOf(dbo.get("sessionName")));
		return dto;
	}
	
	public Flux<SelectDTO> getCourseNameAndCourseCode(){
		return courseWiseScheduleTransaction.getCourseNameAndCourseCode().flatMapMany(Flux::fromIterable).map(this::convertUserDBOToDTO);
	}
	
	public SelectDTO convertUserDBOToDTO(AcaCourseDBO dbo) {
		SelectDTO dto = new SelectDTO();
		dto.setValue(String.valueOf(dbo.getId()));
		dto.setLabel(String.valueOf(dbo.getCourseName()) + " - "+dbo.getCourseCode());
		return dto;
	}
	
	public Flux<CourseWiseScheduleDTO> getGridData() {
		return courseWiseScheduleTransaction.getGridData().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public CourseWiseScheduleDTO convertDBOToDTO(AcaCourseSpecificScheduleDBO dbo) {
		CourseWiseScheduleDTO dto = new CourseWiseScheduleDTO();
		return dto;
	}
	
	public Mono<CourseWiseScheduleDTO> edit(int timeTableTemplateId) {
		return this.convertDboToDto(courseWiseScheduleTransaction.edit(timeTableTemplateId));
	}

	public Mono<CourseWiseScheduleDTO> convertDboToDto(AcaCourseSpecificScheduleDBO dbo) {
		return null;
	}
	
	public Mono<ApiResult> saveOrUpdate(Mono<CourseWiseScheduleDTO> dto, String userId) {
		return dto.handle((courseWiseScheduleDTO, synchronousSink) -> {
			
				synchronousSink.next(courseWiseScheduleDTO);
			
		}).cast(CourseWiseScheduleDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					if (!Utils.isNullOrEmpty(s.getId())) {
						courseWiseScheduleTransaction.update(s);
					} else {
						courseWiseScheduleTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public AcaCourseSpecificScheduleDBO convertDtoToDbo(CourseWiseScheduleDTO dto, String userId) {
		return null;
	}
	
	public Mono<ApiResult> delete(int id, String userId) {
		return courseWiseScheduleTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}
	
}
