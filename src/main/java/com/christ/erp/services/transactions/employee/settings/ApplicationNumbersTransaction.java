package com.christ.erp.services.transactions.employee.settings;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnNumberGenerationDBO;
import com.christ.erp.services.dto.employee.settings.EmpApplnNumberGenerationDTO;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationNumbersTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<EmpApplnNumberGenerationDBO>> getGridData() {
	    String str = "Select sc from EmpApplnNumberGenerationDBO sc where sc.recordStatus ='A' order by calendarYear ASC";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, EmpApplnNumberGenerationDBO.class).getResultList()).subscribeAsCompletionStage());
    }
    
    public void update(EmpApplnNumberGenerationDBO dbo) {
	    sessionFactory.withTransaction((session, tx) -> session.find(EmpApplnNumberGenerationDBO.class, dbo.getEmpApplnNumberGenerationId()).call(()
	    -> session.merge(dbo))).await().indefinitely();	
    }
    
    public void save(EmpApplnNumberGenerationDBO dbo) {
	    sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }

    public Mono<EmpApplnNumberGenerationDBO> edit(int id) {
        return Mono.fromFuture(sessionFactory.withSession(s -> s.find(EmpApplnNumberGenerationDBO.class, id)).subscribeAsCompletionStage());
    }

    public Boolean CalendarYearDuplicateCheck (EmpApplnNumberGenerationDTO dto) {
	    String str1= "from EmpApplnNumberGenerationDBO where recordStatus='A' and calendarYear=:calendarYear";
	    if(!Utils.isNullOrEmpty(dto.getEmpApplnNumberGenerationId())){
	    	str1 += " and empApplnNumberGenerationId != :empApplnNumberGenerationId";	
        }
	    String finalStr =str1;
	    List<EmpApplnNumberGenerationDBO> list = sessionFactory.withSession(s -> {
	        Mutiny.Query<EmpApplnNumberGenerationDBO> query = s.createQuery(finalStr, EmpApplnNumberGenerationDBO.class);
	        if(!Utils.isNullOrEmpty(dto.getEmpApplnNumberGenerationId()))	{
	        	query.setParameter("empApplnNumberGenerationId", dto.getEmpApplnNumberGenerationId());	
	        }
	        query.setParameter("calendarYear", dto.getCalendarYear());  
		    return query.getResultList();
	    }).await().indefinitely();
	    return Utils.isNullOrEmpty(list) ? false : true;
    }
    
    public Boolean isApplicationRangeExists(EmpApplnNumberGenerationDTO dto) {
		String str= "from EmpApplnNumberGenerationDBO where recordStatus='A' and "
				+ "((applnNumberFrom=: applnNumberFrom and applnNumberTo=:applnNumberTo) OR "
				+ " (:applnNumberFrom between applnNumberFrom and applnNumberTo) OR "
				+ " (:applnNumberTo between applnNumberFrom and applnNumberTo) OR " 
				+ " (:applnNumberFrom <= applnNumberFrom and :applnNumberTo >= applnNumberTo)) ";
		if(!Utils.isNullOrEmpty(dto.getEmpApplnNumberGenerationId())){
	    	str += " and empApplnNumberGenerationId != :empApplnNumberGenerationId";	
		}
		String finalStr =str;
	    List<EmpApplnNumberGenerationDBO> list = sessionFactory.withSession(s -> {
	        Mutiny.Query<EmpApplnNumberGenerationDBO> query = s.createQuery(finalStr, EmpApplnNumberGenerationDBO.class);
	        if(!Utils.isNullOrEmpty(dto.getEmpApplnNumberGenerationId()))	{
	        	query.setParameter("empApplnNumberGenerationId", dto.getEmpApplnNumberGenerationId());	
	        }
	        query.setParameter("applnNumberFrom", dto.getApplnNumberFrom());  
	        query.setParameter("applnNumberTo", dto.getApplnNumberTo()); 
		    return query.getResultList();
	    }).await().indefinitely();
	    return Utils.isNullOrEmpty(list) ? false : true;
	}  
     
   public EmpApplnNumberGenerationDBO isRangeExistDuplicate(EmpApplnNumberGenerationDTO dto) {
 	    String str= "from EmpApplnNumberGenerationDBO where isCurrentRange= true and recordStatus ='A' and empApplnNumberGenerationId != :empApplnNumberGenerationId";
	    return sessionFactory.withSession(s -> 
	  		s.createQuery(str,EmpApplnNumberGenerationDBO.class).setParameter("empApplnNumberGenerationId", dto.getEmpApplnNumberGenerationId()).getSingleResultOrNull())
 		    .await().indefinitely();
    }
    
    public Mono<Boolean> delete(int id, Integer userId) {
	    sessionFactory.withTransaction((session, tx) -> session.find(EmpApplnNumberGenerationDBO.class, id).invoke(dbo-> {
	    	dbo.setModifiedUsersId(userId);
	    	dbo.setRecordStatus('D');
	    })).await().indefinitely();
	    return Mono.just(Boolean.TRUE) ;
    }
}