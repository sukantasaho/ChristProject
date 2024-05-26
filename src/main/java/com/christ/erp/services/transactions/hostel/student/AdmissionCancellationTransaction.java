package com.christ.erp.services.transactions.hostel.student;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;

@Repository
public class AdmissionCancellationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public HostelAdmissionsDBO getStudentHostelData(String hostelApplnNo, String regNo, String name, String yearId) {
		String str = " select dbo from HostelAdmissionsDBO dbo"
				    +" inner join dbo.hostelApplicationDBO hdbo"
				    +" inner join hdbo.studentDBO s "
				    +" inner join hdbo.studentApplnEntriesDBO sae "
				    +" where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =:yearId and hdbo.recordStatus ='A'"
				    +" and s.recordStatus ='A' and sae.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and s.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(name)) {
			str+= " and s.studentName =:name";
		}
		if(!Utils.isNullOrEmpty(hostelApplnNo)) {
			str+= " and hdbo.applicationNo =:hostelApplnNo";
		}
		String finalStr = str;
		HostelAdmissionsDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr, HostelAdmissionsDBO.class);
			    query.setParameter("yearId", Integer.parseInt(yearId));
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			if(!Utils.isNullOrEmpty(name)) {
				query.setParameter("name", name);
			}
			if(!Utils.isNullOrEmpty(hostelApplnNo)) {
				query.setParameter("hostelApplnNo", Integer.parseInt(hostelApplnNo));
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}

	public void update(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}
	
	public boolean duplicateCheck(HostelAdmissionsDTO dto) {
			String str = " select dbo from HostelAdmissionsDBO dbo"
					+" inner join dbo.hostelApplicationDBO hdbo"
					+" inner join hdbo.studentDBO s "
			        +" inner join hdbo.studentApplnEntriesDBO sae"
					+" where dbo.recordStatus ='A' and dbo.erpStatusDBO.statusCode ='HOSTEL_CANCELLED' and dbo.erpAcademicYearDBO.id =:yearId"
					+" and hdbo.recordStatus ='A' and s.recordStatus ='A' and sae.recordStatus ='A'";
			if(!Utils.isNullOrEmpty(dto.getStudentDTO().getRegisterNo())) { 
				str+= " and s.registerNo =:regNo ";
			}
			if(!Utils.isNullOrEmpty(dto.getStudentDTO().getStudentName())) {
				str+= " and s.studentName =:name";
			}
			if(!Utils.isNullOrEmpty(dto.getHostelApplicationDTO().getHostelApplicationNo())) {
				str+= " and hdbo.applicationNo =:hostelApplnNo";
			}
			String finalStr = str;
			HostelAdmissionsDBO dbo = sessionFactory.withSession(s -> {
				Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr, HostelAdmissionsDBO.class);
				query.setParameter("yearId", Integer.parseInt(dto.getErpAcademicYearDTO().getValue()));
				if(!Utils.isNullOrEmpty(dto.getStudentDTO().getRegisterNo())) {
					query.setParameter("regNo", dto.getStudentDTO().getRegisterNo());
				}
				if(!Utils.isNullOrEmpty(dto.getStudentDTO().getStudentName())) {
					query.setParameter("name", dto.getStudentDTO().getStudentName());
				}
				if(!Utils.isNullOrEmpty(dto.getHostelApplicationDTO().getHostelApplicationNo())) {
					query.setParameter("hostelApplnNo", Integer.parseInt(dto.getHostelApplicationDTO().getHostelApplicationNo()));
				}
				return query.getSingleResultOrNull();
			}).await().indefinitely();
			return Utils.isNullOrEmpty(dbo) ? false : true;
       }

	public Integer getStatusId(String processCode) {
		String str = "select erp_status.erp_status_id as erp_status_id from erp_status where status_code =:processCode and erp_status.record_status ='A'";
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("processCode", processCode).getSingleResultOrNull()).await().indefinitely();		
	}

	public HostelAdmissionsDBO edit(int id) {
		String str= " from HostelAdmissionsDBO dbo"
				   +" where dbo.recordStatus ='A' and dbo.id =:id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelAdmissionsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public HostelApplicationDBO getApplicantData(String id) {
		String str= " from HostelApplicationDBO dbo"
				   +" where dbo.recordStatus ='A' and dbo.id =:id ";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelApplicationDBO.class).setParameter("id", Integer.parseInt(id)).getSingleResultOrNull()).await().indefinitely();
	}
}
