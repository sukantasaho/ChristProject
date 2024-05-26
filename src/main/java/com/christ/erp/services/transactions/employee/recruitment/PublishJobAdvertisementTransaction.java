package com.christ.erp.services.transactions.employee.recruitment;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAdvertisementImagesDBO;

@Repository
public class PublishJobAdvertisementTransaction {
	private static volatile PublishJobAdvertisementTransaction publishJobAdvertisementTransaction = null;
	
    public static PublishJobAdvertisementTransaction getInstance() {
        if(publishJobAdvertisementTransaction==null) {
        	publishJobAdvertisementTransaction = new PublishJobAdvertisementTransaction();
        }
        return  publishJobAdvertisementTransaction;
    }
    
    @Autowired
	private Mutiny.SessionFactory sessionFactory;

    public List<Tuple> getGridData() throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
    	 @SuppressWarnings("unchecked")
		 @Override
         public List<Tuple> onRun(EntityManager context) throws Exception {
             String str = "select e1.emp_appln_advertisement_id as ID, e1.advertisement_no as advertisementNo,e1.advertisement_start_date as startDate, "
             		+ " e1.is_common_advertisement as isCommonAdvertisement, "
             		+ " e1.advertisement_end_date as endDate,e2.academic_year_name as academicYear"  
             		+ " from emp_appln_advertisement e1 "  
             		+ " left join erp_academic_year e2 on e2.erp_academic_year_id = e1.erp_academic_year_id " 
             		+ " where e1.record_status='A'";
             Query query = context.createNativeQuery(str,Tuple.class);
             return query.getResultList();
         }
         @Override
         public void onError(Exception error) throws Exception {
            throw error;
         }
     });
    }
    
    public boolean saveOrUpdate(EmpApplnAdvertisementDBO empApplnAdvertisementDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empApplnAdvertisementDBO.id == null) {
					context.persist(empApplnAdvertisementDBO);
				} else {
					context.merge(empApplnAdvertisementDBO);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(EmpApplnAdvertisementImagesDBO advertisementImages) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (advertisementImages == null) {
					context.persist(advertisementImages);
				} else {
					context.merge(advertisementImages);
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public EmpApplnAdvertisementDBO getEmpJobAdvertisement(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnAdvertisementDBO>() {
			@Override
			public EmpApplnAdvertisementDBO onRun(EntityManager context) throws Exception {
				 Query query = context.createQuery("select distinct dbo from EmpApplnAdvertisementDBO dbo left join fetch dbo.empApplnAdvertisementImagesSet as dbos where dbo.recordStatus ='A' and dbo.id =:id",EmpApplnAdvertisementDBO.class);
				 query.setParameter("id", id);
				 return (EmpApplnAdvertisementDBO) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

    public EmpApplnAdvertisementImagesDBO getEmpJobAdvertisementImages(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnAdvertisementImagesDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpApplnAdvertisementImagesDBO onRun(EntityManager context) throws Exception {
				String query = "select * from emp_appln_advertisement_images where emp_appln_advertisement_images_id=:id";
		        Query q = context.createNativeQuery(query, EmpApplnAdvertisementImagesDBO.class);
		        q.setParameter("id",id);
				return (EmpApplnAdvertisementImagesDBO) Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String advertisementNo, String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String getJobAdvertisementQuery = "from EmpApplnAdvertisementDBO where recordStatus='A' and advertisementNo=:advertisementNo";
					
					if (!Utils.isNullOrEmpty(id)) {
						getJobAdvertisementQuery += " and id!=:id";
					}
					Query query = context.createQuery(getJobAdvertisementQuery);
					if (!Utils.isNullOrEmpty(id)) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("advertisementNo", advertisementNo);
					List<EmpApplnAdvertisementDBO> empApplnJobAdvertisements = query.getResultList();
					if (empApplnJobAdvertisements.size() > 0) {
						return true;
					}
					return false;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean duplicateDate(LocalDate startDate, LocalDate endDate, String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String Query = " select emp_appln_advertisement.emp_appln_advertisement_id from emp_appln_advertisement"
							+" where emp_appln_advertisement.record_status ='A' and "
							+" ((advertisement_start_date = :startDate and advertisement_end_date =:endDate) OR"
							+" (:startDate between advertisement_start_date and advertisement_end_date) OR "
							+" (:endDate between advertisement_start_date and advertisement_end_date))";
				if(!Utils.isNullOrEmpty(id)){
					Query += " and emp_appln_advertisement.emp_appln_advertisement_id !=:id";
				}
					Query query = context.createNativeQuery(Query);
					if(!Utils.isNullOrEmpty(id)) {
						query.setParameter("id", id);	
					}
					query.setParameter("startDate", startDate);
					query.setParameter("endDate", endDate);
					List<Tuple> jobAdvertisementsId = query.getResultList();
					if (jobAdvertisementsId.size() > 0) {
						return true;
					}
					return false;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean isDuplicateAdvertisement(String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
				@Override
				public Boolean onRun(EntityManager context) throws Exception {
					String Query = " from EmpApplnAdvertisementDBO dbo"
							    +" where dbo.recordStatus='A' and dbo.isCommonAdvertisement = 1";
					if(!Utils.isNullOrEmpty(id)) {
						Query += " and dbo.id !=:id";
					}
					Query query = context.createQuery(Query);
					if(!Utils.isNullOrEmpty(id)) {
						query.setParameter("id", Integer.parseInt(id));	
					}
					List<EmpApplnAdvertisementDBO> empApplnJobAdvertisements = query.getResultList();
					if (empApplnJobAdvertisements.size() > 0) {
						return true;
					}
					return false;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public EmpApplnAdvertisementImagesDBO getAdvImages(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpApplnAdvertisementImagesDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public EmpApplnAdvertisementImagesDBO onRun(EntityManager context) throws Exception {
				String query = "select distinct dbo from EmpApplnAdvertisementImagesDBO dbo where dbo.empApplnAdvertisementId.id =:id and dbo.recordStatus ='A'";
		        Query q = context.createQuery(query, EmpApplnAdvertisementImagesDBO.class);
		        q.setParameter("id",id);
				return (EmpApplnAdvertisementImagesDBO) Utils.getUniqueResult(q.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	 public boolean saveOrUpdate1(EmpApplnAdvertisementImagesDBO advertisementImages) throws Exception {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					if (advertisementImages == null) {
						context.persist(advertisementImages);
					} else {
						context.merge(advertisementImages);
					}
					return true;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		}
}
