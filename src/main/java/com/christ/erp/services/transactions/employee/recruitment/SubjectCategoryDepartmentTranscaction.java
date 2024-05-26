package com.christ.erp.services.transactions.employee.recruitment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnSubjectCategoryDepartmentDBO;

public class SubjectCategoryDepartmentTranscaction {
	private static volatile SubjectCategoryDepartmentTranscaction subjectCategoryDepartmentTranscaction = null;

	public static SubjectCategoryDepartmentTranscaction getInstance() {
		if (subjectCategoryDepartmentTranscaction == null) {
			subjectCategoryDepartmentTranscaction = new SubjectCategoryDepartmentTranscaction();
		}
		return subjectCategoryDepartmentTranscaction;
	}

	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select  emp_appln_subject_category.emp_appln_subject_category_id as emp_appln_subject_category_id, emp_appln_subject_category.subject_category_name as categoryName,"
						   +" group_concat(distinct erp_department.department_name) as department_name"
						   +" from emp_appln_subject_category"
						   +" inner join emp_appln_subject_category_department on emp_appln_subject_category_department.emp_appln_subject_category_id = emp_appln_subject_category.emp_appln_subject_category_id "
						   +" and emp_appln_subject_category_department.record_status ='A'"
						   +" inner join erp_department ON erp_department.erp_department_id = emp_appln_subject_category_department.erp_department_id and erp_department.record_status ='A'"
						   +" where emp_appln_subject_category.record_status ='A'"
						   +" group by emp_appln_subject_category.emp_appln_subject_category_id";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpApplnSubjectCategoryDepartmentDBO empApplnSubjectCategoryDepartmentDBO)
			throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empApplnSubjectCategoryDepartmentDBO.id == null) {
					context.persist(empApplnSubjectCategoryDepartmentDBO);
				} else {
					context.merge(empApplnSubjectCategoryDepartmentDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpApplnSubjectCategoryDepartmentDBO getEmpApplnSubjectCategoryDepartmentDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnSubjectCategoryDepartmentDBO>() {
			@Override
			public EmpApplnSubjectCategoryDepartmentDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpApplnSubjectCategoryDepartmentDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public static ErpDepartmentDBO getErpDepartmentDBO(int id) throws Exception {
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

	public static EmpApplnSubjectCategoryDBO getEmpApplnSubjectCategoryDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnSubjectCategoryDBO>() {
			@Override
			public EmpApplnSubjectCategoryDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpApplnSubjectCategoryDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpApplnSubjectCategoryDepartmentDBO> getEmpApplnSubjectCategoryDepartmentDBO() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnSubjectCategoryDepartmentDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpApplnSubjectCategoryDepartmentDBO> onRun(EntityManager context) throws Exception {
				Query applnSubjectCategoryDepartmentQuery = context
						.createQuery("from EmpApplnSubjectCategoryDepartmentDBO bo where bo.recordStatus='A'");
				return applnSubjectCategoryDepartmentQuery.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(HashSet<Integer> recordIds, String category) {
		Set<Integer> idsList = new HashSet<Integer>();
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String getDuplicateQuery = "from EmpApplnSubjectCategoryDepartmentDBO  where subject.id=:category and recordStatus='A'  and id not in (:recordIds) ";
					Query query = context.createQuery(getDuplicateQuery);
					query.setParameter("category", Integer.parseInt(category));
					Integer id = 0;
					if (!Utils.isNullOrEmpty(recordIds)) {
						for (Integer recordId : recordIds) {
							Integer recId = 0;
							if (!Utils.isNullOrEmpty(recordId)) {
								recId = recordId;
							}
							if (!idsList.contains(recId)) {
								idsList.add(recId);
							}
						}
					} else {
						if (!idsList.contains(id))
							idsList.add(id);
					}
					query.setParameter("recordIds", idsList);
					List<EmpApplnSubjectCategoryDepartmentDBO> empApplnSubjectCategoryDepartment = query
							.getResultList();
					if (empApplnSubjectCategoryDepartment.size() > 0) {
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
	public List<EmpApplnSubjectCategoryDepartmentDBO> isEdit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnSubjectCategoryDepartmentDBO>>() {
			@Override
			public List<EmpApplnSubjectCategoryDepartmentDBO> onRun(EntityManager context) throws Exception {
				String geteditQuery = "from EmpApplnSubjectCategoryDepartmentDBO scd "
						            +" where scd.subject.id =:id and scd.recordStatus='A'";
				Query query = context.createQuery(geteditQuery);
				query.setParameter("id", Integer.parseInt(id));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});

	}
	@SuppressWarnings("unchecked")
	public List<EmpApplnSubjectCategoryDepartmentDBO> isSave(String category) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnSubjectCategoryDepartmentDBO>>() {
			@Override
			public List<EmpApplnSubjectCategoryDepartmentDBO> onRun(EntityManager context) throws Exception {
				String getsaveQuery = "from EmpApplnSubjectCategoryDepartmentDBO scd where scd.subject.id=:ID and scd.recordStatus='A'";
				Query query = context.createQuery(getsaveQuery);
				query.setParameter("ID", Integer.parseInt(category));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});

	}

	@SuppressWarnings("unchecked")
	public List<EmpApplnSubjectCategoryDepartmentDBO> isdelete(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnSubjectCategoryDepartmentDBO>>() {
			@Override
			public List<EmpApplnSubjectCategoryDepartmentDBO> onRun(EntityManager context) throws Exception {
				String getdeleteQuery = "from EmpApplnSubjectCategoryDepartmentDBO scd where scd.subject.id =:id and scd.recordStatus='A'";
				Query query = context.createQuery(getdeleteQuery);
				query.setParameter("id", Integer.parseInt(id));
				return query.getResultList();
			}  			
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
