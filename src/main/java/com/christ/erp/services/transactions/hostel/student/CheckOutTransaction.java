package com.christ.erp.services.transactions.hostel.student;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

@Repository
public class CheckOutTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public List<HostelAdmissionsDBO> getGridData(String academicYearId, String hostelId ) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew(); 
		String str =" from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and "
				+" dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_OUT' and dbo.erpStatusDBO.recordStatus ='A' and "
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
	
	public HostelAdmissionsDBO getDataByStudent(String hostelApplicationNo, String regNo) {
		String str = " select dbo from HostelAdmissionsDBO dbo "
				+" left join fetch dbo.hostelFacilityDBOSet as dbos "
				+" where dbo.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and dbo.studentDBO.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(hostelApplicationNo)) {
			str+= " and dbo.hostelApplicationDBO.applicationNo =:hostelApplicationNo";
		}
		String finalStr = str;
		HostelAdmissionsDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(hostelApplicationNo)) { 
				query.setParameter("hostelApplicationNo",Integer.parseInt(hostelApplicationNo)); 
			}
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}

	public boolean update(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;	
	}

	public HostelAdmissionsDBO edit(int id) {
		String str =" from HostelAdmissionsDBO dbo "
				+" left join fetch dbo.hostelFacilityDBOSet as dbos"
				+" where dbo.recordStatus ='A' and dbo.id =:id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelAdmissionsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Integer getStatusId(String statusCode) {
		String str = "select erp_status.erp_status_id as erp_status_id from erp_status where status_code =:statusCode and erp_status.record_status ='A'";
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("statusCode", statusCode).getSingleResultOrNull()).await().indefinitely();		
	}
	
	public HostelAdmissionsDBO duplicateCheckdata(String regNo, String hostelApplnNo) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and "
				+" dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_OUT' ";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and dbo.studentDBO.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(hostelApplnNo)) {
			str+= " and dbo.hostelApplicationDBO.applicationNo =:hostelApplnNo";
		}
		String finalStr = str;
		HostelAdmissionsDBO dbo = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(hostelApplnNo)) { 
				query.setParameter("hostelApplnNo",Integer.parseInt(hostelApplnNo)); 
			}
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;	
	}
}
