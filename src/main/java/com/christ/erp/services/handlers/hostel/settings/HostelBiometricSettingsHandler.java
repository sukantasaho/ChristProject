package com.christ.erp.services.handlers.hostel.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBiometricSettingsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBiometricSettingsDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.settings.HostelBiometricSettingdTransaction;
import java.util.ArrayList;
import java.util.List;

public class HostelBiometricSettingsHandler {

    private static volatile HostelBiometricSettingsHandler hostelBiometricSettingsHandler = null;
    public static HostelBiometricSettingdTransaction hostelBiometricSettingdTransaction = HostelBiometricSettingdTransaction.getInstance();
    CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

    public static HostelBiometricSettingsHandler getInstance(){
        if(hostelBiometricSettingsHandler == null){
            hostelBiometricSettingsHandler = new HostelBiometricSettingsHandler();
        }
        return hostelBiometricSettingsHandler;
    }

    public List<HostelBiometricSettingsDTO> getGridData() throws Exception {
        List<HostelBiometricSettingsDBO> dboList = hostelBiometricSettingdTransaction.getGridData();
        List<HostelBiometricSettingsDTO> dto = null;
        if(!Utils.isNullOrEmpty(dboList)){
            dto = new ArrayList<>();
            for(HostelBiometricSettingsDBO dbo : dboList){
                HostelBiometricSettingsDTO biometric = new HostelBiometricSettingsDTO();
                biometric.id = String.valueOf(dbo.id);
                biometricDTOSetting(dbo, biometric);
                biometric.machineId = dbo.machineNum;
                biometric.machineIpAddress = dbo.machineIpAddress;
                biometric.machineName = dbo.machineName;
                dto.add(biometric);
            }
        }
        return dto;
    }

    private void biometricDTOSetting(HostelBiometricSettingsDBO dbo, HostelBiometricSettingsDTO biometric) {
        if(!Utils.isNullOrEmpty(dbo.hostelBlockUnitDBO)){
            if(!Utils.isNullOrEmpty(dbo.hostelBlockUnitDBO.id)){
                LookupItemDTO object = new LookupItemDTO();
                object.value = String.valueOf(dbo.hostelBlockUnitDBO.id);
                object.label = dbo.hostelBlockUnitDBO.hostelUnit;
                biometric.hostelBlockAndUnit = object;
                LookupItemDTO hostelObject = new LookupItemDTO();
                hostelObject.label = dbo.hostelBlockUnitDBO.hostelBlockDBO.hostelDBO.hostelName;
                hostelObject.value = String.valueOf(dbo.hostelBlockUnitDBO.hostelBlockDBO.hostelDBO.id);
                biometric.hostel = hostelObject;
            }
            if(!Utils.isNullOrEmpty(dbo.hostelBlockUnitDBO.hostelBlockDBO) && !Utils.isNullOrEmpty(dbo.hostelBlockUnitDBO.hostelBlockDBO.id)){
                LookupItemDTO object = new LookupItemDTO();
                object.value = String.valueOf(dbo.hostelBlockUnitDBO.hostelBlockDBO.id);
                object.label = dbo.hostelBlockUnitDBO.hostelBlockDBO.blockName;
                biometric.block = object;
            }
        }
    }

    public HostelBiometricSettingsDTO edit(String id)throws Exception {
        HostelBiometricSettingsDBO dbo = commonApiTransaction.find(HostelBiometricSettingsDBO.class, Integer.parseInt(id));
        HostelBiometricSettingsDTO dto = null;
        if(!Utils.isNullOrEmpty(dbo)){
            dto = new HostelBiometricSettingsDTO();
            dto.id = String.valueOf(dbo.id);
            dto.machineName = dbo.machineName;
            dto.machineIpAddress = dbo.machineIpAddress;
            dto.machineId = dbo.machineNum;
            biometricDTOSetting(dbo, dto);
        }
        return dto;
    }

    public boolean delete(String id) throws Exception {
        return hostelBiometricSettingdTransaction.delete(id);
    }

    public ApiResult<ModelBaseDTO> saveOrUpdate(HostelBiometricSettingsDTO data, String userId, ApiResult<ModelBaseDTO> results)throws Exception {
        HostelBiometricSettingsDBO dbo;
        boolean isValid = true;
        List <HostelBiometricSettingsDBO> allData = hostelBiometricSettingdTransaction.getGridData();
        for (HostelBiometricSettingsDBO checklist: allData){
            if(data.machineId.equals(checklist.machineNum)){
                if(Utils.isNullOrEmpty(data.id)){
                    results.failureMessage = "Duplicate entry for Machine Id: "+data.machineId;
                    isValid = false;
                    break;
                }else if(Integer.parseInt(data.id) != checklist.id){
                    results.failureMessage = "Duplicate entry for Machine Id: "+data.machineId;
                    isValid = false;
                    break;
                }
            }
        }
        if(isValid) {
            if (Utils.isNullOrEmpty(data.id)) {
                dbo = new HostelBiometricSettingsDBO();
            }else{
                 dbo = commonApiTransaction.find(HostelBiometricSettingsDBO.class, Integer.parseInt(data.id));
            }
            dbo.createdUsersId = Integer.parseInt(userId);
            dbo.machineNum = data.machineId;
            dbo.machineIpAddress = data.machineIpAddress;
            dbo.machineName = data.machineName;
            if(!Utils.isNullOrEmpty(data.hostelBlockAndUnit) && !Utils.isNullOrEmpty(data.hostelBlockAndUnit.value)) {
                HostelBlockUnitDBO unit = new HostelBlockUnitDBO();
                unit.id = Integer.valueOf(data.hostelBlockAndUnit.value);
                dbo.hostelBlockUnitDBO = unit;
            }
            dbo.recordStatus = 'A';
            if(hostelBiometricSettingdTransaction.saveOrUpdate(dbo)){
                results.success = true;
            }
        }
        return results;
    }
}
