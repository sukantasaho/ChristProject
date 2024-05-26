package com.christ.erp.services.handlers.account.settings;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.settings.AccSlipBookDBO;
import com.christ.erp.services.dto.account.settings.AccSlipBookDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.SlipBookTransaction;

public class SlipBookHandler {
	private static volatile SlipBookHandler slipBookHandler = null;
	SlipBookTransaction slipBookTransaction = SlipBookTransaction.getInstance();

	public static SlipBookHandler getInstance() {
		if(slipBookHandler == null) {
			slipBookHandler = new SlipBookHandler();
		}
		return slipBookHandler;
	}

	public List<AccSlipBookDTO> getGridData() throws Exception {
		List<Tuple> list = slipBookTransaction.getGridData();
		List<AccSlipBookDTO> gridList = null;
		if(!Utils.isNullOrEmpty(list)){
			gridList = new ArrayList<>();
			for (Tuple tuple : list){
				AccSlipBookDTO accSlipBookInfo = new AccSlipBookDTO();
				accSlipBookInfo.id = tuple.get("accslipbookid").toString();
				accSlipBookInfo.slipBookType = new ExModelBaseDTO();
				accSlipBookInfo.slipBookType.text = accSlipBookInfo.slipBookType.id = tuple.get("slipbooktype").toString();
				accSlipBookInfo.slipBookNo = tuple.get("slipbookNo").toString();
				gridList.add(accSlipBookInfo);
			}
		}
		return gridList;
	}

	public boolean saveOrUpdate(AccSlipBookDTO data, String userId) throws Exception {
		AccSlipBookDBO dbo = null;
		if(Utils.isNullOrWhitespace(data.id) == true){
			dbo = new AccSlipBookDBO();
		} else {
			dbo = slipBookTransaction.getaccSlipBookData(data.id);
		}
		dbo.slipBookType = data.slipBookType.text;
		if(!Utils.isNullOrEmpty(data.bookNoPrefix)){
			dbo.bookNoPrefix = data.bookNoPrefix;
		}else {
			dbo.bookNoPrefix = null;
		}		
		dbo.slipBookNo = data.slipBookNo;
		dbo.noPrefix = data.noPrefix;
		dbo.startSlipNo = data.startSlipNo;
		dbo.endSlipNo = data.endSlipNo;
		if(!Utils.isNullOrEmpty(data.issuedDate)){
			dbo.issuedDate = Utils.convertStringDateTimeToLocalDate(data.issuedDate);
 		}else {
			dbo.issuedDate = null;
		}
		if(!Utils.isNullOrEmpty(data.issuedBy)){
			dbo.issuedBy = data.issuedBy;
		}else {
			dbo.issuedBy = null;
		}
		if(!Utils.isNullOrEmpty(data.issuedTo)){
			dbo.issuedTo = data.issuedTo;
		}else {
			dbo.issuedTo = null;
		}
		dbo.recordStatus = 'A';
		Boolean isTrueOrFalse = null;
		if(dbo.id == null){
			dbo.createdUsersId = Integer.parseInt(userId);
			dbo.recordStatus = 'A';
			isTrueOrFalse = slipBookTransaction.saveOrUpdate(dbo);
		} else {
			dbo.modifiedUsersId = Integer.parseInt(userId);
			isTrueOrFalse = slipBookTransaction.saveOrUpdate(dbo);
		}
		return isTrueOrFalse;
	}

	public AccSlipBookDTO getaccSlipBookData(String id) throws Exception {
		AccSlipBookDTO slipBookInfo = new AccSlipBookDTO();
		AccSlipBookDBO slipBookDataList = slipBookTransaction.getaccSlipBookData(id);
		if(!Utils.isNullOrEmpty(slipBookDataList)){
			slipBookInfo.id = String.valueOf(slipBookDataList.id);
			slipBookInfo.slipBookType = new ExModelBaseDTO();
			slipBookInfo.slipBookType.text = slipBookInfo.slipBookType.id = slipBookDataList.slipBookType;
			if(!Utils.isNullOrEmpty(slipBookDataList.bookNoPrefix)){
				slipBookInfo.bookNoPrefix = slipBookDataList.bookNoPrefix;
			}
			slipBookInfo.slipBookNo = slipBookDataList.slipBookNo;
			slipBookInfo.noPrefix = slipBookDataList.noPrefix;
			slipBookInfo.startSlipNo = slipBookDataList.startSlipNo;
			slipBookInfo.endSlipNo = slipBookDataList.endSlipNo;
			if(!Utils.isNullOrEmpty(slipBookDataList.issuedDate)){
				slipBookInfo.issuedDate = slipBookDataList.issuedDate.toString();
			}
			if(!Utils.isNullOrEmpty(slipBookDataList.issuedBy)){
				slipBookInfo.issuedBy = slipBookDataList.issuedBy;
			}
			if(!Utils.isNullOrEmpty(slipBookDataList.issuedTo)){
				slipBookInfo.issuedTo = slipBookDataList.issuedTo;
			}
		}
		return slipBookInfo;
	}

	public boolean delete(AccSlipBookDTO data, String userId) throws Exception{
		return slipBookTransaction.delete(data, userId);
	}

	public boolean duplicateAccSlipBook(AccSlipBookDTO data) throws Exception{
		return slipBookTransaction.duplicateAccSlipBook(data);
	}
}
