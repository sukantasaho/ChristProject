package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "erp_data_collection_template_section")
@Getter
@Setter
public class ErpDataCollectionTemplateSectionDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_data_collection_template_section_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "erp_data_collection_template_id")
    private ErpDataCollectionTemplateDBO erpDataCollectionTemplateDBO;

    @Column(name = "section_no")
    private Integer sectionNo;

    @Column(name = "section_value")
    private String sectionValue;

    @Column(name = "created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;

    @OneToMany(mappedBy ="erpDataCollectionTemplateSectionDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<ErpDataCollectionTemplateQuestionsDBO> erpDataCollectionTemplateQuestionsDBOS = new HashSet<>();
}
