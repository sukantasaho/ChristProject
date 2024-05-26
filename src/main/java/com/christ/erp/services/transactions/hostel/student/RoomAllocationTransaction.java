package com.christ.erp.services.transactions.hostel.student;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;
import reactor.core.publisher.Mono;

@Repository
public class RoomAllocationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<HostelRoomTypeDBO>> getRoomTypeForStudent(String hostelId) {
		String str = " from HostelRoomTypeDBO dbo where dbo.recordStatus ='A' and "
				+ " dbo.hostelDBO.id = : hostelId and dbo.roomTypeCategory = 'Student'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelRoomTypeDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelRoomsDBO>> getRoomByUnitAndFloor(String unitId) {
		String str = "select DISTINCT dbo from HostelRoomsDBO dbo "
				+ " left join fetch dbo.hostelBedDBOSet as hds"
				+ " where dbo.recordStatus ='A' and "
				+ " dbo.hostelFloorDBO.hostelBlockUnitDBO.id = :unitId and "
				+ " dbo.hostelFloorDBO.recordStatus ='A' and "
				+ " dbo.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A' "
				+ " and hds.occupied=0";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelRoomsDBO.class).setParameter("unitId", Integer.parseInt(unitId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelBedDBO>> getBedByRoomId(String roomId) {
		String str = " from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.occupied = 0 and dbo.hostelRoomsDBO.id = :roomId and dbo.hostelRoomsDBO.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBedDBO.class).setParameter("roomId", Integer.parseInt(roomId)).getResultList()).subscribeAsCompletionStage());
	}

	public List<HostelAdmissionsDBO> getGridData(String academicYearId,String hostelId,String roomTypeId) {
		Integer erpStatusId = this.erpStatus();
		String str =" from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and "
				+ " dbo.erpStatusDBO.id = :erpStatusId and dbo.erpStatusDBO.recordStatus ='A' and "
				+ " dbo.erpAcademicYearDBO.id = :academicYearId  and dbo.erpAcademicYearDBO.recordStatus ='A' and "
				+ " dbo.hostelDBO.id = :hostelId and dbo.hostelDBO.recordStatus ='A' and " //and dbo.hostelBedDBO.id = null
				+ " dbo.hostelApplicationDBO.recordStatus ='A' and dbo.studentApplnEntriesDBO.recordStatus ='A' and "
				+ " dbo.studentDBO.recordStatus ='A' ";
		if(!Utils.isNullOrEmpty(roomTypeId)) {
			str += "and dbo.hostelRoomTypeDBO.id = :roomTypeId and dbo.hostelRoomTypeDBO.recordStatus ='A'";
		}
		String finalStr = str;
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			query.setParameter("erpStatusId",erpStatusId);
			query.setParameter("academicYearId",Integer.parseInt(academicYearId));
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			if(!Utils.isNullOrEmpty(roomTypeId)) {
				query.setParameter("roomTypeId",Integer.parseInt(roomTypeId));
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public Integer erpStatus() {
		String str = " select es.erp_status_id from erp_status as es where es.record_status = 'A' and es.status_code='HOSTEL_ADMITTED'";
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).getSingleResultOrNull()).await().indefinitely();	
	}

	public boolean update(List<HostelAdmissionsDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}

	public boolean update1(List<HostelBedDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}

	public List<HostelAdmissionsDBO> getData(List<Integer> hosetAdmissionIds) {
		String str = "from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and dbo.id IN (:hosetAdmissionIds)";
		String finalStr = str;
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr, HostelAdmissionsDBO.class);
			query.setParameter("hosetAdmissionIds", hosetAdmissionIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<HostelAdmissionsDBO>  duplicateCheck(List<Integer> hostelBedList,String hostelId, String admissionYearId) {
		String str = " select DISTINCT dbo from HostelAdmissionsDBO dbo "
				+ "	where dbo.recordStatus = 'A' and dbo.erpAcademicYearDBO.recordStatus = 'A' and "
				+ " dbo.erpStatusDBO.recordStatus = 'A' and "
				+ " dbo.hostelDBO.recordStatus = 'A' and "
				+ " dbo.hostelBedDBO.id IN (:hostelBedList) and "
				+ " dbo.hostelDBO.id=:hostelId and "
				+ " dbo.erpAcademicYearDBO.id= :admissionYearId and "
				+ "	(dbo.erpStatusDBO.statusCode ='HOSTEL_ADMITTED' or dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN' )";
		return sessionFactory.withSession(s->s.createQuery(str, HostelAdmissionsDBO.class).setParameter("hostelBedList", hostelBedList).setParameter("admissionYearId", Integer.parseInt(admissionYearId)).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).await().indefinitely();
	}

	public List<HostelBlockDBO> getBlockByHostelId(String hostelId) {
		String str = "select dbo from HostelBlockDBO dbo where dbo.hostelDBO.id=:hostelId and dbo.recordStatus='A' and dbo.hostelDBO.recordStatus='A' ";
		return sessionFactory.withSession(s-> s.createQuery(str,HostelBlockDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).await().indefinitely();
	}

	public List<HostelBlockUnitDBO> getUnitByBlock(List<Integer> hostelBlockIds) {
		String str = "select dbo from HostelBlockUnitDBO dbo where dbo.hostelBlockDBO.id IN (:hostelBlockIds) and dbo.recordStatus = 'A' and dbo.hostelBlockDBO.recordStatus ='A' ORDER BY dbo.hostelBlockDBO.id " ;
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBlockUnitDBO.class).setParameter("hostelBlockIds", hostelBlockIds).getResultList()).await().indefinitely();
	}

	public Integer getHostelId(String hostelName) {
		String str = "select hostel.hostel_id as hostel_id from hostel where hostel_name=:hostelName and hostel.record_status = 'A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("hostelName", hostelName.trim()).getSingleResultOrNull()).await().indefinitely();
	}

	public List<HostelAdmissionsDBO> getUploadData(List<Integer> applicationNos,String admissionYearId,String hostelId){
		String str = "select dbo from HostelAdmissionsDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.hostelApplicationDBO.studentApplnEntriesDBO.applicationNo IN (:applicationNos) and "
				+ " dbo.erpAcademicYearDBO.id =: admissionYearId and dbo.erpAcademicYearDBO.recordStatus = 'A' and"
				+ " dbo.hostelDBO.id =: hostelId and dbo.hostelDBO.recordStatus = 'A' and "
				+ " dbo.hostelApplicationDBO.recordStatus = 'A' and dbo.hostelApplicationDBO.studentApplnEntriesDBO.recordStatus = 'A' ";
		String finalStr = str;
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr, HostelAdmissionsDBO.class);
			query.setParameter("applicationNos", applicationNos);
			query.setParameter("admissionYearId", Integer.parseInt(admissionYearId));
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<HostelRoomsDBO> getRoomListByUnit(List<Integer> hostelUnitIds) {
		String str = "select dbo  from HostelRoomsDBO dbo "
				+ " left join fetch dbo.hostelBedDBOSet as hds "
				+ " where dbo.recordStatus ='A' and "
				+ " dbo.hostelFloorDBO.recordStatus ='A' and dbo.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A' and "
				+ " hds.occupied = 0 and "
				+ " dbo.hostelFloorDBO.hostelBlockUnitDBO.id IN (:hostelUnitIds)";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelRoomsDBO.class).setParameter("hostelUnitIds", hostelUnitIds).getResultList()).await().indefinitely();
	}

	public List<HostelBedDBO> getBedListByRoom(List<Integer> hostelRoomIds) {
		String str = "select dbo from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.occupied = 0 and dbo.hostelRoomsDBO.id IN (:hostelRoomIds)";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBedDBO.class).setParameter("hostelRoomIds", hostelRoomIds).getResultList()).await().indefinitely();
	}

	public List<HostelBlockUnitDBO> getBlockAndUnit(List<String> blockName) {
		String str = "select dbo from HostelBlockUnitDBO dbo where dbo.hostelBlockDBO.blockName IN (:blockName) "
				+ " and dbo.recordStatus = 'A' " ;
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBlockUnitDBO.class).setParameter("blockName", blockName).getResultList()).await().indefinitely();
	}

	public List<HostelBlockDBO> getblockList(List<String> blockName) {
		String str = "select dbo from HostelBlockDBO dbo where dbo.blockName IN (:blockName) and dbo.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBlockDBO.class).setParameter("blockName", blockName).getResultList()).await().indefinitely();
	}

	public List<HostelBlockUnitDBO> getUnitList(List<String> unitName) {
		String str = "select dbo from HostelBlockUnitDBO dbo where dbo.hostelUnit IN (:unitName) and dbo.recordStatus ='A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBlockUnitDBO.class).setParameter("unitName", unitName).getResultList()).await().indefinitely();
	}

	public List<HostelRoomsDBO> getUnitRoom(List<String> unitName) {
		String str = " from HostelRoomsDBO dbo where dbo.recordStatus ='A' and dbo.hostelFloorDBO.hostelBlockUnitDBO.hostelUnit IN (:unitName) and dbo.hostelFloorDBO.recordStatus ='A' and dbo.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A' ";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelRoomsDBO.class).setParameter("unitName", unitName).getResultList()).await().indefinitely();
	}

	public List<HostelRoomsDBO> getRoomList(List<String> roomNo,List<Integer> unitIds) {
		String str = "select dbo from HostelRoomsDBO dbo where  dbo.recordStatus ='A' and dbo.roomNo IN (:roomNo) and dbo.hostelFloorDBO.hostelBlockUnitDBO.id IN (:unitIds) and "
				+ " dbo.hostelFloorDBO.recordStatus ='A' and dbo.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelRoomsDBO.class).setParameter("roomNo", roomNo).setParameter("unitIds", unitIds).getResultList()).await().indefinitely();
	}

	public List<HostelBedDBO> getRoomBedList(List<Integer> roomIds, List<Integer> unitIds) {
		String str = "select dbo from HostelBedDBO dbo where dbo.recordStatus = 'A' and "
				+ " dbo.hostelRoomsDBO.hostelFloorDBO.recordStatus ='A' and dbo.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A' and "
				+ " dbo.hostelRoomsDBO.id IN (:roomIds) and dbo.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.id IN (:unitIds) ";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBedDBO.class).setParameter("roomIds", roomIds).setParameter("unitIds", unitIds).getResultList()).await().indefinitely();
	}

	public List<HostelBedDBO> getBedIds(List<String> bedList,List<Integer> roomIdsForBed) {
		String str = "select dbo from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.bedNo IN (:bedList) and dbo.hostelRoomsDBO.id IN (:roomIdsForBed) "
				+ " and dbo.hostelRoomsDBO.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,HostelBedDBO.class).setParameter("bedList", bedList).setParameter("roomIdsForBed", roomIdsForBed).getResultList()).await().indefinitely();
	}

	public List<HostelBedDBO> getHostelBed(List<Integer> bedList) {
		String str = "select dbo from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.id IN (:bedList)";
		return sessionFactory.withSession(s-> s.createQuery(str,HostelBedDBO.class).setParameter("bedList", bedList).getResultList()).await().indefinitely();
	}

	public Mono<List<HostelRoomsDBO>> getRoomByUnitAndFloor1(String unitId,String roomType) {
		String str = "select DISTINCT dbo from HostelRoomsDBO dbo "
				+ " left join fetch dbo.hostelBedDBOSet as hds"
				+ " where dbo.recordStatus ='A' and "
				+ " dbo.hostelRoomTypeDBO.roomType =: roomType and dbo.hostelRoomTypeDBO.recordStatus = 'A' and "
				+ " dbo.hostelFloorDBO.hostelBlockUnitDBO.id = :unitId and "
				+ " dbo.hostelFloorDBO.recordStatus ='A' and "
				+ " dbo.hostelFloorDBO.hostelBlockUnitDBO.recordStatus ='A' "
				+ " and hds.occupied=0";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelRoomsDBO.class).setParameter("unitId", Integer.parseInt(unitId)).setParameter("roomType", roomType).getResultList()).subscribeAsCompletionStage());
	}

	public List<Tuple> gethostelDetails(String hostelId) {
		String str = " select hostel.hostel_id as hostel_id,hostel.hostel_name as hostel_name,hostel_block.hostel_block_id as hostel_block_id,hostel_block.hostel_block_name as hostel_block_name,hostel_block_unit.hostel_block_unit_id as hostel_block_unit_id,hostel_block_unit.hostel_unit as hostel_unit, "
				+ " hostel_rooms.hostel_rooms_id as hostel_rooms_id,hostel_rooms.room_no as room_no,hostel_room_type.hostel_room_type_id as hostel_room_type_id,hostel_room_type.room_type as room_type,hostel_bed.hostel_bed_id as hostel_bed_id,hostel_bed.bed_no as bed_no, "
				+ " hostel_floor.hostel_floor_id as hostel_floor_id,hostel_floor.floor_no as floor_no "
				+ " from hostel "
				+ " left join hostel_block ON hostel.hostel_id = hostel_block.hostel_id and hostel_block.record_status='A' "
				+ " left join hostel_block_unit ON hostel_block_unit.hostel_block_id = hostel_block.hostel_block_id and hostel_block_unit.record_status='A' "
				+ " left join hostel_floor ON hostel_floor.hostel_block_unit_id = hostel_block_unit.hostel_block_unit_id and hostel_floor.record_status='A' "
				+ " left join hostel_rooms ON hostel_rooms.hostel_floor_id = hostel_floor.hostel_floor_id and hostel_rooms.record_status='A' "
				+ " left join hostel_room_type ON hostel_room_type.hostel_room_type_id = hostel_rooms.hostel_room_type_id and hostel_room_type.record_status='A' "
				+ " left join hostel_bed ON hostel_bed.hostel_rooms_id = hostel_rooms.hostel_rooms_id and hostel_bed.record_status='A' "
				+ " where hostel.record_status='A' and hostel.hostel_id=:hostelId and hostel_bed.is_occupied=0 "
				+ " group by hostel.hostel_id,hostel_block.hostel_block_id,hostel_block_unit.hostel_block_unit_id,hostel_floor.hostel_floor_id,hostel_rooms.hostel_rooms_id, "
				+ " hostel_room_type.hostel_room_type_id,hostel_bed.hostel_bed_id ";
		String finalStr = str;
		List<Tuple> hostelDBOList = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return hostelDBOList;
	}

	public HostelAdmissionsDBO edit(int id) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and dbo.id =: id ";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelAdmissionsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public void updateHostelAdmissionsDBO(HostelAdmissionsDBO hostelAdmissionsDBO) {
		sessionFactory.withTransaction((session, tx) -> session.merge(hostelAdmissionsDBO).chain(session::flush).map(s -> {
			return convertDbo(hostelAdmissionsDBO);
		}).flatMap(s -> session.merge(s))).await().indefinitely();
	}
	
	public HostelAdmissionsDBO convertDbo(HostelAdmissionsDBO hostelAdmissionsDBO) {
		hostelAdmissionsDBO.setHostelBedDBO(null);
		return hostelAdmissionsDBO;
	}

}