package com.christ.erp.services.transactions.admission.settings;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.admission.settings.ErpAdmissionTemplateDTO;

import reactor.core.publisher.Mono;

@Repository
public class AdmissionTemplateTransaction {
	
	@Autowired
	SessionFactory sessionFactory;

	public Mono<List<Tuple>> getGridList() {
		
		String query = "select erp_template_group.erp_template_group_id as grp_id, erp_template_group.template_group_name grp_name, "
				+ "erp_template_group.template_group_code as grp_code , erp_template.erp_template_id as template_id, erp_template.template_id as templateId, "
				+ "erp_template.template_name as template_name, erp_template.template_code as template_code, "
				+ "erp_template.template_type as type, erp_campus.campus_name as campus, erp_template.template_description as description , "
				+ "erp_programme.programme_name program_name,erp_campus.erp_campus_id as campus_id,erp_programme.erp_programme_id as prog_id  From erp_template "
				+ "inner join erp_template_group ON erp_template_group.erp_template_group_id = erp_template.erp_template_group_id "
				+ "inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = erp_template.erp_campus_programme_mapping_id "
				+ "inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id "
				+ "inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id "
				+ "where erp_template.record_status='A' and erp_template_group.record_status='A' and erp_campus_programme_mapping.record_status='A' "
				+ "and erp_campus.record_status='A' and erp_programme.record_status='A' and erp_template_group.template_purpose='Admission'   "
				+ "order by template_group_name,template_name,template_type,campus_name,programme_name";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public void update(ErpTemplateGroupDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(ErpTemplateGroupDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }

    public void save(ErpTemplateGroupDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }
	
	public Mono<ErpTemplateGroupDBO> edit(int id) {
		String query = "select dbo from ErpTemplateGroupDBO dbo"
				+ " inner  join fetch dbo.erpTemplateDBOSet tempDBO "
				+ " where dbo.recordStatus='A' and tempDBO.id=:id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query,ErpTemplateGroupDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
    }
//	public ErpTemplateGroupDBO editObj(int id) {
//		String query = "select distinct grpDBO from ErpTemplateDBO dbo"
//				+ " left join fetch dbo.erpTemplateGroupDBO grpDBO "
//				+ " where dbo.recordStatus='A' and grpDBO.id=:id";
//        return sessionFactory.withSession(s->s.createQuery(query,ErpTemplateGroupDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
//    }
	
	public ErpTemplateGroupDBO editObj(int id) {
		String query = "select dbo from ErpTemplateGroupDBO dbo"
				+ " inner  join fetch dbo.erpTemplateDBOSet tempDBO "
				+ " where dbo.recordStatus='A' and tempDBO.id=:id";
        return sessionFactory.withSession(s->s.createQuery(query,ErpTemplateGroupDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }

	 public Mono<Boolean> delete(int id, Integer userId) {
		 sessionFactory.withTransaction((session, tx) -> session.find(ErpTemplateDBO.class, id).invoke(dbo -> {
	            dbo.setModifiedUsersId(userId);
	            dbo.setRecordStatus('D');
	        })).await().indefinitely();
//	        sessionFactory.withTransaction((session, tx) -> session.find(ErpTemplateGroupDBO.class, id)
//	        		.chain(dbo -> session.fetch(dbo.getErpTemplateDBOSet())
//	        		.invoke(subSet -> {
//	        			subSet.forEach(subDbo -> {
//	        				subDbo.setRecordStatus('D');
//	        				subDbo.setModifiedUsersId(userId);
//	        			});
//	        			dbo.setRecordStatus('D');
//	        			dbo.setModifiedUsersId(userId);
//	        		})		
//	        		)).await().indefinitely();
	        return Mono.just(Boolean.TRUE);
	    }
	 public List<ErpCampusProgrammeMappingDBO> getErpCampusProgrammingId() {
	    	String query = " select  dbo FROM ErpCampusProgrammeMappingDBO dbo where  dbo.recordStatus  = 'A' and dbo.erpCampusDBO.id != null ";
	        return  sessionFactory.withSession(s->s.createQuery(query,ErpCampusProgrammeMappingDBO.class).getResultList()).await().indefinitely();
	    }

	public List<ErpTemplateDBO> duplicateCheck(ErpAdmissionTemplateDTO erpTemplateGroupDTO) {
		List<Integer> programList = new ArrayList<Integer>() ;
		erpTemplateGroupDTO.getProgramSelectedForTemplate().forEach(s -> programList.add(Integer.parseInt(s.getValue())));
		String query = "select dbo from ErpTemplateDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.templateType=:templateFor and dbo.erpTemplateGroupDBO.templateGroupName=:groupTemplateName and dbo.erpTemplateGroupDBO.templateGroupCode=:groupTemplateCode and dbo.erpCampusProgrammeMappingDBO.id in (:programList)";
		return sessionFactory.withSession(s -> s.createQuery(query,ErpTemplateDBO.class).setParameter("templateFor", erpTemplateGroupDTO.getTemplateFor().getValue()).setParameter("groupTemplateName", erpTemplateGroupDTO.getTemplateGroupName()).setParameter("groupTemplateCode", erpTemplateGroupDTO.getTemplateGroupCode())
				.setParameter("programList", programList).getResultList()).await().indefinitely();
		
	}
	
	
	public ErpTemplateGroupDBO getGroupTemplateObj(ErpAdmissionTemplateDTO erpTemplateGroupDTO) {
		String query = "select  dbo from ErpTemplateGroupDBO dbo left join fetch dbo.erpTemplateDBOSet dbos  where dbo.recordStatus='A' and dbo.templateGroupName=:groupTemplateName  and dbo.templateGroupCode=:templateCode";
		return sessionFactory.withSession(s -> s.createQuery(query,ErpTemplateGroupDBO.class).setParameter("templateCode", erpTemplateGroupDTO.getTemplateGroupCode()).setParameter("groupTemplateName", erpTemplateGroupDTO.getTemplateGroupName()).getSingleResultOrNull()).await().indefinitely();
    }

//	public List<ErpTemplateDBO> dataCheck(ErpAdmissionTemplateDTO erpTemplateGroupDTO) {
//		List<Integer> programList = new ArrayList<Integer>() ;
//		erpTemplateGroupDTO.getProgramSelectedForTemplate().forEach(s -> programList.add(Integer.parseInt(s.getValue())));
//		String query = "select dbo from ErpTemplateDBO dbo"
//				+ " where dbo.recordStatus='A' and dbo.templateType=:templateFor and dbo.erpTemplateGroupDBO.templateGroupName=:groupTemplateName and dbo.erpCampusProgrammeMappingDBO.id in (:programList)";
//		return sessionFactory.withSession(s -> s.createQuery(query,ErpTemplateDBO.class).setParameter("templateFor", erpTemplateGroupDTO.getTemplateFor().getValue()).setParameter("groupTemplateName", erpTemplateGroupDTO.getTemplateGroupName())
//				.setParameter("programList", programList).getResultList()).await().indefinitely();
//		
//	}
//	}

}
