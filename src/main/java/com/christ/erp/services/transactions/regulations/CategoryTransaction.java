package com.christ.erp.services.transactions.regulations;

import com.christ.erp.services.dbobjects.common.RegulationCategoryDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.List;

@Repository
public class CategoryTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<RegulationCategoryDBO>> getGridData() {
        String query = "select cbo from RegulationCategoryDBO cbo  where cbo.recordStatus='A'";

        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, RegulationCategoryDBO.class)
                   .getResultList()).subscribeAsCompletionStage());
    }
    /*public boolean delete(Integer id) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                RegulationCategoryDBO dbo = null;
                if (!Utils.isNullOrEmpty(id)) {

                    dbo = context.find(RegulationCategoryDBO.class, id);
                    if (!Utils.isNullOrEmpty(dbo)) {
                        dbo.setRecordStatus('D');
                        context.merge(dbo);
                    }
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }*/
   /* public boolean saveOrUpdate(CategoryUploadDTO dto, String userId){
        RegulationCategoryDBO dbo = new RegulationCategoryDBO();
        int uid = Integer.parseInt(userId);
       if(Utils.isNullOrEmpty(dto.getId()) && !Utils.isNullOrEmpty(dto.getCategoryName())){
            dbo.setRegulationCategoryName(dto.getCategoryName());
            dbo.setCreatedUsersId(uid);
            dbo.setModifiedUsersId(uid);
            dbo.setRecordStatus('A');
           sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
           return true;
        }
        if(!Utils.isNullOrEmpty(dto.getId()) && !Utils.isNullOrEmpty(dto.getCategoryName())) {
            int id = Integer.parseInt(dto.getId());
            dbo.setId(id);
            dbo.setRegulationCategoryName(dto.getCategoryName());
            dbo.setModifiedUsersId(uid);
            dbo.setRecordStatus('A');
            return sessionFactory.withTransaction((session, txt) -> session.merge(dbo).replaceWith(false)).await().indefinitely();

        }
        throw new RuntimeException("Something going wrong");
    }*/
    public Mono<List<RegulationCategoryDBO>> getCategoryDropdownData(){
        String query = " select dbo from RegulationCategoryDBO dbo where dbo.recordStatus = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query,  RegulationCategoryDBO.class).getResultList()).subscribeAsCompletionStage());
    }

}
