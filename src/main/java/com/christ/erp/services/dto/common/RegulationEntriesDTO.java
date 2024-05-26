package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class RegulationEntriesDTO {

    private String regulationDocReferenceNo;
    private String regulationDocTitle;
    private SelectDTO regulationDocCategory;
    private LocalDateTime regulationDocPublishDate;
    private String docAuthor;
    private String regulationDocVersion;
    private LocalDateTime regulationDocValidFrom;
    private LocalDateTime regulationDocValidTill;
    private String regulationDocAccessPolicy;
    private String searchTags;
    private String regulationEntriesDescription;
    private Integer regulationDisplayOrder;
    private Integer createdUsersId;
    private String createdTime;
    private Integer modifiedUsersId;
    private String modifiedTime;
    private char recordStatus;
    private Integer regulationEntryId;
    private String actualPath;
    private String originalFileName;
    private Integer url_access_link;
    private UploadDataDTO uploadData;

}
