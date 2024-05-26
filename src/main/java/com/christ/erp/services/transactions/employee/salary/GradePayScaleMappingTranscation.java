package com.christ.erp.services.transactions.employee.salary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;

public class GradePayScaleMappingTranscation {
	private static volatile GradePayScaleMappingTranscation gradePayScaleMappingTranscation = null;

	public static GradePayScaleMappingTranscation getInstance() {
		if (gradePayScaleMappingTranscation == null) {
			gradePayScaleMappingTranscation = new GradePayScaleMappingTranscation();
		}
		return gradePayScaleMappingTranscation;
	}

	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "SELECT\n" + " PSGM.emp_pay_scale_grade_mapping_id AS 'ID', \n"
						+ " PSG.emp_employee_category_id AS 'Category',\n"
						+ " EC.employee_category_name as 'CategoryName',\n"
						+ " PSGM.emp_pay_scale_grade_id AS 'Grade',\n" + " PSG.grade_name as 'GradeName',\n"
						+ " PSGM.pay_scale_revised_year AS 'RevisedYear'  \n"
						+ "FROM emp_pay_scale_grade_mapping AS PSGM  \n"
						+ "inner join emp_pay_scale_grade PSG ON PSG.emp_pay_scale_grade_id = PSGM.emp_pay_scale_grade_id \n"
						+ "inner join emp_employee_category as EC on EC.emp_employee_category_id = PSG.emp_employee_category_id \n"
						+ "WHERE  PSGM.record_status='A' order by EC.employee_category_name";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpPayScaleGradeMappingDBO empPayScaleGradeMappingDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empPayScaleGradeMappingDBO.id == null) {
					context.persist(empPayScaleGradeMappingDBO);
				} else {
					context.merge(empPayScaleGradeMappingDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpPayScaleGradeMappingDetailDBO empPayScaleGradeMappingDetailDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empPayScaleGradeMappingDetailDBO.id == null) {
					context.persist(empPayScaleGradeMappingDetailDBO);
				} else {
					context.merge(empPayScaleGradeMappingDetailDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpPayScaleGradeMappingDetailDBO> getPayScaleGradeMappingDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpPayScaleGradeMappingDetailDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpPayScaleGradeMappingDetailDBO> onRun(EntityManager context) throws Exception {
				Query query = context
						.createQuery("from EmpPayScaleGradeMappingDetailDBO bo   where  bo.recordStatus='A'");
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpPayScaleGradeMappingDetailDBO getEmpPayScaleGradeMappingDetailDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleGradeMappingDetailDBO>() {
			@Override
			public EmpPayScaleGradeMappingDetailDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpPayScaleGradeMappingDetailDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpPayScaleGradeMappingDBO getEmpPayScaleGradeMappingDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleGradeMappingDBO>() {
			@Override
			public EmpPayScaleGradeMappingDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpPayScaleGradeMappingDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public static EmpPayScaleGradeDBO getEmpPayScaleGradeDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpPayScaleGradeDBO>() {
			@Override
			public EmpPayScaleGradeDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpPayScaleGradeDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpPayScaleGradeMappingDBO> getEmpPayScaleGradeMappingDBO() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpPayScaleGradeMappingDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<EmpPayScaleGradeMappingDBO> onRun(EntityManager context) throws Exception {
				Query payScaleGradeMappingQuery = context
						.createQuery("from EmpPayScaleGradeMappingDBO bo   where  bo.recordStatus='A'");
				return payScaleGradeMappingQuery.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Boolean isDuplicate(String revisedYear, String grade, String category, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getTemplateNameQuery = "SELECT HDR.emp_pay_scale_grade_mapping_id "
							+ "FROM emp_pay_scale_grade_mapping AS HDR " + "INNER JOIN  emp_pay_scale_grade AS CTG "
							+ "ON CTG.emp_pay_scale_grade_id = HDR.emp_pay_scale_grade_id "
							+ "INNER JOIN emp_employee_category AS EC ON EC.emp_employee_category_id = CTG.emp_employee_category_id "
							+ "WHERE HDR.record_status = 'A' AND HDR.emp_pay_scale_grade_id=:gradeId AND HDR.pay_scale_revised_year=:revisedYear and CTG.emp_employee_category_id=:categoryId ";
					if (id != null && !id.isEmpty()) {
						getTemplateNameQuery += "and HDR.emp_pay_scale_grade_mapping_id != :id";
					}
					Query query = context.createNativeQuery(getTemplateNameQuery);
					if (id != null && !id.isEmpty()) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("revisedYear", revisedYear);
					query.setParameter("gradeId", grade);
					query.setParameter("categoryId", category);
					if (query.getResultList().size() > 0) {
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

	public Boolean deleteSubRecord(Integer header, List<Integer> detailIds) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getSubRecord = "DELETE FROM emp_pay_scale_grade_mapping_detail \n"
							+ " WHERE emp_pay_scale_grade_mapping_id = :header_id \n"
							+ " AND emp_pay_scale_grade_mapping_detail_id NOT IN (:detail_ids)";
					Query qry = context.createNativeQuery(getSubRecord);
					qry.setParameter("header_id", header);
					qry.setParameter("detail_ids", detailIds);
					qry.executeUpdate();
					return true;
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
