package com.christ.erp.services.handlers.account.settings;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCurrencyConversionRateDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dto.common.CurrencyConversionRateDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.CurrencyConversionRateTransaction;

public class CurrencyConversionRateHandler {
	
    private static volatile CurrencyConversionRateHandler  currencyConversionRateHandler=null;
	
    CurrencyConversionRateTransaction currencyConversionRateTransaction=CurrencyConversionRateTransaction.getInstance();
	
	public static CurrencyConversionRateHandler getInstance() {
        if(currencyConversionRateHandler==null) {
        	currencyConversionRateHandler = new CurrencyConversionRateHandler();
        }
        return currencyConversionRateHandler;
    }

	public Boolean delete(String iD, String userId) throws Exception {
		ErpCurrencyConversionRateDBO obj=currencyConversionRateTransaction.edit(iD);
		obj.recordStatus='D';
		obj.modifiedUsersId=Integer.parseInt(userId);
		return currencyConversionRateTransaction.delete(obj);
	}

	public List<CurrencyConversionRateDTO> getGridData() throws Exception {
		List<CurrencyConversionRateDTO> listmenus=new ArrayList<>();
		List<ErpCurrencyConversionRateDBO> list=currencyConversionRateTransaction.getGridData();
		for(ErpCurrencyConversionRateDBO obj:list) {
			CurrencyConversionRateDTO menu=new CurrencyConversionRateDTO();
			menu.id=String.valueOf(obj.id);
			menu.currency=new ExModelBaseDTO();
			menu.currency.id=String.valueOf(obj.erpCurrencyDBO.id);
			menu.currency.text=obj.erpCurrencyDBO.currencyCode;
		    LocalDateTime localDateTime = LocalDateTime.parse((obj.exchangeTime).toString());
		    menu.dateandtime=Utils.convertLocalDateTimeToStringDateTime(localDateTime);
      		menu.conversionrate=obj.exchangeRate.toString();
			listmenus.add(menu);
		}
		return listmenus;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(CurrencyConversionRateDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		LocalDateTime date1 = Utils.convertStringDateTimeToLocalDateTime(data.dateandtime);		
		date1=date1.truncatedTo(ChronoUnit.MINUTES);
	    String strDate= Utils.convertLocalDateTimeToStringDateTime(date1);
		if(currencyConversionRateTransaction.duplicateCheck(data,data.currency.id,date1)) {
			result.failureMessage = "Duplicate record exist With Currency : "+ data.currency.text +" and Date & Time: "+ strDate;
		}else {
			ErpCurrencyConversionRateDBO header=null;
		    if(Utils.isNullOrWhitespace(data.id) == true) {
		    	header=new ErpCurrencyConversionRateDBO();
		    	header.createdUsersId=Integer.parseInt(userId);
		    }else {
		    	header = currencyConversionRateTransaction.edit(data.id);
		    	header.modifiedUsersId=Integer.parseInt(userId);
		    }
		    header.erpCurrencyDBO=new ErpCurrencyDBO();
		    header.erpCurrencyDBO.id=Integer.parseInt(data.currency.id);
		    Double double1=Double.valueOf(data.conversionrate);
		    header.exchangeRate=BigDecimal.valueOf(double1.doubleValue());
		    header.exchangeTime=date1;
		    header.recordStatus='A';
		    result.success=currencyConversionRateTransaction.saveOrUpdate(header);
		}
	    return result;
	}

	public CurrencyConversionRateDTO edit(String id) throws Exception {
		CurrencyConversionRateDTO currencyConversionRateDTO=new CurrencyConversionRateDTO();
		ErpCurrencyConversionRateDBO obj=currencyConversionRateTransaction.edit(id);
		currencyConversionRateDTO.id=String.valueOf(obj.id);
		currencyConversionRateDTO.currency=new ExModelBaseDTO();
		currencyConversionRateDTO.currency.id=String.valueOf(obj.erpCurrencyDBO.id);
		currencyConversionRateDTO.currency.text=obj.erpCurrencyDBO.currencyCode;	
		currencyConversionRateDTO.dateandtime=obj.exchangeTime.toString();
		currencyConversionRateDTO.conversionrate=obj.exchangeRate.toString();
		return currencyConversionRateDTO;
	}
}
