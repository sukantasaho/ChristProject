package com.christ.erp.services.handlers.employee.recruitment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDepartmentDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.SubjectCategoryDepartmentDTO;
import com.christ.erp.services.transactions.employee.recruitment.SubjectCategoryDepartmentTranscaction;

public class SubjectCategoryDepartmentHandler {
	private static volatile SubjectCategoryDepartmentHandler subjectCategoryDepartmentHandler = null;
	SubjectCategoryDepartmentTranscaction subjectCategoryDepartmentTranscaction = SubjectCategoryDepartmentTranscaction
			.getInstance();

	public static SubjectCategoryDepartmentHandler getInstance() {
		if (subjectCategoryDepartmentHandler == null) {
			subjectCategoryDepartmentHandler = new SubjectCategoryDepartmentHandler();
		}
		return subjectCategoryDepartmentHandler;
	}

	public List<SubjectCategoryDepartmentDTO> getGridData() {
		List<SubjectCategoryDepartmentDTO> subjectCategoryDepartmentDTO = new ArrayList<>();
	    List<Tuple> list;
		try {
			list = subjectCategoryDepartmentTranscaction.getGridData();
			for (Tuple tuple : list) {
				SubjectCategoryDepartmentDTO gridDTO = new SubjectCategoryDepartmentDTO();
		        gridDTO.setCategory(new SelectDTO());
				gridDTO.getCategory().setValue(String.valueOf(tuple.get("emp_appln_subject_category_id"))); 
				gridDTO.getCategory().setLabel( String.valueOf(tuple.get("categoryName")));
				gridDTO.setErpDepartment(new SelectDTO());
				gridDTO.getErpDepartment().setLabel(String.valueOf(tuple.get("department_name")));
				subjectCategoryDepartmentDTO.add(gridDTO);
				}
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		return subjectCategoryDepartmentDTO;
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes"})
	public ApiResult<ModelBaseDTO> saveOrUpdateSubjectDepartment(SubjectCategoryDepartmentDTO data, String userId)
			throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		EmpApplnSubjectCategoryDepartmentDBO header = null;
		List<EmpApplnSubjectCategoryDepartmentDBO> subcategorydeparts = null;

		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = subjectCategoryDepartmentTranscaction
					.getEmpApplnSubjectCategoryDepartmentDBO(Integer.parseInt(data.id));
		}
		Boolean isduplicate = subjectCategoryDepartmentTranscaction.isDuplicate(data.recordIds, data.category.value);
		if (Utils.isNullOrWhitespace(data.id) == true) {
			if (isduplicate) {
				result.failureMessage = " Duplicate entry for subject:" + data.category.label;
				result.success = false;
			} else {
				for (ExModelBaseDTO model : data.department) {
					header = new EmpApplnSubjectCategoryDepartmentDBO();
					header.subject = new EmpApplnSubjectCategoryDBO();
					header.subject.id = Integer.parseInt(data.category.value);
					header.department = new ErpDepartmentDBO();
					header.department.id = Integer.parseInt(model.id);
					header.recordStatus = 'A';
					subjectCategoryDepartmentTranscaction.saveOrUpdate(header);
				}
				result.success = true;
			} 
		} else {
			if (isduplicate) {
				result.failureMessage = " Duplicate entry for subject:" + data.category.label;
				result.success = false;
			} else {
				if(Utils.isNullOrWhitespace(data.id) == false) {
					subcategorydeparts = subjectCategoryDepartmentTranscaction.isSave(data.category.value);
				}
				if (subcategorydeparts.size() > 0) {
					Map<Integer, Integer> departMap = new HashMap<Integer, Integer>();
					Map<Integer, Integer> subDeptMisMatched = new HashMap<Integer, Integer>();
					for (ExModelBaseDTO model : data.department) {
						departMap.put(Integer.parseInt(model.id), Integer.parseInt(model.id));
					}
					for (EmpApplnSubjectCategoryDepartmentDBO empcatgorydepart : subcategorydeparts) {
						Integer depId = empcatgorydepart.department.id;
						if (departMap.containsKey(depId)) {
							departMap.remove(depId);
						} else {
							subDeptMisMatched.put(empcatgorydepart.id, depId);
						}
					}
					for (Entry<Integer, Integer> map : subDeptMisMatched.entrySet()) {
						EmpApplnSubjectCategoryDepartmentDBO sdm = new EmpApplnSubjectCategoryDepartmentDBO();
						sdm.id = map.getKey();
						sdm.recordStatus = 'D';
						sdm.department = new ErpDepartmentDBO();
						sdm.department.id = map.getValue();
						sdm.subject = new EmpApplnSubjectCategoryDBO();
						sdm.subject.id = Integer.parseInt(data.category.value);
						subjectCategoryDepartmentTranscaction.saveOrUpdate(sdm);
					}
					for (Entry<Integer, Integer> map : departMap.entrySet()) {
						EmpApplnSubjectCategoryDepartmentDBO newobj = new EmpApplnSubjectCategoryDepartmentDBO();
						newobj.subject = new EmpApplnSubjectCategoryDBO();
						newobj.subject.id = Integer.parseInt(data.category.value);
						newobj.department = new ErpDepartmentDBO();
						newobj.department.id = map.getKey();
						newobj.recordStatus = 'A';
						subjectCategoryDepartmentTranscaction.saveOrUpdate(newobj);
					}
				}
				result.success = true;
			}
		}
		return result;
	}

	public SubjectCategoryDepartmentDTO selectsubjectCategoryDepartment(String id) {
		SubjectCategoryDepartmentDTO subjectCategoryDepartmentDTO = new SubjectCategoryDepartmentDTO();
		try {
			List<EmpApplnSubjectCategoryDepartmentDBO> dbSubjectDepartmentMappingInfo = subjectCategoryDepartmentTranscaction
					.isEdit(id);
			for (EmpApplnSubjectCategoryDepartmentDBO subdepartmapping : dbSubjectDepartmentMappingInfo) {
				if (subjectCategoryDepartmentDTO.id == null || subjectCategoryDepartmentDTO.id.isEmpty()) {
					subjectCategoryDepartmentDTO.id = String.valueOf(subdepartmapping.id);
					subjectCategoryDepartmentDTO.setCategory(new SelectDTO());
					subjectCategoryDepartmentDTO.getCategory().setValue(String.valueOf(subdepartmapping.subject.id));
					subjectCategoryDepartmentDTO.getCategory().setLabel(subdepartmapping.subject.subjectCategory);
			        subjectCategoryDepartmentDTO.department = new ArrayList<ExModelBaseDTO>();
					subjectCategoryDepartmentDTO.recordIds = new HashSet<Integer>();
				}
				ExModelBaseDTO exModelBase = new ExModelBaseDTO();
				exModelBase.id = String.valueOf(subdepartmapping.department.id);
				exModelBase.tag = String.valueOf(subdepartmapping.department.departmentName);
				subjectCategoryDepartmentDTO.department.add(exModelBase);
				subjectCategoryDepartmentDTO.recordIds.add(subdepartmapping.id);
			}
			return subjectCategoryDepartmentDTO;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectCategoryDepartmentDTO;
	}
	public boolean deleteSubjectDept(String id, String userId) {
		try {
			List<EmpApplnSubjectCategoryDepartmentDBO> empApplnSubjectCategoryDepartmentDBO = subjectCategoryDepartmentTranscaction.isdelete(id);
			if((id)!=null) {
	        	if(empApplnSubjectCategoryDepartmentDBO.size()>0) {
	        		for(EmpApplnSubjectCategoryDepartmentDBO head: empApplnSubjectCategoryDepartmentDBO) {
	        			head.recordStatus='D';
	        			subjectCategoryDepartmentTranscaction.saveOrUpdate(head); 
	        		}
	        		return true;
	        	}        			
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
