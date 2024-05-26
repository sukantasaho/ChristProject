package com.christ.erp.services.handlers.employee.appraisal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalSessionDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalSessionDTO;
import com.christ.erp.services.transactions.employee.appraisal.AnnualAppraisalSessionTransaction;

public class AnnualAppraisalSessionHandler {
	private static volatile AnnualAppraisalSessionHandler annualAppraisalSessionHandler = null;
	private static volatile AnnualAppraisalSessionTransaction annualAppraisalSessionTransaction = AnnualAppraisalSessionTransaction.getInstance();
	
	public static AnnualAppraisalSessionHandler getInstance() {
		if (annualAppraisalSessionHandler == null) {
			annualAppraisalSessionHandler = new AnnualAppraisalSessionHandler();
		}
		return annualAppraisalSessionHandler;
	}
	
	public ApiResult<ModelBaseDTO> saveOrUpdate(EmpAppraisalSessionDTO data,String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(!Utils.isNullOrEmpty(data)) {			
			EmpAppraisalSessionDBO dbo = new EmpAppraisalSessionDBO();
			boolean flag = true;
			if(Utils.isNullOrEmpty(data.id)) {
				// Duplicate check
				List<Tuple> mappings = annualAppraisalSessionTransaction.getDuplicate(data);
				if(mappings.size() > 0) {
					result.failureMessage = "Already entered data for this selection";
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
				ErpAcademicYearDBO year = new ErpAcademicYearDBO();
				year.id = Integer.parseInt(data.academicYear.id);					
				dbo.erpAcademicYearDBO = year;
				ErpLocationDBO location = new ErpLocationDBO();
				location.id = Integer.parseInt(data.location.id);
				dbo.erpLocationDBO = location;
				EmpEmployeeCategoryDBO employeeCategory = new EmpEmployeeCategoryDBO();
				employeeCategory.id = Integer.parseInt(data.employeeCategory.id);
				dbo.empEmployeeCategoryDBO = employeeCategory;
				dbo.appraisalSessionName = data.sessionName;
				dbo.sessionType = data.type.text;
				dbo.appraisalSessionYear = Integer.parseInt(data.year.text);
				dbo.appraisalSessionMonth = Integer.parseInt(data.month.id);			
				dbo.recordStatus = 'A';					
				result.success = annualAppraisalSessionTransaction.saveOrUpdate(dbo);
			}
		}
		return result;
	}
	
	public List<EmpAppraisalSessionDTO> getGridData() throws Exception {
		List<EmpAppraisalSessionDTO> gridList = null;
		List<Tuple> list = annualAppraisalSessionTransaction.getGridData();
		if (!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<EmpAppraisalSessionDTO>();
			for(Tuple tuple : list) {
				EmpAppraisalSessionDTO dto = new EmpAppraisalSessionDTO();
				dto.id = String.valueOf(tuple.get("empAppraisalSessionId"));
				dto.academicYear = new ExModelBaseDTO();
				dto.academicYear.id = String.valueOf(tuple.get("academicYearId"));
				dto.academicYear.text = String.valueOf(tuple.get("academicYearName"));
				dto.location = new ExModelBaseDTO();
				dto.location.id = String.valueOf(tuple.get("locationId"));
				dto.location.text = String.valueOf(tuple.get("locationName"));
				dto.employeeCategory = new ExModelBaseDTO();
				dto.employeeCategory.id = String.valueOf(tuple.get("empEmployeeCategoryId"));
				dto.employeeCategory.text = String.valueOf(tuple.get("empEmployeeCategoryName"));
				dto.sessionName = String.valueOf(tuple.get("appraisalSessionName"));
				dto.type = new ExModelBaseDTO();
				dto.type.text = dto.type.id = String.valueOf(tuple.get("type"));
				dto.year = new ExModelBaseDTO();
				dto.year.id =dto.year.text = String.valueOf(tuple.get("appraisalSessionYear"));
				dto.month = new ExModelBaseDTO();
				dto.month.id = String.valueOf(tuple.get("appraisalSessionMonth"));
				dto.month.text = Utils.getMonthName(Integer.parseInt(dto.month.id));
				gridList.add(dto);
			}
		}
		return gridList;
	}
	
	
	public boolean delete(EmpAppraisalSessionDTO data) throws Exception {
		Boolean result = false;
		result = annualAppraisalSessionTransaction.delete(data);
		return result;
	}

}
