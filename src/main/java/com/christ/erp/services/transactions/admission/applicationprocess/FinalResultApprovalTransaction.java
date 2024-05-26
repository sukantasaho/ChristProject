package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.LockModeType;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDurationsDetailDBO;
import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.account.settings.AccBillReceiptDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalDTO;
import com.christ.erp.services.transactions.account.fee.DemandSlipGenerationTransaction;

import reactor.core.publisher.Mono;

@Repository
public class FinalResultApprovalTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	@Autowired
	DemandSlipGenerationTransaction demandSlipGenerationTransaction;
 
	
	public Mono<List<Tuple>> getGridData(String applicantCurrentProcessStatus, String applNo, String campusId, String locationId, String programmeLevelId, String programmeId, List<String> finalResultApproverStatus) {
		String queryString = " select s.application_no as applnNum, s.applicant_name as applicantName,erp_campus.campus_name as campusName,erp_programme.programme_name as programeName,erp_programme.erp_programme_id as programeId, "
				+ " s.selection_status_remarks as remarks, erp_work_flow_process.application_status_display_text as status, s.erp_admission_category_id as admissionCategoryId, s.erp_resident_category_id as residentCategoryId, "
				+ " s.personal_email_id as emailId, s.mobile_no as mobileNumber, s.erp_campus_programme_mapping_id as erpCampusProgrameMappingId, s.student_appln_entries_id as studentApplnEntriesId, s.aca_batch_id as acaBatchId, erp_location.location_name as locationName  "
				+ " ,erp_admission_category.admission_category_name as admissionCategoryName,erp_resident_category.resident_category_name as residentCategoryName,"
				+ " s.fee_payment_final_datetime as feePaymentDateTime,s.admission_start_datetime as admissionStartDate,s.admission_final_datetime as admissionFinalDate,erp_work_flow_process.process_code as processCode,s.mode_of_study as modeOfStudy from student_appln_entries s "
				+ "inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = s.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status = 'A' "
				+ "inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status='A' "
				+ "inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status='A' "
				+ "inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme.erp_programme_level_id and erp_programme_level.record_status='A' "
				+ "inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = s.applicant_current_process_status and erp_work_flow_process.record_status='A' "
				+ "inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id and erp_location.record_status='A' "
				+ "left join erp_admission_category ON erp_admission_category.erp_admission_category_id = s.erp_admission_category_id and erp_admission_category.record_status='A' "
				+ " left join erp_resident_category ON erp_resident_category.erp_resident_category_id = s.erp_resident_category_id and erp_resident_category.record_status='A' ";
				
		if (!Utils.isNullOrEmpty(applicantCurrentProcessStatus) && !applicantCurrentProcessStatus.equalsIgnoreCase("undefined"))
			queryString = queryString +  " where erp_work_flow_process.process_code=:applicantCurrentProcessStatusId ";
		else
			queryString = queryString +  " where erp_work_flow_process.process_code in (:applicantCurrentProcessStatusId) ";
		
		if (applNo!=null && !applNo.isEmpty())
			queryString = queryString + " and s.application_no=:applNo ";
		if (!Utils.isNullOrEmpty(campusId) && !campusId.equalsIgnoreCase("undefined"))
			queryString = queryString + " and  erp_campus.erp_campus_id=:campusId ";
		if (!Utils.isNullOrEmpty(locationId) && !locationId.equalsIgnoreCase("undefined"))
			queryString = queryString + " and erp_location.erp_location_id=:locationId ";
		if (!Utils.isNullOrEmpty(programmeLevelId) && !programmeLevelId.equalsIgnoreCase("undefined"))
			queryString = queryString + " and erp_programme_level.erp_programme_level_id=:programmeLevelId ";
		if (!Utils.isNullOrEmpty(programmeId) && !programmeId.equalsIgnoreCase("undefined"))
			queryString = queryString + " and erp_programme.erp_programme_id=:programmeId ";
		
		String finalStr = queryString;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			
			if (!Utils.isNullOrEmpty(applicantCurrentProcessStatus) && !applicantCurrentProcessStatus.equalsIgnoreCase("undefined"))
				query.setParameter("applicantCurrentProcessStatusId", applicantCurrentProcessStatus);
			else {
				query.setParameter("applicantCurrentProcessStatusId", finalResultApproverStatus);
			}
			
			if (applNo!=null && !applNo.isEmpty())
				query.setParameter("applNo", applNo);
			if (!Utils.isNullOrEmpty(campusId) && !campusId.equalsIgnoreCase("undefined"))
				query.setParameter("campusId", campusId);
			if (!Utils.isNullOrEmpty(locationId) && !locationId.equalsIgnoreCase("undefined"))
				query.setParameter("locationId", locationId);
			if (!Utils.isNullOrEmpty(programmeLevelId) && !programmeLevelId.equalsIgnoreCase("undefined"))
				query.setParameter("programmeLevelId", programmeLevelId);
			if (!Utils.isNullOrEmpty(programmeId) && !programmeId.equalsIgnoreCase("undefined"))
				query.setParameter("programmeId", programmeId);
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public List<StudentApplnEntriesDBO> getStudentAppilnEntries(List<Integer> studentApplnEntriesId) {
		String str = "select s from StudentApplnEntriesDBO s where s.recordStatus = 'A' and s.id in (:id)";
		return (List<StudentApplnEntriesDBO>) sessionFactory.withSession(s -> s.createQuery(str, StudentApplnEntriesDBO.class)
						.setParameter("id", studentApplnEntriesId).getResultList()).await().indefinitely();
	}
	public boolean finalResultApprovalStatusUpdate(Object[] objArr, List<Integer> responseDTOList) {
		List<StudentApplnEntriesDBO> studentApplnEntriesDBOList = (List<StudentApplnEntriesDBO>) objArr[0];
		List<StudentApplnEntriesDBO> studentApplnEntriesDBOListNew = studentApplnEntriesDBOList.stream().filter(s->responseDTOList.contains(s.getId())).collect(Collectors.toList());
		List<ErpWorkFlowProcessStatusLogDBO> erpWorkFlowProcessStatusLogDBOList = (List<ErpWorkFlowProcessStatusLogDBO>) objArr[2];
		if(!Utils.isNullOrEmpty(studentApplnEntriesDBOListNew)) {
			sessionFactory.withTransaction((session, tx) -> session.mergeAll(studentApplnEntriesDBOListNew.toArray()).flatMap(s -> session.persistAll(erpWorkFlowProcessStatusLogDBOList.toArray()))).await().indefinitely();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean finalResultApprovalStatusUpdateOld(Object[] objArr) {
		AccFinancialYearDBO curFeeFinYearDBO = demandSlipGenerationTransaction.getCurrentFeeFinancialYearIdAndName();
		Integer finId = curFeeFinYearDBO.getId();
		List<StudentApplnEntriesDBO> studentApplnEntriesDBOList = (List<StudentApplnEntriesDBO>) objArr[0];
		
		studentApplnEntriesDBOList.forEach(studentApplnEntriesDB ->{
			List<ErpWorkFlowProcessStatusLogDBO> erpWorkFlowProcessStatusLogDBOList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
			erpWorkFlowProcessStatusLogDBOList.add(getErpWorkFlowProcessStatusLogDBO(studentApplnEntriesDB.getId(),studentApplnEntriesDB, "1"));
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
					studentApplnEntriesDB.getAccFeeDemandDBOSet().forEach(d->{
						d.setDemandSlipNo(receiptNoForDemand.get());
						receiptNoForDemand.getAndIncrement();
						if(!Utils.isNullOrEmpty(d.getAccFeeDemandAdjustmentDBOSet())) {
							d.getAccFeeDemandAdjustmentDBOSet().forEach(a->{
								a.setAdjustmentNo(receiptNoForAdjustment.get());
								receiptNoForAdjustment.getAndIncrement();
							});
						}
					});
					receiptList.forEach(receipt->{
						if(receipt.getTypeCode().equalsIgnoreCase("DEMAND_SLIP")) {
							receipt.setBillReceiptCurrentNo(receiptNoForDemand.getAndIncrement());
						}
						if(receipt.getTypeCode().equalsIgnoreCase("SCHOLARSHIP")) {
							receipt.setBillReceiptCurrentNo(receiptNoForAdjustment.getAndIncrement());
						}
					});

				 })
			.flatMap(student -> session.merge(studentApplnEntriesDB).chain(session::flush))
			.invoke(d->{			
				studentApplnEntriesDB.getAccFeeDemandDBOSet().forEach(demandWithId->{
					if(!Utils.isNullOrEmpty(demandWithId.getAccFeeDemandAdjustmentDBOSet())) {
						demandWithId.getAccFeeDemandAdjustmentDBOSet().forEach(adj->{
							if(!Utils.isNullOrEmpty(adj.getId())) {
								if(!Utils.isNullOrEmpty(adj.getErpWorkFlowProcessDBO().getId())){
									ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
									erpWorkFlowProcessStatusLogDBO.setEntryId(adj.getId());
									erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(adj.getErpWorkFlowProcessDBO());
									erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
									erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(adj.getCreatedUsersId());
									erpWorkFlowProcessStatusLogDBO.setModifiedUsersId(adj.getCreatedUsersId());
									erpWorkFlowProcessStatusLogDBOList.add(erpWorkFlowProcessStatusLogDBO);
								}
							}
						});
					}
				});
			})
			.flatMap(s->!Utils.isNullOrEmpty(erpWorkFlowProcessStatusLogDBOList)? session.persistAll(erpWorkFlowProcessStatusLogDBOList.toArray()): null)
			).await().indefinitely();
		});
		return true;
	}
	public List<AccBatchFeeDBO> getAccountFeeDetails(String admissionCategoryId, String acaBatchId) {
		/*String str = "from AccBatchFeeDBO abf"
				+ " left join fetch abf.accBatchFeeDurationsDBOSet as afd"
				+ "left join fetch afd.acaDurationDBO.acaDurationDetailDBOSet acaDurDet "
				+ " left join fetch afd.accBatchFeeDurationsDetailDBOSet as det"
				+ " left join fetch det.accBatchFeeCategoryDBOSet as abfc"
				+ " left join fetch abfc.accBatchFeeHeadDBOSet as abfh"
				+ " left join fetch abfh.accBatchFeeAccountDBOSet as abfa" + " where abf.recordStatus = 'A' "
				+ " and abf.acaBatchDBO.id= :acaBatchId and abfc.erpAdmissionCategoryDBO.id=:admissionCategoryId and "
				+ " acaDurDet.acaBatchDBO.id = :acaBatchId and acaDurDet.acaSessionDBO.yearNumber=1";*/
		
		String str = " select  distinct bo from AccBatchFeeDBO bo "
				  +" left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
			      +" left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
			      + " left join fetch durationBo.acaDurationDBO acaDurDBO"
			      + " left join fetch acaDurDBO.acaDurationDetailDBOSet acaDurationDetBo"
				  +" left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
			      +" left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
				  +" left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
			      + " where bo.recordStatus = 'A' "
				  + " and bo.acaBatchDBO.id= :acaBatchId and categoryBo.erpAdmissionCategoryDBO.id=:admissionCategoryId  "
				  + " and acaDurationDetBo.acaBatchDBO.id = :acaBatchId and acaDurationDetBo.acaSessionDBO.yearNumber=1 and durationDetailBo.acaDurationDetailDBO is NULL";
			     
		return sessionFactory.withSession(s -> s.createQuery(str, AccBatchFeeDBO.class)
						.setParameter("acaBatchId", Integer.parseInt(acaBatchId))
						.setParameter("admissionCategoryId", Integer.parseInt(admissionCategoryId)).getResultList()).await().indefinitely();
	}

	public Mono<List<ErpWorkFlowProcessDBO>> getFinalResultApprovalStatusList() {
		List<String> statusCode=new ArrayList<String>();
		statusCode.add("ADM_APPLN_SELECTED_UPLOADED");
		statusCode.add("ADM_APPLN_NOT_SELECTED_UPLOADED");
		statusCode.add("ADM_APPLN_WAITLISTED_UPLOADED");
		statusCode.add("ADM_APPLN_SELECTED");
		String queryString = "select dbo from ErpWorkFlowProcessDBO dbo where dbo.recordStatus = 'A' and dbo.processCode in (:statusCode)";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(queryString, ErpWorkFlowProcessDBO.class)
				.setParameter("statusCode", statusCode)
				.getResultList()).subscribeAsCompletionStage());
	}

	public List<AdmProgrammeSettingsDBO> getAdmProgrammeSettings(String programeId) {
		String str = "select dbo from AdmProgrammeSettingsDBO dbo where dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.id=:programeId ";
		return sessionFactory.withSession(s -> s.createQuery(str, AdmProgrammeSettingsDBO.class)
				.setParameter("programeId", Integer.parseInt(programeId))
			    .getResultList()).await().indefinitely();
	}
	
	public List<StudentApplnEntriesDBO> getTest(int tempId) {
		String str = "select hh from StudentApplnEntriesDBO  hh"
				+ "left join fetch  hh.accFeeDemandDBOSet"
				+ " where hh.recordStatus = 'A' and hh.id in (:tempId)";
		return (List<StudentApplnEntriesDBO>) sessionFactory.withSession(s -> s.createQuery(str, StudentApplnEntriesDBO.class)
						.setParameter("tempId",tempId).getResultList()).await().indefinitely();
	}
	
	public  ErpWorkFlowProcessStatusLogDBO getErpWorkFlowProcessStatusLogDBO(int entryId, StudentApplnEntriesDBO dbo,String userId) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.entryId = entryId;
		ErpWorkFlowProcessDBO e1 = new ErpWorkFlowProcessDBO();
		if (!Utils.isNullOrEmpty(dbo.applicantCurrentProcessStatus.id))
			e1.id = dbo.applicantCurrentProcessStatus.id;
		else
			e1.id = dbo.applicantCurrentProcessStatus.id;
		erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = e1;
		erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
		erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
		return erpWorkFlowProcessStatusLogDBO;

	}

	public boolean isDemandExists(Set<Integer> studentApplicationEntriesId) {
		StringBuffer str = new StringBuffer(" select distinct bo from AccFeeDemandDBO bo where bo.recordStatus = 'A' "
						+ " and bo.studentApplnEntriesDBO.id in (:studentApplicationEntriesId)");
		List<AccFeeDemandDBO> accFeeDemandDBOList = sessionFactory.withSession(s -> {
			Mutiny.Query<AccFeeDemandDBO> query = s.createQuery(str.toString(), AccFeeDemandDBO.class);
			query.setParameter("studentApplicationEntriesId", studentApplicationEntriesId);
			return query.getResultList();
		}).await().indefinitely();
		return !Utils.isNullOrEmpty(accFeeDemandDBOList) ? true : false;
	}
	
	public AccBatchFeeCategoryDBO getStudentwiseDemand(StudentApplnEntriesDBO studentApplnEntriesDBO) {
			StringBuffer str = new StringBuffer(" select distinct bo from AccBatchFeeDBO bo "
					  + " left join fetch bo.accBatchFeeDurationsDBOSet durationBo"
				      + " left join fetch durationBo.accBatchFeeDurationsDetailDBOSet durationDetailBo"
				      + " left join fetch durationBo.acaDurationDBO acaDurDBO"
					  + " left join fetch durationDetailBo.accBatchFeeCategoryDBOSet categoryBo"
				      + " left join fetch categoryBo.accBatchFeeHeadDBOSet headBo"
					  + " left join fetch headBo.accBatchFeeAccountDBOSet accountBo"
				      + " where bo.recordStatus = 'A' "
					  + " and bo.acaBatchDBO.id = :acaBatchId and durationBo.acaDurationDBO.id = :durationId"
					  + " and durationDetailBo.acaDurationDetailDBO is null and categoryBo.erpAdmissionCategoryDBO.id = :categoryId");
   		List<AccBatchFeeDBO> accBatchFeeDBOList = sessionFactory.withSession(s->{
   				Mutiny.Query<AccBatchFeeDBO> query = s.createQuery(str.toString(),AccBatchFeeDBO.class)
   						.setParameter("acaBatchId",studentApplnEntriesDBO.getAcaBatchDBO().getId())
   						.setParameter("durationId",studentApplnEntriesDBO.getSelectedDurationDetailDBO().getAcaDurationDBO().getId())
   								.setParameter("categoryId",studentApplnEntriesDBO.getErpAdmissionCategoryDBO().getId());
	        return query.getResultList();
        }).await().indefinitely();
		Map<String, List<AccBatchFeeDBO>> feeTypewiseBatchFeeMap = new HashMap<String, List<AccBatchFeeDBO>>();				
		feeTypewiseBatchFeeMap = accBatchFeeDBOList.stream().collect(Collectors.groupingBy(f->(f.getFeeCollectionSet() == null ?"common":f.getFeeCollectionSet())));
   		
		List<AccBatchFeeDBO> accBatchFeeDBONewList = new ArrayList<AccBatchFeeDBO>();
		if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getErpSpecializationDBO())) {
			accBatchFeeDBONewList = feeTypewiseBatchFeeMap.get("Specialisation wise Fee");
		}
		else if(!Utils.isNullOrEmpty(studentApplnEntriesDBO.getModeOfStudy())) {
			accBatchFeeDBONewList = feeTypewiseBatchFeeMap.get(studentApplnEntriesDBO.getModeOfStudy());
		}
		if(Utils.isNullOrEmpty(accBatchFeeDBONewList)) {
			accBatchFeeDBONewList = feeTypewiseBatchFeeMap.get("common"); 
		}
		AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = null;
		if(!Utils.isNullOrEmpty(accBatchFeeDBONewList) && !Utils.isNullOrEmpty(accBatchFeeDBONewList.get(0).getAccBatchFeeDurationsDBOSet())) {
			AccBatchFeeDurationsDBO accBatchFeeDurationsDBO = accBatchFeeDBONewList.get(0).getAccBatchFeeDurationsDBOSet().stream().findFirst().get();
				if(!Utils.isNullOrEmpty(accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet())){
					AccBatchFeeDurationsDetailDBO accBatchFeeDurationsDetailDBO = accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet().stream().findFirst().get();
					if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet())) {
						accBatchFeeCategoryDBO = accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet().stream().findFirst().get();
					}
				}
		}
        return !Utils.isNullOrEmpty(accBatchFeeCategoryDBO) ? accBatchFeeCategoryDBO : null;
    }

	public FinalResultApprovalDTO getFinalResultApprovalStatusListCount(FinalResultApprovalDTO dto) {
		String queryString = "select erp_work_flow_process.process_code as processCode, COUNT(s.applicant_current_process_status) as processCodeCount  from student_appln_entries s  "
				+ "				inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = s.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status = 'A'  "
				+ "				inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status='A'  "
				+ "				inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status='A'  "
				+ "       		inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme.erp_programme_level_id and erp_programme_level.record_status='A'  "
				+ "				inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = s.applicant_current_process_status and erp_work_flow_process.record_status='A'  "
				+ "				inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id and erp_location.record_status='A'  "
				+ "				left join erp_admission_category ON erp_admission_category.erp_admission_category_id = s.erp_admission_category_id and erp_admission_category.record_status='A'  "
				+ "				left join erp_resident_category ON erp_resident_category.erp_resident_category_id = s.erp_resident_category_id and erp_resident_category.record_status='A' "
				+ "      where erp_work_flow_process.process_code in ('ADM_APPLN_SELECTED_UPLOADED','ADM_APPLN_NOT_SELECTED_UPLOADED','ADM_APPLN_WAITLISTED_UPLOADED','ADM_APPLN_SELECTED') "
				+ "      group by erp_work_flow_process.process_code ";
		List<Tuple> value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(queryString, Tuple.class);
			return query.getResultList();
		}).await().indefinitely();

		for (Tuple tuple : value) {
			if (tuple.get("processCode").toString().equalsIgnoreCase("ADM_APPLN_SELECTED_UPLOADED"))
				dto.setSelectedCount(tuple.get("processCodeCount").toString());
			else if (tuple.get("processCode").toString().equalsIgnoreCase("ADM_APPLN_NOT_SELECTED_UPLOADED"))
				dto.setNotSelectedCount(tuple.get("processCodeCount").toString());
			else if (tuple.get("processCode").toString().equalsIgnoreCase("ADM_APPLN_WAITLISTED_UPLOADED"))
				dto.setWaitlistedCount(tuple.get("processCodeCount").toString());
		}
		return dto;
	}
	
	public String getFeeModes(List<Integer> admProgrammeSettingsIds) {
		String str = "select distinct acc_fee_payment_mode.payment_mode as mode from adm_programme_fee_payment_mode m "
				+ " inner join adm_programme_settings ON adm_programme_settings.adm_programme_settings_id = m.adm_programme_settings_id and adm_programme_settings.record_status='A' "
				+ " inner join acc_fee_payment_mode ON acc_fee_payment_mode.acc_fee_payment_mode_id = m.acc_fee_payment_mode_id and acc_fee_payment_mode.record_status='A' "
				+ " where adm_programme_settings.adm_programme_settings_id in (:admProgrammeSettingsIds) and m.record_status='A' ";
		List<Tuple> value = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("admProgrammeSettingsIds", admProgrammeSettingsIds);
			return query.getResultList();
		}).await().indefinitely();
		String mode = null;
		for (Tuple tuple : value) {
			if (mode == null)
				mode = String.valueOf(tuple.get("mode"));
			else
				mode = mode + " , " + String.valueOf(tuple.get("mode"));
		}
		return mode;
	}
}
