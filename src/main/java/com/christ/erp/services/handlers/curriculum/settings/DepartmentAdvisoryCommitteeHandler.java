package com.christ.erp.services.handlers.curriculum.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeMembersDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeRoleDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.curriculum.settings.DepartmentAdvisoryCommitteeTransaction;
import com.christ.erp.services.dto.common.ErpCommitteeMembersDTO;
import com.christ.erp.services.dto.common.ErpCommitteeProgrammeDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DepartmentAdvisoryCommitteeHandler {

	@Autowired	
	private DepartmentAdvisoryCommitteeTransaction departmentAdvisoryCommitteeTransaction;

	public Flux<List<ErpCommitteeDTO>> getGridData(String yearId) {
		List<Tuple> list = departmentAdvisoryCommitteeTransaction.getGridData(yearId);
		return this.convertDboToDto(list);
	}

	public Flux<List<ErpCommitteeDTO>> convertDboToDto(List<Tuple> tuple) {
		List<ErpCommitteeDTO> erpCommitteDtoList = new ArrayList<ErpCommitteeDTO>();
		Map<Integer, ErpCommitteeDTO> map = new HashMap<Integer, ErpCommitteeDTO>();
		List<String> programmeList = new ArrayList<String>();
		tuple.forEach(dbo -> {
			ErpCommitteeDTO dto = new ErpCommitteeDTO();
			dto.setId(Integer.parseInt(dbo.get("erp_committee_id").toString()));
			if(!map.containsKey(Integer.parseInt(dbo.get("erp_department_id").toString()))) {
				dto.setErpAcademicYear(new SelectDTO());
				dto.getErpAcademicYear().setLabel(dbo.get("academic_year").toString());
				dto.setErpDepartment(new SelectDTO());
				dto.getErpDepartment().setValue(dbo.get("erp_department_id").toString());
				dto.getErpDepartment().setLabel(dbo.get("department_name").toString());
				programmeList.clear();
				dto.setErpProgram(new SelectDTO());
				SelectDTO sdto = new SelectDTO();
				sdto.setLabel(dbo.get("programme_name").toString());
				String str = dbo.get("programme_name").toString();
				programmeList.add(str);
				dto.setErpProgram(sdto);
				map.put(Integer.parseInt(dbo.get("erp_department_id").toString()), dto);
				erpCommitteDtoList.add(dto);
			} else {
				ErpCommitteeDTO erpCommitteDto = map.get(Integer.parseInt(dbo.get("erp_department_id").toString()));
				SelectDTO program = new SelectDTO();
				String str = dbo.get("programme_name").toString();
				programmeList.add(str);
				String programme = programmeList.stream().collect(Collectors.joining(","));
				program.setLabel(programme);
				erpCommitteDto.setErpProgram(program);
				map.replace(Integer.parseInt(dbo.get("erp_department_id").toString()), erpCommitteDto);
			}  
		});
		return Flux.just(erpCommitteDtoList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpCommitteeDTO> dto, String userId) {
		return dto.handle((erpCommitteeDTO, synchronousSink) -> {
			Integer DACid = departmentAdvisoryCommitteeTransaction.getDACId();
			Integer MembersRoleId = departmentAdvisoryCommitteeTransaction.getMembersId();
			erpCommitteeDTO.setErpCommitteeType(new SelectDTO());
			erpCommitteeDTO.getErpCommitteeType().setValue(DACid.toString());
			erpCommitteeDTO.setMembersId(Integer.parseInt(MembersRoleId.toString()));
			boolean istrue = departmentAdvisoryCommitteeTransaction.duplicateCheck(erpCommitteeDTO);
			if(istrue) {
				synchronousSink.error(new DuplicateException("The DAC members are already added for the department"));
			} else {
				synchronousSink.next(erpCommitteeDTO);
			}
		}).cast(ErpCommitteeDTO.class)
				.map(data -> convertDtoToDbo(data, userId))
				.flatMap(s -> {
					if(!Utils.isNullOrEmpty(s.getId())) {
						departmentAdvisoryCommitteeTransaction.update(s);
					} else {
						departmentAdvisoryCommitteeTransaction.save(s);
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
		dbo.setCreatedUsersId(Integer.parseInt(userId));
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbo.setModifiedUsersId(Integer.parseInt(userId));
		}
		dbo.setRecordStatus('A');
		ErpCommitteeDBO dbList = null ;
		if(!Utils.isNullOrEmpty(dto.getId())) {
			dbList = departmentAdvisoryCommitteeTransaction.edit(dto.getId());
		}
		Set<ErpCommitteeMembersDBO> erpCommitteeMemebersList =  !Utils.isNullOrEmpty(dbList) ?  dbList.getErpCommitteeMembersDBOSet() : null;
		Map<Integer, ErpCommitteeMembersDBO> map = new HashMap<Integer, ErpCommitteeMembersDBO>();
		Map<Integer, ErpCommitteeMembersDBO> exmExistMap = new HashMap<Integer, ErpCommitteeMembersDBO>();
		Map<Integer, ErpCommitteeMembersDBO> studMap = new HashMap<Integer, ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(erpCommitteeMemebersList)) {
			erpCommitteeMemebersList.forEach(exmDbo-> {    			
				if(exmDbo.getRecordStatus()=='A' && exmDbo.getExternalsDBO() != null) {
					exmExistMap.put(exmDbo.getExternalsDBO().getId(), exmDbo);
				}
			});
		}	
		if(!Utils.isNullOrEmpty(erpCommitteeMemebersList )) {
			erpCommitteeMemebersList.forEach(role -> {
				if(role.getRecordStatus()=='A' && role.getErpCommitteeRoleDBO() != null) {
					map.put(role.getId(), role);
				}
			});
		}
		Map<Integer,Integer> internalMembers = new HashMap<Integer, Integer>();
		dto.getInternalMembers().forEach( empId -> {
			internalMembers.put(Integer.parseInt(empId.getValue()), Integer.parseInt(empId.getValue()));
		});
		Set<ErpCommitteeMembersDBO> erpCommitteeMembersSet = new HashSet<ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(dto.getErpCommitteeMembersDTOList())) {
			dto.getErpCommitteeMembersDTOList().forEach(membersDto -> {
				if(!Utils.isNullOrEmpty(membersDto.getErpCommitteeRole())) {
					if(!Utils.isNullOrEmpty(erpCommitteeMemebersList )) {
						erpCommitteeMemebersList.forEach(cmDbo-> {
							if(cmDbo.getRecordStatus()=='A' && cmDbo.getErpCommitteeRoleDBO() != null) {
								if(cmDbo.getErpCommitteeRoleDBO().getId() == Integer.parseInt(membersDto.getErpCommitteeRole().getValue())) {
									map.remove(cmDbo.getId());
									map.put(cmDbo.getEmpDBO().getId(), cmDbo);
								}
							}
						});
					}
					if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
						membersDto.getEmpList().forEach(empDto -> {
							ErpCommitteeMembersDBO membersDbo = null ;
							if(map.containsKey(Integer.parseInt(empDto.getValue()))) {
								membersDbo = map.get(Integer.parseInt(empDto.getValue()));
								membersDbo.setModifiedUsersId(Integer.parseInt(userId));
								map.remove(Integer.parseInt(empDto.getValue()));
							} else  {
								membersDbo = new ErpCommitteeMembersDBO();
								membersDbo.setCreatedUsersId(Integer.parseInt(userId));
							}
							membersDbo.setErpCommitteeDBO(dbo);
							if(!Utils.isNullOrEmpty(membersDto.getErpCommitteeRole().getValue())) {
								membersDbo.setErpCommitteeRoleDBO(new ErpCommitteeRoleDBO());
								membersDbo.getErpCommitteeRoleDBO().setId(Integer.parseInt(membersDto.getErpCommitteeRole().getValue()));
								if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
									membersDbo.setEmpDBO(new EmpDBO());
									membersDbo.getEmpDBO().setId(Integer.parseInt(empDto.getValue()));
									if(internalMembers.containsKey(Integer.parseInt(empDto.getValue()))) {
										internalMembers.remove(Integer.parseInt(empDto.getValue()));
									}
								}
							}
							membersDbo.setRecordStatus('A');
							erpCommitteeMembersSet.add(membersDbo);
						});
					}
				}
				if(!Utils.isNullOrEmpty(membersDto.getExternals())) {
					membersDto.getExternals().forEach( externalMembers -> {
						ErpCommitteeMembersDBO membersDbo2 = null;
						if(exmExistMap.containsKey(Integer.parseInt(externalMembers.getValue()))) {
							membersDbo2 = exmExistMap.get(Integer.parseInt(externalMembers.getValue()));
							membersDbo2.setModifiedUsersId(Integer.parseInt(userId));
							exmExistMap.remove(Integer.parseInt(externalMembers.getValue()));
						} else {
							membersDbo2 = new ErpCommitteeMembersDBO();
							membersDbo2.setCreatedUsersId(Integer.parseInt(userId));
						}
						membersDbo2.setErpCommitteeDBO(dbo);
						membersDbo2.setExternalsDBO(new ExternalsDBO());
						membersDbo2.getExternalsDBO().setId(Integer.parseInt(externalMembers.getValue()));
						membersDbo2.setRecordStatus('A');
						erpCommitteeMembersSet.add(membersDbo2);
					});
					if(!Utils.isNullOrEmpty(exmExistMap)) {
						exmExistMap.forEach((entry2, value2)-> {
							value2.setModifiedUsersId( Integer.parseInt(userId));
							value2.setRecordStatus('D');
							erpCommitteeMembersSet.add(value2);
						});
					}
					if(!Utils.isNullOrEmpty(map)) {
						map.forEach((entry, value)-> {
							value.setModifiedUsersId( Integer.parseInt(userId));
							value.setRecordStatus('D');
							erpCommitteeMembersSet.add(value);
						});
					}
				}
			});	
		}
		if(!Utils.isNullOrEmpty(internalMembers)) {
			internalMembers.forEach((e, v) -> {
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
		if(!Utils.isNullOrEmpty(erpCommitteeMemebersList)) {
			erpCommitteeMemebersList.forEach( studdbo-> {
				if(studdbo.getRecordStatus() == 'A' && studdbo.getBatchYearId() != null) {
					studMap.put(studdbo.getId(), studdbo);
				}
			});
		}
		if(!Utils.isNullOrEmpty(dto.getErpCommitteeMembersDTOList())) {  
			dto.getErpCommitteeMembersDTOList().forEach(studentDto -> {
				if(studentDto.getBatchYear()!= null) {
					ErpCommitteeMembersDBO studentDbo = null;
					if(studMap.containsKey(studentDto.getId())) {
						studentDbo = studMap.get(studentDto.getId());
						studentDbo.setModifiedUsersId(Integer.parseInt(userId));
						studMap.remove(studentDto.getId());
					} else {
						studentDbo = new ErpCommitteeMembersDBO();
						studentDbo.setCreatedUsersId(Integer.parseInt(userId));
					}
					studentDbo.setErpCommitteeDBO(dbo);
					studentDbo.setBatchYearId(new ErpAcademicYearDBO());
					studentDbo.getBatchYearId().setId(Integer.parseInt(studentDto.getBatchYear().getValue()));
					studentDbo.setStudentDBO(new StudentDBO());
					studentDbo.getStudentDBO().setId(studentDto.getStudentDTO().getId());
					studentDbo.setRecordStatus('A');
					erpCommitteeMembersSet.add(studentDbo);
				}
			});
		}
		if(!Utils.isNullOrEmpty(studMap)) {
			studMap.forEach((entry4, value4)-> {
				value4.setModifiedUsersId( Integer.parseInt(userId));
				value4.setRecordStatus('D');
				erpCommitteeMembersSet.add(value4);
			});
		}
		dbo.setErpCommitteeMembersDBOSet(erpCommitteeMembersSet);
		Set<ErpCommitteeProgrammeDBO> progexistDBOSet = !Utils.isNullOrEmpty(dbList) ? dbList.getErpCommitteeProgrammeDBOSet() : null;
		Map<Integer,ErpCommitteeProgrammeDBO> progExistMap = new HashMap<Integer, ErpCommitteeProgrammeDBO>();
		if(!Utils.isNullOrEmpty(progexistDBOSet)) {
			progexistDBOSet.forEach(progdbo -> {
				if(progdbo.getRecordStatus() == 'A') {
					progExistMap.put(progdbo.getErpProgrammeDBO().getId(), progdbo);
				}
			});
		} 
		Set<ErpCommitteeProgrammeDBO> erpCommitteeProgrammeSet = new HashSet<ErpCommitteeProgrammeDBO>();
		if(!Utils.isNullOrEmpty(dto.getErpCommitteeProgrammeDTOList())) {
			dto.getErpCommitteeProgrammeDTOList().forEach(programmeDetailsDto -> {
				programmeDetailsDto.getErpProgrammeList().forEach(programmeList -> {
					ErpCommitteeProgrammeDBO programmeDetailsDbo = null  ;
					if(progExistMap.containsKey(Integer.parseInt(programmeList.getValue()))) {
						programmeDetailsDbo = progExistMap.get(Integer.parseInt(programmeList.getValue()));
						programmeDetailsDbo.setModifiedUsersId(Integer.parseInt(userId));
						progExistMap.remove(Integer.parseInt(programmeList.getValue()));
					} else {
						programmeDetailsDbo = new ErpCommitteeProgrammeDBO();
						programmeDetailsDbo.setCreatedUsersId(Integer.parseInt(userId));
					}
					programmeDetailsDbo.setErpCommitteeDBO(dbo);
					programmeDetailsDbo.setErpProgrammeDBO(new ErpProgrammeDBO());
					programmeDetailsDbo.getErpProgrammeDBO().setId(Integer.parseInt(programmeList.getValue()));
					programmeDetailsDbo.setRecordStatus('A');
					erpCommitteeProgrammeSet.add(programmeDetailsDbo);	
				});
			});
		}	   
		if(!Utils.isNullOrEmpty(progExistMap)) {
			progExistMap.forEach((entry3, value3)-> {
				value3.setModifiedUsersId( Integer.parseInt(userId));
				value3.setRecordStatus('D');
				erpCommitteeProgrammeSet.add(value3);
			});
		}
		dbo.setErpCommitteeProgrammeDBOSet(erpCommitteeProgrammeSet);
		return dbo; 
	}

	public Mono<ErpCommitteeDTO> convertDboToDto2 (ErpCommitteeDBO dbo, List<Tuple> externals ) {
		ErpCommitteeDTO dto = new ErpCommitteeDTO();
		dto.setId(dbo.getId());
		dto.setErpAcademicYear(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getErpAcademicYearDBO())) {
			dto.getErpAcademicYear().setValue(dbo.getErpAcademicYearDBO().getId().toString());
			dto.getErpAcademicYear().setLabel(dbo.getErpAcademicYearDBO().getAcademicYear().toString());
		}	
		dto.setErpDepartment(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
			dto.getErpDepartment().setValue(dbo.getErpDepartmentDBO().getId().toString());
			dto.getErpDepartment().setLabel(dbo.getErpDepartmentDBO().getDepartmentName());
		}
		dto.setErpCommitteeType(new SelectDTO());
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeTypeDBO())) {
			dto.getErpCommitteeType().setValue(dbo.getErpCommitteeTypeDBO().getId().toString());
			dto.getErpCommitteeType().setLabel(dbo.getErpCommitteeTypeDBO().getCommitteeType().toString());	
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
		List<ErpCommitteeMembersDTO> committeeList = new ArrayList<ErpCommitteeMembersDTO>();
		Map<Integer,ErpCommitteeMembersDTO> erpCommitteeMembersDTOMap = new HashMap<Integer, ErpCommitteeMembersDTO>();
		Map<Integer,ErpCommitteeMembersDTO> erpCommitteeMembersExternalsMap = new HashMap<Integer,ErpCommitteeMembersDTO>();
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeMembersDBOSet())) {
			dbo.getErpCommitteeMembersDBOSet().forEach(membersDbo -> {
				ErpCommitteeMembersDTO erpCommitteeMembersDTO = new ErpCommitteeMembersDTO();
				if(!Utils.isNullOrEmpty(membersDbo.getErpCommitteeRoleDBO())) {
					if(!erpCommitteeMembersDTOMap.containsKey(membersDbo.getErpCommitteeRoleDBO().getId())) {
						if(Utils.isNullOrEmpty(erpCommitteeMembersDTO.getId())) {
							erpCommitteeMembersDTO.setId(membersDbo.getId());
							erpCommitteeMembersDTO.setErpCommitteeRole(new SelectDTO());
							erpCommitteeMembersDTO.getErpCommitteeRole().setValue(membersDbo.getErpCommitteeRoleDBO().getId().toString());
							erpCommitteeMembersDTO.getErpCommitteeRole().setLabel(membersDbo.getErpCommitteeRoleDBO().getCommitteeRole());
							erpCommitteeMembersDTO.setEmpList(new ArrayList<SelectDTO>());
						}
						SelectDTO empSelect = new SelectDTO();
						empSelect.setValue(membersDbo.getEmpDBO().getId().toString());
						empSelect.setLabel(membersDbo.getEmpDBO().getEmpName());
						erpCommitteeMembersDTO.getEmpList().add(empSelect);
						erpCommitteeMembersDTOMap.put(membersDbo.getErpCommitteeRoleDBO().getId(), erpCommitteeMembersDTO);
					} else {
						ErpCommitteeMembersDTO cmdbo = erpCommitteeMembersDTOMap.get(membersDbo.getErpCommitteeRoleDBO().getId());
						SelectDTO emp = new SelectDTO();
						emp.setValue(membersDbo.getEmpDBO().getId().toString());
						emp.setLabel(membersDbo.getEmpDBO().getEmpName());
						cmdbo.getEmpList().add(emp);
						erpCommitteeMembersDTOMap.replace(membersDbo.getErpCommitteeRoleDBO().getId(), cmdbo);
					}
				}
			});	   
			externals.forEach(externalDbo-> {
				ErpCommitteeMembersDTO erpCommitteeMembersDTO3 = new ErpCommitteeMembersDTO();
				if(!erpCommitteeMembersExternalsMap.containsKey(Integer.parseInt(externalDbo.get("cid").toString()))) {
					if(Utils.isNullOrEmpty(erpCommitteeMembersDTO3.getId())) {
						erpCommitteeMembersDTO3.setId(Integer.parseInt(externalDbo.get("membersid").toString()));
						erpCommitteeMembersDTO3.setErpExternalsCategory(new SelectDTO());
						erpCommitteeMembersDTO3.getErpExternalsCategory().setValue(externalDbo.get("cid").toString());
						erpCommitteeMembersDTO3.getErpExternalsCategory().setLabel(externalDbo.get("cname").toString());
						erpCommitteeMembersDTO3.setExternals(new ArrayList<SelectDTO>());
					}
					SelectDTO select = new SelectDTO();
					select.setValue(externalDbo.get("eid").toString());
					select.setLabel(externalDbo.get("ename").toString());
					erpCommitteeMembersDTO3.getExternals().add(select);
					erpCommitteeMembersExternalsMap.put(Integer.parseInt(externalDbo.get("cid").toString()), erpCommitteeMembersDTO3);
				} else {
					ErpCommitteeMembersDTO exmdbo = erpCommitteeMembersExternalsMap.get(Integer.parseInt(externalDbo.get("cid").toString()));
					SelectDTO select =  new SelectDTO();
					select.setValue(externalDbo.get("eid").toString());
					select.setLabel(externalDbo.get("ename").toString());
					exmdbo.getExternals().add(select);
					erpCommitteeMembersExternalsMap.replace(Integer.parseInt(externalDbo.get("cid").toString()), exmdbo);
				}
			});		
		}
		erpCommitteeMembersDTOMap.forEach((k, v) -> {
			committeeList.add(v);
		});
		erpCommitteeMembersExternalsMap.forEach((k2, v2) -> {
			committeeList.add(v2);
		});
		dbo.getErpCommitteeMembersDBOSet().forEach(studDbo -> {
			if(studDbo.getRecordStatus()=='A' && studDbo.getBatchYearId() != null) {
				ErpCommitteeMembersDTO erpCommitteeMembersDTO2 = new ErpCommitteeMembersDTO(); 
				if(Utils.isNullOrEmpty(erpCommitteeMembersDTO2.getId())) {
					erpCommitteeMembersDTO2.setId(studDbo.getId());
					erpCommitteeMembersDTO2.setBatchYear(new SelectDTO());
					erpCommitteeMembersDTO2.getBatchYear().setValue(studDbo.getBatchYearId().getId().toString());
					erpCommitteeMembersDTO2.getBatchYear().setLabel(studDbo.getBatchYearId().getAcademicYear().toString());
					erpCommitteeMembersDTO2.setStudentDTO(new StudentDTO());
					erpCommitteeMembersDTO2.getStudentDTO().setId(studDbo.getStudentDBO().getId());
					erpCommitteeMembersDTO2.getStudentDTO().setStudentName(studDbo.getStudentDBO().getStudentName());
					erpCommitteeMembersDTO2.getStudentDTO().setRegisterNo(studDbo.getStudentDBO().getRegisterNo());
					committeeList.add(erpCommitteeMembersDTO2);	
				}
			}
		});
		dto.setErpCommitteeMembersDTOList(committeeList);	
		List<ErpCommitteeProgrammeDTO> erpCommitteeProgrammeDTOlist = new ArrayList<ErpCommitteeProgrammeDTO>(); 
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeProgrammeDBOSet())) {
			ErpCommitteeProgrammeDTO  erpCommitteeProgrammeDTO = new ErpCommitteeProgrammeDTO();
			erpCommitteeProgrammeDTO.setErpProgrammeList(new ArrayList<SelectDTO>());
			dbo.getErpCommitteeProgrammeDBOSet().forEach(subDbo1 -> {
				SelectDTO programSelect = new SelectDTO();
				programSelect.setValue(String.valueOf(subDbo1.getErpProgrammeDBO().getId()));
				programSelect.setLabel(subDbo1.getErpProgrammeDBO().getProgrammeName());
				erpCommitteeProgrammeDTO.getErpProgrammeList().add(programSelect);
			});
			erpCommitteeProgrammeDTOlist.add(erpCommitteeProgrammeDTO);
			dto.setErpCommitteeProgrammeDTOList(erpCommitteeProgrammeDTOlist);
		}
		return Mono.just(dto);
	}

	public Mono<ErpCommitteeDTO> edit(int id) {
		ErpCommitteeDBO dbo  = departmentAdvisoryCommitteeTransaction.edit(id);
		List<Tuple> externals= departmentAdvisoryCommitteeTransaction.externalCategoryMembers(id);
		return this.convertDboToDto2(dbo, externals);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id, String userId) {
		return departmentAdvisoryCommitteeTransaction.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> copyDataFromPrevYear(Mono<ErpCommitteeDTO> dto, String userId) {
		return dto.handle((erpCommitteeDTO, synchronousSink) -> {
			boolean istrue = departmentAdvisoryCommitteeTransaction.duplicateCheck(erpCommitteeDTO);
			if(istrue) {
				synchronousSink.error(new DuplicateException("The DAC members are already added for the department"));
			} else {
				synchronousSink.next(erpCommitteeDTO);
			}
		}).cast(ErpCommitteeDTO.class)
				.map(data -> convertDboToDbo1(data, userId))
				.flatMap(s -> {
					departmentAdvisoryCommitteeTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public ErpCommitteeDBO convertDboToDbo1(ErpCommitteeDTO dto, String userId) {
		ErpCommitteeDBO erpCommitteeDBO = departmentAdvisoryCommitteeTransaction.getValue(dto.getErpAcademicYear().getValue(), dto.getErpDepartment().getValue());
		ErpCommitteeDBO  erpCommitteeDBO2 = new ErpCommitteeDBO();
		BeanUtils.copyProperties(erpCommitteeDBO,erpCommitteeDBO2,"id");
		erpCommitteeDBO2.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		erpCommitteeDBO2.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getImportToYear().getValue()));
		erpCommitteeDBO2.setCreatedUsersId(Integer.parseInt(userId));
		erpCommitteeDBO2.setModifiedUsersId(null);
		Set<ErpCommitteeMembersDBO> erpCommitteeMembersSet = new HashSet<ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(erpCommitteeDBO.getErpCommitteeMembersDBOSet())) {
			erpCommitteeDBO.getErpCommitteeMembersDBOSet().forEach(  membersDbo -> {
				ErpCommitteeMembersDBO erpCommitteeMembersDBO = new ErpCommitteeMembersDBO();
				BeanUtils.copyProperties(membersDbo, erpCommitteeMembersDBO,"id","erpCommitteeDBO");
				erpCommitteeMembersDBO.setErpCommitteeDBO(erpCommitteeDBO2);
				erpCommitteeMembersDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpCommitteeMembersDBO.setModifiedUsersId(null);
				erpCommitteeMembersSet.add(erpCommitteeMembersDBO);
			});
		}
		erpCommitteeDBO2.setErpCommitteeMembersDBOSet(erpCommitteeMembersSet);	
		Set<ErpCommitteeProgrammeDBO> erpCommitteeProgrammeSet = new HashSet<ErpCommitteeProgrammeDBO>();
		if(!Utils.isNullOrEmpty(erpCommitteeDBO.getErpCommitteeProgrammeDBOSet())) {
			erpCommitteeDBO.getErpCommitteeProgrammeDBOSet().forEach(program -> { 
				ErpCommitteeProgrammeDBO erpCommitteeProgrammeDBO = new ErpCommitteeProgrammeDBO();
				BeanUtils.copyProperties(program, erpCommitteeProgrammeDBO ,"id" ,"ErpCommitteeDBO");
				erpCommitteeProgrammeDBO.setErpCommitteeDBO(erpCommitteeDBO2);
				erpCommitteeProgrammeDBO.setCreatedUsersId(Integer.parseInt(userId));
				erpCommitteeProgrammeDBO.setModifiedUsersId(null);
				erpCommitteeProgrammeSet.add(erpCommitteeProgrammeDBO);
			});
		}
		erpCommitteeDBO2.setErpCommitteeProgrammeDBOSet(erpCommitteeProgrammeSet);
		return erpCommitteeDBO2;
	}
}	   	 




