package com.christ.erp.services.transactions.hostel.leavesandattendance;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveApplicationsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelLeaveApplicationsDTO;

import reactor.core.publisher.Mono;

@Repository
public class HostelOfflineLeaveApplicationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public HostelAdmissionsDBO getStudentDetails(String registerNo,String yearId) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.recordStatus = 'A' and dbo.studentDBO.recordStatus = 'A' "
				+ " and dbo.studentDBO.registerNo = :registerNo and dbo.erpAcademicYearDBO.id = :yearId ";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelAdmissionsDBO.class).setParameter("registerNo", registerNo).setParameter("yearId", Integer.parseInt(yearId)).getSingleResultOrNull()).await().indefinitely();
	}

	public List<HostelLeaveApplicationsDBO> getGridData(String academicYear,String hostelId,String blockId,String unitId) {
		String str = " select DISTINCT dbo from HostelLeaveApplicationsDBO dbo "
				+ " left join fetch dbo.hostelLeaveApplicationsDocumentDBOSet "
				+ " where dbo.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.recordStatus = 'A' and dbo.hostelAdmissionDBO.erpAcademicYearDBO.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.hostelBedDBO.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.recordStatus = 'A' "
				+ " and dbo.hostelAdmissionDBO.erpAcademicYearDBO.id = :academicYear ";
		if(!Utils.isNullOrEmpty(hostelId)) {
			str += " and dbo.hostelAdmissionDBO.hostelDBO.recordStatus = 'A' and dbo.hostelAdmissionDBO.hostelDBO.id = :hostelId ";
		}
		if(!Utils.isNullOrEmpty(blockId)) {
			str += " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.hostelBlockDBO.recordStatus = 'A' "
					+ " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.hostelBlockDBO.id = :blockId ";
		}
		if(!Utils.isNullOrEmpty(unitId)) {
			str += " and dbo.hostelAdmissionDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.id = :unitId ";
		}
		String finalStr = str;
		List<HostelLeaveApplicationsDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelLeaveApplicationsDBO> query = s.createQuery(finalStr,HostelLeaveApplicationsDBO.class);
			if(!Utils.isNullOrEmpty(academicYear)) {
				query.setParameter("academicYear", Integer.parseInt(academicYear));
			}
			if(!Utils.isNullOrEmpty(hostelId)) {
				query.setParameter("hostelId", Integer.parseInt(hostelId));
			}
			if(!Utils.isNullOrEmpty(blockId)) {
				query.setParameter("blockId", Integer.parseInt(blockId));
			}
			if(!Utils.isNullOrEmpty(unitId)) {
				query.setParameter("unitId", Integer.parseInt(unitId));
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public HostelLeaveApplicationsDBO edit(int id) {
		String str = " select dbo from HostelLeaveApplicationsDBO dbo "
				+ " left join fetch dbo.hostelLeaveApplicationsDocumentDBOSet "
				+ " where dbo.id = : id and dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelLeaveApplicationsDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelLeaveApplicationsDBO.class, id)
				.chain(dbo1 -> session.fetch(dbo1.getHostelLeaveApplicationsDocumentDBOSet())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(userId);
						})
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public void update(HostelLeaveApplicationsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}
	
	public void save(HostelLeaveApplicationsDBO dbo,Integer submissionId) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo).chain(session::flush).map(s -> {
			return convertDbo(dbo,submissionId);
		}).flatMap(s -> session.persist(s))).await().indefinitely();
	}

	public ErpWorkFlowProcessStatusLogDBO convertDbo(HostelLeaveApplicationsDBO dbo,Integer submissionId) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.setEntryId(dbo.getId());
		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(submissionId);
		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(dbo.getCreatedUsersId());
		return erpWorkFlowProcessStatusLogDBO;
	}
	
	public void update1(HostelLeaveApplicationsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo).chain(session::flush).map(s -> {
			return convertDbo1(dbo);
		}).flatMap(s -> session.persist(s))).await().indefinitely();
	}
	
	public ErpWorkFlowProcessStatusLogDBO convertDbo1(HostelLeaveApplicationsDBO dbo) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.setEntryId(dbo.getId());
		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(dbo.getErpApplicationWorkFlowProcessId().getId());
		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(dbo.getCreatedUsersId());
		return erpWorkFlowProcessStatusLogDBO;
	}

	public Integer getWorkFlowProcessId(String type) {
		String str = " select erp_work_flow_process_id from erp_work_flow_process as ewfp where ewfp.process_code =:type and ewfp.record_status='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("type", type).getSingleResultOrNull()).await().indefinitely();
	}

	public List<HostelLeaveApplicationsDBO> duplicateCheck(HostelLeaveApplicationsDTO dto) {
//		String str = "select DISTINCT dbo from HostelLeaveApplicationsDBO dbo where dbo.recordStatus = 'A' "
//				+ "and dbo.hostelAdmissionDBO.recordStatus = 'A' and dbo.hostelAdmissionDBO.studentDBO.recordStatus = 'A' "
//				+ "and ("
//				+ "	((:leaveFromDate) BETWEEN dbo.leaveFromDate and dbo.leaveToDate) "
//				+ "	and ( "
//				+ "			(CASE WHEN dbo.leaveFromDate = (:leaveFromDate) THEN ( "
//				+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveFromDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveFromDateSession) = 'Evening')) THEN "
//				+ "						true ELSE false END) "
//				+ "			ELSE true END) = TRUE "
//				+ "				"
//				+ "				AND "
//				+ "				"
//				+ "			(CASE WHEN dbo.leaveToDate = (:leaveFromDate) THEN ("
//				+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveFromDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
//				+ "						true ELSE false END) "
//				+ "			ELSE true END) = TRUE)"
//				+ "	OR "
//				+ "	((:leaveToDate) BETWEEN dbo.leaveFromDate and dbo.leaveToDate) "
//				+ "	and ("
//				+ "			(CASE WHEN dbo.leaveFromDate = (:leaveToDate) THEN ("
//				+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveToDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveToDateSession) = 'Evening')) THEN "
//				+ "						true ELSE false END) "
//				+ "			ELSE true END ) =TRUE "
//				+ "				"
//				+ "				AND "
//				+ "			"
//				+ "			(CASE WHEN dbo.leaveToDate = (:leaveToDate) THEN ("
//				+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveToDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
//				+ "						true ELSE false END) "
//				+ "			ELSE true END) = TRUE) "
//				+ "	OR "
//				+ "	((:leaveFromDate) <= dbo.leaveFromDate AND ((:leaveToDate) > dbo.leaveFromDate))"
//				+ " )"
//				+ " and dbo.hostelAdmissionDBO.studentDBO.registerNo = :registerNo ";
		
		String str = "select DISTINCT dbo from HostelLeaveApplicationsDBO dbo where dbo.recordStatus = 'A' "
		+ "and dbo.hostelAdmissionDBO.recordStatus = 'A' and dbo.hostelAdmissionDBO.studentDBO.recordStatus = 'A' "
		+ "and ("
		+ "	((:leaveFromDate) BETWEEN dbo.leaveFromDate and dbo.leaveToDate) "
		+ "	and ( "
		+ "			(CASE WHEN dbo.leaveFromDate = (:leaveFromDate) THEN ( "
		+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveFromDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveFromDateSession) = 'Evening')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE "
		+ "				"
		+ "				AND "
		+ "				"
		+ "			(CASE WHEN dbo.leaveToDate = (:leaveFromDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveFromDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE)"
		+ "	OR "
		+ "	((:leaveToDate) BETWEEN dbo.leaveFromDate and dbo.leaveToDate) "
		+ "	and ("
		+ "			(CASE WHEN dbo.leaveFromDate = (:leaveToDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveToDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveToDateSession) = 'Evening')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END ) =TRUE "
		+ "				"
		+ "				AND "
		+ "			"
		+ "			(CASE WHEN dbo.leaveToDate = (:leaveToDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveToDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE) "
		+ "	AND "
		+ "	((:leaveFromDate) = dbo.leaveFromDate and (:leaveFromDate) = dbo.leaveToDate) "
		+ "	and ( "
		+ "			(CASE WHEN dbo.leaveFromDate = (:leaveFromDate) THEN ( "
		+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveFromDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveFromDateSession) = 'Evening')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE "
		+ "				"
		+ "				AND "
		+ "				"
		+ "			(CASE WHEN dbo.leaveToDate = (:leaveFromDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveFromDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE)"
		+ "	AND "
		+ "	((:leaveToDate) = dbo.leaveFromDate and (:leaveToDate) = dbo.leaveToDate) "
		+ "	and ("
		+ "			(CASE WHEN dbo.leaveFromDate = (:leaveToDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveFromDateSession = (:leaveToDateSession)) OR (dbo.leaveFromDateSession = 'Morning' and (:leaveToDateSession) = 'Evening')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END ) =TRUE "
		+ "				"
		+ "				AND "
		+ "			"
		+ "			(CASE WHEN dbo.leaveToDate = (:leaveToDate) THEN ("
		+ "					CASE WHEN ((dbo.leaveToDateSession = (:leaveToDateSession)) OR (dbo.leaveToDateSession = 'Evening' and (:leaveFromDateSession) = 'Morning')) THEN "
		+ "						true ELSE false END) "
		+ "			ELSE true END) = TRUE) "
		+ " OR "
		+ "	((:leaveFromDate) < dbo.leaveFromDate AND ((:leaveToDate) >= dbo.leaveFromDate))"
		+ " )"
		+ " and dbo.hostelAdmissionDBO.studentDBO.registerNo = :registerNo ";
		
	
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id !=: id ";
		}
		String finalStr = str;
		List<HostelLeaveApplicationsDBO>  dbo = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelLeaveApplicationsDBO> query = s.createQuery(finalStr, HostelLeaveApplicationsDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("leaveFromDate", dto.getLeaveFromDate());
			query.setParameter("leaveToDate", dto.getLeaveToDate());
			query.setParameter("registerNo", dto.getHostelAdmissionsDTO().getStudentDTO().getRegisterNo());
			query.setParameter("leaveToDateSession", dto.getLeaveToDateSession());
			query.setParameter("leaveFromDateSession", dto.getLeaveFromDateSession());
			return query.getResultList();
		}).await().indefinitely();
		return dbo;
	}

	public String getApplicantStatusDisplayText(String type) {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo where dbo.recordStatus='A' and dbo.applicationStatusDisplayText = :type ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,String.class).setParameter("type", type).getSingleResultOrNull()).await().indefinitely();
	}
}