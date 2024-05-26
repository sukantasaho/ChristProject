package com.christ.erp.services.transactions.admission.settings;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmApplnNumberGenerationDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dto.admission.settings.AdmApplnNumbergeneratonDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

@Repository
public class ApplicationNumberTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
//	private static volatile ApplicationNumberTransaction applicationNumberTransaction = null;
//
//    public static ApplicationNumberTransaction getInstance() {
//        if(applicationNumberTransaction==null) {
//        	applicationNumberTransaction = new ApplicationNumberTransaction();
//        }
//        return  applicationNumberTransaction;
//    }
    
    @Autowired
	private CommonApiTransaction commonApiTransaction;
    
    public List<AdmApplnNumberGenerationDBO> getGridData(String yearId) throws Exception {
    	ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAdmissionYear(); 
		return DBGateway.runJPA(new ISelectGenericTransactional<List<AdmApplnNumberGenerationDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<AdmApplnNumberGenerationDBO> onRun(EntityManager context) throws Exception {
				String str =" from AdmApplnNumberGenerationDBO dbo where dbo.recordStatus='A'"
						   +" and dbo.academicYearDBO.id =:yearId order by dbo.id asc";
			    Query qry = context.createQuery(str,AdmApplnNumberGenerationDBO.class);
			    if(!Utils.isNullOrEmpty(yearId)) {
			    	qry.setParameter("yearId", Integer.parseInt(yearId));
				} else {
					qry.setParameter("yearId",currYear.getId());
				}
				return qry.getResultList();
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
    
    public AdmApplnNumberGenerationDBO getAdmApplnNumberGenerationDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmApplnNumberGenerationDBO>() {
			@Override
			public AdmApplnNumberGenerationDBO onRun(EntityManager context) throws Exception {
				return context.find(AdmApplnNumberGenerationDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(AdmApplnNumberGenerationDBO edmApplnNumberGenerationDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(edmApplnNumberGenerationDBO) || Utils.isNullOrEmpty(edmApplnNumberGenerationDBO.id) || edmApplnNumberGenerationDBO.id==0) {
                    context.persist(edmApplnNumberGenerationDBO);
                }
                else {
                    context.merge(edmApplnNumberGenerationDBO);
                } 
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
   
    public Boolean isRangeExists(AdmApplnNumbergeneratonDTO data) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@Override
	         public Boolean onRun(EntityManager context) throws Exception {
	             String str = "from AdmApplnNumberGenerationDBO where  "
	 					+ "((:onlineStart between onlineApplnNoFrom and onlineApplnNoTo) OR "
						+ "(:onlineStart between offlineApplnNoFrom and offlineApplnNoTo) OR "
						+ "(:onlineEnd between onlineApplnNoFrom and onlineApplnNoTo) OR "
						+ "(:onlineEnd between offlineApplnNoFrom and offlineApplnNoTo) OR "
						+ "(:offlineStart between onlineApplnNoFrom and onlineApplnNoTo) OR "
						+ "(:offlineStart between offlineApplnNoFrom and offlineApplnNoTo) OR "
						+ "(:offlineEnd between onlineApplnNoFrom and onlineApplnNoTo) OR "
						+ "(:offlineEnd between offlineApplnNoFrom and offlineApplnNoFrom) OR " 
						+ "(:onlineStart <= onlineApplnNoFrom and :onlineEnd >= onlineApplnNoTo) OR "
						+ "(:onlineStart <= offlineApplnNoFrom and :onlineEnd >= offlineApplnNoTo) OR "
						+ "(:offlineStart <= onlineApplnNoFrom and :offlineEnd >= onlineApplnNoTo) OR "
						+ "(:offlineStart <= offlineApplnNoFrom and :offlineEnd >= offlineApplnNoTo)) and recordStatus='A' ";
	             if(!Utils.isNullOrEmpty(data.id)){
	            	 str += " and id!=:id"; 
	             }
	             Query qry = context.createQuery(str,AdmApplnNumberGenerationDBO.class);
	             if(!Utils.isNullOrEmpty(data.onlineAppNoFrom)){
	            	 qry.setParameter("onlineStart", Integer.parseInt(data.onlineAppNoFrom));
	             }
	             if(!Utils.isNullOrEmpty(data.onlineAppNoTo)){
	            	 qry.setParameter("onlineEnd", Integer.parseInt(data.onlineAppNoTo));
	             }
	             if(!Utils.isNullOrEmpty(data.offlineAppNoFrom)){
	            	 qry.setParameter("offlineStart", Integer.parseInt(data.offlineAppNoFrom));
	             }
	             if(!Utils.isNullOrEmpty(data.offlineAppNoTo)){
	            	 qry.setParameter("offlineEnd", Integer.parseInt(data.offlineAppNoTo));
	             }
	             if(!Utils.isNullOrEmpty(data.id)){
	            	 qry.setParameter("id", Integer.parseInt(data.id));
	             }
				 if(qry.getResultList().size()>0) {
					 return true;
				 }
				 return false;
	         }
	         @Override
	         public void onError(Exception error) throws Exception {
	            throw error;
	         }
	     });
    }
    
    public Boolean isProgramExists(String id, String campusMappingId, String academicYearId) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@Override
	         public Boolean onRun(EntityManager context) throws Exception {
				if(!Utils.isNullOrEmpty(campusMappingId)) {
				 String str = "select bo from AdmApplnNumberGenerationDBO bo join bo.admApplnNumberGenDetailsDBOSet details "
		             		+ " where  details.erpCampusProgrammeMappingDBO.id=:campusMappingId "
		             		+ " and details.recordStatus='A' and bo.recordStatus='A' and  bo.academicYearDBO.id=:academicYearId  ";
		            if(!Utils.isNullOrEmpty(id)){
		            	 str += " and bo.id!=:id ";
		            }
		            if(!Utils.isNullOrEmpty(id)){
		            	str += " and details.admApplnNumberGenerationDBO.id!=:id"; 
		            }
		            Query qry = context.createQuery(str,AdmApplnNumberGenerationDBO.class);
		            if(!Utils.isNullOrEmpty(id)){
		            	qry.setParameter("id", Integer.parseInt(id));
		            }
		            qry.setParameter("campusMappingId", Integer.parseInt(campusMappingId));
	            	qry.setParameter("academicYearId", Integer.parseInt(academicYearId));
					if(!Utils.isNullOrEmpty(qry.getResultList())) {
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
}
