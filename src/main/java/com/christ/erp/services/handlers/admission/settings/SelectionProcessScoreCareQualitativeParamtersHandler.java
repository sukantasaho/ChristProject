package com.christ.erp.services.handlers.admission.settings;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualitativeParamterOptionDBO;
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterDTO;
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterOptionDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.admission.SelectionProcessScoreCareQualitativeParamtersTransaction;

public class SelectionProcessScoreCareQualitativeParamtersHandler {

	private static volatile SelectionProcessScoreCareQualitativeParamtersHandler selectionProcessScoreCareQualitativeParamtersHandler = null;
	SelectionProcessScoreCareQualitativeParamtersTransaction selectionProcessScoreCareQualitativeParamtersTransaction = SelectionProcessScoreCareQualitativeParamtersTransaction.getInstance();

    public static SelectionProcessScoreCareQualitativeParamtersHandler getInstance() {
        if(selectionProcessScoreCareQualitativeParamtersHandler==null) {
        	selectionProcessScoreCareQualitativeParamtersHandler = new SelectionProcessScoreCareQualitativeParamtersHandler();
        }
        return selectionProcessScoreCareQualitativeParamtersHandler;
    }
	    
    public List<AdmQualitativeParamterDTO> getGridData() {
    	List<AdmQualitativeParamterDTO> AdmQualitativeParamterDTO = new ArrayList<>();
    	List<Tuple> list;
		try {
			list = selectionProcessScoreCareQualitativeParamtersTransaction.getGridData();
			for(Tuple tuple : list) {
				AdmQualitativeParamterDTO gridDTO = new AdmQualitativeParamterDTO();
				gridDTO.qualitativeParameterLabel = tuple.get("qualitativeParameterLabel").toString();
				gridDTO.id = tuple.get("ID").toString();
				gridDTO.fieldType = tuple.get("fieldType").toString();
				AdmQualitativeParamterDTO.add(gridDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return AdmQualitativeParamterDTO;
    }
	    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ApiResult<ModelBaseDTO> saveOrUpdate(AdmQualitativeParamterDTO data, String userId) throws Exception {
    	ApiResult<ModelBaseDTO> result = new ApiResult();
    	Boolean isDuplicate = false;
    	List<AdmQualitativeParamterDBO> admQualitativeParamterList = null;
    	if (!Utils.isNullOrEmpty(data)) {
	    	AdmQualitativeParamterDBO admQualitativeParamter = null;
			if(Utils.isNullOrWhitespace(data.id) == false) {
				admQualitativeParamter = selectionProcessScoreCareQualitativeParamtersTransaction.getAdmQualitativeParamter(Integer.parseInt(data.id));
			}
			data.qualitativeParameterLabel = data.qualitativeParameterLabel.trim();
			admQualitativeParamterList = selectionProcessScoreCareQualitativeParamtersTransaction.getAdmQualitativeParamterByLabel(data.qualitativeParameterLabel,data.id);
			if(admQualitativeParamterList != null && !admQualitativeParamterList.isEmpty()) {
				isDuplicate = true;
				result.success = false;
				result.dto = null;
				result.failureMessage = "Duplicate entry for the Qualitative Paramter Label: " + data.qualitativeParameterLabel;
			}
			if(admQualitativeParamter == null) {
				admQualitativeParamter = new AdmQualitativeParamterDBO();
				admQualitativeParamter.createdUsersId = Integer.parseInt(userId);
				
			}
			if(!isDuplicate) {
				admQualitativeParamter.qualitativeParameterLabel = data.qualitativeParameterLabel;
				admQualitativeParamter.fieldType = data.fieldType;
				admQualitativeParamter.recordStatus = 'A';
				if(admQualitativeParamter.id == null) {
					selectionProcessScoreCareQualitativeParamtersTransaction.saveOrUpdate(admQualitativeParamter);
				} else {
					admQualitativeParamter.modifiedUsersId = Integer.parseInt(userId);
					selectionProcessScoreCareQualitativeParamtersTransaction.saveOrUpdate(admQualitativeParamter);
				}
				if(admQualitativeParamter.id != 0) {
					if(data.options.size()==1 && data.options.get(0).option==null) {
						result.success = true;
					}
					else {
						for(AdmQualitativeParamterOptionDTO item : data.options) {
							AdmQualitativeParamterOptionDBO admQualitativeParamterOptions = null;
							if(Utils.isNullOrWhitespace(item.id) == false) {
								admQualitativeParamterOptions = selectionProcessScoreCareQualitativeParamtersTransaction.getAdmQualitativeParamterOptions(Integer.parseInt(item.id));      
							}
							if(admQualitativeParamterOptions == null) {
								admQualitativeParamterOptions = new AdmQualitativeParamterOptionDBO();
								admQualitativeParamterOptions.createdUsersId = Integer.parseInt(userId);
							}
							admQualitativeParamterOptions.optionName = item.option;
							admQualitativeParamterOptions.recordStatus = item.recordStatus;
							admQualitativeParamterOptions.admQualitativeParameter = admQualitativeParamter;
							if (admQualitativeParamterOptions.id == null) {
								admQualitativeParamterOptions.createdUsersId = Integer.parseInt(userId);
							} else {
								admQualitativeParamterOptions.modifiedUsersId = Integer.parseInt(userId);
							}
							selectionProcessScoreCareQualitativeParamtersTransaction.saveOrUpdate(admQualitativeParamterOptions);
							result.success = true;
						}
					}
				}
			}
		}
    	return result;
    }
    
    public AdmQualitativeParamterDTO selectAdmQualitativeParamter(String id) {
    	AdmQualitativeParamterDTO AdmQualitativeParamterDTO = new AdmQualitativeParamterDTO();
		try {
			AdmQualitativeParamterDBO admQualitativeParamter = selectionProcessScoreCareQualitativeParamtersTransaction.getAdmQualitativeParamter(Integer.parseInt(id));
			if(admQualitativeParamter != null) {
				AdmQualitativeParamterDTO.id = admQualitativeParamter.id.toString();
				AdmQualitativeParamterDTO.qualitativeParameterLabel = admQualitativeParamter.qualitativeParameterLabel;
				AdmQualitativeParamterDTO.fieldType = admQualitativeParamter.fieldType;
				AdmQualitativeParamterDTO.options = new ArrayList<>();
				if(admQualitativeParamter.admQualitativeParameterOptionSet != null && admQualitativeParamter.admQualitativeParameterOptionSet.size() > 0) {
					for(AdmQualitativeParamterOptionDBO item : admQualitativeParamter.admQualitativeParameterOptionSet) {
						if(item.recordStatus == 'A') {
							AdmQualitativeParamterOptionDTO admQualitativeParamterOptions = new AdmQualitativeParamterOptionDTO();
							admQualitativeParamterOptions.id = item.id.toString();
							admQualitativeParamterOptions.option = item.optionName;
							admQualitativeParamterOptions.recordStatus = item.recordStatus;
							AdmQualitativeParamterDTO.options.add(admQualitativeParamterOptions);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return AdmQualitativeParamterDTO;
    }
	    
    public boolean deleteAdmQualitativeParamter(String  id,String userId) {
    	try {
	    	AdmQualitativeParamterDBO admQualitativeParamter = selectionProcessScoreCareQualitativeParamtersTransaction.getAdmQualitativeParamter(Integer.parseInt(id));
	    	if(admQualitativeParamter != null) {
	    		admQualitativeParamter.recordStatus = 'D';
	    		admQualitativeParamter.modifiedUsersId = Integer.parseInt(userId);
				for (AdmQualitativeParamterOptionDBO item : admQualitativeParamter.admQualitativeParameterOptionSet) {
					item.recordStatus = 'D';
					item.modifiedUsersId = Integer.parseInt(userId);
					selectionProcessScoreCareQualitativeParamtersTransaction.saveOrUpdate(item);
				}
				if(admQualitativeParamter.id != null) {
					return selectionProcessScoreCareQualitativeParamtersTransaction.saveOrUpdate(admQualitativeParamter);
				}
			}
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
