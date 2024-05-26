package com.christ.erp.services.transactions.hostel.student;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFacilitySettingDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;


@Repository
public class CheckInTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public List<HostelAdmissionsDBO> getGridData(String academicYearId, String hostelId ) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew(); 
		String str =" from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and "
				+" (dbo.erpStatusDBO.statusCode ='HOSTEL_ADMITTED' or dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN') and dbo.erpStatusDBO.recordStatus ='A' and "
				+" dbo.erpAcademicYearDBO.id = :academicYearId  and dbo.erpAcademicYearDBO.recordStatus ='A' and "
				+" dbo.hostelDBO.id = :hostelId and dbo.hostelDBO.recordStatus ='A' and " 
				+" dbo.hostelApplicationDBO.recordStatus ='A' and dbo.studentApplnEntriesDBO.recordStatus ='A' and "
				+" dbo.studentDBO.recordStatus ='A'";
		String finalStr = str;
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(academicYearId)) {
				query.setParameter("academicYearId", Integer.parseInt(academicYearId));
			} else {
				query.setParameter("academicYearId", currYear.getId());
			}
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public HostelAdmissionsDBO edit(int id) {
		String str =" from HostelAdmissionsDBO dbo "
				+" left join fetch dbo.hostelFacilityDBOSet as dbos"
				+" where dbo.recordStatus ='A' and dbo.id =:id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelAdmissionsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public HostelAdmissionsDBO getDataByStudent(String applnNo, String regNo, String name) {
		String str = " select dbo from HostelAdmissionsDBO dbo "
				+" left join fetch dbo.hostelFacilityDBOSet as dbos "
				+" where dbo.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and dbo.studentDBO.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(applnNo)) {
			str+= " and dbo.studentApplnEntriesDBO.applicationNo =:applnNo";
		}
		if(!Utils.isNullOrEmpty(name)) {
			str+= " and dbo.studentApplnEntriesDBO.applicantName =:name";
		}
		String finalStr = str;
		HostelAdmissionsDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(applnNo)) { 
				query.setParameter("applnNo",Integer.parseInt(applnNo)); 
			}
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			if(!Utils.isNullOrEmpty(name)) {
				query.setParameter("name",name);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}

	public boolean merge(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;	
	}

	public Integer getStatusId(String processCode) {
		String str = "select erp_status.erp_status_id as erp_status_id from erp_status where status_code =:processCode and erp_status.record_status ='A'";
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("processCode", processCode).getSingleResultOrNull()).await().indefinitely();		
	}

	public HostelAdmissionsDBO duplicateCheckdata(String regNo, String applnNo, String name) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and "
				+" dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN' ";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and dbo.studentDBO.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(applnNo)) {
			str+= " and dbo.studentApplnEntriesDBO.applicationNo =:applnNo";
		}
		if(!Utils.isNullOrEmpty(name)) {
			str+= " and dbo.studentApplnEntriesDBO.applicantName =:name";
		}
		String finalStr = str;
		HostelAdmissionsDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(applnNo)) { 
				query.setParameter("applnNo",Integer.parseInt(applnNo)); 
			}
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			if(!Utils.isNullOrEmpty(name)) {
				query.setParameter("name", name);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}

	public List<HostelBedDBO> getHostelBed1(List<Integer> bedId) {
		String str = "select dbo from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.id IN (:bedId)";
		return sessionFactory.withSession(s-> s.createQuery(str,HostelBedDBO.class).setParameter("bedId", bedId).getResultList()).await().indefinitely();
	} 

	public boolean update1(List<HostelBedDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}

	public List<HostelFacilitySettingDBO> getHostelFacilities() {
		String str = " select dbo from HostelFacilitySettingDBO dbo where dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelFacilitySettingDBO.class).getResultList()).await().indefinitely();
	}
}
