package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

@Repository
public class UploadFinalResultTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public void update(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}
	
	public List<ErpCampusProgrammeMappingDBO> checkCampusProgrammeValid1() {
    	String query = "  select dbo from ErpCampusProgrammeMappingDBO dbo  where dbo.recordStatus = 'A' and  dbo.erpCampusDBO.id != null";
        return  sessionFactory.withSession(s->s.createQuery(query,ErpCampusProgrammeMappingDBO.class).getResultList()).await().indefinitely();
    }
	
	public List<String> getCampusCode() {
    	String query = " select short_name from erp_campus   where  record_status = 'A' and short_name is not null ";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,String.class).getResultList()).await().indefinitely();
    }
	
	public List<String> getProgrammeCode() {
    	String query = " select Trim( REPLACE(programme_code, \" \", \"\"))  from erp_programme where record_status = 'A' and programme_code is not null";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,String.class).getResultList()).await().indefinitely();
    }
	
	public Integer getyear(int yearId) {
    	String query = "select  academic_year  FROM erp_academic_year where erp_academic_year.erp_academic_year_id = :yearId ";
        return  sessionFactory.withSession(s->s.createNativeQuery(query,Integer.class).setParameter("yearId", yearId).getSingleResultOrNull()).await().indefinitely();
    }
	
	public List<StudentApplnEntriesDBO> getApplicantsDetails(List<Integer> applicationNos) {
    	String query = " select dbo from StudentApplnEntriesDBO dbo where dbo.recordStatus = 'A'  and dbo.applicationNo in( :applicationNos ) ";
        return  sessionFactory.withSession(s->s.createQuery(query,StudentApplnEntriesDBO.class).setParameter("applicationNos", applicationNos).getResultList()).await().indefinitely();
    }
	
	public List<ErpCampusProgrammeMappingDBO> getErpCampusProgrammingId() {
    	String query = " select  dbo FROM ErpCampusProgrammeMappingDBO dbo where  dbo.recordStatus  = 'A' and dbo.erpCampusDBO.id != null ";
        return  sessionFactory.withSession(s->s.createQuery(query,ErpCampusProgrammeMappingDBO.class).getResultList()).await().indefinitely();
    }
	
}
