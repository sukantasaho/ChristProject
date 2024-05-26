package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonUploadDownloadDTO {
    private String originalFileName;
    private String uniqueFileName;
    public Boolean newFile;
    private String processCode;
    private String actualPath;
    private String tempPath;
}
