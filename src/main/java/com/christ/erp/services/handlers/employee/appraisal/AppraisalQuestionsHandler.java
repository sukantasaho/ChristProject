package com.christ.erp.services.handlers.employee.appraisal;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalElementsDBO;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalElementsOptionDBO;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalTemplateDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalElementsDTO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalTemplateDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.appraisal.AppraisalQuestionsTransaction;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppraisalQuestionsHandler {
    private static volatile AppraisalQuestionsHandler appraisalQuestionsHandler = null;
    private static volatile AppraisalQuestionsTransaction appraisalQuestionsTransaction = AppraisalQuestionsTransaction.getInstance();

    public static AppraisalQuestionsHandler getInstance() {
        if (appraisalQuestionsHandler == null) {
            appraisalQuestionsHandler = new AppraisalQuestionsHandler();
        }
        return appraisalQuestionsHandler;
    }

    public List<EmpAppraisalTemplateDTO> getGridData() throws Exception{
        List<Tuple> list = appraisalQuestionsTransaction.getGridData();
        List<EmpAppraisalTemplateDTO> dtoList = null;
        if(!Utils.isNullOrEmpty(list)) {
            dtoList = new ArrayList<>();
            for(Tuple tuple : list) {
                EmpAppraisalTemplateDTO templateDTO = new EmpAppraisalTemplateDTO();
                templateDTO.id = Integer.parseInt(String.valueOf(tuple.get("empAppraisalTemplateId")));
                templateDTO.employeeCategory = new ExModelBaseDTO();
                templateDTO.employeeCategory.id = String.valueOf(tuple.get("empEmployeeCategoryId"));
                templateDTO.employeeCategory.text = String.valueOf(tuple.get("employeeCategoryName"));
                templateDTO.templateName = String.valueOf(tuple.get("templateName"));
                templateDTO.templateCode = String.valueOf(tuple.get("templateCode"));
                templateDTO.appraisalType = new ExModelBaseDTO();
                templateDTO.appraisalType.id = String.valueOf(tuple.get("appraisalType"));
                templateDTO.appraisalType.text = String.valueOf(tuple.get("appraisalType"));
                dtoList.add(templateDTO);
            }
            dtoList.sort((o1, o2) -> {
                int comp = o1.employeeCategory.text.compareTo(o2.employeeCategory.text);
                if(comp == 0) {
                    return o1.appraisalType.text.compareTo(o2.appraisalType.text);
                }else {
                    return comp;
                }
            });
        }
        return dtoList;
    }

    public EmpAppraisalTemplateDTO edit(Map<String,String> data) throws Exception{
        List<Tuple> appraisalElementsData = appraisalQuestionsTransaction.getAppraisalElementsData(data.get("empAppraisalTemplateId"));
        if(!Utils.isNullOrEmpty(appraisalElementsData)){
            EmpAppraisalTemplateDTO empAppraisalTemplateDTO = new EmpAppraisalTemplateDTO();
            empAppraisalTemplateDTO.id = Integer.parseInt(data.get("empAppraisalTemplateId"));
            empAppraisalTemplateDTO.templateName = data.get("templateName");
            empAppraisalTemplateDTO.templateCode = data.get("templateCode");
            if(!Utils.isNullOrEmpty(data.get("empEmployeeCategoryId"))) {
                empAppraisalTemplateDTO.employeeCategory = new ExModelBaseDTO();
                empAppraisalTemplateDTO.employeeCategory.id = data.get("empEmployeeCategoryId");
            }
            if(!Utils.isNullOrEmpty(data.get("appraisalTypeId"))) {
                empAppraisalTemplateDTO.appraisalType = new ExModelBaseDTO();
                empAppraisalTemplateDTO.appraisalType.id = data.get("appraisalTypeId");
            }
            List<EmpAppraisalElementsDTO> elementsDTOS = new ArrayList<>();
            for(Tuple tuple : appraisalElementsData){
                EmpAppraisalElementsDTO elementsDTO = new EmpAppraisalElementsDTO();
                elementsDTO.id = Integer.parseInt(String.valueOf(tuple.get(0)));
                elementsDTO.elementName = String.valueOf(tuple.get(1));
                elementsDTO.elementDescription = String.valueOf(tuple.get(2));
                elementsDTO.elementIdentity = String.valueOf(tuple.get(3));
                elementsDTO.elementParentIdentity = String.valueOf(tuple.get(4));
                elementsDTO.elementOrder = Integer.parseInt(String.valueOf(tuple.get(5)));
                elementsDTO.elementLevel = Integer.parseInt(String.valueOf(tuple.get(7)));
                elementsDTO.isGroupNotDisplayed = !Utils.isNullOrEmpty(tuple.get(8)) ? "1".equalsIgnoreCase(tuple.get(8).toString()) ? "true" : "false" : null;
                elementsDTO.type = new ExModelBaseDTO();
                if(!Utils.isNullOrEmpty(tuple.get(9))){
                    elementsDTO.type.id = "1".equalsIgnoreCase(tuple.get(9).toString()) ? "Question" : "Group";
                }
                elementsDTO.answerOptionSelectionType = new ExModelBaseDTO();
                if(!Utils.isNullOrEmpty(tuple.get(10))){
                    elementsDTO.answerOptionSelectionType.id = tuple.get(10).toString();
                    elementsDTO.answerOptionSelectionType.text = tuple.get(10).toString();
                }
                elementsDTO.empAppraisalElementsOption = new ExModelBaseDTO();
                if(!Utils.isNullOrEmpty(tuple.get(11))){
                    elementsDTO.empAppraisalElementsOption.id =  tuple.get(11).toString();
                }
                elementsDTOS.add(elementsDTO);
            }
            empAppraisalTemplateDTO.appraisalList = elementsDTOS;
            return empAppraisalTemplateDTO;
        }
        return null;
    }

    public boolean saveOrUpdate(EmpAppraisalTemplateDTO empAppraisalTemplateDTO, Integer userId, ApiResult result) throws Exception{
        if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO)){
            boolean isDuplicateCode = appraisalQuestionsTransaction.checkDuplicateTemplateCode(empAppraisalTemplateDTO);
            if(isDuplicateCode){
                result.failureMessage = "Duplicate entry exist for Code: "+empAppraisalTemplateDTO.templateCode+ ".";
            }else{
                boolean isDuplicate = appraisalQuestionsTransaction.checkDuplicate(empAppraisalTemplateDTO);
                if(isDuplicate){
                    result.failureMessage = "Duplicate entry exist for Code: "+empAppraisalTemplateDTO.templateCode+ " and Employee Category: "+empAppraisalTemplateDTO.employeeCategory.text +" and Type: "+empAppraisalTemplateDTO.appraisalType.text +".";
                }else{
                    EmpAppraisalTemplateDBO empAppraisalTemplateDBO;
                    if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO.id)){
                        appraisalQuestionsTransaction.deleteAppraisalElements(empAppraisalTemplateDTO.id);
                        empAppraisalTemplateDBO = CommonApiTransaction.getInstance().find(EmpAppraisalTemplateDBO.class, empAppraisalTemplateDTO.id);
                        empAppraisalTemplateDBO.modifiedUsersId = userId;
                    }else{
                        empAppraisalTemplateDBO = new EmpAppraisalTemplateDBO();
                        empAppraisalTemplateDBO.createdUsersId = userId;
                    }
                    if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO.templateName))
                        empAppraisalTemplateDBO.templateName = empAppraisalTemplateDTO.templateName;
                    if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO.templateCode))
                        empAppraisalTemplateDBO.templateCode = empAppraisalTemplateDTO.templateCode;
                    if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO.employeeCategory) && !Utils.isNullOrEmpty(empAppraisalTemplateDTO.employeeCategory.id)) {
                        empAppraisalTemplateDBO.empEmployeeCategoryDBO = new EmpEmployeeCategoryDBO();
                        empAppraisalTemplateDBO.empEmployeeCategoryDBO.id = Integer.parseInt(empAppraisalTemplateDTO.employeeCategory.id);
                    }
                    if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO.appraisalType) && !Utils.isNullOrEmpty(empAppraisalTemplateDTO.appraisalType.id)) {
                        empAppraisalTemplateDBO.appraisalType = empAppraisalTemplateDTO.appraisalType.id;
                    }
                    empAppraisalTemplateDBO.recordStatus = 'A';
                    Set<EmpAppraisalElementsDBO> empAppraisalElementsDBOSet = new LinkedHashSet<>();
                    int identity = 0;
                    int count = 0;
                    String elementIdentity = appraisalQuestionsTransaction.getHighestElementIdentity();
                    int templateId = !Utils.isNullOrEmpty(elementIdentity) ? Integer.parseInt(elementIdentity.split("\\.")[0]) + 1 : 1;
                    String elementParentIdentity = templateId + "." + identity;
                    List<EmpAppraisalElementsDTO> elementsDTOS = new LinkedList<>();
                    for(EmpAppraisalElementsDTO dto : empAppraisalTemplateDTO.appraisalList){
                        dto.elementIdentity = templateId + "." + (++identity);
                        elementsDTOS.add(dto);
                    }
                    for(int i=0; i<elementsDTOS.size(); i++){
                        if(count == 0){
                            EmpAppraisalElementsDBO empAppraisalElementsDBO = new EmpAppraisalElementsDBO();
                            empAppraisalElementsDBO.elementIdentity = elementParentIdentity;
                            empAppraisalElementsDBO.elementOrder = 1;
                            empAppraisalElementsDBO.elementLevel = 0;
                            empAppraisalElementsDBO.empAppraisalTemplateDBO = empAppraisalTemplateDBO;
                            empAppraisalElementsDBO.isGroupNotDisplayed = false;
                            empAppraisalElementsDBO.createdUsersId = userId;
                            empAppraisalElementsDBO.recordStatus = 'A';
                            empAppraisalElementsDBOSet.add(empAppraisalElementsDBO);
                            count++;
                        }
                        EmpAppraisalElementsDBO empAppraisalElementsDBO = new EmpAppraisalElementsDBO();
                        EmpAppraisalElementsDTO elementsDTO = elementsDTOS.get(i);
                        setAppraisalElementDBOData(elementsDTO,empAppraisalElementsDBO,empAppraisalTemplateDBO);
                        empAppraisalElementsDBO.elementParentIdentity = findParentIdentity(i,elementsDTOS,elementParentIdentity);
                        empAppraisalElementsDBO.createdUsersId = userId;
                        empAppraisalElementsDBO.recordStatus = 'A';
                        empAppraisalElementsDBOSet.add(empAppraisalElementsDBO);
                    }
                    empAppraisalTemplateDBO.elementsDBOSet = empAppraisalElementsDBOSet;
                    return appraisalQuestionsTransaction.saveOrUpdate(empAppraisalTemplateDBO);
                }
            }
        }
        return false;
    }

    public String findParentIdentity(int index, List<EmpAppraisalElementsDTO> appraisalList, String elementParentIdentity) throws Exception{
        String parentIdentity = "";
        if(appraisalList.get(index).elementLevel == 1){
            parentIdentity = elementParentIdentity;
        }else if("Group".equalsIgnoreCase(appraisalList.get(index).type.id)){
            for(int i=index-1; i>=0 && i<appraisalList.size(); i--){
                if("Group".equalsIgnoreCase(appraisalList.get(i).type.id)){
                    if(appraisalList.get(index).elementLevel == appraisalList.get(i).elementLevel){
                        parentIdentity = appraisalList.get(i).elementParentIdentity;
                        appraisalList.get(index).elementParentIdentity = parentIdentity;
                        break;
                    }else{
                        parentIdentity = appraisalList.get(i).elementIdentity;
                        appraisalList.get(index).elementParentIdentity = parentIdentity;
                        break;
                    }
                }
            }
        }else if("Question".equalsIgnoreCase(appraisalList.get(index).type.id)){
            for(int i=index-1; i>=0 && i<appraisalList.size(); i--){
                if("Group".equalsIgnoreCase(appraisalList.get(i).type.id)){
                    parentIdentity = appraisalList.get(i).elementIdentity;
                    appraisalList.get(index).elementParentIdentity = parentIdentity;
                    break;
                }else{
                    parentIdentity = appraisalList.get(i).elementParentIdentity;
                    appraisalList.get(index).elementParentIdentity = parentIdentity;
                    break;
                }
            }
        }
        return parentIdentity;
    }

    public void setAppraisalElementDBOData(EmpAppraisalElementsDTO elementsDTO, EmpAppraisalElementsDBO empAppraisalElementsDBO, EmpAppraisalTemplateDBO empAppraisalTemplateDBO) throws Exception{
        empAppraisalElementsDBO.elementName = !Utils.isNullOrEmpty(elementsDTO.elementName) ? elementsDTO.elementName : null;
        empAppraisalElementsDBO.elementDescription = !Utils.isNullOrEmpty(elementsDTO.elementDescription) ? elementsDTO.elementDescription : null;
        empAppraisalElementsDBO.elementLevel = !Utils.isNullOrEmpty(elementsDTO.elementLevel) ? elementsDTO.elementLevel : null;
        empAppraisalElementsDBO.elementOrder = !Utils.isNullOrEmpty(elementsDTO.elementOrder) ? elementsDTO.elementOrder : null;
        empAppraisalElementsDBO.empAppraisalTemplateDBO = empAppraisalTemplateDBO;
        if(!Utils.isNullOrEmpty(elementsDTO.type) && !Utils.isNullOrEmpty(elementsDTO.type.id)){
            if("Group".equalsIgnoreCase(elementsDTO.type.id)){
                empAppraisalElementsDBO.isQuestion = false;
            }else{
                empAppraisalElementsDBO.isQuestion = true;
                if(!Utils.isNullOrEmpty(elementsDTO.answerOptionSelectionType) && !Utils.isNullOrEmpty(elementsDTO.answerOptionSelectionType.id)){
                    empAppraisalElementsDBO.answerOptionSelectionType = elementsDTO.answerOptionSelectionType.id;
                }
                if(!Utils.isNullOrEmpty(elementsDTO.empAppraisalElementsOption) && !Utils.isNullOrEmpty(elementsDTO.empAppraisalElementsOption.id)){
                    empAppraisalElementsDBO.empAppraisalElementsOptionDBO = new EmpAppraisalElementsOptionDBO();
                    empAppraisalElementsDBO.empAppraisalElementsOptionDBO.id = Integer.parseInt(elementsDTO.empAppraisalElementsOption.id);
                }
            }
        }
        empAppraisalElementsDBO.elementIdentity = !Utils.isNullOrEmpty(elementsDTO.elementIdentity) ? elementsDTO.elementIdentity : null;
    }

    public boolean delete(@NotNull Map<String, String> data, Integer userId) throws Exception{
        try{
            return appraisalQuestionsTransaction.delete(Integer.parseInt(data.get("empAppraisalTemplateId")),userId);
        }catch (Exception e){
            throw e;
        }
    }
}
