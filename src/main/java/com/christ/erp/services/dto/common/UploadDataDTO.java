package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadDataDTO {

    private String originalFileName;
    private String processCode;
    private String actualPath;
    private String uniqueFileName;
    private String newFile;
    private String tempPath;
    private String preSignedUrl;
}
