package com.christ.erp.services.transactions.employee.onboarding;

import java.util.List;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.SysPropertiesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveCategoryAllotmentDBO;

@Repository
public class CreateEmployeeIdTransaction {

	private static volatile CreateEmployeeIdTransaction createEmployeeIdTransaction = null;

	public static CreateEmployeeIdTransaction getInstance() {
		if(createEmployeeIdTransaction==null) {
			createEmployeeIdTransaction = new CreateEmployeeIdTransaction();
		}
		return  createEmployeeIdTransaction;
	}

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public EmpDBO getEmp(String id) {
		String str = " select dbo from EmpDBO dbo left join fetch dbo.empApplnEntriesDBO left join fetch dbo.empJobDetailsDBO where dbo.recordStatus = 'I' and dbo.id=:id ";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpDBO.class).setParameter("id", Integer.parseInt(id)).getSingleResultOrNull()).await().indefinitely();
	}

	public ErpCampusDepartmentMappingDBO getCampusDepartmentMapping(String campusId, String departmentId) {
		String str = " from ErpCampusDepartmentMappingDBO where erpCampusDBO.id=:campusId "
				+ " and erpDepartmentDBO.id=:departmentId and recordStatus='A'" ;
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCampusDepartmentMappingDBO.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("departmentId", Integer.parseInt(departmentId)).getSingleResultOrNull()).await().indefinitely();		
	}

	public EmpDBO getApplicantDetails(String applicationNo){
		String str = " select bo from EmpDBO bo left join fetch bo.empApplnEntriesDBO appln where (bo.recordStatus ='A' or bo.recordStatus ='I') and appln.recordStatus='A' and appln.applicationNo=:applicationNo ";
		return sessionFactory.withSession(session->session.createQuery(str,EmpDBO.class).setParameter("applicationNo", Integer.parseInt(applicationNo)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> getDepartmentByCampus(String campusId) {
		String str = " select erp_department.erp_department_id as ID,erp_department.department_name as text from erp_campus_department_mapping  "
				+ " inner join erp_department on erp_department.erp_department_id=erp_campus_department_mapping.erp_department_id "
				+ " where erp_campus_department_mapping.erp_campus_id=:campusId and erp_campus_department_mapping.record_status='A'";
		return sessionFactory.withSession(session->session.createNativeQuery(str,Tuple.class).setParameter("campusId", Integer.parseInt(campusId)).getResultList()).await().indefinitely();

	}

	public EmpLeaveCategoryAllotmentDBO getLeaveTypeByCategory(String leaveCategoryId) {
		String str = "select dbo from  EmpLeaveCategoryAllotmentDBO dbo"
				+ " left join fetch dbo.empLeaveCategoryAllotmentDetailsDBO "
				+ " where dbo.id=:leaveCategoryId and  dbo.recordStatus='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpLeaveCategoryAllotmentDBO.class).setParameter("leaveCategoryId", Integer.parseInt(leaveCategoryId)).getSingleResultOrNull()).await().indefinitely();		
	}
	
	public boolean saveOrUpdate(List<Object> objects) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(objects.toArray())).await().indefinitely();
		return true;
	}
	
	public Tuple getTemplate() {
		String str =" select  erp_template.erp_template_id as emailTemplateId, "
				+ " erp_template.template_name as emailTemplateName, "
				+ " erp_template.template_code as emailTemplateCode, "
				+ " erp_template.template_content as emailTemplateContent, "
				+ " erp_template.mail_subject as mailSubject, erp_template.mail_from_name as mailFromName "
				+ " from erp_work_flow_process "
				+ " left join erp_work_flow_process_notifications on erp_work_flow_process_notifications.erp_work_flow_process_id = erp_work_flow_process.erp_work_flow_process_id and erp_work_flow_process_notifications.record_status = 'A' "
				+ " left join erp_template  on if(erp_work_flow_process_notifications.is_email_activated=1,erp_work_flow_process_notifications.email_template_id,null)=erp_template.erp_template_id and erp_template.record_status='A' "
				+ " where erp_work_flow_process.process_code = 'EMP_CREATED' and erp_work_flow_process.record_status = 'A' "
				+ " and erp_work_flow_process_notifications.notification_code = 'EMP_CREATED'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).getSingleResultOrNull()).await().indefinitely();
	}
}