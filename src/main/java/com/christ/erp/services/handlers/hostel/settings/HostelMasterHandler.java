package com.christ.erp.services.handlers.hostel.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelImagesDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeLevelDTO;
import com.christ.erp.services.dto.employee.common.HostelProgrammeDetailsDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelImagesDTO;
import com.christ.erp.services.helpers.employee.common.CommonEmployeeHelper;
import com.christ.erp.services.transactions.hostel.settings.HostelMasterTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostelMasterHandler {

	private static volatile HostelMasterHandler hostelMasterHandler = null;
	static HostelMasterTransaction hostelMasterTransaction = HostelMasterTransaction.getInstance();

	public static HostelMasterHandler getInstance() {
		if(hostelMasterHandler == null){
			hostelMasterHandler = new HostelMasterHandler();
		}
		return hostelMasterHandler;
	}

	public static HostelDTO edit(String id)throws Exception {
		HostelDBO dbo =  hostelMasterTransaction.edit(id);
		HostelDTO hostelMaster = null;
		if(!Utils.isNullOrEmpty(dbo)){
			hostelMaster = new HostelDTO();
			hostelMaster.id = String.valueOf(dbo.id);
			hostelMaster.hostelName = dbo.hostelName;
			hostelMaster.addressLineOne = !Utils.isNullOrEmpty(dbo.addressLineOne) ? dbo.addressLineOne :"";
			hostelMaster.addressLineTwo = !Utils.isNullOrEmpty(dbo.addressLineTwo)?dbo.addressLineTwo:"";
			hostelMaster.phoneNumberOne = !Utils.isNullOrEmpty(dbo.phoneNumberOne) ? dbo.phoneNumberOne : "";
			hostelMaster.phoneNumberTwo = !Utils.isNullOrEmpty(dbo.phoneNumberTwo) ? dbo.phoneNumberTwo : "";
			hostelMaster.email = !Utils.isNullOrEmpty(dbo.email) ? dbo.email : "";
			hostelMaster.faxNo = !Utils.isNullOrEmpty(dbo.faxNo) ? dbo.faxNo : "";
			if(!Utils.isNullOrEmpty(dbo.getErpPincode())){
//				hostelMaster.pincode = !Utils.isNullOrEmpty(dbo.erpPincodeDBO.pincode) ? dbo.erpPincodeDBO.pincode : "";
//				hostelMaster.pincodeId = !Utils.isNullOrEmpty(dbo.erpPincodeDBO.id) ? String.valueOf(dbo.erpPincodeDBO.id) : "";
				hostelMaster.pincode = dbo.getErpPincode().toString();
				hostelMaster.pincodeId = dbo.getErpPincode().toString();
			}
//			else{
//				hostelMaster.pincode =  "";
//				hostelMaster.pincodeId = "";
//			}
			hostelMaster.onlineCancellationDays =!Utils.isNullOrEmpty(dbo.onlineCancellationDays) ? dbo.onlineCancellationDays : 0;
			hostelMaster.country = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.erpCountryDBO)){
				hostelMaster.country.label = !Utils.isNullOrEmpty(dbo.erpCountryDBO.countryName) ? dbo.erpCountryDBO.countryName : "";
				hostelMaster.country.value = !Utils.isNullOrEmpty(dbo.erpCountryDBO.id) ? String.valueOf(dbo.erpCountryDBO.id) : "";
			}else{
				hostelMaster.country.label =  "";
				hostelMaster.country.value =  "";
			}
			hostelMaster.state = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.erpStateDBO)){
				hostelMaster.state.label = !Utils.isNullOrEmpty(dbo.erpStateDBO.stateName) ? dbo.erpStateDBO.stateName : "";
				hostelMaster.state.value = !Utils.isNullOrEmpty(dbo.erpStateDBO.id) ? String.valueOf(dbo.erpStateDBO.id) : "";
			}else{
				hostelMaster.state.label =  "";
				hostelMaster.state.value =  "";
			}
			hostelMaster.city = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.erpCityDBO)){
				hostelMaster.city.label = !Utils.isNullOrEmpty(dbo.erpCityDBO.cityName) ? dbo.erpCityDBO.cityName : "";
				hostelMaster.city.value = !Utils.isNullOrEmpty(dbo.erpCityDBO.id) ? String.valueOf(dbo.erpCityDBO.id) :"";
			}else{
				hostelMaster.city.label =  "";
				hostelMaster.city.value =  "";
			}
			hostelMaster.forGender = new LookupItemDTO();
			if(!Utils.isNullOrEmpty(dbo.erpGenderDBO)){
				hostelMaster.forGender.label = !Utils.isNullOrEmpty(dbo.erpGenderDBO.genderName) ? dbo.erpGenderDBO.genderName : "";
				hostelMaster.forGender.value = !Utils.isNullOrEmpty(dbo.erpGenderDBO.erpGenderId) ? String.valueOf(dbo.erpGenderDBO.erpGenderId) : "";
			}else{
				hostelMaster.forGender.label =  "";
				hostelMaster.forGender.value =  "";
			}
			hostelMaster.setCampus(new SelectDTO());
			if(!Utils.isNullOrEmpty(dbo.getErpCampusDBO())) {
				hostelMaster.getCampus().setValue(dbo.getErpCampusDBO().getId().toString());
				hostelMaster.getCampus().setLabel(dbo.getErpCampusDBO().getCampusName());
			} else {
				hostelMaster.getCampus().setValue("");
				hostelMaster.getCampus().setLabel("");
			} 
			if(!Utils.isNullOrEmpty(dbo.getHostelInformation())) {
				hostelMaster.setHostelInformation(dbo.getHostelInformation());
			}
			hostelMaster.hostelImagesDTO = new ArrayList<>();
			try {
				for(HostelImagesDBO item:dbo.getHostelImagesDBOSet()) {
					if(item.recordStatus == 'A') {
						HostelImagesDTO hostelImagesDTO = new HostelImagesDTO();
						hostelImagesDTO.setId(item.getId()); 
						File file = new File(item.getHostelImageUrl());
						if(file.exists() && !file.isDirectory()) { 
							hostelImagesDTO.setExtension(item.getHostelImageUrl().substring(item.getHostelImageUrl().lastIndexOf(".")+1));
							String fileName = new File(item.getHostelImageUrl()).getName();
							hostelImagesDTO.setUrl(item.getHostelImageUrl());
							hostelImagesDTO.setFileName(fileName.replaceFirst("[.][^.]+$", ""));
							hostelImagesDTO.setRecordStatus(item.getRecordStatus());
							hostelMaster.hostelImagesDTO.add(hostelImagesDTO);
						}
					}
				}
			} catch (Exception error) {
				Utils.log(error.getMessage());
			}
			List<HostelProgrammeDetailsDBO> list = hostelMasterTransaction.getCampusLevelProgrammeByHostelId(String.valueOf(dbo.id));
			List<String> checkedList = new ArrayList<>();
			if(list != null && list.size() > 0) {
				CommonEmployeeHelper commonEmployeeHelper = CommonEmployeeHelper.getInstance();
				List<HostelProgrammeDetailsDTO> dtoList = commonEmployeeHelper.setCampusLevelProgrammeDBOToDTO(list);
				try {
					if (!Utils.isNullOrEmpty(dtoList)) {
						for (HostelProgrammeDetailsDTO item : dtoList) {
							for (ErpProgrammeLevelDTO item2 : item.children) {
								for (ErpProgrammeDTO item3 : item2.children) {
									checkedList.add(item3.value);
								}
							}
						}
					}
				} catch (Exception error) {
					throw error;
				}
			}else{
				checkedList = new ArrayList<>();
			}
			hostelMaster.checked = checkedList;
		}
		return hostelMaster;
	}

	public List<HostelDTO> getGridData() throws Exception {
		List<HostelDBO> list = hostelMasterTransaction.getGridData();
		List<HostelDTO> gridList = null;
		if(!Utils.isNullOrEmpty(list)){
			gridList = new ArrayList<>();
			for(HostelDBO dbo : list){
				HostelDTO hostelMaster = new HostelDTO();
				hostelMaster.id = String.valueOf(dbo.id);
				hostelMaster.hostelName = dbo.hostelName;
				hostelMaster.addressLineOne = !Utils.isNullOrEmpty(dbo.addressLineOne) ? dbo.addressLineOne :"";
				hostelMaster.addressLineTwo = !Utils.isNullOrEmpty(dbo.addressLineTwo)?dbo.addressLineTwo:"";
				hostelMaster.phoneNumberOne = !Utils.isNullOrEmpty(dbo.phoneNumberOne) ? dbo.phoneNumberOne : "";
				hostelMaster.phoneNumberTwo = !Utils.isNullOrEmpty(dbo.phoneNumberTwo) ? dbo.phoneNumberTwo : "";
				hostelMaster.email = !Utils.isNullOrEmpty(dbo.email) ? dbo.email : "";
				hostelMaster.faxNo = !Utils.isNullOrEmpty(dbo.faxNo) ? dbo.faxNo : "";
				if(!Utils.isNullOrEmpty(dbo.getErpPincode())){
					hostelMaster.pincode = dbo.getErpPincode().toString();
					hostelMaster.pincodeId = dbo.getErpPincode().toString();;
				}
				hostelMaster.onlineCancellationDays =!Utils.isNullOrEmpty(dbo.onlineCancellationDays) ? dbo.onlineCancellationDays :0;
				hostelMaster.country = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(dbo.erpCountryDBO)){
					hostelMaster.country.label = !Utils.isNullOrEmpty(dbo.erpCountryDBO.countryName) ? dbo.erpCountryDBO.countryName : "";
					hostelMaster.country.value = !Utils.isNullOrEmpty(dbo.erpCountryDBO.id) ? String.valueOf(dbo.erpCountryDBO.id) : "";
				}
				hostelMaster.state = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(dbo.erpStateDBO)){
					hostelMaster.state.label = !Utils.isNullOrEmpty(dbo.erpStateDBO.stateName) ? dbo.erpStateDBO.stateName : "";
					hostelMaster.state.value = !Utils.isNullOrEmpty(dbo.erpStateDBO.id) ? String.valueOf(dbo.erpStateDBO.id) : "";
				}
				hostelMaster.city = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(dbo.erpCityDBO)){
					hostelMaster.city.label = !Utils.isNullOrEmpty(dbo.erpCityDBO.cityName) ? dbo.erpCityDBO.cityName : "";
					hostelMaster.city.value = !Utils.isNullOrEmpty(dbo.erpCityDBO.id) ? String.valueOf(dbo.erpCityDBO.id) :"";
				}
				hostelMaster.forGender = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(dbo.erpGenderDBO)){
					hostelMaster.forGender.label = !Utils.isNullOrEmpty(dbo.erpGenderDBO.genderName) ? dbo.erpGenderDBO.genderName : "";
					hostelMaster.forGender.value = !Utils.isNullOrEmpty(dbo.erpGenderDBO.erpGenderId) ? String.valueOf(dbo.erpGenderDBO.erpGenderId) : "";
				}
				hostelMaster.setCampus(new SelectDTO());
				if(!Utils.isNullOrEmpty(dbo.erpCampusDBO)) {
					hostelMaster.getCampus().setValue(dbo.getErpCampusDBO().getId().toString());
					hostelMaster.getCampus().setLabel(dbo.getErpCampusDBO().getCampusName());
				}
				gridList.add(hostelMaster);
			}
		}
		return gridList;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(HostelDTO data, String userId) throws Exception{
		ApiResult<ModelBaseDTO> results = new ApiResult<>();
		HostelDBO dbo;
		boolean isValid = true;
		List<HostelDBO> list = null;
		List<HostelDBO> allData = hostelMasterTransaction.getGridData();
		for(HostelDBO checkList: allData){
			if(data.hostelName.toLowerCase().equals(checkList.hostelName.toLowerCase())){
				if(Utils.isNullOrEmpty(data.id)){
					results.failureMessage = "Duplicate entry for Hostel Name: "+data.hostelName;
					isValid = false;
					break;
				}else if(Integer.parseInt(data.id) != checkList.id){
					results.failureMessage = "Duplicate entry for Hostel Name: "+data.hostelName;
					isValid = false;
					break;
				}
			}
		}
		if(isValid) {
			if (data.id != null) {
				list = hostelMasterTransaction.getDuplicate(data);
			}
			if (Utils.isNullOrEmpty(list) || list.size() == 0) {
				dbo = new HostelDBO();
				dbo.createdUsersId = Integer.parseInt(userId);
			} else {
				dbo = hostelMasterTransaction.edit(data.id);
				dbo.createdUsersId = Integer.parseInt(userId);
			}
			dbo.hostelName = data.hostelName;
			dbo.faxNo = data.faxNo;
			dbo.email = data.email;
			dbo.phoneNumberTwo = data.phoneNumberTwo;
			dbo.phoneNumberOne = data.phoneNumberOne;
			dbo.onlineCancellationDays = data.onlineCancellationDays;
			dbo.addressLineOne = data.addressLineOne;
			dbo.addressLineTwo = data.addressLineTwo;
			dbo.recordStatus = 'A';
			Set<HostelProgrammeDetailsDBO> hostelProgrammeDetailsDBOS = new HashSet<>();
			Set<HostelProgrammeDetailsDBO> deleteSet = null;
			/*List<HostelProgrammeDetailsDBO> dboList = null;
            if (!Utils.isNullOrEmpty(data.checked)) {
                dboList = new ArrayList<HostelProgrammeDetailsDBO>();
            }*/
			if (dbo.hostelProgrammeDetailsDBO != null)
				deleteSet = dbo.hostelProgrammeDetailsDBO;
			if (!Utils.isNullOrEmpty(data.checked))
				for (String item : data.checked) {
					String mapppingId = item.split("-")[0];
					String campusId = item.split("-")[1];
					String programmeId = item.split("-")[3];
					HostelProgrammeDetailsDBO detail = null;
					if (deleteSet != null) {
						for (HostelProgrammeDetailsDBO bo : deleteSet) {
							if (!Utils.isNullOrWhitespace(mapppingId)) {
								if (Integer.parseInt(mapppingId) == bo.erpCampusProgrammeMappingDBO.id) {
									detail = bo;
									deleteSet.remove(bo);
									break;
								}
							} else if (Utils.isNullOrWhitespace(mapppingId)) {
								deleteSet.add(bo);
								break;
							}
						}
					}
					if (detail == null) {
						detail = new HostelProgrammeDetailsDBO();
						detail.createdUsersId = Integer.parseInt(userId);
						detail.recordStatus = 'A';
					} else {
						detail.modifiedUsersId = Integer.parseInt(userId);
					}
					ErpCampusProgrammeMappingDBO campusProgrammeMappingDBO = new ErpCampusProgrammeMappingDBO();
					campusProgrammeMappingDBO.id = Integer.parseInt(mapppingId);
					ErpProgrammeDBO erpProgrammeDBO = new ErpProgrammeDBO();
					erpProgrammeDBO.id = Integer.parseInt(programmeId);
					campusProgrammeMappingDBO.erpProgrammeDBO = erpProgrammeDBO;
					ErpCampusDBO erpCampusDBO = new ErpCampusDBO();
					erpCampusDBO.id = Integer.parseInt(campusId);
					campusProgrammeMappingDBO.erpCampusDBO = erpCampusDBO;
					detail.erpCampusProgrammeMappingDBO = campusProgrammeMappingDBO;
					detail.hostelDBO = dbo;
					detail.recordStatus = 'A';
					detail.modifiedUsersId = Integer.parseInt(userId);
					hostelProgrammeDetailsDBOS.add(detail);
				}
			if (deleteSet != null) {
				for (HostelProgrammeDetailsDBO bo : deleteSet) {
					bo.recordStatus = 'D';
					bo.modifiedUsersId = Integer.parseInt(userId);
					hostelProgrammeDetailsDBOS.add(bo);
				}
			}
			dbo.hostelProgrammeDetailsDBO = hostelProgrammeDetailsDBOS;
			if (!Utils.isNullOrEmpty(data.country)) {
				ErpCountryDBO country = new ErpCountryDBO();
				if(!Utils.isNullOrEmpty(data.country.value))
					country.id = Integer.parseInt(data.country.value);
				dbo.erpCountryDBO = country;
			}
			if (!Utils.isNullOrEmpty(data.state)) {
				ErpStateDBO state = new ErpStateDBO();
				if(!Utils.isNullOrEmpty(data.state.value))
					state.id = Integer.parseInt(data.state.value);
				dbo.erpStateDBO = state;
			}
			if (!Utils.isNullOrEmpty(data.city)) {
				ErpCityDBO city = new ErpCityDBO();
				if(!Utils.isNullOrEmpty(data.city.value))
					city.id = Integer.parseInt(data.city.value);
				dbo.erpCityDBO = city;
			}
			if (!Utils.isNullOrEmpty(data.pincode)) {
//				ErpPincodeDBO pincode = new ErpPincodeDBO();
//				if(!Utils.isNullOrEmpty(data.pincodeId))
//					pincode.id = Integer.parseInt(data.pincodeId);
//				dbo.erpPincodeDBO = pincode;
				dbo.setErpPincode(Integer.parseInt(data.pincode));
			}
			if (!Utils.isNullOrEmpty(data.forGender)) {
				ErpGenderDBO gender = new ErpGenderDBO();
				if(!Utils.isNullOrEmpty(data.forGender.value))
					gender.erpGenderId = Integer.parseInt(data.forGender.value);
				dbo.erpGenderDBO = gender;
			}
			if(!Utils.isNullOrEmpty(data.getCampus())) {
				ErpCampusDBO campus = new ErpCampusDBO();
				if(!Utils.isNullOrEmpty(data.getCampus().getValue()))
					campus.setId(Integer.parseInt(data.getCampus().getValue()));
				dbo.erpCampusDBO = campus;
			}
			if(!Utils.isNullOrEmpty(data.getHostelInformation())) {
				dbo.setHostelInformation(data.getHostelInformation());
			}

			Set<HostelImagesDBO> existDBOSet = dbo.getHostelImagesDBOSet();
			Map<Integer, HostelImagesDBO> map = new HashMap<Integer, HostelImagesDBO>();
			if(!Utils.isNullOrEmpty(existDBOSet)) {
				existDBOSet.forEach(dbos-> {
					if (dbos.getRecordStatus()=='A') {
						map.put(dbos.getId(), dbos);
					}
				});
			}
			Set<HostelImagesDBO> hostelImages = new HashSet<HostelImagesDBO>();
			if(!Utils.isNullOrEmpty(data.getHostelImagesDTO())) {
				data.getHostelImagesDTO().forEach(subdtos -> {
					HostelImagesDBO dbos = null;
					if(!Utils.isNullOrEmpty(subdtos.getId()) && map.containsKey(subdtos.getId())) {
						dbos = map.get(subdtos.getId());
						dbos.setModifiedUsersId(Integer.parseInt(userId));
						map.remove(subdtos.getId());
					} else {
						dbos = new HostelImagesDBO();
						dbos.setCreatedUsersId(Integer.parseInt(userId));
					}
				});
				HostelDBO dbo1 = dbo;
				if(!Utils.isNullOrEmpty(data.getHostelImagesDTO())) {
					data.getHostelImagesDTO().forEach(subdtos -> {
						HostelImagesDBO detailsDBO = new HostelImagesDBO();
						detailsDBO.setHostelDBO(dbo1);
						detailsDBO.setId(subdtos.getId());
						detailsDBO.setHostelImageUrl(subdtos.getUrl());
						File file = new File("ImageUpload//"+subdtos.getFileName()+"."+subdtos.getExtension());
						detailsDBO.setHostelImageUrl(file.getAbsolutePath());
						detailsDBO.setCreatedUsersId(Integer.parseInt(userId));
						if(!Utils.isNullOrEmpty(subdtos.getId()))
							detailsDBO.setModifiedUsersId(Integer.parseInt(userId));
						detailsDBO.setRecordStatus('A');
						hostelImages.add(detailsDBO);    
					});
					dbo.setHostelImagesDBOSet(hostelImages);
				}
			}
			if(!Utils.isNullOrEmpty(map)) {
				map.forEach((entry, value)-> {
					value.setModifiedUsersId(Integer.parseInt(userId));
					value.setRecordStatus('D');
					hostelImages.add(value);
				});
			}
				hostelMasterTransaction.saveOrUpdate(dbo);
				if (dbo.id != 0) {
					results.success = true;
					results.dto = new ModelBaseDTO();
					results.dto.id = String.valueOf(dbo.id);
				}
			}
			return results;
		}

		public boolean delete(String hostelId) throws Exception {
			return hostelMasterTransaction.delete(hostelId);
		}
	}
