package com.christ.erp.services.dbobjects.employee.appraisal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emp_appraisal_elements")
public class EmpAppraisalElementsDBO implements  Serializable{

    private static final long serialVersionUID = -956912542079138986L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appraisal_elements_id")
    public Integer id;

    @Column(name="element_name")
    public String elementName;

    @Column(name="element_description")
    public String elementDescription;

    @Column(name="element_identity")
    public String elementIdentity;

    @Column(name="element_parent_identity")
    public String elementParentIdentity;

    @Column(name = "element_order")
    public Integer elementOrder;

    @Column(name = "element_level")
    public Integer elementLevel;

    @ManyToOne
    @JoinColumn(name = "emp_appraisal_template_id")
    public EmpAppraisalTemplateDBO empAppraisalTemplateDBO;

    @Column(name = "is_group_not_displayed")
    public Boolean isGroupNotDisplayed;

    @Column(name = "is_question")
    public Boolean isQuestion;

    @Column(name="answer_option_selection_type")
    public String answerOptionSelectionType;

    @ManyToOne
    @JoinColumn(name = "emp_appraisal_elements_option_id")
    public EmpAppraisalElementsOptionDBO empAppraisalElementsOptionDBO;

    @Column(name = "created_users_id", updatable = false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
}
