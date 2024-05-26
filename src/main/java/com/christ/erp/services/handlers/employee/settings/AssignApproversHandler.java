package com.christ.erp.services.handlers.employee.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDetailsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;
import com.christ.erp.services.transactions.employee.settings.AssignApproversTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssignApproversHandler {

	@Autowired
	private AssignApproversTransaction assignApproversTransaction;

	public Flux<EmpApproversDTO> getGridData() {
		List<Tuple> list = assignApproversTransaction.getGridData();
		return this.convertDboToDto(list); 
	}

	private Flux<EmpApproversDTO> convertDboToDto(List<Tuple> list) {
		List<EmpApproversDTO> empApproversDTOList = new ArrayList<EmpApproversDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(tuple ->{
				EmpApproversDTO empApproversDTO = new EmpApproversDTO();
				empApproversDTO.setTypes(new SelectDTO());
				empApproversDTO.getTypes().setValue(tuple.get("type_Id").toString());
				empApproversDTO.getTypes().setLabel(tuple.get("type_Name").toString());
				empApproversDTO.setCampus(new SelectDTO());
				empApproversDTO.getCampus().setValue(tuple.get("erp_campus_id").toString());
				empApproversDTO.getCampus().setLabel(tuple.get("campus_name").toString());
				empApproversDTO.setDepartment(new SelectDTO());
				empApproversDTO.getDepartment().setValue(tuple.get("erp_department_id").toString());
				empApproversDTO.getDepartment().setLabel(tuple.get("department_name").toString());
				empApproversDTO.setErpCampusDepartmentMapping(new SelectDTO());
				empApproversDTO.getErpCampusDepartmentMapping().setValue(tuple.get("erp_campus_department_mapping_id").toString());
				empApproversDTO.setEmpCategory(new SelectDTO());
				empApproversDTO.getEmpCategory().setValue(tuple.get("emp_Category_Id").toString());
				empApproversDTO.getEmpCategory().setLabel(tuple.get("emp_Category_Name").toString());
				empApproversDTOList.add(empApproversDTO);
			});
		}
		return Flux.fromIterable(empApproversDTOList);
	}

	public Mono<EmpApproversDTO> edit(EmpApproversDTO data) {
		List<Tuple> assignApproversDataList = assignApproversTransaction.edit(data);
		return this.convertDboToEmpApproversDTO(assignApproversDataList,data);
	}

	private Mono<EmpApproversDTO> convertDboToEmpApproversDTO(List<Tuple> assignApproversDataList,EmpApproversDTO data) {
		EmpApproversDTO empApproversDTO = new EmpApproversDTO();
		List<EmpDBO> empDboList = assignApproversTransaction.getNewEmpDetails(data.getCampus().getValue(),data.getDepartment().getValue(),data.getEmpCategory().getValue());
		Map<Integer,EmpDBO> empMap = !Utils.isNullOrEmpty(empDboList) ? empDboList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) :new HashMap<Integer,EmpDBO>();
		List<EmpApproversDetailsDTO> detailList = new ArrayList<EmpApproversDetailsDTO>();
		if(!Utils.isNullOrEmpty(assignApproversDataList)) {
			assignApproversDataList.forEach(dblist ->  {
				EmpApproversDetailsDTO empApproversDetailsDTO = new EmpApproversDetailsDTO();
				empApproversDTO.setTypes(new SelectDTO());
				empApproversDTO.getTypes().setValue(data.getTypes().getValue());
				empApproversDTO.getTypes().setLabel(data.getTypes().getLabel());
				empApproversDTO.setCampus(new SelectDTO());
				empApproversDTO.getCampus().setValue(data.getCampus().getValue());
				empApproversDTO.getCampus().setLabel(data.getCampus().getLabel());
				empApproversDTO.setDepartment(new SelectDTO());
				empApproversDTO.getDepartment().setValue(data.getDepartment().getValue());
				empApproversDTO.getDepartment().setLabel(data.getDepartment().getLabel());
				empApproversDTO.setEmpCategory(new SelectDTO());
				empApproversDTO.getEmpCategory().setValue(data.getEmpCategory().getValue());
				empApproversDTO.getEmpCategory().setLabel(data.getEmpCategory().getLabel());
				
				empApproversDetailsDTO.setEmpId(dblist.get("emp_id").toString());
				if(!Utils.isNullOrEmpty(empMap)) {
					if(empMap.containsKey(Integer.parseInt(dblist.get("emp_id").toString()))) {
						empMap.remove(Integer.parseInt(dblist.get("emp_id").toString()));
					}
				}
				empApproversDetailsDTO.setEmpName(dblist.get("emp_name").toString());
				empApproversDetailsDTO.setEmpDepartment(dblist.get("department_name").toString());
				empApproversDetailsDTO.setEmpCategoryName(dblist.get("employee_category_name").toString());
				if(!Utils.isNullOrEmpty(dblist.get("emp_approvers_id"))) {
					empApproversDetailsDTO.setId(dblist.get("emp_approvers_id").toString());
				}
				if(data.getTypes() != null && data.getTypes().getLabel().equalsIgnoreCase("Appraiser")) {
					if(!Utils.isNullOrEmpty(dblist.get("level_one_appraiser_id")) && !Utils.isNullOrEmpty(dblist.get("level_one_appraiser_name"))) {
						empApproversDetailsDTO.setLevelOneAppraiserId(new SelectDTO());
						empApproversDetailsDTO.getLevelOneAppraiserId().setValue(dblist.get("level_one_appraiser_id").toString());
						empApproversDetailsDTO.getLevelOneAppraiserId().setLabel(dblist.get("level_one_appraiser_name").toString());
					}
					if (!Utils.isNullOrEmpty(dblist.get("level_two_appraiser_id")) && !Utils.isNullOrEmpty(dblist.get("level_two_appraiser_name"))) {
						empApproversDetailsDTO.setLevelTwoAppraiserId(new SelectDTO());
						empApproversDetailsDTO.getLevelTwoAppraiserId().setValue(dblist.get("level_two_appraiser_id").toString());
						empApproversDetailsDTO.getLevelTwoAppraiserId().setLabel(dblist.get("level_two_appraiser_name").toString());
					}
				}
				if(data.getTypes() != null && data.getTypes().getLabel().equalsIgnoreCase("Leave")) {
					if(!Utils.isNullOrEmpty(dblist.get("leave_approver_id")) && !Utils.isNullOrEmpty(dblist.get("leave_approver_name"))) {
						empApproversDetailsDTO.setLeaveApproverId(new SelectDTO());
						empApproversDetailsDTO.getLeaveApproverId().setValue(dblist.get("leave_approver_id").toString());
						empApproversDetailsDTO.getLeaveApproverId().setLabel(dblist.get("leave_approver_name").toString());	
					}
					if (!Utils.isNullOrEmpty(dblist.get("leave_authoriser_id")) && !Utils.isNullOrEmpty(dblist.get("leave_authoriser_name"))) {
						empApproversDetailsDTO.setLeaveAuthorizerId(new SelectDTO());
						empApproversDetailsDTO.getLeaveAuthorizerId().setValue(dblist.get("leave_authoriser_id").toString());
						empApproversDetailsDTO.getLeaveAuthorizerId().setLabel(dblist.get("leave_authoriser_name").toString());
					}
				}
				if(data.getTypes() != null && data.getTypes().getLabel().equalsIgnoreCase("Work Dairy")) {
					if(!Utils.isNullOrEmpty(dblist.get("work_diary_approver_id")) && !Utils.isNullOrEmpty(dblist.get("work_diary_approver_name"))) {
						empApproversDetailsDTO.setWorkDairyApproverId(new SelectDTO());
						empApproversDetailsDTO.getWorkDairyApproverId().setValue(dblist.get("work_diary_approver_id").toString());
						empApproversDetailsDTO.getWorkDairyApproverId().setLabel(dblist.get("work_diary_approver_name").toString());
					}
				}
				detailList.add(empApproversDetailsDTO);
				empApproversDTO.setItems(detailList);
			});
			if(!Utils.isNullOrEmpty(empMap)) {
				empMap.forEach((k,v) -> {
					EmpApproversDetailsDTO empApproversDetailsDTO = new EmpApproversDetailsDTO();
					empApproversDetailsDTO.setEmpId(k.toString());
					empApproversDetailsDTO.setEmpName(v.getEmpName());
					empApproversDetailsDTO.setEmpDepartment(v.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
					empApproversDetailsDTO.setEmpCategoryName(v.getEmpEmployeeCategoryDBO().getEmployeeCategoryName());
					detailList.add(empApproversDetailsDTO);
					empApproversDTO.setItems(detailList);
				});
			}
		}
		return Mono.just(empApproversDTO);
	}

	public List<Tuple> dupcheck(EmpApproversDTO data) {
		List<Tuple> dataList  = assignApproversTransaction.getAssignApproversDetails(data);
		return dataList;
	}

	public void getempDetails(EmpApproversDTO data, List<Tuple> emp, EmpApproversDTO dto){
		List<Tuple> list = assignApproversTransaction.getEmpDetails(data);
		if(!Utils.isNullOrEmpty(list)) {
			List<EmpApproversDetailsDTO> empApproversDetailsDTOList = new ArrayList<EmpApproversDetailsDTO>();
			list.forEach(empDetails -> {
				EmpApproversDetailsDTO empApproversDetailsDTO = new EmpApproversDetailsDTO();
				empApproversDetailsDTO.setEmpId(empDetails.get("emp_id").toString());
				empApproversDetailsDTO.setEmpName(empDetails.get("emp_name").toString());
				empApproversDetailsDTO.setEmpDepartment(empDetails.get("department_name").toString());
				empApproversDetailsDTO.setEmpCategoryName(empDetails.get("emp_Category_Name").toString());
				if(!Utils.isNullOrEmpty(empDetails.get("emp_approvers_id"))) {
					empApproversDetailsDTO.setId(empDetails.get("emp_approvers_id").toString());
				}
				empApproversDetailsDTOList.add(empApproversDetailsDTO);
			});
			dto.setErrorMsg("");
			dto.setItems(empApproversDetailsDTOList);
		}else {
			dto.setErrorMsg("No Data");
			dto.setItems(null);
		}
	}

	public Flux<SelectDTO> getEmployeeList(EmpApproversDTO data){
		List<Tuple> list = null;
		if(!Utils.isNullOrEmpty(data.getCampus().getValue())) {
			list = assignApproversTransaction.getEmployeeList(data);
		}
		List<SelectDTO> empSelectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
			list.forEach(empList -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(empList.get("emp_id").toString());
				selectDTO.setLabel(empList.get("emp_name").toString());
				selectDTOList.add(selectDTO);
			});
			empSelectDTOList.addAll(selectDTOList);
		}
		return Flux.fromIterable(empSelectDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<EmpApproversDTO> dto, String userId) {
		return dto
				.handle((empApproversDTO,synchronousSink) -> {
					synchronousSink.next(empApproversDTO);
				}).cast(EmpApproversDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( empApproversDBOList -> {
					if(!Utils.isNullOrEmpty(empApproversDBOList)){
						assignApproversTransaction.update(empApproversDBOList);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public EmpApproversDetailsDBO setDetails(EmpApproversDetailsDTO dto, String type, Map<String, EmpApproversDetailsDBO> map, String userId, EmpApproversDBO dbo) {
		EmpApproversDetailsDBO subDbo = null;
		if(map.containsKey(type)) {
			subDbo = map.get(type);
			subDbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		else {
			subDbo = new EmpApproversDetailsDBO();
			subDbo.setCreatedUsersId(Integer.parseInt(userId));
		}
		if(type.equalsIgnoreCase("Appraiser")) {
			if(!Utils.isNullOrEmpty(dto.getLevelOneAppraiserId())) {
				subDbo.setLevelOneAppraiserId(new EmpDBO());
				subDbo.getLevelOneAppraiserId().setId(Integer.parseInt(dto.getLevelOneAppraiserId().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getLevelTwoAppraiserId())) {
				subDbo.setLevelTwoAppraiserId(new EmpDBO());
				subDbo.getLevelTwoAppraiserId().setId(Integer.parseInt(dto.getLevelTwoAppraiserId().getValue()));
			}
		}
		else if(type.equalsIgnoreCase("Leave")) {
			if(!Utils.isNullOrEmpty(dto.getLeaveApproverId())) {
				subDbo.setLeaveApproverId(new EmpDBO());
				subDbo.getLeaveApproverId().setId(Integer.parseInt(dto.getLeaveApproverId().getValue()));
			}
			if(!Utils.isNullOrEmpty(dto.getLeaveAuthorizerId())) {
				subDbo.setLeaveAuthorizerId(new EmpDBO());
				subDbo.getLeaveAuthorizerId().setId(Integer.parseInt(dto.getLeaveAuthorizerId().getValue()));
			}
		}
		else if(type.equalsIgnoreCase("Work Dairy")) {
			if(!Utils.isNullOrEmpty(dto.getWorkDairyApproverId())) {
				subDbo.setWorkDairyApproverId(new EmpDBO());
				subDbo.getWorkDairyApproverId().setId(Integer.parseInt(dto.getWorkDairyApproverId().getValue()));
			}
		}
		subDbo.setEmpApproversId(dbo);
		subDbo.setApprovalType(type);
		subDbo.setRecordStatus('A');
		return subDbo;
	}

	public List<EmpApproversDBO> convertDtoToDbo(EmpApproversDTO dto, String userId) {
		List<EmpApproversDBO> empApproversDBOList = new ArrayList<EmpApproversDBO>();
		Map<Integer,EmpApproversDBO> empApproversDBOMap = new HashMap<Integer, EmpApproversDBO>();
		List<Integer> empApproverIds = new ArrayList<Integer>();
		dto.getItems().forEach(empIds -> {
			if(!Utils.isNullOrEmpty(empIds.getEmpId())) {
				empApproverIds.add(Integer.parseInt(empIds.getEmpId()));
			}
		});
		List<EmpApproversDBO> empApproversDBOListExist = assignApproversTransaction.getEmpApproversData(empApproverIds,null);
		empApproversDBOListExist.forEach(exist -> {
			empApproversDBOMap.put(exist.getEmpDBO().getId(), exist);
		});
		dto.getItems().forEach(empApproversDetailsDTO -> {
			EmpApproversDBO empApproversDBO = null;
			if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getEmpId()) && empApproversDBOMap.containsKey(Integer.parseInt(empApproversDetailsDTO.getEmpId()))) {
				empApproversDBO = empApproversDBOMap.get(Integer.parseInt(empApproversDetailsDTO.getEmpId()));
				empApproversDBO.setModifiedUsersId(Integer.parseInt(userId));
			}
			else {
				empApproversDBO = new EmpApproversDBO();
				empApproversDBO.setCreatedUsersId(Integer.parseInt(userId));
			}
			if(!Utils.isNullOrEmpty(dto.getTypes().getLabel()) && dto.getTypes().getLabel().equalsIgnoreCase("Appraiser")) {
				if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getLevelOneAppraiserId())) {
					empApproversDBO.setLevelOneAppraiserId(new EmpDBO());
					empApproversDBO.getLevelOneAppraiserId().setId(Integer.parseInt(empApproversDetailsDTO.getLevelOneAppraiserId().getValue()));
				}
				if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getLevelTwoAppraiserId())) {
					empApproversDBO.setLevelTwoAppraiserId(new EmpDBO());
					empApproversDBO.getLevelTwoAppraiserId().setId(Integer.parseInt(empApproversDetailsDTO.getLevelTwoAppraiserId().getValue()));
				}
			}
			else if(!Utils.isNullOrEmpty(dto.getTypes().getLabel()) && dto.getTypes().getLabel().equalsIgnoreCase("Leave")) {
				if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getLeaveApproverId())) {
					empApproversDBO.setLeaveApproverId(new EmpDBO());
					empApproversDBO.getLeaveApproverId().setId(Integer.parseInt(empApproversDetailsDTO.getLeaveApproverId().getValue()));
				}
				if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getLeaveAuthorizerId())) {
					empApproversDBO.setLeaveAuthorizerId(new EmpDBO());
					empApproversDBO.getLeaveAuthorizerId().setId(Integer.parseInt(empApproversDetailsDTO.getLeaveAuthorizerId().getValue()));
				}				
			}
			else if(!Utils.isNullOrEmpty(dto.getTypes().getLabel()) && dto.getTypes().getLabel().equalsIgnoreCase("Work Dairy")) {
				if(!Utils.isNullOrEmpty(empApproversDetailsDTO.getWorkDairyApproverId())) {
					empApproversDBO.setWorkDairyApproverId(new EmpDBO());
					empApproversDBO.getWorkDairyApproverId().setId(Integer.parseInt(empApproversDetailsDTO.getWorkDairyApproverId().getValue()));
				}
			}
			empApproversDBO.setRecordStatus('A');
			empApproversDBO.setEmpDBO(new EmpDBO());
			empApproversDBO.getEmpDBO().setId(Integer.parseInt(empApproversDetailsDTO.getEmpId()));
			Set<EmpApproversDetailsDBO> detailsDboSet = null;
			Map<String,EmpApproversDetailsDBO> subMap = new HashMap<>();
			if(!Utils.isNullOrEmpty(empApproversDBO.getEmpApproversDetailsDBOSet())) {
				detailsDboSet = empApproversDBO.getEmpApproversDetailsDBOSet();
				detailsDboSet.forEach(subDbo -> {
					if(subDbo.getRecordStatus() == 'A') {
						subMap.put(subDbo.getApprovalType().trim(), subDbo);
					}
				});
			}
			else {
				detailsDboSet = new HashSet<>();
			}
			detailsDboSet.add(setDetails(empApproversDetailsDTO, dto.getTypes().getLabel(), subMap, userId, empApproversDBO));
			empApproversDBO.setEmpApproversDetailsDBOSet(detailsDboSet);
			empApproversDBOList.add(empApproversDBO);
		});
		return empApproversDBOList;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(EmpApproversDTO empApproversDTO, String userId) {
		List<EmpApproversDBO> empApproverDBOList = new ArrayList<EmpApproversDBO>();
		List<Integer> empApproverIds = new ArrayList<Integer>();
		List<Tuple> assgnApprvrsDataList = assignApproversTransaction.edit(empApproversDTO);
		if(!Utils.isNullOrEmpty(assgnApprvrsDataList)){
			assgnApprvrsDataList.forEach(data -> {
				empApproverIds.add(Integer.parseInt(data.get("emp_id").toString()));
			});
		}
		List<EmpApproversDBO> empApproversDBOList = assignApproversTransaction.getEmpApproversData(empApproverIds,empApproversDTO.getTypes().getLabel());
		if(!Utils.isNullOrEmpty(empApproversDBOList)) {
			empApproversDBOList.forEach(dbo -> {
				if(empApproversDTO.getTypes().getLabel().equalsIgnoreCase("Work Dairy")) {
					dbo.setWorkDairyApproverId(null);
				}
				else if(empApproversDTO.getTypes().getLabel().equalsIgnoreCase("Leave")) {
					dbo.setLeaveAuthorizerId(null);
					dbo.setLeaveApproverId(null);
				}
				else if(empApproversDTO.getTypes().getLabel().equalsIgnoreCase("Appraiser")) {
					dbo.setLevelOneAppraiserId(null);
					dbo.setLevelTwoAppraiserId(null);
				}
				if(!Utils.isNullOrEmpty(dbo.getEmpApproversDetailsDBOSet())) {
					dbo.getEmpApproversDetailsDBOSet().forEach(subDbo-> {
						subDbo.setRecordStatus('D');
						subDbo.setModifiedUsersId(Integer.parseInt(userId));
					});
				}
				if(Utils.isNullOrEmpty(dbo.getLeaveApproverId()) && Utils.isNullOrEmpty(dbo.getLevelOneAppraiserId()) && Utils.isNullOrEmpty(dbo.getWorkDairyApproverId())) {
					dbo.setRecordStatus('D');
				}
				dbo.setModifiedUsersId(Integer.parseInt(userId));
				empApproverDBOList.add(dbo);
			});
		}
		assignApproversTransaction.update(empApproverDBOList);
		return Mono.just(Boolean.TRUE).map(Utils::responseResult);	
	}
}