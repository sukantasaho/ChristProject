package com.christ.erp.services.transactions.employee.appraisal;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalElementsDBO;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalTemplateDBO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalTemplateDTO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppraisalQuestionsTransaction {
    private static volatile AppraisalQuestionsTransaction  appraisalQuestionsTransaction = null;

    public static AppraisalQuestionsTransaction getInstance() {
        if (appraisalQuestionsTransaction == null) {
            appraisalQuestionsTransaction = new AppraisalQuestionsTransaction();
        }
        return appraisalQuestionsTransaction;
    }

    public List<Tuple> getGridData() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @SuppressWarnings("unchecked")
			@Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                String str = "select eat.emp_appraisal_template_id as empAppraisalTemplateId,eat.template_name as templateName, "
                		+ " eat.template_code as templateCode,   "
                		+ " eat.emp_employee_category_id as empEmployeeCategoryId,eat.appraisal_type as appraisalType ,"
                		+ " eec.employee_category_name as employeeCategoryName"
                		+ " from emp_appraisal_template eat "
                		+ " join emp_employee_category eec on eec.emp_employee_category_id = eat.emp_employee_category_id and  eec.record_status ='A' "
                		+ " where eat.record_status = 'A'";
                Query query = context.createNativeQuery(str,Tuple.class);
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public List<Tuple> getAppraisalElementsData(String empAppraisalTemplateId) throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
            @Override
            public List<Tuple> onRun(EntityManager context) throws Exception {
                String str = "WITH RECURSIVE emp_appraisal_paper (template_id,element_group, element_question,element_id,parent_id, element_order,indx_ing,element_level,is_group_not_displayed," +
                        "   is_question,answer_option_selection_type,emp_appraisal_elements_option_id) AS ( " +
                        "   select * from ( " +
                        "       SELECT  emp_appraisal_elements.emp_appraisal_template_id as template_id, element_name, element_description, " +
                        "           element_identity, element_parent_identity, element_order, CAST(element_order AS CHAR(100))  as indx_ing, element_level,is_group_not_displayed,is_question, answer_option_selection_type,emp_appraisal_elements_option_id " +
                        "       FROM emp_appraisal_elements " +
                        "       WHERE record_status like 'A' " +
                        "       and emp_appraisal_elements.element_parent_identity is null and emp_appraisal_elements.emp_appraisal_template_id=:empAppraisalTemplateId order by element_order " +
                        "       ) as initiat " +
                        "   UNION   " +
                        "       SELECT emp_appraisal_elements.emp_appraisal_template_id,emp_appraisal_elements.element_name , emp_appraisal_elements.element_description , " +
                        "           emp_appraisal_elements.element_identity, emp_appraisal_elements.element_parent_identity, emp_appraisal_elements.element_order, " +
                        "           CONCAT(emp_appraisal_paper.indx_ing,'.',emp_appraisal_elements.element_order ) as indx_ing, " +
                        "           emp_appraisal_elements.element_level,emp_appraisal_elements.is_group_not_displayed,emp_appraisal_elements.is_question, " +
                        "           emp_appraisal_elements.answer_option_selection_type,emp_appraisal_elements.emp_appraisal_elements_option_id " +
                        "       FROM emp_appraisal_paper " +
                        "       inner JOIN emp_appraisal_elements ON emp_appraisal_elements.element_parent_identity =emp_appraisal_paper.element_id " +
                        "       where emp_appraisal_elements.emp_appraisal_template_id=:empAppraisalTemplateId"+
                        "   ) " +
                        "   Select * from emp_appraisal_paper where parent_id is not null order by template_id,indx_ing";
                Query query = context.createNativeQuery(str,Tuple.class);
                query.setParameter("empAppraisalTemplateId", Integer.parseInt(empAppraisalTemplateId));
                return query.getResultList();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean deleteAppraisalElements(Integer appraisalTemplateId) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                Query query = context.createNativeQuery("update emp_appraisal_elements set record_status='D' where emp_appraisal_template_id=:appraisalTemplateId");
                query.setParameter("appraisalTemplateId",appraisalTemplateId);
                return query.executeUpdate() > 0;
            }

            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean checkDuplicateTemplateCode(EmpAppraisalTemplateDTO empAppraisalTemplateDTO) throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) throws Exception {
                String str = "select eat.emp_appraisal_template_id from emp_appraisal_template eat "+
                        " where eat.template_code=:templateCode and eat.emp_appraisal_template_id not in (:templateId)";
                Query query = context.createNativeQuery(str);
                query.setParameter("templateCode", empAppraisalTemplateDTO.templateCode);
                query.setParameter("templateId", empAppraisalTemplateDTO.id);
                if(Utils.isNullOrEmpty(Utils.getUniqueResult(query.getResultList()))){
                    return false;
                }else{
                    return true;
                }
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public Boolean checkDuplicate(EmpAppraisalTemplateDTO empAppraisalTemplateDTO) throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
            @Override
            public Boolean onRun(EntityManager context) throws Exception {
                String str = "select eat.emp_appraisal_template_id from emp_appraisal_template eat "+
                        " where eat.record_status like 'A' and eat.emp_employee_category_id=:employeeCategoryId and eat.appraisal_type=:appraisalType and eat.template_code=:templateCode and eat.emp_appraisal_template_id not in (:templateId)";
                Query query = context.createNativeQuery(str);
                query.setParameter("employeeCategoryId", Integer.parseInt(empAppraisalTemplateDTO.employeeCategory.id));
                query.setParameter("appraisalType", empAppraisalTemplateDTO.appraisalType.id);
                query.setParameter("templateCode", empAppraisalTemplateDTO.templateCode);
                query.setParameter("templateId", empAppraisalTemplateDTO.id);
                if(Utils.isNullOrEmpty(Utils.getUniqueResult(query.getResultList()))){
                    return false;
                }else{
                    return true;
                }
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean saveOrUpdate(EmpAppraisalTemplateDBO empAppraisalTemplateDBO) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if (Utils.isNullOrEmpty(empAppraisalTemplateDBO.id)) {
                    context.persist(empAppraisalTemplateDBO);
                } else {
                    context.merge(empAppraisalTemplateDBO);
                }
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public String getHighestElementIdentity() throws Exception{
        return DBGateway.runJPA(new ISelectGenericTransactional<String>() {
            @Override
            public String onRun(EntityManager context) throws Exception {
                String str = "select max(cast(eae.element_identity AS DECIMAL(10,5))) as elementIdentity from emp_appraisal_elements eae";
                Query query = context.createNativeQuery(str);
                return ((BigDecimal) Utils.getUniqueResult(query.getResultList())).toString();
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public boolean delete(Integer appraisalTemplateId, Integer userId) throws Exception {
        return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                EmpAppraisalTemplateDBO empAppraisalTemplateDBO = context.find(EmpAppraisalTemplateDBO.class, appraisalTemplateId);
                if(!Utils.isNullOrEmpty(empAppraisalTemplateDBO)){
                    empAppraisalTemplateDBO.modifiedUsersId = userId;
                    empAppraisalTemplateDBO.recordStatus = 'D';
                    Set<EmpAppraisalElementsDBO> elementsDBOSet = new HashSet<>();
                    empAppraisalTemplateDBO.elementsDBOSet.forEach(empAppraisalElementsDBO -> {
                        empAppraisalElementsDBO.modifiedUsersId = userId;
                        empAppraisalElementsDBO.recordStatus = 'D';
                        elementsDBOSet.add(empAppraisalElementsDBO);
                    });
                    empAppraisalTemplateDBO.elementsDBOSet = elementsDBOSet;
                    context.merge(empAppraisalTemplateDBO);
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
}
