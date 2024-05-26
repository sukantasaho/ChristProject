package com.christ.erp.services.dto.admission.applicationprocess;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeHeadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalResultApprovalDTO {
	private String studentAdmApplnId;
	private String programe;
	private String campus;
	private String location;
	private String applicantName;
	private String applicationNumber;
	private LocalDateTime lastDateTimeOfFeePayment;
	private String status;
	private String remarks;
	private SelectDTO admissionCategory;
	private SelectDTO residentCategory;
	private String emailId;
	private String mobileNumber;
	private String erpCampusProgrameMappingId;
	private String showAdmissionCard;
	private String admissionCardPreview;
	private String feeDetails;
	private String acaDurationId;
	private String acaBatchId;
	private List<AccBatchFeeDTO> accBatchFeeDTOList;
	private String programeId;
	private String feePaymentModes;
	private String erpWorkFlowProcessCode;
	private Map<String,List<FinalResultApprovalTemplatesDTO>> templateDetails;
	private AccBatchFeeCategoryDBO accBatchFeeCategoryDBO;
	private List<AccFeeHeadsAccountDBO> admissionProcessingFeeList;
	private List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList;
	private String admissionProcessingFeeString;
	private LocalDateTime admissionStartDateTime;
	private LocalDateTime admissionEndDateTime;
	private String selectedCount;
	private String notSelectedCount;
	private String waitlistedCount;
	private String modeOfStudy;
}
