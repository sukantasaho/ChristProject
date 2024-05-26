package com.christ.erp.services.transactions.hostel.application;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Repository

public class PublishSelectionTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public List<HostelApplicationDBO> getGridData(String yearId, String hostelId, Boolean isPublished) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAdmissionYear(); 
		String str =  " select distinct dbo from HostelApplicationDBO dbo"
				+" inner join fetch dbo.hostelApplicationRoomTypePreferenceDBO dbos "
				+" left join HostelAdmissionsDBO hdbo on hdbo.hostelApplicationDBO.id = dbo.id";
		if(isPublished.equals(true)) {
			str+= " where (dbo.hostelApplicationCurrentProcessStatus.processCode = 'HOSTEL_APPLICATION_SELECTED' or dbo.hostelApplicationCurrentProcessStatus.processCode = 'HOSTEL_APPLICATION_NOT_SELECTED')"
				 +" and dbo.erpAcademicYearDBO.id =:yearId and dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A' and dbos.recordStatus ='A'";
		} else {
			str+= " where (dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED' or dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_SELECTED_UPLOADED')"
			     +" and dbo.erpAcademicYearDBO.id =:yearId and dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A' and dbos.recordStatus ='A' order by dbo.applicationNo ASC ";	
		}
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				query.setParameter("yearId", Integer.parseInt(yearId));
			} else {
				query.setParameter("yearId",currYear.getId());
			}
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void publishSelectionUpdate(List<Object> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();	
	}

	public List<HostelApplicationDBO> getData(List<Integer> applicationIds) {
		String str = " select dbo from HostelApplicationDBO dbo where dbo.recordStatus ='A'and dbo.id IN(:applicationIds) ";
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			query.setParameter("applicationIds", applicationIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public Mono<List<ErpWorkFlowProcessDBO>> getHostelStatus() {
		List<String> statusCode = new ArrayList<String>();
		statusCode.add("HOSTEL_APPLICATION_SELECTED");
		statusCode.add("HOSTEL_APPLICATION_NOT_SELECTED");
		String queryString = "select dbo from ErpWorkFlowProcessDBO dbo where dbo.recordStatus = 'A' and dbo.processCode in (:statusCode)";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpWorkFlowProcessDBO.class)
				.setParameter("statusCode", statusCode)
				.getResultList()).subscribeAsCompletionStage());
	}
}
