package com.christ.erp.services.dto.common;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCommitteeProgrammeCourseReviewDTO {
	
	private int id;
    private SelectDTO courseStructureReviewer1;
    private SelectDTO courseStructureReviewer2;
    private LocalDate  courseStructureReviewLastDate;
    private List<SelectDTO> erpCommitteeProgrammeCourseReviewDetailsDTOList;

}
