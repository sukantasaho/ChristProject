package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "erp_data_collection_template_questions_options")
@Getter
@Setter
public class ErpDataCollectionTemplateQuestionsOptionsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_data_collection_template_questions_options_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "erp_data_collection_template_questions_id")
    private ErpDataCollectionTemplateQuestionsDBO erpDataCollectionTemplateQuestionsDBO;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "option_value")
    private BigDecimal optionValue;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "option_value_text")
    private String optionValueText;

    @ManyToOne
    @JoinColumn(name = "erp_data_collection_template_section_id")
    private ErpDataCollectionTemplateSectionDBO erpDataCollectionTemplateSectionDBO;

    @Column(name = "created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
