package com.christ.erp.services.transactions.hostel.leavesandattendance;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelBlockLeavesDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;

import reactor.core.publisher.Mono;

@Repository
public class BlockOnlineLeaveApplicationTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<HostelAdmissionsDBO>> getStudentToBlock(String hostelId, String userId,String blockId, String blockUnitId,Boolean isUserSpecific,Integer academicYearId) {
		String blocked = "select dbo.hostelAdmissionsDBO.id from HostelBlockLeavesDBO dbo"
				+ " where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =: academicYearId";
		var blockedlist = sessionFactory.withSession(s -> s.createQuery(blocked, Integer.class).setParameter("academicYearId", academicYearId).getResultList()).await().indefinitely();		
		String str = "select distinct dbo from HostelAdmissionsDBO dbo"
				+ " inner join dbo.hostelDBO"
				+ " inner join dbo.hostelBedDBO as bed"
				+ " inner join bed.hostelRoomsDBO as room"
				+ " inner join room.hostelFloorDBO as floor"
				+ " inner join floor.hostelBlockUnitDBO as unit"
				+ " inner join unit.hostelBlockDBO as block";
		if(isUserSpecific) {
			str += " inner join unit.hostelBlockUnitDetailsDBOSet as unitD";
		}
		str += " where dbo.recordStatus ='A' and bed.recordStatus ='A' and room.recordStatus ='A' and floor.recordStatus ='A' and unit.recordStatus ='A' and block.recordStatus ='A' "
				+ "and dbo.hostelDBO.id =:hostelId and dbo.erpAcademicYearDBO.id =: academicYearId and dbo.erpStatusDBO.recordStatus ='A' and dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN'";
		if(!Utils.isNullOrEmpty(blockedlist)) {
			str +=" and dbo.id not in (:list)";
		}
		if(!Utils.isNullOrEmpty(blockId)) {
			str +=" and block.id =:blockId";
		}
		if(!Utils.isNullOrEmpty(blockUnitId)) {
			str +=" and unit.id =:blockUnitId";
		}
		if(isUserSpecific) {
			str += " and unitD.erpUsersDBO.id =: userId and unitD.recordStatus ='A'";
		}
		String finalStr = str;
		var list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr, HostelAdmissionsDBO.class);
			if (!Utils.isNullOrEmpty(blockId)) {
				query.setParameter("blockId", Integer.parseInt(blockId));
			}
			if(!Utils.isNullOrEmpty(blockUnitId)) {
				query.setParameter("blockUnitId", Integer.parseInt(blockUnitId));
			}
			if(isUserSpecific) {
				query.setParameter("userId", Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(blockedlist)) {
				query.setParameter("list", blockedlist);
			}
			query.setParameter("academicYearId", academicYearId);
			query.setParameter("hostelId", Integer.parseInt(hostelId));			
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return  list;
	}		
	
	public Mono<List<HostelBlockLeavesDBO>> getStudentToUnBlock(String hostelId, String userId,String blockId, String blockUnitId,Boolean isUserSpecific,Integer academicYearId) {
		String str = "select distinct hbl from HostelBlockLeavesDBO hbl  "
				+ "	inner join hbl.hostelAdmissionsDBO dbo"
				+ " inner join dbo.hostelDBO"
				+ " inner join dbo.hostelBedDBO as bed"
				+ " inner join bed.hostelRoomsDBO as room"
				+ " inner join room.hostelFloorDBO as floor"
				+ " inner join floor.hostelBlockUnitDBO as unit"
				+ " inner join unit.hostelBlockDBO as block";
				if(isUserSpecific) {
					str += " inner join unit.hostelBlockUnitDetailsDBOSet as unitD";
				}
				str += " where hbl.recordStatus ='A' and dbo.recordStatus ='A' and bed.recordStatus ='A' and room.recordStatus ='A' and floor.recordStatus ='A' and unit.recordStatus ='A' and block.recordStatus ='A'"
						+ " and dbo.hostelDBO.id =:hostelId and dbo.erpAcademicYearDBO.id =: academicYearId and dbo.erpStatusDBO.recordStatus ='A' and dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN'";	
		if(!Utils.isNullOrEmpty(blockId)) {
			str +=" and block.id =:blockId";
		}
		if(!Utils.isNullOrEmpty(blockUnitId)) {
			str +=" and unit.id =:blockUnitId";
		}
		if(isUserSpecific) {
			str += " and unitD.erpUsersDBO.id =: userId and unitD.recordStatus ='A'";
		}
		String finalStr = str;
		var list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<HostelBlockLeavesDBO> query = s.createQuery(finalStr, HostelBlockLeavesDBO.class);
			if (!Utils.isNullOrEmpty(blockId)) {
				query.setParameter("blockId", Integer.parseInt(blockId));
			}
			if(!Utils.isNullOrEmpty(blockUnitId)) {
				query.setParameter("blockUnitId", Integer.parseInt(blockUnitId));
			}
			if(isUserSpecific) {
				query.setParameter("userId", Integer.parseInt(userId));
			}
			query.setParameter("academicYearId", academicYearId);
			query.setParameter("hostelId", Integer.parseInt(hostelId));
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return  list;
	}


	public Boolean unblockStudents(List<Integer> list, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelBlockLeavesDBO.class, list.toArray()).invoke(dbo -> {
			dbo.forEach(db->{
				db.setModifiedUsersId(Integer.parseInt(userId));
				db.setRecordStatus('D');
			});
		})).await().indefinitely();
		return Boolean.TRUE;		
	}


	public void blockStudents(List<HostelBlockLeavesDBO> list) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(list.toArray())).subscribeAsCompletionStage();	
	}
}
