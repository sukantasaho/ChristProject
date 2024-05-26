package com.christ.erp.services.dbobjects.common;

import com.christ.erp.services.dbobjects.hostel.settings.HostelSeatAvailabilityDetailsDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "erp_data_collection_template")
@Getter
@Setter
public class ErpDataCollectionTemplateDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_data_collection_template_id")
    private int id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_for")
    private String templateFor;

    @Column(name = "instructions", columnDefinition = "mediumtext")
    private String instructions;

    @Column(name = "created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;

    @OneToMany(mappedBy ="erpDataCollectionTemplateDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<ErpDataCollectionTemplateSectionDBO> erpDataCollectionTemplateSectionDBOS = new HashSet<>();
}
