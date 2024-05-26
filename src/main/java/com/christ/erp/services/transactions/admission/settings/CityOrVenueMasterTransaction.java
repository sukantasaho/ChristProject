package com.christ.erp.services.transactions.admission.settings;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessCenterDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessVenueCityDBO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;

public class CityOrVenueMasterTransaction {
private static volatile CityOrVenueMasterTransaction  cityOrVenueMasterTransaction=null;
	
	public static CityOrVenueMasterTransaction getInstance() {
		if(cityOrVenueMasterTransaction==null) {
			cityOrVenueMasterTransaction = new CityOrVenueMasterTransaction();
        }
        return cityOrVenueMasterTransaction;
	}

	public List<Tuple> getGridData() throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery("select adm_selection_process_venue_city_id AS 'ID', selection_process_mode AS 'Mode', venue_name AS 'Venue' from adm_selection_process_venue_city where record_status = 'A' ", Tuple.class);
                List<Tuple> mappings = query.getResultList();
                return mappings;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });	
	}

	public boolean delete(String id, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	AdmSelectionProcessVenueCityDBO dbo = null;
             	if (id != null && !id.isEmpty()) {
					if (Utils.isNullOrWhitespace(id) == false) {
						 dbo  = context.find(AdmSelectionProcessVenueCityDBO.class, Integer.parseInt(id));
						if (!Utils.isNullOrEmpty(dbo)) {
							for (AdmSelectionProcessCenterDetailsDBO centerDetailsDBO : dbo.getCenterDetailsDBOs()) {
								if(centerDetailsDBO.getRecordStatus() == 'A'){
									centerDetailsDBO.setRecordStatus('D');
									centerDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
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

	public boolean saveOrUpdate(AdmSelectionProcessVenueCityDBO dbo) throws Exception {
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

	public AdmSelectionProcessVenueCityDBO edit(int id) throws Exception{
		return  DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessVenueCityDBO>() {
			@Override
            public AdmSelectionProcessVenueCityDBO onRun(EntityManager context) throws Exception {
			    AdmSelectionProcessVenueCityDBO dbo = null;
				try{
					dbo = context.find(AdmSelectionProcessVenueCityDBO.class, id);	
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

	public List<AdmSelectionProcessVenueCityDBO> getDuplicateCheck(AdmSelectionProcessVenueCityDTO data) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<AdmSelectionProcessVenueCityDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
            public List<AdmSelectionProcessVenueCityDBO> onRun(EntityManager context) throws Exception {
				List<AdmSelectionProcessVenueCityDBO> duplicateChecking = null;
				StringBuffer sb = new StringBuffer();
				sb.append("from AdmSelectionProcessVenueCityDBO bo where bo.recordStatus='A' and bo.selectionProcessMode=:Mode and bo.venueName=:Venue");
				if(!Utils.isNullOrEmpty(data.id)) {
					sb.append(" and bo.id not in (:ID)");
				}
				if(!Utils.isNullOrEmpty(data.mode) && !Utils.isNullOrEmpty(data.mode.text) && !Utils.isNullOrEmpty(data.venue)) {
			        Query queryDuplicateCheck = context.createQuery(sb.toString());
		            queryDuplicateCheck.setParameter("Mode", data.mode.text);
		            queryDuplicateCheck.setParameter("Venue", data.venue.trim());
		            if(!Utils.isNullOrEmpty(data.id)) {
		            	queryDuplicateCheck.setParameter("ID", Integer.parseInt(data.id));
		            }
			        duplicateChecking = (List<AdmSelectionProcessVenueCityDBO>) queryDuplicateCheck.getResultList();
				}
		        return duplicateChecking;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
	    });
	}
	
}
