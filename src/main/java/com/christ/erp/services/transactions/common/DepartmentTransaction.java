package com.christ.erp.services.transactions.common;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;

public class DepartmentTransaction {

	private static volatile DepartmentTransaction departmentTransaction = null;

	public static DepartmentTransaction getInstance() {
		if (departmentTransaction == null) {
			departmentTransaction = new DepartmentTransaction();
		}
		return departmentTransaction;
	}

	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e.erp_department_id as ID,e.department_name AS `departmentName`, EC.department_category_name as departmentCategoryName, ed.erp_deanery_id, ed.deanery_name  "
						+ "from erp_department as e "
						+ "inner join erp_department_category as EC on EC.erp_department_category_id = e.erp_department_category_id "
						+ "left join erp_deanery as ed on ed.erp_deanery_id = e.erp_deanery_id "
						+ "where  e.record_status='A' order by e.erp_department_category_id, e.department_name ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(ErpDepartmentDBO erpDepartmentDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (erpDepartmentDBO.id == null) {
					context.persist(erpDepartmentDBO);
				} else {
					context.merge(erpDepartmentDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String departmentName, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getDepartmentNameQuery = "from ErpDepartmentDBO where recordStatus='A' and departmentName=:departmentName";
					if (id != null) {
						getDepartmentNameQuery += " and id!=:id";
					}
					Query query = context.createQuery(getDepartmentNameQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("departmentName", departmentName);
					List<ErpDepartmentDBO> erpDepartmentDBO = query.getResultList();
					if (erpDepartmentDBO.size() > 0) {
						return true;
					}
					return false;
				}

				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ErpDepartmentDBO getdepartment(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpDepartmentDBO>() {
			@Override
			public ErpDepartmentDBO onRun(EntityManager context) throws Exception {
				return context.find(ErpDepartmentDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
