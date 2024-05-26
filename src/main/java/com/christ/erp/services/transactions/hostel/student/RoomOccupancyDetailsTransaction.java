package com.christ.erp.services.transactions.hostel.student;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Repository
public class RoomOccupancyDetailsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public Mono<List<HostelBlockDBO>> getBlock(int hostelId) {
		String str = " select dbo from HostelBlockDBO dbo "
				+" where dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBlockDBO.class).setParameter("hostelId", hostelId).getResultList()).subscribeAsCompletionStage());
	}

	public Mono<List<HostelBlockUnitDBO>> getUnitsByBlock(String blockId) {
		String str = " select dbo from HostelBlockUnitDBO dbo"
				+" where dbo.hostelBlockDBO.id =:blockId and dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelBlockUnitDBO.class).setParameter("blockId",Integer.parseInt(blockId)).getResultList()).subscribeAsCompletionStage());
	}

	public List<Tuple> getOccupancyDetails(int yearId, int hostelId, String blockId, String unitId) {
		String query = " select DISTINCT hostel_block.hostel_block_id as hostel_block_id, hostel_block.hostel_block_name as hostel_block_name,"
				+" hostel_block_unit.hostel_block_unit_id as hostel_block_unit_id, hostel_block_unit.hostel_unit as hostel_unit,"
				+" hostel_floor.hostel_floor_id as hostel_floor_id, hostel_bed.is_occupied as is_occupied,"
				+" hostel_rooms.hostel_rooms_id as hostel_rooms_id, hostel_bed.hostel_bed_id as hostel_bed_id,"
				+" hostel_room_type.hostel_room_type_id as hostel_room_type_id, hostel_room_type.room_type as room_type,"
				+" hostel_bed.bed_no as bed_no, hostel_rooms.room_no as room_no, hostel_admissions.hostel_admissions_id as hostel_admissions_id,"
				+" hostel_floor.floor_no as floor_no, student.student_id as student_id, student.register_no as register_no,"
				+" student.student_name as student_name, erp_campus.campus_name as campus_name, erp_programme.programme_name as programme_name,"
				+" hostel.hostel_id as hostel_id"
				+" from hostel_bed"
				+" inner join hostel_rooms on hostel_rooms.hostel_rooms_id = hostel_bed.hostel_rooms_id and hostel_rooms.record_status ='A'"
				+" inner join hostel_room_type ON hostel_room_type.hostel_room_type_id = hostel_rooms.hostel_room_type_id and hostel_room_type.record_status ='A'"
				+" inner join hostel_floor ON hostel_floor.hostel_floor_id = hostel_rooms.hostel_floor_id and hostel_floor.record_status= 'A'"
				+" inner join hostel_block_unit ON hostel_block_unit.hostel_block_unit_id = hostel_floor.hostel_block_unit_id and hostel_block_unit.record_status ='A'"
				+" inner join hostel_block ON hostel_block.hostel_block_id = hostel_block_unit.hostel_block_id and hostel_block.record_status ='A'"
				+" inner join hostel ON hostel.hostel_id = hostel_block.hostel_id and hostel.record_status ='A'"
				+" left join hostel_admissions on hostel_bed.hostel_bed_id = hostel_admissions.hostel_bed_id  and hostel_admissions.record_status ='A'"
				+" and hostel_admissions.erp_academic_year_id =:yearId"
				+" left join erp_status on hostel_admissions.erp_status_id = erp_status.erp_status_id  and erp_status.status_code ='HOSTEL_ADMITTED' or erp_status.status_code ='HOSTEL_CHECK_IN'"
				+" left join student on hostel_admissions.student_id = student.student_id and student.record_status ='A'"
				+" left join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student.erp_campus_programme_mapping_id"
				+" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status ='A'"
				+" left join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status ='A'"
				+" where hostel.hostel_id =:hostelId and hostel_bed.record_status ='A'";
		if(!Utils.isNullOrEmpty(blockId)) { 
			query+= " and hostel_block.hostel_block_id =:blockId";
		}
		if(!Utils.isNullOrEmpty(unitId)) {
			query+= " and hostel_block_unit.hostel_block_unit_id =:unitId";
		}
		query+= " group by hostel_block.hostel_block_id, hostel_block_unit.hostel_block_unit_id, hostel_floor.hostel_floor_id, hostel_admissions.hostel_admissions_id,"
			   +" hostel.hostel_id, hostel_rooms.hostel_rooms_id,  hostel_room_type.hostel_room_type_id, hostel_bed.hostel_bed_id, student.student_id"
			   +" order by hostel_block.hostel_block_id, hostel_block_unit.hostel_block_unit_id, hostel_floor.hostel_floor_id, hostel_rooms.hostel_rooms_id, hostel_bed.hostel_bed_id";
		String str = query;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(str, Tuple.class);
		if(!Utils.isNullOrEmpty(blockId) && !Utils.isNullOrEmpty(unitId)) {
			query1.setParameter("blockId", Integer.parseInt(blockId));
			query1.setParameter("unitId", Integer.parseInt(unitId));
		}
		query1.setParameter("yearId", yearId);
		query1.setParameter("hostelId", hostelId);
		return query1.getResultList();
		}).await().indefinitely();
		return list;
	}
}
