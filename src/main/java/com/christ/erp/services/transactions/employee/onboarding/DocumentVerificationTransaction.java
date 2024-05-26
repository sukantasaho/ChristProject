package com.christ.erp.services.transactions.employee.onboarding;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.validation.Valid;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentVerificationDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentVerificationDetailsDBO;

public class DocumentVerificationTransaction {

	private static volatile DocumentVerificationTransaction documentVerificationTransaction=null;

	public static DocumentVerificationTransaction getInstance() {
		if(documentVerificationTransaction==null) {
			documentVerificationTransaction = new DocumentVerificationTransaction();
		}
		return documentVerificationTransaction;
	}

	public  List<Tuple> editDocumentverifiedbyApplicationnumber(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select  ea.emp_appln_entries_id as ApplicantId, ea.applicant_name as ApplicantName , ecat.employee_category_name as PostApplied ,con.country_name as Country,ec.campus_name as Campus, edoc.wait_for_document as WaitForDoc,edoc.is_in_draft_mode as IsInDraftMode,edoc.wait_remarks as WaitForRemarks, edoc.submission_due_date as SubmissionDueDate, e1.emp_document_checklist_sub_id as ChickList, e1.verification_status as VerifyStatus, e1.verification_remarks as VerifyRemarks, e1.emp_document_verification_details_id as ID, edoc.emp_document_verification_id as ParentId  from emp_document_verification edoc inner join emp_document_verification_details e1 on e1.emp_document_verification_id=edoc.emp_document_verification_id " + 
						" inner join emp_appln_entries ea on ea.emp_appln_entries_id=edoc.emp_appln_entries_id " + 
						" inner join emp_employee_category ecat on ecat.emp_employee_category_id=ea.emp_employee_category_id " + 
						" inner join emp_appln_personal_data per on per.emp_appln_entries_id=ea.emp_appln_entries_id " + 
						" left join erp_campus ec on ec.erp_campus_id =ea.erp_campus_id " + 
						" inner join erp_country con on  per.erp_country_id=con.erp_country_id where ea.application_no=:applicationNumber and e1.record_status='A'";
				Query qry = context.createNativeQuery(str.toString(), Tuple.class);
				qry.setParameter("applicationNumber", Integer.parseInt(id));
				List<Tuple> authors = qry.getResultList();
				return authors;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public  List<Tuple> editDocumentVerifiedbyEmployeeId(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e1.emp_id as EmpId, e1.emp_name as EmpName, ecat.employee_category_name as PostApplied,con.country_name as Country,ec.campus_name as Campus,e.wait_for_document as WaitForDoc,e.is_in_draft_mode as IsInDraftMode,e.wait_remarks as WaitForRemarks, e.submission_due_date as SubmissionDueDate, e2.emp_document_checklist_sub_id as ChickList, e2.verification_status as VerifyStatus, e2.verification_remarks as VerifyRemarks, e2.emp_document_verification_details_id as ID, e.emp_document_verification_id as ParentId from emp_document_verification e inner join emp e1 on e1.emp_id=e.emp_id " + 
						" inner join emp_document_verification_details e2 on e2.emp_document_verification_id=e.emp_document_verification_id " + 
						" inner join emp_employee_category ecat on ecat.emp_employee_category_id=e1.emp_employee_category_id " + 
						" inner join erp_campus_department_mapping ecdd on ecdd.erp_campus_department_mapping_id=e1.erp_campus_department_mapping_id " + 
						" inner join erp_campus ec on ec.erp_campus_id =ecdd.erp_campus_id " + 
						" inner join emp_personal_data per on per.emp_personal_data_id=e1.emp_personal_data_id " + 
						" inner join erp_country con on  per.erp_country_id=con.erp_country_id  where e1.emp_no=:empnumber and e2.record_status='A' and e.is_in_draft_mode=false";
				Query qry = context.createNativeQuery(str.toString(),Tuple.class);
				qry.setParameter("empnumber", id);
				@SuppressWarnings("unchecked")
				List<Tuple> authors = qry.getResultList();
				return authors;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public  List<Tuple> editDocumentverified(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str ="select e.emp_id as EmpId, e.emp_name as EmpName, ecat.employee_category_name as PostApplied,con.country_name as Country,ec.campus_name as Campus,edoc.wait_for_document as WaitForDoc,edoc.is_in_draft_mode as IsInDraftMode,edoc.wait_remarks as WaitForRemarks, edoc.submission_due_date as SubmissionDueDate, e1.emp_document_checklist_sub_id as ChickList, e1.verification_status as VerifyStatus, e1.verification_remarks as VerifyRemarks, e1.emp_document_verification_details_id as ID, edoc.emp_document_verification_id as ParentId from emp_document_verification edoc " + 
						" inner join emp_document_verification_details e1 on e1.emp_document_verification_id=edoc.emp_document_verification_id " + 
						" inner join emp_appln_entries ea on ea.emp_appln_entries_id=edoc.emp_appln_entries_id " + 
						" inner join emp e on ea.emp_appln_entries_id=e.emp_appln_entries_id " + 
						" inner join emp_employee_category ecat on ecat.emp_employee_category_id=e.emp_employee_category_id " + 
						" inner join erp_campus_department_mapping ecdd on ecdd.erp_campus_department_mapping_id=e.erp_campus_department_mapping_id " + 
						" inner join erp_campus ec on ec.erp_campus_id =ecdd.erp_campus_id " + 
						" inner join emp_personal_data per on per.emp_personal_data_id=e.emp_personal_data_id " + 
						" inner join erp_country con on  per.erp_country_id=con.erp_country_id where e.emp_no=:empnumber and e1.record_status='A' and edoc.is_in_draft_mode=false";
				Query qry = context.createNativeQuery(str.toString(),Tuple.class);
				qry.setParameter("empnumber", id);
				@SuppressWarnings("unchecked")
				List<Tuple> authors = qry.getResultList();
				return authors;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public  EmpDocumentVerificationDBO get(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpDocumentVerificationDBO>() {
			@Override
			public EmpDocumentVerificationDBO onRun(EntityManager context) throws Exception {
				EmpDocumentVerificationDBO empDocumentVerificationDBO = context.find(EmpDocumentVerificationDBO.class, Integer.parseInt(id));
				return empDocumentVerificationDBO;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpDocumentVerificationDetailsDBO getDocuemntVerificationDetails(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpDocumentVerificationDetailsDBO>() {
			@Override
			public EmpDocumentVerificationDetailsDBO onRun(EntityManager context) throws Exception {
				EmpDocumentVerificationDetailsDBO empDocumentVerificationDetailsDBO= context.find(EmpDocumentVerificationDetailsDBO.class, Integer.parseInt(id));
				return empDocumentVerificationDetailsDBO;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Boolean saveOrUpdate(EmpDocumentVerificationDBO empDocumentVerificationDBO,
			@Valid List<Integer> detailsId, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(empDocumentVerificationDBO.id)) {
					context.persist(empDocumentVerificationDBO);
				}
				else {
					String str="UPDATE emp_document_verification_details SET record_status='D' WHERE emp_document_verification_id = :heading_id AND emp_document_verification_details_id NOT IN (:detail_ids)";
					Query deleteQuery = context.createNativeQuery(str.toString());
					deleteQuery.setParameter("heading_id", empDocumentVerificationDBO.id);
					deleteQuery.setParameter("detail_ids",detailsId );
					deleteQuery.executeUpdate();
					context.merge(empDocumentVerificationDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
