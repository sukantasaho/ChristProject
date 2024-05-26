package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AdmQualificationListDTO {
    private int id;
    private String qualificationName;
    private Integer qualificationOrder;
    private String shortName;
    private Boolean isAdditionalDocument;
    private SelectDTO boardType;
    private Integer createdUsersId;
    private Date createdTime;
    private Integer modifiedUsersId;
    private Date modifiedTime;
    private char recordStatus;
    private Integer parentId;
    private Integer score;
    private SelectDTO qualificationlist;
}
