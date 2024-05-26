package com.christ.erp.services.handlers.account.settings;

import java.util.ArrayList;
import java.util.List;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccBillReceiptDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dto.account.BillOrReceiptNoDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.BillOrReceiptNoTransaction;

public class BillOrReceiptNoHandler {
	
	private static volatile BillOrReceiptNoHandler  billOrReceiptNumberHandler=null;
		
	BillOrReceiptNoTransaction billOrReceiptNumberTransaction=BillOrReceiptNoTransaction.getInstance();
	
	public static BillOrReceiptNoHandler getInstance() {
        if(billOrReceiptNumberHandler==null) {
        	billOrReceiptNumberHandler = new BillOrReceiptNoHandler();
        }
        return billOrReceiptNumberHandler;
    }

	public List<BillOrReceiptNoDTO> getGridData() throws Exception {
		List<BillOrReceiptNoDTO> list=new ArrayList<>();
		List<AccBillReceiptDBO> listbillreceipt=billOrReceiptNumberTransaction.getGridData();
		for(AccBillReceiptDBO obj:listbillreceipt) {
			BillOrReceiptNoDTO billOrReceiptDTO=new BillOrReceiptNoDTO();
			billOrReceiptDTO.id=String.valueOf(obj.id);
			billOrReceiptDTO.finanicalyear=new ExModelBaseDTO();
			billOrReceiptDTO.finanicalyear.id=String.valueOf(obj.accFinancialYearDBO.id);
			billOrReceiptDTO.finanicalyear.text=String.valueOf(obj.accFinancialYearDBO.financialYear);
			billOrReceiptDTO.type=new ExModelBaseDTO();
			billOrReceiptDTO.type.id=String.valueOf(obj.type);
			billOrReceiptDTO.type.text=String.valueOf(obj.type);
			billOrReceiptDTO.noprefix=obj.billReceiptNoPrefix.toString();
			billOrReceiptDTO.startingNo=obj.billReceiptStartNo.toString();
			list.add(billOrReceiptDTO);
		}
		return list;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(BillOrReceiptNoDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(billOrReceiptNumberTransaction.duplicateCheck(data, userId)) {
			result.failureMessage="Duplicate record exist With Financial Year : "+ data.finanicalyear.text +" and Type : "+data.type.text;
		} else {
			AccBillReceiptDBO header=null;
		    if(Utils.isNullOrWhitespace(data.id) == true) {
		    	header=new AccBillReceiptDBO();
		    	header.createdUsersId=Integer.parseInt(userId);
		    }else {
		    	header = billOrReceiptNumberTransaction.edit(data.id);
		    	header.modifiedUsersId=Integer.parseInt(userId);
		    }
		    header.accFinancialYearDBO=new AccFinancialYearDBO();
		    header.accFinancialYearDBO.id=Integer.parseInt(data.finanicalyear.id);
		    header.type=data.type.text;
		    header.billReceiptNoPrefix=data.noprefix;
		    header.billReceiptStartNo=Integer.parseInt(data.startingNo);
		    header.recordStatus='A';
		    result.success=billOrReceiptNumberTransaction.saveOrUpdate(header);
		}
	    return result;
	}

	public BillOrReceiptNoDTO edit(String id) throws Exception {
		BillOrReceiptNoDTO billOrReceiptDTO=new BillOrReceiptNoDTO();
		AccBillReceiptDBO obj=billOrReceiptNumberTransaction.edit(id);
		billOrReceiptDTO.id=String.valueOf(obj.id);
		billOrReceiptDTO.finanicalyear=new ExModelBaseDTO();
		billOrReceiptDTO.finanicalyear.id=String.valueOf(obj.accFinancialYearDBO.id);
		billOrReceiptDTO.finanicalyear.text=String.valueOf(obj.accFinancialYearDBO.financialYear);
		billOrReceiptDTO.type=new ExModelBaseDTO();
		billOrReceiptDTO.type.id=String.valueOf(obj.type);
		billOrReceiptDTO.type.text=String.valueOf(obj.type);
		billOrReceiptDTO.noprefix=!Utils.isNullOrEmpty( obj.billReceiptNoPrefix) ?  obj.billReceiptNoPrefix.toString() : "" ;
		billOrReceiptDTO.startingNo=obj.billReceiptStartNo.toString();
		return billOrReceiptDTO;
	}

	public boolean delete(String id, String userId) throws Exception {
		AccBillReceiptDBO obj=billOrReceiptNumberTransaction.edit(id);
		obj.recordStatus='D';
		obj.modifiedUsersId=Integer.parseInt(userId);
		return billOrReceiptNumberTransaction.delete(obj);
	}
}
