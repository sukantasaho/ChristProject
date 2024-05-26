package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name ="regulation_category")
@Setter
@Getter
public class RegulationCategoryDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_category_id")
    private Integer id;

    @Column(name = "regulation_category_name")
    private String regulationCategoryName;

    /*@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;
    @Column(name="modified_users_id")
    public Integer modifiedUsersId;*/
    @Column(name = "record_status")
    private char recordStatus;
}
