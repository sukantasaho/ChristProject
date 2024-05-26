package com.christ.erp.services.handlers.curriculum.settings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeMembersDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeProgrammeCourseReviewDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeProgrammeCourseReviewDetailsDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeRoleDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ExternalsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.ErpCommitteeMembersDTO;
import com.christ.erp.services.dto.common.ErpCommitteeProgrammeCourseReviewDTO;
import com.christ.erp.services.dto.common.ErpCommitteeProgrammeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.transactions.curriculum.settings.BoardOfStudiesTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardOfStudiesHandler {
	
	@Autowired
    private BoardOfStudiesTransaction  boardOfStudiesTransaction ;
	
	public Flux<ErpCommitteeDTO> getGridData(String yearId) {
		return boardOfStudiesTransaction.getGridData(yearId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO6);
	}
		
	public ErpCommitteeDTO convertDBOToDTO6(Tuple  dbo) {
		ErpCommitteeDTO dto = new ErpCommitteeDTO();
		dto.setId(Integer.parseInt(dbo.get("erp_committee_id").toString()));
		dto.setErpAcademicYear(new SelectDTO());
		dto.getErpAcademicYear().setLabel(dbo.get("academic_year").toString());
		dto.setErpDepartment(new SelectDTO());
		if(Utils.isNullOrEmpty(dbo.get("department_name"))) {
			dto.getErpDepartment().setValue("");
			dto.getErpDepartment().setLabel("Common BOS");
		} else {
			dto.getErpDepartment().setValue(dbo.get("deptId").toString());
			dto.getErpDepartment().setLabel(dbo.get("department_name").toString());
		}
		dto.setProgrammeCourseStructureEntryLastDate((LocalDate) dbo.get("lastDate"));
		return dto;
	}

	public Mono<ErpCommitteeDTO> edit(int id ,String deptId) {
		ErpCommitteeDBO dbo1  = boardOfStudiesTransaction.edit(id,deptId);
		List<Tuple> externalCM = boardOfStudiesTransaction.externalCategoryMembers(id);
	    return this.convertDboToDto(dbo1,externalCM);
	}
	
	public Mono<ErpCommitteeDTO> convertDboToDto(ErpCommitteeDBO dbo, List<Tuple> externalCM ) {
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
			dto.setUniversityBOS(false);
		} else {
			dto.setUniversityBOS(true);
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
		dto.setProgrammeCourseStructureEntryLastDate(dbo.getProgrammeCourseStructureEntryLastDate());
		List<ErpCommitteeMembersDTO> listUpdate = new ArrayList<ErpCommitteeMembersDTO>();
		Map<Integer,ErpCommitteeMembersDTO> erpCommitteeMembersDTOMap = new HashMap<Integer,ErpCommitteeMembersDTO>();
		Map<Integer,ErpCommitteeMembersDTO> erpCommitteeMembersDtoExternalsMap = new HashMap<Integer,ErpCommitteeMembersDTO>();
		if(!Utils.isNullOrEmpty(dbo.getErpCommitteeMembersDBOSet())) {
			dbo.getErpCommitteeMembersDBOSet().forEach(subDbo -> {
				ErpCommitteeMembersDTO erpCommitteeMembersDTO = new ErpCommitteeMembersDTO();
				if(!Utils.isNullOrEmpty(subDbo.getErpCommitteeRoleDBO())) {
					if(!erpCommitteeMembersDTOMap.containsKey(subDbo.getErpCommitteeRoleDBO().getId())) { 
						erpCommitteeMembersDTO.setErpCommitteeRole(new SelectDTO());
						erpCommitteeMembersDTO.getErpCommitteeRole().setValue(subDbo.getErpCommitteeRoleDBO().getId().toString());
						erpCommitteeMembersDTO.getErpCommitteeRole().setLabel(subDbo.getErpCommitteeRoleDBO().getCommitteeRole());
						erpCommitteeMembersDTO.setEmpList(new ArrayList<SelectDTO>());
						SelectDTO emp = new SelectDTO();
						emp.setValue(subDbo.getEmpDBO().getId().toString());
						emp.setLabel(subDbo.getEmpDBO().getEmpName());
						erpCommitteeMembersDTO.getEmpList().add(emp);
					    erpCommitteeMembersDTOMap.put(subDbo.getErpCommitteeRoleDBO().getId(),erpCommitteeMembersDTO); 
					} else {
						ErpCommitteeMembersDTO cmdbo = erpCommitteeMembersDTOMap.get(subDbo.getErpCommitteeRoleDBO().getId());
						SelectDTO emp = new SelectDTO();
						emp.setValue(subDbo.getEmpDBO().getId().toString());
						emp.setLabel(subDbo.getEmpDBO().getEmpName());
						cmdbo.getEmpList().add(emp);
						erpCommitteeMembersDTOMap.replace(subDbo.getErpCommitteeRoleDBO().getId(), cmdbo);
					}
	            } 
			});		
            externalCM.forEach( edbo -> {
			ErpCommitteeMembersDTO erpCommitteeMembersDTO2 = new ErpCommitteeMembersDTO();
				if(!erpCommitteeMembersDtoExternalsMap.containsKey(Integer.parseInt(edbo.get("cid").toString()))) {
					erpCommitteeMembersDTO2.setErpExternalsCategory(new SelectDTO());
					erpCommitteeMembersDTO2.getErpExternalsCategory().setValue(edbo.get("cid").toString());
					erpCommitteeMembersDTO2.getErpExternalsCategory().setLabel(edbo.get("cname").toString());
					erpCommitteeMembersDTO2.setExternals(new ArrayList<SelectDTO>());
					SelectDTO select =  new SelectDTO();
					select.setValue(edbo.get("eid").toString());
					select.setLabel(edbo.get("ename").toString());
					erpCommitteeMembersDTO2.getExternals().add(select);
					erpCommitteeMembersDtoExternalsMap.put(Integer.parseInt(edbo.get("cid").toString()), erpCommitteeMembersDTO2);
				} else {
					ErpCommitteeMembersDTO exmdbo = erpCommitteeMembersDtoExternalsMap.get(Integer.parseInt(edbo.get("cid").toString()));
					SelectDTO select =  new SelectDTO();
					select.setValue(edbo.get("eid").toString());
					select.setLabel(edbo.get("ename").toString());
					exmdbo.getExternals().add(select);
					erpCommitteeMembersDtoExternalsMap.replace(Integer.parseInt(edbo.get("cid").toString()), exmdbo);
				}
			});		
		}
		erpCommitteeMembersDTOMap.forEach((k,v) -> {
			listUpdate.add(v);
		});
		erpCommitteeMembersDtoExternalsMap.forEach((k2,v2) -> {
			listUpdate.add(v2);
		});
		dto.setErpCommitteeMembersDTOList(listUpdate);
		if(!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
			if(!Utils.isNullOrEmpty(dbo.getErpCommitteeProgrammeDBOSet())) {
				List<ErpCommitteeProgrammeDTO> erpCommitteeProgrammeDTOlist = new ArrayList<ErpCommitteeProgrammeDTO>();
				dbo.getErpCommitteeProgrammeDBOSet().forEach(subDbo1 -> {
					ErpCommitteeProgrammeDTO  erpCommitteeProgrammeDTO = new ErpCommitteeProgrammeDTO();
					erpCommitteeProgrammeDTO.setId(subDbo1.getId());
					erpCommitteeProgrammeDTO.setErpProgramme(new SelectDTO());
					erpCommitteeProgrammeDTO.getErpProgramme().setValue(String.valueOf(subDbo1.getErpProgrammeDBO().getId()));
					erpCommitteeProgrammeDTO.getErpProgramme().setLabel(subDbo1.getErpProgrammeDBO().getProgrammeName());
					erpCommitteeProgrammeDTO.setProgrammeCoordinator(new SelectDTO());
					erpCommitteeProgrammeDTO.getProgrammeCoordinator().setValue(subDbo1.getProgrammeCoordinatorId().getId().toString());
					erpCommitteeProgrammeDTO.getProgrammeCoordinator().setLabel(subDbo1.getProgrammeCoordinatorId().getEmpName());
					erpCommitteeProgrammeDTO.setProgrammeStructureEntryLastDate(subDbo1.getProgramme_structure_entry_last_date());
					erpCommitteeProgrammeDTOlist.add(erpCommitteeProgrammeDTO);
				});
				dto.setErpCommitteeProgrammeDTOList(erpCommitteeProgrammeDTOlist);
			}
			List<ErpCommitteeProgrammeCourseReviewDTO> erpCommitteeProgrammeCourseReviewDTOlist = new ArrayList<ErpCommitteeProgrammeCourseReviewDTO>();
			if(!Utils.isNullOrEmpty(dbo.getErpCommitteeProgrammeCourseReviewDBOSet())) {
				dbo.getErpCommitteeProgrammeCourseReviewDBOSet().forEach(subDbo2 -> {
					ErpCommitteeProgrammeCourseReviewDTO erpCommitteeProgrammeCourseReviewDTO = new ErpCommitteeProgrammeCourseReviewDTO();
					erpCommitteeProgrammeCourseReviewDTO.setId(subDbo2.getId());
					erpCommitteeProgrammeCourseReviewDTO.setCourseStructureReviewer1(new SelectDTO());
					erpCommitteeProgrammeCourseReviewDTO.getCourseStructureReviewer1().setValue(subDbo2.getCourseStructureReviewer1Id().getId().toString());
					erpCommitteeProgrammeCourseReviewDTO.getCourseStructureReviewer1().setLabel(subDbo2.getCourseStructureReviewer1Id().getEmpName());
					erpCommitteeProgrammeCourseReviewDTO.setCourseStructureReviewer2(new SelectDTO());
					erpCommitteeProgrammeCourseReviewDTO.getCourseStructureReviewer2().setValue(subDbo2.getCourseStructureReviewer2Id().getId().toString());
					erpCommitteeProgrammeCourseReviewDTO.getCourseStructureReviewer2().setLabel(subDbo2.getCourseStructureReviewer2Id().getEmpName());
					erpCommitteeProgrammeCourseReviewDTO.setCourseStructureReviewLastDate(subDbo2.getCourseStructureReviewLastDate());
					List<SelectDTO> erpCommitteeProgrammeCourseReviewDetailsDTOlist = new ArrayList<SelectDTO>();
					if(!Utils.isNullOrEmpty(subDbo2.getErpCommitteeProgrammeCourseReviewDetailsDBOSet())) {
						subDbo2.getErpCommitteeProgrammeCourseReviewDetailsDBOSet().forEach(subDbo3 -> {
							SelectDTO progDepartMap = new SelectDTO();
							progDepartMap.setValue(String.valueOf(subDbo3.getErpProgrammeDepartmentMappingDBO().getId()));
							progDepartMap.setLabel(subDbo3.getErpProgrammeDepartmentMappingDBO().getErpProgrammeDBO().getProgrammeName());
							erpCommitteeProgrammeCourseReviewDetailsDTOlist.add(progDepartMap);
						});
//							erpCommitteeProgrammeCourseReviewDetailsDTOlist.add(erpCommitteeProgrammeCourseReviewDetailsDTO);
					}
					erpCommitteeProgrammeCourseReviewDTO.setErpCommitteeProgrammeCourseReviewDetailsDTOList(erpCommitteeProgrammeCourseReviewDetailsDTOlist);
					erpCommitteeProgrammeCourseReviewDTOlist.add(erpCommitteeProgrammeCourseReviewDTO);
				});
			}
			dto.setErpCommitteeProgrammeCourseReviewDTOList(erpCommitteeProgrammeCourseReviewDTOlist);
		}
		return Mono.just(dto);
	}
    
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdate(Mono<ErpCommitteeDTO> dto, String userId) {
		return dto.handle((erpCommitteeDTO, synchronousSink) ->  {
			List<ErpCommitteeDBO> list = null ;
			ErpCommitteeDBO value = null;
			Integer bosId = boardOfStudiesTransaction.getBosId(); 
			Integer membersId = boardOfStudiesTransaction.getMembersId();
			erpCommitteeDTO.setErpCommitteeType(new SelectDTO());
			erpCommitteeDTO.getErpCommitteeType().setValue(bosId.toString());
			erpCommitteeDTO.setMembersId(membersId);
			if(!erpCommitteeDTO.getUniversityBOS()) {
				 list = boardOfStudiesTransaction.duplicateCheck(erpCommitteeDTO);
			} else  {
				 value = boardOfStudiesTransaction.checkCommonUniversity(erpCommitteeDTO);
			}
			if(!Utils.isNullOrEmpty(list)) {
				synchronousSink.error(new DuplicateException("The BOS members are already added for these department for selected Academic Year"));
			} else if(!Utils.isNullOrEmpty(value)) {
				synchronousSink.error(new DuplicateException("The Common  BOS committee  already exist for selected Academic Year"));
			}
			else {
				synchronousSink.next(erpCommitteeDTO);
			}
		}).cast(ErpCommitteeDTO.class)
		  .map(data -> convertDtoToDbo(data, userId))
		  .flatMap( s -> {
	    	  if (!Utils.isNullOrEmpty(s.getId())) {
	    		  boardOfStudiesTransaction.update(s);
              } else {
            	  boardOfStudiesTransaction.save(s);
              }
		  return Mono.just(Boolean.TRUE);
	      }).map(Utils::responseResult);
	}
	
	public ErpCommitteeDBO convertDtoToDbo(ErpCommitteeDTO dto, String userId) {
	    ErpCommitteeDBO header = new ErpCommitteeDBO();
	    if(!Utils.isNullOrEmpty(dto.getId())) {
		    header.setId(dto.getId());
	    }
	    header.setErpAcademicYearDBO(new ErpAcademicYearDBO());
	    header.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getErpAcademicYear().getValue()));
	    if(!dto.getUniversityBOS()) {
		    header.setErpDepartmentDBO(new ErpDepartmentDBO());
		    header.getErpDepartmentDBO().setId(Integer.parseInt(dto.getErpDepartment().getValue()));
	    }
	    header.setErpCommitteeTypeDBO(new ErpCommitteeTypeDBO());
	    header.getErpCommitteeTypeDBO().setId(Integer.parseInt(dto.getErpCommitteeType().getValue()));
	    header.setProgrammeCourseStructureEntryLastDate(dto.getProgrammeCourseStructureEntryLastDate());
	    header.setRecordStatus('A');
	    if(!Utils.isNullOrEmpty(dto.getId())) {
	    	header.setModifiedUsersId(Integer.parseInt(userId));
	    } else {
	    	header.setCreatedUsersId(Integer.parseInt(userId));
	    }
	    ErpCommitteeDBO dbList = null ;
	    if(!Utils.isNullOrEmpty(dto.getId())) {
	    	dbList = boardOfStudiesTransaction.edit(dto.getId(),dto.getErpDepartment().value);
	    }
	    Set<ErpCommitteeMembersDBO> existDBOSet =  !Utils.isNullOrEmpty(dbList) ?  dbList.getErpCommitteeMembersDBOSet() : null;
		Map<Integer,ErpCommitteeMembersDBO> existDBOMap = new HashMap<Integer, ErpCommitteeMembersDBO>();
		Map<Integer,ErpCommitteeMembersDBO> exmExistDBOMap = new HashMap<Integer, ErpCommitteeMembersDBO>();
		if (!Utils.isNullOrEmpty(existDBOSet)) {
			existDBOSet.forEach(exmDbo-> {    			
				if (exmDbo.getRecordStatus()=='A' && exmDbo.getExternalsDBO() != null) {
					exmExistDBOMap.put(exmDbo.getExternalsDBO().getId(), exmDbo);
				}
			});
		}	
		if(!Utils.isNullOrEmpty(existDBOSet)) {
			existDBOSet.forEach(role -> {
				if (role.getRecordStatus()=='A' && role.getErpCommitteeRoleDBO() != null) {
					existDBOMap.put(role.getId(), role);
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
	    			if (!Utils.isNullOrEmpty(existDBOSet)) {
	    				existDBOSet.forEach(cmDbo-> {
	    					if (cmDbo.getRecordStatus()=='A' && cmDbo.getErpCommitteeRoleDBO() != null) {
	    						if(cmDbo.getErpCommitteeRoleDBO().getId() == Integer.parseInt(membersDto.getErpCommitteeRole().getValue())) {
	    							existDBOMap.remove(cmDbo.getId());
	    							existDBOMap.put(Integer.parseInt(cmDbo.getEmpDBO().getId().toString()+membersDto.getErpCommitteeRole().getValue()), cmDbo);
		    					}
		    				}
		    			});
		    		}
		    		if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
		    			membersDto.getEmpList().forEach(emp1 -> {
			    			ErpCommitteeMembersDBO membersDbo = null ;
			    			if(existDBOMap.containsKey(Integer.parseInt(emp1.getValue()+membersDto.getErpCommitteeRole().getValue()))) {
			    				membersDbo = existDBOMap.get(Integer.parseInt(emp1.getValue()+membersDto.getErpCommitteeRole().getValue()));
			    				existDBOMap.remove(Integer.parseInt(emp1.getValue()+membersDto.getErpCommitteeRole().getValue()));
			    				membersDbo.setModifiedUsersId(Integer.parseInt(userId));
			    			} else  {
			    				membersDbo = new ErpCommitteeMembersDBO();
			    				membersDbo.setCreatedUsersId(Integer.parseInt(userId));
			    			}
			    		    membersDbo.setErpCommitteeDBO(header);
				    		if(!Utils.isNullOrEmpty(membersDto.getErpCommitteeRole().getValue())) {
					    		membersDbo.setErpCommitteeRoleDBO(new ErpCommitteeRoleDBO());
					    		membersDbo.getErpCommitteeRoleDBO().setId(Integer.parseInt(membersDto.getErpCommitteeRole().getValue()));
					    		if(!Utils.isNullOrEmpty(membersDto.getEmpList())) {
							    		membersDbo.setEmpDBO(new EmpDBO());
							    		membersDbo.getEmpDBO().setId(Integer.parseInt(emp1.getValue()));
							    		if(internalMembers.containsKey(Integer.parseInt(emp1.getValue()))) {
							    			internalMembers.remove(Integer.parseInt(emp1.getValue()));
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
			    		if(exmExistDBOMap.containsKey(Integer.parseInt(externalMembers.getValue()))) {
			    			membersDbo2 = exmExistDBOMap.get(Integer.parseInt(externalMembers.getValue()));
			    			membersDbo2.setModifiedUsersId(Integer.parseInt(userId));
			    			exmExistDBOMap.remove(Integer.parseInt(externalMembers.getValue()));
			    		} else {
			    			membersDbo2 = new ErpCommitteeMembersDBO();
			    			membersDbo2.setCreatedUsersId(Integer.parseInt(userId));
			    		}
			    		membersDbo2.setErpCommitteeDBO(header);
			    		membersDbo2.setExternalsDBO(new ExternalsDBO());
				    	membersDbo2.getExternalsDBO().setId(Integer.parseInt(externalMembers.getValue()));
				    	membersDbo2.setRecordStatus('A');
				    	erpCommitteeMembersSet.add(membersDbo2);
			    	});
		    		if (!Utils.isNullOrEmpty(exmExistDBOMap)) {
		    			exmExistDBOMap.forEach((entry2, value2)-> {
		    				value2.setModifiedUsersId( Integer.parseInt(userId));
		    				value2.setRecordStatus('D');
		    				erpCommitteeMembersSet.add(value2);
		    			});
		    		}
		    	   
		    		 if (!Utils.isNullOrEmpty(existDBOMap)) {
	    			    	existDBOMap.forEach((entry, value)-> {
	    			    		value.setModifiedUsersId( Integer.parseInt(userId));
	    						value.setRecordStatus('D');
	    						erpCommitteeMembersSet.add(value);
	    					});
	    			 }
	    		}
	    	});	
	    }
	    if(!Utils.isNullOrEmpty(internalMembers)) {
	    	internalMembers.forEach((e,v) -> {
	    		ErpCommitteeMembersDBO membersDbo = new ErpCommitteeMembersDBO();
	    		membersDbo.setErpCommitteeDBO(header);
	    		membersDbo.setEmpDBO(new EmpDBO());
	    		membersDbo.getEmpDBO().setId(v);
	    		membersDbo.setErpCommitteeRoleDBO(new ErpCommitteeRoleDBO());
	    		membersDbo.getErpCommitteeRoleDBO().setId(dto.getMembersId());
	    		membersDbo.setCreatedUsersId(Integer.parseInt(userId));
	    		membersDbo.setRecordStatus('A');
	    		erpCommitteeMembersSet.add(membersDbo);
	    	});
	    }
	    header.setErpCommitteeMembersDBOSet(erpCommitteeMembersSet);
	    if(!dto.getUniversityBOS()) {
		    Set<ErpCommitteeProgrammeDBO> progexistDBOSet = !Utils.isNullOrEmpty(dbList) ?  dbList.getErpCommitteeProgrammeDBOSet() : null;
		    Map<Integer,ErpCommitteeProgrammeDBO> progExistDBOMap = new HashMap<Integer, ErpCommitteeProgrammeDBO>();
		    if (!Utils.isNullOrEmpty(progexistDBOSet)) {
		    	progexistDBOSet.forEach( progdbo-> {
					if (progdbo.getRecordStatus() == 'A') {
						progExistDBOMap.put(progdbo.getId(), progdbo);
					}
				});
			}
		    Set<ErpCommitteeProgrammeDBO> ErpCommitteeProgrammeSet = new HashSet<ErpCommitteeProgrammeDBO>();
		    if(!Utils.isNullOrEmpty(dto.getErpCommitteeProgrammeDTOList())) {
		    	dto.getErpCommitteeProgrammeDTOList().forEach( programmeDto -> {
		    		ErpCommitteeProgrammeDBO programmeDbo = null;
		    		if(progExistDBOMap.containsKey(programmeDto.getId())) {
		    			programmeDbo = progExistDBOMap.get(programmeDto.getId());
		    			programmeDbo.setModifiedUsersId(Integer.parseInt(userId));
		    			progExistDBOMap.remove(programmeDto.getId());
		    		} else {
		    			programmeDbo = new ErpCommitteeProgrammeDBO();
		    			programmeDbo.setCreatedUsersId(Integer.parseInt(userId));
		    		}
		    		programmeDbo.setErpCommitteeDBO(header);
		    		programmeDbo.setErpProgrammeDBO(new ErpProgrammeDBO());
		    		programmeDbo.getErpProgrammeDBO().setId(Integer.parseInt(programmeDto.getErpProgramme().getValue()));
		    		programmeDbo.setProgrammeCoordinatorId(new EmpDBO());
		    		programmeDbo.getProgrammeCoordinatorId().setId(Integer.parseInt(programmeDto.getProgrammeCoordinator().getValue()));
		    		programmeDbo.setProgramme_structure_entry_last_date(programmeDto.getProgrammeStructureEntryLastDate());
		    		programmeDbo.setRecordStatus('A');
		    		ErpCommitteeProgrammeSet.add(programmeDbo);
		    	});
		    }
			if (!Utils.isNullOrEmpty(progExistDBOMap)) {
				progExistDBOMap.forEach((entry3, value3)-> {
					value3.setModifiedUsersId( Integer.parseInt(userId));
					value3.setRecordStatus('D');
					ErpCommitteeProgrammeSet.add(value3);
				});
			}
		    header.setErpCommitteeProgrammeDBOSet(ErpCommitteeProgrammeSet);
		    Set<ErpCommitteeProgrammeCourseReviewDBO> erpCommitteeProgrammeCourseReviewSet = new HashSet<ErpCommitteeProgrammeCourseReviewDBO>();
		    Map<Integer,ErpCommitteeProgrammeCourseReviewDBO> pcrExistDBOMap = new HashMap<Integer, ErpCommitteeProgrammeCourseReviewDBO>();
		    Map<Integer,ErpCommitteeProgrammeCourseReviewDetailsDBO> pcrDetailsExistDBOMap = new HashMap<Integer, ErpCommitteeProgrammeCourseReviewDetailsDBO>();
		    Set<ErpCommitteeProgrammeCourseReviewDBO> pcrExistDBOSet =!Utils.isNullOrEmpty(dbList) ?  dbList.getErpCommitteeProgrammeCourseReviewDBOSet() : null; ;
		    if(!Utils.isNullOrEmpty(pcrExistDBOSet)) {
		    	pcrExistDBOSet.forEach(pcrDbo -> {
		    		if(pcrDbo.getRecordStatus() == 'A') {
		    			pcrExistDBOMap.put(pcrDbo.getId(), pcrDbo);
		    			Set<ErpCommitteeProgrammeCourseReviewDetailsDBO> pcrDetailsExistDBOSet = pcrDbo.getErpCommitteeProgrammeCourseReviewDetailsDBOSet();
			    		if(!Utils.isNullOrEmpty(pcrDetailsExistDBOSet)) {
			    			pcrDetailsExistDBOSet.forEach(pcrDetailsDbo -> {
			    	    		if(pcrDetailsDbo.getRecordStatus() == 'A') {
			    	    			pcrDetailsExistDBOMap.put(pcrDetailsDbo.getErpProgrammeDepartmentMappingDBO().getId(), pcrDetailsDbo);
			    	    		}
			    	    	});
			    	    }
		    		}
		    	});
		    }
		    if(!Utils.isNullOrEmpty(dto.getErpCommitteeProgrammeCourseReviewDTOList())) {
		    	dto.getErpCommitteeProgrammeCourseReviewDTOList().forEach( programmeReviewDto ->{
		    		if(!Utils.isNullOrEmpty(programmeReviewDto)) {
		    			ErpCommitteeProgrammeCourseReviewDBO programmeReviewDbo = null;
			    		if(pcrExistDBOMap.containsKey(programmeReviewDto.getId())) {
			    			programmeReviewDbo = pcrExistDBOMap.get(programmeReviewDto.getId());
			    			pcrExistDBOMap.remove(programmeReviewDto.getId());
			    		} else {
			    			programmeReviewDbo = new ErpCommitteeProgrammeCourseReviewDBO() ;
			    		}
			    		ErpCommitteeProgrammeCourseReviewDBO programmeReviewDbo1 = programmeReviewDbo;
				        programmeReviewDbo.setErpCommitteeDBO(header);
				    	programmeReviewDbo.setCourseStructureReviewer1Id(new EmpDBO());
				    	programmeReviewDbo.getCourseStructureReviewer1Id().setId(Integer.parseInt(programmeReviewDto.getCourseStructureReviewer1().getValue()));
				    	programmeReviewDbo.setCourseStructureReviewer2Id(new EmpDBO());
				    	programmeReviewDbo.getCourseStructureReviewer2Id().setId(Integer.parseInt(programmeReviewDto.getCourseStructureReviewer2().getValue()));
				    	programmeReviewDbo.setCourseStructureReviewLastDate(programmeReviewDto.getCourseStructureReviewLastDate());
				    	programmeReviewDbo.setRecordStatus('A');
				    	programmeReviewDbo.setCreatedUsersId(Integer.parseInt(userId));
				    	if(!Utils.isNullOrEmpty(programmeReviewDto.getId())) {
				    		programmeReviewDbo.setModifiedUsersId(Integer.parseInt(userId));
				    	} 
			    		Set<ErpCommitteeProgrammeCourseReviewDetailsDBO> erpCommitteeProgrammeCourseReviewDetailsSet = new HashSet<ErpCommitteeProgrammeCourseReviewDetailsDBO>();
			    		if(!Utils.isNullOrEmpty(programmeReviewDto.getErpCommitteeProgrammeCourseReviewDetailsDTOList())) {
			    			 ErpCommitteeProgrammeCourseReviewDetailsDBO programmeReviewDetailsDbo1 = null  ;
			    			 programmeReviewDto.getErpCommitteeProgrammeCourseReviewDetailsDTOList().forEach( programmeReviewDetailsDto -> {
				    				ErpCommitteeProgrammeCourseReviewDetailsDBO programmeReviewDetailsDbo  = programmeReviewDetailsDbo1;
				    				if(pcrDetailsExistDBOMap.containsKey(Integer.parseInt(programmeReviewDetailsDto.getValue()))) {
				    					programmeReviewDetailsDbo = pcrDetailsExistDBOMap.get(Integer.parseInt(programmeReviewDetailsDto.getValue()));
				    					pcrDetailsExistDBOMap.remove(Integer.parseInt(programmeReviewDetailsDto.getValue()));
				    				} else {
				    					programmeReviewDetailsDbo = new ErpCommitteeProgrammeCourseReviewDetailsDBO();
				    					programmeReviewDetailsDbo.setErpProgrammeDepartmentMappingDBO(new ErpProgrammeDepartmentMappingDBO());
				    					programmeReviewDetailsDbo.getErpProgrammeDepartmentMappingDBO().setId(Integer.parseInt(programmeReviewDetailsDto.getValue()));
				    				}
				    				programmeReviewDetailsDbo.setErpCommitteeProgrammeCourseReviewDBO(programmeReviewDbo1);
				    				programmeReviewDetailsDbo.setRecordStatus('A');
				    				programmeReviewDetailsDbo.setCreatedUsersId(Integer.parseInt(userId));
				    				erpCommitteeProgrammeCourseReviewDetailsSet.add(programmeReviewDetailsDbo);	
			    				 });
			    		}
			    		if (!Utils.isNullOrEmpty(pcrDetailsExistDBOMap)) {
			    			pcrDetailsExistDBOMap.forEach((entry5, value5)-> {
			    				value5.setModifiedUsersId( Integer.parseInt(userId));
			    				value5.setRecordStatus('D');
			    				erpCommitteeProgrammeCourseReviewDetailsSet.add(value5);
			    			});
			    		}
			    		programmeReviewDbo.setErpCommitteeProgrammeCourseReviewDetailsDBOSet(erpCommitteeProgrammeCourseReviewDetailsSet);
			    		erpCommitteeProgrammeCourseReviewSet.add(programmeReviewDbo);
		    		}
		    	});	    
		    }
		    if (!Utils.isNullOrEmpty(pcrExistDBOMap)) {
		    	pcrExistDBOMap.forEach((entry4, value4)-> {
					value4.setModifiedUsersId( Integer.parseInt(userId));
					value4.setRecordStatus('D');
					value4.getErpCommitteeProgrammeCourseReviewDetailsDBOSet().forEach( detailsDbo -> {
						detailsDbo.setRecordStatus('D');
						detailsDbo.setModifiedUsersId(Integer.parseInt(userId));	
					});
					erpCommitteeProgrammeCourseReviewSet.add(value4);
				});
			}
		    header.setErpCommitteeProgrammeCourseReviewDBOSet(erpCommitteeProgrammeCourseReviewSet);
	    }	 
		return header;
	}
	
    @SuppressWarnings("rawtypes")
	public Mono<ApiResult> delete(int id,String deptId, String userId) {
         ErpCommitteeDBO dbo = boardOfStudiesTransaction.edit(id,deptId);
        return this.convertDbo(dbo,userId);
    }
    
    @SuppressWarnings("rawtypes")
	public Mono<ApiResult> convertDbo(ErpCommitteeDBO dbo, String userId ) {
    	if(!Utils.isNullOrEmpty(dbo)) {
    		dbo.setRecordStatus('D');
            dbo.setModifiedUsersId(Integer.parseInt(userId));
            dbo.getErpCommitteeMembersDBOSet().forEach(membersDbo -> {
            	membersDbo.setRecordStatus('D');
            	membersDbo.setModifiedUsersId(Integer.parseInt(userId));
            });
            if(!Utils.isNullOrEmpty(dbo.getErpDepartmentDBO())) {
            	dbo.getErpCommitteeProgrammeDBOSet().forEach(programmeDbo -> {
                	programmeDbo.setRecordStatus('D');
                	programmeDbo.setModifiedUsersId(Integer.parseInt(userId));
                });
                dbo.getErpCommitteeProgrammeCourseReviewDBOSet().forEach(courseDbo -> {
                	courseDbo.setRecordStatus('D');
                	courseDbo.setModifiedUsersId(Integer.parseInt(userId));
                	courseDbo.getErpCommitteeProgrammeCourseReviewDetailsDBOSet().forEach(courseDetailsDbo -> {
                		courseDetailsDbo.setRecordStatus('D');
                		courseDetailsDbo.setModifiedUsersId(Integer.parseInt(userId));
                	});
                });
            }
            boardOfStudiesTransaction.update(dbo);
            return  Mono.just(Boolean.TRUE).map(Utils::responseResult);
    	} else {
    		return Mono.error(new NotFoundException(null));
    	}
    }	

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveImport(Mono<ErpCommitteeDTO> data, String userId) {
		return data.handle((erpCommitteeDTO, synchronousSink) ->  {
			List<ErpCommitteeDBO> list = null ;
			ErpCommitteeDBO value = null;
			Integer bosId = boardOfStudiesTransaction.getBosId();
			erpCommitteeDTO.setErpCommitteeType(new SelectDTO());
			erpCommitteeDTO.getErpCommitteeType().setValue(bosId.toString());
			if(!erpCommitteeDTO.getUniversityBOS()) {
				 list = boardOfStudiesTransaction.duplicateCheck(erpCommitteeDTO);
			} else  {
				 value = boardOfStudiesTransaction.checkCommonUniversity(erpCommitteeDTO);
			}
			if(!Utils.isNullOrEmpty(list)) {
				synchronousSink.error(new DuplicateException("The BOS members are already added for these department for selected Academic Year"));
			} else if(!Utils.isNullOrEmpty(value)) {
				synchronousSink.error(new DuplicateException("The Common  BOS committee  already exist for selected Academic Year"));
			}
			else {
				synchronousSink.next(erpCommitteeDTO);
			}
		}).cast(ErpCommitteeDTO.class)
			.map(data1 -> convertDboToDbo(data1,userId))
		  .flatMap( s -> {
            	  boardOfStudiesTransaction.merge(s);
		  return Mono.just(Boolean.TRUE);
	      }).map(Utils::responseResult);
	}

	public ErpCommitteeDBO convertDboToDbo(ErpCommitteeDTO dto, String userId) {
		ErpCommitteeDBO value = boardOfStudiesTransaction.getPerviousYearData(dto);
		ErpCommitteeDBO  valueNew =  new ErpCommitteeDBO();
		BeanUtils.copyProperties(value,valueNew,"id");
		valueNew.setErpAcademicYearDBO(new ErpAcademicYearDBO());
		valueNew.getErpAcademicYearDBO().setId(Integer.parseInt(dto.getImportToYear().getValue()));
		if(!dto.getUniversityBOS()) {
			valueNew.setErpDepartmentDBO(new ErpDepartmentDBO());
			valueNew.getErpDepartmentDBO().setId(value.getErpDepartmentDBO().getId());
		}
		valueNew.setCreatedUsersId(Integer.parseInt(userId));
		valueNew.setModifiedUsersId(null);
		Set<ErpCommitteeMembersDBO> erpCommitteeMembersSet = new HashSet<ErpCommitteeMembersDBO>();
		if(!Utils.isNullOrEmpty(value.getErpCommitteeMembersDBOSet())) {
			value.getErpCommitteeMembersDBOSet().forEach(  membersDbo -> {
				ErpCommitteeMembersDBO mem = new ErpCommitteeMembersDBO();
				BeanUtils.copyProperties(membersDbo, mem,"id","erpCommitteeDBO");
				mem.setErpCommitteeDBO(valueNew);
				mem.setCreatedUsersId(Integer.parseInt(userId));
				mem.setModifiedUsersId(null);
				erpCommitteeMembersSet.add(mem);
			});
		}
		valueNew.setErpCommitteeMembersDBOSet(erpCommitteeMembersSet);	
		if(!dto.getUniversityBOS()) {
			Set<ErpCommitteeProgrammeDBO> ErpCommitteeProgrammeSet = new HashSet<ErpCommitteeProgrammeDBO>();
			if(!Utils.isNullOrEmpty(value.getErpCommitteeProgrammeDBOSet())) {
				value.getErpCommitteeProgrammeDBOSet().forEach( progDbo -> {
					ErpCommitteeProgrammeDBO prog =  new ErpCommitteeProgrammeDBO();
					BeanUtils.copyProperties(progDbo, prog,"id","erpCommitteeDBO");
					prog.setErpCommitteeDBO(valueNew);
					prog.setCreatedUsersId(Integer.parseInt(userId));
					prog.setModifiedUsersId(null);
					ErpCommitteeProgrammeSet.add(prog);
				});
			}
			valueNew.setErpCommitteeProgrammeDBOSet(ErpCommitteeProgrammeSet);
			Set<ErpCommitteeProgrammeCourseReviewDBO> erpCommitteeProgrammeCourseReviewSet = new HashSet<ErpCommitteeProgrammeCourseReviewDBO>();
			if(!Utils.isNullOrEmpty(value.getErpCommitteeProgrammeCourseReviewDBOSet())) {
				value.getErpCommitteeProgrammeCourseReviewDBOSet().forEach(  courseDbo -> {
					ErpCommitteeProgrammeCourseReviewDBO course = new ErpCommitteeProgrammeCourseReviewDBO();
					BeanUtils.copyProperties(courseDbo, course,"id","erpCommitteeDBO");
					course.setErpCommitteeDBO(valueNew);
					course.setCreatedUsersId(Integer.parseInt(userId));
					course.setModifiedUsersId(null);
					if(!Utils.isNullOrEmpty(courseDbo)) {
						Set<ErpCommitteeProgrammeCourseReviewDetailsDBO> erpCommitteeProgrammeCourseReviewDetailsSet = new HashSet<ErpCommitteeProgrammeCourseReviewDetailsDBO>();
						courseDbo.getErpCommitteeProgrammeCourseReviewDetailsDBOSet().forEach( courseDetails -> {
							ErpCommitteeProgrammeCourseReviewDetailsDBO courseDet = new ErpCommitteeProgrammeCourseReviewDetailsDBO();
							BeanUtils.copyProperties(courseDetails, courseDet,"id","erpCommitteeProgrammeCourseReviewDBO");
							courseDet.setErpCommitteeProgrammeCourseReviewDBO(course);
							courseDet.setCreatedUsersId(Integer.parseInt(userId));
							courseDet.setModifiedUsersId(null);
							erpCommitteeProgrammeCourseReviewDetailsSet.add(courseDet); 
						});
						course.setErpCommitteeProgrammeCourseReviewDetailsDBOSet(erpCommitteeProgrammeCourseReviewDetailsSet);
					}
					erpCommitteeProgrammeCourseReviewSet.add(course);
				});
			}
			valueNew.setErpCommitteeProgrammeCourseReviewDBOSet(erpCommitteeProgrammeCourseReviewSet);
		} 
		return valueNew;
	}

}