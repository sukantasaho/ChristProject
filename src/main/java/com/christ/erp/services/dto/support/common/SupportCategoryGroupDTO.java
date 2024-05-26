package com.christ.erp.services.dto.support.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCategoryGroupDTO {
	private int id;
	private String supportCategoryGroup;
	private SupportAreaTO supportAreaTO;
	private Integer createdUsersId;
	private Integer modifiedUsersId;
    private char recordStatus;
}
