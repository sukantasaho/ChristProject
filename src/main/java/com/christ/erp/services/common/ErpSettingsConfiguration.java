package com.christ.erp.services.common;

import com.christ.erp.services.dbobjects.common.ErpSettingsDBO;
import com.christ.erp.services.transactions.common.ErpSettingsTransaction;
import java.util.List;

public class ErpSettingsConfiguration {
   ErpSettingsTransaction erpSettingsTransaction = ErpSettingsTransaction.getInstance();

   public String ERP_PUBLISH_JOB_ADVERTISEMENT="";
   public String ERP_EMPLOYEE_DOCUMENT_UPLOAD_PATH="";

   public void setProperties(int campusId) throws Exception {
       List<ErpSettingsDBO> erpSettingsDataList = erpSettingsTransaction.getErpSettingsData();
       if(!Utils.isNullOrEmpty(erpSettingsDataList)) {
          erpSettingsDataList.forEach(erpSettingsDBO -> {
             if(!Utils.isNullOrEmpty(erpSettingsDBO.propertyName)) {
                if(erpSettingsDBO.propertyName.trim().equals("ERP_PUBLISH_JOB_ADVERTISEMENT")) {
                   ERP_PUBLISH_JOB_ADVERTISEMENT = erpSettingsDBO.propertyName.trim();
                }
                else if(erpSettingsDBO.propertyName.trim().equals("ERP_EMPLOYEE_DOCUMENT_UPLOAD_PATH")) {
                   ERP_EMPLOYEE_DOCUMENT_UPLOAD_PATH = erpSettingsDBO.propertyName.trim();
                }
             }
          });
       }
   }
}
