package com.christ.erp.services.transactions.admission.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmSelectionProcessTypeDetailsDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestTypeDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleGradeMappingDetailDBO;

public class SelectionProcessTypesTransaction {
	private static volatile SelectionProcessTypesTransaction selectionProcessTypesTransaction = null;

	public static SelectionProcessTypesTransaction getInstance() {
		if (selectionProcessTypesTransaction == null) {
			selectionProcessTypesTransaction = new SelectionProcessTypesTransaction();
		}
		return selectionProcessTypesTransaction;
	}
	
	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e.adm_selection_process_type_id as ID,e.selection_stage_name as selectionProcessName,e.mode as mode, e.admit_card_display_name as displayName from adm_selection_process_type  as e where  e.record_status='A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public boolean saveOrUpdate(AdmSelectionProcessTypeDBO adminSelectionProcessTypeDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (Utils.isNullOrEmpty( adminSelectionProcessTypeDBO) || Utils.isNullOrEmpty(adminSelectionProcessTypeDBO.id)||adminSelectionProcessTypeDBO.id==0) {
					context.persist(adminSelectionProcessTypeDBO);
				} else {
					context.merge(adminSelectionProcessTypeDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public AdmSelectionProcessTypeDBO getAdminSelectionProcessTypeDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<AdmSelectionProcessTypeDBO>() {
			@Override
			public AdmSelectionProcessTypeDBO onRun(EntityManager context) throws Exception {
				return context.find(AdmSelectionProcessTypeDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String selectionStageName, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getselectionStageNameQuery = "from AdmSelectionProcessTypeDBO where recordStatus='A' and selectionStageName=:selectionStageName";
					if (id != null) {
						getselectionStageNameQuery += " and id!=:id";
					}
					Query query = context.createQuery(getselectionStageNameQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("selectionStageName", selectionStageName);
					List<AdmSelectionProcessTypeDBO> adminSelectionProcessType = query.getResultList();
					if (adminSelectionProcessType.size() > 0) {
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

}
