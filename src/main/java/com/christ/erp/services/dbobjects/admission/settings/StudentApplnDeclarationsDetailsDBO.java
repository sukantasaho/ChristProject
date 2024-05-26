package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "student_appln_declarations_details")
@Getter
@Setter
public class StudentApplnDeclarationsDetailsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_appln_declarations_details_id")
    private int id;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "declaration_display_order")
    private Integer declarationDisplayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_appln_declarations_template_id")
    private StudentApplnDeclarationsTemplateDBO studentApplnDeclarationsTemplateDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_appln_declarations_id")
    private StudentApplnDeclarationsDBO studentApplnDeclarationsDBO;

    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}
