package com.christ.erp.services.transactions.admin;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dto.common.ErpWorkFlowProcessNotificationsDTO;
import reactor.core.publisher.Mono;

@Repository
public class ErpWorkFlowProcessNotificationsTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public boolean duplicateCheck(ErpWorkFlowProcessNotificationsDTO dto) {
		String str = "from ErpWorkFlowProcessNotificationsDBO ex where ex.recordStatus='A' and ex.notificationCode = :notificationCode  ";
		if (!Utils.isNullOrEmpty(dto.getId())) {
			str += " and ex.id != :id ";
		}
		str += " order by ex.erpWorkFlowProcessDBO.processCode, ex.notificationCode";
		String finalStr = str;
		List<ErpWorkFlowProcessNotificationsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpWorkFlowProcessNotificationsDBO> query = s.createQuery(finalStr, ErpWorkFlowProcessNotificationsDBO.class);
			if (!Utils.isNullOrEmpty(dto.getId())) {
				query.setParameter("id", dto.getId());
			}
			query.setParameter("notificationCode", dto.getNotificationCode());
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public void save(ErpWorkFlowProcessNotificationsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
		}

	public void update(ErpWorkFlowProcessNotificationsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpWorkFlowProcessNotificationsDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
		}

	public Mono<List<ErpWorkFlowProcessNotificationsDBO>> getGridData() {
		String str = " from ErpWorkFlowProcessNotificationsDBO e where e.recordStatus = 'A' ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpWorkFlowProcessNotificationsDBO.class).getResultList()).subscribeAsCompletionStage());
	
	}

	public Mono<ErpWorkFlowProcessNotificationsDBO> edit(int id) {
		String str = "select e from ErpWorkFlowProcessNotificationsDBO e where e.recordStatus = 'A' and e.id= :id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpWorkFlowProcessNotificationsDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
	}
}
