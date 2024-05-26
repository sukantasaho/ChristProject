package com.christ.erp.services.handlers.employee.letter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.letter.EmpLetterRequestDTO;
import com.christ.erp.services.transactions.employee.letter.LetterTypesTransaction;
import com.christ.erp.services.common.Utils;

public class LetterTypesHandlers {

	private static volatile LetterTypesHandlers letterTypesHandlers = null;
	LetterTypesTransaction letterTypesTransaction = LetterTypesTransaction.getInstance();

	public static LetterTypesHandlers getInstance() {
		if (letterTypesHandlers == null) {
			letterTypesHandlers = new LetterTypesHandlers();
		}
		return letterTypesHandlers;
	}

	public List<EmpLetterRequestDTO> getGridData() {
		List<EmpLetterRequestDTO> empLetterRequestDTO = new ArrayList<>();
		List<Tuple> list;
		try {
			list = letterTypesTransaction.getGridData();
			for (Tuple tuple : list) {
				EmpLetterRequestDTO gridDTO = new EmpLetterRequestDTO();
				gridDTO.letterTemplate = new ExModelBaseDTO();
				gridDTO.letterTemplate.text = tuple.get("letterTemplate").toString();
				gridDTO.letterName = tuple.get("letterName").toString();
				gridDTO.id = tuple.get("ID").toString();
				gridDTO.startNo = tuple.get("startNo").toString();
				empLetterRequestDTO.add(gridDTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return empLetterRequestDTO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ApiResult<ModelBaseDTO> saveOrUpdateLetterTypes(EmpLetterRequestDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult();
		EmpLetterRequestTypeDBO header = null;
		if (Utils.isNullOrWhitespace(data.id) == false) {
			header = letterTypesTransaction.getEmpLetterRequestTypeDBO(Integer.parseInt(data.id));
		}
		Boolean isDuplicate = letterTypesTransaction.isDuplicate(data.letterName.trim().replaceAll("\\s+", ""), data.id);
		if (!isDuplicate) {
			if (header == null) {
				header = new EmpLetterRequestTypeDBO();
				header.createdUsersId = Integer.parseInt(userId);
			}
			try {
				ErpTemplateDBO erptemplateId = LetterTypesTransaction
						.gettemplate(Integer.parseInt(data.letterTemplate.id));
				header.erptemplateId = erptemplateId;
				header.letterTypeName = data.letterName.trim().replaceAll("\\s+", " ");
				header.letterTypePrefix = data.letterNoPrefix;
				header.letterTypeStartNo = data.startNo;
				header.letterHelpText = data.letterHelpText;
				header.isAvailableOnline = Boolean.valueOf(data.availableOnline);
				header.recordStatus = 'A';
				if (header.id != null) {
					header.modifiedUsersId = Integer.parseInt(userId);
				}
				letterTypesTransaction.saveOrUpdate(header);
				if (header.id != 0) {
					result.success = true;
					result.dto = new ModelBaseDTO();
					result.dto.id = header.id.toString();
				}
				result.success = true;
			} catch (java.text.ParseException error) {
				Utils.log(error.getMessage());
			}
		} else {
			result.failureMessage = "Duplicate record exists for Letter Name: " + data.letterName;
			result.success = false;
		}
		return result;
	}

	public EmpLetterRequestDTO selectLetterTypes(String id) {
		Map<Integer, String> letterTemplateMap = new HashMap<Integer, String>();
		EmpLetterRequestDTO empLetterRequestDTO = new EmpLetterRequestDTO();
		List<Tuple> mappings;
		try {
			mappings = letterTypesTransaction.getLetterTemplate();
			if (mappings != null && mappings.size() > 0) {
				for (Tuple mapping : mappings) {
					letterTemplateMap.put(Integer.parseInt(mapping.get("ID").toString()),
							mapping.get("Text").toString());
				}
			}
			EmpLetterRequestTypeDBO selectLetterType = letterTypesTransaction
					.getEmpLetterRequestTypeDBO(Integer.parseInt(id));
			if (selectLetterType != null) {
				empLetterRequestDTO.id = selectLetterType.id.toString();
				empLetterRequestDTO.letterTemplate = new ExModelBaseDTO();
				empLetterRequestDTO.letterTemplate.id = String.valueOf(selectLetterType.erptemplateId.id);
				empLetterRequestDTO.letterName = selectLetterType.letterTypeName.toString();
				empLetterRequestDTO.startNo = selectLetterType.letterTypeStartNo.toString();
				empLetterRequestDTO.letterNoPrefix = selectLetterType.letterTypePrefix.toString();
				empLetterRequestDTO.letterHelpText = selectLetterType.letterHelpText.toString();
				empLetterRequestDTO.availableOnline = Boolean.valueOf(selectLetterType.isAvailableOnline);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empLetterRequestDTO;
	}

	public boolean deleteLetterTypes(String id, String userId) {
		try {
			EmpLetterRequestTypeDBO empLetterRequestTypeDBO = letterTypesTransaction
					.getEmpLetterRequestTypeDBO(Integer.parseInt(id));
			if (empLetterRequestTypeDBO != null) {
				empLetterRequestTypeDBO.recordStatus = 'D';
				empLetterRequestTypeDBO.modifiedUsersId = Integer.parseInt(userId);
				if (empLetterRequestTypeDBO.id != null) {
					return letterTypesTransaction.saveOrUpdate(empLetterRequestTypeDBO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
