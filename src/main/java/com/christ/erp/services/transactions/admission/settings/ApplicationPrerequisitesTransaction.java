package com.christ.erp.services.transactions.admission.settings;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmPrerequisiteSettingsDBO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class ApplicationPrerequisitesTransaction {
//	private static volatile ApplicationPrerequisitesTransaction  applicationPrerequisitesTransaction=null;

    //	public static ApplicationPrerequisitesTransaction getInstance() {
//		if(applicationPrerequisitesTransaction==null) {
//			applicationPrerequisitesTransaction = new ApplicationPrerequisitesTransaction();
//        }
//        return applicationPrerequisitesTransaction;
//	}
    @Autowired
    private Mutiny.SessionFactory sessionFactory;

//	public Boolean duplicateCheck(AdmPrerequisiteSettingsDTO admPrerequisiteSettingsDTO) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
//			@Override
//			public Boolean onRun(EntityManager context) throws Exception {
//				boolean flag;
//				String str ="SELECT * FROM adm_prerequisite_settings where record_status='A' and erp_academic_year_id=:academicYearId and erp_programme_id=:programmeId and adm_admission_type_id =:typeId ";
//				Query q = context.createNativeQuery(str, Tuple.class);
//				q.setParameter("academicYearId", admPrerequisiteSettingsDTO.academicYear.id);
//	            q.setParameter("programmeId", admPrerequisiteSettingsDTO.getErpProgrammeDTO().getValue()programme.id);
//				q.setParameter("typeId", Integer.parseInt(admPrerequisiteSettingsDTO.getAdmAdmissionTypeDTO().getValue()));
//	            int length = q.getResultList().size();
//				flag = length > 0;
//			return flag;
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public boolean saveOrUpdate(Set<AdmPrerequisiteSettingsDBO> dboList) throws Exception {
//		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
//			@Override
//			public Boolean onRun(EntityManager context) {
//				dboList.forEach(dbo -> {
//					if (Utils.isNullOrEmpty(dbo.getId())) {
//						context.persist(dbo);
//					} else {
//						context.merge(dbo);
//					}
//				});
//				return true;
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public boolean delete(AdmPrerequisiteSettingsDTO data) throws Exception {
//		return DBGateway.runJPA(new ICommitTransactional() {
//			@Override
//			public boolean onRun(EntityManager context) {
//				Query query = context.createNativeQuery(
//						"update adm_prerequisite_settings set record_status='D' where erp_academic_year_id=:academicYearId and erp_programme_id=:programmeId and adm_admission_type_id =:typeId ");
//				query.setParameter("academicYearId", data.academicYear.id);
//				query.setParameter("programmeId", data.programme.id);
//				query.setParameter("typeId", Integer.parseInt(data.getAdmAdmissionTypeDTO().getValue()));
//				return query.executeUpdate() > 0 ? true : false;
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public List<Tuple> getGridDat() throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "select DISTINCT erp_academic_year.erp_academic_year_id as academicYearId," +
//						" erp_academic_year.academic_year_name as academicYearName," +
//						" erp_programme.erp_programme_id as programmeId,erp_programme.programme_name as programmeName," +
//						" adm_admission_type.admission_type, adm_admission_type.adm_admission_type_id" +
//						" from adm_prerequisite_settings" +
//						" inner join erp_academic_year ON erp_academic_year.erp_academic_year_id = adm_prerequisite_settings.erp_academic_year_id" +
//						" inner join erp_programme on erp_programme.erp_programme_id = adm_prerequisite_settings.erp_programme_id" +
//						" inner join adm_admission_type on adm_admission_type.adm_admission_type_id = adm_prerequisite_settings.adm_admission_type_id " +
//						" where adm_prerequisite_settings.record_status = 'A' order by erp_academic_year.academic_year_name DESC";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				return query.getResultList();
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public List<Tuple> getPrerequisiteExams( String academicYearId, String programmeId) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "select DISTINCT adm_prerequisite_settings.adm_prerequisite_settings_id as Id, adm_prerequisite_exam.adm_prerequisite_exam_id as prerequisiteExamId,  \n" +
//						" adm_prerequisite_exam.exam_name as prerequisiteExamName, \n" +
//						" adm_prerequisite_settings.min_marks as minMarks,  \n" +
//						" adm_prerequisite_settings.min_marks_for_christite as minMarksChristite,  \n" +
//						" adm_prerequisite_settings.total_marks as totalMarks,  \n" +
//						" adm_prerequisite_settings.is_exam_mandatory as mandatory,\n" +
//						" adm_prerequisite_settings.erp_location_id as locationId,\n" +
//						" erp_location.location_name as locationName, erp_programme.programme_name as programmeName," +
//						" adm_prerequisite_settings.adm_prerequisite_settings_id as id," +
//						" adm_admission_type.adm_admission_type_id as typeId, adm_admission_type.admission_type as admissionType," +
//						" adm_prerequisite_settings.is_score_details_mandatory , adm_prerequisite_settings.is_document_upload_mandatory , adm_prerequisite_settings.is_register_no_mandatory " +
//						" from adm_prerequisite_settings  \n" +
//						" inner join adm_prerequisite_exam on adm_prerequisite_settings.adm_prerequisite_exam_id = adm_prerequisite_exam.adm_prerequisite_exam_id  \n" +
//						" inner join erp_location on adm_prerequisite_settings.erp_location_id = erp_location.erp_location_id \n" +
//						" inner join erp_programme ON erp_programme.erp_programme_id = adm_prerequisite_settings.erp_programme_id" +
//						" inner join adm_admission_type on adm_prerequisite_settings.adm_admission_type_id = adm_admission_type.adm_admission_type_id"+
//						" where adm_prerequisite_settings.erp_academic_year_id =:academicYearId and adm_prerequisite_settings.erp_programme_id =:programmeId  \n" +
//						" and adm_prerequisite_settings.record_status ='A' group by adm_prerequisite_settings.adm_prerequisite_settings_id";
//
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("academicYearId", academicYearId);
//				query.setParameter("programmeId", programmeId);
//		//		query.setParameter("typeId", Integer.parseInt(data.getAdmAdmissionTypeDTO().getValue()));
//				return query.getResultList();
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public List<Tuple> getLocation(String academicYearId,String programmeId) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "select DISTINCT erp_location.erp_location_id as locationId,erp_location.location_name as locationName,adm_prerequisite_settings_id as id from adm_prerequisite_settings  \n" +
//						" inner join erp_location ON erp_location.erp_location_id = adm_prerequisite_settings.erp_location_id  \n" +
//						" where adm_prerequisite_settings.erp_academic_year_id =:academicYearId and  \n" +
//						" adm_prerequisite_settings.erp_programme_id =:programmeId and  \n" +
//						" adm_prerequisite_settings.record_status = 'A'";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("academicYearId", academicYearId);
//				query.setParameter("programmeId", programmeId);
//				return query.getResultList();
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}

//	public List<Tuple> getPeriodList(int id) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = " select distinct adm_prerequisite_settings_period_id as id,adm_prerequisite_settings_period.exam_year as years,adm_prerequisite_settings_period.exam_month as months from adm_prerequisite_settings_period  \n" +
//						"inner join adm_prerequisite_settings on adm_prerequisite_settings.adm_prerequisite_settings_id = adm_prerequisite_settings_period.adm_prerequisite_settings_id  \n" +
//						"where adm_prerequisite_settings.adm_prerequisite_settings_id =:id and    \n" +
//						"adm_prerequisite_settings_period.record_status = 'A' and  \n" +
//						"adm_prerequisite_settings.record_status = 'A' order by adm_prerequisite_settings_period.exam_year,adm_prerequisite_settings_period.exam_month";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("id",id);
//				return query.getResultList();
//			}
//
//			@Override
//			public void onError(Exception error) throws Exception {
//
//			}
//		});
//	}

//	public boolean deleteLocation(Tuple filterLocId) throws Exception {
//		return  DBGateway.runJPA(new ICommitTransactional() {
//			@Override
//			public boolean onRun(EntityManager context) {
//				Query query = context.createNativeQuery(
//						"update adm_prerequisite_settings,adm_prerequisite_settings_period set adm_prerequisite_settings.record_status='D',adm_prerequisite_settings_period.record_status='D' where adm_prerequisite_settings.adm_prerequisite_settings_id=:id and adm_prerequisite_settings_period.adm_prerequisite_settings_id=:id");
//				query.setParameter("id", filterLocId.get("id"));
//				return query.executeUpdate() > 0 ? true : false;
//			}
//
//			@Override
//			public void onError(Exception error){
//
//			}
//		});
//	}

//	public boolean deletePeriod(Tuple filterPerId) throws Exception {
//		return DBGateway.runJPA(new ICommitTransactional() {
//			@Override
//			public boolean onRun(EntityManager context) {
//				Query query = context.createNativeQuery(
//						"update adm_prerequisite_settings_period set adm_prerequisite_settings_period.record_status='D' where adm_prerequisite_settings_period.adm_prerequisite_settings_period_id=:id");
//				query.setParameter("id", filterPerId.get("id"));
//				return query.executeUpdate() > 0 ? true : false;
//			}
//
//			@Override
//			public void onError(Exception error){
//
//			}
//		});
//	}

    //	public String getProgrammeNameById(String programmeId) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<String>() {
//			@Override
//			public String onRun(EntityManager context) throws Exception {
//				String str = " select erp_programme.programme_name as programeName from erp_programme "
//					        +" where erp_programme.erp_programme_id =:programmeId and erp_programme.record_status = 'A'";
//				Query query = context.createNativeQuery(str);
//				query.setParameter("programmeId",Integer.parseInt(programmeId));
//				return ((String) Utils.getUniqueResult(query.getResultList())).toString();
//			}
//
//			@Override
//			public void onError(Exception error) throws Exception {
//
//			}
//		});
//	}
    public Mono<Boolean> delete(int id, String userId) {
        sessionFactory.withTransaction((session, tx) -> session.createQuery("select dbo from AdmPrerequisiteSettingsDBO dbo" +
                        " left join fetch dbo.admPrerequisiteSettingsDetailsDBOSet detailSet" +
                        " left join fetch detailSet.admPrerequisiteSettingsDetailsPeriodDBOSet " +
                        "where dbo.id =:id ",AdmPrerequisiteSettingsDBO.class).setParameter("id",id).getSingleResultOrNull()
                .chain(bo -> session.fetch(bo.getAdmPrerequisiteSettingsDetailsDBOSet())
                        .invoke(subDbo -> {
                            subDbo.forEach(dbos -> {
                                dbos.setRecordStatus('D');
                                dbos.setModifiedUsersId(Integer.valueOf(userId));
                                dbos.getAdmPrerequisiteSettingsDetailsPeriodDBOSet().forEach(periodDbo -> {
                                    periodDbo.setRecordStatus('D');
                                    periodDbo.setModifiedUsersId(Integer.valueOf(userId));
                                });
                            });
                            bo.setRecordStatus('D');
                            bo.setModifiedUsersId(Integer.valueOf(userId));
                        }))).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public Mono<List<AdmPrerequisiteSettingsDBO>> getGridData(Integer yearId, String programmeId, String typeId) {
        String str = "select dbo from AdmPrerequisiteSettingsDBO dbo" +
                " where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =:yearId ";
        if (!Utils.isNullOrEmpty(programmeId)) {
            str += " and dbo.erpProgrammeDBO.id =:programmeId ";
        }
        if (!Utils.isNullOrEmpty(typeId)) {
            str += " and dbo.admAdmissionTypeDBO.id =:typeId ";
        }
        String finalStr = str;
        return Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<AdmPrerequisiteSettingsDBO> query = s.createQuery(finalStr, AdmPrerequisiteSettingsDBO.class);
            query.setParameter("yearId", yearId);
            if (!Utils.isNullOrEmpty(programmeId)) {
                query.setParameter("programmeId", Integer.parseInt(programmeId));
            }
            if (!Utils.isNullOrEmpty(typeId)) {
                query.setParameter("typeId", Integer.parseInt(typeId));
            }
            return query.getResultList();
        }).subscribeAsCompletionStage());
    }

    public AdmPrerequisiteSettingsDBO edit(Integer id) {
        String str = " select dbo from AdmPrerequisiteSettingsDBO dbo " +
                " left join fetch dbo.admPrerequisiteSettingsDetailsDBOSet as detailDbo" +
                " left join fetch detailDbo.admPrerequisiteSettingsDetailsPeriodDBOSet" +
                " where dbo.recordStatus ='A' and dbo.id = :id ";
        return sessionFactory.withSession(s -> s.createQuery(str, AdmPrerequisiteSettingsDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }

    public boolean duplicateCheck(AdmPrerequisiteSettingsDTO admPrerequisiteSettingsDTO) {
        String str = "select * from adm_prerequisite_settings where adm_prerequisite_settings.record_status='A' and adm_prerequisite_settings.erp_academic_year_id=:academicYearId and adm_prerequisite_settings.erp_programme_id=:programmeId and adm_prerequisite_settings.adm_admission_type_id =:typeId ";
        if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.getId())) {
            str += " and adm_prerequisite_settings.adm_prerequisite_settings_id != :id";
        }
        String finalStr = str;
        List<AdmPrerequisiteSettingsDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<AdmPrerequisiteSettingsDBO> query = s.createNativeQuery(finalStr, AdmPrerequisiteSettingsDBO.class);
            query.setParameter("academicYearId", Integer.parseInt(admPrerequisiteSettingsDTO.getErpAcademicYearDTO().getValue()));
            query.setParameter("programmeId", Integer.parseInt(admPrerequisiteSettingsDTO.getErpProgrammeDTO().getValue()));
            query.setParameter("typeId", Integer.parseInt(admPrerequisiteSettingsDTO.getAdmAdmissionTypeDTO().getValue()));
            if (!Utils.isNullOrEmpty(admPrerequisiteSettingsDTO.getId())) {
                query.setParameter("id", admPrerequisiteSettingsDTO.getId());
            }
            return query.getResultList();
        }).await().indefinitely();
        return !Utils.isNullOrEmpty(list);
    }


    public void update(AdmPrerequisiteSettingsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
    }

    public void save(AdmPrerequisiteSettingsDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }
}
