package com.christ.erp.services.dto.regulations;

import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class RegulationUploadDownloadDTO {

    private String regulationDocReferenceNo;
    private String regulationDocTitle;
    private SelectDTO regulationDocCategory;
    //private String docCategory;
    private LocalDateTime regulationDocPublishDate;
    private String  docAuthor;
    private String regulationDocVersion;
    private LocalDateTime regulationDocValidFrom;
    private LocalDateTime regulationDocValidTill;
    private String regulationDocAccessPolicy;
    private String searchTags;
    private String regulationEntriesDescription;
    private Integer regulationEntryId;
    private CommonUploadDownloadDTO uploadData;
    private String[] uploadCodeList;
}
