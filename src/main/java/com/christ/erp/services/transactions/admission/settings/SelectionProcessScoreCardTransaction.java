package com.christ.erp.services.transactions.admission.settings;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Valid;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmScoreCardDBO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardDTO;

public class SelectionProcessScoreCardTransaction {
	
	private static volatile SelectionProcessScoreCardTransaction  admSelectionProcessScoreCardTransaction=null;
	
	public static SelectionProcessScoreCardTransaction getInstance() {
		if(admSelectionProcessScoreCardTransaction==null) {
			admSelectionProcessScoreCardTransaction = new SelectionProcessScoreCardTransaction();
        }
        return admSelectionProcessScoreCardTransaction;
	}
	
	public AdmScoreCardDBO get(@Valid String id) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<AdmScoreCardDBO>() {
			@Override
            public AdmScoreCardDBO onRun(EntityManager context) throws Exception {
            	AdmScoreCardDBO admScoreCardDBO=context.find(AdmScoreCardDBO.class, Integer.valueOf(id));
                return admScoreCardDBO;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}
	
	public Boolean saveOrUpdate(AdmScoreCardDBO admScoreCardDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	if(Utils.isNullOrEmpty(admScoreCardDBO.id)) {
            		context.persist(admScoreCardDBO);
                }else {
                    context.merge(admScoreCardDBO);
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
	public List<AdmScoreCardDBO> getGridData() throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<AdmScoreCardDBO>>() {
            @SuppressWarnings("unchecked")
			@Override
            public List<AdmScoreCardDBO> onRun(EntityManager context) throws Exception {
            	Query qry=context.createQuery("from AdmScoreCardDBO ab where ab.recordStatus='A'");
            	List<AdmScoreCardDBO> listofadmscorecard=qry.getResultList();
            	return listofadmscorecard;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
	
	public Boolean delete(AdmScoreCardDBO admScoreCardDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {   
                context.merge(admScoreCardDBO);
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}

	public Boolean duplicateCheck(@Valid AdmScoreCardDTO admScoreCardDTO) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
            @SuppressWarnings("unchecked")
			@Override
            public Boolean onRun(EntityManager context) throws Exception {
            	Boolean duplicate = false;
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT * FROM adm_scorecard where  scorecard_template_name=:scorecardTemplateName and record_status='A'");
                if(admScoreCardDTO.id != null && !admScoreCardDTO.id.isEmpty()) {
                	sb.append(" and adm_scorecard_id not in (:id) ");
                }
                Query q = context.createNativeQuery(sb.toString(), AdmScoreCardDBO.class);
                q.setParameter("scorecardTemplateName", admScoreCardDTO.scoreCardTemplateName.trim().replaceAll(" +", " "));
                if (admScoreCardDTO.id != null && !admScoreCardDTO.id.isEmpty()) {
                	q.setParameter("id", admScoreCardDTO.id);
                }
                AdmScoreCardDBO leavetype2 = null;
                leavetype2 = (AdmScoreCardDBO) Utils.getUniqueResult(q.getResultList());
                if(leavetype2 != null) {
                	duplicate = true;
                }else {
                	duplicate = false;
                }
                return duplicate;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
}
