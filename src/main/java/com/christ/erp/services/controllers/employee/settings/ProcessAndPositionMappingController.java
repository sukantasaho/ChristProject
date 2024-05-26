package com.christ.erp.services.controllers.employee.settings;
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
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleSubDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.ErpEmployeeTitleDBO;
import com.christ.erp.services.dbqueries.employee.SettingsQueries;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleDTO;
import com.christ.erp.services.dto.employee.EmpPositionRoleSubDTO;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/Secured/Employee/Settings/ProcessAndPositionMapping")
public class ProcessAndPositionMappingController  extends BaseApiController {
	
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPositionRoleDTO>>> getGridData() {
		ApiResult<List<EmpPositionRoleDTO>> result = new ApiResult<List<EmpPositionRoleDTO>>();
		DBGateway.runJPA(new ITransactional() {
			public void onRun(EntityManager context) {
				Query query = context.createNativeQuery(SettingsQueries.PROCESS_AND_POSITION_MAPPING_GET_GRID_DATA, Tuple.class);
				@SuppressWarnings("unchecked")
				List<Tuple> mappings = query.getResultList();
				if (mappings != null && mappings.size() > 0) {
					result.success = true;
					result.dto = new ArrayList<>();
					for (Tuple mapping : mappings) {
						EmpPositionRoleDTO mappingInfo = new EmpPositionRoleDTO();
						mappingInfo.id = mapping.get("id").toString();
						mappingInfo.campus = new ExModelBaseDTO();
						mappingInfo.campus.text = mapping.get("Campus").toString();
						mappingInfo.processType = mapping.get("Process").toString();
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
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpPositionRoleDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	 	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(data != null) {
			DBGateway.runJPA(new ITransactional() {
				@SuppressWarnings("unchecked")
				@Override
				public void onRun(EntityManager context) {
					EmpPositionRoleDBO header = null;
					List<EmpPositionRoleDBO> list = null;
					Query q = context.createQuery(SettingsQueries.PROCESS_AND_POSITION_MAPPING_DUPLICATE_CHECK);
					q.setParameter("campusId", Integer.parseInt(data.campus.id));
					q.setParameter("processType", data.processType);
					list = q.getResultList();
					if (list != null && list.size() != 0) {
						for (EmpPositionRoleDBO bo : list) {
							if (Utils.isNullOrWhitespace(data.id) == true && data.id == null || data.id.isEmpty()) {
								if (data.campus.id.equalsIgnoreCase(bo.erpCampusId.id.toString()) && data.processType.equalsIgnoreCase(bo.processType)) {
									result.failureMessage = " Duplicate entry for Campus   " +  " " +  bo.erpCampusId.campusName  + "  " + " and Process Type " +  "  " + bo.processType;
								}
							}
							else if (!data.id.equalsIgnoreCase(bo.id.toString()) && Utils.isNullOrWhitespace(data.id) == false) {
								if (data.campus.id.equalsIgnoreCase(bo.erpCampusId.id.toString()) && data.processType.equalsIgnoreCase(bo.processType)) {
									result.failureMessage = " Duplicate entry for Campus " + " " +  bo.erpCampusId.campusName  + "  and Process Type   " + " " + bo.processType;
								}
							}
						}
					}

					if(Utils.isNullOrWhitespace(result.failureMessage)) {
						if(Utils.isNullOrWhitespace(data.id) == false) {
							header = context.find(EmpPositionRoleDBO.class, Integer.parseInt(data.id));
						}
						if(header == null) {
							header = new EmpPositionRoleDBO();
							header.createdUsersId= Integer.parseInt(userId);
						}
						ErpCampusDBO camp = new ErpCampusDBO();
						camp.id = Integer.parseInt(data.campus.id);
						header.erpCampusId = camp;
						header.processType = data.processType;
						if(!Utils.isNullOrEmpty(header.id)) {
							header.modifiedUsersId = Integer.parseInt(userId);
						}
						header.recordStatus = 'A';
						List<Integer> detailIds = new ArrayList<>();
						Set<EmpPositionRoleSubDBO> sub = new HashSet<>();
						Set<EmpPositionRoleSubDBO> subSet= header.empPositionSubAssignmentDBOSet;
						for(EmpPositionRoleSubDTO item : data.Levels) {
							EmpPositionRoleSubDBO detail = null;
							if(header.empPositionSubAssignmentDBOSet!=null) {
								for(EmpPositionRoleSubDBO bo : subSet) {
									if(Utils.isNullOrWhitespace(item.id) == false) {
										if(Integer.parseInt(item.id) == bo.id) {
											detail = context.find(EmpPositionRoleSubDBO.class, Integer.parseInt(item.id));
											subSet.remove(bo);
											break;
										}
									}else if(Utils.isNullOrWhitespace(item.id)){
										detail = context.find(EmpPositionRoleSubDBO.class, bo.id);
										subSet.remove(bo);
										break;
									}
								}
							}
							if(detail == null) {
								detail = new EmpPositionRoleSubDBO();
								detail.createdUsersId = Integer.parseInt(userId);
								detail.recordStatus = 'A';
							}else {
							   detail.modifiedUsersId = Integer.parseInt(userId);
							}
							detail.displayOrder = Integer.parseInt(item.order);
							EmpDBO empDBO = new EmpDBO();
							empDBO.id = Integer.parseInt(item.employee.id);
							detail.empDBO = empDBO;
							//detail.empId = Integer.parseInt(item.employee.id);
							ErpEmployeeTitleDBO title = new ErpEmployeeTitleDBO();
							title.id = Integer.parseInt(item.empTitle.id);
							detail.empTitleId = title;
							detail.empPositionRoleId = header;
							detail.recordStatus = 'A';
							detailIds.add(detail.id);
							sub.add(detail);
						}
						if(subSet!=null) {
							for(EmpPositionRoleSubDBO bo : subSet) {
								bo.recordStatus = 'D';
								bo.modifiedUsersId = Integer.parseInt(userId);
								sub.add(bo);
							}
					   }
					   header.empPositionSubAssignmentDBOSet = sub;
					   if(header.id==null) {
						   context.persist(header);
					   }else {
							header.modifiedUsersId = Integer.parseInt(userId);
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
	}
	 
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<EmpPositionRoleDTO>> edit(@RequestParam("id") String id) {
		ApiResult<EmpPositionRoleDTO> result = new ApiResult<EmpPositionRoleDTO>();
		DBGateway.runJPA(new ITransactional() {
		@Override
		public void onRun(EntityManager context) {
			EmpPositionRoleDBO pos = context.find(EmpPositionRoleDBO.class, Integer.parseInt(id));
				if(pos != null) {
					result.success = true;
					result.dto = new EmpPositionRoleDTO();
					result.dto.id = pos.id.toString();
					result.dto.processType = pos.processType.toString();
					result.dto.campus = new ExModelBaseDTO();
					result.dto.campus.id = pos.erpCampusId.id.toString();
					result.dto.Levels = new ArrayList<>();
					if(pos.empPositionSubAssignmentDBOSet != null && pos.empPositionSubAssignmentDBOSet.size() > 0) {
						for(EmpPositionRoleSubDBO item : pos.empPositionSubAssignmentDBOSet) {
							EmpPositionRoleSubDTO levelInfo = new EmpPositionRoleSubDTO();
								if(item.recordStatus == 'A') {
									levelInfo.id = item.id.toString();
									levelInfo.empTitle = new ExModelBaseDTO();
									levelInfo.empTitle.id = item.empTitleId.id.toString();
									levelInfo.employee = new ExModelBaseDTO();
									levelInfo.employee.id = item.empDBO.id.toString();
									levelInfo.order = item.displayOrder.toString();
									result.dto.Levels.add(levelInfo);
								}
							}
						 Collections.sort(result.dto.Levels, new Comparator<EmpPositionRoleSubDTO>() {
	                            @Override
	                            public int compare(EmpPositionRoleSubDTO o1, EmpPositionRoleSubDTO o2) {
	                                return Integer.compare(Integer.parseInt(o1.order), Integer.parseInt(o2.order));
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
			}, true);
			return Utils.monoFromObject(result);
		}	 
	 
	 @RequestMapping( value = "/delete", method = RequestMethod.POST)
		public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("headingId") String headingId) {
			ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
			try {
				if (headingId != null) {
					DBGateway.runJPA(new ITransactional() {
						@Override
						public void onRun(EntityManager context) {
							EmpPositionRoleDBO heading = null;
							if (!Utils.isNullOrEmpty(headingId) && !Utils.isNullOrWhitespace(headingId)) {
								heading = context.find(EmpPositionRoleDBO.class, Integer.parseInt(headingId));
								if (heading.id != 0) {
									heading.recordStatus = 'D';
									Set<EmpPositionRoleSubDBO> subSet = new HashSet<EmpPositionRoleSubDBO>();
									for (EmpPositionRoleSubDBO document : heading.empPositionSubAssignmentDBOSet) {
										document.recordStatus = 'D';
										subSet.add(document);
									}
									heading.empPositionSubAssignmentDBOSet = subSet;
									context.merge(heading);
									result.success = true;
									result.dto = new ModelBaseDTO();
									result.dto.id = heading.id.toString();
								}
							}
						}
						@Override
						public void onError(Exception ex) {
							result.success = false;
							result.dto = null;
							result.failureMessage = ex.getMessage();
						}
					});
				}
			} catch (Exception e) {

			}
			return Utils.monoFromObject(result);
		}
}