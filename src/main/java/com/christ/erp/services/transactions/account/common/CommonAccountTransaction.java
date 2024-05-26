package com.christ.erp.services.transactions.account.common;

import java.util.List;

import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;

import reactor.core.publisher.Mono;

@Repository
public class CommonAccountTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<ErpAcademicYearDBO>> getBatchYear(){
		String queryString = " select distinct bo.erpAcademicYearDBO from ErpProgrammeBatchwiseSettingsDBO bo where bo.recordStatus = 'A'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, ErpAcademicYearDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<ErpProgrammeDBO>> getProgrammesByBatchYear(int batchYearId){
		String queryString = "select bo.erpProgrammeDBO from ErpProgrammeBatchwiseSettingsDBO bo where bo.recordStatus = 'A' and bo.erpAcademicYearDBO.id = :batchYearId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, ErpProgrammeDBO.class)
				.setParameter("batchYearId", batchYearId)
				.getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<AcaDurationDetailDBO>> getAcademicYearByBatch(int acaBatchId){
		String queryString = " from AcaDurationDetailDBO bo where bo.recordStatus = 'A'"
				+ " and bo.acaBatchDBO.id = :batchId ";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AcaDurationDetailDBO.class)
				.setParameter("acaBatchId", acaBatchId)
				.getResultList()).subscribeAsCompletionStage());
	}
	public Mono<List<AcaBatchDBO>> getBatchNameByProgramAndYear(int programId, int batchYearId){
		String queryString = " from AcaBatchDBO bo where bo.recordStatus = 'A'"
				+ " and bo.erpProgrammeBatchwiseSettingsDBO.erpProgrammeDBO.id = :programId and bo.erpProgrammeBatchwiseSettingsDBO.erpAcademicYearDBO.id = :yearId "
				+ " and bo.inTakeBatchNumber = 1";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AcaBatchDBO.class)
				.setParameter("yearId", batchYearId)
				.setParameter("programId", programId)
				.getResultList()).subscribeAsCompletionStage());
	}

}
