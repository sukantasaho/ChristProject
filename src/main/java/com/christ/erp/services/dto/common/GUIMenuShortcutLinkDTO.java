package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GUIMenuShortcutLinkDTO {
	private int id;
	private String quickLinkType;
	private Integer linkDisplayOrder;
	private SelectDTO erpUsersDTO;
	private SelectDTO sysMenuDTO;
	private String sysMenuComponentPath;
	private String iconClassName;
	private List<GUIMenuShortcutLinkDTO> favouriteList;
	private List<GUIMenuShortcutLinkDTO> recentTabList;
	private String masterTablename;
}
