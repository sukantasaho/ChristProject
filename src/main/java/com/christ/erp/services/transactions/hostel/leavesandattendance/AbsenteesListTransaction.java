package com.christ.erp.services.transactions.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelAttendanceDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.hostel.leavesandattendance.AbsenteesListDTO;
import reactor.core.publisher.Mono;

@Repository
public class AbsenteesListTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<HostelAttendanceDBO> getGridData(AbsenteesListDTO absenteesListDTO) {
		String query = " select distinct dbo from HostelAttendanceDBO dbo "
				+ " where dbo.hostelAdmissionsDBO.erpAcademicYearDBO.id = :yearId and dbo.hostelAdmissionsDBO.hostelDBO.id = :hostelId and dbo.attendanceDate = :date "
				+ " and dbo.recordStatus = 'A'";
		if(absenteesListDTO.getLeaveSession().equalsIgnoreCase("morning")) {
			query += " and dbo.morningTime is null";
		} else {
			query += " and dbo.eveningTime is null";
		}
		if(!Utils.isNullOrEmpty(absenteesListDTO.getSelectedBlock())) {
			query +=" and  dbo.hostelAdmissionsDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.hostelBlockDBO.id = :blockId";
		}
		if(!Utils.isNullOrEmpty(absenteesListDTO.getSelectedUnit())) {
			query +=" and dbo.hostelAdmissionsDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.id = :unitId";
		}
		String finalquery = query;
		List<HostelAttendanceDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<HostelAttendanceDBO> query1 = s.createQuery(finalquery, HostelAttendanceDBO.class);
		query1.setParameter("yearId",Integer.parseInt(absenteesListDTO.getAcademicYearSelected().getValue()));
		query1.setParameter("hostelId", Integer.parseInt(absenteesListDTO.getSelectedHostel().getValue()));
		query1.setParameter("date", absenteesListDTO.getLeaveDate());
		if(!Utils.isNullOrEmpty(absenteesListDTO.getSelectedBlock())) {
			query1.setParameter("blockId", Integer.parseInt(absenteesListDTO.getSelectedBlock().getValue()));
		} 
		if(!Utils.isNullOrEmpty(absenteesListDTO.getSelectedUnit()))  {
			query1.setParameter("unitId", Integer.parseInt(absenteesListDTO.getSelectedUnit().getValue()));
		} 
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<HostelAttendanceDBO> getData(LocalDate date, String session) {
		String query = " select distinct dbo from HostelAttendanceDBO dbo "
				+ " where dbo.attendanceDate = :date and dbo.recordStatus = 'A' ";
		if(session.equalsIgnoreCase("Morning")) {
			query += " and dbo.morningTime is null";
		} else {
			query += " and dbo.eveningTime is null";
		}
		String finalquery = query;
		List<HostelAttendanceDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<HostelAttendanceDBO> query1 = s.createQuery(finalquery, HostelAttendanceDBO.class);
		query1.setParameter("date", date);
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void save(List<HostelAttendanceDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}

	public void saveFine(List<HostelFineEntryDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}

	public List<ErpWorkFlowProcessNotificationsDBO> getErpNotifications(List<String> code) {
		String query = " select distinct dbo from ErpWorkFlowProcessNotificationsDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.notificationCode in (:code)";
		return sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("code", code).getResultList()).await().indefinitely();
	}

	public HostelFineCategoryDBO getFineCategory(AbsenteesListDTO absenteesListDTO) {
		String query = " select distinct dbo from HostelFineCategoryDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.hostelDBO.id = :hostelId and dbo.isAbsentFine = true" ;
		return sessionFactory.withSession(s->s.createQuery(query,HostelFineCategoryDBO.class)
				.setParameter("hostelId",Integer.parseInt(absenteesListDTO.getSelectedHostel().value)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<HostelFineEntryDBO> getFineEntry(LocalDate localDate, LocalTime morningTime, String leaveSession) {
		String query = " select distinct dbo from HostelFineEntryDBO dbo  where dbo.recordStatus = 'A' and dbo.date = :date1 and ";
		if(Utils.isNullOrEmpty(morningTime)|| (leaveSession != null &&  leaveSession.equalsIgnoreCase("Morning"))) {
			query += " dbo.morningHostelAttendanceDBO != null";
		} else {
			query += " dbo.eveningHostelAttendanceDBO != null";
		}
		String finalquery = query;
		return sessionFactory.withSession(s->s.createQuery(finalquery,HostelFineEntryDBO.class).setParameter("date1", localDate).getResultList()).await().indefinitely();
	}

	public Mono<List<HostelAttendanceDBO>> getAbsenteeListofStudent (int yearId,int admissionId, int month) {
		String query = " select distinct dbo from HostelAttendanceDBO dbo where  dbo.recordStatus = 'A' and dbo.hostelAdmissionsDBO.id = :admissionId "
				+ "and dbo.hostelAdmissionsDBO.erpAcademicYearDBO.id = :yearId and month(dbo.attendanceDate) = :month";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(query,HostelAttendanceDBO.class).setParameter("admissionId", admissionId)
				.setParameter("month", month).setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());
	}

}