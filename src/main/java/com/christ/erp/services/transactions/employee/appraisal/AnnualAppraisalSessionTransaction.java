package com.christ.erp.services.transactions.employee.appraisal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalSessionDBO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalSessionDTO;

public class AnnualAppraisalSessionTransaction {
	private static volatile AnnualAppraisalSessionTransaction annualAppraisalSessionTransaction = null;

	public static AnnualAppraisalSessionTransaction getInstance() {
		if (annualAppraisalSessionTransaction == null) {
			annualAppraisalSessionTransaction = new AnnualAppraisalSessionTransaction();
		}
		return annualAppraisalSessionTransaction;
	}
	
	public boolean saveOrUpdate(EmpAppraisalSessionDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (Utils.isNullOrEmpty(dbo.id)) {
					context.persist(dbo);
				} else {
					context.merge(dbo);
				}				
			return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getDuplicate(EmpAppraisalSessionDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_appraisal_session_id from emp_appraisal_session where session_type=:sessionType and erp_academic_year_id =:academicyearId" + 
						"  and appraisal_session_year =:appraisalSessionYear and appraisal_session_month =:appraisalSessionMonth and emp_employee_category_id =:employeeCategoryId and erp_location_id=:locationId" + 
						"  and record_status = 'A'";
				Query q = context.createNativeQuery(str, Tuple.class);
				q.setParameter("locationId", Integer.parseInt(data.location.id));
				q.setParameter("academicyearId", Integer.parseInt(data.academicYear.id));
				q.setParameter("employeeCategoryId", Integer.parseInt(data.employeeCategory.id));
				q.setParameter("sessionType", data.type.text);
				q.setParameter("appraisalSessionYear", Integer.parseInt(data.year.id));
				q.setParameter("appraisalSessionMonth", Integer.parseInt(data.month.id));
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select erp_academic_year.erp_academic_year_id as academicYearId,erp_academic_year.academic_year_name as academicYearName," 
						+" erp_location.erp_location_id as locationId,erp_location.location_name as locationName," 
						+" emp_employee_category.emp_employee_category_id as empEmployeeCategoryId,emp_employee_category.employee_category_name as empEmployeeCategoryName," 
						+" emp_appraisal_session.emp_appraisal_session_id  as empAppraisalSessionId,emp_appraisal_session.appraisal_session_name as appraisalSessionName," 
						+" emp_appraisal_session.session_type as type,emp_appraisal_session.appraisal_session_year as appraisalSessionYear,emp_appraisal_session.appraisal_session_month as" 
						+" appraisalSessionMonth from emp_appraisal_session" 
						+" inner join erp_academic_year on erp_academic_year.erp_academic_year_id = emp_appraisal_session.erp_academic_year_id"  
						+" inner join erp_location ON erp_location.erp_location_id = emp_appraisal_session.erp_location_id"  
						+" inner join emp_employee_category ON emp_employee_category.emp_employee_category_id = emp_appraisal_session.emp_employee_category_id" 
						+" where emp_appraisal_session.record_status = 'A' order by erp_academic_year.academic_year_name,erp_location.location_name,emp_employee_category.employee_category_name";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean delete(EmpAppraisalSessionDTO data) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery("update emp_appraisal_session set record_status='D' where emp_appraisal_session_id=:id");
				query.setParameter("id", data.id);				
				return query.executeUpdate() > 0 ? true : false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
