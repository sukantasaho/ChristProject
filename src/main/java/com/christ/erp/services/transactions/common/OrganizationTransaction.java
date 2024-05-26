package com.christ.erp.services.transactions.common;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpOrganizationsDBO;

public class OrganizationTransaction {
	private static volatile OrganizationTransaction organizationTransaction = null;

	public static OrganizationTransaction getInstance() {
		if (organizationTransaction == null ) {
			organizationTransaction = new OrganizationTransaction();
		}
		return organizationTransaction;
	}

	public ErpOrganizationsDBO getErpOrganizationsDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpOrganizationsDBO>() {
			@SuppressWarnings("unchecked")
			@Override
			public ErpOrganizationsDBO onRun(EntityManager context) throws Exception {
				if(id!=0) {
					return context.find(ErpOrganizationsDBO.class, id);
				}
				else {
					String query = "from ErpOrganizationsDBO bo where bo.recordStatus='A'";
					Query query1 = context.createQuery(query);
					return (ErpOrganizationsDBO) Utils.getUniqueResult(query1.getResultList());
				}
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;	
			}
		} );
	}

	public boolean updateErpOrganizationsDBO(ErpOrganizationsDBO erpOrganizationsDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if ( erpOrganizationsDBO.id != 0){
					context.merge(erpOrganizationsDBO);
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
