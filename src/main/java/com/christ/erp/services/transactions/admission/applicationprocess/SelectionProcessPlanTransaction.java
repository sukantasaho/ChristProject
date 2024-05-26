package com.christ.erp.services.transactions.admission.applicationprocess;

import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.*;
import com.christ.erp.services.dbobjects.admission.applicationprocess.*;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddSlotDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAddVenueDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SelectionProcessPlanTransaction {

private static volatile SelectionProcessPlanTransaction  selectionProcessPlanTransaction=null;
	public static SelectionProcessPlanTransaction getInstance() {
        if(selectionProcessPlanTransaction==null) {
        	selectionProcessPlanTransaction = new SelectionProcessPlanTransaction();
        }
        return selectionProcessPlanTransaction;
    }
	@Autowired
	private Mutiny.SessionFactory sessionFactory;


	//	public List<Tuple> getGridData(String admissionBatchId, String intakeId, String date) throws Exception {
//		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//            public List<Tuple> onRun(EntityManager context) throws Exception {
//            	StringBuffer sb = new StringBuffer();
//            	sb.append("select  plan.adm_selection_process_plan_id as id, plan.erp_academic_year_id as yearId,adm_intake_batch.adm_intake_batch_name as batch ," +
//						" group_concat(distinct adm_admission_type.admission_type) as aType, " +
//						" plan.selection_process_session as session, plan.application_open_from as openFrom,  plan.application_open_till as openTill," +
//						" plan.selection_process_start_date as sdate, plan.selection_process_end_date as edate, plan.last_date_of_admission as lastAdm ," +
//						" plan.result_declaration_date as resultDecl  " +
//						" from adm_selection_process_plan as plan" +
//						" left  join adm_selection_process_plan_programme on plan.adm_selection_process_plan_id = adm_selection_process_plan_programme.adm_selection_process_plan_id " +
//						" left join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id " +
//						" left join adm_programme_settings ON adm_programme_settings.adm_programme_settings_id = adm_programme_batch.adm_programme_settings_id " +
//						" left join adm_admission_type ON adm_admission_type.adm_admission_type_id = adm_programme_settings.adm_admission_type_id " +
//						" inner join erp_academic_year  ON erp_academic_year.erp_academic_year_id = plan.erp_academic_year_id  and erp_academic_year.record_status = 'A'" +
//						" inner join adm_intake_batch ON adm_intake_batch.adm_intake_batch_id = plan.adm_intake_batch_id and adm_intake_batch.record_status = 'A'" +
//						" where plan.record_status = 'A' and plan.erp_academic_year_id = :admissionBatchId and plan.adm_intake_batch_id = :intakeId " );
//            	if(!Utils.isNullOrEmpty(date)) {
//            		sb.append(" and plan.result_declaration_date >= :Date ");
//            	}
//				sb.append(" group by  plan.adm_selection_process_plan_id");
//				Query query = context.createNativeQuery(sb.toString());
//				if(!Utils.isNullOrEmpty(admissionBatchId)) {
//					query.setParameter("admissionBatchId", Integer.parseInt(admissionBatchId.trim()));
//				}
//				if(!Utils.isNullOrEmpty(intakeId)){
//					query.setParameter("intakeId", Integer.parseInt(intakeId.trim()));
//				}
//				if(!Utils.isNullOrEmpty(date)) {
//					query.setParameter("Date", Utils.convertStringDateToLocalDate(date).atStartOfDay());
//				}
//                List<Tuple> mappings = query.getResultList();
//                return mappings;
//            }
//            @Override
//            public void onError(Exception error) throws Exception {
//                throw error;
//            }
//	    });
//	}

	public Mono<List<Tuple>> getGridData(String admissionBatchId , String  intakeId, String date) {
		String queryString = " select  plan.adm_selection_process_plan_id as id, plan.erp_academic_year_id as yearId, " +
				" cast(group_concat(distinct adm_admission_type.admission_type) as char) as aType, " +
				" plan.selection_process_session as session, plan.application_open_from as openFrom,  plan.application_open_till as openTill," +
				" plan.selection_process_start_date as sdate, plan.selection_process_end_date as edate, plan.last_date_of_admission as lastAdm ," +
				" plan.result_declaration_date as resultDecl, plan.is_conducted_in_india  " +
				" from adm_selection_process_plan as plan" +
				" left  join adm_selection_process_plan_programme on plan.adm_selection_process_plan_id = adm_selection_process_plan_programme.adm_selection_process_plan_id" +
				" left join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id" +
				" left join adm_programme_settings ON adm_programme_settings.adm_programme_settings_id = adm_programme_batch.adm_programme_settings_id" +
				" left join adm_admission_type ON adm_admission_type.adm_admission_type_id = adm_programme_settings.adm_admission_type_id" +
				" inner join erp_academic_year  ON erp_academic_year.erp_academic_year_id = plan.erp_academic_year_id  and erp_academic_year.record_status = 'A'" +
//				" inner join adm_intake_batch ON adm_intake_batch.adm_intake_batch_id = plan.adm_intake_batch_id and adm_intake_batch.record_status = 'A'" +
				" where plan.record_status = 'A'  ";
		if(!Utils.isNullOrEmpty(admissionBatchId)) {
			queryString += " and plan.erp_academic_year_id = :admissionBatchId ";
		}
//		if(!Utils.isNullOrEmpty(intakeId)) {
//			queryString += "  and plan.adm_intake_batch_id = :intakeId";
//		}
//		if(!Utils.isNullOrEmpty(date)) {
//			queryString += " and Date(plan.result_declaration_date) >= :date ";
//		}
		queryString += " group by  plan.adm_selection_process_plan_id";
		String finalStr = queryString;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
			if(!Utils.isNullOrEmpty(admissionBatchId)) {
				query.setParameter("admissionBatchId", Integer.parseInt(admissionBatchId.trim()));
			}
//			if(!Utils.isNullOrEmpty(intakeId)) {
//				query.setParameter("intakeId", Integer.parseInt(intakeId.trim()));
//			}
//			if(!Utils.isNullOrEmpty(date)) {
//				query.setParameter("date", Utils.convertStringDateToLocalDate(date));
//			}
			return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}
	public boolean saveOrUpdate(AdmSelectionProcessPlanDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(dbo.id)) {
                    context.persist(dbo);
                }
                else {
                    context.merge(dbo);
                }	            
            return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
	public List<AdmSelectionProcessPlanDBO> getDuplicateCheck(AdmSelectionProcessPlanDTO data) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<AdmSelectionProcessPlanDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
            public List<AdmSelectionProcessPlanDBO> onRun(EntityManager context) throws Exception {
				List<AdmSelectionProcessPlanDBO> duplicateChecking = null;
				StringBuffer sb = new StringBuffer();
				sb.append("from AdmSelectionProcessPlanDBO bo where bo.recordStatus='A' and bo.selectionProcessSession=:SessionName");
				if(!Utils.isNullOrEmpty(data.id)) {
					sb.append(" and bo.id not in (:ID)");
				}
				if(!Utils.isNullOrEmpty(data.sessionname) && !Utils.isNullOrEmpty(data.sessionname)) {
			        Query queryDuplicateCheck = context.createQuery(sb.toString());
		            queryDuplicateCheck.setParameter("SessionName", data.sessionname);
		            if(!Utils.isNullOrEmpty(data.id)) {
		            	queryDuplicateCheck.setParameter("ID", Integer.parseInt(data.id));
		            }
			        duplicateChecking = (List<AdmSelectionProcessPlanDBO>) queryDuplicateCheck.getResultList();
				}
		        return duplicateChecking;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}

	public AdmSelectionProcessPlanDBO edit(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessPlanDBO>() {
			@Override
			public AdmSelectionProcessPlanDBO onRun(EntityManager context) throws Exception {
				AdmSelectionProcessPlanDBO dbo = null;
				try{
					Query query = context.createQuery("select distinct bo from AdmSelectionProcessPlanDBO bo left join fetch bo.admSelectionProcessPlanDetailDBO cbo "
							+ " where bo.id=:Id and bo.recordStatus='A'");
					query.setParameter("Id", id);
					dbo = (AdmSelectionProcessPlanDBO) Utils.getUniqueResult(query.getResultList());
				}catch (Exception e) {
					e.printStackTrace();
				}
//				catch (NoResultException nre){
//					//Ignore this because as per your logic this is ok!
//				}
				return dbo;	
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
	
//	public AdmSelectionProcessPlanDBO edit1(int id) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessPlanDBO>() {
//			@Override
//			public AdmSelectionProcessPlanDBO onRun(EntityManager context) throws Exception {
//				AdmSelectionProcessPlanDBO dbo = null;
//				try{
//					Query query = context.createQuery("select distinct dbo from AdmSelectionProcessPlanDBO dbo " +
//							" left join fetch dbo.admSelectionProcessPlanDetailDBO pd " +
////							" left join fetch pd.admSelectionProcessPlanDetailProgDBOs pdp" +
////							" left join fetch pdp.admProgrammeBatchDBO apb" +
////							" left join fetch apb.erpCampusProgrammeMappingDBO" +
//							" where dbo.id=:Id and dbo.recordStatus='A' ");
//					query.setParameter("Id", id);
//					dbo = (AdmSelectionProcessPlanDBO)  query.getResultList();
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//				return dbo;
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

	public  AdmSelectionProcessPlanDBO edit2(int id) {
		String query =" select dbo from AdmSelectionProcessPlanDBO dbo" +
				" left join fetch dbo.admSelectionProcessPlanProgrammeDBOs pp" +
				" left join fetch pp.admProgrammeBatchDBO apb" +
				" left join fetch apb.erpCampusProgrammeMappingDBO" +
				" left join fetch apb.admProgrammeSettingsDBO ps" +
				" left join fetch ps.admIntakeBatchDBO aib" +
				" left join fetch ps.admAdmissionTypeDBO at" +
				" where dbo.id=:Id and dbo.recordStatus='A'";
		return  sessionFactory.withSession(s->s.createQuery(query,AdmSelectionProcessPlanDBO.class).setParameter("Id", id).getSingleResultOrNull()).await().indefinitely();
	}
	public  AdmSelectionProcessPlanDBO edit1(int id) {
		String query1 = " select dbo from AdmSelectionProcessPlanDBO dbo" +
				" left join fetch dbo.admSelectionProcessPlanProgrammeDBOs pp" +
				" where dbo.recordStatus='A' and dbo.id = :id" ;
		AdmSelectionProcessPlanDBO planDBO = sessionFactory.withSession(s->s.createQuery(query1,AdmSelectionProcessPlanDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();

		String query2 = " select dbo from AdmSelectionProcessPlanDetailDBO dbo " +
				" left join fetch dbo.admSelectionProcessPlanDetailAllotmentDBOs " +
				" where dbo.recordStatus='A' and dbo.admSelectionProcessPlanDBO.id =:id";
		List<AdmSelectionProcessPlanDetailDBO> planDetailDBOList = sessionFactory.withSession(s->s.createQuery(query2, AdmSelectionProcessPlanDetailDBO.class).setParameter("id", id).getResultList()).await().indefinitely();

		Set<Integer> detaiIdSet = planDetailDBOList.stream().map(s->s.getId()).collect(Collectors.toSet());
		String query3 = "select dbo from AdmSelectionProcessPlanCenterBasedDBO dbo" +
				" where dbo.recordStatus='A' and dbo.admSelectionProcessPlanDetailDBO.id in (:ids)";
		List<AdmSelectionProcessPlanCenterBasedDBO> cntreBasedList = sessionFactory.withSession(s->s.createQuery(query3, AdmSelectionProcessPlanCenterBasedDBO.class).setParameter("ids", detaiIdSet).getResultList()).await().indefinitely();

		String query4 = "select dbo from AdmSelectionProcessPlanDetailProgDBO dbo" +
				" left join fetch dbo.admProgrammeBatchDBO apb" +
				" left join fetch apb.erpCampusProgrammeMappingDBO" +
				" left join fetch apb.admProgrammeSettingsDBO ps" +
				" left join fetch ps.admIntakeBatchDBO aib" +
				" left join fetch ps.admAdmissionTypeDBO at" +
				" where dbo.recordStatus='A' and dbo.admSelectionProcessPlanDetailDBO.id in (:ids)";
		List<AdmSelectionProcessPlanDetailProgDBO> detailProgList = sessionFactory.withSession(s->s.createQuery(query4, AdmSelectionProcessPlanDetailProgDBO.class).setParameter("ids", detaiIdSet).getResultList()).await().indefinitely();

		Map<Integer,Set<AdmSelectionProcessPlanCenterBasedDBO>> centreMap = cntreBasedList.stream().collect(Collectors.groupingBy(s->s.getAdmSelectionProcessPlanDetailDBO().getId(), Collectors.toSet()));
		Map<Integer,Set<AdmSelectionProcessPlanDetailProgDBO>> detailProgMap = detailProgList.stream().collect(Collectors.groupingBy(s->s.getAdmSelectionProcessPlanDetailDBO().getId(),Collectors.toSet()));
		planDetailDBOList.forEach(s->{
			s.setAdmSelectionProcessPlanCenterBasedDBOs(new HashSet<>());
			s.setAdmSelectionProcessPlanDetailProgDBOs(new HashSet<>());
			if (!Utils.isNullOrEmpty(detailProgMap)) {
				s.setAdmSelectionProcessPlanDetailProgDBOs(detailProgMap.get(s.getId()));
			}
			if (!Utils.isNullOrEmpty(centreMap)) {
				s.setAdmSelectionProcessPlanCenterBasedDBOs(centreMap.get(s.getId()));
			}
		});
		planDBO.setAdmSelectionProcessPlanDetailDBO(new HashSet<>());
		planDBO.setAdmSelectionProcessPlanDetailDBO(planDetailDBOList.stream().collect(Collectors.toSet()));
		return planDBO;
/* THE QUERY SPLITTED TO GET ONE TO MANY LISTS BECAUSE SINGLE QUERY TAKING MUCH TIME
		String query = " select dbo from AdmSelectionProcessPlanDBO dbo" +
				" left join fetch dbo.admSelectionProcessPlanDetailDBO pd" +
				" left join fetch dbo.admSelectionProcessPlanProgrammeDBOs pp" +
				" left join fetch pd.admSelectionProcessPlanDetailAllotmentDBOs da" +
				" left join fetch pd.admSelectionProcessPlanCenterBasedDBOs cb" +
				" left join fetch pd.admSelectionProcessPlanDetailProgDBOs pdp" +
				" left join fetch pdp.admProgrammeBatchDBO apb" +
				" left join fetch apb.erpCampusProgrammeMappingDBO" +
				" left join fetch apb.admProgrammeSettingsDBO ps" +
				" left join fetch ps.admIntakeBatchDBO aib" +
				" left join fetch ps.admAdmissionTypeDBO at" +
				" where dbo.id=:Id and dbo.recordStatus='A'";
//		return  sessionFactory.withSession(s->s.createQuery(query,AdmSelectionProcessPlanDBO.class).setParameter("Id", id).getSingleResultOrNull()).await().indefinitely();
	 */
	}


	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	AdmSelectionProcessPlanDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(AdmSelectionProcessPlanDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
							if(!Utils.isNullOrEmpty(dbo.admSelectionProcessPlanDetailDBO)){
								   for(AdmSelectionProcessPlanDetailDBO detailDBO: dbo.admSelectionProcessPlanDetailDBO){
									   if(!Utils.isNullOrEmpty(detailDBO.getAdmSelectionProcessPlanCenterBasedDBOs())){
										   for (AdmSelectionProcessPlanCenterBasedDBO centerBasedDbo :  detailDBO.getAdmSelectionProcessPlanCenterBasedDBOs()){
											   centerBasedDbo.recordStatus = 'D';
											   centerBasedDbo.modifiedUsersId = Integer.parseInt(userId);
										   }
									   }
									   if(!Utils.isNullOrEmpty(detailDBO.getAdmSelectionProcessPlanDetailAllotmentDBOs())){
										   for (AdmSelectionProcessPlanDetailAllotmentDBO allotmentDbo :  detailDBO.getAdmSelectionProcessPlanDetailAllotmentDBOs()){
											   allotmentDbo.recordStatus = 'D';
											   allotmentDbo.modifiedUsersId = Integer.parseInt(userId);
										   }
									   }
									   if(!Utils.isNullOrEmpty(detailDBO.getAdmSelectionProcessPlanDetailProgDBOs())){
										   for (AdmSelectionProcessPlanDetailProgDBO detailProgDBO :  detailDBO.getAdmSelectionProcessPlanDetailProgDBOs()){
											   detailProgDBO.recordStatus = 'D';
											   detailProgDBO.modifiedUsersId = Integer.parseInt(userId);
										   }
									   }
									   detailDBO.recordStatus = 'D';
									   detailDBO.modifiedUsersId = Integer.parseInt(userId);
								   }
							}
							if(!Utils.isNullOrEmpty(dbo.admSelectionProcessPlanProgrammeDBOs)){
								for (AdmSelectionProcessPlanProgrammeDBO detailProgDBO :  dbo.admSelectionProcessPlanProgrammeDBOs){
									detailProgDBO.recordStatus = 'D';
									detailProgDBO.modifiedUsersId = Integer.parseInt(userId);
								}
							}
							dbo.recordStatus = 'D';
							dbo.modifiedUsersId = Integer.parseInt(userId);	
							context.merge(dbo);
						}
					}
				}
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	     });
	}

	public AdmSelectionProcessPlanDetailDBO editSlotDetails(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessPlanDetailDBO>() {
			@Override
			public AdmSelectionProcessPlanDetailDBO onRun(EntityManager context) throws Exception {
				AdmSelectionProcessPlanDetailDBO dbo = null;
				try{
					Query query = context.createQuery("from AdmSelectionProcessPlanDetailDBO bo where bo.id=:Id and bo.recordStatus='A'");
					query.setParameter("Id", id);
					dbo = (AdmSelectionProcessPlanDetailDBO) Utils.getUniqueResult(query.getResultList());
				}catch (Exception e) {
					e.printStackTrace();
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}

	public boolean saveOrUpdateDetails(AdmSelectionProcessPlanDetailDBO detailDbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(detailDbo.id)) {
                    context.persist(detailDbo);
                }else {
                    context.merge(detailDbo);
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}

	public boolean deleteDetails(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	AdmSelectionProcessPlanDetailDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(AdmSelectionProcessPlanDetailDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
								if(!Utils.isNullOrEmpty(dbo.getAdmSelectionProcessPlanCenterBasedDBOs())){
									for (AdmSelectionProcessPlanCenterBasedDBO centerBasedDbo :  dbo.getAdmSelectionProcessPlanCenterBasedDBOs()){
										centerBasedDbo.recordStatus = 'D';
										centerBasedDbo.modifiedUsersId = Integer.parseInt(userId);
									}
								}
								if(!Utils.isNullOrEmpty(dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs())){
									for (AdmSelectionProcessPlanDetailAllotmentDBO allotmentDbo :  dbo.getAdmSelectionProcessPlanDetailAllotmentDBOs()){
										allotmentDbo.recordStatus = 'D';
										allotmentDbo.modifiedUsersId = Integer.parseInt(userId);
									}
								}
								if(!Utils.isNullOrEmpty(dbo.getAdmSelectionProcessPlanDetailProgDBOs())){
									for (AdmSelectionProcessPlanDetailProgDBO detailProgDBO :  dbo.getAdmSelectionProcessPlanDetailProgDBOs()){
										detailProgDBO.recordStatus = 'D';
										detailProgDBO.modifiedUsersId = Integer.parseInt(userId);
									}
								}
								dbo.recordStatus = 'D';
								dbo.modifiedUsersId = Integer.parseInt(userId);
								context.merge(dbo);
							}
					}
				}
                return  true ;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	     });
	}

	public AdmSelectionProcessPlanDetailDBO editDetails(List<Integer> ids, String parentId, String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessPlanDetailDBO>() {
			@Override
			public AdmSelectionProcessPlanDetailDBO onRun(EntityManager context) throws Exception {
				AdmSelectionProcessPlanDetailDBO dbo = null;
				try{
					StringBuffer sb = new StringBuffer();
					sb.append("from AdmSelectionProcessPlanDetailDBO bo where bo.recordStatus='A' and bo.admSelectionProcessPlanDBO.id=:ParentId ");
					if(!Utils.isNullOrEmpty(ids) && ids.size()>0) {
						sb.append(" and bo.id not in(:Ids)");
					}
					if(!Utils.isNullOrEmpty(id)) {
						sb.append(" and bo.id=:Id ");
					}
					Query query = context.createQuery(sb.toString(), AdmSelectionProcessPlanDetailDBO.class);
					query.setParameter("ParentId", Integer.parseInt(parentId));
					if(!Utils.isNullOrEmpty(ids) && ids.size()>0) {
				        query.setParameter("Ids", ids);
					}
					if(!Utils.isNullOrEmpty(id)) {
						query.setParameter("Id", Integer.parseInt(id));
					}
					dbo = (AdmSelectionProcessPlanDetailDBO) Utils.getUniqueResult(query.getResultList());
				}catch (Exception e) {
					e.printStackTrace();
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	
	}

	public List<Object[]> duplicateCheckdetails(LocalDate spDate, String slot, String venueid, String id, String processOrder, String parentId) throws Exception{
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			@Override
            public List<Object[]> onRun(EntityManager context) throws Exception {
            	StringBuffer sb = new StringBuffer();
            	sb.append(" select * from adm_selection_process_plan_detail " + 
            	          " where selection_process_date=:SpDate and record_status='A'");
            	if(!Utils.isNullOrEmpty(slot)) {
            		sb.append(" and slot=:Slot");
            	}
            	if(!Utils.isNullOrEmpty(id)) {
            		sb.append(" and not adm_selection_process_plan_detail_id In(:Id)");
            	}
            	if(!Utils.isNullOrEmpty(venueid)) {
            		sb.append(" and adm_selection_process_venue_city_id=:VenueId");
            	}
            	if(!Utils.isNullOrEmpty(processOrder)) {
            		sb.append(" and process_order=:ProcessOrder");
            	}
            	if(!Utils.isNullOrEmpty(parentId)) {
            		sb.append(" and adm_selection_process_plan_id=:ParentId");
            	}
				Query query = context.createNativeQuery(sb.toString());
				query.setParameter("SpDate", spDate);
				if(!Utils.isNullOrEmpty(slot)) {
					query.setParameter("Slot", slot);
				}
				if(!Utils.isNullOrEmpty(id)) {
					query.setParameter("Id", id);
				}
				if(!Utils.isNullOrEmpty(venueid)) {
					query.setParameter("VenueId", venueid);
				}
				if(!Utils.isNullOrEmpty(processOrder)) {
					query.setParameter("ProcessOrder", Integer.parseInt(processOrder));
				}
				if(!Utils.isNullOrEmpty(parentId)) {
					query.setParameter("ParentId", parentId);
				}
                List<Object[]> mappings = query.getResultList();
                return mappings;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });	
	}

	public List<Tuple> getStudentApplnSPDatesBasedSPDetails(List<Integer> detailsIds) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str =  " select ifnull( student_appln_selection_process_dates.adm_selection_process_plan_detail_id, adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id) as detailId, COUNT(student_appln_entries_id) as totalStudentAvailble" +
						" from student_appln_selection_process_dates" +
						" left join adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id" +
						" and adm_selection_process_plan_center_based.record_status = 'A'" +
						" where (student_appln_selection_process_dates.adm_selection_process_plan_detail_id in (:detailsIds) or  adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id in (:detailsIds)) " +
						" and student_appln_selection_process_dates.record_status='A'" +
						" GROUP BY ifnull(student_appln_selection_process_dates.adm_selection_process_plan_detail_id, adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id)";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("detailsIds", detailsIds);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

    public Mono<List<AdmSelectionProcessVenueCityDBO>> getCityVenueList(Boolean isConductedInIndia) {
		String str= " select dbo from AdmSelectionProcessVenueCityDBO dbo" +
				" left join dbo.erpCountryDBO" +
				" where dbo.recordStatus ='A'";
		if(isConductedInIndia) {
			str += " and ((dbo.erpCountryDBO.recordStatus ='A' and dbo.erpCountryDBO.nationalityName like 'Indian') or dbo.selectionProcessMode like 'Online Entrance') ";
		} else {
			str += " and ((dbo.erpCountryDBO.recordStatus ='A' and dbo.erpCountryDBO.nationalityName not like 'Indian') or dbo.selectionProcessMode like 'Online Entrance')";
		}
		String finalStr = str;
		return Mono.fromFuture(sessionFactory.withSession(session -> session.createQuery(finalStr, AdmSelectionProcessVenueCityDBO.class).getResultList()).subscribeAsCompletionStage());
    }

	public List<Tuple> duplicateCheckSlot(AdmSelectionProcessPlanDetailAddSlotDTO data) {
		Set<Integer> porgIds = new HashSet<>();
		String queryString = "Select distinct erp_programme.programme_name  as prog from adm_selection_process_plan_detail " +
				" inner join adm_selection_process_plan_detail_prog on adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail_prog.record_status  = 'A' " +
				" inner join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_detail_prog.adm_programme_batch_id and adm_programme_batch.record_status = 'A'" +
				" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status = 'A'" +
				" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status = 'A' " +
				" where adm_selection_process_plan_detail.record_status = 'A'" +
				" and adm_selection_process_plan_detail.adm_selection_process_plan_id = :planId " +
				" and process_order = :processOrder" +
				" and adm_selection_process_plan_detail_prog.adm_programme_batch_id in (:progs)";
		if(!Utils.isNullOrEmpty(data.id)){
			queryString +=" and adm_selection_process_plan_detail.adm_selection_process_plan_detail_id != :detailsId";
		}
		String finalquery = queryString;
		if(!Utils.isNullOrEmpty(data.getProgramWithPreferance())){
			data.getProgramWithPreferance().forEach( prog -> {
				porgIds.add(Integer.parseInt(prog.getValue()));
				porgIds.add(Integer.parseInt(prog.getValue()));
			});
		}
		List<Tuple> value = sessionFactory.withSession( s-> { Mutiny.Query<Tuple> str = s.createNativeQuery(finalquery,Tuple.class);
			str.setParameter("planId",Integer.parseInt(data.getParentId()));
			str.setParameter("processOrder",Integer.parseInt(data.getProcessOrder()));
			str.setParameter("progs",porgIds);
			if(!Utils.isNullOrEmpty(data.id)) {
				str.setParameter("detailsId",Integer.parseInt(data.id));
			}
			return str.getResultList();
		}).await().indefinitely();
		return value;
	}

	public List<Tuple> duplicateCheckVenu(AdmSelectionProcessPlanDetailAddVenueDTO data) {
		Set<Integer> porgIds = new HashSet<>();
		String queryString = " Select distinct adm_selection_process_plan_detail.adm_selection_process_plan_detail_id from adm_selection_process_plan_detail" +
				" inner join adm_selection_process_plan_detail_prog on adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail.adm_selection_process_plan_detail_id and adm_selection_process_plan_detail_prog.record_status = 'A'" +
				" where adm_selection_process_plan_detail.record_status = 'A'" +
				" and adm_selection_process_plan_detail.selection_process_date = :date" +
				" and adm_selection_process_plan_detail.adm_selection_process_venue_city_id = :venueId" +
				" and adm_selection_process_plan_detail_prog.adm_programme_batch_id in (:progs)" +
				" and adm_selection_process_plan_detail.process_order = :processOrder";
		if(!Utils.isNullOrEmpty(data.id)){
			queryString +=" and adm_selection_process_plan_detail.adm_selection_process_plan_detail_id != :detailsId";
		}
		String finalquery = queryString;
		if(!Utils.isNullOrEmpty(data.getProgramWithPreferance())){
			data.getProgramWithPreferance().forEach( prog -> {
				porgIds.add(Integer.parseInt(prog.getValue()));
			});
		}
		List<Tuple> value = sessionFactory.withSession( s-> { Mutiny.Query<Tuple> str = s.createNativeQuery(finalquery,Tuple.class);
			str.setParameter("date", data.selectionprocessdate);
			str.setParameter("venueId",Integer.parseInt(data.venue.id));
			str.setParameter("progs",porgIds);
			str.setParameter("processOrder",Integer.parseInt(data.getProcessOrder()));
			if(!Utils.isNullOrEmpty(data.id)) {
				str.setParameter("detailsId",Integer.parseInt(data.id));
			}
			return str.getResultList();
		}).await().indefinitely();
		return value;
	}


	public List<Tuple> getCheckDetails(Integer detailsId) {
		String str= " Select distinct student_appln_selection_process_dates.student_appln_selection_process_dates_id as id  from student_appln_selection_process_dates " +
				" where student_appln_selection_process_dates.record_status = 'A' and adm_selection_process_plan_detail_id = :detailsId " +
				" union " +
				" Select distinct student_appln_selection_process_dates.student_appln_selection_process_dates_id as id  from student_appln_selection_process_dates " +
				" inner join adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id " +
				" and adm_selection_process_plan_center_based.record_status = 'A' " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail.record_status = 'A'" +
				" where student_appln_selection_process_dates.record_status = 'A' and adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = :detailsId" +
				" union " +
				" Select distinct student_appln_selection_process_dates_temp.student_appln_selection_process_dates_temp_id as id  from student_appln_selection_process_dates_temp " +
				" where student_appln_selection_process_dates_temp.record_status = 'A' and adm_selection_process_plan_detail_id = :detailsId " +
				" union" +
				" Select distinct student_appln_selection_process_dates_temp.student_appln_selection_process_dates_temp_id as id  from student_appln_selection_process_dates_temp " +
				" inner join adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates_temp.adm_selection_process_plan_center_based_id " +
				" and adm_selection_process_plan_center_based.record_status = 'A' " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail.record_status = 'A' " +
				" where student_appln_selection_process_dates_temp.record_status = 'A' and adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = :detailsId";
		return sessionFactory.withSession(session -> session.createNativeQuery(str, Tuple.class).setParameter("detailsId",detailsId).getResultList()).await().indefinitely();
	}

	public List<Tuple> getCheckPlan(Integer planId) {
		String str= " Select distinct student_appln_selection_process_dates.student_appln_selection_process_dates_id from  student_appln_selection_process_dates " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = student_appln_selection_process_dates.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail.record_status = 'A' " +
				" inner  join adm_selection_process_plan on adm_selection_process_plan_detail.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id " +
				" and adm_selection_process_plan.record_status = 'A' " +
				" where student_appln_selection_process_dates.record_status = 'A' and adm_selection_process_plan.adm_selection_process_plan_id = :planId " +
				" union " +
				" Select distinct student_appln_selection_process_dates.student_appln_selection_process_dates_id from  student_appln_selection_process_dates " +
				" inner join adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id " +
				" and adm_selection_process_plan_center_based.record_status = 'A' " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail.record_status = 'A' " +
				" inner  join adm_selection_process_plan on adm_selection_process_plan_detail.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id " +
				" and adm_selection_process_plan.record_status = 'A' " +
				" where student_appln_selection_process_dates.record_status = 'A' and adm_selection_process_plan.adm_selection_process_plan_id = :planId" +
				" union " +
				" Select distinct student_appln_selection_process_dates_temp.student_appln_selection_process_dates_temp_id from  student_appln_selection_process_dates_temp " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = student_appln_selection_process_dates_temp.adm_selection_process_plan_detail_id" +
				" and adm_selection_process_plan_detail.record_status = 'A'" +
				" inner  join adm_selection_process_plan on adm_selection_process_plan_detail.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id " +
				" and adm_selection_process_plan.record_status = 'A'" +
				" where student_appln_selection_process_dates_temp.record_status = 'A' and adm_selection_process_plan.adm_selection_process_plan_id = :planId  " +
				" union " +
				" Select distinct student_appln_selection_process_dates_temp.student_appln_selection_process_dates_temp_id from  student_appln_selection_process_dates_temp " +
				" inner join adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates_temp.adm_selection_process_plan_center_based_id " +
				" and adm_selection_process_plan_center_based.record_status = 'A' " +
				" inner join   adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id " +
				" and adm_selection_process_plan_detail.record_status = 'A' " +
				" inner  join adm_selection_process_plan on adm_selection_process_plan_detail.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id " +
				" and adm_selection_process_plan.record_status = 'A' " +
				" where student_appln_selection_process_dates_temp.record_status = 'A' and adm_selection_process_plan.adm_selection_process_plan_id = :planId";
		return sessionFactory.withSession(session -> session.createNativeQuery(str, Tuple.class).setParameter("planId",planId).getResultList()).await().indefinitely();
	}

	public List<String> isApplicationNumberCreated(ArrayList<Integer> batchIds) {
		String str = " select  concat (erp_programme.programme_name_for_application,' (',ifnull(erp_campus.campus_name,erp_location.location_name),')') as pclName" +
		" from adm_programme_batch"+
		" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id and erp_campus_programme_mapping.record_status ='A'"+
		" left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status ='A'"+
		" left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id and erp_location.record_status ='A'"+
		" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status ='A'"+
		" left join adm_programme_settings ON adm_programme_settings.adm_programme_settings_id = adm_programme_batch.adm_programme_settings_id"+
		" left join adm_appln_number_gen_details ON adm_programme_batch.erp_campus_programme_mapping_id = adm_appln_number_gen_details.erp_campus_programme_mapping_id and adm_appln_number_gen_details.record_status='A'"+
		" left join adm_appln_number_generation ON adm_appln_number_generation.adm_appln_number_generation_id = adm_appln_number_gen_details.adm_appln_number_generation_id and adm_appln_number_generation.record_status='A'"+
		" and adm_programme_settings.erp_academic_year_id = adm_appln_number_generation.erp_academic_year_id"+
		" where adm_appln_number_generation.adm_appln_number_generation_id is null and adm_programme_batch.adm_programme_batch_id in (:batchIds)"+
		" group by pclName order by pclName";
		return sessionFactory.withSession(session -> session.createNativeQuery(str, String.class).setParameter("batchIds",batchIds).getResultList()).await().indefinitely();
	}

	public List<Tuple> getCheckProg(Integer planId,List<Integer> progIds) {
		String str = " select adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_prog_id  from  adm_selection_process_plan_detail_prog" +
				" inner join adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_prog.adm_selection_process_plan_detail_id" +
				" and adm_selection_process_plan_detail.record_status = 'A'" +
				" inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id" +
				" and adm_selection_process_plan.record_status = 'A'" +
				" where adm_selection_process_plan_detail_prog.record_status = 'A'" +
				" and adm_selection_process_plan.adm_selection_process_plan_id = :planId" +
				" and adm_selection_process_plan_detail_prog.adm_programme_batch_id in (:progIds) ";
		return sessionFactory.withSession(session -> session.createNativeQuery(str, Tuple.class).setParameter("planId",planId).setParameter("progIds",progIds).getResultList()).await().indefinitely();
	}
}