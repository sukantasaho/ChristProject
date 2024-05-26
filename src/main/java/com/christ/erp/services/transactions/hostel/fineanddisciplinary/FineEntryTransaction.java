package com.christ.erp.services.transactions.hostel.fineanddisciplinary;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.transactions.hostel.common.CommonHostelTransaction;

import reactor.core.publisher.Mono;


@Repository
public class FineEntryTransaction {


	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonHostelTransaction commonHostelTransaction;

	public Mono<List<HostelFineEntryDBO>> getGridData(Integer academicYearId,Integer hostelId,Integer blockId,Integer unitId,String userId ) {	
		Boolean isUserSpecific = commonHostelTransaction.isUserSpecific(userId);
		String str = "select dbo from HostelFineEntryDBO dbo"
				+ " inner join dbo.hostelFineCategoryDBO hfc"
				+ " inner join dbo.hostelAdmissionsDBO ha"
				+ " inner join ha.hostelDBO"
				+ " inner join ha.hostelBedDBO as bed"
				+ " inner join bed.hostelRoomsDBO as room"
				+ " inner join room.hostelFloorDBO as floor"
				+ " inner join floor.hostelBlockUnitDBO as unit"
				+ " inner join unit.hostelBlockDBO as block";
		if(isUserSpecific) {
			str += " inner join unit.hostelBlockUnitDetailsDBOSet as unitD";
		}
		str += " where dbo.recordStatus = 'A' and hfc.recordStatus = 'A' and ha.recordStatus = 'A' and hfc.isOthersFine = true and "
				+ " ha.erpAcademicYearDBO.id =:academicYearId and ha.hostelDBO.id =: hostelId and (dbo.isCompletelyPaid = false or dbo.isCompletelyPaid = null) ";
		if (!Utils.isNullOrEmpty(blockId)) {
			str += " and block.id =: blockId";
			if (!Utils.isNullOrEmpty(unitId)) {
				str += " and unit.id =: unitId";
			}
		}
		if(isUserSpecific) {
			str += " and unitD.erpUsersDBO.id =: userId";
		}
		String finalStr = str;
		Mono<List<HostelFineEntryDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<HostelFineEntryDBO> query = s.createQuery(finalStr, HostelFineEntryDBO.class);
			query.setParameter("academicYearId", academicYearId);
			query.setParameter("hostelId", hostelId);
			if (!Utils.isNullOrEmpty(blockId)) {
				query.setParameter("blockId", blockId);
			}
			if (!Utils.isNullOrEmpty(unitId)) {
				query.setParameter("unitId", unitId);
			}
			if(isUserSpecific) {
				query.setParameter("userId", Integer.parseInt(userId));
			}
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelFineEntryDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public HostelFineEntryDBO edit(int id) {
		return sessionFactory.withSession(s -> s.find(HostelFineEntryDBO.class, id )).await().indefinitely();
	}

	public void update(HostelFineEntryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelFineEntryDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(HostelFineEntryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}
}
