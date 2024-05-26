package com.christ.erp.services.transactions.account.fee;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.LockModeType;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandCombinationTypeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.account.settings.AccBillReceiptDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

import reactor.core.publisher.Mono;

@Repository
public class DemandSlipGenerationTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public AccFeeHeadsDBO generateAdditionalFeeDemand(String type) {
			StringBuffer queryString = new StringBuffer();
			queryString.append("from AccFeeHeadsDBO bo "
					+ "left join fetch bo.accFeeHeadsAccountList boAcc"
					+ " where bo.recordStatus='A' and bo.feeHeadsType = :type");
	   		AccFeeHeadsDBO accFeeHeadsDBO =
	   				sessionFactory.withSession(s->{
	   					Mutiny.Query<AccFeeHeadsDBO> query = s.createQuery(queryString.toString(),AccFeeHeadsDBO.class);
						query.setParameter("type", type);
		        return query.getSingleResultOrNull();
	        }).await().indefinitely();
	   		
	        return !Utils.isNullOrEmpty(accFeeHeadsDBO) ? accFeeHeadsDBO : new AccFeeHeadsDBO();
	 }

 	public AccFinancialYearDBO getCurrentFeeFinancialYearIdAndName(){
		String queryString = "from AccFinancialYearDBO bo where bo.recordStatus = 'A' and isCurrentForFee = 1 ";
		return sessionFactory.withSession(s->s.createQuery(queryString, AccFinancialYearDBO.class)
				.getSingleResultOrNull()).await().indefinitely();
	}
	 
	public AccFeeDemandCombinationTypeDBO getAccFeeCombinationType(String type){
		String queryString = "select bo from AccFeeDemandCombinationTypeDBO bo "
						      +" where bo.demandCombinationType = :type and bo.recordStatus = 'A' "; 
		return sessionFactory.withSession(s->s.createQuery(queryString, AccFeeDemandCombinationTypeDBO.class).setParameter("type", type).getSingleResultOrNull()).await().indefinitely();
	}
	 
	public Integer getWorkFlowProcessId(String processCode){
		String queryString = "select bo.id from ErpWorkFlowProcessDBO bo "
						      +" where bo.processCode = :processCode and bo.recordStatus = 'A' "; 
		return sessionFactory.withSession(s->s.createQuery(queryString, Integer.class).setParameter("processCode", processCode).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<AccBatchFeeDBO> generateTuitionFeeDemandNew(Set<Integer> batchIdSet) {
		StringBuffer str = new StringBuffer(" select distinct bo from AccBatchFeeDBO bo "
				  + " left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
			      + " left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
			      + " left join fetch durationBo.acaDurationDBO acaDurDBO"
				  + " left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
			      + " left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
				  + " left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
			      + " where bo.recordStatus = 'A'"
				  + " and bo.acaBatchDBO.id in (:acaBatchIds)");
		List<AccBatchFeeDBO> accBatchFeeDBOList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AccBatchFeeDBO> query = s.createQuery(str.toString(),AccBatchFeeDBO.class);
   					query.setParameter("acaBatchIds", batchIdSet);
	        return query.getResultList();
        }).await().indefinitely();

        return !Utils.isNullOrEmpty(accBatchFeeDBOList) ? accBatchFeeDBOList : new ArrayList<AccBatchFeeDBO>();
	}
	
	public List<AccBatchFeeDBO> generateTuitionFeeDemand(Set<Integer> batchIdSet, int academicYearId) {
		StringBuffer str = new StringBuffer(" select distinct bo from AccBatchFeeDBO bo "
				  + " left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
			      + " left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
			      + " left join fetch durationBo.acaDurationDBO acaDurDBO"
				  + " left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
			      + " left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
				  + " left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
			      + " where bo.recordStatus = 'A' and  durationBo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId"
				  + " and bo.acaBatchDBO.id in (:acaBatchIds)");
		List<AccBatchFeeDBO> accBatchFeeDBOList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AccBatchFeeDBO> query = s.createQuery(str.toString(),AccBatchFeeDBO.class);
   					query.setParameter("academicYearId", academicYearId);
   					query.setParameter("acaBatchIds", batchIdSet);
	        return query.getResultList();
        }).await().indefinitely();

        return !Utils.isNullOrEmpty(accBatchFeeDBOList) ? accBatchFeeDBOList : new ArrayList<AccBatchFeeDBO>();
	}
	
	public Mono<List<AcaBatchDBO>> getBatchNameByYear(int batchYearId){
		String queryString = " from AcaBatchDBO bo where bo.recordStatus = 'A'"
				+ " and bo.erpProgrammeBatchwiseSettingsDBO.erpProgrammeDBO.id = :programId and bo.erpProgrammeBatchwiseSettingsDBO.erpAcademicYearDBO.id = :yearId "
				+ " and bo.inTakeBatchNumber = 1";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AcaBatchDBO.class)
				.setParameter("yearId", batchYearId)
				.getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Integer>> getBatchId(int academicYearId, int campusId){
		String queryString = "select distinct bo.acaSessionDBO.yearNumber from AcaDurationDetailDBO bo where bo.recordStatus = 'A'"
				+ " and bo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId and bo.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id = :campusId order by bo.acaSessionDBO.yearNumber";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, Integer.class)
				.setParameter("academicYearId", academicYearId)
				.setParameter("campusId", campusId)
				.getResultList()).subscribeAsCompletionStage());
 	}
	
	public Mono<List<Integer>> getYearNoByBatch(int batchYearId){
		String queryString = "select distinct acaSessionDBO.yearNumber from AcaDurationDetailDBO bo where bo.recordStatus = 'A'"
				+ " and bo.acaBatchDBO.id = :batchYearId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, Integer.class)
				.setParameter("batchYearId", batchYearId)
				.getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Integer>> getAllTheBatches(int academicYearId, int campusId){
		String queryString = "select distinct bo.acaSessionDBO.yearNumber from AcaDurationDetailDBO bo where bo.recordStatus = 'A'"
				+ " and bo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId and bo.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id = :campusId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, Integer.class)
				.setParameter("academicYearId", academicYearId)
				.setParameter("campusId", campusId)
				.getResultList()).subscribeAsCompletionStage());
 	}
	
	public List<AcaDurationDetailDBO> getAllProgramsByYearNo(int academicYearId, int campusId, int yearNo){
		String queryString = "select bo from AcaDurationDetailDBO bo where bo.recordStatus = 'A'"
				+ " and bo.acaDurationDBO.erpAcademicYearDBO.id = :academicYearId "
				+ " and bo.acaBatchDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id = :campusId"
				+ " and bo.acaSessionDBO.yearNumber = :yearNo and bo.acaSessionDBO.acaSessionGroup.sessionNumber = 1";
		return sessionFactory.withSession(s->s.createQuery(queryString, AcaDurationDetailDBO.class)
				.setParameter("academicYearId", academicYearId)
				.setParameter("campusId", campusId)
				.setParameter("yearNo", yearNo)
				.getResultList()).await().indefinitely();
 	}
	
	public List<StudentDBO> getStudentsBybatchId(Set<Integer> batchIdList, Integer academicYearId, boolean isAll, String regNoFrom, String regNoTo){
		StringBuffer queryString = new StringBuffer("select bo from StudentDBO bo where bo.recordStatus = 'A'"
				+ " and bo.erpStatus.statusCode = 'STUDENT_ONROLL' and bo.acaBatchDBO.id in (:batchIdList)");
				if(!isAll) {
					queryString.append(" and bo.id not in (select d.studentDBO.id from  AccFeeDemandDBO d where erpAcademicYearDBO.id = :academicYearId and d.recordStatus = 'A') ");
				}
				if(!Utils.isNullOrEmpty(regNoFrom) && !Utils.isNullOrEmpty(regNoTo)) {
					queryString.append(" and bo.registerNo >=:regNoFrom and  bo.registerNo <= :regNoTo");
				}
				List<StudentDBO> studentDBOList = sessionFactory.withSession(s->{
				Mutiny.Query<StudentDBO> query = s.createQuery(queryString.toString(),StudentDBO.class);
				if(!isAll) {
					query.setParameter("academicYearId", academicYearId);
				}
				if(!Utils.isNullOrEmpty(regNoFrom) && !Utils.isNullOrEmpty(regNoTo)) {
					query.setParameter("regNoFrom", regNoFrom);
					query.setParameter("regNoTo", regNoTo);
				}
				query.setParameter("batchIdList", batchIdList);
	        return query.getResultList();
        }).await().indefinitely();
		return !Utils.isNullOrEmpty(studentDBOList) ? studentDBOList : new ArrayList<StudentDBO>();
 	}
	public boolean saveDemand(List<Set<AccFeeDemandDBO>> demandList) {
		AccFinancialYearDBO curFeeFinYearDBO = getCurrentFeeFinancialYearIdAndName();
		Integer finId = curFeeFinYearDBO.getId();

		demandList.forEach(list ->{
			list.forEach(demand->{
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				sessionFactory.withTransaction((session, transaction) -> 
				session.createQuery("from AccBillReceiptDBO bo where (bo.typeCode = 'DEMAND_SLIP' or bo.typeCode = 'SCHOLARSHIP') "
						+ " and bo.accFinancialYearDBO.id = :finId and bo.recordStatus = 'A'", AccBillReceiptDBO.class)
				.setParameter("finId", finId)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList()
				.invoke(receiptList->{
					AtomicInteger receiptNoForDemand = new AtomicInteger();
					AtomicInteger receiptNoForAdjustment = new AtomicInteger();
					receiptList.forEach(receipt->{
							if(receipt.getTypeCode().equalsIgnoreCase("DEMAND_SLIP")) {
								receiptNoForDemand.set(receipt.getBillReceiptCurrentNo());
							}
							if(receipt.getTypeCode().equalsIgnoreCase("SCHOLARSHIP")) {
								receiptNoForAdjustment.set(receipt.getBillReceiptCurrentNo());
							}
						});
						demand.setDemandSlipNo(receiptNoForDemand.get());
						receiptNoForDemand.getAndIncrement();
						if(!Utils.isNullOrEmpty(demand.getAccFeeDemandAdjustmentDBOSet())) {
							demand.getAccFeeDemandAdjustmentDBOSet().forEach(a->{
								a.setAdjustmentNo(receiptNoForAdjustment.get());
								receiptNoForAdjustment.getAndIncrement();
							});
						}
						receiptList.forEach(receipt->{
							if(receipt.getTypeCode().equalsIgnoreCase("DEMAND_SLIP")) {
								receipt.setBillReceiptCurrentNo(receiptNoForDemand.getAndIncrement());
							}
							if(receipt.getTypeCode().equalsIgnoreCase("SCHOLARSHIP")) {
								receipt.setBillReceiptCurrentNo(receiptNoForAdjustment.getAndIncrement());
							}
						});
					})
				.flatMap(student -> session.persist(demand).chain(session::flush))
				.invoke(d->{			
				if(!Utils.isNullOrEmpty(demand.getAccFeeDemandAdjustmentDBOSet())) {
					demand.getAccFeeDemandAdjustmentDBOSet().forEach(adj->{
						if(!Utils.isNullOrEmpty(adj.getId())) {
							if(!Utils.isNullOrEmpty(adj.getErpWorkFlowProcessDBO().getId())){
								erpWorkFlowProcessStatusLogDBO.setEntryId(adj.getId());
								erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(adj.getErpWorkFlowProcessDBO());
								erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
								erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(adj.getCreatedUsersId());
								erpWorkFlowProcessStatusLogDBO.setModifiedUsersId(adj.getCreatedUsersId());
							}
						}
					});
				}
			})
			.flatMap(s->!Utils.isNullOrEmpty(erpWorkFlowProcessStatusLogDBO)? session.persist(erpWorkFlowProcessStatusLogDBO): null)).await().indefinitely();
			});
		});
		return true;
	}
	
	public List<AccFeeDemandDBO> getDemandGeneratedForBatch(Set<Integer> batchIdSet, Integer academicYearId){
		StringBuffer queryString = new StringBuffer("from AccFeeDemandDBO d where erpAcademicYearDBO.id = :academicYearId and d.studentDBO.acaBatchDBO.id in(:batchIdSet) and d.recordStatus = 'A'");
		List<AccFeeDemandDBO> demandDBOList =
				sessionFactory.withSession(s->{
				Mutiny.Query<AccFeeDemandDBO> query = s.createQuery(queryString.toString(),AccFeeDemandDBO.class);
				query.setParameter("batchIdSet", batchIdSet);
				query.setParameter("academicYearId", academicYearId);
	        return query.getResultList();
        }).await().indefinitely();
		return !Utils.isNullOrEmpty(demandDBOList) ? demandDBOList : new ArrayList<AccFeeDemandDBO>();
 	}

}
