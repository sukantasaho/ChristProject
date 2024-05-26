package com.christ.erp.services.transactions.curriculum.settings;

import java.util.List;
import javax.persistence.Tuple;
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
public class DepartmentAdvisoryCommitteeTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	private CommonApiTransaction commonApiTransaction ;

	public List<Tuple> getGridData(String yearId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew(); 
		String str = "select distinct erp_committee.erp_committee_id as erp_committee_id,erp_academic_year.academic_year as academic_year ,erp_department.erp_department_id as erp_department_id ,erp_department.department_name as department_name , erp_programme.programme_name as programme_name ,erp_committee_type.committee_type as committee_type from erp_committee"
				+ "	inner join erp_academic_year on erp_academic_year.erp_academic_year_id = erp_committee.erp_academic_year_id and erp_academic_year.record_status = 'A'"
				+ "	inner join erp_department on erp_committee.erp_department_id = erp_department.erp_department_id and erp_department.record_status = 'A'"
				+ "	inner join erp_committee_type on erp_committee.erp_committee_type_id = erp_committee_type.erp_committee_type_id and erp_committee_type.record_status ='A'"
				+ "	inner join erp_committee_programme on erp_committee.erp_committee_id = erp_committee_programme.erp_committee_id and erp_committee_programme.record_status ='A'"
				+ "	inner join erp_programme ON erp_programme.erp_programme_id = erp_committee_programme.erp_programme_id and erp_programme.record_status ='A'"
				+ "	where erp_committee.record_status = 'A' and erp_committee_type.committee_type ='DAC' and erp_academic_year.erp_academic_year_id = :yearId";
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
		if(!Utils.isNullOrEmpty(yearId)) {
			query.setParameter("yearId", yearId);
		} else {
			query.setParameter("yearId",currYear.getId());
		}
		return query.getResultList();
		}).await().indefinitely();
		return list;
	}	

	public ErpCommitteeDBO edit(int id) {
		String query =" select distinct dbo from ErpCommitteeDBO dbo"
				+ " left join fetch dbo.erpCommitteeMembersDBOSet cmdb"
				+ " left join fetch dbo.erpCommitteeProgrammeDBOSet spdb"
				+ " where dbo.id = :id and dbo.recordStatus = 'A' and cmdb.recordStatus = 'A' and spdb.recordStatus = 'A'";
		return sessionFactory.withSession(s->s.createQuery(query,ErpCommitteeDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public void update(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCommitteeDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}

	public void merge(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
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
						.chain(dbo2 -> session.fetch(dbo1.getErpCommitteeProgrammeDBOSet()) )
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

	public Integer getDACId() {
		String query ="select erp_committee_type.erp_committee_type_id as dacId from erp_committee_type"
				+ " where erp_committee_type.record_status = 'A' and erp_committee_type.committee_type = 'DAC'";
		return  sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> externalCategoryMembers(int id) {
		String query ="select distinct erp_committee_members.erp_committee_members_id as membersid , erp_committee_members.externals_id as eid ,externals.external_name as ename ,erp_externals_category.erp_externals_category_id as cid , erp_externals_category.externals_category_name as cname from externals "
				+ " inner join erp_externals_category ON erp_externals_category.erp_externals_category_id = externals.erp_externals_category_id"
				+ " inner join erp_committee_members on erp_committee_members.externals_id = externals.externals_id"
				+ " where erp_committee_members.record_status = 'A' and externals.record_status = 'A' and erp_committee_members.erp_committee_id = :id "
				+ " and erp_committee_members.externals_id = externals.externals_id order by cid ";
		return  sessionFactory.withSession(s->s.createNativeQuery(query, Tuple.class).setParameter("id", id).getResultList()).await().indefinitely();
	}

	public List<String> getProgrammes() {		
		String query = "select distinct erp_department.department_name as department_name ,erp_programme_addtnl_details.programme_name as programme_name from erp_committee"
				+ " inner join erp_committee_type ON erp_committee_type.erp_committee_type_id = erp_committee.erp_committee_type_id"
				+ " inner join erp_department on erp_committee.erp_department_id = erp_department.erp_department_id"
				+ "	inner join erp_committee_programme on erp_committee.erp_committee_id = erp_committee_programme.erp_committee_id"
				+ "	inner join erp_programme_addtnl_details on erp_programme_addtnl_details.erp_programme_id = erp_committee_programme.erp_programme_id"
				+ "	where erp_committee.record_status = 'A' and erp_committee_type.committee_type ='DAC'";
		return  sessionFactory.withSession(s->s.createNativeQuery(query, String.class).getResultList()).await().indefinitely();
	}

	public ErpCommitteeDBO getValue( String fromYear, String deptId) {
		String query =" select distinct dbo from ErpCommitteeDBO dbo "
				+ " left join fetch dbo.erpCommitteeMembersDBOSet cmdb"
				+ " left join fetch dbo.erpCommitteeProgrammeDBOSet spdb"
				+ " where dbo.erpAcademicYearDBO.id = :fromYear and dbo.erpDepartmentDBO.id =:deptId and dbo.erpCommitteeTypeDBO.committeeType = 'DAC' and dbo.recordStatus = 'A' and cmdb.recordStatus = 'A' and spdb.recordStatus = 'A' ";
		return sessionFactory.withSession(s->s.createQuery(query,ErpCommitteeDBO.class).setParameter("fromYear", Integer.parseInt(fromYear)).setParameter("deptId",Integer.parseInt(deptId)).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(ErpCommitteeDTO dto) {
		String str = " select dbo from ErpCommitteeDBO dbo"
				+ " where dbo.erpAcademicYearDBO.id = :academicId and dbo.erpDepartmentDBO.id = :deptId and dbo.erpCommitteeTypeDBO.committeeType = 'DAC' and dbo.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(dto.getId())) {
			str += " and dbo.id != :id";
		}
		String finalStr =str;
		List<ErpCommitteeDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCommitteeDBO> query = s.createQuery(finalStr, ErpCommitteeDBO.class);
			if(!Utils.isNullOrEmpty(dto.getImportToYear())) {
				query.setParameter("academicId", Integer.parseInt(dto.getImportToYear().getValue()));	
			} else {
				query.setParameter("academicId", Integer.parseInt(dto.getErpAcademicYear().getValue()));
			}
			query.setParameter("deptId",Integer.parseInt(dto.getErpDepartment().getValue()));  
			if(!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());	
			}
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Integer getMembersId() {
		String query ="select erp_committee_role.erp_committee_role_id as membersRoleId from erp_committee_role"
				+ " where erp_committee_role.record_status = 'A' and erp_committee_role.committee_role = 'Member'";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}
}




