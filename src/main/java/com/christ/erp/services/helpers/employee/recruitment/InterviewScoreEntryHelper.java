package com.christ.erp.services.helpers.employee.recruitment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryGroupHeadDTO;
import com.christ.erp.services.dto.employee.recruitment.InteviewScoreEntryGroupDetailsDTO;

@Service
public class InterviewScoreEntryHelper {

	public static volatile InterviewScoreEntryHelper interviewScoreEntryHelper = null;
	public static InterviewScoreEntryHelper getInstance() {
		if(interviewScoreEntryHelper== null) 
			interviewScoreEntryHelper =  new InterviewScoreEntryHelper();
		return interviewScoreEntryHelper;
	}
	
	public void getInterviewTemplate (List<Tuple> list,ApiResult<InterviewScoreEntryDTO> resultDto, Boolean isEdit) {	
		resultDto.success = true;
		resultDto.dto.groupHeadMap = new HashMap<Integer, InterviewScoreEntryGroupHeadDTO>();
		Map<Integer, String> scores =  new HashMap<Integer, String>();
		int totalScore = 0;
		if(!Utils.isNullOrEmpty(list)) {
			for (Tuple tuple : list) {
				String  headingId = tuple.get("group_id").toString();
				String  detailId = tuple.get("detail_id").toString();
				String  templateName = tuple.get("interview_name").toString();
				String  headingname = tuple.get("group_heading").toString();
				String  detailName = tuple.get("detail_parameter").toString();
				Integer headingOrder = Integer.parseInt(tuple.get("group_order").toString());
				Integer detailsOrder = Integer.parseInt(tuple.get("detail_order").toString());
				String  commentRequired = tuple.get("comment_required").toString();
				Integer maxScore = Integer.parseInt(tuple.get("detail_max_score").toString());
				String  obtainedScore ="";
				String  comments = "";
				String detailsEntryId = "";
				String scoreEntryId = "";
				if(tuple.get("comment_required").toString() != null && tuple.get("comment_required").toString().equals("true")) {
					if(isEdit.equals(true)) {
					if(tuple.get("comments") == null || tuple.get("comments").toString()== null) {
						comments = "";
					}else {
						comments = tuple.get("comments").toString();
					}
				}else {
					comments = "";
				 }
				}
				try {
					obtainedScore = tuple.get("obtained_score").toString();
					detailsEntryId = tuple.get("details_id").toString();
					scoreEntryId = tuple.get("score_entry_id").toString();
					scores.put(Integer.parseInt(detailId), obtainedScore);
				} catch (Exception e) {
					obtainedScore = null;
					detailsEntryId = null;
					scoreEntryId = null;
				}
				if(resultDto.dto.groupHeadMap.containsKey(Integer.parseInt(headingId))) {
					InterviewScoreEntryGroupHeadDTO  interveiwScoreEntryGroupHeadDTO = resultDto.dto.groupHeadMap.get(Integer.parseInt(headingId));
					InteviewScoreEntryGroupDetailsDTO detailto = new InteviewScoreEntryGroupDetailsDTO();
					if(detailsEntryId != null && !detailsEntryId.isEmpty()) {
						detailto.id = detailsEntryId;
					}
					detailto.groupDetailId = detailId;
					detailto.parameterName = detailName;
					detailto.parameterMaxScore = maxScore;
					totalScore = totalScore + maxScore;
					detailto.parameterOrderNumber = detailsOrder;
					if(obtainedScore != null && !obtainedScore.isEmpty())
						detailto.obtainedScore = Integer.parseInt(obtainedScore);
					interveiwScoreEntryGroupHeadDTO.groupDetailsMap.put(Integer.parseInt(detailId), detailto);
					resultDto.dto.groupHeadMap.put(Integer.parseInt(headingId), interveiwScoreEntryGroupHeadDTO);
				}else {
					if(scoreEntryId != null && !scoreEntryId.isEmpty()) {
						resultDto.dto.id = scoreEntryId;
					}
					resultDto.dto.commentRequired = commentRequired;
					resultDto.dto.templateName = templateName;
					if(commentRequired != null && !commentRequired.isEmpty()) {
						resultDto.dto.comments = comments;
					}				
					InterviewScoreEntryGroupHeadDTO headingTO = new InterviewScoreEntryGroupHeadDTO();
					headingTO.templateGroupHeading = headingname;
					headingTO.id = headingId;
					headingTO.headingOrderNo = headingOrder;				
					InteviewScoreEntryGroupDetailsDTO detailto = new InteviewScoreEntryGroupDetailsDTO();
					if(detailsEntryId != null && !detailsEntryId.isEmpty()) {
						detailto.id = detailsEntryId;
					}
					detailto.groupDetailId = detailId;
					detailto.parameterName = detailName;
					detailto.parameterMaxScore = maxScore;
					totalScore = totalScore + maxScore;
					detailto.parameterOrderNumber = detailsOrder;
					if(obtainedScore != null && !obtainedScore.isEmpty())
						detailto.obtainedScore = Integer.parseInt(obtainedScore);
					headingTO.groupDetailsMap = new HashMap<Integer, InteviewScoreEntryGroupDetailsDTO>(); 
					headingTO.groupDetailsMap.put(Integer.parseInt(detailId), detailto);
					resultDto.dto.groupHeadMap.put(Integer.parseInt(headingId), headingTO);
				}
				resultDto.dto.empApplnEntryId = resultDto.dto.empApplnEntryId;
				resultDto.dto.applicationNumber = resultDto.dto.applicationNumber;
				resultDto.dto.applicantName = resultDto.dto.applicantName;
			}
		}
		resultDto.dto.totalMaxScore = totalScore;
		// To sort the elements in the order of headingorder
		Map<Integer, InterviewScoreEntryGroupHeadDTO> sortedNewMap = resultDto.dto.groupHeadMap
				.entrySet().stream().sorted((e1, e2) -> e1.getValue().headingOrderNo
						.compareTo(e2.getValue().headingOrderNo))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
						LinkedHashMap::new));
		sortedNewMap.forEach((key, val) -> {
		});
		resultDto.dto.groupHeadMap = sortedNewMap;
		
		// To sort the elements in the order of paramentorder
		for (Entry<Integer, InterviewScoreEntryGroupHeadDTO> heading : resultDto.dto.groupHeadMap.entrySet()) {
			
			Map<Integer, InteviewScoreEntryGroupDetailsDTO> sortedNewHeadingMap = heading.getValue().groupDetailsMap
					.entrySet().stream()
					.sorted((e1, e2) -> e1.getValue().parameterOrderNumber
							.compareTo(e2.getValue().parameterOrderNumber))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
							LinkedHashMap::new));
			sortedNewHeadingMap.forEach((key, val) -> {
			});
			heading.getValue().groupDetailsMap = sortedNewHeadingMap;
			resultDto.dto.groupHeadMap = sortedNewMap;
		}
	}
}
