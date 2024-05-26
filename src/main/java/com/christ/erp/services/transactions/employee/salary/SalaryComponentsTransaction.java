package com.christ.erp.services.transactions.employee.salary;

import java.util.List;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;

import reactor.core.publisher.Mono;

@Repository
public class SalaryComponentsTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<EmpPayScaleComponentsDBO>> getGridData() {
        String str = "select bo from EmpPayScaleComponentsDBO bo where bo.recordStatus = 'A' order by bo.salaryComponentDisplayOrder";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, EmpPayScaleComponentsDBO.class).getResultList()).subscribeAsCompletionStage());
    }

    public void update(EmpPayScaleComponentsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(EmpPayScaleComponentsDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }

    public void save(EmpPayScaleComponentsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }

    public EmpPayScaleComponentsDBO getEmpPayScaleComponentDetail(int id) {
        return sessionFactory.withSession(s->s.find(EmpPayScaleComponentsDBO.class, id)).await().indefinitely();
    }

    public boolean duplicateCheck(EmpPayScaleComponentsDTO dto) {
    	String str1 = "from EmpPayScaleComponentsDBO where recordStatus='A' and ";
    	if(dto.getIsComponentBasic()) {
    		str1 += " ( ";
    	}
    	str1 += " ((payScaleType=:payScaleType  and replace(salaryComponentName, ' ','') =:salaryComponentName) or (payScaleType=:payScaleType and salaryComponentShortName =: salaryComponentShortName))";
    	if(dto.getIsComponentBasic()) {
    		str1 += " or (payScaleType=:payScaleType and isComponentBasic=true))";
    	}
    	if (!Utils.isNullOrEmpty(dto.getId())) {
    		str1 += " and id != :id";
    	}
    	String finalStr = str1;
    	List<EmpPayScaleComponentsDBO> list = sessionFactory.withSession(s-> {
    		Mutiny.Query<EmpPayScaleComponentsDBO> query = s.createQuery(finalStr,EmpPayScaleComponentsDBO.class);
    		if (!Utils.isNullOrEmpty(dto.getId())) {
    			query.setParameter("id", dto.getId());
    		}
    		query.setParameter("payScaleType", dto.getPayScaleType().getValue());
    		query.setParameter("salaryComponentName", dto.getSalaryComponentName().replace(" ","").toLowerCase().trim());
    		query.setParameter("salaryComponentShortName", dto.getSalaryComponentShortName());
    		return query.getResultList();
    	}).await().indefinitely();
    	return Utils.isNullOrEmpty(list) ? false : true;
    }
	
    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(EmpPayScaleComponentsDBO.class, id).invoke(dbo -> {
            dbo.setModifiedUsersId(userId);
            dbo.setRecordStatus('D');
        })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

	public Boolean isDuplicateDisplayOrder(String payScaleType, String id, String displayOrder) {
		String str = "from EmpPayScaleComponentsDBO where recordStatus='A' and (payScaleType=:payScaleType  and salaryComponentDisplayOrder = :displayOrder) ";
		if (!Utils.isNullOrEmpty(id)) {
			str += " and id != :id";
		}
		String finalStr = str;
		List<EmpPayScaleComponentsDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<EmpPayScaleComponentsDBO> query = s.createQuery(finalStr,EmpPayScaleComponentsDBO.class);
			if (!Utils.isNullOrEmpty(id)) {
				query.setParameter("id", Integer.parseInt(id));
			}
			query.setParameter("payScaleType", payScaleType);
			query.setParameter("displayOrder", Integer.parseInt(displayOrder));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;	
	}
}