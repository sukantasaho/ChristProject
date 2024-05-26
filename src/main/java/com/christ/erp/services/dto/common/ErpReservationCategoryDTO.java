package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpReservationCategoryDTO {

    private int id;
    private String reservationCategoryName;
    private Integer createdUsersId;
    private Integer modifiedUsersId;
    private char recordStatus;
}
