package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FamilyDependentDTO {
    public String relationship;
    public List<EmpFamilyDetailsAddtnlDTO> empFamilyDetailsAddtnlDTOs;
}
