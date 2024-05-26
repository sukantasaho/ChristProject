package com.christ.erp.services.handlers.account.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccAccountsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dto.account.settings.AccAccountsDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LogoImageDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.AccountEntryTransaction;

public class AccountEntryHandler {

	private static volatile AccountEntryHandler  accountEntryHandler=null;
	public static AccountEntryHandler getInstance() {
        if(accountEntryHandler==null) {
        	accountEntryHandler = new AccountEntryHandler();
        }
        return accountEntryHandler;
    }
	
	AccountEntryTransaction accountEntryTransaction = AccountEntryTransaction.getInstance();
	
	public List<AccAccountsDTO> getGridData() throws Exception {
		List<AccAccountsDTO> list = null;
		List<AccAccountsDBO> accountEntryDBOsList = accountEntryTransaction.getGridData();
		if(!Utils.isNullOrEmpty(accountEntryDBOsList)) {
			list = new ArrayList<AccAccountsDTO>();
			for(AccAccountsDBO obj:accountEntryDBOsList) {
				AccAccountsDTO dto=new AccAccountsDTO();
				dto.id=String.valueOf(obj.id);
				if(!Utils.isNullOrEmpty(obj.erpCampusDBO) && !Utils.isNullOrEmpty(obj.erpCampusDBO.id)) {
					ExModelBaseDTO exModelBaseDTO=new ExModelBaseDTO();
					exModelBaseDTO.id = String.valueOf(obj.erpCampusDBO.id);
					exModelBaseDTO.text = obj.erpCampusDBO.campusName;
					dto.campus = exModelBaseDTO;
				}
				dto.accountNo=!Utils.isNullOrEmpty(obj.accountNo)?String.valueOf(obj.accountNo):"";
				dto.accountName=!Utils.isNullOrEmpty(obj.accountName)?String.valueOf(obj.accountName):"";
				list.add(dto);
			}
		}
		return list;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(AccAccountsDTO data, String userId) throws NumberFormatException, Exception {
		AccAccountsDBO dbo = null;
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(Utils.isNullOrWhitespace(data.id) == false) {
			dbo =  accountEntryTransaction.editAccountEntry(Integer.parseInt(data.id));
			dbo.modifiedUsersId = Integer.parseInt(userId);
		}
		if(!Utils.isNullOrEmpty(data.accountNo)) {
			Boolean isDuplicate=accountEntryTransaction.isDuplicate(data.accountNo,data.id);
			if(!isDuplicate) {
				if(dbo == null) {
					dbo = new AccAccountsDBO();
					dbo.createdUsersId = Integer.parseInt(userId);
				}
				if(!Utils.isNullOrEmpty(data.accountName)) 
					dbo.accountName = data.accountName;
					dbo.accountNo = data.accountNo;
				if(!Utils.isNullOrEmpty(data.campus) && !Utils.isNullOrEmpty(data.campus.id)) {
					ErpCampusDBO  campus = new ErpCampusDBO();
					campus.id = Integer.parseInt(data.campus.id);
					dbo.erpCampusDBO = campus;
				}
				if(!Utils.isNullOrEmpty(data.universityAccount)) 
					dbo.isUniversityAccount = Boolean.parseBoolean(data.universityAccount);
				if(!Utils.isNullOrEmpty(data.sibAccountCode)) 
					dbo.accountCode = data.sibAccountCode;
				if(!Utils.isNullOrEmpty(data.printName))
					dbo.printName = data.printName;
				if(!Utils.isNullOrEmpty(data.logoImages)) {
					for (LogoImageDTO item : data.logoImages) {
						//dbo.logoFileName=item.fileName+"."+item.extension;
						File file = new File("ImageUpload//"+item.fileName+"."+item.extension);
						dbo.logoFileName =file.getAbsolutePath();
					}
				}
				if(!Utils.isNullOrEmpty(data.swiftCode))
					dbo.swiftCode = data.swiftCode;
				if(!Utils.isNullOrEmpty(data.verifiedBy)) 
					dbo.verifiedBy = data.verifiedBy;
				if(!Utils.isNullOrEmpty(data.description1)) 
					dbo.description1 = data.description1;
				if(!Utils.isNullOrEmpty(data.description2)) 
					dbo.description2 = data.description2;
				if(!Utils.isNullOrEmpty(data.bankInformation)) 
					dbo.bankInfo = data.bankInformation;
				if(!Utils.isNullOrEmpty(data.code)) 
					dbo.accountCode = data.code;
				if(!Utils.isNullOrEmpty(data.displayname)) 
					dbo.NEFTDisplayName = data.displayname;
				if(!Utils.isNullOrEmpty(data.ifscCode)) 
					dbo.IFSCCode = data.ifscCode;
				if(!Utils.isNullOrEmpty(data.codeSibPayment)) 
					dbo.NEFTCodeForSibPayment = data.codeSibPayment;
				if(!Utils.isNullOrEmpty(data.refundFromAccount)) 
					dbo.refundFromAccount = data.refundFromAccount;
				if(!Utils.isNullOrEmpty(data.senderName)) 
					dbo.senderName = data.senderName;
				if(!Utils.isNullOrEmpty(data.senderEmail)) 
					dbo.senderEmail = data.senderEmail;
				if(!Utils.isNullOrEmpty(data.fileCode)) 
					dbo.refundFileCode = data.fileCode;
				if(!Utils.isNullOrEmpty(data.address1)) 
					dbo.refundAddress1 = data.address1;
				if(!Utils.isNullOrEmpty(data.address2)) 
					dbo.refundAddress2 = data.address2;
				if(!Utils.isNullOrEmpty(data.address3)) 
					dbo.refundAddress3 = data.address3;
				if(!Utils.isNullOrEmpty(data.address4)) 
					dbo.refundAddress4 = data.address4;
				dbo.recordStatus = 'A';
				accountEntryTransaction.saveOrUpdate(dbo);
			}else {
				deleteFile(data.logoImages);
				result.failureMessage="Duplicate record exists for Account Number : "+data.accountNo;
				result.success=false;
			}
		}
		return result;
	}

	private void deleteFile(List<LogoImageDTO> logoImages) {
		if(!Utils.isNullOrEmpty(logoImages) && logoImages.size()>0) {
			for (LogoImageDTO logoImageDTO : logoImages) {
				File file = new File("ImageUpload//"+logoImageDTO.fileName+"."+logoImageDTO.extension);
				file = new File(file.getAbsolutePath());
				if(file.exists()) { 
					file.delete();
				}
			}
		}
	}

	public boolean delete(String id, String userId) throws Exception {
		return accountEntryTransaction.delete(id, userId);
	}

	public AccAccountsDTO edit(String id) throws NumberFormatException, Exception {
		AccAccountsDBO dbo = null;
		AccAccountsDTO dto = null;
		if(!Utils.isNullOrEmpty(id)) {
			dbo = accountEntryTransaction.edit(Integer.parseInt(id.trim()));
		}
		if(!Utils.isNullOrEmpty(dbo)) {
			dto = new AccAccountsDTO();
			if(!Utils.isNullOrEmpty(dbo.id))
				dto.id = String.valueOf(dbo.id);
			if(!Utils.isNullOrEmpty(dbo.erpCampusDBO)) {
				ExModelBaseDTO baseDTO = new ExModelBaseDTO();
				baseDTO.id = String.valueOf(dbo.erpCampusDBO.id);
				dto.campus = baseDTO;
			}
			if(!Utils.isNullOrEmpty(dbo.accountName)) 
				dto.accountName = dbo.accountName;
			if(!Utils.isNullOrEmpty(dbo.accountNo)) 
				dto.accountNo = dbo.accountNo;
			if(!Utils.isNullOrEmpty(dbo.isUniversityAccount)) 
				dto.universityAccount = String.valueOf(dbo.isUniversityAccount);
			if(!Utils.isNullOrEmpty(dbo.accountCode)) 
				dto.sibAccountCode = dbo.accountCode;
			if(!Utils.isNullOrEmpty(dbo.swiftCode))
				dto.swiftCode = dbo.swiftCode;
			if(!Utils.isNullOrEmpty(dbo.printName))
				dto.printName = dbo.printName;
			if(!Utils.isNullOrEmpty(dbo.bankInfo))
				dto.bankInformation = dbo.bankInfo;
			if(!Utils.isNullOrEmpty(dbo.verifiedBy)) 
				dto.verifiedBy = dbo.verifiedBy;
			if(!Utils.isNullOrEmpty(dbo.description1)) 
				dto.description1 = dbo.description1;
			if(!Utils.isNullOrEmpty(dbo.description2)) 
				dto.description2 = dbo.description2;
			if(!Utils.isNullOrEmpty(dbo.accountCode)) 
				dto.code = dbo.accountCode;
			if(!Utils.isNullOrEmpty(dbo.NEFTDisplayName)) 
				dto.displayname = dbo.NEFTDisplayName;
			if(!Utils.isNullOrEmpty(dbo.IFSCCode)) 
				dto.ifscCode = dbo.IFSCCode;
			if(!Utils.isNullOrEmpty(dbo.NEFTCodeForSibPayment)) 
				dto.codeSibPayment = dbo.NEFTCodeForSibPayment;
			if(!Utils.isNullOrEmpty(dbo.refundFromAccount)) 
				dto.refundFromAccount = dbo.refundFromAccount;
			if(!Utils.isNullOrEmpty(dbo.senderName)) 
				dto.senderName = dbo.senderName;
			if(!Utils.isNullOrEmpty(dbo.senderEmail)) 
				dto.senderEmail = dbo.senderEmail;
			if(!Utils.isNullOrEmpty(dbo.refundFileCode)) 
				dto.fileCode = dbo.refundFileCode;
			if(!Utils.isNullOrEmpty(dbo.refundAddress1)) 
				dto.address1 = dbo.refundAddress1;
			if(!Utils.isNullOrEmpty(dbo.refundAddress2)) 
				dto.address2 = dbo.refundAddress2;
			if(!Utils.isNullOrEmpty(dbo.refundAddress3)) 
				dto.address3 = dbo.refundAddress3;
			if(!Utils.isNullOrEmpty(dbo.refundAddress4)) 
				dto.address4 = dbo.refundAddress4;	
			if(!Utils.isNullOrEmpty(dbo.logoFileName)) {
				dto.logoImages = new ArrayList<LogoImageDTO>();
				LogoImageDTO logoDto = new LogoImageDTO();
				logoDto.extension = dbo.logoFileName.substring(dbo.logoFileName.lastIndexOf(".")+1);
				//logoDto.fileName = dbo.logoFileName.replaceFirst("[.][^.]+$", "");
				String fileName = new File( dbo.logoFileName).getName();
				logoDto.fileName = fileName.replaceFirst("[.][^.]+$", "");
				logoDto.url = "./Images/"+dbo.logoFileName;
				logoDto.newFile = false;
				logoDto.recordStatus = dbo.recordStatus;
				dto.logoImages.add(logoDto);
			}			
		}
		return dto;
	}
}
