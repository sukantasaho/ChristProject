package com.christ.erp.services.transactions.employee.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnNumberGenerationDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnRegistrationsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import reactor.core.publisher.Mono;

@Repository
@SuppressWarnings("unchecked")
public class EmployeeApplicationTransaction {
	
	private static volatile EmployeeApplicationTransaction employeeApplicationTransaction = null;
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
    public static EmployeeApplicationTransaction getInstance() {
        if(employeeApplicationTransaction==null) {
        	employeeApplicationTransaction = new EmployeeApplicationTransaction();
        }
        return employeeApplicationTransaction;
    }
    

    public EmpApplnEntriesDBO getEmployeeApplication(String applicationNo) throws Exception{
    	return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnEntriesDBO>() {
			@Override
			public EmpApplnEntriesDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from EmpApplnEntriesDBO bo where bo.recordStatus='A' and bo.applicationNo= :applicationNo");
				query.setParameter("applicationNo", Integer.parseInt(applicationNo));
				return (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
    }

	/*public List<EmpApplnAddtnlInfoHeadingDBO> getResearchDetails(Integer employeeCategoryId, boolean isTypeResearch) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnAddtnlInfoHeadingDBO>>() {
			@Override
			public List<EmpApplnAddtnlInfoHeadingDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("select head from EmpApplnAddtnlInfoHeadingDBO head where head.recordStatus='A' and head.empEmployeeCategoryId.id=:employeeCategoryId order by head.headingDisplayOrder asc");
				query.setParameter("employeeCategoryId", employeeCategoryId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}*/

	public List<EmpApplnAddtnlInfoHeadingDBO> getResearchDetails(Integer employeeCategoryId, boolean isTypeResearch, EntityManager context) throws Exception{
		Query query = context.createQuery("select head from EmpApplnAddtnlInfoHeadingDBO head where head.recordStatus='A' and head.empEmployeeCategoryId.id=:employeeCategoryId and head.isTypeResearch=:isTypeResearch order by head.headingDisplayOrder asc");
		query.setParameter("employeeCategoryId", employeeCategoryId);
		query.setParameter("isTypeResearch", isTypeResearch);
		return query.getResultList();
	}

	public boolean submitEmployeeApplication(EmpApplnEntriesDBO empApplnEntriesDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(empApplnEntriesDBO) || Utils.isNullOrEmpty(empApplnEntriesDBO.id) || empApplnEntriesDBO.id==0) {
                    context.persist(empApplnEntriesDBO);
                }else {
                    context.merge(empApplnEntriesDBO);
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}

	public <T> T find(Class<T> className, Integer primaryKey) throws Exception{
    	return DBGateway.runJPA(new ISelectGenericTransactional<T>() {
			@Override
			public T onRun(EntityManager context) throws Exception {
				return context.find(className, primaryKey);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
    }
	
//	public synchronized Integer getApplicationNumber(Integer userId) throws Exception{
//    	return DBGateway.runJPA(new ICommitGenericTransactional<Integer>() {
//			@Override
//			public Integer onRun(EntityManager context) throws Exception {
//				Integer applnNo = null;
//				try {
//					Query query = context.createQuery("select dbo from EmpApplnNumberGenerationDBO dbo where dbo.recordStatus='A' and dbo.isCurrentRange=1");
//					EmpApplnNumberGenerationDBO empApplnNumberGenerationDBO = (EmpApplnNumberGenerationDBO) Utils.getUniqueResult(query.getResultList());
//					if(Utils.isNullOrEmpty(empApplnNumberGenerationDBO)) {
//						return null;
//					} else if (Utils.isNullOrEmpty(empApplnNumberGenerationDBO.currentApplnNo)) {
//						applnNo = empApplnNumberGenerationDBO.getApplnNumberFrom();
//					} else {
//						applnNo = empApplnNumberGenerationDBO.currentApplnNo;
//					}
//					empApplnNumberGenerationDBO.modifiedUsersId = userId;
//					empApplnNumberGenerationDBO.recordStatus = 'A';
//					empApplnNumberGenerationDBO.currentApplnNo = applnNo + 1;
//					context.merge(empApplnNumberGenerationDBO);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return !Utils.isNullOrEmpty(applnNo) ? applnNo : null;
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

	public synchronized Integer getApplicationNumber(EntityManager context, Integer userId) throws Exception{
		Integer applnNo = null;
		if(!Utils.isNullOrEmpty(context)){
			try {
				Query query = context.createQuery("select dbo from EmpApplnNumberGenerationDBO dbo where dbo.recordStatus='A' and dbo.isCurrentRange=1");
				query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
				EmpApplnNumberGenerationDBO empApplnNumberGenerationDBO = (EmpApplnNumberGenerationDBO) Utils.getUniqueResult(query.getResultList());
				if(Utils.isNullOrEmpty(empApplnNumberGenerationDBO)) {
					return null;
				} else if (Utils.isNullOrEmpty(empApplnNumberGenerationDBO.currentApplnNo)) {
					applnNo = empApplnNumberGenerationDBO.getApplnNumberFrom();
				} else {
					applnNo = empApplnNumberGenerationDBO.currentApplnNo;
				}
				empApplnNumberGenerationDBO.modifiedUsersId = userId;
				empApplnNumberGenerationDBO.recordStatus = 'A';
				empApplnNumberGenerationDBO.currentApplnNo = applnNo + 1;
				context.merge(empApplnNumberGenerationDBO);
			} catch (Exception e) {
				e.printStackTrace();
				context.getTransaction().rollback();
			}
		}
		return !Utils.isNullOrEmpty(applnNo) ? applnNo : null;
	}

	public List<EmpApplnEntriesDBO> getAppliedEmployeeApplications(String empApplicationRegistrationId) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpApplnEntriesDBO>>() {
			@Override
			public List<EmpApplnEntriesDBO> onRun(EntityManager context) throws Exception {
				List<EmpApplnEntriesDBO> empApplnEntriesDBOS = new ArrayList<>();
				Query query = context.createQuery("select bo from EmpApplnEntriesDBO bo" +
						" where bo.empApplnRegistrationsDBO.id=:empApplicationRegistrationId and bo.recordStatus='A'",Tuple.class);
				query.setParameter("empApplicationRegistrationId", Integer.parseInt(empApplicationRegistrationId));
				empApplnEntriesDBOS = query.getResultList();
				return empApplnEntriesDBOS;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean registerApplicant(EmpApplnRegistrationsDBO empApplnRegistrationsDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(empApplnRegistrationsDBO) || Utils.isNullOrEmpty(empApplnRegistrationsDBO.id) || empApplnRegistrationsDBO.id==0) {
					context.persist(empApplnRegistrationsDBO);
				}else {
					context.merge(empApplnRegistrationsDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpApplnRegistrationsDBO isApplicantRegistered(String email) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnRegistrationsDBO>() {
			@Override
			public EmpApplnRegistrationsDBO onRun(EntityManager context) throws Exception {
				//boolean isRegistered = false;
				Query query = context.createQuery("from EmpApplnRegistrationsDBO bo where bo.recordStatus='A' and bo.email=:email");
				query.setParameter("email", email);
				EmpApplnRegistrationsDBO bo = (EmpApplnRegistrationsDBO) Utils.getUniqueResult(query.getResultList());
				/*if(!Utils.isNullOrEmpty(bo)){
					isRegistered = true;
				}*/
				return bo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean updateApplicantPassword(EmpApplnRegistrationsDBO bo) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				context.merge(bo);
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public EmpApplnRegistrationsDBO getRegistrationDetails(String empApplicationRegistrationId) {
		String str = " select dbo from EmpApplnRegistrationsDBO dbo"
				+" where dbo.recordStatus ='A' and dbo.id =: empApplicationRegistrationId";
		EmpApplnRegistrationsDBO empApplnRegistrationsDBO = 
				sessionFactory.withSession(s -> s.createQuery(str, EmpApplnRegistrationsDBO.class).setParameter("empApplicationRegistrationId", Integer.parseInt(empApplicationRegistrationId)).getSingleResultOrNull()).await().indefinitely();	
		return empApplnRegistrationsDBO;
	}

    public ErpTemplateDBO getApplicantEmailOtpTemplate(String templateCode) {
		return sessionFactory.withSession(s -> s.createQuery("from ErpTemplateDBO bo where bo.recordStatus='A' and bo.templateCode=:templateCode", ErpTemplateDBO.class)
				.setParameter("templateCode", templateCode).getSingleResultOrNull()).await().indefinitely();
    }

	public List<Tuple> getEmployeeApplicationAdditionalInformations(Integer applicationEntriesId) {
		String str = "select emp_appln_addtnl_info_heading.emp_appln_addtnl_info_heading_id as headingId, emp_appln_addtnl_info_heading.addtnl_info_heading_name as headingName,emp_appln_addtnl_info_heading.heading_display_order as headingDisplayOrder, " +
				" emp_appln_addtnl_info_parameter.emp_appln_addtnl_info_parameter_id as parameterId, emp_appln_addtnl_info_parameter.addtnl_info_parameter_name as parameterName,emp_appln_addtnl_info_parameter.parameter_display_order as parameterDisplayOrder, " +
				" emp_appln_addtnl_info_entries.emp_appln_addtnl_info_entries_id as entriesId, emp_appln_addtnl_info_entries.emp_appln_addtnl_info_parameter_id, emp_appln_addtnl_info_entries.addtnl_info_value as additionalInfoValue " +
				" from emp_appln_addtnl_info_entries " +
				" inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_addtnl_info_entries.emp_appln_entries_id and emp_appln_entries.record_status='A' " +
				" inner join emp_appln_addtnl_info_parameter ON emp_appln_addtnl_info_parameter.emp_appln_addtnl_info_parameter_id = emp_appln_addtnl_info_entries.emp_appln_addtnl_info_parameter_id and emp_appln_addtnl_info_parameter.record_status='A' " +
				" inner join emp_appln_addtnl_info_heading ON emp_appln_addtnl_info_heading.emp_appln_addtnl_info_heading_id = emp_appln_addtnl_info_parameter.emp_appln_addtnl_info_heading_id and emp_appln_addtnl_info_heading.record_status='A' " +
				" where emp_appln_addtnl_info_entries.record_status='A' " +
				" and emp_appln_entries.emp_appln_entries_id=:applicationEntriesId " +
				" and (emp_appln_addtnl_info_heading.is_type_research=0 or emp_appln_addtnl_info_heading.is_type_research is null)" +
				" order by emp_appln_addtnl_info_heading.heading_display_order asc, emp_appln_addtnl_info_parameter.parameter_display_order asc ";
		return (sessionFactory.withSession(s-> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(applicationEntriesId)) {
				query.setParameter("applicationEntriesId", applicationEntriesId);
			}
			return query.getResultList();
		}).await().indefinitely());
	}
	
	public Tuple getAppliedSubject(String applicationNumber) {
		String str = " select subjectCategory.is_academic as acd,cast(group_concat( DISTINCT subjectCategory.subject_category_name)as char) AS 'Text'  "
				+ " from emp_appln_subj_specialization_pref subjectPref "
				+ " inner join emp_appln_entries e ON e.emp_appln_entries_id = subjectPref.emp_appln_entries_id "
				+ " inner join emp_appln_subject_category subjectCategory ON subjectCategory.emp_appln_subject_category_id = subjectPref.emp_appln_subject_category_id "
				+ " where subjectCategory.record_status='A' and subjectPref.record_status='A' "
				+ " and e.application_no=:applicationNumber and e.record_status='A' "
				+ " group by subjectCategory.is_academic ";
		return sessionFactory.withSession(s-> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(applicationNumber)) {
				query.setParameter("applicationNumber", applicationNumber);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
	}
	
	public Tuple getAppliedSubjectSpecialization(String applicationNumber) {
		String str = " select  cast(group_concat(subjectCategorySpecialization.subject_category_specialization_name) as char) AS 'Text' "
				+ " from emp_appln_subj_specialization_pref subjectPref "
				+ " inner join emp_appln_entries e ON e.emp_appln_entries_id = subjectPref.emp_appln_entries_id "
				+ " inner join emp_appln_subject_category_specialization subjectCategorySpecialization ON subjectCategorySpecialization.emp_appln_subject_category_specialization_id = subjectPref.emp_appln_subject_category_specialization_id "
				+ " where subjectCategorySpecialization.record_status='A' "
				+ " and subjectPref.record_status='A' "
				+ " and e.application_no=:applicationNumber"
				+ " and e.record_status='A'";
		return sessionFactory.withSession(s-> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(applicationNumber)) {
				query.setParameter("applicationNumber", applicationNumber);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
	}


	public Tuple getAppliedLocationPref(String applicationNumber) {
		String str = " Select cast(group_concat(DISTINCT erp_location.location_name)as char) as text from emp_appln_location_pref"
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_location_pref.emp_appln_entries_id and emp_appln_entries.record_status = 'A'"
				+ " inner join erp_location on erp_location.erp_location_id = emp_appln_location_pref.erp_location_id and erp_location.record_status = 'A'"
				+ " where emp_appln_location_pref.record_status = 'A'and emp_appln_entries.application_no=:applicationNumber";
		return sessionFactory.withSession(s-> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(applicationNumber)) {
				query.setParameter("applicationNumber", applicationNumber);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
	}
}
