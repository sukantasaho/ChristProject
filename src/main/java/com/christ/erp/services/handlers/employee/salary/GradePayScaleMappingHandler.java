package com.christ.erp.services.handlers.employee.salary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleLevelDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDetailDTO;
import com.christ.erp.services.transactions.employee.salary.GradePayScaleMappingTranscation;

public class GradePayScaleMappingHandler {
	private static volatile GradePayScaleMappingHandler gradePayScaleMappingHandler = null;
	GradePayScaleMappingTranscation gradePayScaleMappingTranscation = GradePayScaleMappingTranscation.getInstance();

	public static GradePayScaleMappingHandler getInstance() {
		if (gradePayScaleMappingHandler == null) {
			gradePayScaleMappingHandler = new GradePayScaleMappingHandler();
		}
		return gradePayScaleMappingHandler;
	}

	public List<EmpPayScaleGradeMappingDTO> getGridData() {
		List<EmpPayScaleGradeMappingDTO> empPayScaleGradeMappingDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = gradePayScaleMappingTranscation.getGridData();
			for (Tuple tuple : list) {
				EmpPayScaleGradeMappingDTO gridDTO = new EmpPayScaleGradeMappingDTO();
				gridDTO.id = tuple.get("ID").toString();
				gridDTO.revisedYear = new ExModelBaseDTO();
				gridDTO.revisedYear.text = tuple.get("RevisedYear").toString();
				gridDTO.category = new ExModelBaseDTO();
				gridDTO.category.text = tuple.get("CategoryName").toString();
				gridDTO.grade = new ExModelBaseDTO();
				gridDTO.grade.text = tuple.get("GradeName").toString();
				empPayScaleGradeMappingDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empPayScaleGradeMappingDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public ApiResult<ModelBaseDTO> saveOrUpdateGradePayScaleMapping(EmpPayScaleGradeMappingDTO data, String userId)
			throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		EmpPayScaleGradeMappingDBO header = null;
		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = gradePayScaleMappingTranscation.getEmpPayScaleGradeMappingDBO(Integer.parseInt(data.id));
		}
		Boolean isDuplicate = gradePayScaleMappingTranscation.isDuplicate(data.revisedYear.text, data.grade.id,
				data.category.id, data.id);
		if (!isDuplicate) {
			if (header == null) {
				header = new EmpPayScaleGradeMappingDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			header.empPayScaleGradeDBO = new EmpPayScaleGradeDBO();
			header.empPayScaleGradeDBO.id = Integer.parseInt(data.grade.id);
			header.payScaleRevisedYear = Integer.parseInt(data.revisedYear.text);
			header.recordStatus = 'A';
			
			if (header.id != null) {
				header.modifiedUsersId = Integer.parseInt(userId);
			}
			gradePayScaleMappingTranscation.saveOrUpdate(header);
			if (header.id != 0) {
				List<Integer> detailIds = new ArrayList<>();
				for (EmpPayScaleGradeMappingDetailDTO item : data.levels) {
					EmpPayScaleGradeMappingDetailDBO detail = null;
					if (Utils.isNullOrWhitespace(item.id) == false) {
						detail = gradePayScaleMappingTranscation
								.getEmpPayScaleGradeMappingDetailDBO(Integer.parseInt(item.id));
					}
					if (detail == null) {
						detail = new EmpPayScaleGradeMappingDetailDBO();
						detail.createdUsersId = Integer.parseInt(userId);
					}
					if(!Utils.isNullOrEmpty(item.getEmpPayScaleLevel())) {
					detail.setEmpPayScaleLevelDBO(new EmpPayScaleLevelDBO());
					detail.getEmpPayScaleLevelDBO().setId(Integer.parseInt(item.getEmpPayScaleLevel().getValue()));
					detail.getEmpPayScaleLevelDBO().setEmpPayScaleLevel(item.getEmpPayScaleLevel().getLabel());
					}
					detail.payScaleDisplayOrder = Integer.parseInt(item.displayOrder);
					detail.payScale = item.payScale;
					detail.recordStatus = 'A';
					detail.empPayScaleGradeMappingDBO = header;
					if (detail.id != null) {
						detail.modifiedUsersId = Integer.parseInt(userId);
					}
					gradePayScaleMappingTranscation.saveOrUpdate(detail);
					detailIds.add(detail.id);
				}
				gradePayScaleMappingTranscation.deleteSubRecord(header.id, detailIds);
				result.success = true;
				result.dto = new ModelBaseDTO();
				result.dto.id = String.valueOf(header.id);
			}
		} else {
			result.failureMessage = "Duplicate record exists with Revised Year: '" + data.revisedYear.text
					+ "'  , Employee Category: '" + data.category.text + "' and Grade:'" + data.grade.text + "'.";
			result.success = false;
		}
		return result;
	}

	public EmpPayScaleGradeMappingDTO selectPayScaleGradeMapping(String id) {
		EmpPayScaleGradeMappingDTO empPayScaleGradeMappingDTO = new EmpPayScaleGradeMappingDTO();
		try {
			EmpPayScaleGradeMappingDBO payScaleGradeMapping = gradePayScaleMappingTranscation
					.getEmpPayScaleGradeMappingDBO(Integer.parseInt(id));
			if (payScaleGradeMapping != null) {
				empPayScaleGradeMappingDTO = new EmpPayScaleGradeMappingDTO();
				empPayScaleGradeMappingDTO.id = String.valueOf(payScaleGradeMapping.id);
				empPayScaleGradeMappingDTO.revisedYear = new ExModelBaseDTO();
				empPayScaleGradeMappingDTO.revisedYear.text = payScaleGradeMapping.payScaleRevisedYear.toString();
				empPayScaleGradeMappingDTO.category = new ExModelBaseDTO();
				empPayScaleGradeMappingDTO.category.id = String
						.valueOf(payScaleGradeMapping.empPayScaleGradeDBO.empEmployeeCategoryDBO.id);
				empPayScaleGradeMappingDTO.grade = new ExModelBaseDTO();
				empPayScaleGradeMappingDTO.grade.id = String.valueOf(payScaleGradeMapping.empPayScaleGradeDBO.id);
				empPayScaleGradeMappingDTO.levels = new ArrayList<>();
				if (payScaleGradeMapping.empPayScaleGradeMappingDetailDBOSet != null
						&& payScaleGradeMapping.empPayScaleGradeMappingDetailDBOSet.size() > 0) {
					for (EmpPayScaleGradeMappingDetailDBO item : payScaleGradeMapping.empPayScaleGradeMappingDetailDBOSet) {
						EmpPayScaleGradeMappingDetailDTO levelInfo = new EmpPayScaleGradeMappingDetailDTO();
						if (item.recordStatus == 'A') {
							levelInfo.id = String.valueOf(item.id);
							levelInfo.payScale = item.payScale.toString();
							if(!Utils.isNullOrEmpty(item.getEmpPayScaleLevelDBO())) {
							levelInfo.setEmpPayScaleLevel(new SelectDTO());
							levelInfo.getEmpPayScaleLevel().setValue(item.getEmpPayScaleLevelDBO().getId().toString());
							levelInfo.getEmpPayScaleLevel().setLabel(item.getEmpPayScaleLevelDBO().getEmpPayScaleLevel());
							}
							levelInfo.displayOrder = item.payScaleDisplayOrder.toString();
							empPayScaleGradeMappingDTO.levels.add(levelInfo);
						}
					}
					Collections.sort(empPayScaleGradeMappingDTO.levels);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empPayScaleGradeMappingDTO;
	}

	public boolean deletePayScaleGradeMapping(String id, String userId) {
		try {
			EmpPayScaleGradeMappingDBO empPayScaleGradeMappingDBO = gradePayScaleMappingTranscation
					.getEmpPayScaleGradeMappingDBO(Integer.parseInt(id));
			if (empPayScaleGradeMappingDBO != null) {
				empPayScaleGradeMappingDBO.recordStatus = 'D';
				empPayScaleGradeMappingDBO.modifiedUsersId = Integer.parseInt(userId);
				for (EmpPayScaleGradeMappingDetailDBO item : empPayScaleGradeMappingDBO.empPayScaleGradeMappingDetailDBOSet) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
					gradePayScaleMappingTranscation.saveOrUpdate(item);
				}
				if (empPayScaleGradeMappingDBO.id != null) {
					return gradePayScaleMappingTranscation.saveOrUpdate(empPayScaleGradeMappingDBO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
