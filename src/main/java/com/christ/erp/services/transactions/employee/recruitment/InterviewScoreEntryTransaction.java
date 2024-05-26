package com.christ.erp.services.transactions.employee.recruitment;

import java.math.BigInteger;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewScoreDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")

@Repository
public class InterviewScoreEntryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	//	public static volatile InterviewScoreEntryTransaction interviewScoreEntryTransaction = null;
	//	public static InterviewScoreEntryTransaction getInstance() {
	//		if(interviewScoreEntryTransaction== null) 
	//			interviewScoreEntryTransaction =  new InterviewScoreEntryTransaction();
	//		return interviewScoreEntryTransaction;
	//	}

	public Tuple getEmployeeDetails(String applicationNumber) throws Exception{		
		String str = "select emp_appln_entries.emp_appln_entries_id as applnEntryId, emp_employee_category_id as categoryId, "
				+" emp_appln_entries.applicant_name as name ,emp_appln_entries.application_no as applnNo,emp_appln_entries.application_current_process_status as workFlowStatus, "
				+" erp_work_flow_process.process_code as processCode, erp_work_flow_process.process_order as processOrder "
				+" from emp_appln_entries "
				+" inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = emp_appln_entries.application_current_process_status "
				+" where emp_appln_entries.application_no =:appln_no and emp_appln_entries.record_status='A' and erp_work_flow_process.record_status='A'";
		Tuple value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("appln_no", applicationNumber);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return value;
	}

	public List<Tuple> getInterviewtemplate(ApiResult<InterviewScoreEntryDTO> resultDto) throws Exception{		
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {       	
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String externalName = resultDto.dto.interviewPanelMember.label;
				String SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY_MARK_ENTERED = "select ait.emp_appln_interview_template_id as 'template_id', ait.interview_name as 'interview_name', " + 
						" ait.is_panelist_comment_required as 'comment_required', itg.emp_appln_interview_template_group_id as 'group_id', " + 
						" itg.template_group_heading as 'group_heading',itg.heading_order_no as 'group_order', " + 
						" tgd.emp_appln_interview_template_group_details_id as 'detail_id',tgd.parameter_name as 'detail_parameter', " + 
						" tgd.parameter_order_no as 'detail_order',tgd.parameter_max_score as 'detail_max_score',isd.score_entered as 'obtained_score' ,ise.comments as 'comments' ," + 
						" isd.emp_appln_interview_score_details_id  as 'details_id', ise.emp_appln_interview_score_id as 'score_entry_id'" + 
						" from emp_appln_interview_template ait " + 
						" inner join emp_appln_interview_template_group itg on ait.emp_appln_interview_template_id = itg.emp_appln_interview_template_id " + 
						" inner join emp_appln_interview_template_group_details tgd on tgd.emp_appln_interview_template_group_id = itg.emp_appln_interview_template_group_id " + 
						" inner join emp_appln_interview_score_details isd on isd.emp_appln_interview_template_group_details_id = tgd.emp_appln_interview_template_group_details_id and isd.record_status='A' " + 
						" inner join emp_appln_interview_score ise on ise.emp_appln_interview_score_id = isd.emp_appln_interview_score_id and ise.record_status='A' " + 
						" inner join emp_appln_entries ae on ae.emp_appln_entries_id = ise.emp_appln_entries_id and ae.record_status='A' "+ 
						" left join erp_users eu ON eu.erp_users_id = ise.erp_users_id and eu.record_status='A' " +
						" left join emp_interview_university_externals ue ON ue.emp_interview_university_externals_id = ise.emp_interview_university_externals_id and ue.record_status='A' "+
						" where ait.record_status='A' and ait.emp_employee_category_id=:category_id " + 
						" and ae.application_no=:appln_no ";
				if(!Utils.isNullOrEmpty(externalName) && !externalName.contains("External")) {
					SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY_MARK_ENTERED += " and ise.erp_users_id =:panel_member ";
				} else {
					SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY_MARK_ENTERED += " and ise.emp_interview_university_externals_id =:panel_member";
				}
				SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY_MARK_ENTERED += " order by itg.heading_order_no,tgd.parameter_order_no ASC";

				Query templateParametersMarkEnteredQuery = context.createNativeQuery(SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY_MARK_ENTERED, Tuple.class);
				templateParametersMarkEnteredQuery.setParameter("panel_member", resultDto.dto.interviewPanelMember.value);
				templateParametersMarkEnteredQuery.setParameter("category_id", resultDto.dto.categoryId);
				templateParametersMarkEnteredQuery.setParameter("appln_no", resultDto.dto.applicationNumber);
				return templateParametersMarkEnteredQuery.getResultList(); 
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getMaxScoreDetails(InterviewScoreEntryDTO interviewScoreEntryDTO) throws Exception{		
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {       	
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_MAX_SCORE_ENTRY_INTERVEIW_SCORE = "select tgd.emp_appln_interview_template_group_details_id as 'detail_id',tgd.parameter_name as 'detail_parameter',  " + 
						"tgd.parameter_max_score as 'detail_max_score' from emp_appln_interview_template ait  " + 
						"inner join emp_appln_interview_template_group itg on ait.emp_appln_interview_template_id = itg.emp_appln_interview_template_id  " + 
						"inner join emp_appln_interview_template_group_details tgd on tgd.emp_appln_interview_template_group_id = itg.emp_appln_interview_template_group_id  " + 
						"where ait.record_status='A' and itg.record_status='A' and tgd.record_status='A' and ait.emp_employee_category_id=:category_id  " + 
						"order by itg.heading_order_no,tgd.parameter_order_no";
				Query checkMaxScore = context.createNativeQuery(SELECT_MAX_SCORE_ENTRY_INTERVEIW_SCORE, Tuple.class);
				checkMaxScore.setParameter("category_id", interviewScoreEntryDTO.categoryId);
				return checkMaxScore.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpApplnInterviewScoreDBO empApplnInterviewScoreDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(empApplnInterviewScoreDBO.empApplnInterviewScoreId==0) {
					context.persist(empApplnInterviewScoreDBO);
				}
				else {
					context.merge(empApplnInterviewScoreDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpApplnEntriesDBO empApplnEntriesDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				context.merge(empApplnEntriesDBO);
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				context.persist(erpWorkFlowProcessStatusLogDBO);
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}	

	public List<Tuple> getInterviewtemplateNew(ApiResult<InterviewScoreEntryDTO> resultDto) throws	Exception{		
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {        	
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY = "select ait.emp_appln_interview_template_id as 'template_id', ait.interview_name as 'interview_name',   " + 
						" ait.is_panelist_comment_required as 'comment_required', itg.emp_appln_interview_template_group_id as 'group_id',  " + 
						" itg.template_group_heading as 'group_heading',itg.heading_order_no as 'group_order',  " + 
						" tgd.emp_appln_interview_template_group_details_id as 'detail_id',tgd.parameter_name as 'detail_parameter',  " + 
						" tgd.parameter_order_no as 'detail_order',tgd.parameter_max_score as 'detail_max_score'  " + 
						" from emp_appln_interview_template ait " + 
						" inner join emp_appln_interview_template_group itg on ait.emp_appln_interview_template_id = itg.emp_appln_interview_template_id and itg.record_status='A' " + 
						" inner join emp_appln_interview_template_group_details tgd on tgd.emp_appln_interview_template_group_id = itg.emp_appln_interview_template_group_id and tgd.record_status='A' " +  
						" where ait.record_status='A' and ait.emp_employee_category_id=:category_id  " + 
						" order by itg.heading_order_no,tgd.parameter_order_no";
				Query templateParametersMarkEnteredQuery = context.createNativeQuery(SELECT_INTERVEW_TEMPLATE_FOR_CATEGORY, Tuple.class);
				templateParametersMarkEnteredQuery.setParameter("category_id", resultDto.dto.categoryId);				
				return templateParametersMarkEnteredQuery.getResultList(); 
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getInterviewScheduleId(InterviewScoreEntryDTO dto) throws Exception {		
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			String externalName = dto.interviewPanelMember.label;
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String query = "select sch.emp_appln_interview_schedules_id as schedule_id from emp_appln_interview_schedules sch " + 
						" inner join emp_appln_interview_panel panel on panel.emp_appln_interview_schedules_id = sch.emp_appln_interview_schedules_id " + 
						" inner join emp_appln_entries emp_app on emp_app.emp_appln_entries_id = sch.emp_appln_entries_id " + 
						" where emp_app.application_no=:appln_no and sch.record_status='A' and panel.record_status='A' and emp_app.record_status='A' " ;
				if(!externalName.contains("External")) {
					query += " and panel.erp_users_id =:emp_panel " ;  
				} else {
					query += " and panel.emp_interview_university_externals_id =:emp_panel " ; 	
				}
				query += " order by sch.interview_date_time desc";
				Query schedule = context.createNativeQuery(query, Tuple.class);
				schedule.setParameter("appln_no", dto.applicationNumber);
				schedule.setParameter("emp_panel", dto.interviewPanelMember.value);
				return schedule.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ErpWorkFlowProcessDBO getTemplate(String templateCode) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpWorkFlowProcessDBO>() {
			@Override
			public ErpWorkFlowProcessDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpWorkFlowProcessDBO where processCode=:templateCode and recordStatus='A'");
				query.setParameter("templateCode", templateCode);
				return (ErpWorkFlowProcessDBO) Utils.getUniqueResult(query.getResultList()) ;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public EmpApplnEntriesDBO getApplicantDetails(Integer empApplnEntryId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnEntriesDBO>() {
			@Override
			public EmpApplnEntriesDBO onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from EmpApplnEntriesDBO where id=:empApplnEntryId and recordStatus='A'");
				query.setParameter("empApplnEntryId", empApplnEntryId);
				return (EmpApplnEntriesDBO) Utils.getUniqueResult(query.getResultList()) ;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Mono<List<SelectDTO>> getPaneListForAppln(Integer applicationNumber) {
//		String str = " select dbo from EmpApplnInterviewPanelDBO dbo"
//				+" inner join dbo.empApplnInterviewSchedulesDBO edbo on edbo.recordStatus ='A'"
//				+" left join dbo.erpUsersDBO udbo on udbo.recordStatus ='A'"
//				+" left join dbo.empInterviewUniversityExternalsDBO udbo on udbo.recordStatus ='A'"
//				+" where dbo.recordStatus='A' and edbo.empApplnEntriesDBO.applicationNo =:applicationNumber";
		String str1 = "select new com.christ.erp.services.dto.common.SelectDTO "
				+ " (CASE WHEN dbo.isInternalPanel = 1 "
				+ " THEN udbo.id"
				+ " ELSE ifnull(eidbo.id, udbo.id) "
				+ " END as id,"
				+ " CASE WHEN dbo.isInternalPanel = 1 "
				+ " THEN udbo.empDBO.empName "
				+ " ELSE ifnull(concat(eidbo.panelName,'(External)'), udbo.empDBO.empName)"
				+ " END as label)"
				+ " from EmpApplnInterviewPanelDBO dbo"
				+" inner join dbo.empApplnInterviewSchedulesDBO edbo on edbo.recordStatus ='A'"
				+" left join dbo.erpUsersDBO udbo "
				+" left join dbo.empInterviewUniversityExternalsDBO eidbo "
				+" left join udbo.empDBO "
				+" where dbo.recordStatus='A' and edbo.empApplnEntriesDBO.applicationNo =:applicationNumber";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str1, SelectDTO.class).setParameter("applicationNumber", applicationNumber).getResultList()).subscribeAsCompletionStage());
	}

	public BigInteger getPanelMemberCount(Integer empApplnEntryId) {
		String str = "select COUNT(panel.emp_appln_interview_schedules_id) as count"
				+" from emp_appln_interview_panel panel "
				+" inner join emp_appln_interview_schedules sch ON sch.emp_appln_interview_schedules_id = panel.emp_appln_interview_schedules_id "
				+" inner join emp_appln_entries emp_enties ON emp_enties.emp_appln_entries_id = sch.emp_appln_entries_id "
				+" where panel.record_status='A' and sch.record_status='A' and emp_enties.record_status='A' and "                                          
				+" emp_enties.emp_appln_entries_id =:empApplnEntryId group by panel.emp_appln_interview_schedules_id ";
		return sessionFactory.withSession(s->s.createNativeQuery(str, BigInteger.class).setParameter("empApplnEntryId", empApplnEntryId).getSingleResultOrNull()).await().indefinitely();
	}

	public BigInteger getScoreEnteredPanelCount(Integer empApplnEntryId) {
		String str = " select COUNT(sc.emp_appln_interview_schedules_id) as count from emp_appln_interview_score sc "
				+" inner join emp_appln_interview_schedules sch ON sch.emp_appln_interview_schedules_id = sc.emp_appln_interview_schedules_id "
				+" inner join emp_appln_entries emp_entries ON emp_entries.emp_appln_entries_id = sch.emp_appln_entries_id "
				+" where emp_entries.emp_appln_entries_id =:empApplnEntryId and sc.record_status='A' and sch.record_status='A' and emp_entries.record_status='A'"
				+" group by sc.emp_appln_interview_schedules_id ";
		return sessionFactory.withSession(s->s.createNativeQuery(str, BigInteger.class).setParameter("empApplnEntryId", empApplnEntryId).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<SelectDTO> getPanelListByUserId(Integer applicationNumber, String userId) {
//		String str = " select dbo from EmpApplnInterviewPanelDBO dbo"
//				+" inner join dbo.empApplnInterviewSchedulesDBO edbo on edbo.recordStatus ='A'"
//				+" left join dbo.erpUsersDBO udbo on udbo.recordStatus ='A'"
//				+" where dbo.recordStatus='A' and edbo.empApplnEntriesDBO.applicationNo =:applicationNumber and dbo.erpUsersDBO.id =:userId";
		String str = " select new com.christ.erp.services.dto.common.SelectDTO"
				+" (udbo.id, emdbo.empName) "
				+" from EmpApplnInterviewPanelDBO dbo"
				+" inner join dbo.empApplnInterviewSchedulesDBO edbo on edbo.recordStatus ='A'"
				+" left join dbo.erpUsersDBO udbo on udbo.recordStatus ='A'"
				+" left join udbo.empDBO emdbo on emdbo.recordStatus ='A' "
				+" where dbo.recordStatus='A' and edbo.empApplnEntriesDBO.applicationNo =:applicationNumber and dbo.erpUsersDBO.id =:userId";		
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, SelectDTO.class).setParameter("applicationNumber", applicationNumber).setParameter("userId", Integer.parseInt(userId)).getSingleResult()).subscribeAsCompletionStage());
	}

	public void saveOrUpdateList(List<Object> objList) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(objList.toArray())).await().indefinitely();	
	}
}
