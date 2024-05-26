package com.christ.erp.services.handlers.account.settings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccGSTPercentageDBO;
import com.christ.erp.services.dto.account.GSTPercentageDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.GSTPercentagesTransaction;

public class GSTPercentagesHandler {
	
	private static volatile GSTPercentagesHandler  gstPercentagesHandler=null;
	
	GSTPercentagesTransaction gstPercentagesTransaction=GSTPercentagesTransaction.getInstance();
	
	public static GSTPercentagesHandler getInstance() {
        if(gstPercentagesHandler==null) {
        	gstPercentagesHandler = new GSTPercentagesHandler();
        }
        return gstPercentagesHandler;
    }

	public List<GSTPercentageDTO> getGridData() throws Exception {
		List<GSTPercentageDTO> listgstPercentageDTO=new ArrayList<>();
		List<AccGSTPercentageDBO> listgstPercentages=gstPercentagesTransaction.getGridData();
		for(AccGSTPercentageDBO obj:listgstPercentages) {
			GSTPercentageDTO gstPercentageDTO=new GSTPercentageDTO();
			gstPercentageDTO.id=String.valueOf(obj.id);
			gstPercentageDTO.cgst=obj.CGSTPercentage.toString();
			gstPercentageDTO.igst=obj.IGSTPercentage.toString();
			gstPercentageDTO.sgst=obj.SGSTPercentage.toString();
			gstPercentageDTO.currentlyApplicable=obj.isCurrent.toString();
			gstPercentageDTO.dateApplicablefrom=Utils.convertLocalDateToStringDate(obj.applicableFromDate);
			listgstPercentageDTO.add(gstPercentageDTO);
		}
		return listgstPercentageDTO;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(GSTPercentageDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		LocalDateTime localDateTime = Utils.convertStringDateTimeToLocalDateTime(data.dateApplicablefrom);
		if(gstPercentagesTransaction.duplicateCheck(data,localDateTime)) {
			result.failureMessage="Duplicate Record Exist With Date Application Form: " + Utils.convertLocalDateTimeToStringDate(localDateTime);
		} else {
			AccGSTPercentageDBO header=null;
		    if(Utils.isNullOrWhitespace(data.id) == true) {
		    	header=new AccGSTPercentageDBO();
		    	header.createdUsersId=Integer.parseInt(userId);
		    }else {
		    	header = gstPercentagesTransaction.edit(data.id);
		    	header.modifiedUsersId=Integer.parseInt(userId);
		    }
		    header.applicableFromDate=Utils.convertStringDateTimeToLocalDate(data.dateApplicablefrom);
		    Double doublecgst=Double.valueOf(data.cgst);
		    header.CGSTPercentage=BigDecimal.valueOf(doublecgst.doubleValue());
		    Double doubleigst=Double.valueOf(data.igst);
		    header.IGSTPercentage=BigDecimal.valueOf(doubleigst.doubleValue());
		    Double doublesgst=Double.valueOf(data.sgst);
		    header.SGSTPercentage=BigDecimal.valueOf(doublesgst.doubleValue());
		    header.isCurrent=Boolean.valueOf(data.currentlyApplicable);
		    header.recordStatus='A';
		    result.success=gstPercentagesTransaction.saveOrUpdate(header);	
		}
		return result;
	}

	public GSTPercentageDTO edit(String id) throws Exception {
		GSTPercentageDTO gstPercentageDTO=new GSTPercentageDTO();
		AccGSTPercentageDBO obj=gstPercentagesTransaction.edit(id);
		gstPercentageDTO.id=String.valueOf(obj.id);
		gstPercentageDTO.cgst=obj.CGSTPercentage.toString();
		gstPercentageDTO.igst=obj.IGSTPercentage.toString();
		gstPercentageDTO.sgst=obj.SGSTPercentage.toString();
		gstPercentageDTO.currentlyApplicable=obj.isCurrent.toString();
		gstPercentageDTO.dateApplicablefrom=Utils.convertLocalDateToStringDate(obj.applicableFromDate);
		return gstPercentageDTO;
	}

	public boolean delete(String id, String userId) throws Exception {
		AccGSTPercentageDBO obj=gstPercentagesTransaction.edit(id);
		obj.recordStatus='D';
		obj.modifiedUsersId=Integer.parseInt(userId);
		return gstPercentagesTransaction.delete(obj);
	}
}
