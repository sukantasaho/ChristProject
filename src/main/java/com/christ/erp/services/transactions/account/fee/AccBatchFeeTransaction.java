package com.christ.erp.services.transactions.account.fee;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeAccountDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeHeadDBO;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.common.AccFeeDemandAdjustmentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryCampusMappingDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;

import reactor.core.publisher.Mono;

@Repository
public class AccBatchFeeTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public List<AcaDurationDetailDBO> getDurationsByBatch(int batchId){
		StringBuffer queryString = new StringBuffer();
		queryString.append("select bo from AcaDurationDetailDBO bo "
				+ " where bo.recordStatus = 'A'"
				+ " and bo.acaBatchDBO is not null and bo.acaDurationDBO.recordStatus = 'A'"
				+ " and bo.acaSessionDBO.recordStatus = 'A'"
				+ " and  bo.acaDurationDBO.acaSessionGroupDBO.recordStatus = 'A'"
				+ " and bo.acaBatchDBO.id = :batchId");
		return sessionFactory.withSession(s->s.createQuery(queryString.toString(), AcaDurationDetailDBO.class)
		    		.setParameter("batchId", batchId).getResultList()).await().indefinitely();
	}	
	
	public List<ErpAdmissionCategoryCampusMappingDBO> getAdmissionCategoryByCampus(int campusId){
		StringBuffer queryString = new StringBuffer();
		queryString.append("select bo from ErpAdmissionCategoryCampusMappingDBO bo "
				+ " where bo.recordStatus = 'A' and bo.erpAdmissionCategoryDBO.recordStatus = 'A' and bo.erpCampusDBO.id = :campusId");
		return	sessionFactory.withSession(s->s.createQuery(queryString.toString(), 
				ErpAdmissionCategoryCampusMappingDBO.class).setParameter("campusId", campusId).getResultList()).await().indefinitely();
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
	
	public List<AccAccountsDBO> getAccountNosByCampus(int campusId){
		String queryString = "select bo from AccAccountsDBO bo where bo.recordStatus = 'A' and bo.erpCampusDBO.id = :campusId";
	    return	sessionFactory.withSession(s->s.createQuery(queryString.toString(), 
	    		AccAccountsDBO.class).setParameter("campusId", campusId).getResultList()).await().indefinitely();
	}
	
	public List<AccFeeHeadsDBO> getFeeHeads(){
		String queryString = "select bo from AccFeeHeadsDBO bo where bo.recordStatus = 'A' and bo.feeHeadsType = 'Tuition Fee' order by bo.heading";
	    return	sessionFactory.withSession(s->s.createQuery(queryString.toString(), AccFeeHeadsDBO.class).getResultList()).await().indefinitely();
	}
	
	public void save(AccBatchFeeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
    }
	public void update(AccBatchFeeDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}
	public boolean duplicateCheck(AccBatchFeeDTO accBatchFeeDTO) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("from AccBatchFeeDBO bo where bo.recordStatus='A' and "
				+ "bo.acaBatchDBO.id = :batchId ");
		if(Utils.isNullOrEmpty(accBatchFeeDTO.getFeeCollectionSet()) && Utils.isNullOrEmpty(accBatchFeeDTO.getErpSpecializationDTO())) {
			queryString.append("and bo.feeCollectionSet is null and bo.erpSpecializationDBO.id is null");		
		}
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getFeeCollectionSet())) {
			queryString.append("and bo.feeCollectionSet = :feeCollectionSet");
		}
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getErpSpecializationDTO())) {
			queryString.append(" and bo.erpSpecializationDBO.id = :specializationId");
		}
		if(!Utils.isNullOrEmpty(accBatchFeeDTO.getId())) {
			queryString.append(" and bo.id <> :accBatchFeeId");	
		}
   		List<AccBatchFeeDBO> accBatchFeeDBOList =
   				sessionFactory.withSession(s->{
   					Mutiny.Query<AccBatchFeeDBO> query = s.createQuery(queryString.toString(),AccBatchFeeDBO.class);
   					query.setParameter("batchId", Integer.parseInt(accBatchFeeDTO.getAcaBatchDTO().getValue()));
   					if(!Utils.isNullOrEmpty(accBatchFeeDTO.getFeeCollectionSet())) {
   						query.setParameter("feeCollectionSet", accBatchFeeDTO.getFeeCollectionSet().getValue());
   					}
   					if(!Utils.isNullOrEmpty(accBatchFeeDTO.getErpSpecializationDTO())) {
   						query.setParameter("specializationId", Integer.parseInt(accBatchFeeDTO.getErpSpecializationDTO().getValue()));
   					}
   					if(!Utils.isNullOrEmpty(accBatchFeeDTO.getId())) {
   						query.setParameter("accBatchFeeId", accBatchFeeDTO.getId());
   					}
	        return query.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(accBatchFeeDBOList) ? false : true;
    }
	
	public Mono<List<AccBatchFeeDBO>> getGridData(){
		String queryString = "select bo from AccBatchFeeDBO bo "
			     +" where bo.recordStatus = 'A' order by bo.acaBatchDBO.erpProgrammeBatchwiseSettingsDBO.erpAcademicYearDBO.academicYear";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccBatchFeeDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	public AccBatchFeeDBO getAccBatchFeeById(int id){
		String queryString = "select bo from AccBatchFeeDBO bo "
							  +" left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
						      +" left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
						      +" left join fetch durationBo.acaDurationDBO acaDurDBO"
						      +" left join fetch acaDurDBO.acaDurationDetailDBOSet acaDurationDetBo"
							  +" left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
						      +" left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
							  +" left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
						      +" where bo.id = :batchFeeId and bo.recordStatus = 'A' "; 
		return sessionFactory.withSession(s->s.createQuery(queryString, AccBatchFeeDBO.class).setParameter("batchFeeId", id).getSingleResultOrNull()).await().indefinitely();
	}
	 
	public AcaBatchDBO getAcaBatchDBO(int id){
		String queryString = "select bo from AcaBatchDBO bo "
					     +" where bo.id = :batchId and bo.recordStatus = 'A' "; 
		return sessionFactory.withSession(s->s.createQuery(queryString, AcaBatchDBO.class).setParameter("batchId", id).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<Boolean> delete(int id, String userId) {
		sessionFactory.withTransaction((session, tx) -> session.createQuery( "select bo from AccBatchFeeDBO bo "
			+ "left join fetch bo.accBatchFeeDurationsDBOSet durationBo "
			+ "left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo "
			+ "left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo "
			+ "left join fetch categoryBo.accBatchFeeHeadDBOSet headBo "		
			+ "left join fetch headBo.accBatchFeeAccountDBOSet accountBo "
	     	+ "where bo.id=:id", AccBatchFeeDBO.class).setParameter("id", id).getSingleResultOrNull()
			.chain(dbo1->session.fetch(dbo1.getAccBatchFeeDurationsDBOSet())
					.invoke(batchFeeDurationSet->{
						batchFeeDurationSet.forEach(batchFeeDuration->{
						batchFeeDuration.setRecordStatus('D');
						batchFeeDuration.setModifiedUsersId(Integer.parseInt(userId));
						Set<AccBatchFeeDurationsDetailDBO> accBatchFeeDurationDetailDBOSet = batchFeeDuration.getAccBatchFeeDurationsDetailDBOSet().stream().map(batchFeeDurDetailDbo->{
						batchFeeDurDetailDbo.setRecordStatus('D');
						batchFeeDurDetailDbo.setModifiedUsersId(Integer.parseInt(userId));
						Set<AccBatchFeeCategoryDBO> accBatchFeeCategoryDBOSet = batchFeeDurDetailDbo.getAccBatchFeeCategoryDBOSet().stream().map(accBatchFeeCatDbo->{
							accBatchFeeCatDbo.setRecordStatus('D');
							accBatchFeeCatDbo.setModifiedUsersId(Integer.parseInt(userId));
							Set<AccBatchFeeHeadDBO> accbatchFeeHeadDboSet = accBatchFeeCatDbo.getAccBatchFeeHeadDBOSet().stream().map(accBatchFeeheadDbo->{
								accBatchFeeheadDbo.setRecordStatus('D');
								accBatchFeeheadDbo.setModifiedUsersId(Integer.parseInt(userId));
								Set<AccBatchFeeAccountDBO> accBatchFeeAccountDBOSet = accBatchFeeheadDbo.getAccBatchFeeAccountDBOSet().stream().map(accBatchFeeAccDbo->{
									accBatchFeeAccDbo.setRecordStatus('D');
									accBatchFeeAccDbo.setModifiedUsersId(Integer.parseInt(userId));
									return accBatchFeeAccDbo;
								}).collect(Collectors.toSet());
								accBatchFeeheadDbo.setAccBatchFeeAccountDBOSet(accBatchFeeAccountDBOSet);
								return accBatchFeeheadDbo;
							}).collect(Collectors.toSet());
							accBatchFeeCatDbo.setAccBatchFeeHeadDBOSet(accbatchFeeHeadDboSet);
							return accBatchFeeCatDbo;
						}).collect(Collectors.toSet());
							batchFeeDurDetailDbo.setAccBatchFeeCategoryDBOSet(accBatchFeeCategoryDBOSet);
							return batchFeeDurDetailDbo;
					}).collect(Collectors.toSet());
					batchFeeDuration.setAccBatchFeeDurationsDetailDBOSet(accBatchFeeDurationDetailDBOSet);
				});
			dbo1.setRecordStatus('D');
			dbo1.setModifiedUsersId(Integer.parseInt(userId));
		}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}
	public List<AccFinancialYearDBO> getFinancialYear(){
		String queryString = "select bo from AccFinancialYearDBO bo "
					     +" where bo.recordStatus = 'A' "; 
		return sessionFactory.withSession(s->s.createQuery(queryString, AccFinancialYearDBO.class).getResultList()).await().indefinitely();
	}
	
	public List<AccBatchFeeDBO> getAccountFeeDetails(String admissionCategoryId, String acaBatchId) {
		
		String str = " select  bo from AccBatchFeeDBO bo "
				  +" left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
			     +" left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
			     + " left join fetch durationBo.acaDurationDBO acaDurDBO"
			     + " left join fetch acaDurDBO.acaDurationDetailDBOSet acaDurationDetBo"
				 +" left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
			     +" left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
				 +" left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
			     + " where bo.recordStatus = 'A' "
				+ " and bo.acaBatchDBO.id= :acaBatchId and categoryBo.erpAdmissionCategoryDBO.id=:admissionCategoryId  "
				+ " and acaDurationDetBo.acaBatchDBO.id = :acaBatchId and acaDurationDetBo.acaSessionDBO.yearNumber=1";

		return sessionFactory.withSession(s -> s.createQuery(str, AccBatchFeeDBO.class)
						.setParameter("acaBatchId", Integer.parseInt(acaBatchId))
						.setParameter("admissionCategoryId", Integer.parseInt(admissionCategoryId)).getResultList()).await().indefinitely();
	}
	
	public Mono<List<AccFeeDemandAdjustmentCategoryDBO>> getDemandAdjustmentCatgeory(){
		String queryString = " from AccFeeDemandAdjustmentCategoryDBO bo where bo.recordStatus = 'A' and bo.adjustmentType = 'S'";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(queryString, AccFeeDemandAdjustmentCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
	}
	
}

