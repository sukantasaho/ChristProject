package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmApplnNumberGenDetailsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmApplnNumberGenerationDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dto.admission.settings.AdmApplnNumberGenDetailsDTO;
import com.christ.erp.services.dto.admission.settings.AdmApplnNumbergeneratonDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.admission.settings.ApplicationNumberTransaction;

@Service
public class ApplicationNumberHandler {
	
//	private static volatile ApplicationNumberHandler applicationNumberHandler = null;
//	ApplicationNumberTransaction applicationNumberTransaction = ApplicationNumberTransaction.getInstance();
//
//    public static ApplicationNumberHandler getInstance() {
//        if(applicationNumberHandler==null) {
//        	applicationNumberHandler = new ApplicationNumberHandler();
//        }
//        return applicationNumberHandler;
//    }
    @Autowired
    ApplicationNumberTransaction applicationNumberTransaction;
    
    public List<AdmApplnNumbergeneratonDTO> getGridData(String yearId) {
    	List<AdmApplnNumbergeneratonDTO> admApplnNumbergeneratonDTO = new ArrayList<>();
    	List<AdmApplnNumberGenerationDBO> list;
		try {
			list = applicationNumberTransaction.getGridData(yearId);
			for(AdmApplnNumberGenerationDBO dbo : list) {
				AdmApplnNumbergeneratonDTO gridDTO = new AdmApplnNumbergeneratonDTO();
				gridDTO.onlineAppNoFrom = dbo.onlineApplnNoFrom+"-"+dbo.onlineApplnNoTo;
				gridDTO.id = dbo.id.toString();
				gridDTO.offlineAppNoFrom = dbo.offlineApplnNoFrom+"-"+dbo.offlineApplnNoTo;
				gridDTO.year = dbo.academicYearDBO.academicYearName;
				gridDTO.onlinePrefix = dbo.onlineApplnNoPrefix;
				gridDTO.offlinePrefix = dbo.offlineApplnNoPrefix;
				gridDTO.setOfflineApplnCurrentNo(dbo.getOfflineApplnCurrentNo());
				gridDTO.setOnlineApplnCurrentNo(dbo.getOnlineApplnCurrentNo());
				Set<AdmApplnNumberGenDetailsDBO> details = dbo.admApplnNumberGenDetailsDBOSet;
				for(AdmApplnNumberGenDetailsDBO admApplnNumberGenDetailsDBO:details) {
					if(admApplnNumberGenDetailsDBO.recordStatus=='A'){
						if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO)&&
								!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
							if(Utils.isNullOrEmpty(gridDTO.programmes)) {
								gridDTO.programmes = admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+" ("+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.locationName+")";
							}
							else {
								gridDTO.programmes += ", "+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+" ("+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.locationName+")";
							}
						}
						if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO)
								&& !Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
							if(Utils.isNullOrEmpty(gridDTO.programmes)) {
								gridDTO.programmes = admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+" ("+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName+")";
							}
							else {
								gridDTO.programmes +=", "+ admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+" ("+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName+")";
							}
						}
					}
				}
				admApplnNumbergeneratonDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return admApplnNumbergeneratonDTO;
    }
	  
    @SuppressWarnings({ "unchecked", "rawtypes" })
  	public ApiResult<ModelBaseDTO> saveOrUpdate(AdmApplnNumbergeneratonDTO data, String userId) throws Exception {
      	ApiResult<ModelBaseDTO> result = new ApiResult();
      	Boolean isDuplicate = false;
      	Boolean isProgramExists = false;
      	String programs=null;
      	if (!Utils.isNullOrEmpty(data)) {
      		AdmApplnNumberGenerationDBO admApplnNumberGenerationDBO = null;
      		Boolean isRangeExists = applicationNumberTransaction.isRangeExists(data);
      		for(ProgramPreferenceDTO program:data.selectedProgrammes) {
      			//Boolean programExists = applicationNumberTransaction.isProgramExists(data.id, program.programId, program.preferenceOption,program.preferenceId, data.academicYear.id);
      			Boolean programExists = applicationNumberTransaction.isProgramExists(data.id, program.campusMappingId , data.academicYear.getId().toString());
      			if(programExists) {
      				if(!Utils.isNullOrWhitespace(programs)) {
      					programs += ","+program.label;
      				}
      				else {
          				isProgramExists = true;
      					programs = program.label;
      				}
      			}
      		}
      		if(isProgramExists) {
  				result.success = false;
				result.dto = null;
				result.failureMessage = "Application number exists for "+programs;
				return result;
  			}
      		if(isRangeExists) {
      			result.success = false;
				result.dto = null;
				result.failureMessage = "Given application number range already exists";
				return result;
      		}
  		if(!Utils.isNullOrEmpty(data.id)) {
  			admApplnNumberGenerationDBO = applicationNumberTransaction.getAdmApplnNumberGenerationDBO(Integer.parseInt(data.id));
  		}
  		if(admApplnNumberGenerationDBO == null) {
  			admApplnNumberGenerationDBO = new AdmApplnNumberGenerationDBO();
  			admApplnNumberGenerationDBO.createdUsersId = Integer.parseInt(userId);
  		}
  		if(!isDuplicate) {
  			admApplnNumberGenerationDBO.academicYearDBO = new ErpAcademicYearDBO();
  			admApplnNumberGenerationDBO.academicYearDBO.id = data.academicYear.getId();
  			if(!Utils.isNullOrWhitespace(data.onlinePrefix)) {
  	  			admApplnNumberGenerationDBO.onlineApplnNoPrefix = data.onlinePrefix.trim();
  			}
  			else {
  	  			admApplnNumberGenerationDBO.onlineApplnNoPrefix = null;
  			}
  			if(!Utils.isNullOrWhitespace(data.offlinePrefix)) {
  	  			admApplnNumberGenerationDBO.offlineApplnNoPrefix = data.offlinePrefix.trim();
  			}
  			else {
  	  			admApplnNumberGenerationDBO.offlineApplnNoPrefix = null;
  			}
  			admApplnNumberGenerationDBO.offlineApplnNoFrom = Integer.parseInt(data.offlineAppNoFrom);
  			admApplnNumberGenerationDBO.onlineApplnNoFrom = Integer.parseInt(data.onlineAppNoFrom);
  			admApplnNumberGenerationDBO.offlineApplnNoTo = Integer.parseInt(data.offlineAppNoTo);
  			admApplnNumberGenerationDBO.onlineApplnNoTo = Integer.parseInt(data.onlineAppNoTo);
  			admApplnNumberGenerationDBO.recordStatus = 'A';
  			if(admApplnNumberGenerationDBO.id == null) {
  				//applicationNumberTransaction.saveOrUpdate(admApplnNumberGenerationDBO);
  				Set<AdmApplnNumberGenDetailsDBO> admApplnNumberGenDetailsDBOSet = new HashSet<>();
  				for (ProgramPreferenceDTO preferenceDTO : data.selectedProgrammes) {
  					AdmApplnNumberGenDetailsDBO detailsDBO = new AdmApplnNumberGenDetailsDBO();
  					detailsDBO.admApplnNumberGenerationDBO = admApplnNumberGenerationDBO;
  					if(!Utils.isNullOrEmpty(preferenceDTO.campusMappingId)) {
  						ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
  						erpCampusProgrammeMappingDBO.id = Integer.parseInt(preferenceDTO.campusMappingId);
  						detailsDBO.erpCampusProgrammeMappingDBO = erpCampusProgrammeMappingDBO;
  					}
  					detailsDBO.createdUsersId = Integer.parseInt(userId);
  					detailsDBO.recordStatus = 'A';
  					admApplnNumberGenDetailsDBOSet.add(detailsDBO);
  				}
  				if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBOSet))
  				admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet = admApplnNumberGenDetailsDBOSet;
  			} else {
  				List<Integer> isUpdated = null;
  				if(!Utils.isNullOrEmpty(data.selectedProgrammes)) {
  					isUpdated = new ArrayList<Integer>();
  					boolean save = false;
  	  				for (AdmApplnNumberGenDetailsDBO updatedbo : admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet) {
  	  					if(!Utils.isNullOrEmpty(updatedbo.recordStatus) && updatedbo.recordStatus == 'A') {
	  	  					save = false;
	   						for (ProgramPreferenceDTO  preferenceDTO : data.selectedProgrammes) {
	   							if(!Utils.isNullOrEmpty(updatedbo.erpCampusProgrammeMappingDBO) && !Utils.isNullOrEmpty(preferenceDTO.campusMappingId) &&
	   								updatedbo.erpCampusProgrammeMappingDBO.id == Integer.parseInt(preferenceDTO.campusMappingId)) {
	   								isUpdated.add(Integer.parseInt(preferenceDTO.campusMappingId));
	   								save = true;
	   								updatedbo.recordStatus = 'A';
	   								updatedbo.modifiedUsersId = Integer.parseInt(userId);
	   							}
	   						}
	   						if(!save) {
	   							if(!Utils.isNullOrEmpty(updatedbo.erpCampusProgrammeMappingDBO)) {
		   							isUpdated.add(updatedbo.erpCampusProgrammeMappingDBO.id);
		   							updatedbo.modifiedUsersId = Integer.parseInt(userId);
		   							updatedbo.recordStatus = 'D';
	   							}
	   							admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet.add(updatedbo);
	   						}
  	  					}
  					}
  	  				if(Utils.isNullOrEmpty(admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet)) {
  	  				admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet = new HashSet<AdmApplnNumberGenDetailsDBO>();
  	  				}
  	  			    for (ProgramPreferenceDTO  preferenceDTO : data.selectedProgrammes) {
						if(!Utils.isNullOrEmpty(preferenceDTO.campusMappingId) && !isUpdated.contains(Integer.parseInt(preferenceDTO.campusMappingId))) {
							AdmApplnNumberGenDetailsDBO detailsDBO = new AdmApplnNumberGenDetailsDBO();
							detailsDBO.admApplnNumberGenerationDBO = admApplnNumberGenerationDBO;
							detailsDBO.createdUsersId = Integer.parseInt(userId);
							detailsDBO.recordStatus = 'A';
							if(!Utils.isNullOrEmpty(preferenceDTO.campusMappingId)) {
								ErpCampusProgrammeMappingDBO  erpCampusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
								erpCampusProgrammeMappingDBO.id = Integer.parseInt(preferenceDTO.campusMappingId);
								detailsDBO.erpCampusProgrammeMappingDBO = erpCampusProgrammeMappingDBO;
							}
							admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet.add(detailsDBO);
						}
					}
  				}	
  			}
  			if(applicationNumberTransaction.saveOrUpdate(admApplnNumberGenerationDBO)) 
  				result.success = true;
  			else
  				result.success = false;
  			}
  		}
      	return result;
      }
    
    public AdmApplnNumbergeneratonDTO edit(String id) {
    	AdmApplnNumbergeneratonDTO admApplnNumbergeneratonDTO = new AdmApplnNumbergeneratonDTO();
		try {
			AdmApplnNumberGenerationDBO admApplnNumberGenerationDBO = applicationNumberTransaction.getAdmApplnNumberGenerationDBO(Integer.parseInt(id));
			if(admApplnNumberGenerationDBO != null) {
				admApplnNumbergeneratonDTO.id = admApplnNumberGenerationDBO.id.toString();
				admApplnNumbergeneratonDTO.academicYear = new ErpAcademicYearDTO();
				admApplnNumbergeneratonDTO.academicYear.setId(admApplnNumberGenerationDBO.getAcademicYearDBO().getId());
				admApplnNumbergeneratonDTO.academicYear.setAcademicYearName(admApplnNumberGenerationDBO.getAcademicYearDBO().getAcademicYearName());
				admApplnNumbergeneratonDTO.academicYear.setAcademicYear(admApplnNumberGenerationDBO.getAcademicYearDBO().getAcademicYear());
				admApplnNumbergeneratonDTO.onlinePrefix = admApplnNumberGenerationDBO.onlineApplnNoPrefix;
				admApplnNumbergeneratonDTO.offlinePrefix = admApplnNumberGenerationDBO.offlineApplnNoPrefix;
				admApplnNumbergeneratonDTO.onlineAppNoFrom = admApplnNumberGenerationDBO.onlineApplnNoFrom.toString();
				admApplnNumbergeneratonDTO.offlineAppNoFrom = admApplnNumberGenerationDBO.offlineApplnNoFrom.toString();
				admApplnNumbergeneratonDTO.onlinePrefix = admApplnNumberGenerationDBO.onlineApplnNoPrefix;
				admApplnNumbergeneratonDTO.onlineAppNoTo = admApplnNumberGenerationDBO.onlineApplnNoTo.toString();
				admApplnNumbergeneratonDTO.offlineAppNoTo = admApplnNumberGenerationDBO.offlineApplnNoTo.toString();
				admApplnNumbergeneratonDTO.setOfflineApplnCurrentNo(admApplnNumberGenerationDBO.getOfflineApplnCurrentNo());
				admApplnNumbergeneratonDTO.setOnlineApplnCurrentNo(admApplnNumberGenerationDBO.getOnlineApplnCurrentNo());
  				Set<AdmApplnNumberGenDetailsDBO> admApplnNumberGenDetailsDBOList = admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet;
  				admApplnNumbergeneratonDTO.selectedProgrammes = new ArrayList<ProgramPreferenceDTO>();
  				for(AdmApplnNumberGenDetailsDBO admApplnNumberGenDetailsDBO:admApplnNumberGenDetailsDBOList) {
  					if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.recordStatus) && admApplnNumberGenDetailsDBO.recordStatus == 'A') {
  						AdmApplnNumberGenDetailsDTO admApplnNumberGenDetailsDTO = new AdmApplnNumberGenDetailsDTO();
  	  					admApplnNumberGenDetailsDTO.id = admApplnNumberGenDetailsDBO.id.toString();
  	  					admApplnNumberGenDetailsDTO.program = new LookupItemDTO();
  	  				    ProgramPreferenceDTO programPreferenceDTO = new ProgramPreferenceDTO();
  	  					if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO)) {
  	  						if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO) && !Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO)) {
  	  	  	  					admApplnNumberGenDetailsDTO.campusId = admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.id.toString();
  	  	  	  			        programPreferenceDTO.label = admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+"( "+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName+" )";
  	  	  	  			        programPreferenceDTO.campusMappingId = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.id);
  	  	  	  			        programPreferenceDTO.preferenceOption = 'C';
  	  	  	  			        programPreferenceDTO.id = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.id);
  	  						}
  	  	  					if(!Utils.isNullOrEmpty(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO)) {
  	  	  	  					admApplnNumberGenDetailsDTO.locationid = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.id);
  	  	  	  					programPreferenceDTO.label = admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeNameForApplication+" ("+admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpLocationDBO.locationName+")";
  	  	  	  					programPreferenceDTO.campusMappingId = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.id);
  	  	  	  					programPreferenceDTO.preferenceOption = 'L';
  	  	  	  					programPreferenceDTO.id = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.id);
  	  	  					}
  	  	  	  				admApplnNumberGenDetailsDTO.program.value = String.valueOf(admApplnNumberGenDetailsDBO.erpCampusProgrammeMappingDBO.erpProgrammeDBO.id);
  	  					}
  	  					admApplnNumbergeneratonDTO.selectedProgrammes.add(programPreferenceDTO);
  					}
  				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return admApplnNumbergeneratonDTO;
    }
    
    public boolean delete(String  id,String userId) {
    	try {
			AdmApplnNumberGenerationDBO admApplnNumberGenerationDBO = applicationNumberTransaction.getAdmApplnNumberGenerationDBO(Integer.parseInt(id));
	    	if(admApplnNumberGenerationDBO != null) {
	    		admApplnNumberGenerationDBO.recordStatus = 'D';
	    		admApplnNumberGenerationDBO.modifiedUsersId = Integer.parseInt(userId);
  				Set<AdmApplnNumberGenDetailsDBO> admApplnNumberGenDetailsDBOList = admApplnNumberGenerationDBO.admApplnNumberGenDetailsDBOSet;
				for (AdmApplnNumberGenDetailsDBO item : admApplnNumberGenDetailsDBOList) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
				}
				if(admApplnNumberGenerationDBO.id != null) {
					return applicationNumberTransaction.saveOrUpdate(admApplnNumberGenerationDBO);
				}
			}
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
