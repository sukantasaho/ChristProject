package com.christ.erp.services.dto.aws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDownloadDTO {
	private String originalFileName;
	private String uniqueFileName;
	private String tempPath;
	private String actualPath;
	private String preSignedUrl;
	private String menuId;
	private String processCode;
	private String bucketName;
	private String tempBucketName;
	public Boolean newFile;
	
	public FileUploadDownloadDTO() { 
	}
	public FileUploadDownloadDTO(String uniqueFileName,  String temPath, String actualPath) {
		this.tempPath = temPath;
		this.actualPath = actualPath;
		this.uniqueFileName = uniqueFileName;
    }
	
}
