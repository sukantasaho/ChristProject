package com.christ.erp.services.dto.common;
import java.util.List;
public class UserDTO extends ModelBaseDTO {
    public String loginId;
    public String userName;
    public List<String> roles;
    public List<String> campuses;
}