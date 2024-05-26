package com.christ.erp.services.controllers.employee.recruitment;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoParameterDBO;
import com.christ.erp.services.dbqueries.employee.RecruitmentQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoHeadingDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoParameterDTO;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/ResearchAndAdditionalDetailsSetup")
public class ResearchAndAdditionalDetailsSetupController extends BaseApiController {

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
    public Mono<ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>>> getGridData(){
        ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>> result = new ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
                Query query = context.createNativeQuery(RecruitmentQueries.RESEARCH_DETAILS_SEARCH_ALL, Tuple.class);
                @SuppressWarnings("unchecked")
				List<Tuple> mappings = query.getResultList();
                if(mappings != null && mappings.size() > 0) {
                    result.success = true;
                    result.dto = new ArrayList<EmpApplnAddtnlInfoHeadingDTO>();
                    for(Tuple mapping : mappings) {
                        EmpApplnAddtnlInfoHeadingDTO mappingInfo = new EmpApplnAddtnlInfoHeadingDTO();
                        mappingInfo.id = String.valueOf(mapping.get("ID"));
                        mappingInfo.category = new ExModelBaseDTO();
                        mappingInfo.category.text = String.valueOf(mapping.get("CategoryText"));
                        mappingInfo.groupHeading = String.valueOf(mapping.get("Group Heading"));
                        mappingInfo.displayOrder = String.valueOf(mapping.get("Display order"));
                        mappingInfo.isTypeResearch = (Boolean) mapping.get("Research");
                        result.dto.add(mappingInfo);
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
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpApplnAddtnlInfoHeadingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        if (data != null) {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    result.success = false;
                    //Server side validation
                    if(!Utils.isNullOrWhitespace(data.id) == false && Utils.isNullOrWhitespace(data.category.text)) {
                        result.failureMessage = "Please select Employee Category.";
                    }else if(Utils.isNullOrEmpty(data.isTypeResearch) == true) {
                        result.failureMessage = "Please select Research.";
                    }
                    else if(Utils.isNullOrWhitespace(data.displayOrder)) {
                        result.failureMessage = "Please enter Display Order.";
                    }
                    else if(Utils.isNullOrWhitespace(data.groupHeading)) {
                        result.failureMessage = "Please enter Group Heading.";
                    }
                    else if(data.parameters.size()>=1) {
                        for (EmpApplnAddtnlInfoParameterDTO item : data.parameters) {
                            if(Utils.isNullOrWhitespace(item.displayOrder)) {
                                result.failureMessage = "Please enter Display Order.";
                            }
                            else if(Utils.isNullOrWhitespace(item.researchParameter)) {
                                result.failureMessage = "Please enter Research/Additional Parameter.";
                            }
                            else if(Utils.isNullOrEmpty(item.isDisplayInApplication) == true) {
                                result.failureMessage = "Please select Display in Application.";
                            }
                        }
                    }
                    else if(Utils.isNullOrWhitespace(data.id) == false && Utils.isNullOrWhitespace(data.category.id)) {
                        result.failureMessage = "Please select Employee Category.";
                    }
                    //Duplicate Check
                    Query queryResult = context.createNativeQuery(RecruitmentQueries.RESEARCH_DETAILS_SEARCH_ALL, Tuple.class);
                    @SuppressWarnings("unchecked")
                    List<Tuple> mappingsResult = queryResult.getResultList();
                    if(mappingsResult != null && mappingsResult.size() > 0) {
                        for(Tuple mapping : mappingsResult) {
                            if(data.id == null || data.id.isEmpty()) {
                                if(data.category.text.equalsIgnoreCase(String.valueOf(mapping.get("CategoryText")))) {
                                    if(data.displayOrder.equalsIgnoreCase( String.valueOf(mapping.get("Display order")))) {
                                        result.failureMessage = "Duplicate entry for Display Order "+data.displayOrder;
                                    }
                                    if(data.groupHeading.trim().equalsIgnoreCase( String.valueOf(mapping.get("Group Heading")).trim())) {
                                        result.failureMessage = !Utils.isNullOrWhitespace(result.failureMessage) ?
                                                result.failureMessage +" and duplicate entry for Group Heading "+data.groupHeading : "Duplicate entry for Group Heading "+data.groupHeading;
                                    }
                                }
                            }else if(!data.id.equalsIgnoreCase(String.valueOf(mapping.get("ID")))){
                                if(data.category.id.equalsIgnoreCase(String.valueOf(mapping.get("CategoryID")))) {
                                    if(data.displayOrder.equalsIgnoreCase( String.valueOf(mapping.get("Display order")))) {
                                        result.failureMessage = "Duplicate entry for Display Order "+data.displayOrder;
                                    }
                                    if(data.groupHeading.trim().equalsIgnoreCase(String.valueOf( mapping.get("Group Heading")).trim())) {
                                        result.failureMessage = !Utils.isNullOrWhitespace(result.failureMessage) ?
                                                result.failureMessage +" and duplicate entry for Group Heading "+data.groupHeading : "Duplicate entry for Group Heading "+data.groupHeading;
                                    }
                                }
                            }
                        }
                    }
                    if(Utils.isNullOrWhitespace(result.failureMessage)) {
                        EmpApplnAddtnlInfoHeadingDBO empApplnAddtnlInfoHeading = null;

                        if(Utils.isNullOrWhitespace(data.id) == false) {
                            empApplnAddtnlInfoHeading = context.find(EmpApplnAddtnlInfoHeadingDBO.class,Integer.parseInt(data.id));
                        }
                        if(empApplnAddtnlInfoHeading == null) {
                            empApplnAddtnlInfoHeading = new EmpApplnAddtnlInfoHeadingDBO();
                            empApplnAddtnlInfoHeading.createdUsersId = Integer.parseInt(userId);
                        }
                        empApplnAddtnlInfoHeading.isTypeResearch = data.isTypeResearch;
                        empApplnAddtnlInfoHeading.addtnlInfoHeadingName = data.groupHeading;
                        empApplnAddtnlInfoHeading.headingDisplayOrder = Integer.parseInt(data.displayOrder);
                        empApplnAddtnlInfoHeading.recordStatus = 'A';
                        EmpEmployeeCategoryDBO employeeCategory = new EmpEmployeeCategoryDBO();
                        employeeCategory.id = Integer.parseInt(data.category.id);
                        empApplnAddtnlInfoHeading.empEmployeeCategoryId = employeeCategory;
                        Set<EmpApplnAddtnlInfoParameterDBO> empApplnAddtnlInfoParameterMap = new HashSet<>();
                        Set<EmpApplnAddtnlInfoParameterDBO> deleteSet= empApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap;
                        List<Integer> detailIds = new ArrayList<>();
                        for(EmpApplnAddtnlInfoParameterDTO item : data.parameters) {
                            EmpApplnAddtnlInfoParameterDBO detail = null;
                                if(empApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap!=null) {
                                    for(EmpApplnAddtnlInfoParameterDBO bo : deleteSet) {
                                        if(Utils.isNullOrWhitespace(item.id) == false) {
                                            if(Integer.parseInt(item.id) == bo.id) {
                                                detail = context.find(EmpApplnAddtnlInfoParameterDBO.class, Integer.parseInt(item.id));
                                                deleteSet.remove(bo);
                                                break;
                                            }
                                        }else if(Utils.isNullOrWhitespace(item.id)){
                                            deleteSet.add(bo);
                                            break;
                                        }
                                    }
                                }
                            if(detail == null) {
                                detail = new EmpApplnAddtnlInfoParameterDBO();
                                detail.createdUsersId = Integer.parseInt(userId);
                                detail.recordStatus = 'A';
                            }else {
                                detail.modifiedUsersId = Integer.parseInt(userId);
                            }
                            detail.parameterDisplayOrder = Integer.parseInt(item.displayOrder);
                            detail.addtnlInfoParameterName = item.researchParameter;
                            detail.isDisplayInApplication = item.isDisplayInApplication;
                            detail.empApplnAddtnlInfoHeading = empApplnAddtnlInfoHeading;
                            detail.recordStatus = 'A';
                            detailIds.add(detail.id);
                            empApplnAddtnlInfoParameterMap.add(detail);
                        }
                        if(deleteSet!=null) {
                            for(EmpApplnAddtnlInfoParameterDBO bo : deleteSet) {
                                bo.recordStatus = 'D';
                                bo.modifiedUsersId = Integer.parseInt(userId);
                                empApplnAddtnlInfoParameterMap.add(bo);
                            }
                        }
                        empApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap = empApplnAddtnlInfoParameterMap;
                        if(empApplnAddtnlInfoHeading.id == null) {
                            context.persist(empApplnAddtnlInfoHeading);
                        }else {
                            empApplnAddtnlInfoHeading.modifiedUsersId = Integer.parseInt(userId);
                            context.merge(empApplnAddtnlInfoHeading);
                        }
                        result.success = true;
                        result.dto = new ModelBaseDTO();
                        result.dto.id = String.valueOf(empApplnAddtnlInfoHeading.id);
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
    public Mono<ApiResult<EmpApplnAddtnlInfoHeadingDTO>> edit(@RequestParam("id") String id){
        ApiResult<EmpApplnAddtnlInfoHeadingDTO> result = new ApiResult<EmpApplnAddtnlInfoHeadingDTO>();
        DBGateway.runJPA(new ITransactional() {
            @Override
            public void onRun(EntityManager context) {
                EmpApplnAddtnlInfoHeadingDBO dbEmpApplnAddtnlInfoHeading = context.find(EmpApplnAddtnlInfoHeadingDBO.class, Integer.parseInt(id));
                if(dbEmpApplnAddtnlInfoHeading != null) {
                    result.success = true;
                    result.dto = new EmpApplnAddtnlInfoHeadingDTO();
                    result.dto.id = String.valueOf(dbEmpApplnAddtnlInfoHeading.id);
                    result.dto.isTypeResearch = dbEmpApplnAddtnlInfoHeading.isTypeResearch;
                    result.dto.category = new ExModelBaseDTO();
                    result.dto.category.id = String.valueOf(dbEmpApplnAddtnlInfoHeading.empEmployeeCategoryId.id);
                    result.dto.groupHeading = String.valueOf(dbEmpApplnAddtnlInfoHeading.addtnlInfoHeadingName);
                    result.dto.displayOrder = String.valueOf(dbEmpApplnAddtnlInfoHeading.headingDisplayOrder);
                    result.dto.parameters = new ArrayList<EmpApplnAddtnlInfoParameterDTO>();
                    if(dbEmpApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap != null && dbEmpApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap.size() > 0) {
                        for(EmpApplnAddtnlInfoParameterDBO item : dbEmpApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap) {
                            EmpApplnAddtnlInfoParameterDTO parameterInfo = new EmpApplnAddtnlInfoParameterDTO();
                            if(item.recordStatus == 'A') {
                                parameterInfo.id = String.valueOf(item.id);
                                parameterInfo.displayOrder = String.valueOf(item.parameterDisplayOrder);
                                parameterInfo.researchParameter = String.valueOf(item.addtnlInfoParameterName);
                                parameterInfo.isDisplayInApplication = item.isDisplayInApplication;
                                result.dto.parameters.add(parameterInfo);
                            }
                        }
                        Collections.sort(result.dto.parameters, new Comparator<EmpApplnAddtnlInfoParameterDTO>() {
                            @Override
                            public int compare(EmpApplnAddtnlInfoParameterDTO o1, EmpApplnAddtnlInfoParameterDTO o2) {
                                return Integer.compare(Integer.parseInt(o1.displayOrder), Integer.parseInt(o2.displayOrder));
                            }
                        });
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
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("headingId") String headingId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        if(headingId != null) {
            DBGateway.runJPA(new ITransactional() {
                @Override
                public void onRun(EntityManager context) {
                    EmpApplnAddtnlInfoHeadingDBO headingDBO = null;
                    if(!Utils.isNullOrEmpty(headingId) && !Utils.isNullOrWhitespace(headingId)) {
                        headingDBO = context.find(EmpApplnAddtnlInfoHeadingDBO.class, Integer.parseInt(headingId));
                        headingDBO.recordStatus = 'D';
                        headingDBO.modifiedUsersId = Integer.parseInt(userId);
                        Set<EmpApplnAddtnlInfoParameterDBO> subSet = new HashSet<EmpApplnAddtnlInfoParameterDBO>();
                        for(EmpApplnAddtnlInfoParameterDBO parameterDBO : headingDBO.empApplnAddtnlInfoParameterMap) {
                            parameterDBO.recordStatus = 'D';
                            parameterDBO.modifiedUsersId = Integer.parseInt(userId);
                            subSet.add(parameterDBO);
                        }
                        headingDBO.empApplnAddtnlInfoParameterMap = subSet;
                        context.merge(headingDBO);
                        result.success = true;
                        result.dto = new ModelBaseDTO();
                        result.dto.id = String.valueOf(headingDBO.id);
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