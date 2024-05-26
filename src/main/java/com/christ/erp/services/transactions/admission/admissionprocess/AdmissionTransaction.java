package com.christ.erp.services.transactions.admission.admissionprocess;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

@Repository
public class AdmissionTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Integer getStudentId(Integer studentEntriesId) {
		String query ="select student_id from  student where  student_appln_entries_id = :id";
		return  sessionFactory.withSession(s->s.createNativeQuery(query, Integer.class).setParameter("id", studentEntriesId).getSingleResultOrNull()).await().indefinitely();
	}
	
	public void save(StudentDBO dbo, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo).chain(session::flush).map(s -> {
			return convertDbo(dbo,userId);
		}).flatMap(s-> session.mergeAll(s.toArray()))
				).await().indefinitely();
	}
	
	public List<Object> convertDbo(StudentDBO dbo, String userId) {
		List<Object> dbos = new ArrayList<Object>();
		StudentApplnEntriesDBO data = dbo.getStudentApplnEntriesDBO();
			data.getStudentEducationalDetailsDBOS().forEach(studentEducation -> {
				studentEducation.setStudentDBO(dbo);
		});
		data.getStudentWorkExperienceDBOS().forEach(studentWorkExp -> {
			studentWorkExp.setStudentDBO(dbo);
		});
//		data.getAccFeeDemandDBOSet().forEach( accFeeDeamand -> {
//			accFeeDeamand.setStudentDBO(dbo);
//		});
		
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.setEntryId(data.getId());
		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(data.getApplicationCurrentProcessStatus().getId());
		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
		
		dbos.add(data);
		dbos.add(erpWorkFlowProcessStatusLogDBO);
		return dbos;
	}

}
