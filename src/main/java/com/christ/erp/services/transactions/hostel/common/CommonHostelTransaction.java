package com.christ.erp.services.transactions.hostel.common;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelDisciplinaryActionsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelLeaveTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBedDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDisciplinaryActionsTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFacilitySettingDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFloorDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDetailsDBO;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class CommonHostelTransaction {

	private static volatile CommonHostelTransaction commonHostelTransaction = null;

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public static CommonHostelTransaction getInstance() {
		if(commonHostelTransaction==null) {
			commonHostelTransaction = new CommonHostelTransaction();
		}
		return commonHostelTransaction;
	}

	public List<Tuple> getHostels() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_BLOCK = "select  hostel_id as ID,hostel_name as Text from hostel where record_status='A' ";
				Query query = context.createNativeQuery(SELECT_BLOCK, Tuple.class);				
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getHostelFacility() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select  hostel_facility_setting_id as ID,facility_name as Text from hostel_facility_setting where record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);				
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple getFloorsByBlockUnitId(String blockUnitId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String SELECT_UNIT = "select  total_floors from hostel_block_unit where hostel_block_unit_id=:blockUnitId and record_status='A' ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("blockUnitId", Integer.parseInt(blockUnitId));
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getUnitsByBlockAndHostelId(String blockId, String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_UNIT = "select  hostel_block_unit_id as ID,hostel_unit as Text from hostel_block_unit where record_status='A' and  hostel_block_id=:blockId ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("blockId", blockId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getBlockByHostelId(String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) {
				String SELECT_UNIT = "select distinct hostel_block.hostel_block_id as ID,hostel_block.hostel_block_name as Text  from hostel_block " +
						"where  hostel_block.hostel_id=:hostelId" +
						" and hostel_block.record_status='A'";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("hostelId", hostelId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getHostelRoomTypes(String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_UNIT = "select  hostel_room_type_id as ID,room_type as Text,total_occupants from hostel_room_type where record_status='A' and hostel_id=:hostelId";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				query.setParameter("hostelId", hostelId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<HostelProgrammeDetailsDBO> getCampusProgrammesTree(String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelProgrammeDetailsDBO>>() {
			@Override
			public List<HostelProgrammeDetailsDBO> onRun(EntityManager context) throws Exception {
				List<HostelProgrammeDetailsDBO> dboList = null;		
				StringBuffer sb = new StringBuffer();
				sb.append(" FROM HostelProgrammeDetailsDBO P "
						+ " left join fetch P.erpCampusProgrammeMappingDBO C WHERE");
				if(!Utils.isNullOrEmpty(hostelId)) {
					sb.append(" P.hostelDBO.id=:HostelId ");
				}
				sb.append(" and P.recordStatus='A' and (C is null or C.recordStatus='A') ORDER BY C.erpCampusDBO.id ASC");
				Query query = context.createQuery(sb.toString());
				if(!Utils.isNullOrEmpty(hostelId)) 
					query.setParameter("HostelId", Integer.parseInt(hostelId));
				dboList = (List<HostelProgrammeDetailsDBO>) query.getResultList();	
				return dboList;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public List<Tuple> getHostelBlocks() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_UNIT = "select  hostel_block_id as ID,hostel_block_name as Text from hostel_block where record_status='A' ";
				Query query = context.createNativeQuery(SELECT_UNIT, Tuple.class);
				//	query.setParameter("hostelId", hostelId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public HostelDBO getHostelSeatsAvailable(String hostelId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public HostelDBO onRun(EntityManager context) throws Exception {
				Query q = context.createQuery("from HostelDBO bo where bo.recordStatus='A' and bo.id=:hostelId");
				q.setParameter("hostelId", Integer.parseInt(hostelId));
				return (HostelDBO) Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getHostelsByGender(String genderId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String SELECT_BLOCK = "select  hostel_id as ID,hostel_name as Text from hostel where record_status='A' and erp_gender_id=:genderId";
				Query query = context.createNativeQuery(SELECT_BLOCK, Tuple.class);		
				query.setParameter("genderId", genderId);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Mono<List<HostelDBO>> getHostel() {
		String str = " from HostelDBO dbo where dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<Tuple>> getHostelByUser(String userId) {
		String str = "select distinct hostel.hostel_id as hostel_id, hostel.hostel_name as hostel_name from hostel"
				+" inner join hostel_block on hostel_block.hostel_id = hostel.hostel_id and hostel_block.record_status ='A'"
				+" inner join hostel_block_unit on hostel_block_unit.hostel_block_id = hostel_block.hostel_block_id and hostel_block_unit.record_status ='A'"
				+" inner join hostel_block_unit_details on hostel_block_unit_details.hostel_block_unit_id = hostel_block_unit.hostel_block_unit_id "
				+" and hostel_block_unit_details.record_status ='A'"
				+" where hostel_block_unit_details.erp_users_id =:userId and hostel.record_status ='A'";
		return Mono.fromFuture(sessionFactory.withSession(q -> q.createNativeQuery(str, Tuple.class)
				.setParameter("userId", Integer.parseInt(userId))
				.getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelBlockUnitDBO>> getUnitsByBlock(String blockId) {
		String str = " select dbo from HostelBlockUnitDBO dbo"
				+" where dbo.hostelBlockDBO.id =:blockId and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBlockUnitDBO.class).setParameter("blockId",Integer.parseInt(blockId)).getResultList()).subscribeAsCompletionStage());
	}

	public List<HostelRoomTypeDBO> getRoomTypeByCount(String hostelId) {
		String str = " select distinct dbo from HostelRoomTypeDBO dbo where dbo.recordStatus ='A' and dbo.hostelDBO.id = :hostelId and dbo.roomTypeCategory ='Student'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelRoomTypeDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpWorkFlowProcessDBO>> getStatus() {
		String str = "select dbo from ErpWorkFlowProcessDBO dbo where (dbo.processCode ='HOSTEL_APPLICATION_SELECTED_UPLOADED' or dbo.processCode ='HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED') and dbo.recordStatus ='A' order by dbo.id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpWorkFlowProcessDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<HostelApplicationDBO> getHostelApplicationCount(String hostelId, String academicYearId, List<Integer> roomTypeIds) {
		String str = "select dbo from HostelApplicationDBO dbo "
				+" where (dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_SELECTED' or dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_ADMITTED' or dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_SELECTED_UPLOADED')"
				+" and dbo.erpAcademicYearDBO.id =:academicYearId and dbo.hostelDBO.id =:hostelId and dbo.allottedHostelRoomTypeDBO.id IN (:roomTypeIds)"
				+" and dbo.recordStatus ='A'";  
		return sessionFactory.withSession(s->s.createQuery(str, HostelApplicationDBO.class).setParameter("hostelId", Integer.parseInt(hostelId))
				.setParameter("academicYearId", Integer.parseInt(academicYearId)).setParameter("roomTypeIds", roomTypeIds).getResultList()).await().indefinitely();
	}

	public List<HostelSeatAvailabilityDetailsDBO> getSeatAvailabilityCount(String hostelId, String academicYearId, List<Integer> roomTypeIds) {
		String str = " select distinct dbos from HostelSeatAvailabilityDetailsDBO dbos "
				+" where dbos.hostelSeatAvailabilityDBO.hostelDBO.id =:hostelId and dbos.hostelRoomTypeDBO.id IN (:roomTypeIds) and dbos.hostelSeatAvailabilityDBO.academicYearDBO.id =:academicYearId"
				+" and dbos.recordStatus ='A' and dbos.hostelSeatAvailabilityDBO.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelSeatAvailabilityDetailsDBO.class)
				.setParameter("hostelId", Integer.parseInt(hostelId)).setParameter("academicYearId", Integer.parseInt(academicYearId)).setParameter("roomTypeIds", roomTypeIds)
				.getResultList()).await().indefinitely();
	}

	public Mono<List<HostelBlockDBO>> getBlockByHostelId1(int hostelId) {
		String str = " select dbo from HostelBlockDBO dbo "
				+" where dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBlockDBO.class).setParameter("hostelId", hostelId).getResultList()).subscribeAsCompletionStage());
	}

	public List<HostelFacilitySettingDBO> getHostelFacilities() {
		String str = " select dbo from HostelFacilitySettingDBO dbo where dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelFacilitySettingDBO.class).getResultList()).await().indefinitely();
	}

	public Mono<List<HostelFloorDBO>> getFloorByUnit(String unitId) {
		String str = " select dbo from HostelFloorDBO dbo where dbo.recordStatus ='A' and dbo.hostelBlockUnitDBO.id =:unitId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelFloorDBO.class).setParameter("unitId", Integer.parseInt(unitId)).getResultList()).subscribeAsCompletionStage());
	}

	public List<HostelRoomTypeDetailsDBO> getFacilityList(String roomTypeId) {
		String str = " select dbo from HostelRoomTypeDetailsDBO dbo where dbo.recordStatus = 'A' and dbo.hostelRoomTypeDBO.id =:roomTypeId";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelRoomTypeDetailsDBO.class).setParameter("roomTypeId", Integer.parseInt(roomTypeId)).getResultList()).await().indefinitely();
	}

	public Mono<List<HostelBlockDBO>> getHostelBlockByUser(String hostelId, String userId) {
		String str = "select distinct dbo from HostelBlockDBO dbo"
				+ " inner join dbo.hostelBlockUnitDBOSet hbu"
				+ " inner join hbu.hostelBlockUnitDetailsDBOSet hbud"
				+" where dbo.recordStatus ='A' and dbo.hostelDBO.id =:hostelId and hbud.erpUsersDBO.id = :userId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBlockDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());		
	}

	public Boolean isUserSpecific(String userId) {
		String str = "select dbo from HostelBlockUnitDetailsDBO dbo"
				+ " where dbo.recordStatus ='A'and dbo.erpUsersDBO.id = :userId";
		var list= sessionFactory.withSession(s -> s.createQuery(str, HostelBlockUnitDetailsDBO.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();		
		return Utils.isNullOrEmpty(list)?false:true;		
	}

	public Mono<List<HostelBlockUnitDBO>> getHostelBlockUnitByUser(String userId,String blockId) {
		String str =  "select distinct hbu from HostelBlockUnitDBO hbu"
				+ " inner join hbu.hostelBlockDBO hb"
				+ " inner join hb.hostelDBO dbo"
				+ " inner join hbu.hostelBlockUnitDetailsDBOSet hbud"
				+ " where dbo.recordStatus ='A' and hbu.recordStatus ='A' and hbud.recordStatus ='A' and hb.recordStatus ='A'"
				+ " and hb.id =:blockId and hbud.erpUsersDBO.id = :userId ";
		String finalStr =str ;
		var list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<HostelBlockUnitDBO> query = s.createQuery(finalStr, HostelBlockUnitDBO.class);		
			query.setParameter("blockId", Integer.parseInt(blockId));
			query.setParameter("userId", Integer.parseInt(userId));
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public Mono<List<HostelLeaveTypeDBO>> getHostelLeaveType() {
		String str = "select dbo from HostelLeaveTypeDBO dbo where dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelLeaveTypeDBO.class).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<HostelDisciplinaryActionsTypeDBO>> getDisciplinaryActionType() {
		String str = " select dbo from HostelDisciplinaryActionsTypeDBO dbo where dbo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelDisciplinaryActionsTypeDBO.class).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<HostelBedDBO>> getBedByRoomId(String roomId) {
		String str = " from HostelBedDBO dbo where dbo.recordStatus ='A' and dbo.occupied = 0 and dbo.hostelRoomsDBO.id = :roomId and dbo.hostelRoomsDBO.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBedDBO.class).setParameter("roomId", Integer.parseInt(roomId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelRoomTypeDBO>> getRoomTypeForStudent(String hostelId) {
		String str = " from HostelRoomTypeDBO dbo where dbo.recordStatus ='A' and "
				+ " dbo.hostelDBO.id = : hostelId and dbo.roomTypeCategory = 'Student'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelRoomTypeDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelRoomsDBO>> getRoomByFloor(String floorId) {
		String str = "select DISTINCT dbo from HostelRoomsDBO dbo where dbo.recordStatus ='A' and dbo.hostelFloorDBO.id =:floorId";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelRoomsDBO.class).setParameter("floorId", Integer.parseInt(floorId)).getResultList()).subscribeAsCompletionStage());
	}

	public HostelFineEntryDBO edit(int id) {
		String str = "select dbo from HostelFineEntryDBO dbo where dbo.recordStatus ='A' and dbo.hostelDisciplinaryActionsDBO.id =:id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelFineEntryDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();	
	}

	public Mono<List<AccFeeHeadsDBO>> getAccHeadsForHostelFees(String hostelId) {
		String queryString = "select dbo from AccFeeHeadsDBO dbo "
				           +" left join fetch dbo.accFeeHeadsAccountList as dbos"
				           +" where dbo.recordStatus = 'A' and dbo.feeHeadsType = 'Hostel Fine' and dbo.hostelDBO.id =:hostelId and dbos.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, AccFeeHeadsDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<HostelAdmissionsDBO> getDataByStudentNameOrRegNo(String yearId, String regNo, String studentName) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =:yearId";
		if(!Utils.isNullOrEmpty(regNo)) { 
			str+= " and dbo.studentDBO.registerNo =:regNo";
		}
		if(!Utils.isNullOrEmpty(studentName)) {
			str+= " and dbo.studentDBO.studentName =:studentName";
		}
		String finalStr = str;
		return Mono.fromFuture(sessionFactory.withSession(s-> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(finalStr,HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(studentName)) { 
				query.setParameter("studentName", studentName); 
			}
			if(!Utils.isNullOrEmpty(regNo)) {
				query.setParameter("regNo", regNo);
			}
			if(!Utils.isNullOrEmpty(yearId)) {
				query.setParameter("yearId", Integer.parseInt(yearId));
			}
			return query.getSingleResultOrNull();
		}).subscribeAsCompletionStage());
		
	}

	public List<HostelDisciplinaryActionsDBO> getOtherDisciplinary(String yearId, String regNo) {
		String str = " from HostelDisciplinaryActionsDBO dbo where dbo.recordStatus ='A' and dbo.hostelAdmissionsDBO.erpAcademicYearDBO.id =:yearId "
				    +" and dbo.hostelAdmissionsDBO.studentDBO.registerNo =:regNo";
		return sessionFactory.withSession(s->s.createQuery(str, HostelDisciplinaryActionsDBO.class).setParameter("yearId", Integer.parseInt(yearId)).setParameter("regNo", regNo).getResultList()).await().indefinitely();
	}
	
	public Mono<List<HostelFineCategoryDBO>> getFineCategoryOthers() {
		String str = "select dbo from  HostelFineCategoryDBO dbo "
				+ " where dbo.recordStatus ='A' and dbo.isOthersFine = true";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelFineCategoryDBO.class).getResultList()).subscribeAsCompletionStage());		
	}

	public Mono<List<HostelAdmissionsDBO>> getStudentRegAndNameListFromHostel(String data, Integer academicYearId) {
		String str ="select dbo from HostelAdmissionsDBO dbo"
				+ " inner join dbo.studentDBO bo"
				+ " where dbo.recordStatus ='A' and bo.recordStatus ='A' and dbo.erpAcademicYearDBO.id = :academicYearId"
				+ " and (bo.studentName like :data or bo.registerNo like :data) and dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN' ";
		return Mono.fromFuture(sessionFactory.withSession(s-> s.createQuery(str,HostelAdmissionsDBO.class).setParameter("academicYearId", academicYearId).setParameter("data", data + "%").getResultList()).subscribeAsCompletionStage());
	}
	
	public List<Tuple> getHostelByGenderAndCampus(String genderId, String erpCampusProgrammeMappingId) {
		String str = "select hostel.hostel_id, hostel.hostel_name from hostel"
				+" inner join hostel_programme_details on hostel_programme_details.hostel_id = hostel.hostel_id and hostel_programme_details.record_status ='A'"
				+" where hostel.erp_gender_id =:genderId and hostel_programme_details.erp_campus_programme_mapping_id =:erpCampusProgrammeMappingId and hostel.record_status ='A'";
		return sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).setParameter("genderId", Integer.parseInt(genderId)).setParameter("erpCampusProgrammeMappingId", Integer.parseInt(erpCampusProgrammeMappingId)).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpWorkFlowProcessDBO>> getHostelSelectedStatus() {
		String str = "select dbo from ErpWorkFlowProcessDBO dbo where dbo.processCode ='HOSTEL_APPLICATION_SELECTED' and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpWorkFlowProcessDBO.class).getResultList()).subscribeAsCompletionStage());
	}

}
