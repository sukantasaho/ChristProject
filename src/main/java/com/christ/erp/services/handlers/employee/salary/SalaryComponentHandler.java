package com.christ.erp.services.handlers.employee.salary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.transactions.employee.salary.SalaryComponentTransaction;
import org.springframework.beans.factory.annotation.Autowired;

public class SalaryComponentHandler {
	private static volatile SalaryComponentHandler salaryComponentHandler = null;
	SalaryComponentTransaction salaryComponentTransaction = SalaryComponentTransaction.getInstance();

	public static SalaryComponentHandler getInstance() {
		if (salaryComponentHandler == null) {
			salaryComponentHandler = new SalaryComponentHandler();
		}
		return salaryComponentHandler;
	}

	public List<SalaryComponentDTO> getGridData() throws Exception {
		List<SalaryComponentDTO> salaryComponentDTO = new ArrayList<>();
		List<Tuple> list = salaryComponentTransaction.getGridData();
		for (Tuple tuple : list) {
			SalaryComponentDTO gridDTO = new SalaryComponentDTO();
			gridDTO.id = tuple.get("ID").toString();
			gridDTO.allowanceType = tuple.get("salaryComponentName").toString();
			gridDTO.shortName = tuple.get("salaryComponentShortName").toString();
			gridDTO.payScaleType = tuple.get("payScaleType").toString();
			gridDTO.displayOrder = tuple.get("salaryComponentDisplayOrder").toString();
			gridDTO.isBasic = (Boolean) tuple.get("isComponentBasic");
			if(!Utils.isNullOrEmpty(tuple.get("percentage")))
				gridDTO.mentionPercentage = tuple.get("percentage").toString();
			else
				gridDTO.mentionPercentage = "";
			salaryComponentDTO.add(gridDTO);
		}
		return salaryComponentDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public ApiResult<ModelBaseDTO> saveOrUpdateSalaryComponent(SalaryComponentDTO data, String userId)
			throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		EmpPayScaleComponentsDBO header = null;
		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = salaryComponentTransaction.getEmpPayScaleComponentsDBO(Integer.parseInt(data.id));
		}
		if (data.isBasic == true && header == null) {
			Boolean academicYear = salaryComponentTransaction.isBasicSave(data.payScaleType);
			if (academicYear != null) {
				result.success = false;
				result.dto = null;
				result.failureMessage = "Only one entry should be mentioned as basic with pay scale type ' "+ data.payScaleType+ " '. ' "+ data.allowanceType+ " ' already mentioned as Basic.";
				return result;
			}
		}
		if (data.isBasic == true && header != null) {
			Boolean academicYear = salaryComponentTransaction.isBasicEdit(data.payScaleType, data.id);
			if (academicYear != null) {
				result.success = false;
				result.dto = null;
				result.failureMessage = "Only one entry should be mentioned as basic with pay scale type ' "+ data.payScaleType+ " '. ' "+ data.allowanceType+ " ' already mentioned as Basic.";
				return result;
			}
		}

		Boolean isDuplicate = salaryComponentTransaction.isDuplicate(data.allowanceType, data.payScaleType, data.id);
		Boolean isDuplicateDispalyOrder = salaryComponentTransaction.isDuplicateDispalyOrder(data.displayOrder,
				data.id);
		if (!isDuplicate && !isDuplicateDispalyOrder) {

			if (header == null) {
				header = new EmpPayScaleComponentsDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			try {

				header.salaryComponentName = data.allowanceType;
				header.salaryComponentShortName = data.shortName.toUpperCase();
				header.payScaleType = data.payScaleType;
				header.isComponentBasic = data.isBasic;
				header.salaryComponentDisplayOrder = Integer.parseInt(data.displayOrder);
				header.isCalculationTypePercentage = data.calculationType;
				if (header.isCalculationTypePercentage == true) {
					if (data.mentionPercentage.isEmpty()) {
						header.percentage = null;
					} else {
						header.percentage = new BigDecimal(data.mentionPercentage);
					}
				} else {
					header.percentage = null;
				}
				header.recordStatus = 'A';
				if (header.id != null) {
					header.modifiedUsersId = Integer.parseInt(userId);
				}
				salaryComponentTransaction.saveOrUpdate(header);
				if (header.id != 0) {
					result.success = true;
					result.dto = new ModelBaseDTO();
					result.dto.id = header.id.toString();
				}
				result.success = true;
			} catch (java.text.ParseException error) {
				Utils.log(error.getMessage());
			}
		} else if (isDuplicate) {
			result.failureMessage = "Duplicate entry for Allowance Type: ' " + data.allowanceType
					+ "'.Record already exist with Pay Scale Type:" + data.payScaleType;
			result.success = false;
		} else {
			result.failureMessage = "Duplicate entry for Display Order: " + data.displayOrder;
			result.success = false;
		}
		return result;
	}

	public SalaryComponentDTO selectSalaryComponent(String id) {
		SalaryComponentDTO salaryComponentDTO = new SalaryComponentDTO();
		try {
			EmpPayScaleComponentsDBO selectSalaryComponent = salaryComponentTransaction
					.getEmpPayScaleComponentsDBO(Integer.parseInt(id));
			if (selectSalaryComponent != null) {
				salaryComponentDTO.id = selectSalaryComponent.id.toString();
				salaryComponentDTO.allowanceType = selectSalaryComponent.salaryComponentName.toString();
				salaryComponentDTO.shortName = selectSalaryComponent.salaryComponentShortName.toString();
				salaryComponentDTO.payScaleType = selectSalaryComponent.payScaleType.toString();
				salaryComponentDTO.displayOrder = String.valueOf(selectSalaryComponent.salaryComponentDisplayOrder);
				salaryComponentDTO.isBasic = Boolean.valueOf(selectSalaryComponent.isComponentBasic);
				salaryComponentDTO.calculationType = Boolean.valueOf(selectSalaryComponent.isCalculationTypePercentage);
				if(!Utils.isNullOrEmpty(selectSalaryComponent.percentage))
				salaryComponentDTO.mentionPercentage = String.valueOf(selectSalaryComponent.percentage);
				else
					salaryComponentDTO.mentionPercentage = "";

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salaryComponentDTO;
	}

	public boolean deleteSalartComponent(String id, String userId) {
		try {
			EmpPayScaleComponentsDBO empPayScaleComponents = salaryComponentTransaction
					.getEmpPayScaleComponentsDBO(Integer.parseInt(id));
			if (empPayScaleComponents != null) {
				empPayScaleComponents.recordStatus = 'D';
				empPayScaleComponents.modifiedUsersId = Integer.parseInt(userId);
				if (empPayScaleComponents.id != null) {
					return salaryComponentTransaction.saveOrUpdate(empPayScaleComponents);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
