package com.christ.erp.services.dto.employee.recruitment;

import java.util.Map;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author diwakar.d
 *
 */
@Getter
@Setter

public class InterviewScoreEntryDTO extends ModelBaseDTO{
	
	public String applicationNumber;
	public SelectDTO interviewPanelMember;
	public String applicantName;
	public Map<Integer,InterviewScoreEntryGroupHeadDTO> groupHeadMap;
	public String commentRequired;
	public String templateName;
	public Integer empApplnEntryId;
	public String comments;
	public String categoryId;
	public Integer userId;
	public Integer totalMaxScore;
	private boolean isInternal;
	private boolean isEditable;
	private Integer totalScore;
}
