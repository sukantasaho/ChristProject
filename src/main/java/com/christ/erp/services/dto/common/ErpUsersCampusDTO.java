package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpUsersCampusDTO {
   public String campusId;
   public String userId;
   public String campusName;
   public Boolean isPreferred;
   public String campusColor;
   public String shortName;
   private SelectDTO campus;
   private int id;
   private String userName;
}
