package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDBO;
import com.christ.erp.services.dto.hostel.settings.HostelSeatAvailabilityDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Repository
public class SeatAvailabilityTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public List<Tuple> getHostelRoomType(int hostelId, int academicYearId) {
		String str = "select hostel_room_type.hostel_room_type_id as hostel_room_type_id, hostel_room_type.room_type as room_type,"
				+" hostel.hostel_id as hostel_id, hostel.hostel_name as hostel_name,"
				+" ifnull(max(hostel_appln.allocated_count),0) as allocatedCount,"
				+" ifnull(hostel_seat_availability_details.total_seats,count(hostel_rooms.hostel_room_type_id)*hostel_room_type.total_occupants) as totalSeat,"
				+" if(hostel_seat_availability_details.available_seats is null,"
				+" ifnull(hostel_seat_availability_details.total_seats,count(hostel_rooms.hostel_room_type_id)*hostel_room_type.total_occupants),"
				+" hostel_seat_availability_details.available_seats) as available_seats"
				+" from hostel "
				+" inner join hostel_room_type on hostel_room_type.hostel_id = hostel.hostel_id and hostel_room_type.record_status ='A'"
				+" left join hostel_rooms on hostel_rooms.hostel_room_type_id = hostel_room_type.hostel_room_type_id and hostel_rooms.record_status ='A'"
				+" left join hostel_seat_availability ON hostel_seat_availability.hostel_id = hostel.hostel_id and hostel_seat_availability.record_status='A'  and hostel_seat_availability.erp_academic_year_id=:academicYearId"
				+" left join hostel_seat_availability_details on hostel_seat_availability.hostel_seat_availability_id = hostel_seat_availability_details.hostel_seat_availability_id and hostel_seat_availability_details.hostel_room_type_id = hostel_room_type.hostel_room_type_id"
				+" and hostel_seat_availability_details.record_status ='A'"
				+" left join(select count(hostel_application.allotted_hostel_room_type_id) as allocated_count, hostel_application.allotted_hostel_room_type_id "
				+" from hostel_application "
				+" inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = hostel_application.hostel_application_current_process_status"
				+" and hostel_application.record_status='A'"
				+" and hostel_application.erp_academic_year_id = :academicYearId"
				+" and (erp_work_flow_process.process_code = 'HOSTEL_APPLICATION_SELECTED' OR erp_work_flow_process.process_code ='HOSTEL_APPLICATION_ADMITTED'"
				+" OR erp_work_flow_process.process_code ='HOSTEL_APPLICATION_SELECTED_UPLOADED') group by hostel_application.allotted_hostel_room_type_id) as hostel_appln on hostel_appln.allotted_hostel_room_type_id = hostel_room_type.hostel_room_type_id"			            
				+" where hostel.record_status = 'A' and hostel.hostel_id =:hostelId and hostel_room_type.room_type_category= 'Student'"
				+" group by hostel_room_type.hostel_room_type_id,hostel_seat_availability_details.hostel_seat_availability_details_id order by hostel_room_type.hostel_room_type_id";
		return sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).setParameter("hostelId", hostelId).setParameter("academicYearId", academicYearId).getResultList()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelSeatAvailabilityDBO.class, id)
				.chain(dbo -> session.fetch(dbo.getHostelSeatAvailabilityDetailsDBO())
						.invoke(subDbo -> {
							subDbo.forEach(dbos -> {
								dbos.setRecordStatus('D');
								dbos.setModifiedUsersId(userId);
							});
							dbo.setRecordStatus('D');
							dbo.setModifiedUsersId(userId);						
						}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public Mono<HostelSeatAvailabilityDBO> edit(int id) {
		String str = "select dbo from HostelSeatAvailabilityDBO dbo left join fetch dbo.hostelSeatAvailabilityDetailsDBO as dbos where dbo.recordStatus = 'A' and dbos.recordStatus = 'A' and dbo.id= :id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelSeatAvailabilityDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
	}

	public void update(HostelSeatAvailabilityDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelSeatAvailabilityDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(HostelSeatAvailabilityDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public boolean duplicateCheck(HostelSeatAvailabilityDTO dto) {
		String str = " from HostelSeatAvailabilityDBO dbo where dbo.recordStatus='A' and dbo.academicYearDBO.id =:academicId and dbo.hostelDBO.id =:hostelId";
		if(!Utils.isNullOrEmpty(dto.getId())) {
			str += " and dbo.id != :id";
		}
		String finalStr = str;
		List<HostelSeatAvailabilityDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelSeatAvailabilityDBO> query = s.createQuery(finalStr, HostelSeatAvailabilityDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("academicId",Integer.parseInt(dto.getAcademicYearDTO().getValue()));
			query.setParameter("hostelId", Integer.parseInt(dto.getHostelDTO().getValue()));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Mono<List<HostelSeatAvailabilityDBO>> getGridData(String yearId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew();
		String str = " select distinct dbo from HostelSeatAvailabilityDBO dbo"
				     +" left join fetch dbo.hostelSeatAvailabilityDetailsDBO as dbos"
			    	 +" where dbo.recordStatus ='A' and dbo.academicYearDBO.id =:yearId and dbos.recordStatus ='A' and dbos.hostelRoomTypeDBO.roomTypeCategory ='Student'";
		String finalStr = str;
		Mono<List<HostelSeatAvailabilityDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<HostelSeatAvailabilityDBO> query = s.createQuery(finalStr, HostelSeatAvailabilityDBO.class);
		if(!Utils.isNullOrEmpty(yearId)) {
			query.setParameter("yearId", Integer.parseInt(yearId));
		} else {
			query.setParameter("yearId",  currYear.getId());
		}
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}
}
