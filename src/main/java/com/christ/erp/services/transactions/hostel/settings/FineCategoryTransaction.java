package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import reactor.core.publisher.Mono;

@Repository
public class FineCategoryTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<HostelFineCategoryDBO> getGridData() {
		String str = "select dbo from HostelFineCategoryDBO dbo where dbo.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelFineCategoryDBO.class).getResultList()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelFineCategoryDBO.class, id).invoke(dbo -> {
			dbo.setModifiedUsersId(userId);
			dbo.setRecordStatus('D');
		})).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public HostelFineCategoryDBO edit(int id) {
		String str =" from HostelFineCategoryDBO dbo "
				+" where dbo.recordStatus ='A' and dbo.id =:id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelFineCategoryDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public boolean duplicateCheck(HostelFineCategoryDTO dto) {
		String str = "from HostelFineCategoryDBO dbo"
				 +" where dbo.hostelDBO.id =:hostelId and dbo.recordStatus='A' ";
		if(dto.getIsAbsentFine()) {
		    str+= " and dbo.isAbsentFine = 1"; 
		} else if(dto.getIsDisciplinaryFine()) {
			str+= " and dbo.isDisciplinaryFine = 1";
		}
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id != :id";
		}
		String finalStr = str;
		List<HostelFineCategoryDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelFineCategoryDBO> query = s.createQuery(finalStr,HostelFineCategoryDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("hostelId", Integer.parseInt(dto.getHostelDTO().getValue()));
			return query.getResultList();
		}).await().indefinitely();  
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public void update(HostelFineCategoryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelFineCategoryDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(HostelFineCategoryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public boolean isDuplicate(HostelFineCategoryDTO dto) {
		String str = "from HostelFineCategoryDBO dbo where dbo.fineCategory =:fineCategory and dbo.hostelDBO.id =:hostelId and dbo.recordStatus='A' ";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and id != :id";
		}
		String finalStr = str;
		List<HostelFineCategoryDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelFineCategoryDBO> query = s.createQuery(finalStr,HostelFineCategoryDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("fineCategory", dto.getFineCategory());
			query.setParameter("hostelId", Integer.parseInt(dto.getHostelDTO().getValue()));
			return query.getResultList();
		}).await().indefinitely();  
		return Utils.isNullOrEmpty(list) ? false : true;
	}
}
