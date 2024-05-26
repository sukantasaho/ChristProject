package com.christ.erp.services.dto.aws;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class URLFolderListDTO {
	private int folderListId;
	private String uploadProcessCode;
	private String tempFolderPath;
	private String folderPath;
	private Integer fileSizeInKB;
	private List<String> fileTypeList;
	private String bucketName;
	private String tempBucketName;
	private String region;
	private String endPoint;
	private Integer expiryUpload;
	private Integer expiryDownload;

}
