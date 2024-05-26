package com.christ.erp.services.transactions.employee.letter;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

public class LetterTypesTransaction {
	private static volatile LetterTypesTransaction letterTypesTransaction = null;

	public static LetterTypesTransaction getInstance() {
		if (letterTypesTransaction == null) {
			letterTypesTransaction = new LetterTypesTransaction();
		}
		return letterTypesTransaction;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e.emp_letter_request_type_id as ID,EC.template_name as letterTemplate,e.letter_type_name as letterName,\r\n"
						+ "e.letter_type_start_no as startNo from emp_letter_request_type as e inner join erp_template as EC on EC.erp_template_id = e.erp_template_id where  e.record_status='A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean saveOrUpdate(EmpLetterRequestTypeDBO empLetterRequestTypeDBO) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (empLetterRequestTypeDBO.id == null) {
					context.persist(empLetterRequestTypeDBO);
				} else {
					context.merge(empLetterRequestTypeDBO);
				}
				return true;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public static ErpTemplateDBO gettemplate(int id) throws Exception {
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

	@SuppressWarnings("unchecked")
	public Boolean isDuplicate(String letterName, String id) {
		try {
			return DBGateway.runJPA(new ICommitTransactional() {
				@Override
				public boolean onRun(EntityManager context) throws Exception {
					String getTemplateNameQuery = "from EmpLetterRequestTypeDBO where recordStatus='A' and replace(letterTypeName,' ','')  =:letterTypeName";
					if (id != null) {
						getTemplateNameQuery += " and id!=:id";
					}
					Query query = context.createQuery(getTemplateNameQuery);
					if (id != null) {
						query.setParameter("id", Integer.parseInt(id));
					}
					query.setParameter("letterTypeName", letterName);
					List<EmpLetterRequestTypeDBO> empLetterRequestTypes = query.getResultList();
					if (empLetterRequestTypes.size() > 0) {
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

	public EmpLetterRequestTypeDBO getEmpLetterRequestTypeDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpLetterRequestTypeDBO>() {
			@Override
			public EmpLetterRequestTypeDBO onRun(EntityManager context) throws Exception {
				return context.find(EmpLetterRequestTypeDBO.class, id);
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getLetterTemplate() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				Query erpTemplateQuery = context.createNativeQuery(
						"select temp.erp_template_id as ID, temp.template_name as TEXT  from erp_template temp \r\n"
								+ "	 		inner join erp_template_group grp on temp.erp_template_group_id = grp.erp_template_group_id\r\n"
								+ "	 		where grp.record_status='A' and temp.record_status='A'\r\n"
								+ "	 		and grp.template_group_name <> 'Appointment Letter'\r\n"
								+ "	 		and grp.template_group_name <> 'Offer Letter'",
						Tuple.class);
				return erpTemplateQuery.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
