package com.christ.erp.services.transactions.employee.salary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.salary.EmpDailyWageSlabDBO;

public class DailyWagePaymentAndBasicsTransaction {

	private static volatile DailyWagePaymentAndBasicsTransaction dailyWagePaymentAndBasicsTransaction = null;

	public static DailyWagePaymentAndBasicsTransaction getInstance() {
		if (dailyWagePaymentAndBasicsTransaction == null) {
			dailyWagePaymentAndBasicsTransaction = new DailyWagePaymentAndBasicsTransaction();
		}
		return dailyWagePaymentAndBasicsTransaction;
	}

	public boolean delete(List<Integer> dlyWgIds) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery("update emp_daily_wage_slab set record_status='D' where emp_daily_wage_slab_id in (:ids)");
				query.setParameter("ids", dlyWgIds);
				return query.executeUpdate() > 0 ? true : false;
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
				String str = " select emp_daily_wage_slab.emp_employee_category_id as emp_cat_id , emp_employee_category.employee_category_name as emp_cat_name , "
						+ " emp_daily_wage_slab.emp_employee_job_category_id as emp_job_cat_id , emp_employee_job_category.employee_job_name as emp_job_cat_name from emp_daily_wage_slab "
						+ " inner join emp_employee_category on emp_daily_wage_slab.emp_employee_category_id = emp_employee_category.emp_employee_category_id "
						+ " inner join emp_employee_job_category on emp_daily_wage_slab.emp_employee_job_category_id = emp_employee_job_category.emp_employee_job_category_id "
						+ " where emp_daily_wage_slab.record_status = 'A' group by emp_daily_wage_slab.emp_employee_category_id , emp_daily_wage_slab.emp_employee_job_category_id order by emp_employee_category.employee_category_name ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmployeeDlyWageDetails(String empCatId, String empJobCatId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select emp_daily_wage_slab.emp_daily_wage_slab_id as dly_wg_slab_Id , emp_daily_wage_slab.emp_employee_category_id as emp_cat_id , "
						+ "  emp_employee_category.employee_category_name as emp_cat_name , emp_daily_wage_slab.emp_employee_job_category_id as emp_job_cat_id , "
						+ "  emp_employee_job_category.employee_job_name as emp_job_cat_name ,emp_daily_wage_slab.daily_wage_slab_from as dly_wge_from , "
						+ "  emp_daily_wage_slab.daily_wage_slab_to as dly_wge_to , emp_daily_wage_slab.daily_wage_basic as dly_wge_bsc  from emp_daily_wage_slab "
						+ "  inner join emp_employee_category on emp_daily_wage_slab.emp_employee_category_id = emp_employee_category.emp_employee_category_id "
						+ "  inner join emp_employee_job_category on emp_daily_wage_slab.emp_employee_job_category_id = emp_employee_job_category.emp_employee_job_category_id "
						+ "  where emp_daily_wage_slab.record_status = 'A' and emp_daily_wage_slab.emp_employee_category_id =:empCatId and emp_daily_wage_slab.emp_employee_job_category_id =:empJobCatId ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empCatId", Integer.parseInt(empCatId));
				query.setParameter("empJobCatId", Integer.parseInt(empJobCatId));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(List<EmpDailyWageSlabDBO> dboList) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				dboList.forEach(dbo -> {
					if(dbo.id == null ||dbo.id == 0) {
						context.persist(dbo);
					} else {
						context.merge(dbo);
					}
				});
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
