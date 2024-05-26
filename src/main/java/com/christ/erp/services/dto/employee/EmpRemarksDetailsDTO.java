package com.christ.erp.services.dto.employee;

import java.time.LocalDate;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;

@Getter
@Setter
public class EmpRemarksDetailsDTO {
	private int id;
	private LocalDate remarksDate;
	private String remarksDetails;
	private String enteredBy;
	private Boolean isForOfficeUse;
	private CommonUploadDownloadDTO remarksUploadUrlDTO;
	
	public EmpRemarksDetailsDTO() {
	}
	
	public EmpRemarksDetailsDTO(int id, LocalDate remarksDate, String remarksDetails, String fileNameUnique, String fileNameOrig, String processCode, String enteredBy, Boolean isForOfficeUse) {
		this.id = id;
		this.remarksDate = remarksDate;
		this.remarksDetails = remarksDetails;
		this.enteredBy = enteredBy;
		this.isForOfficeUse = isForOfficeUse;

		CommonUploadDownloadDTO remarksUploadUrlDTO = null;
		if(!Utils.isNullOrEmpty(fileNameUnique)) {
			remarksUploadUrlDTO = new CommonUploadDownloadDTO();
			remarksUploadUrlDTO.setActualPath(fileNameUnique);
			remarksUploadUrlDTO.setOriginalFileName(fileNameOrig);
			remarksUploadUrlDTO.setProcessCode(processCode);
			remarksUploadUrlDTO.setNewFile(false);
		}
		this.remarksUploadUrlDTO = remarksUploadUrlDTO;
	}
}
