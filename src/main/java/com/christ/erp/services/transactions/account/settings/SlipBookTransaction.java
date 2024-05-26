package com.christ.erp.services.transactions.account.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.AccSlipBookDBO;
import com.christ.erp.services.dto.account.settings.AccSlipBookDTO;

public class SlipBookTransaction {

	private static volatile SlipBookTransaction slipBookTransaction = null;

	public static SlipBookTransaction getInstance() {
		if(slipBookTransaction == null){
			slipBookTransaction = new SlipBookTransaction();
		}
		return slipBookTransaction;
	}

	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select a.acc_slip_book_id as accslipbookid , a.slip_book_no  as slipbookNo ,"
						+ " a.slip_book_type as slipbooktype from acc_slip_book a where record_status = 'A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(AccSlipBookDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(dbo.id == null){
					context.persist(dbo);
				} else {
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

	public AccSlipBookDBO getaccSlipBookData(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AccSlipBookDBO>() {
			@Override
			public AccSlipBookDBO onRun(EntityManager context) throws Exception {
				AccSlipBookDBO slipBookInfo = context.find(AccSlipBookDBO.class, Integer.parseInt(id));
				return slipBookInfo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean delete(AccSlipBookDTO data, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				AccSlipBookDBO header = null;
				if(Utils.isNullOrWhitespace(data.id) == false){
					header = context.find(AccSlipBookDBO.class, Integer.parseInt(data.id));
				}
				if(header != null){
					header.recordStatus = 'D';
					header.modifiedUsersId = Integer.parseInt(userId);
					if (header.id != null) {
						context.merge(header);
					}
				}
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean duplicateAccSlipBook(AccSlipBookDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public Boolean onRun(EntityManager context) throws Exception {
				Boolean duplicate = false;
				StringBuffer sb = new StringBuffer();
				sb.append(" from AccSlipBookDBO  where slipBookNo=:slipBookNo and recordStatus='A' ");
				Query q = context.createQuery(sb.toString());
				q.setParameter("slipBookNo", data.slipBookNo);
				List<AccSlipBookDBO> bo = q.getResultList();
				if(bo != null){
					for(AccSlipBookDBO accSlipBookDBO : bo){			
						if(!Utils.isNullOrEmpty(data.id) && !Utils.isNullOrEmpty(accSlipBookDBO.id)){
							if(!(Utils.isNullOrEmpty(data.bookNoPrefix)) && !(Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix))){
								if(data.bookNoPrefix.equalsIgnoreCase(accSlipBookDBO.bookNoPrefix)){
									if (!(Integer.parseInt(data.id) == accSlipBookDBO.id)) {
										duplicate = true;
									}
								}		
							}
							if(Utils.isNullOrEmpty(data.bookNoPrefix) && Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix)){
								if(Utils.isNullOrEmpty(data.bookNoPrefix) == Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix)){
									if (!(Integer.parseInt(data.id) == accSlipBookDBO.id)) {
										duplicate = true;
									}
								}		
							}
						}else {
							if(!(Utils.isNullOrEmpty(data.bookNoPrefix)) && !(Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix))){
								if(data.bookNoPrefix.equalsIgnoreCase(accSlipBookDBO.bookNoPrefix)){
									duplicate = true;
									}		
								}
								if(Utils.isNullOrEmpty(data.bookNoPrefix) && Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix)){
									if(Utils.isNullOrEmpty(data.bookNoPrefix) == Utils.isNullOrEmpty(accSlipBookDBO.bookNoPrefix)){
										duplicate = true;
										}		
									}					
								}
							}
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
