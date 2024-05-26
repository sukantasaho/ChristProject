package com.christ.erp.services.transactions.employee.salary;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;

public class SalaryComponentTransaction {
	public static volatile SalaryComponentTransaction salaryComponentTransaction = null;
	  
	public static SalaryComponentTransaction getInstance() {
        if(salaryComponentTransaction==null) {
        	salaryComponentTransaction = new SalaryComponentTransaction();
        }
        return  salaryComponentTransaction;
    }
	
	public List<Tuple> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
         @Override
         public List<Tuple> onRun(EntityManager context) throws Exception {
             String str = "select SC.emp_pay_scale_components_id AS ID, SC.salary_component_name AS salaryComponentName , SC.salary_component_short_name AS salaryComponentShortName,\r\n" +
             		 "SC.salary_component_display_order  AS salaryComponentDisplayOrder,is_component_basic AS isComponentBasic,SC.percentage  AS percentage,SC.is_caculation_type_percentage AS isCaculationTypePercentage,SC.pay_scale_type AS payScaleType FROM emp_pay_scale_components AS SC where SC.record_status='A' order by salaryComponentDisplayorder";
             Query query = context.createNativeQuery(str,Tuple.class);
             return query.getResultList();
         }
         @Override
         public void onError(Exception error) throws Exception {
            throw error;
         }
		});
	}
	
	public boolean saveOrUpdate(EmpPayScaleComponentsDBO empPayScaleComponentsDBO) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
    		public boolean onRun(EntityManager context) throws Exception {
    			if (empPayScaleComponentsDBO.id == null) {
    				context.persist(empPayScaleComponentsDBO);
    			} else {
    				context.merge(empPayScaleComponentsDBO);
    			}
    			return true;
    		}
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
	public EmpPayScaleComponentsDBO getEmpPayScaleComponentsDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleComponentsDBO>() {
			@Override
			public EmpPayScaleComponentsDBO onRun(EntityManager context) throws Exception {

				return context.find(EmpPayScaleComponentsDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String allowanceType,String payScaleType, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getAllowanceTypeQuery = "from EmpPayScaleComponentsDBO where salaryComponentName=:salaryComponentName and payScaleType=:payScaleType and recordStatus='A'";
					if (id != null) {
						getAllowanceTypeQuery += " and id!=:id";
					}
					Query query = context.createQuery(getAllowanceTypeQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("salaryComponentName", allowanceType.trim());
					query.setParameter("payScaleType", payScaleType);
					List<EmpPayScaleComponentsDBO> empPayScaleComponents = query.getResultList();
					if (empPayScaleComponents.size() > 0) {
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
    
	@SuppressWarnings("unchecked")
	public Boolean isDuplicateDispalyOrder(String displayOrder, String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String getDisplayOrderQuery = "from EmpPayScaleComponentsDBO where  recordStatus='A' and salaryComponentDisplayOrder=:salaryComponentDisplayOrder";
					if (id != null) {
						getDisplayOrderQuery += " and id!=:id";
					}
					Query query = context.createQuery(getDisplayOrderQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("salaryComponentDisplayOrder", Integer.parseInt(displayOrder));
					List<EmpPayScaleComponentsDBO> empPayScaleComponents = query.getResultList();
					if (empPayScaleComponents.size() > 0) {
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
	
	@SuppressWarnings("unchecked")
	public Boolean isBasicEdit(String payScaleType, String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String getisBasicQuery = "from EmpPayScaleComponentsDBO where isComponentBasic=true and recordStatus='A' and payScaleType=:payScaleType ";
					if (id != null) {
						getisBasicQuery += " and id!=:id";
					}
					Query query = context.createQuery(getisBasicQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("payScaleType", payScaleType);
					List<EmpPayScaleComponentsDBO> empPayScaleComponents = query.getResultList();
					if (empPayScaleComponents.size() > 0 ) {
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
	
	@SuppressWarnings("unchecked")
	public Boolean isBasicSave(String payScaleType) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String getisBasicQuery = "from EmpPayScaleComponentsDBO where isComponentBasic=true and recordStatus='A'and payScaleType=:payScaleType";
					Query query = context.createQuery(getisBasicQuery);
					query.setParameter("payScaleType", payScaleType);
					List<EmpPayScaleComponentsDBO> empPayScaleComponents = query.getResultList();
					if (empPayScaleComponents.size() > 0) {
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
	
}
