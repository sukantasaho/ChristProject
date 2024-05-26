package com.christ.erp.services.controllers.employee;
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
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoParameterDBO;
import com.christ.erp.services.dbqueries.employee.RecruitmentQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoHeadingDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoParameterDTO;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment")
@SuppressWarnings("unchecked")
public class RecruitmentController extends BaseApiController{ 
	
	@RequestMapping(value = "/getResearchAndAdditionalDetailsSetup", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>>> getResearchAndAdditionalDetailsSetup(){
		ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>> result = new ApiResult<List<EmpApplnAddtnlInfoHeadingDTO>>();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				Query query = context.createNativeQuery(RecruitmentQueries.RESEARCH_DETAILS_SEARCH_ALL, Tuple.class);
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
	
	@RequestMapping(value = "/saveOrUpdateResearchAndAdditionalDetailsSetup", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateResearchAndAdditionalDetailsSetup(@RequestBody EmpApplnAddtnlInfoHeadingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
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
					List<Tuple> mappingsResult = queryResult.getResultList();
					if(mappingsResult != null && mappingsResult.size() > 0) {
						for(Tuple mapping : mappingsResult) {
							if(data.id == null || data.id.isEmpty()) {
								if(data.category.text.equalsIgnoreCase(String.valueOf(mapping.get("CategoryText")))) {
									if(data.displayOrder.equalsIgnoreCase( String.valueOf(mapping.get("Display order")))) {
										result.failureMessage = "Duplicate entry for Display Order "+data.displayOrder;
									}
									if(data.groupHeading.trim().equalsIgnoreCase(String.valueOf( mapping.get("Group Heading")).trim())) {
										result.failureMessage = !Utils.isNullOrWhitespace(result.failureMessage) ?
										result.failureMessage +" and duplicate entry for Group Heading "+data.groupHeading : "Duplicate entry for Group Heading "+data.groupHeading;
									}
								}
							}else if(!data.id.equalsIgnoreCase(String.valueOf(mapping.get("ID")))){
								if(data.category.id.equalsIgnoreCase(String.valueOf(mapping.get("CategoryID")))) {
									if(data.displayOrder.equalsIgnoreCase(String.valueOf( mapping.get("Display order")))) {
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
						if (Utils.isNullOrWhitespace(data.id) == false) {
							empApplnAddtnlInfoHeading = context.find(EmpApplnAddtnlInfoHeadingDBO.class,Integer.parseInt(data.id));
						}
						if (empApplnAddtnlInfoHeading == null) {
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
						List<Integer> detailIds = new ArrayList<>();
						for (EmpApplnAddtnlInfoParameterDTO item : data.parameters) {
							EmpApplnAddtnlInfoParameterDBO detail = null;
							if (Utils.isNullOrWhitespace(item.id) == false) {
								detail = context.find(EmpApplnAddtnlInfoParameterDBO.class, Integer.parseInt(item.id));
							}
							if (detail == null) {
								detail = new EmpApplnAddtnlInfoParameterDBO();
								detail.createdUsersId = Integer.parseInt(userId);
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
						empApplnAddtnlInfoHeading.empApplnAddtnlInfoParameterMap = empApplnAddtnlInfoParameterMap;
						if(empApplnAddtnlInfoHeading.id == null) {
							context.persist(empApplnAddtnlInfoHeading);
						}else {
							empApplnAddtnlInfoHeading.modifiedUsersId = Integer.parseInt(userId);
							context.merge(empApplnAddtnlInfoHeading);
						}
						/*
						 * Query deleteQuery = context.createNativeQuery(RecruitmentQueries.
						 * EMP_APPLN_ADDTNL_INFO_PARAMETER_DELETE_DETAIL);
						 * deleteQuery.setParameter("heading_id", empApplnAddtnlInfoHeading.id);
						 * deleteQuery.setParameter("detail_ids", detailIds);
						 * deleteQuery.executeUpdate();
						 */
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
	
	@RequestMapping(value = "/editResearchAndAdditionalDetailsSetup", method = RequestMethod.POST)
	public Mono<ApiResult<EmpApplnAddtnlInfoHeadingDTO>> editResearchAndAdditionalDetailsSetup(@RequestParam("id") String id){
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
	
	@RequestMapping( value = "/deleteResearchAndAdditionalDetailsSetup", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> deleteResearchAndAdditionalDetailsSetup(@RequestParam("headingId") String headingId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
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

	@RequestMapping(value = "/getResearchDetails", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmployeeApplicationDTO>>> getResearchDetails() {
	  	ApiResult<List<EmployeeApplicationDTO>> researchDetails = new ApiResult<List<EmployeeApplicationDTO>>();
	      try {
	          DBGateway.runJPA(new ITransactional() {
	              @Override
	              public void onRun(EntityManager context) {
	                  Query qry = context.createQuery(RecruitmentQueries.SELECT_RESEARCH_DETAILS_ALL);
	                  List<EmpApplnAddtnlInfoHeadingDBO> researchHeadingList = qry.getResultList();
	                  if(!Utils.isNullOrEmpty(researchHeadingList)){
	                	  researchDetails.success = true;
	                	  researchDetails.dto = new ArrayList<>();
	                      for(EmpApplnAddtnlInfoHeadingDBO heading : researchHeadingList){
	                    	  EmployeeApplicationDTO headDTO = new EmployeeApplicationDTO();
	                    	  headDTO.value = !Utils.isNullOrEmpty(heading.id) ? String.valueOf(heading.id) : null;
	                    	  headDTO.label = !Utils.isNullOrEmpty(heading.addtnlInfoHeadingName) ? String.valueOf(heading.addtnlInfoHeadingName) : "";
	                    	  headDTO.headingDisplayOrder = !Utils.isNullOrEmpty(heading.headingDisplayOrder) ? String.valueOf(heading.headingDisplayOrder) : "";
	                    	  headDTO.isTypeResearch = !Utils.isNullOrEmpty(heading.isTypeResearch) ? Boolean.valueOf(heading.isTypeResearch) : false;
	                    	  headDTO.employeeCategoryId = !Utils.isNullOrEmpty(heading.empEmployeeCategoryId) && !Utils.isNullOrEmpty(heading.empEmployeeCategoryId.id) 
	                    			  ? String.valueOf(heading.empEmployeeCategoryId.id) : null;
	                    	  headDTO.detailsDTO = new ArrayList<>();
	                    	  if(!Utils.isNullOrEmpty(heading.empApplnAddtnlInfoParameterMap)) {
	                    		  for(EmpApplnAddtnlInfoParameterDBO parameters : heading.empApplnAddtnlInfoParameterMap) {
	                    			  if(parameters.recordStatus == 'A') {
	                    				  EmployeeApplicationDetailsDTO parameterDTO = new EmployeeApplicationDetailsDTO();
	                        			  parameterDTO.value = !Utils.isNullOrEmpty(parameters.id) ? String.valueOf(parameters.id) : null;
	                        			  parameterDTO.label = !Utils.isNullOrEmpty(parameters.addtnlInfoParameterName) ? String.valueOf(parameters.addtnlInfoParameterName) : "";
	                        			  parameterDTO.empApplnAddtnlInfoHeadingId = (!Utils.isNullOrEmpty(parameters.empApplnAddtnlInfoHeading) && !Utils.isNullOrEmpty(parameters.empApplnAddtnlInfoHeading.id)) ? String.valueOf(parameters.empApplnAddtnlInfoHeading.id) : null;
	                        			  parameterDTO.parameterDisplayOrder = !Utils.isNullOrEmpty(parameters.parameterDisplayOrder) ? String.valueOf(parameters.parameterDisplayOrder) : "";
	                        			  parameterDTO.isDisplayInApplication = !Utils.isNullOrEmpty(parameters.isDisplayInApplication) ? Boolean.valueOf(parameters.isDisplayInApplication) : false;
	                        			  headDTO.detailsDTO.add(parameterDTO);
	                    			  }
	                    		  }
	                    		  Collections.sort(headDTO.detailsDTO, new Comparator<EmployeeApplicationDetailsDTO>() {
	                                  @Override
	                                  public int compare(EmployeeApplicationDetailsDTO o1, EmployeeApplicationDetailsDTO o2) {
	                                      return Integer.compare(Integer.parseInt(o1.parameterDisplayOrder), Integer.parseInt(o2.parameterDisplayOrder));
	                                  }
	                              });
	                    	  }
	                    	  researchDetails.dto.add(headDTO);
	                      }
	                  }
	              }
	              @Override
	              public void onError(Exception error) {
	              	researchDetails.success = false;
	              	researchDetails.dto = null;
	              	researchDetails.failureMessage = error.getMessage();
	              }
	          }, true);
	      }
	      catch(Exception error) {
	          Utils.log(error.getMessage());
	      }
	      return Utils.monoFromObject(researchDetails);
	}
}
