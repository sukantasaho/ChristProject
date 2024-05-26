package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Setter
@Getter
public class ModuleSubDTO implements Comparable<ModuleSubDTO> {

    public String ID;
    public String Text;
    public Integer DisplayOrder;
 //   public String IconClassName;
    public List<MenuScreenDTO> Items;
	/* using for role permissions expand*/
    public MenuScreenDTO screenName;
    public List<SysFunctionDTO> permissions;
    private SelectDTO users;
    private SelectDTO roles;

    @Override
    public int compareTo(@NotNull ModuleSubDTO dto) {
        return this.DisplayOrder.compareTo(dto.DisplayOrder);
    }
    
	private String menuId;
	private SelectDTO subModule;
	private String menuName;
	private Integer menuDisplayOrder;
	private Boolean menu = false;
	private String submoduleName;
    public List<MenuScreenDTO> menuScreenList;
	private Integer totalAdditionalPrivilegeAllowed;
	private Integer totalAdditionalPrivilegeCount;
}
