package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

import reactor.core.publisher.Mono;

@Repository
public class CurriculumDevelopmentCommitteeTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public List<ErpCommitteeDBO> getGridData(String yearId){
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew();
		String str =" select dbo from ErpCommitteeDBO dbo "
				+ " left join fetch dbo.erpCommitteeCampusDBOSet as eccds "
				+ " where dbo.recordStatus = 'A' and dbo.erpAcademicYearDBO.recordStatus = 'A' "
				+ " and dbo.erpCommitteeTypeDBO.recordStatus = 'A' and eccds.recordStatus = 'A' and "
				+ " dbo.erpCommitteeTypeDBO.committeeType = 'CDC' and dbo.erpAcademicYearDBO.id = :yearId ";
		String finalStr = str;
		List<ErpCommitteeDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<ErpCommitteeDBO> query = s.createQuery(finalStr,ErpCommitteeDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				String yr = yearId.trim();
				query.setParameter("yearId", Integer.parseInt(yr));
			}else {
				query.setParameter("yearId",currYear.getId());
			}
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCommitteeDBO.class, id)
				.chain(dbo1 -> session.fetch(dbo1.getErpCommitteeMembersDBOSet())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
						})
						.chain(dbo2 -> session.fetch(dbo1.getErpCommitteeCampusDBOSet()))
						.invoke(subSet2 -> {
							subSet2.forEach(subDbo2 -> {
								subDbo2.setRecordStatus('D');
								subDbo2.setModifiedUsersId(userId);
							});
							dbo1.setRecordStatus('D');
							dbo1.setModifiedUsersId(userId);
						})
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public void update(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCommitteeDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}

	public ErpCommitteeDBO edit(int id) {
		String str = "select dbo from ErpCommitteeDBO dbo"
				+ " left join fetch  dbo.erpCommitteeMembersDBOSet ecds "
				+ " left join fetch dbo.erpCommitteeCampusDBOSet eccs"
				+ " where dbo.id = :id and dbo.recordStatus ='A' and ecds.recordStatus ='A' and eccs.recordStatus ='A'  ";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCommitteeDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public Integer getCampusDepartmentMapping(String campusId,String departmentId) {
		String str =" select erp_campus_department_mapping_id from erp_campus_department_mapping"
				+ " where erp_campus_id = :campusId and erp_department_id = :departmentId and record_status = 'A'" ;
		return sessionFactory.withSession(s-> s.createNativeQuery(str, Integer.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("departmentId", Integer.parseInt(departmentId)).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(ErpCommitteeDTO erpCommitteeDTO) {
		String query =" select  db from ErpCommitteeDBO db "
				+ " where db.erpAcademicYearDBO.id = :aId and db.erpDepartmentDBO.id = :deptId and db.erpCommitteeTypeDBO.committeeType = 'CDC' and db.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getId())) {
			query += " and db.id != :id";
		}
		String finalquery = query;
		List<ErpCommitteeDBO> list = sessionFactory.withSession( s-> { Mutiny.Query<ErpCommitteeDBO> str = s.createQuery(finalquery,ErpCommitteeDBO.class);
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getImportToYear())) {
			str.setParameter("aId",Integer.parseInt( erpCommitteeDTO.getImportToYear().getValue()));
		}else {
			str.setParameter("aId",Integer.parseInt( erpCommitteeDTO.getErpAcademicYear().getValue()));
		}
		str.setParameter("deptId",Integer.parseInt(erpCommitteeDTO.getErpDepartment().getValue()));
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getId())) {
			str.setParameter("id",erpCommitteeDTO.getId());
		}
		return str.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Integer getCdcId() {
		String str = " select erp_committee_type.erp_committee_type_id as cdcId from erp_committee_type "
				+ "where erp_committee_type.record_status = 'A' and erp_committee_type.committee_type = 'CDC' ";
		return  sessionFactory.withSession(s->s.createNativeQuery(str,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}

	public ErpCommitteeDBO getPreviousData(String deptId, String importfromyear) {
		String str ="select dbo from ErpCommitteeDBO dbo"
				+ " left join fetch  dbo.erpCommitteeMembersDBOSet ecds "
				+ " left join fetch dbo.erpCommitteeCampusDBOSet eccs "
				+ " where dbo.erpAcademicYearDBO.id = :importfromyear and dbo.erpDepartmentDBO.id = :deptId and dbo.recordStatus = 'A' and ecds.recordStatus ='A' and eccs.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpCommitteeDBO.class).setParameter("importfromyear", Integer.parseInt(importfromyear)).setParameter("deptId", Integer.parseInt(deptId)).getSingleResultOrNull()).await().indefinitely();
	}

	public void merge(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}	

	public Integer getMembersId() {
		String query ="select erp_committee_role.erp_committee_role_id as cdcId from erp_committee_role "
				+ " where erp_committee_role.record_status = 'A' and erp_committee_role.committee_role = 'Member' ";
		return  sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}
}
