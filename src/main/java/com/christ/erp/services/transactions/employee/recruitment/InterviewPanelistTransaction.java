package com.christ.erp.services.transactions.employee.recruitment;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewPanelistDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewUniversityExternalsDBO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewExternalPanelistDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewPanelistDTO;

@Service
public class InterviewPanelistTransaction {
	private static volatile InterviewPanelistTransaction interviewPanellistTransaction = null;

	public static InterviewPanelistTransaction getInstance() {
		if (interviewPanellistTransaction == null) {
			interviewPanellistTransaction = new InterviewPanelistTransaction();
		}
		return interviewPanellistTransaction;
	}
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getInternalPanelMembers(String departmentId, String locationId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_INTRERNAL_PANEL = "select erp_users.erp_users_id as ID,emp.emp_name as Text from erp_users"
				+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
				+ " inner join erp_campus_department_mapping on emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id"
				+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id where"
				+ " erp_campus.erp_location_id=:locationId and erp_campus_department_mapping.erp_department_id=:departmentId and erp_users.record_status='A' order by emp.emp_name ASC ";
				Query query = context.createNativeQuery(SELECT_INTRERNAL_PANEL, Tuple.class);				
				query.setParameter("locationId", locationId);
				query.setParameter("departmentId", departmentId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getExternalPanelMembers(String departmentId, String locationId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_EXTERNAL_PANEL = "select erp_users.erp_users_id as ID, CONCAT(emp.emp_name, ' (', erp_department.department_name, ')') as Text from erp_users"
						+ " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A' "
						+ " inner join erp_campus_department_mapping on emp.erp_campus_department_mapping_id = erp_campus_department_mapping.erp_campus_department_mapping_id"
						+ " inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
						+ " inner join erp_department on erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id  "
						+ " where erp_campus.erp_location_id=:locationId and erp_campus_department_mapping.erp_department_id!=:departmentId and erp_users.record_status='A' order by emp.emp_name ASC ";
				Query query = context.createNativeQuery(SELECT_EXTERNAL_PANEL, Tuple.class);				
				query.setParameter("locationId", locationId);
				query.setParameter("departmentId", departmentId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean saveOrUpdate(List<EmpInterviewPanelistDBO> dboList) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				dboList.forEach(dbo -> {
					if (Utils.isNullOrEmpty(dbo.id)) {
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

	public boolean saveOrUpdateUniversityExternalPanel(EmpInterviewUniversityExternalsDBO dbo) throws Exception {
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
	
	public List<EmpInterviewPanelistDBO> getDuplicate(EmpInterviewPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpInterviewPanelistDBO>>() {
			@Override
			public List<EmpInterviewPanelistDBO> onRun(EntityManager context) throws Exception {
				Query q = context.createQuery(
						"from EmpInterviewPanelistDBO bo where bo.erpLocationDBO.id=:locationId and bo.erpAcademicYearDBO.id=:academicyearId and bo.erpDepartmentDBO.id=:departmentId and bo.recordStatus='A'");
				q.setParameter("locationId", Integer.parseInt(data.location.id));
				q.setParameter("academicyearId", Integer.parseInt(data.academicYear.id));
				q.setParameter("departmentId", Integer.parseInt(data.department.id));
				@SuppressWarnings("unchecked")
				List<EmpInterviewPanelistDBO> mappings = q.getResultList();
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<EmpInterviewUniversityExternalsDBO> getDuplicateExternalPanel(EmpInterviewExternalPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpInterviewUniversityExternalsDBO>>() {
			@Override
			public List<EmpInterviewUniversityExternalsDBO> onRun(EntityManager context) throws Exception {
				Query q = context.createQuery(
						"from EmpInterviewUniversityExternalsDBO bo where bo.panelName=:panelName and bo.recordStatus='A'");
				q.setParameter("panelName", data.name);
				@SuppressWarnings("unchecked")
				List<EmpInterviewUniversityExternalsDBO> mappings = q.getResultList();
				return mappings;
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
				String str = "select DISTINCT erp_academic_year.erp_academic_year_id as academicYearId,erp_academic_year.academic_year_name as academicYearName,"
						+ " erp_location.erp_location_id as locationId,erp_location.location_name as locationName,"
						+ " erp_department.erp_department_id as deptId,erp_department.department_name as deptName"
						+ " from emp_interview_panelist"
						+ " inner join erp_location ON erp_location.erp_location_id = emp_interview_panelist.erp_location_id"
						+ " inner join erp_department ON erp_department.erp_department_id = emp_interview_panelist.erp_department_id"
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
						+ " where emp_interview_panelist.record_status = 'A' order by erp_academic_year.academic_year_name,erp_location.location_name,erp_department.department_name ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getGridDataUniversityExternalPanel() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select bo.emp_interview_university_externals_id as id,bo.panelist_name as panelName from emp_interview_university_externals bo where bo.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean deleteUniversityExternalPanel(EmpInterviewExternalPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery(
						"update emp_interview_university_externals set record_status='D' where emp_interview_university_externals_id=:id");
				query.setParameter("id", data.id);				
				return query.executeUpdate() > 0 ? true : false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean delete(EmpInterviewPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery(
						"update emp_interview_panelist set record_status='D' where erp_academic_year_id=:academicYearId and erp_location_id=:locationId and erp_department_id=:departmentId");
				query.setParameter("academicYearId", data.academicYear.id);
				query.setParameter("locationId", data.location.id);
				query.setParameter("departmentId", data.department.id);
				return query.executeUpdate() > 0 ? true : false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getInternalPanel(EmpInterviewPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_interview_panelist.emp_interview_panelist_id as empInterviewPanelId, emp_interview_panelist.internal_erp_users_id as internalPanelId,\r\n"
						+ " emp.emp_name as empName"
						+ " from emp_interview_panelist"
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.internal_erp_users_id"
						+ " inner join emp ON emp.emp_id = erp_users.emp_id"
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
						+ " inner join erp_department ON erp_department.erp_department_id = emp_interview_panelist.erp_department_id"
						+ " inner join erp_location ON erp_location.erp_location_id = emp_interview_panelist.erp_location_id"
						+ " where emp_interview_panelist.erp_academic_year_id =:academicYearId and emp_interview_panelist.erp_location_id =:locationId"
						+ " and emp_interview_panelist.erp_department_id =:departmentId and emp_interview_panelist.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("academicYearId", data.academicYear.id);
				query.setParameter("locationId", data.location.id);
				query.setParameter("departmentId", data.department.id);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getExternalPanel(EmpInterviewPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_interview_panelist.emp_interview_panelist_id as empInterviewPanelId, emp_interview_panelist.external_erp_users_id as externalPanelId,\r\n"
						+ " emp.emp_name as empName"
						+ " from emp_interview_panelist"
						+ " inner join erp_users ON erp_users.erp_users_id = emp_interview_panelist.external_erp_users_id"
						+ " inner join emp ON emp.emp_id = erp_users.emp_id"
						+ " inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = emp_interview_panelist.erp_academic_year_id"
						+ " inner join erp_department ON erp_department.erp_department_id = emp_interview_panelist.erp_department_id"
						+ " inner join erp_location ON erp_location.erp_location_id = emp_interview_panelist.erp_location_id"
						+ " where emp_interview_panelist.erp_academic_year_id = :academicYearId and emp_interview_panelist.erp_location_id =:locationId"
						+ " and emp_interview_panelist.erp_department_id =:departmentId and emp_interview_panelist.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("academicYearId", data.academicYear.id);
				query.setParameter("locationId", data.location.id);
				query.setParameter("departmentId", data.department.id);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getUniversityExternalPanel(EmpInterviewPanelistDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_interview_panelist.emp_interview_panelist_id as empInterviewPanelId,emp_interview_panelist.emp_interview_university_externals_id as empUniversityExternalPanelId,"
						+ " emp_interview_university_externals.panelist_name as empName from emp_interview_panelist" 
						+ " inner join emp_interview_university_externals" 
						+ " ON emp_interview_university_externals.emp_interview_university_externals_id = emp_interview_panelist.emp_interview_university_externals_id"
						+ " where emp_interview_panelist.erp_academic_year_id = :academicYearId and emp_interview_panelist.erp_location_id = :locationId"
						+ " and emp_interview_panelist.erp_department_id = :departmentId and emp_interview_panelist.record_status = 'A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("academicYearId", data.academicYear.id);
				query.setParameter("locationId", data.location.id);
				query.setParameter("departmentId", data.department.id);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	  public EmpInterviewUniversityExternalsDBO getUniversityExtrenalPanelProfile(String id) throws Exception {
			return DBGateway.runJPA(new ISelectGenericTransactional<EmpInterviewUniversityExternalsDBO>() {
				@Override
				public EmpInterviewUniversityExternalsDBO onRun(EntityManager context) throws Exception {
					return context.find(EmpInterviewUniversityExternalsDBO.class, Integer.parseInt(id));
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		}
	  public EmpInterviewUniversityExternalsDBO getExternalDBO(int id){
			String queryString = "select bo from EmpInterviewUniversityExternalsDBO bo left join fetch bo.panelistDocumentUrlDBO"
							      +" where bo.id = :id and bo.recordStatus = 'A' "; 
			return sessionFactory.withSession(s->s.createQuery(queryString, EmpInterviewUniversityExternalsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
		}
		 
}

