package com.christ.erp.services.controllers.employee.recruitment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewTemplateGroupDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewTemplateGroupDetailsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewTemplateDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewTemplateDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewTemplateGroupDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewTemplateGroupDetailsDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/InterviewScoreCardSettings")
@SuppressWarnings("unchecked")
public class InterviewScoreCardSettingsController extends BaseApiController{
	
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
    public Mono<ApiResult<List<EmpApplnInterviewTemplateDTO>>> getGridData(){
        ApiResult<List<EmpApplnInterviewTemplateDTO>> result = new ApiResult<List<EmpApplnInterviewTemplateDTO>>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
            	Query query = context.createQuery("from EmpApplnInterviewTemplateDBO bo where bo.recordStatus='A' ");
            	List<EmpApplnInterviewTemplateDBO> templateList = query.getResultList();
            	List<Tuple> mappings = query.getResultList();
                if(mappings != null && mappings.size() > 0) {
                    result.success = true;
                    result.dto = new ArrayList<EmpApplnInterviewTemplateDTO>();
                    for(EmpApplnInterviewTemplateDBO mapping : templateList) {
                    	if(mapping.getRecordStatus() == 'A') {
                    		EmpApplnInterviewTemplateDTO mappingInfo = new EmpApplnInterviewTemplateDTO();
                            mappingInfo.id = String.valueOf(mapping.getId());
                            mappingInfo.category = new ExModelBaseDTO();
                            mappingInfo.category.text = mapping.getEmpEmployeeCategoryDBO().getEmployeeCategoryName();
                            mappingInfo.interviewName = mapping.getInterviewName();
                            result.dto.add(mappingInfo);
                    	}
                    }
                }
                result.success = true;
            }
            @Override
            public void onError(Exception error) {
                result.success = false;
                result.dto = null;
                result.failureMessage = error.getMessage();
            }
        });
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpApplnInterviewTemplateDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        if (data != null) {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    result.success = false;
                    Query query = context.createQuery("from EmpApplnInterviewTemplateDBO bo where bo.recordStatus='A' ");
                    List<EmpApplnInterviewTemplateDBO> templateList = query.getResultList();
                    List<Tuple> mappings = query.getResultList();
                    if(mappings != null && mappings.size() > 0) {
                        for(EmpApplnInterviewTemplateDBO mapping : templateList) {
                            if(data.id == null || data.id.isEmpty()) {
                                if(data.category.text.equalsIgnoreCase(mapping.getEmpEmployeeCategoryDBO().getEmployeeCategoryName())) {
                                    result.failureMessage = "Duplicate entry for Employee Category "+data.category.text;
                                }
                            }else if(!data.id.equalsIgnoreCase(String.valueOf(mapping.getId()))){
                                if(data.category.id.equalsIgnoreCase(String.valueOf(mapping.getEmpEmployeeCategoryDBO().id))) {
                                    result.failureMessage = "Duplicate entry for Employee Category "+data.category.text;
                                }
                            }
                        }
                    }
                    if(Utils.isNullOrWhitespace(result.failureMessage)) {
                        EmpApplnInterviewTemplateDBO empApplnInterviewTemplate = null;
                        if(Utils.isNullOrWhitespace(data.id) == false) {
                            empApplnInterviewTemplate = context.find(EmpApplnInterviewTemplateDBO.class,Integer.parseInt(data.id));
                        }
                        if(empApplnInterviewTemplate == null) {
                            empApplnInterviewTemplate = new EmpApplnInterviewTemplateDBO();
                            empApplnInterviewTemplate.setCreatedUsersId(Integer.parseInt(userId));
                        }
                        EmpEmployeeCategoryDBO employeeCategory = new EmpEmployeeCategoryDBO();
                        employeeCategory.id = Integer.parseInt(data.category.id);
                        empApplnInterviewTemplate.setEmpEmployeeCategoryDBO(employeeCategory);
                        empApplnInterviewTemplate.setInterviewName(data.interviewName);
                        empApplnInterviewTemplate.setIsPanelistCommentRequired(data.isPanelistCommentRequired);
                        empApplnInterviewTemplate.setRecordStatus('A');
                        Set<EmpApplnInterviewTemplateGroupDBO> empApplnInterviewTemplateGroupSet = new HashSet<>();
                        Set<EmpApplnInterviewTemplateGroupDBO> deleteEmpApplnInterviewTemplateGroupSet= empApplnInterviewTemplate.getEmpApplnInterviewTemplateGroup();
                        for(EmpApplnInterviewTemplateGroupDTO item : data.heading) {
                            EmpApplnInterviewTemplateGroupDBO empApplnInterviewTemplateGroupDBO = null;
                                if(empApplnInterviewTemplate.getEmpApplnInterviewTemplateGroup() != null) {
                                    for(EmpApplnInterviewTemplateGroupDBO bo : deleteEmpApplnInterviewTemplateGroupSet) {
                                        if(Utils.isNullOrWhitespace(item.id) == false) {
                                            if(Integer.parseInt(item.id) == bo.getId()) {
                                                empApplnInterviewTemplateGroupDBO = context.find(EmpApplnInterviewTemplateGroupDBO.class, Integer.parseInt(item.id));
                                                deleteEmpApplnInterviewTemplateGroupSet.remove(bo);
                                                break;
                                            }
                                        }else if(Utils.isNullOrWhitespace(item.id)){
                                            deleteEmpApplnInterviewTemplateGroupSet.add(bo);
                                            break;
                                        }
                                    }
                                }
                                if(empApplnInterviewTemplateGroupDBO == null) {
                                    empApplnInterviewTemplateGroupDBO = new EmpApplnInterviewTemplateGroupDBO();
                                    empApplnInterviewTemplateGroupDBO.setCreatedUsersId(Integer.parseInt(userId));
                                    empApplnInterviewTemplateGroupDBO.setRecordStatus('A');
                                }else {
                                    empApplnInterviewTemplateGroupDBO.setModifiedUsersId(Integer.parseInt(userId));
                                }
                                empApplnInterviewTemplateGroupDBO.setHeadingOrderNo(Integer.parseInt(item.headingOrderNo));
                                empApplnInterviewTemplateGroupDBO.setTemplateGroupHeading(item.templateGroupHeading);
                                empApplnInterviewTemplateGroupDBO.setEmpApplnInterviewTemplateDBO(empApplnInterviewTemplate);
                                empApplnInterviewTemplateGroupDBO.setRecordStatus('A');
                                Set<EmpApplnInterviewTemplateGroupDetailsDBO> empApplnInterviewTemplateGroupDetails = new HashSet<>();
                                Set<EmpApplnInterviewTemplateGroupDetailsDBO> deleteEmpApplnInterviewTemplateGroupDetailsSet= empApplnInterviewTemplateGroupDBO.getEmpApplnInterviewTemplateGroupDetails();
                                List<Integer> detailIds = new ArrayList<>();
                                for(EmpApplnInterviewTemplateGroupDetailsDTO item1 : item.parameters) {
                                     EmpApplnInterviewTemplateGroupDetailsDBO detail = null;
                                        if(empApplnInterviewTemplateGroupDBO.getEmpApplnInterviewTemplateGroupDetails() != null) {
                                            for(EmpApplnInterviewTemplateGroupDetailsDBO bo : deleteEmpApplnInterviewTemplateGroupDetailsSet) {
                                                if(Utils.isNullOrWhitespace(item1.id) == false) {
                                                    if(Integer.parseInt(item1.id) == bo.getId()) {
                                                        detail = context.find(EmpApplnInterviewTemplateGroupDetailsDBO.class, Integer.parseInt(item1.id));
                                                        deleteEmpApplnInterviewTemplateGroupDetailsSet.remove(bo);
                                                        break;
                                                    }
                                                }else if(Utils.isNullOrWhitespace(item1.id)){
                                                    deleteEmpApplnInterviewTemplateGroupDetailsSet.add(bo);
                                                    break;
                                                }
                                            }
                                        }
                                        if(detail == null) {
                                            detail = new EmpApplnInterviewTemplateGroupDetailsDBO();
                                            detail.setCreatedUsersId(Integer.parseInt(userId));
                                            detail.setRecordStatus('A');
                                        }else {
                                                detail.setModifiedUsersId(Integer.parseInt(userId));
                                            }
                                        detail.setParameterOrderNo(Integer.parseInt(item1.parameterOrderNo));
                                        detail.setParameterName(item1.parameterName);
                                        detail.setParameterMaxScore(Integer.parseInt(item1.parameterMaxScore));
                                        detail.setEmpApplnInterviewTemplateGroupDBO(empApplnInterviewTemplateGroupDBO);
                                        detail.setRecordStatus('A');
                                        detailIds.add(detail.getId());
                                        empApplnInterviewTemplateGroupDetails.add(detail);
                                 }
                                 if(deleteEmpApplnInterviewTemplateGroupDetailsSet!=null) {
                                    for(EmpApplnInterviewTemplateGroupDetailsDBO bo : deleteEmpApplnInterviewTemplateGroupDetailsSet) {
                                        bo.setRecordStatus('D');
                                        bo.setModifiedUsersId(Integer.parseInt(userId));
                                        empApplnInterviewTemplateGroupDetails.add(bo);
                                     }
                                 }
                                 empApplnInterviewTemplateGroupDBO.setEmpApplnInterviewTemplateGroupDetails(empApplnInterviewTemplateGroupDetails);
                                 empApplnInterviewTemplateGroupSet.add(empApplnInterviewTemplateGroupDBO);
                             }
                             if(deleteEmpApplnInterviewTemplateGroupSet!=null) {
                                for(EmpApplnInterviewTemplateGroupDBO bo : deleteEmpApplnInterviewTemplateGroupSet) {
                                    bo.setRecordStatus('D');
                                    bo.setModifiedUsersId(Integer.parseInt(userId));
                                    Set<EmpApplnInterviewTemplateGroupDetailsDBO> empApplnInterviewTemplateGroupDetails = new HashSet<>();
                                    for(EmpApplnInterviewTemplateGroupDetailsDBO bo1 : bo.getEmpApplnInterviewTemplateGroupDetails()) {
                                        bo1.setRecordStatus('D');
                                        bo1.setModifiedUsersId(Integer.parseInt(userId));
                                        empApplnInterviewTemplateGroupDetails.add(bo1);
                                     }
                                    bo.setEmpApplnInterviewTemplateGroupDetails(empApplnInterviewTemplateGroupDetails);
                                    empApplnInterviewTemplateGroupSet.add(bo);
                                 }
                             }
                             empApplnInterviewTemplate.setEmpApplnInterviewTemplateGroup(empApplnInterviewTemplateGroupSet);
                             if(empApplnInterviewTemplate.getId() == 0) {
                                context.persist(empApplnInterviewTemplate);
                             }else {
                                empApplnInterviewTemplate.setModifiedUsersId(Integer.parseInt(userId));
                                context.merge(empApplnInterviewTemplate);
                             }
                             result.success = true;
                             result.dto = new ModelBaseDTO();
                             result.dto.id = String.valueOf(empApplnInterviewTemplate.getId());
                    }
                }
                @Override
                public void onError(Exception error) {
                    result.success = false;
                    result.dto = null;
                    result.failureMessage = error.getMessage();
                }
            });
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Mono<ApiResult<EmpApplnInterviewTemplateDTO>> edit(@RequestParam("id") String id){
        ApiResult<EmpApplnInterviewTemplateDTO> result = new ApiResult<EmpApplnInterviewTemplateDTO>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
            	EmpApplnInterviewTemplateDBO empApplnInterviewTemplate = context.find(EmpApplnInterviewTemplateDBO.class, Integer.parseInt(id));
                if(empApplnInterviewTemplate != null) {
                	if(empApplnInterviewTemplate.getRecordStatus() == 'A') {
                		result.success = true;
                        result.dto = new EmpApplnInterviewTemplateDTO();
                        result.dto.id = String.valueOf(empApplnInterviewTemplate.getId());
                        result.dto.category = new ExModelBaseDTO();
                        result.dto.category.id = String.valueOf(empApplnInterviewTemplate.getEmpEmployeeCategoryDBO().getId());
                        result.dto.category.text = empApplnInterviewTemplate.getEmpEmployeeCategoryDBO().getEmployeeCategoryName();
                        result.dto.interviewName = empApplnInterviewTemplate.getInterviewName();
                        result.dto.isPanelistCommentRequired = empApplnInterviewTemplate.getIsPanelistCommentRequired();
                        result.dto.heading = new ArrayList<EmpApplnInterviewTemplateGroupDTO>();
                        if(empApplnInterviewTemplate.getEmpApplnInterviewTemplateGroup() != null && empApplnInterviewTemplate.getEmpApplnInterviewTemplateGroup().size() > 0) {
                            for(EmpApplnInterviewTemplateGroupDBO item : empApplnInterviewTemplate.getEmpApplnInterviewTemplateGroup()) {
                            	EmpApplnInterviewTemplateGroupDTO headingInfo = new EmpApplnInterviewTemplateGroupDTO();
                                if(item.getRecordStatus() == 'A') {
                                	headingInfo.id = String.valueOf(item.getId());
                                	headingInfo.headingOrderNo = String.valueOf(item.getHeadingOrderNo());
                                	headingInfo.templateGroupHeading = String.valueOf(item.getTemplateGroupHeading());
                                	headingInfo.parameters = new ArrayList<EmpApplnInterviewTemplateGroupDetailsDTO>();
                                    if(item.getEmpApplnInterviewTemplateGroupDetails() != null && item.getEmpApplnInterviewTemplateGroupDetails().size() > 0) {
                                        for(EmpApplnInterviewTemplateGroupDetailsDBO item1 : item.getEmpApplnInterviewTemplateGroupDetails()) {
                                        	EmpApplnInterviewTemplateGroupDetailsDTO parameterInfo = new EmpApplnInterviewTemplateGroupDetailsDTO();
                                            if(item1.getRecordStatus() == 'A') {
                                                parameterInfo.id = String.valueOf(item1.getId());
                                                parameterInfo.parameterOrderNo = String.valueOf(item1.getParameterOrderNo());
                                                parameterInfo.parameterName = String.valueOf(item1.getParameterName());
                                                parameterInfo.parameterMaxScore = String.valueOf(item1.getParameterMaxScore());
                                                headingInfo.parameters.add(parameterInfo);
                                            }
                                        }
                                        Collections.sort(headingInfo.parameters, new Comparator<EmpApplnInterviewTemplateGroupDetailsDTO>() {
                                            @Override
                                            public int compare(EmpApplnInterviewTemplateGroupDetailsDTO o1, EmpApplnInterviewTemplateGroupDetailsDTO o2) {
                                                return Integer.compare(Integer.parseInt(o1.parameterOrderNo), Integer.parseInt(o2.parameterOrderNo));
                                            }
                                        });
                                    }
                                    result.dto.heading.add(headingInfo);
                                }
                            }
                            Collections.sort(result.dto.heading, new Comparator<EmpApplnInterviewTemplateGroupDTO>() {
                                @Override
                                public int compare(EmpApplnInterviewTemplateGroupDTO o1, EmpApplnInterviewTemplateGroupDTO o2) {
                                    return Integer.compare(Integer.parseInt(o1.headingOrderNo), Integer.parseInt(o2.headingOrderNo));
                                }
                            });
                        }
                	}
                }
            }
            @Override
            public void onError(Exception error) {
                result.success = false;
                result.dto = null;
                result.failureMessage = error.getMessage();
            }
        },true);
        return Utils.monoFromObject(result);
    }

    @RequestMapping( value = "/delete", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("templateId") String templateId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        if(templateId != null) {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    EmpApplnInterviewTemplateDBO templateDBO = null;
                    if(!Utils.isNullOrEmpty(templateId) && !Utils.isNullOrWhitespace(templateId)) {
                        templateDBO = context.find(EmpApplnInterviewTemplateDBO.class, Integer.parseInt(templateId));
                        templateDBO.setRecordStatus('D');
                        templateDBO.setModifiedUsersId(Integer.parseInt(userId));
                        Set<EmpApplnInterviewTemplateGroupDBO> headingSubSet = new HashSet<EmpApplnInterviewTemplateGroupDBO>();
                        Set<EmpApplnInterviewTemplateGroupDetailsDBO> parameterSubSet = new HashSet<EmpApplnInterviewTemplateGroupDetailsDBO>();
                        for(EmpApplnInterviewTemplateGroupDBO headingDBO : templateDBO.getEmpApplnInterviewTemplateGroup()) {
                            headingDBO.setRecordStatus('D');
                            headingDBO.setModifiedUsersId(Integer.parseInt(userId));
                            for(EmpApplnInterviewTemplateGroupDetailsDBO parameterDBO : headingDBO.getEmpApplnInterviewTemplateGroupDetails()) {
                                parameterDBO.setRecordStatus('D');
                                parameterDBO.setModifiedUsersId(Integer.parseInt(userId));
                                parameterSubSet.add(parameterDBO);
                            }
                            headingDBO.setEmpApplnInterviewTemplateGroupDetails(parameterSubSet);
                            headingSubSet.add(headingDBO);
                        }
                        templateDBO.setEmpApplnInterviewTemplateGroup(headingSubSet);
                        context.merge(templateDBO);
                        result.success = true;
                        result.dto = new ModelBaseDTO();
                        result.dto.id = String.valueOf(templateDBO.getId());
                    }
                }
                @Override
                public void onError(Exception error) {
                    result.success = false;
                    result.dto = null;
                    result.failureMessage = error.getMessage();
                }
            });
        }
        return Utils.monoFromObject(result);
    }
}
