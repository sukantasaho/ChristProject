package com.christ.erp.services.transactions.employee.letter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.AppProperties;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueListDTO;
import com.christ.erp.services.helpers.employee.letter.LetterGenerateIssueHelper;

import reactor.core.publisher.Mono;

@Repository
public class LetterGenerateIssueTransaction {

public static volatile LetterGenerateIssueTransaction letterGenerateIssueTransaction= null;
	
	private static final String EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH;
	static {
		EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH = AppProperties.get("erp.emp.letterprint.fileserver.path");
	}
	@Autowired
	LetterGenerateIssueHelper helper;
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public List<Tuple> getEmployeeLocation(int userId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {			
			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
            	Query getEmpCategory = null;
            		String empLocationQuery = "select erp_campus.erp_location_id as location_id From erp_users " + 
            				"inner join emp on emp.emp_id = erp_users.emp_id " + 
            				"inner join erp_campus_department_mapping dep_campus on dep_campus.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " + 
            				"inner join erp_campus on dep_campus.erp_campus_id = erp_campus.erp_campus_id " + 
            				"where erp_users.record_status='A' and emp.record_status='A' and dep_campus.record_status='A' and erp_campus.record_status='A' " + 
            				"and erp_users.erp_users_id=:logged_in_user";
					getEmpCategory = context.createNativeQuery(empLocationQuery,Tuple.class);
					getEmpCategory.setParameter("logged_in_user", userId);	
					return getEmpCategory.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
               throw error;
            }
        });
	}
	public List<Tuple> getLetterRequestlist(LetterGenerateIssueDTO dto, LocalDate fromDate, LocalDate toDate){
		String letterRequestQuery = "select emp_letter_request_id as emp_letter_request_id,    " + 
				" emp.emp_id as emp_id,emp.emp_name as emp_name,    " + 
				" emp.emp_no as emp_no,dep.department_name as dep_name,    " + 
				" erp_campus.campus_name as campus_name,DATE_FORMAT(req.created_time, '%d/%b/%Y') as applied_date,    " + 
				" reason.letter_request_reason_name as reason,let_type.letter_type_name as letter_type_name , " + 
				" erp_work_flow_process.process_code as status_code,erp_work_flow_process.erp_work_flow_process_id as status_id ,"+
				" if(req.letter_issued_date is not null,DATE_FORMAT(req.letter_issued_date, '%d/%b/%Y'),if(erp_work_flow_process.process_code ='LETTER_REQUEST_SUBMISSION',null,DATE_FORMAT(req.modified_time, '%d/%b/%Y'))) as issued_date,req.letter_request_po_comment as comment From emp_letter_request req    " + 
				" inner join emp on emp.emp_id = req.emp_id    " +   					
				" inner join emp_letter_request_reason reason ON reason.emp_letter_request_reason_id = req.emp_letter_request_reason_id    " + 
				" inner join emp_letter_request_type let_type on let_type.emp_letter_request_type_id = req.emp_letter_request_type_id     " + 
				" inner join erp_campus_department_mapping dep_campus on dep_campus.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id     " + 
				" inner join erp_department dep on dep.erp_department_id = dep_campus.erp_department_id    " + 
				" inner join erp_campus on dep_campus.erp_campus_id = erp_campus.erp_campus_id     " + 
				" inner join erp_location on erp_location.erp_location_id = erp_campus.erp_location_id   " + 
				" inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = req.erp_application_work_flow_process_id " + 
				" where emp.record_status='A' and dep_campus.record_status='A' and erp_campus.record_status='A' and req.record_status='A' and  " + 
				" erp_location.erp_location_id=:location and erp_work_flow_process.record_status='A'";			
		if(dto != null ) {
			if(dto.employeeName != null && !dto.employeeName.isEmpty()) {
				letterRequestQuery = letterRequestQuery+" and emp.emp_name=:emp_name";
			}
			if(dto.employeeId != null && !dto.employeeId.isEmpty()) {
				letterRequestQuery = letterRequestQuery+" and emp.emp_no=:emp_no";
			}
			if(fromDate != null ) {
				letterRequestQuery = letterRequestQuery+" and req.letter_request_applied_date>=:from_date  ";
			}
			if(toDate != null){
				letterRequestQuery = letterRequestQuery+" and req.letter_request_applied_date<=:to_date ";
			} 
			if(dto.status == null || dto.status.label == null || dto.status.label.isEmpty()) {
				letterRequestQuery = letterRequestQuery+" and (erp_work_flow_process.process_code = :status1 or erp_work_flow_process.process_code =:status2)";
			}else {
				letterRequestQuery = letterRequestQuery+" and erp_work_flow_process.erp_work_flow_process_id = :search_status";
			}
		}
		String finalQry = letterRequestQuery;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalQry, Tuple.class);
		if(dto != null ) {
			query.setParameter("location", dto.location);
			if(dto.employeeName != null && !dto.employeeName.isEmpty()) {
				query.setParameter("emp_name", dto.employeeName);
			}
			if(dto.employeeId != null && !dto.employeeId.isEmpty()) {
				query.setParameter("emp_no", dto.employeeId);
			}
			if(fromDate != null ) {
				query.setParameter("from_date", fromDate);
			}
			if(toDate != null){
				query.setParameter("to_date", toDate);
			}
			if(dto.status == null || dto.status.label == null || dto.status.label.isEmpty()) {
				query.setParameter("status1", "LETTER_REQUEST_SUBMISSION");
				query.setParameter("status2", "LETTER_REQUEST_PRINTED");
			}else {
				query.setParameter("search_status", dto.status.value);
			}
		}
		return  query.getResultList();
		}).await().indefinitely();
		return list;
	}
	protected void getEmployeeNameAndId(String employeeName, LetterGenerateIssueDTO dto) {
		employeeName = employeeName.replace("(", "-");
		String[] empName = employeeName.split("-");
		if(empName.length >1) {
			dto.employeeName = empName[0].trim();
			dto.employeeId = empName[1].replace(")", "");
		}else {
			dto.employeeName = empName[0].trim();
		}
		
		
	}
	public List<Tuple> getLetterPrint(int requestId, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query getletterRequestQuery = null;
				String letterRequestQuery = "select emp_letter_request_type.emp_letter_request_type_id as type_id,emp_letter_request_type.letter_type_name as letter_name,   " + 
						" erp_template.template_code as templ_code,erp_template.template_content as template ,emp_letter_request.letter_request_url as letter_url " + 
						" from emp_letter_request" + 
						" inner join emp_letter_request_type on emp_letter_request_type.emp_letter_request_type_id = emp_letter_request.emp_letter_request_type_id  " + 
						" inner join erp_template on erp_template.erp_template_id = emp_letter_request_type.erp_template_id   " + 
						" where  emp_letter_request_type.record_status='A' and emp_letter_request.emp_letter_request_id=:request_id   " + 
						" and erp_template.record_status='A' and emp_letter_request_type.is_available_online=1";
				getletterRequestQuery = context.createNativeQuery(letterRequestQuery,Tuple.class);
				getletterRequestQuery.setParameter("request_id", requestId);
				return getletterRequestQuery.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
			   throw error;
			}
		});
	}
	
	public  Tuple getEmpDetailsBasedOnEmpId(String empid) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
           @SuppressWarnings("unchecked")
			@Override
           public Tuple onRun(EntityManager context) throws Exception {
           	String str ="select erp_department.department_name AS emp_dep,emp.emp_name AS emp_name,emp.emp_id AS EMPID,emp.dob as dob, emp.emp_no as emp_no,    " + 
           			"emp.mobile_no as mobile, erp_campus.campus_name as campus,emp_designation.emp_designation_name as designation , " + 
           			"emp_personal_data.current_address_line_1 as cur_address_line1, " + 
           			"emp_personal_data.current_address_line_2  as cur_address_line2, " + 
           			"erp_country.country_name as cur_country, " + 
           			"erp_state.state_name as cur_state, " + 
           			"emp_personal_data.current_state_others as state_others, " + 
           			"erp_city.city_name as cur_city, " + 
           			"emp_personal_data.current_city_others as city_others, " + 
           			//"erp_pincode.pincode_name as pincode, " + 
           			"emp_personal_data.permanent_address_line_1 as per_add_line1, " + 
           			"emp_personal_data.permanent_address_line_2 as per_add_line2, " + 
           			"emp_personal_data.permanent_state_others as per_state_others, " + 
           			"emp_personal_data.permanent_city_others as per_city_others, " + 
           			"per_country.country_name as per_county_name, " + 
           			"per_state.state_name as per_state_name, " + 
           			"per_city.city_name as per_city_name " + 
           			//"erp_pincode.pincode_name as per_pinc " + 
           			"from emp    " + 
           			"inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id      " + 
           			"inner join erp_campus on erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id    " + 
           			"inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id    " + 
           			"inner join emp_designation ON emp_designation.emp_designation_id = emp.emp_designation_id " + 
           			"inner join emp_personal_data on emp.emp_personal_data_id = emp_personal_data.emp_personal_data_id " + 
           			"left join erp_country  ON erp_country.erp_country_id = emp_personal_data.current_country_id " + 
           			"left join erp_state ON erp_state.erp_state_id = emp_personal_data.current_state_id " + 
           			"left join erp_city ON erp_city.erp_city_id = emp_personal_data.current_city_id " + 
           			//"left join erp_pincode ON erp_pincode.erp_pincode_id = emp_personal_data.current_pincode_id " + 
           			"left join erp_country per_country ON per_country.erp_country_id = emp_personal_data.permanent_country_id " + 
           			"left join erp_state per_state ON per_state.erp_state_id = emp_personal_data.permanent_state_id " + 
           			"left join erp_city per_city ON per_city.erp_city_id = emp_personal_data.permanent_city_id " + 
           			//"left join erp_pincode per_pincode ON erp_pincode.erp_pincode_id = emp_personal_data.permanent_pincode_id " + 
           			"where  emp.record_status = 'A' and emp.emp_id=:employeeId " + 
           			"and emp.record_status='A' and erp_campus_department_mapping.record_status='A'  " + 
           			"and erp_campus.record_status='A' and emp_designation.record_status='A' " ;
       		Query qry =context.createNativeQuery(str, Tuple.class);
           	qry.setParameter("employeeId", empid);
           	Tuple employee=(Tuple) Utils.getUniqueResult(qry.getResultList());
               return employee;
           }
           @Override
           public void onError(Exception error) throws Exception {
               throw error;
           }
        });
	 }
	public Tuple updateLetterRequestType(String type_id, LetterGenerateIssueListDTO dto) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str ="select req_type.letter_type_prefix as prefix,req_type.letter_type_current_no as current_no  from emp_letter_request_type req_type " + 
						"where req_type.record_status='A' and req_type.emp_letter_request_type_id=:req_type";
        		Query qry =context.createNativeQuery(str, Tuple.class);
            	qry.setParameter("req_type", type_id);
            	@SuppressWarnings("unchecked")
				Tuple templateTuple=(Tuple) Utils.getUniqueResult(qry.getResultList());
            	if(templateTuple != null) {
            		String prefix =  !Utils.isNullOrEmpty(templateTuple.get("prefix")) ? templateTuple.get("prefix").toString():"";
            		String curNo = 	 !Utils.isNullOrEmpty(templateTuple.get("current_no")) ? templateTuple.get("current_no").toString():"";
            		int cuNo = curNo != null && !curNo.isEmpty() ?Integer.parseInt(curNo):1;
            		if(curNo.isEmpty()) {
            			curNo = cuNo+"";
            		}
            		String updateQuery = "update emp_letter_request_type set letter_type_current_no=:curNo where emp_letter_request_type_id=:type_id";
            		Query updateQry = context.createNativeQuery(updateQuery);
            		updateQry.setParameter("curNo", ++cuNo);
            		updateQry.setParameter("type_id", type_id);
            		updateQry.executeUpdate();
            		
            		String updateRequestQuery = "update emp_letter_request set letter_request_url=:url,erp_application_work_flow_process_id=:status where emp_letter_request_id=:request_id";
            		Query updateRequestQry = context.createNativeQuery(updateRequestQuery);
            		updateRequestQry.setParameter("url", EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+prefix+curNo+".pdf");
            		updateRequestQry.setParameter("status", dto.status.value);
            		updateRequestQry.setParameter("request_id", dto.letterRequestId);
            		updateRequestQry.executeUpdate();
            	}
				return templateTuple;
			}
			@Override
			public void onError(Exception error) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			
		});
 	 }
	public Tuple getErpTemplateByCodeBO(String templateCode) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
            @SuppressWarnings("unchecked")
 			@Override
            public Tuple onRun(EntityManager context) throws Exception {
            	String str ="select a.template_name as name,a.template_content as template, a.mail_subject as sub , a.mail_from_name as mfrom " + 
            			"from erp_template a where a.template_code=:template_code and a.record_status='A'";
        		Query qry =context.createNativeQuery(str, Tuple.class);
            	qry.setParameter("template_code", templateCode);
            	Tuple template=(Tuple) Utils.getUniqueResult(qry.getResultList());
                return template;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
         });
 	 }
	
	public List<Tuple> getStatusCode() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select process.erp_work_flow_process_id as 'ID',process.process_code as 'Text' From erp_work_flow_process_group grp " + 
						" inner join erp_work_flow_process process on process.erp_work_flow_process_group_id = grp.erp_work_flow_process_group_id " +
						" where grp.erp_process_table_name = 'emp_letter_request' and grp.record_status='A' and process.record_status='A' ORDER BY process.process_code ASC " ;
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public List<Tuple> getStatupdateInfo() throws Exception{
        return  DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @SuppressWarnings("unchecked")
 			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
            	String str ="select process.erp_work_flow_process_id as 'ID',process.process_code as 'Text',process.application_status_display_text as admin_status, " + 
            			" process.applicant_status_display_text as applicant_status From erp_work_flow_process_group grp " + 
            			" inner join erp_work_flow_process process on process.erp_work_flow_process_group_id = grp.erp_work_flow_process_group_id " + 
            			" where grp.erp_process_table_name = 'emp_letter_request' and grp.record_status='A' and process.record_status='A' order by process.process_code";
        		Query qry = context.createNativeQuery(str, Tuple.class);
            	return qry.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
         });
 	 }
	public boolean updateLetterRequeststatus(LetterGenerateIssueListDTO dto, Map<String, String[]> statusMap, List<CommonDTO> statusListMap) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				String value = dto.status.value;
				Date date = new Date();
				java.sql.Date curTime = new java.sql.Date(date.getTime());
				java.sql.Date issueDate = helper.convertStringToDate(dto.issueDate);
				String qry = "update emp_letter_request set modified_time=:issueDate , ";
				if(statusMap.containsKey(value) && statusMap.get(value)[0] != null && !statusMap.get(value)[0].isEmpty()) {
					qry = qry+" erp_application_work_flow_process_id=:admin_status,  ";
				}
				if(statusMap.containsKey(value) && statusMap.get(value)[1] != null && !statusMap.get(value)[1].isEmpty()) {
					qry = qry+" erp_applicant_work_flow_process_id=:user_status ,applicant_status_log_time=:applicantLog, ";
				}				
				if(dto.status.label.equals("Reject") || dto.status.label.equals("Clarify/Meet")) {
					qry = qry+" letter_request_po_comment=:rejectReason, ";
				}
				if(dto.status.label.equals("Issued")) {
					qry = qry+" letter_issued_date=:issueDate, ";
				}
				qry = qry+" application_status_log_time=:applicationLog ";
				qry = qry+" where emp_letter_request_id=:req_id";
				Query query = context.createNativeQuery(qry);
				if(statusMap.containsKey(value) && statusMap.get(value)[0] != null && !statusMap.get(value)[0].isEmpty()) {
					query.setParameter("admin_status", value);
				}
				if(statusMap.containsKey(value) && statusMap.get(value)[1] != null && !statusMap.get(value)[1].isEmpty()) {
					query.setParameter("user_status", value);
					query.setParameter("applicantLog", curTime);
				}
				if(dto.status.label.equals("Reject") || dto.status.label.equals("Clarify/Meet")) {
					query.setParameter("rejectReason", dto.rejectReason);	
				}
				if(dto.status.label.equals("Issued")) {
					query.setParameter("issueDate", issueDate);
				}else {
					query.setParameter("issueDate", issueDate);
				}
				query.setParameter("req_id", dto.letterRequestId);
					
				query.setParameter("applicationLog", curTime);	
				query.executeUpdate();
				return query.executeUpdate() > 0 ? true : false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public List<Tuple> getLetterPrintUrl(int requestId, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query getletterRequestQuery = null;
				String letterRequestQuery = "select emp_letter_request_type.emp_letter_request_type_id as type_id,emp_letter_request_type.letter_type_name as letter_name,   " + 
						" erp_template.template_code as templ_code,erp_template.template_content as template ,emp_letter_request.letter_request_url as letter_url " + 
						" from emp_letter_request" + 
						" inner join emp_letter_request_type on emp_letter_request_type.emp_letter_request_type_id = emp_letter_request.emp_letter_request_type_id  " + 
						" inner join erp_template on erp_template.erp_template_id = emp_letter_request_type.erp_template_id   " + 
						" where  emp_letter_request_type.record_status='A' and emp_letter_request.emp_letter_request_id=:request_id   " + 
						" and erp_template.record_status='A' and emp_letter_request_type.is_available_online=1";
				getletterRequestQuery = context.createNativeQuery(letterRequestQuery,Tuple.class);
				getletterRequestQuery.setParameter("request_id", requestId);
				return getletterRequestQuery.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
			   throw error;
			}
		});
	}
}
