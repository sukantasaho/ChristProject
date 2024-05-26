/*
package com.christ.erp.services.controllers.employee;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.controllers.common.CommonApiController;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllotmentDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbqueries.employee.LeaveQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentListDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave")
public class LeaveController extends BaseApiController {
	
	@SuppressWarnings("unused")
	@Autowired
	private CommonApiController commonApiController;

	// @Screen - Leave Type
	// @Comments - save and update leave type record
	@RequestMapping(value = "/LeaveType/SaveMapping", method = RequestMethod.POST, consumes = "application/json")
	public Mono<ApiResult<ModelBaseDTO>> saveLeaveType(@Valid @RequestBody EmpLeaveTypeDTO leaveTypeMapping) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
		      return getUserID().flatMap(userId -> {
		    	  if (leaveTypeMapping != null) {
						DBGateway.runJPA(new ITransactional() {
							@Override
							public void onRun(EntityManager context) {
								EmpLeaveTypeDBO header = null;
								if(leaveTypeMapping.leavetype == null || leaveTypeMapping.leavetype.isEmpty()) {
									result.failureMessage = "Please enter Leave Type.";
								}else if(leaveTypeMapping.leavecode == null || leaveTypeMapping.leavecode.isEmpty()) {
									result.failureMessage = "Please enter Leave Code.";
								}else if(leaveTypeMapping.applyOnline == null) {
									result.failureMessage = "Please select Apply Online.";
								}else if(leaveTypeMapping.partialDaysAllowed == null) {
									result.failureMessage = "Please select Can Apply for Half Day.";
								}else if(leaveTypeMapping.continuousDays == null) {
									result.failureMessage = "Please select Continuous Days.";
								}else if(leaveTypeMapping.isExemption == null) {
									result.failureMessage = "Please select Exemption.";
								}else if(leaveTypeMapping.isLeave == null) {
									result.failureMessage = "Please select Leave.";
								}else if(leaveTypeMapping.autoApproveLeave == null) {
									result.failureMessage = "Please select Auto Approve Leave.";
								}else {
									if(Utils.isNullOrWhitespace(leaveTypeMapping.id) == false) {
										Boolean isduplicate = duplicateCheckLeaveType(context, leaveTypeMapping, header);
										if(isduplicate) {
											result.failureMessage = "Duplicate record exist With Leave type: "+ leaveTypeMapping.leavetype;
										}else {
											header = context.find(EmpLeaveTypeDBO.class, Integer.parseInt(leaveTypeMapping.id));
										}
									}
									if(header == null) {
										Boolean isdiplicateCreate = duplicateCheckLeaveType(context, leaveTypeMapping, header);
										if(isdiplicateCreate) {
											result.failureMessage = "Duplicate record exist With Leave type: "+ leaveTypeMapping.leavetype;
										}else {
											header = new EmpLeaveTypeDBO();
										}
									}
									if(header != null) {
										header.leaveTypeName = leaveTypeMapping.leavetype;
										header.leaveTypeCode = leaveTypeMapping.leavecode.toUpperCase();
										header.leaveTypeColorCodeHexvalue = leaveTypeMapping.leaveTypeColorCodeHexvalue;
										header.isApplyOnline = leaveTypeMapping.applyOnline;
										if(header.isApplyOnline == true) {
											if(leaveTypeMapping.maxOnlineLeaveInMonth.isEmpty()) {
												header.maxOnlineLeaveInMonth = 0;
											}else {
												header.maxOnlineLeaveInMonth = Integer.parseInt(leaveTypeMapping.maxOnlineLeaveInMonth);
											}
										}else {
											header.maxOnlineLeaveInMonth = 0;
										}
										header.autoApproveLeave = leaveTypeMapping.autoApproveLeave;
										if(header.autoApproveLeave == true) {
											if(leaveTypeMapping.autoApprovedDays.isEmpty()) {
												header.autoApprovedDays = 0;
											}else {
												header.autoApprovedDays = Integer.parseInt(leaveTypeMapping.autoApprovedDays);
											}
										}else {
											header.autoApprovedDays = 0;
										}
										header.continousDays = leaveTypeMapping.continuousDays;
										header.isExemption = leaveTypeMapping.isExemption;
										header.isLeave = leaveTypeMapping.isLeave;
										header.partialDaysAllowed = leaveTypeMapping.partialDaysAllowed;
										header.supportingDoc = leaveTypeMapping.supportingDoc;
										header.leavePolicy = leaveTypeMapping.leavepolicy;
										if(header.id == null) {
											header.createdUsersId=Integer.parseInt(userId);
											header.recordStatus = 'A';
											context.persist(header);
										}else {
											header.modifiedUsersId=Integer.parseInt(userId);
											context.merge(header);
										}
										result.success = true;
										result.dto = new ModelBaseDTO();
										result.dto.id = header.id.toString();
									}
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
		      }    );
		     
		}catch (Exception e) {
				}
		return Utils.monoFromObject(result);
	}
	// @Screen - Leave Type
	// @Comments - list all Leave type records
	@RequestMapping(value = "/LeaveType/GetAllMapping", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveTypeDTO>>> getAllLeaveTypes() {
		ApiResult<List<EmpLeaveTypeDTO>> result = new ApiResult<List<EmpLeaveTypeDTO>>();
		DBGateway.runJPA(new ITransactional() {
			@SuppressWarnings("unchecked")
			@Override
			public void onRun(EntityManager context) {
				Query query = context.createNativeQuery(LeaveQueries.LEAVE_TYPE_MAPPING_SEARCH_ALL, Tuple.class);
				List<Tuple> mappings = query.getResultList();
				if(mappings != null && mappings.size() > 0) {
					result.success = true;
					result.dto = new ArrayList<>();
					for(Tuple mapping : mappings) {
						EmpLeaveTypeDTO mappingInfo = new EmpLeaveTypeDTO();
						mappingInfo.id = mapping.get("ID").toString();
						mappingInfo.leavetype = mapping.get("leaveTypeName").toString();
						mappingInfo.leavecode = mapping.get("leaveTypeCode").toString();
						mappingInfo.applyOnline = (Boolean) mapping.get("isApplyOnline");
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
		}, true);
		return Utils.monoFromObject(result);
	}

	// @Screen - Leave Type
	// @Comments - list Leave type record based on id
	@RequestMapping(value = "/LeaveType/SelectMapping", method = RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveTypeDTO>> selectLeaveType(@RequestParam("ID") String id) {
		ApiResult<EmpLeaveTypeDTO> result = new ApiResult<EmpLeaveTypeDTO>();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				EmpLeaveTypeDBO dbleavetypeMappingInfo = context.find(EmpLeaveTypeDBO.class, Integer.parseInt(id));
				if (dbleavetypeMappingInfo != null) {
					result.success = true;
					result.dto = new EmpLeaveTypeDTO();
					result.dto.id = dbleavetypeMappingInfo.id.toString();
					result.dto.leavetype = dbleavetypeMappingInfo.leaveTypeName;
					result.dto.leavecode = dbleavetypeMappingInfo.leaveTypeCode;
					result.dto.leaveTypeColorCodeHexvalue = dbleavetypeMappingInfo.leaveTypeColorCodeHexvalue;
					result.dto.applyOnline = dbleavetypeMappingInfo.isApplyOnline;
					result.dto.partialDaysAllowed = dbleavetypeMappingInfo.partialDaysAllowed;
					result.dto.autoApprovedDays = String.valueOf(dbleavetypeMappingInfo.autoApprovedDays);
					result.dto.continuousDays = dbleavetypeMappingInfo.continousDays;
					result.dto.maxOnlineLeaveInMonth = String.valueOf(dbleavetypeMappingInfo.maxOnlineLeaveInMonth);
					result.dto.isExemption = dbleavetypeMappingInfo.isExemption;
					result.dto.supportingDoc = dbleavetypeMappingInfo.supportingDoc;
					result.dto.isLeave = dbleavetypeMappingInfo.isLeave;
					result.dto.autoApproveLeave = dbleavetypeMappingInfo.autoApproveLeave;
					result.dto.leavepolicy = dbleavetypeMappingInfo.leavePolicy;
				}
			}
			@Override
			public void onError(Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
			}
		}, true);
		return Utils.monoFromObject(result);
	}

	// @Screen - Leave Type
	// @Comments - checking duplicate records
	public Boolean duplicateCheckLeaveType(EntityManager context, EmpLeaveTypeDTO leaveTypeMapping,EmpLeaveTypeDBO header) {
		Boolean duplicate = false;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM emp_leave_type where  leave_type_name=:leavetype and record_status='A'");
		if(leaveTypeMapping.id != null && !leaveTypeMapping.id.isEmpty()) {
			sb.append(" and emp_leave_type_id not in (:id) ");
		}
		Query q = context.createNativeQuery(sb.toString(), EmpLeaveTypeDBO.class);
		q.setParameter("leavetype", leaveTypeMapping.leavetype);
		if (leaveTypeMapping.id != null && !leaveTypeMapping.id.isEmpty()) {
			q.setParameter("id", leaveTypeMapping.id);
		}
		EmpLeaveTypeDBO leavetype2 = null;
		try {
			leavetype2 = (EmpLeaveTypeDBO) q.getSingleResult();
		} catch (NoResultException nre) {
			leavetype2 = null;
		}
		if(leavetype2 != null) {
			duplicate = true;
		}else {
			duplicate = false;
		}
		return duplicate;
	}
	// @Screen - Leave Type
		// @Comments - delete the record for leave type
		@RequestMapping(value = "/LeaveType/DeleteMapping", method = RequestMethod.POST, consumes = "application/json")
		public Mono<ApiResult<ModelBaseDTO>> deleteLeaveType(@RequestBody EmpLeaveTypeDTO leaveTypeMapping) {
			ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
			
			try {
	        	return getUserID().flatMap(userId -> {
			if (leaveTypeMapping != null) {
				DBGateway.runJPA(new ITransactional() {
					@Override
					public void onRun(EntityManager context) {
						EmpLeaveTypeDBO header = null;
						if(Utils.isNullOrWhitespace(leaveTypeMapping.id) == false) {
							header = context.find(EmpLeaveTypeDBO.class, Integer.parseInt(leaveTypeMapping.id));
						}
						if(header != null) {
							header.recordStatus = 'D';
							header.modifiedUsersId=Integer.parseInt(userId);
							if(header.id != null) {
								context.merge(header);
							}
							result.success = true;
							result.dto = new ModelBaseDTO();
							result.dto.id = header.id.toString();
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
	        	});
			}catch(Exception ex)
			{
			}
			return Utils.monoFromObject(result);
		}
	*/
/*
	 * Method return List of Leave Allotment Name's and Month's
	 *//*

	@RequestMapping(value = "/LeaveAllotment/GetAllMapping", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveAllotmentDTO>>> getAllLeaveAllotments() {
		ApiResult<List<EmpLeaveAllotmentDTO>> result = new ApiResult<List<EmpLeaveAllotmentDTO>>();
		DBGateway.runJPA(new ITransactional() {
			@SuppressWarnings("unchecked")
			public void onRun(EntityManager context) {
				Query query = context.createNativeQuery(LeaveQueries.LEAVE_ALLOTMENT_MAPPING_SEARCH_ALL, Tuple.class);
				List<Tuple> mappings = query.getResultList();
				if(mappings != null && mappings.size() > 0) {
					result.dto = new ArrayList<>();
					for(Tuple mapping : mappings) {
						EmpLeaveAllotmentDTO mappingInfo = new EmpLeaveAllotmentDTO();
						mappingInfo.leaveCategoryName = mapping.get("LeaveCategoryName").toString();
						mappingInfo.month = Utils.getMonthName(Integer.valueOf(mapping.get("Month").toString()));
						result.dto.add(mappingInfo);
					}
					result.success = true;
				}
			}
			public void onError(Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
			}
		}, true);
		return Utils.monoFromObject(result);
	}

	*/
/*
	 * Method return List of LeaveAllotment Data with Active Records Like('A')
	 *//*

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/LeaveAllotment/SelectMapping", method = RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveAllotmentDTO>> getSelectLeaveAllotement(@RequestParam("leaveCategoryName") String leaveCategoryName) {
		ApiResult<EmpLeaveAllotmentDTO> result = new ApiResult<EmpLeaveAllotmentDTO>();
		EntityManager jpaManager = this.getEntityManagerFactory().createEntityManager();
		jpaManager.getTransaction().begin();
		DBGateway.runJPA(new ITransactional() {
			@Override
			public void onRun(EntityManager context) {
				List<EmpLeaveAllotmentDBO> allotmentMappings = new ArrayList<EmpLeaveAllotmentDBO>();
				if(leaveCategoryName != null && !leaveCategoryName.isEmpty()) {
					Query query = jpaManager.createQuery("from EmpLeaveAllotmentDBO E where E.leaveCategoryName =:leaveCategoryName ");
					query.setParameter("leaveCategoryName", leaveCategoryName);
					allotmentMappings = (List<EmpLeaveAllotmentDBO>) query.getResultList();
					if(allotmentMappings.size()!=0) {
					result.dto = new EmpLeaveAllotmentDTO();
					// use for matching records when its Update
					result.dto.allomentIds = new HashSet<Integer>();
					result.success = true;
					for (EmpLeaveAllotmentDBO dbo : allotmentMappings) {
						if((dbo.recordStatus == 'A' && dbo.leaveType.recordStatus == 'A') || dbo.recordStatus == 'A') {
							if(result.dto.id == null || result.dto.id.isEmpty()) {
								result.dto.id = dbo.id.toString();
								result.dto.leaveCategoryName = dbo.leaveCategoryName.toString();
								result.dto.month = dbo.month.toString();
								result.dto.leaveAllotment = new ArrayList<EmpLeaveAllotmentListDTO>();
							}
							result.dto.allomentIds.add(dbo.id);
							EmpLeaveAllotmentListDTO leaveallotment = new EmpLeaveAllotmentListDTO();
							leaveallotment.isApplicable = dbo.isApplicable;
							leaveallotment.allottedLeaves = dbo.allottedLeaves;
							leaveallotment.accumulatedLeave = dbo.accumulatedLeave;
							leaveallotment.leaveallotedId = dbo.id;							
							if(dbo.leaveType != null && dbo.leaveType.id != null && !String.valueOf(dbo.leaveType.id).isEmpty()) {
								ExModelBaseDTO exbaseLeaveType = new ExModelBaseDTO();
								exbaseLeaveType.id = String.valueOf(dbo.leaveType.id);
								exbaseLeaveType.text = dbo.leaveType.leaveTypeName;
								leaveallotment.leavetypeId = (int) Integer.parseInt(exbaseLeaveType.id);
								leaveallotment.leavetypeName = exbaseLeaveType.text;
							} else {
								leaveallotment.leavetypeId = Integer.parseInt("");
								leaveallotment.leavetypeName = "";
							}
							ExModelBaseDTO addLeaveType = new ExModelBaseDTO();
							if(dbo.addToLeaveType != null && dbo.addToLeaveType.id != null	&& !String.valueOf(dbo.addToLeaveType.id).isEmpty()) {
								addLeaveType.id = String.valueOf(dbo.addToLeaveType.id);
								leaveallotment.addToLeaveType = addLeaveType;
							}else {
								addLeaveType.id = "";
								addLeaveType.text = "";
								leaveallotment.addToLeaveType = addLeaveType;
							}
							result.dto.leaveAllotment.add(leaveallotment);
						}
					}
					jpaManager.getTransaction().commit();
				}
				}else {
					result.success = false;
					result.dto = null;
				}
			}
			@Override
			public void onError(Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
			}
		}, true);
		return Utils.monoFromObject(result);
	}

	*/
/*
	 * Method use to create New Leave Allotment Records or Update the List of Leave
	 * Allotment Records and also create New Leave Allotment if parent data(Leave
	 * Type) Exist, it will be Deactivated.
	 *//*

	@RequestMapping(value = "/LeaveAllotment/SaveMapping", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveLeaveAllotment(@RequestBody EmpLeaveAllotmentDTO data) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {	
	    return getUserID().flatMap(userId -> {
		if(data != null) {
			DBGateway.runJPA(new ITransactional() {
				@SuppressWarnings("unchecked")
				@Override
				public void onRun(EntityManager context) {
					if(data.leaveCategoryName == null || data.leaveCategoryName.isEmpty()) {
						result.failureMessage = "Please enter Leave Category.";
					}else if (data.month == null || data.month.isEmpty()) {
						result.failureMessage = "Please select Leave Initialize Month.";
					}else if (data.leaveAllotment != null && !data.leaveAllotment.isEmpty()) {
						for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
							if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
								if(leaveallot.allottedLeaves == null || String.valueOf(leaveallot.allottedLeaves).isEmpty()) {
									result.failureMessage = "Please enter Allotted Leaves to  to "+ leaveallot.leavetypeName; break;
								}
								if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty()) {
									if(!(leaveallot.allottedLeaves >= leaveallot.accumulatedLeave)) {
										result.failureMessage = "Accumulated leave is less than Allotted Leaves to "+ leaveallot.leavetypeName;
										break;
									}
									if(leaveallot.addToLeaveType.id == null || leaveallot.addToLeaveType.id.isEmpty()) {
										result.failureMessage = "Please enter Add To Leave type to "+ leaveallot.leavetypeName;
										break;
									}
								}
							}
						}
					}
					if (result.failureMessage == null || result.failureMessage.isEmpty()) {
						boolean duplicated = duplicateCheckLeaveAllotment(context, data, result);
						if (!duplicated) {
							if(data.allomentIds.size() > 0) {
								List<EmpLeaveAllotmentDBO> allotmentMappings = null;
								if(data.allomentIds != null && !data.allomentIds.isEmpty()) {
									Query query = context.createQuery("SELECT c FROM EmpLeaveAllotmentDBO c WHERE c.id IN (:ids)");
									allotmentMappings = (List<EmpLeaveAllotmentDBO>) query.setParameter("ids", data.allomentIds).getResultList();
								}
								if(allotmentMappings != null && !allotmentMappings.isEmpty() && allotmentMappings.size() > 0) {
									HashSet<Integer> parentAllIds = new HashSet<Integer>();
									HashSet<Integer> parentActiveIds = new HashSet<Integer>();
									for(EmpLeaveAllotmentDBO dbo : allotmentMappings) {
										dbo.leaveCategoryName = data.leaveCategoryName;
										dbo.month = Integer.parseInt(data.month);
										// added unique All Parent records id's
										parentAllIds.add(dbo.id);
										if(data.leaveAllotment != null && !data.leaveAllotment.isEmpty()) {
											for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
												if(leaveallot.leaveallotedId != null && !String.valueOf(leaveallot.leaveallotedId).isEmpty() && leaveallot.leaveallotedId.equals(dbo.id)) {
													// added unique parent Active records id's (record Status='A')
													parentActiveIds.add(leaveallot.leaveallotedId);
													dbo.leaveType.id = leaveallot.leavetypeId;
													dbo.isApplicable = leaveallot.isApplicable;
													if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
														if(leaveallot.allottedLeaves != null && !String.valueOf(leaveallot.allottedLeaves).isEmpty())
															dbo.allottedLeaves = leaveallot.allottedLeaves;
														else
															dbo.allottedLeaves = null;
														if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty()) {
															if(leaveallot.allottedLeaves >= leaveallot.accumulatedLeave)
																dbo.accumulatedLeave = leaveallot.accumulatedLeave;
															else
																dbo.accumulatedLeave = leaveallot.allottedLeaves;
														}else {
															dbo.accumulatedLeave = null;
														}
														if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.addToLeaveType != null
																&& leaveallot.addToLeaveType.id != null && !String.valueOf(leaveallot.addToLeaveType.id).isEmpty()) {
															EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
															addleaveType.id = (int) Integer.parseInt(leaveallot.addToLeaveType.id);
															dbo.addToLeaveType = addleaveType;
														}else {
															dbo.addToLeaveType = null;
														}
													}else {
														dbo.accumulatedLeave = null;
														dbo.allottedLeaves = null;
														dbo.addToLeaveType = null;
													}
													dbo.recordStatus = 'A';
													dbo.modifiedUsersId = Integer.parseInt(userId);
													context.merge(dbo);
												}
											}
										}
									}
									// finding deactivated parent record id's
									HashSet<Integer> parentDectiveIds = new HashSet<Integer>();
									if(parentAllIds.size() > 0) {
										for(Integer id2 : parentAllIds) {
											if(!parentActiveIds.contains(id2))
												parentDectiveIds.add(id2);
										}
									}
									// Deactive Parent Record in child table statusRecords 'A' to 'D' or 'D' to 'A'
									for(EmpLeaveAllotmentDBO dbo : allotmentMappings) {
										for(Integer id3 : parentDectiveIds) {
											if(dbo.id.equals(id3)) {
												dbo.recordStatus = 'D';
												dbo.modifiedUsersId = Integer.parseInt(userId);
												context.merge(dbo);
											}
										}
									}
									// creating new record in child
									for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
										if(leaveallot.leaveallotedId == null || String.valueOf(leaveallot.leaveallotedId).isEmpty()) {
											EmpLeaveAllotmentDBO dbo = new EmpLeaveAllotmentDBO();
											dbo.leaveCategoryName = data.leaveCategoryName;
											dbo.month = Integer.parseInt(data.month);
											EmpLeaveTypeDBO leaveType = new EmpLeaveTypeDBO();
											leaveType.id = leaveallot.leavetypeId;
											dbo.leaveType = leaveType;
											dbo.isApplicable = leaveallot.isApplicable;
											if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
												dbo.allottedLeaves = leaveallot.allottedLeaves;
												if(leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.allottedLeaves >= leaveallot.accumulatedLeave) {
													dbo.accumulatedLeave = leaveallot.accumulatedLeave;
												}else {
													if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty())
														dbo.accumulatedLeave = leaveallot.allottedLeaves;
												}
												if(leaveallot.addToLeaveType.id != null && !leaveallot.addToLeaveType.id.isEmpty()) {
													ExModelBaseDTO modelBase = new ExModelBaseDTO();
													modelBase.id = leaveallot.addToLeaveType.id;
													EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
													addleaveType.id = Integer.parseInt(modelBase.id);
													dbo.addToLeaveType = addleaveType;
												}
											}else {
												dbo.accumulatedLeave = null;
												dbo.allottedLeaves = null;
												dbo.addToLeaveType = null;
											}
											dbo.recordStatus = 'A';	
											dbo.createdUsersId = Integer.parseInt(userId);
											context.persist(dbo);
										}
									}
									result.success = true;
								}
							}else {
								EmpLeaveAllotmentDBO dbo = null;
								if(data.leaveAllotment.size() == 0) {
									result.success = false;
								}else {
									for(EmpLeaveAllotmentListDTO leaveallot : data.leaveAllotment) {
										dbo = new EmpLeaveAllotmentDBO();
										dbo.leaveCategoryName = data.leaveCategoryName;
										dbo.month = Integer.parseInt(data.month);
										if(data.leaveAllotment != null && !data.leaveAllotment.isEmpty()) {
											EmpLeaveTypeDBO leaveType = new EmpLeaveTypeDBO();
											leaveType.id = leaveallot.leavetypeId;
											dbo.leaveType = leaveType;
											dbo.isApplicable = leaveallot.isApplicable;
											if(leaveallot.isApplicable != null && leaveallot.isApplicable == true) {
												dbo.allottedLeaves = leaveallot.allottedLeaves;
												if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty() && leaveallot.allottedLeaves >= leaveallot.accumulatedLeave) {
													dbo.accumulatedLeave = leaveallot.accumulatedLeave;
												}else {
													if (leaveallot.accumulatedLeave != null && !String.valueOf(leaveallot.accumulatedLeave).isEmpty())
														dbo.accumulatedLeave = leaveallot.allottedLeaves;
												}
												ExModelBaseDTO modelBase = new ExModelBaseDTO();
												modelBase.id = leaveallot.addToLeaveType.id;
												if(modelBase.id != null && !modelBase.id.isEmpty()) {
													EmpLeaveTypeDBO addleaveType = new EmpLeaveTypeDBO();
													addleaveType.id = Integer.parseInt(modelBase.id);
													dbo.addToLeaveType = addleaveType;
												}
											}else {
												dbo.accumulatedLeave = null;
												dbo.allottedLeaves = null;
												dbo.addToLeaveType = null;
											}
											dbo.createdUsersId = Integer.parseInt(userId);
											dbo.recordStatus = 'A';
											context.persist(dbo);
										}
									}
									result.success = true;
								}
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
			});
		}
		return Utils.monoFromObject(result);
	});
		}catch (Exception e) {			
		}
	return Utils.monoFromObject(result);
	}

	*/
/*
	 * Method return for Duplicate data exist. if exist, it will not creating new
	 * record and not update record if not exist, then allow to Update Data
	 *//*

	@SuppressWarnings("unchecked")
	public Boolean duplicateCheckLeaveAllotment(EntityManager context, EmpLeaveAllotmentDTO data,ApiResult<ModelBaseDTO> result) {
		Boolean duplicateCheck = false;
		List<EmpLeaveAllotmentDBO> duplicateChecking = new ArrayList<EmpLeaveAllotmentDBO>();
		StringBuffer sb = new StringBuffer();
		if(data.leaveCategoryName != null && !data.leaveCategoryName.isEmpty())
			sb.append("SELECT c FROM EmpLeaveAllotmentDBO c WHERE c.leaveCategoryName=:LeaveCategoryName and c.recordStatus='A' ");
		if(data.allomentIds != null && !data.allomentIds.isEmpty())
			sb.append(" and c.id NOT IN (:ids) ");
		Query queryDuplicateCheck = context.createQuery(sb.toString());
		if(data.leaveCategoryName != null && !data.leaveCategoryName.isEmpty())
			queryDuplicateCheck.setParameter("LeaveCategoryName", data.leaveCategoryName);
		if(data.allomentIds != null && !data.allomentIds.isEmpty())
			queryDuplicateCheck.setParameter("ids", data.allomentIds);
		duplicateChecking = (List<EmpLeaveAllotmentDBO>) queryDuplicateCheck.getResultList();
		if(duplicateChecking.size() > 0) {
			for(EmpLeaveAllotmentDBO leaveAllotmentDBO : duplicateChecking) {
				if(!String.valueOf(leaveAllotmentDBO.recordStatus).isEmpty()) {
					if(leaveAllotmentDBO.recordStatus == 'A') {
						duplicateCheck = true;
						result.failureMessage = "Duplicate entry for Leave Category "+leaveAllotmentDBO.leaveCategoryName;
					}
				}
			}
		}
		return duplicateCheck;
	}

	*/
/*
	 * Method Use to delete records example like 'A' to 'D'
	 *//*

	@RequestMapping(value = "/LeaveAllotment/DeleteMapping", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> deleteLeaveAllotment(@RequestParam("leaveCategoryName") String leaveCategoryName) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
	    return getUserID().flatMap(userId -> {
		DBGateway.runJPA(new ITransactional() {
			@SuppressWarnings("unchecked")
			public void onRun(EntityManager context) {
				if(leaveCategoryName != null && !leaveCategoryName.isEmpty()) {
					List<EmpLeaveAllotmentDBO> allotmentMappings = null;
					if(Utils.isNullOrWhitespace(leaveCategoryName) == false) {
						Query query = context.createQuery("SELECT c FROM EmpLeaveAllotmentDBO c WHERE c.leaveCategoryName=:LeaveCategoryName ");
						allotmentMappings = (List<EmpLeaveAllotmentDBO>) query.setParameter("LeaveCategoryName", leaveCategoryName).getResultList();
					}
					for(EmpLeaveAllotmentDBO dbo : allotmentMappings) {
						dbo.recordStatus = 'D';
						dbo.modifiedUsersId = Integer.parseInt(userId);
						context.merge(dbo);
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
	  });
		}catch (Exception e) {}
		return Utils.monoFromObject(result);
	}
}*/
