package com.christ.erp.services.transactions.admission;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterOptionDBO;

public class SelectionProcessScoreCareQualitativeParamtersTransaction {

	private static volatile SelectionProcessScoreCareQualitativeParamtersTransaction selectionProcessScoreCareQualitativeParamtersTransaction = null;

    public static SelectionProcessScoreCareQualitativeParamtersTransaction getInstance() {
        if(selectionProcessScoreCareQualitativeParamtersTransaction==null) {
        	selectionProcessScoreCareQualitativeParamtersTransaction = new SelectionProcessScoreCareQualitativeParamtersTransaction();
        }
        return  selectionProcessScoreCareQualitativeParamtersTransaction;
    }
    
    public List<Tuple> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
        	@SuppressWarnings("unchecked")
			@Override
	        public List<Tuple> onRun(EntityManager context) throws Exception {
	            String str = "select a.adm_qualitative_parameter_id as 'ID',a.qualitative_parameter_label as qualitativeParameterLabel,"
	            		+ " a.field_type as fieldType"
	             		+ " from adm_qualitative_parameter a where a.record_status='A'";
	            Query query = context.createNativeQuery(str,Tuple.class);
	            return query.getResultList();
	        }
        	@Override
         	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });
    }
    
    public boolean saveOrUpdate(AdmQualitativeParamterDBO admQualitativeParamterDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (admQualitativeParamterDBO.id == null) {
					context.persist(admQualitativeParamterDBO);
				} else {
					context.merge(admQualitativeParamterDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(AdmQualitativeParamterOptionDBO admQualitativeParamterOptionDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (admQualitativeParamterOptionDBO.id == null) {
					context.persist(admQualitativeParamterOptionDBO);
				} else {
					context.merge(admQualitativeParamterOptionDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<AdmQualitativeParamterOptionDBO> getAcademiYearDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AdmQualitativeParamterOptionDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<AdmQualitativeParamterOptionDBO>  onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from AdmQualitativeParamterOptionDBO bo   where  bo.recordStatus='A'");
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public AdmQualitativeParamterOptionDBO getAdmQualitativeParamterOptions(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmQualitativeParamterOptionDBO>() {
			@Override
			public AdmQualitativeParamterOptionDBO onRun(EntityManager context) throws Exception {
				return context.find(AdmQualitativeParamterOptionDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public AdmQualitativeParamterDBO getAdmQualitativeParamter(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmQualitativeParamterDBO>() {
			@Override
			public AdmQualitativeParamterDBO onRun(EntityManager context) throws Exception {
				return context.find(AdmQualitativeParamterDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<AdmQualitativeParamterDBO> getAdmQualitativeParamterByLabel(String qualitativeParameterLabel,String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AdmQualitativeParamterDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<AdmQualitativeParamterDBO>  onRun(EntityManager context) throws Exception {
				String hql = "from AdmQualitativeParamterDBO bo where bo.recordStatus='A' and qualitativeParameterLabel=:qualitativeParameterLabel";
				if(id != null && !id.isEmpty()) {
					hql += " and id!=:id";
				}
				Query query = context.createQuery(hql);
				query.setParameter("qualitativeParameterLabel", qualitativeParameterLabel);
				if(id != null && !id.isEmpty()) {
					query.setParameter("id", Integer.parseInt(id));
				}
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
