package com.christ.erp.services.transactions.admission.settings;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchPreferencesDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;

import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProgrammeSettingsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public static volatile ProgrammeSettingsTransaction programmeSettingsTransaction = null;
	public static ProgrammeSettingsTransaction getInstance() {
		if(programmeSettingsTransaction == null) {
			programmeSettingsTransaction = new ProgrammeSettingsTransaction();
		}
		return programmeSettingsTransaction;
	}
	
	public AdmProgrammeSettingsDBO editById(int id) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<AdmProgrammeSettingsDBO>() {
			@Override
            public AdmProgrammeSettingsDBO onRun(EntityManager context) throws Exception {
				AdmProgrammeSettingsDBO dbo = null;
				try{
					dbo = context.find(AdmProgrammeSettingsDBO.class, id);	
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
	
	public boolean saveOrUpdate(AdmProgrammeSettingsDBO dbo) throws Exception {
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
	
	public AdmProgrammeSettingsDBO editByProgramme(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmProgrammeSettingsDBO>() {
			@Override
			public AdmProgrammeSettingsDBO onRun(EntityManager context) throws Exception {
				AdmProgrammeSettingsDBO dbo = null;
				try{
					 Query query = context.createQuery("from AdmProgrammeSettingsDBO bo where bo.id=:Id and bo.recordStatus='A'");
					 query.setParameter("Id", id);
					 dbo = (AdmProgrammeSettingsDBO) Utils.getUniqueResult(query.getResultList());
				}
				catch (Exception e) {
					//e.printStackTrace();
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
	
	public List<ErpCampusProgrammeMappingDBO> getLocationByProgramme(Integer programmeId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpCampusProgrammeMappingDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpCampusProgrammeMappingDBO>  onRun(EntityManager context) throws Exception {
				List<ErpCampusProgrammeMappingDBO>  mapping = null;
				try{
					 Query query = context.createQuery(" from ErpCampusProgrammeMappingDBO bo where bo.recordStatus='A' and bo.erpProgrammeDBO.id =:ProgrammeId ");
					 query.setParameter("ProgrammeId", programmeId);
					 mapping = query.getResultList();
				}catch (Exception e) {
					e.printStackTrace();
				}
				return mapping;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}

	public List<AdmAdmissionTypeDBO> getAdmissionType() {
		String queryString = "select dbo from AdmAdmissionTypeDBO dbo where dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(queryString, AdmAdmissionTypeDBO.class).getResultList()).await().indefinitely();
	}

	public Integer getAcademic(Integer year) {
		String queryString = "select erp_academic_year.erp_academic_year_id from erp_academic_year where academic_year  = :year and record_status = 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Integer.class).setParameter("year", year).getSingleResultOrNull()).await().indefinitely();
	}

	public List<AdmProgrammeSettingsDBO> duplicateCheck(AdmProgrammeSettingsDTO admProgrammeSettingsDTO, Set<Integer> ids) {
		String queryString = "select distinct dbo from AdmProgrammeSettingsDBO dbo "
				+ " inner join fetch dbo.admProgrammeBatchDBOSet apb"
				+ " left join fetch apb.erpCampusProgrammeMappingDBO ecpm"
				+ " where dbo.recordStatus = 'A'  "
				+ " and dbo.admBatchYear.id = :batchId  and dbo.erpProgrammeDBO.id  = :progId "
				+ "  and dbo.preferenceOption = :pre";
		if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getAdmissionType())){
			queryString +=" and dbo.admAdmissionTypeDBO.id = :typeId";
		}
		if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getIntakeBatch()) && !Utils.isNullOrEmpty(admProgrammeSettingsDTO.getIntakeBatch().getValue())){
			queryString += " and dbo.admIntakeBatchDBO.id = :sessionId ";
		}
		if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.id)){
			queryString +=" and dbo.id != :id";
		}
		if(!Utils.isNullOrEmpty(ids)){
			queryString +=" and ecpm.id in (:ids)";
		}
		String finalquery = queryString;
		List<AdmProgrammeSettingsDBO> value = sessionFactory.withSession( s-> { Mutiny.Query<AdmProgrammeSettingsDBO> str = s.createQuery(finalquery,AdmProgrammeSettingsDBO.class);
			str.setParameter("batchId",Integer.parseInt(admProgrammeSettingsDTO.getErpAcademicYear().getValue()));
			if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getIntakeBatch()) && !Utils.isNullOrEmpty(admProgrammeSettingsDTO.getIntakeBatch().getValue())){
				str.setParameter("sessionId",Integer.parseInt(admProgrammeSettingsDTO.getIntakeBatch().getValue()));
			}
			if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getProgramme())) {
				str.setParameter("progId",Integer.parseInt(admProgrammeSettingsDTO.getProgramme().getValue()));
			}
			if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.getAdmissionType())) {
				str.setParameter("typeId",Integer.parseInt(admProgrammeSettingsDTO.getAdmissionType().getValue()));
			}
			str.setParameter("pre",admProgrammeSettingsDTO.getPreferenceBasedOn());
			if(!Utils.isNullOrEmpty(admProgrammeSettingsDTO.id)) {
				str.setParameter("id",Integer.parseInt(admProgrammeSettingsDTO.id));
			}
			if(!Utils.isNullOrEmpty(ids)) {
				str.setParameter("ids",ids);
			}
			return str.getResultList();
		}).await().indefinitely();
		return value;
	}
    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmProgrammeSettingsDBO.class, id)
        		.chain(dbo -> session.fetch(dbo.getAdmProgrammeQualificationSettingsSetDbos())
        		.invoke(subSet -> {
        			subSet.forEach(subDbo -> {
        				subDbo.setRecordStatus('D');
        				subDbo.setModifiedUsersId(userId);
        			});
        		}).chain(dbo2 -> session.fetch(dbo.getAdmProgrammeFeePaymentModeDBOSet()))
        			.invoke(subSet2 -> {
        				subSet2.forEach(subDbo -> {
            				subDbo.setRecordStatus('D');
            				subDbo.setModifiedUsersId(userId);
            			});
        			})
        			.chain(dbo3 -> session.fetch(dbo.getAdmProgrammeBatchDBOSet()))
        			.invoke(subSet3 -> {
        				subSet3.forEach(subDbo -> {
            				subDbo.setRecordStatus('D');
            				subDbo.setModifiedUsersId(userId);
            			});
        			})
        			.chain(dbo4 -> session.fetch(dbo.getAdmProgrammeBatchPreferencesDBOSet()))
        			.invoke(subSet4 -> {
        				subSet4.forEach(subDbo -> {
            				subDbo.setRecordStatus('D');
            				subDbo.setModifiedUsersId(userId);
            			});
        			})
        		.chain(dbo1 -> session.fetch(dbo.getAdmProgrammeDocumentSettingSetDbos()))
        		.invoke(subSet1 -> {
        			subSet1.forEach(subDbo1 -> {
        				subDbo1.setRecordStatus('D');
        				subDbo1.setModifiedUsersId(userId);
        			});
        			dbo.setRecordStatus('D');
        			dbo.setModifiedUsersId(userId);
        		})
        		)).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public Mono<List<AdmProgrammeSettingsDBO>> getGridData(Integer yearId, String programmeId, String intakeBatchId) {
		String str = "select distinct dbo from AdmProgrammeSettingsDBO dbo" +
				" left join fetch dbo.admProgrammeBatchDBOSet batchDbo" +
				" left join fetch batchDbo.erpCampusProgrammeMappingDBO" +
				" left join fetch dbo.admAdmissionTypeDBO" +
				" left join fetch dbo.erpAcademicYearDBO" +
				" left join fetch dbo.admIntakeBatchDBO" +
				" where dbo.recordStatus='A' and dbo.erpAcademicYearDBO.id =:yearId ";
		if(!Utils.isNullOrEmpty(programmeId)) {
			str += " and dbo.erpProgrammeDBO.id =:programmeId ";
		}
		if(!Utils.isNullOrEmpty(intakeBatchId)) {
			str += " and dbo.admIntakeBatchDBO.id =:intakeBatchId ";
		}
		String finalStr= str;
		Mono<List<AdmProgrammeSettingsDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<AdmProgrammeSettingsDBO> query = s.createQuery(finalStr, AdmProgrammeSettingsDBO.class);
			query.setParameter("yearId", yearId);
			if(!Utils.isNullOrEmpty(programmeId)) {
				query.setParameter("programmeId", Integer.parseInt(programmeId));
			}
			if(!Utils.isNullOrEmpty(intakeBatchId)) {
				query.setParameter("intakeBatchId", Integer.parseInt(intakeBatchId));
			}
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
    }

    public List<AdmProgrammeBatchPreferencesDBO> getOtherPreference(String settingsId) {
		String str = "select distinct dbo from AdmProgrammeBatchPreferencesDBO dbo" +
				" left join fetch dbo.admProgrammeBatchDBO batch" +
				" left join fetch batch.admProgrammeSettingsDBO" +
				" left join fetch batch.erpCampusProgrammeMappingDBO" +
				" where dbo.recordStatus='A' and  dbo.admProgrammeSettingsDBO.id =:settingsId";
		return sessionFactory.withSession(s-> s.createQuery(str, AdmProgrammeBatchPreferencesDBO.class).setParameter("settingsId",Integer.parseInt(settingsId)).getResultList()).await().indefinitely();
    }

	public Mono<List<AdmProgrammeSettingsDBO>> getOtherPreferenceProgrammeList(AdmProgrammeSettingsDTO dto) {
		String str = "select dbo from AdmProgrammeSettingsDBO dbo" +
				" where dbo.recordStatus='A' and dbo.erpAcademicYearDBO.id =:yearId and " +
				" dbo.admAdmissionTypeDBO.id =:typeId and dbo.id !=:settingId" +
				" and dbo.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id = (select bo.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id from AdmProgrammeSettingsDBO bo where bo.id = :settingId)";
		if(!Utils.isNullOrEmpty(dto.getIntakeBatch()) && !Utils.isNullOrEmpty(dto.getIntakeBatch().getValue())) {
			str += " and dbo.admIntakeBatchDBO.id =:intakeBatchId";
		}
		str += " order by dbo.erpProgrammeDBO.programmeNameForApplication";
		String finalStr = str;
		return Mono.fromFuture(sessionFactory.withSession(s-> {
			Mutiny.Query<AdmProgrammeSettingsDBO> query = s.createQuery(finalStr, AdmProgrammeSettingsDBO.class);
			query.setParameter("yearId",dto.getErpAcademicYear().getId());
			if(!Utils.isNullOrEmpty(dto.getIntakeBatch()) && !Utils.isNullOrEmpty(dto.getIntakeBatch().getValue())) {
				query.setParameter("intakeBatchId", Integer.parseInt(dto.getIntakeBatch().getValue()));
			}
			query.setParameter("typeId", Integer.parseInt(dto.getAdmissionType().getValue()));
			query.setParameter("settingId", Integer.parseInt(dto.id));
			return query.getResultList();
		}).subscribeAsCompletionStage());
	}

	public Mono<List<AdmProgrammeBatchDBO>> getProgrammeBatchBySettingId(String settingId) {
		String str = "select dbo from AdmProgrammeBatchDBO dbo" +
				" left join fetch dbo.erpCampusProgrammeMappingDBO " +
				" where dbo.recordStatus='A' and dbo.admProgrammeSettingsDBO.id =:settingId";
		return Mono.fromFuture(sessionFactory.withSession(s-> s.createQuery(str, AdmProgrammeBatchDBO.class)
				.setParameter("settingId",Integer.parseInt(settingId)).getResultList()).subscribeAsCompletionStage());
	}

	public void update(List<AdmProgrammeBatchPreferencesDBO> preferenceList) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(preferenceList.toArray())).subscribeAsCompletionStage();
	}

	public Tuple getIntakeBatch(SelectDTO programme, ErpAcademicYearDTO year) {
		String str = " select distinct adm_intake_batch.adm_intake_batch_id as id ,adm_intake_batch.adm_intake_batch_name  as name from erp_programme " +
				" inner join erp_programme_batchwise_settings on erp_programme.erp_programme_id = erp_programme_batchwise_settings.erp_programme_id and erp_programme_batchwise_settings.record_status = 'A' " +
				" inner join aca_session_type ON aca_session_type.aca_session_type_id = erp_programme_batchwise_settings.aca_session_type_id  and aca_session_type.record_status = 'A' " +
				" inner join  aca_session_group on aca_session_type.aca_session_type_id = aca_session_group.aca_session_type_id and aca_session_group.record_status = 'A'" +
				" inner join " +
				"         ( " +
				"           select aca_session_group.aca_session_group_id as aca_session_group_id1 ,min_ses.aca_session_type_id as aca_session_type_id1 ,min_ses.record_status as record_status1 from aca_session_group" +
				"           inner join (" +
				"            SELECT aca_session_group_id, aca_session_group.aca_session_type_id, aca_session_group.record_status, MIN(aca_session_group.session_number) " +
				"            OVER (  PARTITION BY aca_session_group.aca_session_type_id  ) AS min_" +
				"            FROM aca_session_group" +
				"            ) as min_ses ON min_ses.aca_session_group_id = aca_session_group.aca_session_group_id" +
				"            and aca_session_group.session_number = min_ses.min_" +
				"          ) as bb on bb.aca_session_type_id1 = aca_session_type.aca_session_type_id   and   bb.record_status1 = 'A' " +
				" inner join adm_intake_batch on adm_intake_batch.aca_session_group_id = bb.aca_session_group_id1 and adm_intake_batch.record_status = 'A'" +
				" where erp_programme.record_status = 'A' and erp_programme.erp_programme_id = :programmeId and aca_session_type.total_session_intakes_in_year = 1 " +
				" and erp_programme_batchwise_settings.batch_year_id = :yearId  ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("programmeId", Integer.parseInt(programme.getValue()))
				.setParameter("yearId",Integer.parseInt(year.getValue())).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> programmeExit(int id) {
		String str = "select adm_selection_process_plan_programme.adm_selection_process_plan_programme_id from adm_selection_process_plan" +
				" inner join adm_selection_process_plan_programme on adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_programme.adm_selection_process_plan_id" +
				" and adm_selection_process_plan_programme.record_status = 'A'" +
				" inner join adm_programme_batch on adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id and adm_programme_batch.record_status = 'A'" +
				" inner join adm_programme_settings on adm_programme_batch.adm_programme_settings_id = adm_programme_settings.adm_programme_settings_id and adm_programme_settings.record_status = 'A'" +
				" where adm_programme_settings.adm_programme_settings_id = :id  and adm_selection_process_plan.record_status = 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("id",id).getResultList()).await().indefinitely();
	}

	public List<Tuple> getCheckProg( Integer progSettingId, List<Integer> campOrlocIds) {
		String str= " select adm_selection_process_plan_programme.adm_selection_process_plan_programme_id from  adm_selection_process_plan_programme" +
				" inner join adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id " +
				" and adm_programme_batch.record_status = 'A' " +
				" where adm_selection_process_plan_programme.record_status = 'A' and" +
				" adm_programme_batch.adm_programme_settings_id = :progSettingId and adm_programme_batch.erp_campus_programme_mapping_id in (:campOrlocIds)";
		return sessionFactory.withSession(session -> session.createNativeQuery(str, Tuple.class).setParameter("progSettingId",progSettingId)
				.setParameter("campOrlocIds",campOrlocIds).getResultList()).await().indefinitely();
	}

}
