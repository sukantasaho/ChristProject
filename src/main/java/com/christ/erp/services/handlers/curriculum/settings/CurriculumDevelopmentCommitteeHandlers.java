package com.christ.erp.services.handlers.curriculum.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeMembersDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeRoleDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.ErpCommitteeMembersDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.settings.CurriculumDevelopmentCommitteeTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CurriculumDevelopmentCommitteeHandlers {
	@Autowired
	private CurriculumDevelopmentCommitteeTransaction curriculumDevelopmentCommitteeTransaction;

	public Flux<List<ErpCommitteeDTO>> getGridData(String yearId) {
		List<ErpCommitteeDBO> list = curriculumDevelopmentCommitteeTransaction.getGridData(yearId);
		return this.convertDboToDto(list);
	}

	public Flux<List<ErpCommitteeDTO>> convertDboToDto(List<ErpCommitteeDBO> tupleList) {
		List<ErpCommitteeDTO> erpCommitteeDTOList = new ArrayList<ErpCommitteeDTO>();
		Map<Integer, ErpCommitteeDTO> erpCommitteeDTOMap = new HashMap<Integer, ErpCommitteeDTO>();
		List<String> campusStrList = new ArrayList<String>();
		tupleList.forEach(dbo -> {
			ErpCommitteeDTO erpCommitteeDTO = new ErpCommitteeDTO();
			erpCommitteeDTO.setId(dbo.getId());
			if(!erpCommitteeDTOMap.containsKey(dbo.getErpDepartmentDBO().getId())) {
				erpCommitteeDTO.setErpDepartment(new SelectDTO());
				erpCommitteeDTO.getErpDepartment().setLabel(dbo.getErpDepartmentDBO().getDepartmentName());
				erpCommitteeDTO.getErpDepartment().setValue(dbo.getErpDepartmentDBO().getId().toString());
				campusStrList.clear();
				erpCommitteeDTO.setErpCampus(new SelectDTO());
				SelectDTO selectDTO = new SelectDTO();
				dbo.getErpCommitteeCampusDBOSet().forEach(campus ->{
					selectDTO.setLabel(campus.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
					selectDTO.setValue(campus.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
				});
				erpCommitteeDTO.setErpCampus(selectDTO);
				erpCommitteeDTOMap.put(dbo.getErpDepartmentDBO().getId(), erpCommitteeDTO);
				erpCommitteeDTOList.add(erpCommitteeDTO);
			}else {
				ErpCommitteeDTO erpCommitteeDTO1 = erpCommitteeDTOMap.get(dbo.getErpDepartmentDBO().getId());
				SelectDTO select = new SelectDTO();
				campusStrList.clear();
				dbo.getErpCommitteeCampusDBOSet().forEach(campus ->{
					campusStrList.add(campus.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
					String campusList = campusStrList.stream().collect(Collectors.joining(", "));
					select.setLabel(campusList);
				});
				erpCommitteeDTO1.setErpCampus(select);
				erpCommitteeDTOMap.replace(dbo.getErpDepartmentDBO().getId(), erpCommitteeDTO1);
			}
		});
		return Flux.just(erpCommitteeDTOList) ;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return curriculumDevelopmentCommitteeTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	public Flux<ErpCommitteeDTO> edit(int id) {
		ErpCommitteeDBO erpCommitteeDBO = curriculumDevelopmentCommitteeTransaction.edit(id);
		return this.convertDboToErpcommitteeDto(erpCommitteeDBO);
	}

	private Flux<ErpCommitteeDTO> convertDboToErpcommitteeDto(ErpCommitteeDBO dbo) {
		ErpCommitteeDTO dto = new ErpCommitteeDTO();
		dto.setId(dbo.getId());
		dto.setErpAcademicYear(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
			dto.getErpAcademicYear().setValue(dbo.getErpAcademicYearDBO().getId().toString());
			dto.getErpAcademicYear().setLabel(dbo.getErpAcademicYearDBO().getAcademicYearName());
		}
		dto.setErpDepartment(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
			dto.getErpDepartment().setValue(dbo.getErpDepartmentDBO().getId().toString());
			dto.getErpDepartment().setLabel(dbo.getErpDepartmentDBO().getDepartmentName());
		}
		dto.setInternalMembers(new ArrayList<SelectDTO>());
		Map<Integer,String> membersList = new HashMap<Integer, String>();
		dbo.getErpCommitteeMembersDBOSet().forEach(empList -> {
			if(empList.getRecordStatus()=='A' && empList.getEmpDBO() != null) {
				if(!membersList.containsKey(empList.getEmpDBO().getId())) {
					SelectDTO sdto = new SelectDTO();
					sdto.setValue(empList.getEmpDBO().getId().toString());
					sdto.setLabel(empList.getEmpDBO().getEmpName());
					membersList.put(empList.getEmpDBO().getId(), null);
					dto.getInternalMembers().add(sdto);
				}
			}
		});
		List<ErpCommitteeMembersDTO> erpCommitteeMembersDTOList = new ArrayList<ErpCommitteeMembersDTO>();
		Map<Integer,ErpCommitteeMembersDTO> erpCommitteeMembersDTOMap = new HashMap<Integer, ErpCommitteeMembersDTO>();
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeMembersDBOSet())) {
			dbo.getErpCommitteeMembersDBOSet().forEach(membersDbo -> {
				ErpCommitteeMembersDTO erpCommitteeMembersDTO = new ErpCommitteeMembersDTO();
				if(!Utils.isNullOrEmpty(membersDbo.getErpCommitteeRoleDBO())) {
					if(!erpCommitteeMembersDTOMap.containsKey(membersDbo.getErpCommitteeRoleDBO().getId())) {
						erpCommitteeMembersDTO.setErpCommitteeRole(new SelectDTO());
						erpCommitteeMembersDTO.getErpCommitteeRole().setValue(membersDbo.getErpCommitteeRoleDBO().getId().toString());
						erpCommitteeMembersDTO.getErpCommitteeRole().setLabel(membersDbo.getErpCommitteeRoleDBO().getCommitteeRole());
						erpCommitteeMembersDTO.setEmpList(new ArrayList<SelectDTO>());
						SelectDTO emp = new SelectDTO();
						emp.setValue(membersDbo.getEmpDBO().getId().toString());
						emp.setLabel(membersDbo.getEmpDBO().getEmpName());
						erpCommitteeMembersDTO.getEmpList().add(emp);
						erpCommitteeMembersDTOMap.put(membersDbo.getErpCommitteeRoleDBO().getId(),erpCommitteeMembersDTO); 
					}else {
						ErpCommitteeMembersDTO cmdbo = erpCommitteeMembersDTOMap.get(membersDbo.getErpCommitteeRoleDBO().getId());
						SelectDTO emp = new SelectDTO();
						emp.setValue(membersDbo.getEmpDBO().getId().toString());
						emp.setLabel(membersDbo.getEmpDBO().getEmpName());
						cmdbo.getEmpList().add(emp);
						erpCommitteeMembersDTOMap.replace(membersDbo.getErpCommitteeRoleDBO().getId(), cmdbo);
					}
				}
			});
		}	
		erpCommitteeMembersDTOMap.forEach((k,v) ->{
			erpCommitteeMembersDTOList.add(v);
		});
		dto.setErpCommitteeMembersDTOList(erpCommitteeMembersDTOList);
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeCampusDBOSet())) {
			dto.setErpCommitteeCampusList(new ArrayList<SelectDTO>());
			dbo.getErpCommitteeCampusDBOSet().forEach(committeeDbo -> {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(committeeDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getId().toString());
				selectDTO.setLabel(committeeDbo.getErpCampusDepartmentMappingDBO().getErpCampusDBO().getCampusName());
				dto.getErpCommitteeCampusList().add(selectDTO);			
			});
		}		
		return Flux.just(dto);		
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpCommitteeDTO> dto, String userId) {
		return dto
				.handle((erpCommitteeDTO, synchronousSink) ->  {
					Integer cdcId = curriculumDevelopmentCommitteeTransaction.getCdcId();
					Integer membersRoleId = curriculumDevelopmentCommitteeTransaction.getMembersId();
					erpCommitteeDTO.setErpCommitteeType(new SelectDTO());
					erpCommitteeDTO.getErpCommitteeType().setValue(cdcId.toString());
					erpCommitteeDTO.setMembersId(membersRoleId);
					boolean isTrue = curriculumDevelopmentCommitteeTransaction.duplicateCheck(erpCommitteeDTO);
					if(isTrue) {
						synchronousSink.error(new DuplicateException("The CDC members are already added for the department"));
					}else {
						synchronousSink.next(erpCommitteeDTO);
					}
				}).cast(ErpCommitteeDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap( s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						curriculumDevelopmentCommitteeTransaction.update(s);
					}else {
						curriculumDevelopmentCommitteeTransaction.save(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public ErpCommitteeDBO convertDtoToDbo(ErpCommitteeDTO dto, String userId) {
		ErpCommitteeDBO dbo = new ErpCommitteeDBO();
		dbo.setId(dto.getId());
		dbo.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		dbo.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getErpAcademicYear().getValue()));
		dbo.setErpDepartmentDBO(new ErpDepartmentDBO());
		dbo.getErpDepartmentDBO().setId(Integer.parseInt(dto.getErpDepartment().getValue()));
		dbo.setErpCommitteeTypeDBO(new ErpCommitteeTypeDBO());
		dbo.getErpCommitteeTypeDBO().setId(Integer.parseInt(dto.getErpCommitteeType().getValue()));
		dbo.setRecordStatus('A');
		if(Utils.isNullOrEmpty(dto.getId())) {
			dbo.setCreatedUsersId(Integer.parseInt(userId));
		}else {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		ErpCommitteeDBO erpCommitteList = null;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			erpCommitteList = curriculumDevelopmentCommitteeTransaction.edit(dto.getId());
		}
		Set<ErpCommitteeMembersDBO> existMembersDBOSet =  !Utils.isNullOrEmpty(erpCommitteList) ?  erpCommitteList.getErpCommitteeMembersDBOSet() : null;
		Map<Integer,ErpCommitteeMembersDBO> existMembersDBOMap = new HashMap<Integer, ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(existMembersDBOSet)) {
			existMembersDBOSet.forEach(role -> {
				if(role.getRecordStatus()=='A' && role.getErpCommitteeRoleDBO() != null) {
					existMembersDBOMap.put(role.getId(), role);
				}
			});
		}
		Map<Integer,Integer> internalMembers = new HashMap<Integer, Integer>();
		dto.getInternalMembers().forEach( empId -> {
			internalMembers.put(Integer.parseInt(empId.getValue()), Integer.parseInt(empId.getValue()));
		});
		Set<ErpCommitteeMembersDBO> erpCommitteeMembersSet = new HashSet<ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(dto.getErpCommitteeMembersDTOList())) {
			dto.getErpCommitteeMembersDTOList().forEach( membersDto -> {
				if(!Utils.isNullOrEmpty(membersDto.getErpCommitteeRole())) {
					if (!Utils.isNullOrEmpty(existMembersDBOSet)) {
						existMembersDBOSet.forEach(cmDbo-> {
							if (cmDbo.getRecordStatus()=='A' && cmDbo.getErpCommitteeRoleDBO() != null) {
								if(cmDbo.getErpCommitteeRoleDBO().getId() == Integer.parseInt(membersDto.getErpCommitteeRole().getValue())) {
									existMembersDBOMap.remove(cmDbo.getId());
									existMembersDBOMap.put(cmDbo.getEmpDBO().getId(), cmDbo);
								}
							}
						});
					}
					if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
						membersDto.getEmpList().forEach(emp -> {
							ErpCommitteeMembersDBO membersDbo = null ;
							if(existMembersDBOMap.containsKey(Integer.parseInt(emp.getValue()))) {
								membersDbo = existMembersDBOMap.get(Integer.parseInt(emp.getValue()));
								membersDbo.setModifiedUsersId(Integer.parseInt(userId));
								existMembersDBOMap.remove(Integer.parseInt(emp.getValue()));
							}else  {
								membersDbo = new ErpCommitteeMembersDBO();
								membersDbo.setCreatedUsersId(Integer.parseInt(userId));
							}
							membersDbo.setErpCommitteeDBO(dbo);
							if(!Utils.isNullOrEmpty(membersDto.getErpCommitteeRole().getValue())) {
								membersDbo.setErpCommitteeRoleDBO(new ErpCommitteeRoleDBO());
								membersDbo.getErpCommitteeRoleDBO().setId(Integer.parseInt(membersDto.getErpCommitteeRole().getValue()));
								if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
									membersDbo.setEmpDBO(new EmpDBO());
									membersDbo.getEmpDBO().setId(Integer.parseInt(emp.getValue()));
									if(internalMembers.containsKey(Integer.parseInt(emp.getValue()))) {
										internalMembers.remove(Integer.parseInt(emp.getValue()));
									}
								}
							}
							membersDbo.setRecordStatus('A');
							erpCommitteeMembersSet.add(membersDbo);
						});	
					}
				}
			});	
			if(!Utils.isNullOrEmpty(existMembersDBOMap)) {
				existMembersDBOMap.forEach((entry, value)-> {
					value.setModifiedUsersId( Integer.parseInt(userId));
					value.setRecordStatus('D');
					erpCommitteeMembersSet.add(value);
				});
			}
		}
		if(!Utils.isNullOrEmpty(internalMembers)) {
			internalMembers.forEach((e,v) -> {
				ErpCommitteeMembersDBO membersDbo = new ErpCommitteeMembersDBO();
				membersDbo.setErpCommitteeDBO(dbo);
				membersDbo.setEmpDBO(new EmpDBO());
				membersDbo.getEmpDBO().setId(v);
				membersDbo.setErpCommitteeRoleDBO(new ErpCommitteeRoleDBO());
				membersDbo.getErpCommitteeRoleDBO().setId(dto.getMembersId());
				membersDbo.setCreatedUsersId(Integer.parseInt(userId));
				membersDbo.setRecordStatus('A');
				erpCommitteeMembersSet.add(membersDbo);
			});
		}
		dbo.setErpCommitteeMembersDBOSet(erpCommitteeMembersSet);
		Set<ErpCommitteeCampusDBO> existCampusDBOSet = !Utils.isNullOrEmpty(erpCommitteList) ? erpCommitteList.getErpCommitteeCampusDBOSet() : null;
		Map<Integer, ErpCommitteeCampusDBO> erpCommitteeCampusDBOMap = new HashMap<Integer, ErpCommitteeCampusDBO>();
		if(!Utils.isNullOrEmpty(existCampusDBOSet)) {
			existCampusDBOSet.forEach(existCampusList -> {
				if(existCampusList.getRecordStatus() == 'A') {
					erpCommitteeCampusDBOMap.put(existCampusList.getErpCampusDepartmentMappingDBO().getId(), existCampusList);
				}
			});
		}	
		Set<ErpCommitteeCampusDBO> erpCommitteeCampusDBOSet = new HashSet<ErpCommitteeCampusDBO>();
		if(!Utils.isNullOrEmpty(dto.getErpCommitteeCampusList())) {
			dto.getErpCommitteeCampusList().forEach(committeeCampus ->{
				ErpCommitteeCampusDBO erpCommitteeCampusDBO = null;
				Integer campusDepartmentId = curriculumDevelopmentCommitteeTransaction.getCampusDepartmentMapping(committeeCampus.getValue(), dto.getErpDepartment().getValue());
				if(erpCommitteeCampusDBOMap.containsKey(campusDepartmentId)) {
					erpCommitteeCampusDBO = erpCommitteeCampusDBOMap.get(campusDepartmentId);
					erpCommitteeCampusDBO.setModifiedUsersId(Integer.parseInt(userId));
					erpCommitteeCampusDBOMap.remove(campusDepartmentId);
				}else {
					erpCommitteeCampusDBO = new ErpCommitteeCampusDBO();
					erpCommitteeCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
				}
				erpCommitteeCampusDBO.setErpCommitteeDBO(dbo);
				erpCommitteeCampusDBO.setErpCampusDepartmentMappingDBO(new ErpCampusDepartmentMappingDBO());
				erpCommitteeCampusDBO.getErpCampusDepartmentMappingDBO().setId(campusDepartmentId);
				erpCommitteeCampusDBO.setRecordStatus('A');
				erpCommitteeCampusDBOSet.add(erpCommitteeCampusDBO);

			});
		}
		if(!Utils.isNullOrEmpty(erpCommitteeCampusDBOMap)) {
			erpCommitteeCampusDBOMap.forEach((entry, value)-> {
				value.setModifiedUsersId( Integer.parseInt(userId));
				value.setRecordStatus('D');
				erpCommitteeCampusDBOSet.add(value);
			});
		}
		dbo.setErpCommitteeCampusDBOSet(erpCommitteeCampusDBOSet);
		return dbo;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> importFromPreviousYear(Mono<ErpCommitteeDTO> dto, String userId) {
		return dto
				.handle((erpCommitteeDTO, synchronousSink) ->  {
					boolean isTrue = curriculumDevelopmentCommitteeTransaction.duplicateCheck(erpCommitteeDTO);
					if(isTrue) {
						synchronousSink.error(new DuplicateException("The CDC members are already added for the department"));
					}else {
						synchronousSink.next(erpCommitteeDTO);
					}
				}).cast(ErpCommitteeDTO.class)
				.map(data -> convertDtoPreDbo(data, userId))
				.flatMap( s -> {
					curriculumDevelopmentCommitteeTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private ErpCommitteeDBO  convertDtoPreDbo(ErpCommitteeDTO data, String userId) {
		ErpCommitteeDBO erpCommitteeDBO = new ErpCommitteeDBO();
		ErpCommitteeDBO erpCommitteeDBO1 = curriculumDevelopmentCommitteeTransaction.getPreviousData(data.getErpDepartment().getValue(),data.getErpAcademicYear().getValue());
		if(!Utils.isNullOrEmpty(erpCommitteeDBO1)) {
			BeanUtils.copyProperties(erpCommitteeDBO1, erpCommitteeDBO,"id");
			erpCommitteeDBO.getErpAcademicYearDBO().setId(Integer.parseInt(data.getImportToYear().getValue()));
			erpCommitteeDBO.setCreatedUsersId(Integer.parseInt(userId));
			erpCommitteeDBO.setModifiedUsersId(null);
			erpCommitteeDBO.setRecordStatus('A');
		}
		Set<ErpCommitteeMembersDBO> erpCommitteeMembersDBOSet = new HashSet<ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(erpCommitteeDBO1.getErpCommitteeMembersDBOSet())) {
			erpCommitteeDBO1.getErpCommitteeMembersDBOSet().forEach(committeemembers ->{
				ErpCommitteeMembersDBO erpCommitteeMembersDBO = new ErpCommitteeMembersDBO();
				BeanUtils.copyProperties(committeemembers, erpCommitteeMembersDBO,"id","erpCommitteeDBO");
				erpCommitteeMembersDBO.setErpCommitteeDBO(erpCommitteeDBO);
				erpCommitteeMembersDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpCommitteeMembersDBO.setModifiedUsersId(null);
				erpCommitteeMembersDBOSet.add(erpCommitteeMembersDBO);
			});	
		}
		erpCommitteeDBO.setErpCommitteeMembersDBOSet(erpCommitteeMembersDBOSet);
		Set<ErpCommitteeCampusDBO> erpCommitteeCampusDBOSet = new HashSet<ErpCommitteeCampusDBO>();
		if(!Utils.isNullOrEmpty(erpCommitteeDBO1.getErpCommitteeCampusDBOSet())) {
			erpCommitteeDBO.getErpCommitteeCampusDBOSet().forEach(committeecampus ->{
				ErpCommitteeCampusDBO erpCommitteeCampusDBO = new ErpCommitteeCampusDBO();
				BeanUtils.copyProperties(committeecampus, erpCommitteeCampusDBO,"id","erpCommitteeDBO");
				erpCommitteeCampusDBO.setErpCommitteeDBO(erpCommitteeDBO);
				erpCommitteeCampusDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpCommitteeCampusDBO.setModifiedUsersId(null);
				erpCommitteeCampusDBOSet.add(erpCommitteeCampusDBO);
			});
		}
		erpCommitteeDBO.setErpCommitteeCampusDBOSet(erpCommitteeCampusDBOSet);
		return erpCommitteeDBO;
	}
}
