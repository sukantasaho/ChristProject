package com.christ.erp.services.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NestedSelectDTO {
    private String value;
    private String label;
    private List<?> list;
}
