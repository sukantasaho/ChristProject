package com.christ.erp.services.transactions.hostel.fineanddisciplinary;

import java.time.LocalDate;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelDisciplinaryActionsDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Repository
public class DisciplinaryActionEntryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public List<HostelDisciplinaryActionsDBO> getGridData(String yearId, String hostelId, String blockId, String unitId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew(); 
		String str = " select dbo from HostelDisciplinaryActionsDBO dbo"
				+" where dbo.hostelAdmissionsDBO.erpAcademicYearDBO.id =:yearId "
				+" and dbo.hostelAdmissionsDBO.hostelDBO.id =:hostelId"
				+" and dbo.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(unitId)) { 
			str+= " and dbo.hostelAdmissionsDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.id =:unitId";
		}
		if(!Utils.isNullOrEmpty(blockId)) {
			str+= " and dbo.hostelAdmissionsDBO.hostelBedDBO.hostelRoomsDBO.hostelFloorDBO.hostelBlockUnitDBO.hostelBlockDBO.id =:blockId";
		}
		String finalStr = str;
		List<HostelDisciplinaryActionsDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelDisciplinaryActionsDBO> query = s.createQuery(finalStr, HostelDisciplinaryActionsDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				query.setParameter("yearId", Integer.parseInt(yearId));
			} else {
				query.setParameter("yearId",currYear.getId());
			}
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			if(!Utils.isNullOrEmpty(unitId)) {
				query.setParameter("unitId", Integer.parseInt(unitId));
			}
			if(!Utils.isNullOrEmpty(blockId)) {
				query.setParameter("blockId", Integer.parseInt(blockId));	
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public HostelDisciplinaryActionsDBO edit(int id) {
		String str = " select distinct dbo from HostelDisciplinaryActionsDBO dbo "
				+" where dbo.recordStatus ='A' and dbo.id =:id ";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelDisciplinaryActionsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();	
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelDisciplinaryActionsDBO.class, id)
				.chain(bo -> session.fetch(bo.getHostelFineEntryDBO())
						.invoke(subDbo -> {
						subDbo.setRecordStatus('D');
						subDbo.setModifiedUsersId(userId);
						bo.setRecordStatus('D');
						bo.setModifiedUsersId(userId);						
				}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public void update(HostelDisciplinaryActionsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelDisciplinaryActionsDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public boolean save(HostelDisciplinaryActionsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
		return true;
	}

	public HostelDisciplinaryActionsDBO duplicateCheckdata(String regNo, Integer categoryId, LocalDate entryDate) {
		String str = " from HostelDisciplinaryActionsDBO dbo where dbo.recordStatus ='A' and dbo.disciplinaryActionsDate =:entryDate"
				    +" and dbo.hostelAdmissionsDBO.studentDBO.registerNo =:regNo and dbo.hostelDisciplinaryActionsTypeDBO.id =:categoryId";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelDisciplinaryActionsDBO.class).setParameter("regNo", regNo)
				.setParameter("categoryId", categoryId).setParameter("entryDate", entryDate)
				.getSingleResultOrNull()).await().indefinitely();
	}

	public HostelFineEntryDBO getFineEntryData(int id) {
	  String str = " from HostelFineEntryDBO dbo where dbo.recordStatus ='A' and dbo.hostelDisciplinaryActionsDBO.id =:id ";
	  return sessionFactory.withSession(s -> s.createQuery(str, HostelFineEntryDBO.class).setParameter("id", id)
				.getSingleResultOrNull()).await().indefinitely();	
	}

	public HostelFineCategoryDBO getFineCategory(String hostelId) {
		String query = " select distinct dbo from HostelFineCategoryDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.isDisciplinaryFine = true and dbo.hostelDBO.id =:hostelId";
		return sessionFactory.withSession(s->s.createQuery(query,HostelFineCategoryDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getSingleResultOrNull()).await().indefinitely();
	}
}
