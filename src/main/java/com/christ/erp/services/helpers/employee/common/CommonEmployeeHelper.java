package com.christ.erp.services.helpers.employee.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dto.employee.common.*;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;

@Service
public class CommonEmployeeHelper {

    private static volatile CommonEmployeeHelper commonEmployeeHelper = null;
    CommonEmployeeTransaction commonEmployeeTransaction = CommonEmployeeTransaction.getInstance();

    public static CommonEmployeeHelper getInstance() {
        if(commonEmployeeHelper==null) {
            commonEmployeeHelper = new CommonEmployeeHelper();
        }
        return commonEmployeeHelper;
    }

	public List<EmpCampusDeaneryDepartmentDTO> setEmpCampusDeaneryDepartmentDBOsToDTOs(
			List<ErpCampusDepartmentMappingDBO> dboList) {
		List<EmpCampusDeaneryDepartmentDTO> dtoList = null;
		if(dboList!=null && dboList.size()>0) {	
			dtoList = new ArrayList<EmpCampusDeaneryDepartmentDTO>();
     		Set<Integer> campusIds = new HashSet<Integer>();
     		for (ErpCampusDepartmentMappingDBO dbo : dboList) {
     			 EmpCampusDeaneryDepartmentDTO dto = new EmpCampusDeaneryDepartmentDTO(); 
			     if(!Utils.isNullOrEmpty(dbo.erpCampusDBO) && !Utils.isNullOrEmpty(dbo.erpCampusDBO.id) && !Utils.isNullOrEmpty(dbo.erpCampusDBO.campusName) && dbo.erpCampusDBO.recordStatus == 'A') {
			    	 dto.value =dbo.id+"-"+String.valueOf(dbo.erpCampusDBO.id);
					 dto.label = dbo.erpCampusDBO.campusName;
				    if (!campusIds.contains(dbo.erpCampusDBO.id)) {
				    	campusIds.add(dbo.erpCampusDBO.id);
					    dto.children = new ArrayList <EmpCampusDeaneryDTO>();
						Set<Integer> deaneryIds = new HashSet<Integer>();
						for (ErpCampusDepartmentMappingDBO dbo2 : dboList) {
							if( !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) &&  !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)  &&
									!deaneryIds.contains(dbo2.erpDepartmentDBO.erpDeaneryDBO.id) && dbo2.erpDepartmentDBO.recordStatus == 'A' && dbo2.erpDepartmentDBO.erpDeaneryDBO.recordStatus == 'A'  ){
							      EmpCampusDeaneryDTO deanery = new EmpCampusDeaneryDTO();
						      if (!Utils.isNullOrEmpty(dbo.erpCampusDBO) && !Utils.isNullOrEmpty(dbo2.erpCampusDBO)
						    		   && dbo.erpCampusDBO.id == dbo2.erpCampusDBO.id && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)
						    		     && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO.id) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO.deaneryName) 
						    		     && dbo2.erpCampusDBO.recordStatus == 'A' && dbo2.erpDepartmentDBO.recordStatus == 'A' && dbo2.erpDepartmentDBO.erpDeaneryDBO.recordStatus == 'A') {
						    	  deanery.value = dbo2.id+"-"+dbo.erpCampusDBO.id+"-"+dbo2.erpDepartmentDBO.erpDeaneryDBO.id;
								  deanery.label = dbo2.erpDepartmentDBO.erpDeaneryDBO.deaneryName;
								  dto.children.add(deanery);
								  deaneryIds.add(dbo2.erpDepartmentDBO.erpDeaneryDBO.id);
								  deanery.children = new ArrayList<EmpDeaneryDepartmentDTO>();
						          for (ErpCampusDepartmentMappingDBO dbo3 : dboList) {
							        	 EmpDeaneryDepartmentDTO department = new EmpDeaneryDepartmentDTO();
							             if (!Utils.isNullOrEmpty(dbo2.erpCampusDBO) && !Utils.isNullOrEmpty(dbo3.erpCampusDBO)  && dbo2.erpCampusDBO.id == dbo3.erpCampusDBO.id
							            		&& !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo2.erpDepartmentDBO.erpDeaneryDBO)
							            		&& !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.erpDeaneryDBO) && dbo2.erpDepartmentDBO.erpDeaneryDBO.id == dbo3.erpDepartmentDBO.erpDeaneryDBO.id
							            		&& !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO) && !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.id) && !Utils.isNullOrEmpty(dbo3.erpDepartmentDBO.departmentName)
							            		&& dbo3.erpCampusDBO.recordStatus == 'A' && dbo3.erpDepartmentDBO.recordStatus == 'A' && dbo3.erpDepartmentDBO.erpDeaneryDBO.recordStatus == 'A'){
							            	department.value = dbo3.id+"-"+dbo.erpCampusDBO.id+"-"+dbo2.erpDepartmentDBO.erpDeaneryDBO.id+"-"+dbo3.erpDepartmentDBO.id;
									    	department.label = dbo3.erpDepartmentDBO.departmentName;
									    	deanery.children.add(department);
						                 }
							      }
						     }
						   }
					    }
						dtoList.add(dto);
			        }
			   }
	  	  }
	   }
		return dtoList;
	}

	public List<HostelProgrammeDetailsDTO> setCampusLevelProgrammeDBOToDTO(List<HostelProgrammeDetailsDBO> dbo) {
    	List<HostelProgrammeDetailsDTO> dto = null;
    	if(!Utils.isNullOrEmpty(dbo)|| dbo.size() > 0){
    		dto = new ArrayList<HostelProgrammeDetailsDTO>();
    		Set<Integer> campusId= new HashSet<Integer>();
    		for(HostelProgrammeDetailsDBO dboLists:dbo){
				HostelProgrammeDetailsDTO dtoList = new HostelProgrammeDetailsDTO();
				ArrayList checked = new ArrayList();
    			if(!Utils.isNullOrEmpty(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO) && !Utils.isNullOrEmpty(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id) &&
					!Utils.isNullOrEmpty(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName)){
    				dtoList.value = dboLists.erpCampusProgrammeMappingDBO.id+"-"+String.valueOf(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
    				dtoList.label = dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName;
    				if(!campusId.contains(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id)){
    					campusId.add(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id);
    					dtoList.children = new ArrayList<ErpProgrammeLevelDTO>();
    					Set<Integer> levelId = new HashSet<Integer>();
    					for(HostelProgrammeDetailsDBO dboList2: dbo){
    						if(!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO) &&
								!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO) &&
								!levelId.contains(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id)){
    								ErpProgrammeLevelDTO level = new ErpProgrammeLevelDTO();
    							if(!Utils.isNullOrEmpty(dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpCampusDBO) &&
									dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id ==dboList2.erpCampusProgrammeMappingDBO.erpCampusDBO.id &&
									!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeLevelDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeLevelDBO.id) &&
									!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeLevelDBO.programmeLevel)){
    									level.value = dboList2.erpCampusProgrammeMappingDBO.id+"-"+dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id;
    									level.label = dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.programmeLevel;
    									dtoList.children.add(level);
    									levelId.add(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id);
    									level.children = new ArrayList<ErpProgrammeDTO>();
    									for(HostelProgrammeDetailsDBO dboList3: dbo){
    										ErpProgrammeDTO department = new ErpProgrammeDTO();
    										if(!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpCampusDBO) &&
												!Utils.isNullOrEmpty(dboList3.erpCampusProgrammeMappingDBO.erpCampusDBO) &&
												dboList2.erpCampusProgrammeMappingDBO.erpCampusDBO.id == dboList3.erpCampusProgrammeMappingDBO.erpCampusDBO.id &&
												!Utils.isNullOrEmpty(dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO) &&
												!Utils.isNullOrEmpty(dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO) &&
												dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id == dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id &&
												!Utils.isNullOrEmpty(dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id) &&
												!Utils.isNullOrEmpty(dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName)){
													String newId = dboList3.erpCampusProgrammeMappingDBO.id+"-"+dboLists.erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+dboList2.erpCampusProgrammeMappingDBO.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id+"-"+
													dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id;
    											if(level.children.size() > 0){
    												boolean isExistId = false;
    												for (ErpProgrammeDTO dboList4: level.children){
    													isExistId = dboList4.value.equals(newId);
													}
													if(isExistId){
														break;
													}else{
														department.value = newId;
														department.label = dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName;
														level.children.add(department);
														checked.add(newId);
													}
												}else{
													department.value = newId;
													department.label = dboList3.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName;
													level.children.add(department);
													checked.add(newId);
												}
											}
										}
								}
							}
						}
    					dtoList.checked = checked;
    					dto.add(dtoList);
					}
				}
			}
		}
	return dto;
	}
	
	public List<HostelProgrammeDetailsDTO> setCampusLevelProgrammeDBOToDTO1(List<ErpCampusProgrammeMappingDBO> dbo) {
    	List<HostelProgrammeDetailsDTO> dto = null;
    	if(!Utils.isNullOrEmpty(dbo)|| dbo.size() > 0){
    		dto = new ArrayList<HostelProgrammeDetailsDTO>();
    		Set<Integer> campusId= new HashSet<Integer>();
    		for(ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO:dbo){
				HostelProgrammeDetailsDTO dtoList = new HostelProgrammeDetailsDTO();
				ArrayList checked = new ArrayList();
    			if(!Utils.isNullOrEmpty(erpCampusProgrammeMappingDBO.erpCampusDBO) && !Utils.isNullOrEmpty( erpCampusProgrammeMappingDBO.erpCampusDBO.id) &&
					!Utils.isNullOrEmpty( erpCampusProgrammeMappingDBO.erpCampusDBO.campusName)){
    				dtoList.value =  erpCampusProgrammeMappingDBO.id+"-"+String.valueOf( erpCampusProgrammeMappingDBO.erpCampusDBO.id);
    				dtoList.label =  erpCampusProgrammeMappingDBO.erpCampusDBO.campusName;
    				if(!campusId.contains( erpCampusProgrammeMappingDBO.erpCampusDBO.id)){
    					campusId.add( erpCampusProgrammeMappingDBO.erpCampusDBO.id);
    					dtoList.children = new ArrayList<ErpProgrammeLevelDTO>();
    					Set<Integer> levelId = new HashSet<Integer>();
    					for(ErpCampusProgrammeMappingDBO dboList2: dbo){
    						if(!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO) &&
								!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO) &&
								!levelId.contains(dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id)){
    								ErpProgrammeLevelDTO level = new ErpProgrammeLevelDTO();
    							if(!Utils.isNullOrEmpty( erpCampusProgrammeMappingDBO.erpCampusDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpCampusDBO) &&
									 erpCampusProgrammeMappingDBO.erpCampusDBO.id ==dboList2.erpCampusDBO.id &&
									!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO.erpProgrammeLevelDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO) &&
									!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO.erpProgrammeLevelDBO.id) &&
									!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO.erpProgrammeLevelDBO.programmeLevel)){
    									level.value = dboList2.id+"-"+ erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id;
    									level.label = dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.programmeLevel;
    									dtoList.children.add(level);
    									levelId.add(dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id);
    									level.children = new ArrayList<ErpProgrammeDTO>();
    									for(ErpCampusProgrammeMappingDBO dboList3: dbo){
    										ErpProgrammeDTO department = new ErpProgrammeDTO();
    										if(!Utils.isNullOrEmpty(dboList2.erpCampusDBO) &&
												!Utils.isNullOrEmpty(dboList3.erpCampusDBO) &&
												dboList2.erpCampusDBO.id == dboList3.erpCampusDBO.id &&
												!Utils.isNullOrEmpty(dboList2.erpProgrammeDBO) &&
												!Utils.isNullOrEmpty(dboList3.erpProgrammeDBO) &&
												dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id == dboList3.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id &&
												!Utils.isNullOrEmpty(dboList3.erpProgrammeDBO.id) &&
												!Utils.isNullOrEmpty(dboList3.erpProgrammeDBO.programmeName)){
													String newId = dboList3.id+"-"+ erpCampusProgrammeMappingDBO.erpCampusDBO.id+"-"+dboList2.erpProgrammeDBO.erpProgrammeDegreeDBO.erpProgrammeLevelDBO.id+"-"+
													dboList3.erpProgrammeDBO.id;
    											if(level.children.size() > 0){
    												boolean isExistId = false;
    												for (ErpProgrammeDTO dboList4: level.children){
    													isExistId = dboList4.value.equals(newId);
													}
													if(isExistId){
														break;
													}else{
														department.value = newId;
														department.label = dboList3.erpProgrammeDBO.programmeName;
														level.children.add(department);
														//checked.add(newId);
													}
												}else{
													department.value = newId;
													department.label = dboList3.erpProgrammeDBO.programmeName;
													level.children.add(department);
													//checked.add(newId);
												}
											}
										}
								}
							}
						}
    					dtoList.checked = checked;
    					dto.add(dtoList);
					}
				}
			}
		}
	return dto;
	}

	public List<EmpCampusDeaneryDepartmentDTO> setTreeDepartmentCampusDBOsToDTOs(
		List<ErpCampusDepartmentMappingDBO> dboList) {
		List<EmpCampusDeaneryDepartmentDTO>  dtoList = new ArrayList<EmpCampusDeaneryDepartmentDTO>();
 		Set<Integer> deptIds = new HashSet<Integer>();
		for (ErpCampusDepartmentMappingDBO dbo : dboList) {
			 EmpCampusDeaneryDepartmentDTO dto = new EmpCampusDeaneryDepartmentDTO(); 
			 if(!Utils.isNullOrEmpty(dbo.erpDepartmentDBO) && !deptIds.contains(dbo.erpDepartmentDBO.id)) { 
				 deptIds.add(dbo.erpDepartmentDBO.id);
				 dto.value =dbo.id+"-"+String.valueOf(dbo.erpDepartmentDBO.id);		
				 dto.label = dbo.erpDepartmentDBO.departmentName;
				 dto.children = new ArrayList <EmpCampusDeaneryDTO>();	
				 for (ErpCampusDepartmentMappingDBO dbo2 : dboList) {
					 if(!Utils.isNullOrEmpty(dbo2.erpDepartmentDBO) && (dbo.erpDepartmentDBO.id == dbo2.erpDepartmentDBO.id)) {
						 EmpCampusDeaneryDTO campus = new EmpCampusDeaneryDTO();	
						 if(!Utils.isNullOrEmpty(dbo2.erpCampusDBO)) {
							 campus.value = dbo2.id+"-"+dbo2.erpCampusDBO.id+"-"+dbo2.erpDepartmentDBO.id;						 
							 campus.label = dbo2.erpCampusDBO.campusName;
							 dto.children.add(campus);
						 }
					 }
				 }
				 dtoList.add(dto);
			 }
		}
		return dtoList;
	}
}
