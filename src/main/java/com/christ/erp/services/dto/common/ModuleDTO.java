package com.christ.erp.services.dto.common;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class ModuleDTO implements Comparable<ModuleDTO> {
    public String id;
    public String text;
    public Integer displayOrder;
    public List<ModuleSubDTO> menus;
    private String iconClassName;
    @Override
    public int compareTo(@NotNull ModuleDTO dto) {
        return this.displayOrder.compareTo(dto.displayOrder);
    }
}
