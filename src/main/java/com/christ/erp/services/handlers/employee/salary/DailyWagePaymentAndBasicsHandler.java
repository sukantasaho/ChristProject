package com.christ.erp.services.handlers.employee.salary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpDailyWageSlabDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDetailsDTO;
import com.christ.erp.services.transactions.employee.salary.DailyWagePaymentAndBasicsTransaction;

public class DailyWagePaymentAndBasicsHandler {

	private static volatile DailyWagePaymentAndBasicsHandler dailyWagePaymentAndBasicsHandler = null;
	DailyWagePaymentAndBasicsTransaction dailyWagePaymentAndBasicsTransaction = DailyWagePaymentAndBasicsTransaction.getInstance();

	public static DailyWagePaymentAndBasicsHandler getInstance() {
		if (dailyWagePaymentAndBasicsHandler == null) {
			dailyWagePaymentAndBasicsHandler = new DailyWagePaymentAndBasicsHandler();
		}
		return dailyWagePaymentAndBasicsHandler;
	}

	public List<EmpDailyWageSlabDTO> getGridData() throws Exception {
		List<Tuple> list = dailyWagePaymentAndBasicsTransaction.getGridData();
		List<EmpDailyWageSlabDTO> gridList = null;
		if(!Utils.isNullOrEmpty(list)) {
			gridList = new ArrayList<>();
			for(Tuple tuple : list) {
				EmpDailyWageSlabDTO dailywageInfo = new EmpDailyWageSlabDTO();
				dailywageInfo.empCategory = new ExModelBaseDTO();
				dailywageInfo.empCategory.id = tuple.get("emp_cat_id").toString();
				dailywageInfo.empCategory.text = tuple.get("emp_cat_name").toString();
				dailywageInfo.jobCategory = new ExModelBaseDTO();
				dailywageInfo.jobCategory.id = tuple.get("emp_job_cat_id").toString();
				dailywageInfo.jobCategory.text = tuple.get("emp_job_cat_name").toString();
				gridList.add(dailywageInfo);
			}
		}
		return gridList;
	}

	public boolean saveOrUpdate(EmpDailyWageSlabDTO data, String userId) throws Exception {
		List<EmpDailyWageSlabDBO> dboList = new ArrayList<EmpDailyWageSlabDBO>();
		List<Integer> dlyWgIds1 = new ArrayList<>();
		if(!Utils.isNullOrEmpty(data)) {
			List<Tuple> dlyWgDataList = dailyWagePaymentAndBasicsTransaction.getEmployeeDlyWageDetails(data.empCategory.id, data.jobCategory.id);
				if(!Utils.isNullOrEmpty(dlyWgDataList)) {
					dlyWgDataList.forEach(tuple -> dlyWgIds1.add(Integer.parseInt(tuple.get("dly_wg_slab_Id").toString())));
				}
			for(EmpDailyWageSlabDetailsDTO slabDetailDTO : data.empDailyWageDetails) {
				EmpDailyWageSlabDBO dbo = new EmpDailyWageSlabDBO();
				if(!Utils.isNullOrEmpty(slabDetailDTO.id)) {
					dbo.id = Integer.valueOf(slabDetailDTO.id);
					dbo.dailyWageSlabBasic = slabDetailDTO.dailyWageSlabbasic;
					dbo.dailyWageSlabFrom = slabDetailDTO.dailyWageSlabfrom;
					dbo.dailyWageSlabTo = slabDetailDTO.dailyWageSlabto;
					dbo.modifiedUsersId = Integer.parseInt(userId);
					dlyWgIds1.remove(Integer.valueOf(slabDetailDTO.id));
				}
				EmpEmployeeCategoryDBO bo = new EmpEmployeeCategoryDBO();
				bo.id = Integer.parseInt(data.empCategory.id);
				dbo.empCategory = bo;

				EmpEmployeeJobCategoryDBO bo1 = new EmpEmployeeJobCategoryDBO();
				bo1.id = Integer.parseInt(data.jobCategory.id);
				dbo.jobCategory = bo1;

				dbo.dailyWageSlabBasic = slabDetailDTO.dailyWageSlabbasic;
				dbo.dailyWageSlabFrom = slabDetailDTO.dailyWageSlabfrom;
				dbo.dailyWageSlabTo = slabDetailDTO.dailyWageSlabto;
				dbo.recordStatus = 'A';
				dboList.add(dbo);
			}
			if(!Utils.isNullOrEmpty(dlyWgIds1)) {
				dailyWagePaymentAndBasicsTransaction.delete(dlyWgIds1);
			}
			if(!Utils.isNullOrEmpty(dboList)) {
				return dailyWagePaymentAndBasicsTransaction.saveOrUpdate(dboList);
			}
		}

		return false;
	}

	public EmpDailyWageSlabDTO getDlyWgData(Map<String, String> data) throws Exception {
		List<Tuple> dlyWgDataList = dailyWagePaymentAndBasicsTransaction.getEmployeeDlyWageDetails(data.get("empCategory"), data.get("jobCategory"));
		EmpDailyWageSlabDTO dailywageInfo = new EmpDailyWageSlabDTO();
		if(!Utils.isNullOrEmpty(dlyWgDataList)) {
			EmpDailyWageSlabDetailsDTO dailywageDetlInfo = null;
			ArrayList<EmpDailyWageSlabDetailsDTO> detailList = new ArrayList<>();
			for(Tuple slabDetailDTO : dlyWgDataList) {
				dailywageDetlInfo = new EmpDailyWageSlabDetailsDTO();
				dailywageInfo.empCategory = new ExModelBaseDTO();
				dailywageInfo.empCategory.id = slabDetailDTO.get("emp_cat_id").toString();
				dailywageInfo.empCategory.text = slabDetailDTO.get("emp_cat_name").toString();
				dailywageInfo.jobCategory = new ExModelBaseDTO();
				dailywageInfo.jobCategory.id = slabDetailDTO.get("emp_job_cat_id").toString();
				dailywageInfo.jobCategory.text = slabDetailDTO.get("emp_job_cat_name").toString();
				dailywageDetlInfo.id = (Integer) slabDetailDTO.get("dly_wg_slab_Id");
				dailywageDetlInfo.dailyWageSlabfrom = (Integer) slabDetailDTO.get("dly_wge_from");
				dailywageDetlInfo.dailyWageSlabto = (Integer) slabDetailDTO.get("dly_wge_to");
				dailywageDetlInfo.dailyWageSlabbasic = (Integer) slabDetailDTO.get("dly_wge_bsc");
				detailList.add(dailywageDetlInfo);
				dailywageInfo.empDailyWageDetails = detailList;
			}
		}
		return dailywageInfo;
	}

	@SuppressWarnings("unused")
	public boolean dupcheck(EmpDailyWageSlabDTO data) throws Exception {
		boolean isDup = false;
		
		List<Integer> dlyWgIds = new ArrayList<>();
		for (EmpDailyWageSlabDetailsDTO dto : data.empDailyWageDetails) {
			if(dto.id != null) {
				dlyWgIds.add(dto.id);
			}
		}
		List<Tuple> dlyWgDataList = dailyWagePaymentAndBasicsTransaction.getEmployeeDlyWageDetails(data.empCategory.id,data.jobCategory.id);
			if(dlyWgDataList != null && !dlyWgDataList.isEmpty()) {
				if(dlyWgIds != null && !dlyWgIds.isEmpty()) {
					
				}else {
					isDup = true;
				}			
			}
		
		return isDup;
	}
	
	public boolean delete(Map<String, String> data) throws Exception {
		List<Tuple> dlyWgDataList = dailyWagePaymentAndBasicsTransaction.getEmployeeDlyWageDetails(data.get("empCategory"),data.get("jobCategory"));
		List<Integer> dlyWgIds = new ArrayList<>();
		if(!Utils.isNullOrEmpty(dlyWgDataList)) {
			dlyWgDataList.forEach(tuple -> dlyWgIds.add(Integer.parseInt(tuple.get("dly_wg_slab_Id").toString())));
			return dailyWagePaymentAndBasicsTransaction.delete(dlyWgIds);
		}
		return false;
	}

}
