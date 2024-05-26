package com.christ.erp.services.transactions.regulations;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessScoreDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.common.RegulationCategoryDBO;
import com.christ.erp.services.dbobjects.common.RegulationUploadDownloadDBO;
import com.christ.erp.services.dbobjects.common.UrlUploadedFilesDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDatewiseTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.common.RegulationEntriesDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class RegulationDocumentUploadTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<AdmSelectionProcessTypeDBO>> getEntranceTest() {
        String query = " select dbo from AdmSelectionProcessTypeDBO dbo where dbo.recordStatus = 'A' and dbo.mode in ('Center Based Entrance','Online Entrance')";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, AdmSelectionProcessTypeDBO.class).getResultList()).subscribeAsCompletionStage());
    }
    public void save (List<AdmSelectionProcessScoreDBO> dbos) {
        sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
    }
    public void save (UrlUploadedFilesDBO dbo) {
        sessionFactory.withTransaction((session, txt) -> session.persist(dbo)).subscribeAsCompletionStage();

    }
    public boolean saveRegulations (RegulationUploadDownloadDBO dbo) {
        boolean flag = false;
        if(Utils.isNullOrEmpty(dbo.getId()))
        {
            sessionFactory.withTransaction((session, txt) -> session.persist(dbo)).await().indefinitely();
            flag = true;
        }
        sessionFactory.withTransaction((session, txt) -> session.merge(dbo)).await().indefinitely();
        return flag;
    }
    public Mono<List<RegulationUploadDownloadDBO>> getGridData(SelectDTO regulationDocCategory, List<String> typeKeyWords) {

        if(!Utils.isNullOrEmpty(regulationDocCategory))
        {
            int categoryId = Integer.parseInt(regulationDocCategory.getValue());
            String query = "select distinct dbo from RegulationUploadDownloadDBO dbo left join fetch"
                          + " dbo.urlAccessLinkDBO AS access_link_dbo where dbo.recordStatus='A' and dbo.regulationCategoryDBO.id =:categoryId";
            List<RegulationUploadDownloadDBO>  regulationUploadDownloadDBOList =
                    sessionFactory.withSession(s->{
                        Mutiny.Query<RegulationUploadDownloadDBO> query1 = s.createQuery(query, RegulationUploadDownloadDBO.class);
                        if(categoryId>0)
                        {
                            query1.setParameter("categoryId", categoryId);
                        }
                        return query1.getResultList();
                    }).await().indefinitely();
            return Mono.justOrEmpty(regulationUploadDownloadDBOList);
        }
        if(!Utils.isNullOrEmpty(typeKeyWords))
        {
            String query = "select distinct dbo from RegulationUploadDownloadDBO dbo left join fetch"
                    + " dbo.urlAccessLinkDBO AS access_link_dbo where dbo.recordStatus='A' and dbo.regulationDocCategory IN(:typeKeyWords)";
            List<RegulationUploadDownloadDBO>  regulationUploadDownloadDBOList =
                    sessionFactory.withSession(s->{
                        Mutiny.Query<RegulationUploadDownloadDBO> query2 = s.createQuery(query, RegulationUploadDownloadDBO.class);
                        if(!Utils.isNullOrEmpty(typeKeyWords))
                        {
                            query2.setParameter("typeKeyWords", typeKeyWords);
                        }
                        return query2.getResultList();
                    }).await().indefinitely();
            return Mono.justOrEmpty(regulationUploadDownloadDBOList);
        }
            String query = "select distinct dbo from RegulationUploadDownloadDBO dbo left join fetch"
                    + " dbo.urlAccessLinkDBO AS access_link_dbo where dbo.recordStatus='A'";
            List<RegulationUploadDownloadDBO> regulationUploadDownloadDBOList =
                    sessionFactory.withSession(s -> {
                        Mutiny.Query<RegulationUploadDownloadDBO> query3 = s.createQuery(query, RegulationUploadDownloadDBO.class);
                        return query3.getResultList();
                    }).await().indefinitely();
        return Mono.justOrEmpty(regulationUploadDownloadDBOList);
    }
    public RegulationUploadDownloadDBO getExistData(int id) {

        String query = "select dbo from RegulationUploadDownloadDBO dbo"
                + " left join fetch dbo.urlAccessLinkDBO AS access_link_dbo"
                + " where dbo.recordStatus='A' and dbo.id=:id";

        return sessionFactory.withSession(s -> s.createQuery(query, RegulationUploadDownloadDBO.class)
                .setParameter("id", id)
                .getSingleResultOrNull()).await().indefinitely();
    }
    public RegulationCategoryDBO getCategoryDBO(String categoryName) {

        String query = "select cdbo from RegulationCategoryDBO cdbo"
                     + " where cdbo.recordStatus='A' and cdbo.regulationCategoryName=:categoryName";

        return sessionFactory.withSession(s -> s.createQuery(query, RegulationCategoryDBO.class)
                .setParameter("categoryName", categoryName)
                .getSingleResultOrNull()).await().indefinitely();
    }
    public boolean delete(Integer id) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                RegulationUploadDownloadDBO dbo = null;
                if (!Utils.isNullOrEmpty(id)) {

                    dbo = context.find(RegulationUploadDownloadDBO.class, id);
                    if (!Utils.isNullOrEmpty(dbo)) {
                        dbo.setRecordStatus('D');
                        //dbo.getRegulationCategoryDBO().setRecordStatus('D');
                        if(!Utils.isNullOrEmpty(dbo.getUrlAccessLinkDBO()))
                        {
                            dbo.getUrlAccessLinkDBO().setRecordStatus('D');
                        }
                        context.merge(dbo);
                        return true;
                    }

                }
                 return false;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
            public Tuple getScoreCardId(int selectionTypeId) {
                String query = " select adm_scorecard_id ,adm_selection_process_type_details_id from adm_selection_process_type_details where adm_selection_process_type_id = :selectionTypeId and record_status = 'A'";
                return sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("selectionTypeId", selectionTypeId).getSingleResultOrNull()).await().indefinitely();
            }
            public List<Tuple> scorecardQuantitativeCheck(int scoreCardId) {
                String query = " select adm_scorecard_quantitative_parameter_id,max_value from adm_scorecard_quantitative_parameter where adm_scorecard_quantitative_parameter.adm_scorecard_id = :scoreCardId and record_status = 'A'";
                return sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("scoreCardId", scoreCardId).getResultList()).await().indefinitely();
            }
            public List<String> scorecardQualitativeCheck(int scoreCardId) {
                String query = " select adm_scorecard_qualitative_parameter.adm_qualitative_parameter_id from adm_scorecard_qualitative_parameter where adm_scorecard_qualitative_parameter.adm_scorecard_id = :scoreCardId  and record_status = 'A'";
                return sessionFactory.withSession(s -> s.createNativeQuery(query, String.class).setParameter("scoreCardId", scoreCardId).getResultList()).await().indefinitely();
            }
            public List<StudentApplnEntriesDBO> getApplicantsDetails(List<Integer> applicationNos) {
                String query = " select dbo from StudentApplnEntriesDBO dbo where dbo.recordStatus = 'A'  and dbo.applicationNo in( :applicationNos ) ";
                return sessionFactory.withSession(s -> s.createQuery(query, StudentApplnEntriesDBO.class).setParameter("applicationNos", applicationNos).getResultList()).await().indefinitely();
            }
            public List<AdmSelectionProcessDBO> getApplicantsSelectionProcessDetails(List<Integer> applicationNos, int yearId) {
                String query = "select dbo from AdmSelectionProcessDBO dbo "
                        + " left join fetch dbo.studentApplnEntriesDBO AS sdbo "
                        + " left join fetch dbo.admSelectionProcessPlanDetailDBO AS sppdbo "
                        + " where dbo.recordStatus = 'A' and  dbo.erpAcademicYearDBO.id = :yearId and sdbo.applicationNo in( :applicationNos )  ";
                return sessionFactory.withSession(s -> s.createQuery(query, AdmSelectionProcessDBO.class).setParameter("applicationNos", applicationNos)
                        .setParameter("yearId", yearId).getResultList()).await().indefinitely();
            }
            public List<AdmSelectionProcessScoreDBO> getAdmSelectionProcessScoreDBODetails(List<Integer> selectionProcessId, Integer selectionProcessTypeDeatilsId) {
                String str = "select dbo from AdmSelectionProcessScoreDBO dbo "
                        + " left join fetch dbo.AdmSelectionProcessScoreEntryDBOSet AS aasdbs "
                        + " left join fetch aasdbs.AdmSelectionProcessScoreEntryDetailsDBOSet AS aastbs "
                        + " where dbo.recordStatus = 'A' and aasdbs.recordStatus = 'A' and  aastbs.recordStatus = 'A'"
                        + " and dbo.admSelectionProcessDBO.id in ( :selectionProcessId) "
                        + " and dbo.admSelectionProcessTypeDetailsDBO.id = :selectionProcessTypeDeatilsId "
                        + " ORDER BY dbo.id ";
                return sessionFactory.withSession(s -> s.createQuery(str, AdmSelectionProcessScoreDBO.class).setParameter("selectionProcessId", selectionProcessId).setParameter("selectionProcessTypeDeatilsId", selectionProcessTypeDeatilsId).getResultList()).await().indefinitely();

            }
            public Tuple regulationDocumentUploadDownloadFormat() {
                String str = " select url_access_link.file_name_unique as fileNameUnique, url_access_link.file_name_original as fileNameOriginal,"
                        + "url_folder_list.upload_process_code as uploadProcessCode from url_folder_list "
                        + "inner join url_access_link On url_access_link.url_folder_list_id = url_folder_list.url_folder_list_id "
                        + "inner join erp_bulk_upload_format On erp_bulk_upload_format.bulk_upload_format_url_id = url_access_link.url_access_link_id "
                        + "where erp_bulk_upload_format.bulk_upload_format_name=:name  "
                        + "and url_folder_list.record_status='A' and url_access_link.record_status='A' and erp_bulk_upload_format.record_status='A' ";
                Tuple list = (Tuple) sessionFactory.withSession(s -> {
                    Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
                    query.setParameter("name", "ENTRANCE_RESULT_UPLOAD");
                    return query.getSingleResult();
                }).await().indefinitely();
                return list;
            }
    public Mono<List<RegulationUploadDownloadDBO>> getRegulationCategoryDropdownData(){
        String query = " select dbo from RegulationUploadDownloadDBO dbo where dbo.recordStatus = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, RegulationUploadDownloadDBO.class).getResultList()).subscribeAsCompletionStage());
    }

}


