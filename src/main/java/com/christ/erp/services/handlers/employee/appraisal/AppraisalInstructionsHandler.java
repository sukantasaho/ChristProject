package com.christ.erp.services.handlers.employee.appraisal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalInstructionDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalInstructionDTO;
import com.christ.erp.services.transactions.employee.appraisal.AppraisalInstructionsTransaction;

public class AppraisalInstructionsHandler {
	private static volatile AppraisalInstructionsHandler appraisalInstructionsHandler = null;
	private static volatile AppraisalInstructionsTransaction appraisalInstructionsTransaction = AppraisalInstructionsTransaction.getInstance();
	
	public static AppraisalInstructionsHandler getInstance() {
		if (appraisalInstructionsHandler == null) {
			appraisalInstructionsHandler = new AppraisalInstructionsHandler();
		}
		return appraisalInstructionsHandler;
	}
	
	public ApiResult<ModelBaseDTO> saveOrUpdate(EmpAppraisalInstructionDTO data,String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(!Utils.isNullOrEmpty(data)) {			
			EmpAppraisalInstructionDBO dbo = new EmpAppraisalInstructionDBO();
			boolean flag = true;
			if(Utils.isNullOrEmpty(data.id)) {
				// Duplicate check
				List<Tuple> mappings = appraisalInstructionsTransaction.getDuplicate(data);
				if(mappings.size() > 0) {
					result.failureMessage = "Already entered instruction name.";
					result.success = false;	
					flag = false;					
				} 
				else {
					dbo.createdUsersId = Integer.parseInt(userId);
				}
			}
			else{
				dbo.modifiedUsersId = Integer.parseInt(userId);	
				dbo.id =Integer.parseInt(data.id);
			}
			if(flag) {
				dbo.instructionName = data.instructionName;
				dbo.appraisalType = data.appraisalType.text;
				dbo.instructionContent = data.instructionContent;
				dbo.recordStatus = 'A';					
				result.success = appraisalInstructionsTransaction.saveOrUpdate(dbo);
			}
		}
		return result;
	}
	
	public List<EmpAppraisalInstructionDTO> getGridData() throws Exception {
		List<EmpAppraisalInstructionDTO> gridList = null;
		List<Tuple> list = appraisalInstructionsTransaction.getGridData();
		if (!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<EmpAppraisalInstructionDTO>();
			for(Tuple tuple : list) {
				EmpAppraisalInstructionDTO dto = new EmpAppraisalInstructionDTO();
				dto.id = String.valueOf(tuple.get("empAppraisalInstructionId"));				
				dto.instructionName = String.valueOf(tuple.get("instructionName"));
				dto.appraisalType = new ExModelBaseDTO();
				dto.appraisalType.text= dto.appraisalType.id = String.valueOf(tuple.get("appraisalType"));				
				gridList.add(dto);
			}
		}
		return gridList;
	}
	
	public EmpAppraisalInstructionDTO edit(EmpAppraisalInstructionDTO data) throws Exception {
		EmpAppraisalInstructionDTO dto = null;
		if (!Utils.isNullOrEmpty(data)) {
			List<Tuple> appraisalInstruction = appraisalInstructionsTransaction.edit(data);
			if(appraisalInstruction.size() > 0) {
				for(Tuple tuple : appraisalInstruction) {
					dto = new EmpAppraisalInstructionDTO();
					dto.id = String.valueOf(tuple.get("empAppraisalInstructionId"));				
					dto.instructionName = String.valueOf(tuple.get("instructionName"));
					dto.appraisalType = new ExModelBaseDTO();
					dto.appraisalType.text= dto.appraisalType.id = String.valueOf(tuple.get("appraisalType"));	
					dto.instructionContent = String.valueOf(tuple.get("instructionContent")); 					
				}
			}
		}
		return dto;
	}
	
	public boolean delete(EmpAppraisalInstructionDTO data) throws Exception {
		Boolean result = false;
		result = appraisalInstructionsTransaction.delete(data);
		return result;
	}
}
