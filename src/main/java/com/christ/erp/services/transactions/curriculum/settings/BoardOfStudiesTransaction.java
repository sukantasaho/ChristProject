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
public class BoardOfStudiesTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	@Autowired
    private CommonApiTransaction  commonApiTransaction ;
	
	public Mono<List<Tuple>> getGridData(String yearId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAcademicYearNew(); 
		String queryString = " select erp_committee_id as erp_committee_id , erp_academic_year.academic_year as academic_year,erp_committee.erp_department_id as deptId,erp_department.department_name as department_name ,erp_committee.programme_course_structure_entry_last_date as lastDate  from erp_committee"
				+ " inner join erp_academic_year on erp_academic_year.erp_academic_year_id = erp_committee.erp_academic_year_id"
				+ " left join erp_department on erp_committee.erp_department_id = erp_department.erp_department_id"
				+ " inner join erp_committee_type on erp_committee.erp_committee_type_id = erp_committee_type.erp_committee_type_id"
				+ " where erp_committee.record_status = 'A' and erp_committee_type.committee_type = 'BoS' and erp_academic_year.erp_academic_year_id = :yearId";
		String finalStr = queryString;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
		if(!Utils.isNullOrEmpty(yearId)) {
			query.setParameter("yearId", yearId);
		} else {
			query.setParameter("yearId",currYear.getId());
		}
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public  ErpCommitteeDBO edit(int id,String deptId) {
		String query =" select  db from ErpCommitteeDBO db"
				    + " inner join fetch db.erpCommitteeMembersDBOSet ecm ";
		if(!Utils.isNullOrEmpty(deptId)) {
			query +=" left join fetch db.erpCommitteeProgrammeDBOSet ecp"
				  + " left join fetch db.erpCommitteeProgrammeCourseReviewDBOSet ecpc"
				  + " left join fetch ecpc.erpCommitteeProgrammeCourseReviewDetailsDBOSet ecpcr";
		}
		query += " where db.id = :id and db.recordStatus = 'A' and ecm.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(deptId)) {
			query +=" and ecp.recordStatus = 'A' and ecpc.recordStatus = 'A' and ecpcr.recordStatus = 'A'";
		}
		String finalquery = query;
		return  sessionFactory.withSession(s->s.createQuery(finalquery,ErpCommitteeDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}
    
	public void update(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpCommitteeDBO.class, dbo.getId()).call(() -> session.mergeAll(dbo))).await().indefinitely();
	}

	public void save(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
	}
	
	public void merge(ErpCommitteeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}
	
	public Integer getProgrammeMappingId (String departId, String programmeId) {
		String query = " select erp_programme_department_mapping_id from erp_programme_department_mapping"
				+ " where erp_programme_id = :programmeId and erp_department_id = :departId and record_status = 'A'";
		return sessionFactory.withSession(s->s.createNativeQuery(query, Integer.class).setParameter("programmeId",Integer.parseInt(programmeId)).setParameter("departId",Integer.parseInt(departId)).getSingleResultOrNull()).await().indefinitely();        
	}

	public List<Tuple> externalCategoryMembers(int id) {
		String query ="select distinct erp_committee_members.erp_committee_members_id as membersid , erp_committee_members.externals_id as eid ,externals.external_name as ename ,erp_externals_category.erp_externals_category_id as cid , erp_externals_category.externals_category_name as cname "
				+ " from externals "
				+ " inner join erp_externals_category ON erp_externals_category.erp_externals_category_id = externals.erp_externals_category_id"
				+ " inner join erp_committee_members on erp_committee_members.externals_id = externals.externals_id"
				+ " where erp_committee_members.record_status = 'A' and externals.record_status = 'A' and erp_committee_members.erp_committee_id = :id "
				+ " and erp_committee_members.externals_id = externals.externals_id order by cid ";
		return  sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("id", id).getResultList()).await().indefinitely();
	}
	
	public  List<ErpCommitteeDBO> duplicateCheck(ErpCommitteeDTO dto) {
		String query =" select  db from ErpCommitteeDBO db "
				+ " where db.erpAcademicYearDBO.id = :aId and db.erpCommitteeTypeDBO.id = :cid and db.erpDepartmentDBO.id = :deptId   and db.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(dto.getId())) {
			query += " and db.id != :id";
		}
		String finalquery = query;
		List<ErpCommitteeDBO> value = sessionFactory.withSession( s-> { Mutiny.Query<ErpCommitteeDBO> str = s.createQuery(finalquery,ErpCommitteeDBO.class);
		str.setParameter("cid", Integer.parseInt(dto.getErpCommitteeType().getValue()));
		str.setParameter("deptId",Integer.parseInt(dto.getErpDepartment().getValue()));
		if(!Utils.isNullOrEmpty(dto.getImportToYear())) {
			str.setParameter("aId",Integer.parseInt( dto.getImportToYear().getValue()));
		} else {
			str.setParameter("aId",Integer.parseInt( dto.getErpAcademicYear().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getId())) {
			str.setParameter("id",dto.getId());
		}	
		return str.getResultList();
		}).await().indefinitely();
		return value;
	}

	public ErpCommitteeDBO checkCommonUniversity(ErpCommitteeDTO erpCommitteeDTO) {
		String query =" select db from  ErpCommitteeDBO db where  db.erpAcademicYearDBO.id = :academicId and db.recordStatus = 'A' and db.erpCommitteeTypeDBO.id = :cid and db.erpDepartmentDBO.id is null";
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getId())) {
			query += " and db.id != :id";
		}
		String finalquery = query;
		ErpCommitteeDBO value = sessionFactory.withSession( s->  { Mutiny.Query<ErpCommitteeDBO> str = s.createQuery(finalquery,ErpCommitteeDBO.class);
				str.setParameter("cid", Integer.parseInt(erpCommitteeDTO.getErpCommitteeType().getValue()));
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getImportToYear())) {
			str.setParameter("academicId",Integer.parseInt(erpCommitteeDTO.getImportToYear().getValue()));
		} else {
			str.setParameter("academicId",Integer.parseInt(erpCommitteeDTO.getErpAcademicYear().getValue()));
		}
		if(!Utils.isNullOrEmpty(erpCommitteeDTO.getId())) {
			str.setParameter("id",erpCommitteeDTO.getId());
		}
		return str.getSingleResultOrNull();
		}).await().indefinitely();	
		return value;
	}
	
	public Integer getBosId() {
		String query ="select erp_committee_type.erp_committee_type_id as bosId from erp_committee_type"
				+ " where erp_committee_type.record_status = 'A' and erp_committee_type.committee_type = 'BoS'";
		return  sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}
	
	public Integer getMembersId() {
		String query ="select erp_committee_role.erp_committee_role_id as membersRoleId from erp_committee_role"
				+ " where erp_committee_role.record_status = 'A' and erp_committee_role.committee_role = 'Member'";
		return  sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).getSingleResultOrNull()).await().indefinitely();
	}

	public ErpCommitteeDBO getPerviousYearData(ErpCommitteeDTO erpCommitteeDTO) {
		String query =" select  db from ErpCommitteeDBO db"
			    + " inner join fetch db.erpCommitteeMembersDBOSet ecm ";
	if(!Utils.isNullOrEmpty(erpCommitteeDTO.getErpDepartment())) {
		query +=" left join fetch db.erpCommitteeProgrammeDBOSet ecp"
			  + " left join fetch db.erpCommitteeProgrammeCourseReviewDBOSet ecpc"
			  + " left join fetch ecpc.erpCommitteeProgrammeCourseReviewDetailsDBOSet ecpcr";
	}
	query += " where db.erpAcademicYearDBO.id = :aid  and  db.recordStatus = 'A' and ecm.recordStatus = 'A'";
	if(!Utils.isNullOrEmpty(erpCommitteeDTO.getErpDepartment())) {
		query += " and db.erpDepartmentDBO.id = :deptId" ;
	} else {
		query += " and db.erpDepartmentDBO.id  is null";
	}
	if(!Utils.isNullOrEmpty(erpCommitteeDTO.getErpDepartment())) {
		query +=" and ecp.recordStatus = 'A' and ecpc.recordStatus = 'A' and ecpcr.recordStatus = 'A'";
	}
	String finalquery = query;
	ErpCommitteeDBO value = sessionFactory.withSession( s->  { Mutiny.Query<ErpCommitteeDBO> str = s.createQuery(finalquery,ErpCommitteeDBO.class);
	str.setParameter("aid",Integer.parseInt(erpCommitteeDTO.getErpAcademicYear().getValue()));
	if(!Utils.isNullOrEmpty(erpCommitteeDTO.getErpDepartment())) {
		str.setParameter("deptId",Integer.parseInt( erpCommitteeDTO.getErpDepartment().getValue()));
	}
	return str.getSingleResultOrNull();
	}).await().indefinitely();	
	return value;
	}
	
}