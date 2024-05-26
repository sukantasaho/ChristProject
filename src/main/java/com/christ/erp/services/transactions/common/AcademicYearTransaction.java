package com.christ.erp.services.transactions.common;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDetailsDBO;

public class AcademicYearTransaction {

	private static volatile AcademicYearTransaction academicYearTransaction = null;

    public static AcademicYearTransaction getInstance() {
        if(academicYearTransaction==null) {
        	academicYearTransaction = new AcademicYearTransaction();
        }
        return  academicYearTransaction;
    }
    
    public List<Tuple> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
        	@SuppressWarnings("unchecked")
			@Override
	         public List<Tuple> onRun(EntityManager context) throws Exception {
	             String str = "select e.erp_academic_year_id as 'ID',e.academic_year as academicYear,e.is_current_academic_year as isCurrent "
	             		+ " from erp_academic_year e where e.record_status='A'";
	             Query query = context.createNativeQuery(str,Tuple.class);
	             return query.getResultList();
	         }
	         @Override
	         public void onError(Exception error) throws Exception {
	            throw error;
	         }
	     });
    }
    
    public boolean saveOrUpdate(ErpAcademicYearDBO erpAcademicYearDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (erpAcademicYearDBO.id == null) {
					context.persist(erpAcademicYearDBO);
				} else {
					context.merge(erpAcademicYearDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(ErpAcademicYearDetailsDBO erpAcademicYearDetailsDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (erpAcademicYearDetailsDBO.id == null) {
					context.persist(erpAcademicYearDetailsDBO);
				} else {
					context.merge(erpAcademicYearDetailsDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<ErpAcademicYearDetailsDBO> getAcademiYearDetails() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpAcademicYearDetailsDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpAcademicYearDetailsDBO>  onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpAcademicYearDetailsDBO bo   where  bo.recordStatus='A'");
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ErpAcademicYearDetailsDBO getAcademicYearDetailsDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpAcademicYearDetailsDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpAcademicYearDetailsDBO onRun(EntityManager context) throws Exception {
				
				String query = "select * from erp_academic_year_detail where erp_academic_year_detail_id=:id";
		        Query q = context.createNativeQuery(query, ErpAcademicYearDetailsDBO.class);
		        q.setParameter("id",id);
				
				return (ErpAcademicYearDetailsDBO) Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public ErpAcademicYearDBO getAcademicYearDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpAcademicYearDBO>() {
			@Override
			public ErpAcademicYearDBO onRun(EntityManager context) throws Exception {
				return context.find(ErpAcademicYearDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<ErpAcademicYearDBO> getAcademiYearByYear(String year) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpAcademicYearDBO> >() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpAcademicYearDBO>  onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from ErpAcademicYearDBO bo where bo.recordStatus='A' and academic_year=:academicYear");
				query.setParameter("academicYear", year);
				return  query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<ErpAcademicYearDBO> getAcademicYearDBO() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<ErpAcademicYearDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<ErpAcademicYearDBO> onRun(EntityManager context) throws Exception {
			Query academicYearDetailsQuery = context.createQuery("from ErpAcademicYearDBO bo   where  bo.recordStatus='A'");
			return academicYearDetailsQuery.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public List<Tuple> getCampus() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
			Query erpCampusQuery = context.createNativeQuery("select erp_campus_id as 'ID',campus_name as 'Text' "
					+ "from erp_campus where record_status='A' order by campus_name", Tuple.class);
			return erpCampusQuery.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
