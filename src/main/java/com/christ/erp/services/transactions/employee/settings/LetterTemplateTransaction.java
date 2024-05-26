package com.christ.erp.services.transactions.employee.settings;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;

public class LetterTemplateTransaction {

    private static volatile LetterTemplateTransaction letterTemplateTransaction = null;
    public static LetterTemplateTransaction getInstance() {
        if(letterTemplateTransaction==null) {
        	letterTemplateTransaction = new LetterTemplateTransaction();
        }
        return  letterTemplateTransaction;
    }

    public boolean saveOrUpdate(ErpTemplateDBO dbo) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	if(dbo==null) {
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
    
    public boolean groupSaveOrUpdate(ErpTemplateGroupDBO dbo) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
            	if(dbo.getId() == 0) {
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
    
    public List<ErpTemplateDBO> getGridData() throws Exception {
         return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpTemplateDBO>>() {
        	 @Override
        	 public List <ErpTemplateDBO> onRun(EntityManager context) throws Exception  {
        		 Query query = context.createQuery("select bo from ErpTemplateDBO bo where bo.recordStatus = 'A' and bo.erpTemplateGroupDBO!=null");
        		 List <ErpTemplateDBO> mappings = query.getResultList();
        		 return mappings;
        	 }
        	 @Override
        	 public void onError(Exception error) throws Exception {
        		 throw error;
        	 }
         });
    }
    
    public ErpTemplateGroupDBO getDBO(String letterType) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateGroupDBO>() {
        	@Override
        	public ErpTemplateGroupDBO onRun(EntityManager context) throws Exception  {
        		try {
        			Query query = context.createQuery("select bo from ErpTemplateGroupDBO bo where bo.recordStatus='A' and bo.templateGroupName=:letterType");
        			query.setParameter("letterType", letterType);
        			ErpTemplateGroupDBO mappings = (ErpTemplateGroupDBO) Utils.getUniqueResult(query.getResultList());
        			return mappings;
        		}
        		catch(NoResultException e) {
        			return null;
        		}
           }
           @Override
           public void onError(Exception error) throws Exception {
        	   throw error;
           }
        });
    }
    
    public Tuple getCount(Integer templateGroupId) throws Exception {
    	return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
    		@Override
    		public Tuple onRun(EntityManager context) throws Exception {
    			Query query1 = context.createNativeQuery("select count(erp_template_id) as counter from erp_template where  erp_template_group_id=:templateGroupId",Tuple.class);
    			query1.setParameter("templateGroupId", templateGroupId);
    			return (Tuple) Utils.getUniqueResult(query1.getResultList());
    		}
    		@Override
    		public void onError(Exception error) throws Exception {
    			throw error;
    		}
    	});
    }
    
    public List<ErpTemplateGroupDBO> getDuplicate(LetterTemplatesDTO data) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpTemplateGroupDBO>>() {
        	@Override
        	public List <ErpTemplateGroupDBO> onRun(EntityManager context) throws Exception  {
        		Query query = context.createQuery("select bo from ErpTemplateGroupDBO bo where bo.recordStatus='A'  and bo.templateGroupCode=:templateGroupCode");
        		query.setParameter("templateGroupCode", data.groupCode);
        		List <ErpTemplateGroupDBO> mappings = query.getResultList();
        		return mappings;
        	}         
        	@Override
        	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });
    }    
    
    public ErpTemplateDBO edit(String id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
        	@Override
        	public ErpTemplateDBO onRun(EntityManager context) throws Exception  {
        		ErpTemplateDBO bo = context.find(ErpTemplateDBO.class, Integer.parseInt(id));
        		return bo;
        	}
        	@Override
        	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });  
   }

   public boolean delete(String headingId) throws Exception {
	   return DBGateway.runJPA(new ICommitTransactional() {
		   @Override
           public boolean onRun(EntityManager context) throws Exception {
			   ErpTemplateDBO heading = null;
				boolean flag=false;
				if (!Utils.isNullOrEmpty(headingId) && !Utils.isNullOrWhitespace(headingId)) {
					heading = context.find(ErpTemplateDBO.class, Integer.parseInt(headingId));
					if (heading.id != 0) {
						heading.recordStatus ='D';
						context.merge(heading);
						flag = true;
					}
				}	
				else {
					flag = false;
				}
				return flag;
			}
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }
    
    public ErpTemplateDBO getId(Integer id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
        	@Override
        	public ErpTemplateDBO onRun(EntityManager context) throws Exception  {
        		ErpTemplateDBO bo = context.find(ErpTemplateDBO.class, id);
        		return bo;
        	}
        	@Override
        	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });
    }
    
    public ErpTemplateGroupDBO getGroupId(Integer id) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateGroupDBO>() {
        	@Override
        	public ErpTemplateGroupDBO onRun(EntityManager context) throws Exception  {
        		ErpTemplateGroupDBO bo = context.find(ErpTemplateGroupDBO.class, id);
        		return bo;
        	}         
        	@Override
        	public void onError(Exception error) throws Exception {
        		throw error;
        	}
        });
    }   
	
}
