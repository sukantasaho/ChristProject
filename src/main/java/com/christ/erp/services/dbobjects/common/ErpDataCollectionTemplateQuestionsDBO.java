package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "erp_data_collection_template_questions")
@Getter
@Setter
public class ErpDataCollectionTemplateQuestionsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erp_data_collection_template_questions_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "erp_data_collection_template_section_id")
    private ErpDataCollectionTemplateSectionDBO erpDataCollectionTemplateSectionDBO;

    @Column(name = "display_level")
    private Integer displayLevel;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "feedback_question")
    private String feedbackQuestion;

    @Column(name = "Is_mandatory")
    private Boolean isMandatory;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "go_to_question_based_answer")
    private Boolean goToQuestionBasedAnswer;

    @Column(name = "is_multiple_file_upload")
    private Boolean isMultipleFileUpload;

    @Column(name = "is_allow_half_icon")
    private Boolean isAllowHalfIcon;

    @Column(name = "rating_scale")
    private Integer ratingScale;

    @Column(name = "rating_shape")
    private String ratingShape;

    @Column(name = "rating_color")
    private String ratingColor;

    @Column(name = "is_add_rating_label")
    private Boolean isAddRatingLabel;

    @Column(name = "slider_min_value")
    private Integer sliderMinValue;

    @Column(name = "slider_max_value")
    private Integer sliderMaxValue;

    @Column(name = "slider_interval")
    private Integer sliderInterval;

    @Column(name = "is_add_image_label")
    private Boolean isAddImageLabel;

    @Column(name = "created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;

    @OneToMany(mappedBy ="erpDataCollectionTemplateQuestionsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<ErpDataCollectionTemplateQuestionsOptionsDBO> erpDataCollectionTemplateQuestionsOptionsDBOS = new HashSet<>();
}
