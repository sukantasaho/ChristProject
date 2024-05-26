package com.christ.erp.services.transactions.employee.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.common.SysMenuModuleSubDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

public class FormattedTemplatesTransaction {
	private static volatile FormattedTemplatesTransaction formattedTemplatesTransaction = null;

	public static FormattedTemplatesTransaction getInstance() {
		if (formattedTemplatesTransaction == null) {
			formattedTemplatesTransaction = new FormattedTemplatesTransaction();
		}
		return formattedTemplatesTransaction;
	}

	public boolean saveOrUpdate(ErpTemplateDBO erpTemplateDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (erpTemplateDBO.id == null) {
					context.persist(erpTemplateDBO);
				} else {
					context.merge(erpTemplateDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public SysMenuModuleSubDBO getModuleSubDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<SysMenuModuleSubDBO>() {
			@Override
			public SysMenuModuleSubDBO onRun(EntityManager context) throws Exception {

				return context.find(SysMenuModuleSubDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
//@EventListener(ApplicationReadyEvent.class) 
	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "select e.erp_template_id as 'ID',e.erp_module_sub_id as process,EC.sub_module_name as 'subModuleName',e.template_code as templateCode,\r\n"
//						+ "e.template_name as templateName from erp_template as e inner join erp_module_sub as EC on EC.erp_module_sub_id = e.erp_module_sub_id where  e.record_status='A' order by EC.sub_module_name;";
				
				String str = "select e.erp_template_id as 'ID',e.sys_menu_module_sub_id as process,EC.sub_module_name as 'subModuleName',e.template_code as templateCode, sm.module_name as moduleName, "
						+ "e.template_name as templateName, e.template_id as templateId "
						+ " from erp_template as e "
						+" inner join sys_menu_module_sub as EC on EC.sys_menu_module_sub_id = e.sys_menu_module_sub_id "
						+" inner join sys_menu_module sm ON sm.sys_menu_module_id = EC.sys_menu_module_id"
						+" where e.record_status='A' and sm.record_status='A' and EC.record_status='A' order by EC.sub_module_name;";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String templateCode, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getTemplateCodeQuery = "from ErpTemplateDBO where  recordStatus='A' and templateCode=:templateCode";
					if (id != null) {
						getTemplateCodeQuery += " and id!=:id";
					}
					Query query = context.createQuery(getTemplateCodeQuery);
					if (id != null && !id.isEmpty()) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("templateCode", templateCode.trim());
					List<ErpTemplateDBO> erpTemplate = query.getResultList();
					if (erpTemplate.size() > 0) {
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

	public List<Tuple> getSubModuleList() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query subModuleQuery = context.createNativeQuery(
						"select e.sys_menu_module_sub_id as 'ID',e.sub_module_name as 'Text',sm.module_name as moduleName from sys_menu_module_sub e "
						+" inner join sys_menu_module sm ON sm.sys_menu_module_id = e.sys_menu_module_id "
						+" where e.record_status='A' and sm.record_status='A' order by e.sub_module_name asc",
						Tuple.class);
				return subModuleQuery.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ErpTemplateDBO getErpTemplateDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
			@Override
			public ErpTemplateDBO onRun(EntityManager context) throws Exception {
				return context.find(ErpTemplateDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
