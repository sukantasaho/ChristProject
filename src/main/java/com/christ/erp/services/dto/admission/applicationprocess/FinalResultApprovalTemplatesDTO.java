package com.christ.erp.services.dto.admission.applicationprocess;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalResultApprovalTemplatesDTO {
	private String campus;
	private FinalResultApprovalStatusDTO smsTemplate;
	private FinalResultApprovalStatusDTO emailTemplate;
	private FinalResultApprovalStatusDTO AdmissionCardTemplate;

}
