package com.christ.erp.services.transactions.admission.admissionprocess;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.student.common.StudentAdmissionCancellationOfflineDTO;

@Repository
public class AdmissionCancellationOfflineTransaction {

	@Autowired
	SessionFactory sessionFactory;

	public List<Tuple> getErpWorkFlowProcessIdbyProcessCode1(List<String> processCode) {
		String str = "select w.erp_work_flow_process_id as erp_work_flow_process_id,w.process_code as code"
				+ "	from erp_work_flow_process w where w.process_code in ( :processCode) and w.record_status='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("processCode", processCode).getResultList()).await().indefinitely();
	}
	
	public List<ErpStatusDBO> getErpStatus(List<String> statusCodes) {
		String str = " select dbo from ErpStatusDBO dbo where dbo.recordStatus = 'A' and dbo.statusCode in (:statusCode)";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpStatusDBO.class).setParameter("statusCode", statusCodes).getResultList()).await().indefinitely();
	}
	
	public List<ErpWorkFlowProcessNotificationsDBO> getErpNotifications(List<String> code) {
		String query = " select distinct dbo from ErpWorkFlowProcessNotificationsDBO dbo  where dbo.recordStatus = 'A' and dbo.notificationCode in (:code)";
		return sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("code", code).getResultList()).await().indefinitely();
	}

	public StudentApplnEntriesDBO getStudentDetails(StudentAdmissionCancellationOfflineDTO dto) {
		String query = "select dbo from StudentApplnEntriesDBO dbo "
				+ "	inner join fetch dbo.studentPersonalDataDBO spd"
				+ " left join fetch spd.studentPersonalDataAddressDBO spda"
				+ "	left join fetch dbo.hostelAdmissionsDBOSet ha"
				+ " left join fetch dbo.StudentDBOS s"
				+ "	where dbo.appliedAcademicYear.id = :yearId and dbo.applicationNo = :applicationNo and dbo.recordStatus='A'";
		return sessionFactory.withSession(s -> s.createQuery(query, StudentApplnEntriesDBO.class).setParameter("yearId", Integer.parseInt(dto.getBatchYear().getValue()))
				.setParameter("applicationNo", dto.getApplicationNo()).getSingleResultOrNull()).await().indefinitely();
	}
	
	public StudentApplnEntriesDBO checkStudentAdmissions(StudentAdmissionCancellationOfflineDTO dto) {
		String query = "select dbo from StudentApplnEntriesDBO dbo "
				+ " left join fetch dbo.hostelAdmissionsDBOSet ha"
				+ " where dbo.appliedAcademicYear.id = :yearId and dbo.applicationNo = :applicationNo and dbo.recordStatus='A'";
		return sessionFactory.withSession(s -> s.createQuery(query, StudentApplnEntriesDBO.class)
				.setParameter("yearId", Integer.parseInt(dto.getBatchYear().getValue())).setParameter("applicationNo",dto.getApplicationNo() ).getSingleResultOrNull()).await().indefinitely();
	}

	public Boolean update(List<Object> dbos) {
		return sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray()).replaceWith(true)).await().indefinitely();
	}

}
