package com.christ.erp.services.handlers.hostel.application;

import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelPublishApplicationDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelPublishApplicationDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.application.PublishApplicationTransaction;

public class PublishApplicationHandler {

    public static volatile PublishApplicationHandler publishApplicationHandler = null;
    PublishApplicationTransaction publishApplicationTransaction = PublishApplicationTransaction.getInstance();
    CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

    public static PublishApplicationHandler getInstance() {
        if(publishApplicationHandler==null) {
            publishApplicationHandler = new PublishApplicationHandler();
        }
        return publishApplicationHandler;
    }

    public List<HostelPublishApplicationDTO> getGridData() throws Exception {
        List<HostelPublishApplicationDTO> hostelPublishApplicationDTOS = new ArrayList<>();
        List<HostelPublishApplicationDBO> list;
        list = publishApplicationTransaction.getGridData();
        for(HostelPublishApplicationDBO dbo : list) {
            HostelPublishApplicationDTO gridDTO = new HostelPublishApplicationDTO();
            gridDTO.id = dbo.id.toString();
            gridDTO.academicyearId = String.valueOf(dbo.erpAcademicYearDBO.id);
            gridDTO.academicYearText = dbo.erpAcademicYearDBO.academicYearName;
            gridDTO.hostelId = String.valueOf(dbo.hostelDBO.id);
            gridDTO.hostelText = dbo.hostelDBO.hostelName;
            hostelPublishApplicationDTOS.add(gridDTO);
        }
        return hostelPublishApplicationDTOS;
    }

    public HostelPublishApplicationDTO edit(String id) throws Exception {
        HostelPublishApplicationDTO hostelPublishApplicationDTO = new HostelPublishApplicationDTO();
        HostelPublishApplicationDBO hostelPublishApplicationDBO = commonApiTransaction.find(HostelPublishApplicationDBO.class, Integer.parseInt(id));
        if(hostelPublishApplicationDBO != null) {
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.id))
                hostelPublishApplicationDTO.id = String.valueOf(hostelPublishApplicationDBO.id);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.erpAcademicYearDBO) && !Utils.isNullOrEmpty(hostelPublishApplicationDBO.erpAcademicYearDBO.id))
                hostelPublishApplicationDTO.academicyearId = String.valueOf(hostelPublishApplicationDBO.erpAcademicYearDBO.id);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.hostelDBO) && !Utils.isNullOrEmpty(hostelPublishApplicationDBO.hostelDBO.id))
                hostelPublishApplicationDTO.hostelId = String.valueOf(hostelPublishApplicationDBO.hostelDBO.id);
            hostelPublishApplicationDTO.isOpenForFirstYear = String.valueOf(hostelPublishApplicationDBO.isOpenForFirstYear);
            hostelPublishApplicationDTO.isStatusForFirstYear = String.valueOf(hostelPublishApplicationDBO.isStatusForFirstYear);
            hostelPublishApplicationDTO.isOpenForSubsequentYear = String.valueOf(hostelPublishApplicationDBO.isOpenForSubsequentYear);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.offlineApplicationStartNo))
                hostelPublishApplicationDTO.offlineApplicationPrefix = hostelPublishApplicationDBO.offlineApplicationPrefix;
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.offlineApplicationStartNo))
                hostelPublishApplicationDTO.offlineApplicationStartNo = String.valueOf(hostelPublishApplicationDBO.offlineApplicationStartNo);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.offlineApplicationEndNo))
                hostelPublishApplicationDTO.offlineApplicationStartNo = String.valueOf(hostelPublishApplicationDBO.offlineApplicationEndNo);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.offlineApplicationEndNo))
                hostelPublishApplicationDTO.offlineApplicationEndNo = String.valueOf(hostelPublishApplicationDBO.offlineApplicationEndNo);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.onlineApplicationPrefix))
                hostelPublishApplicationDTO.onlineApplicationPrefix = hostelPublishApplicationDBO.onlineApplicationPrefix;
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.onlineApplicationStartNo))
                hostelPublishApplicationDTO.onlineApplicationStartNo = String.valueOf(hostelPublishApplicationDBO.onlineApplicationStartNo);
            
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.onlineApplicationEndNo))
                hostelPublishApplicationDTO.onlineApplicationEndNo = String.valueOf(hostelPublishApplicationDBO.onlineApplicationEndNo);
            hostelPublishApplicationDTO.isStatusForSubsequentYear = String.valueOf(hostelPublishApplicationDBO.isStatusForSubsequentYear);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.instructionTemplateId) && !Utils.isNullOrEmpty(hostelPublishApplicationDBO.instructionTemplateId.id))
                hostelPublishApplicationDTO.instructionTemplateId = String.valueOf(hostelPublishApplicationDBO.instructionTemplateId.id);
            if(!Utils.isNullOrEmpty(hostelPublishApplicationDBO.declarationTemplateId) && !Utils.isNullOrEmpty(hostelPublishApplicationDBO.declarationTemplateId.id))
                hostelPublishApplicationDTO.declarationTemplateId = String.valueOf(hostelPublishApplicationDBO.declarationTemplateId.id);
        }
        return hostelPublishApplicationDTO;
    }

    public boolean delete(Integer id, String userId) throws Exception {
        return publishApplicationTransaction.delete(String.valueOf(id), Integer.valueOf(userId));
    }

    public ApiResult<ModelBaseDTO> saveOrUpdate(HostelPublishApplicationDTO data, String userId, ApiResult<ModelBaseDTO> results) throws Exception {
        HostelPublishApplicationDBO dbo;
        boolean isValid = true;
        List <HostelPublishApplicationDBO> allData = publishApplicationTransaction.getGridData();
        for (HostelPublishApplicationDBO checklist: allData){
            if(data.academicyearId.equals(String.valueOf(checklist.erpAcademicYearDBO.id)) && data.hostelId.equals(String.valueOf(checklist.hostelDBO.id))){
                if(Utils.isNullOrEmpty(data.id)){
                    results.failureMessage = "Duplicate entry for Academic Year: "+checklist.erpAcademicYearDBO.academicYearName + " and Hostel: "+checklist.hostelDBO.hostelName;
                    isValid = false;
                    break;
                }else if(Integer.parseInt(data.id) != checklist.id){
                    results.failureMessage = "Duplicate entry for Academic Year: "+checklist.erpAcademicYearDBO.academicYearName + " and Hostel: "+checklist.hostelDBO.hostelName;
                    isValid = false;
                    break;
                }
            }
        }
        if(isValid){
            if (Utils.isNullOrEmpty(data.id)) {
                dbo = new HostelPublishApplicationDBO();
                dbo.createdUsersId = Integer.valueOf(userId);
            }else{
                dbo = commonApiTransaction.find(HostelPublishApplicationDBO.class, Integer.parseInt(data.id));
                dbo.modifiedUsersId = Integer.valueOf(userId);
            }
            if(!Utils.isNullOrEmpty(data.hostelId)) {
                HostelDBO hostel = new HostelDBO();
                hostel.id = Integer.valueOf(data.hostelId);
                dbo.hostelDBO = hostel;
            }
            if(!Utils.isNullOrEmpty(data.academicyearId)) {
                ErpAcademicYearDBO academic = new ErpAcademicYearDBO();
                academic.id = Integer.valueOf(data.academicyearId);
                dbo.erpAcademicYearDBO = academic;
            }
            if(!Utils.isNullOrEmpty(data.instructionTemplateId)) {
                ErpTemplateDBO template = new ErpTemplateDBO();
                template.id = Integer.valueOf(data.instructionTemplateId);
                dbo.instructionTemplateId = template;
            }
            if(!Utils.isNullOrEmpty(data.declarationTemplateId)) {
                ErpTemplateDBO template2 = new ErpTemplateDBO();
                template2.id = Integer.valueOf(data.declarationTemplateId);
                dbo.declarationTemplateId = template2;
            }
            dbo.isOpenForFirstYear = data.isOpenForFirstYear.equalsIgnoreCase("true");
            dbo.isStatusForFirstYear = data.isStatusForFirstYear.equalsIgnoreCase("true");
            dbo.isOpenForSubsequentYear = data.isOpenForSubsequentYear.equalsIgnoreCase("true");
            dbo.isStatusForSubsequentYear = data.isStatusForSubsequentYear.equalsIgnoreCase("true");
            if (!Utils.isNullOrEmpty(data.offlineApplicationPrefix)){
                dbo.offlineApplicationPrefix = data.offlineApplicationPrefix;
            }
            if (!Utils.isNullOrEmpty(data.offlineApplicationStartNo)){
                dbo.offlineApplicationStartNo = Integer.valueOf(data.offlineApplicationStartNo);
            }
            if (!Utils.isNullOrEmpty(data.offlineApplicationEndNo)){
                dbo.offlineApplicationEndNo = Integer.valueOf(data.offlineApplicationEndNo);
            }
            if (!Utils.isNullOrEmpty(data.onlineApplicationPrefix)){
                dbo.onlineApplicationPrefix = data.onlineApplicationPrefix;
            }
            if (!Utils.isNullOrEmpty(data.onlineApplicationStartNo)){
                dbo.onlineApplicationStartNo = Integer.valueOf(data.onlineApplicationStartNo);
            }
            if (!Utils.isNullOrEmpty(data.onlineApplicationEndNo)){
                dbo.onlineApplicationEndNo = Integer.valueOf(data.onlineApplicationEndNo);
            }
            dbo.recordStatus = 'A';
            if(publishApplicationTransaction.saveOrUpdate(dbo)){
                results.success = true;
            }
        }
        return results;
    }

}
