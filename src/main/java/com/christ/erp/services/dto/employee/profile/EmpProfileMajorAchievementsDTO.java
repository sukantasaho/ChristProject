package com.christ.erp.services.dto.employee.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpProfileMajorAchievementsDTO {
    public int id;
    public String achievements;
    private int empId;
    public EmpProfileMajorAchievementsDTO(){

    }
    public EmpProfileMajorAchievementsDTO(int id, int empId, String achievements) {
        this.id = id;
        this.empId = empId;
        this.achievements = achievements;
    }
}
