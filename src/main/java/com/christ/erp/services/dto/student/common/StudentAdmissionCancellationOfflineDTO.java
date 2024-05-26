package com.christ.erp.services.dto.student.common;

import java.time.LocalDateTime;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentAdmissionCancellationOfflineDTO {
	
	private int id;
	private SelectDTO batchYear;
	private Integer applicationNo;
    private SelectDTO reasonForCancellation;
    private String reasonForCancellationOthers;
	private LocalDateTime cancellationRequestDateTime;
	private String refundBankName;
	private String refundIfscCode;
	private String refundSwiftCode;
	private String refundIbanNo;
	private String refundAccountNumber;
	private String refundAccountHolderName;
	private String chequeInFavour;
	private String CancellationType;
	private String refundType;
	private String refundAccountHolderType;
	private String refundAccountType;
	private Boolean isHostelAdmitted;
}
