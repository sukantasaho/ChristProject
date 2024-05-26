package com.christ.erp.services.handlers.common;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDeaneryDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dto.common.ErpDepartmentCategoryDTO;
import com.christ.erp.services.dto.common.ErpDepartmentDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.common.DepartmentTransaction;

public class DepartmentHandler {

	private static volatile DepartmentHandler departmentHandler = null;
	DepartmentTransaction departmentTransaction = DepartmentTransaction.getInstance();

	public static DepartmentHandler getInstance() {
		if (departmentHandler == null) {
			departmentHandler = new DepartmentHandler();
		}
		return departmentHandler;
	}

	public List<ErpDepartmentDTO> getGridData() {
		List<ErpDepartmentDTO> erpDepartmentDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = departmentTransaction.getGridData();
			if(!Utils.isNullOrEmpty(list)) {
				for (Tuple tuple : list) {
					ErpDepartmentDTO gridDTO = new ErpDepartmentDTO();
					gridDTO.category = new ErpDepartmentCategoryDTO();
					gridDTO.category.text = String.valueOf(tuple.get("departmentCategoryName"));
					gridDTO.departmentName = String.valueOf(tuple.get("departmentName")); 
					if(!Utils.isNullOrEmpty(tuple.get("erp_deanery_id"))) {
						gridDTO.setSchoolName(new ExModelBaseDTO());
						gridDTO.getSchoolName().setId(String.valueOf(tuple.get("erp_deanery_id")));
						gridDTO.getSchoolName().setText(String.valueOf(tuple.get("deanery_name")));
					}
					gridDTO.id = String.valueOf(tuple.get("ID"));
					erpDepartmentDTO.add(gridDTO);
				}
			}} catch (Exception e) {
			e.printStackTrace();
		}
		return erpDepartmentDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ApiResult<ModelBaseDTO> saveOrUpdateDepartment(ErpDepartmentDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		ErpDepartmentDBO header = null;
		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = departmentTransaction.getdepartment(Integer.parseInt(data.id));
		}
		data.departmentName = data.departmentName.trim();
		Boolean isDuplicate = departmentTransaction.isDuplicate(data.departmentName, data.id);
		if (!isDuplicate) {
			if (header == null) {
				header = new ErpDepartmentDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			try {
				header.erpDepartmentCategoryDBO = new ErpDepartmentCategoryDBO();
				header.erpDepartmentCategoryDBO.id = Integer.parseInt(data.category.id);
				ErpDeaneryDBO deanery = new ErpDeaneryDBO();
				if (header.erpDepartmentCategoryDBO != null) {
					if (data.category.isAcademic == false) {
						header.erpDeaneryDBO = null;
					} else {
						deanery.id = Integer.parseInt(data.schoolName.id);
						header.erpDeaneryDBO = deanery;
					}
				} else {
					header.erpDeaneryDBO = null;
				}
				header.departmentName = data.departmentName;
				header.recordStatus = 'A';
				if (header.id == null) {
					departmentTransaction.saveOrUpdate(header);
				} else {
					header.modifiedUsersId = Integer.parseInt(userId);
					departmentTransaction.saveOrUpdate(header);
				}
				if (header.id != 0) {
					result.success = true;
					result.dto = new ModelBaseDTO();
					result.dto.id = header.id.toString();
				}
				result.success = true;
			} catch (java.text.ParseException error) {
				Utils.log(error.getMessage());
			}
		} else{
			result.failureMessage = "Duplicate record exists for Department Name:" + data.departmentName;
			result.success = false;
		}
		return result;
	}

	public ErpDepartmentDTO selectDepartment(String id) {
		ErpDepartmentDTO erpDepartmentDTO = new ErpDepartmentDTO();
		try {
			ErpDepartmentDBO selectDepartment = departmentTransaction.getdepartment(Integer.parseInt(id));
			if (selectDepartment != null) {
				erpDepartmentDTO.id = selectDepartment.id.toString();
				erpDepartmentDTO.departmentName = selectDepartment.departmentName.toString();
				erpDepartmentDTO.category = new ErpDepartmentCategoryDTO();
				erpDepartmentDTO.category.id = String.valueOf(selectDepartment.erpDepartmentCategoryDBO.id);
				erpDepartmentDTO.category.isAcademic = selectDepartment.erpDepartmentCategoryDBO.isCategoryAcademic;
				erpDepartmentDTO.schoolName = new ExModelBaseDTO();
				if (selectDepartment.erpDeaneryDBO != null) {
					erpDepartmentDTO.schoolName.id = String.valueOf(selectDepartment.erpDeaneryDBO.id);
					erpDepartmentDTO.schoolName.text = selectDepartment.erpDeaneryDBO.deaneryName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return erpDepartmentDTO;
	}

	public boolean deleteDepartments(String id, String userId) {
		try {
			ErpDepartmentDBO erpDepartmentDBO = departmentTransaction.getdepartment(Integer.parseInt(id));
			if (erpDepartmentDBO != null) {
				erpDepartmentDBO.recordStatus = 'D';
				erpDepartmentDBO.modifiedUsersId = Integer.parseInt(userId);
				if (erpDepartmentDBO.id != null) {
					return departmentTransaction.saveOrUpdate(erpDepartmentDBO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
