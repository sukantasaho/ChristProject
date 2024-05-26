package com.christ.erp.services.dto.common;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class MenuScreenDTO implements Comparable<MenuScreenDTO> {

    public String ID="";
    public String Text="";
    public String Tag="";
    public String Screen="";
    public Integer DisplayOrder;
    public String ComponentPath="";
    public String IconClassName="";
    public boolean isGranted;
    public Boolean isAdditionalPrivilege;
    public Set<ErpCampusDTO> campusList;

    @Override
    public int compareTo(@NotNull MenuScreenDTO dto) {
        return this.DisplayOrder.compareTo(dto.DisplayOrder);
    }
    
    private int menuId;
	private Integer moduleId;
	private SelectDTO moduleName;
	private SelectDTO subModule;
	private String menuName;
	private String menuComponentPath;
	private String displayOrderNo;
	private SelectDTO masterScreenReference;
	private boolean menuLink;
	private boolean reportMenu;
	private boolean userSpecificReport;
	private boolean otpRequired;
	private boolean otpForEveryInstance;
	private List<ModuleSubDTO> moduleSubDTO;
    public List<SysFunctionDTO> sysFunctionDTO;
    private boolean quickMenu;
}
