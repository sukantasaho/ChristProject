package com.christ.erp.services.transactions.employee.onboarding;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistMainDBO;
import com.christ.erp.services.dbobjects.employee.onboarding.EmpDocumentChecklistSubDBO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;

@SuppressWarnings("unchecked")
public class DocumentVerificationSettingsTransaction {
	private static volatile DocumentVerificationSettingsTransaction documentVerificationSettingsTransaction = null;

    public static DocumentVerificationSettingsTransaction getInstance() {
        if(documentVerificationSettingsTransaction==null) {
        	documentVerificationSettingsTransaction = new DocumentVerificationSettingsTransaction();
        }
        return documentVerificationSettingsTransaction;
    }

    public List<Tuple> getChecklistHeadings() throws Exception{
    	return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
	             String query1 = "select emp_document_checklist_main_id as id,document_checklist_name as name"
	             		+ " ,document_checklist_display_order as displayOrder,is_document_addl_checklist_foreign_national as checklistForeignNational"
	             		+ " from emp_document_checklist_main bo where bo.record_status='A' order by bo.is_document_addl_checklist_foreign_national,bo.document_checklist_display_order asc";
	             Query query = context.createNativeQuery(query1,Tuple.class);
	             return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
    }

	public List<EmpDocumentChecklistMainDBO> getChecklistMainBos(EmpDocumentChecklistMainDTO headingData) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDocumentChecklistMainDBO>>() {
			@Override
			public List<EmpDocumentChecklistMainDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from EmpDocumentChecklistMainDBO bo where bo.recordStatus='A' and bo.id!=:headingId").setParameter("headingId", headingData.id);
	            return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<EmpDocumentChecklistSubDBO> getChecklistSubBos() throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpDocumentChecklistSubDBO>>() {
			@Override
			public List<EmpDocumentChecklistSubDBO> onRun(EntityManager context) throws Exception {
				Query query = context.createQuery("from EmpDocumentChecklistSubDBO bo where bo.recordStatus='A'");
	            return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpDocumentChecklistMainDBO getChecklistMainDBOById(int headingId) throws Exception{
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpDocumentChecklistMainDBO>() {
			@Override
			public EmpDocumentChecklistMainDBO onRun(EntityManager context) throws Exception {
				EmpDocumentChecklistMainDBO dbo = null;
				try {
					Query query = context.createQuery("from EmpDocumentChecklistMainDBO bo where bo.recordStatus='A' and bo.id=:headingId").setParameter("headingId", headingId);
					dbo = (EmpDocumentChecklistMainDBO) Utils.getUniqueResult(query.getResultList());
				}catch (NoResultException e) {
					dbo = null;
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdateOrDelete(EmpDocumentChecklistMainDBO heading) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if(heading.id == null) {
                    context.persist(heading);
                }else {
                    context.merge(heading);
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
