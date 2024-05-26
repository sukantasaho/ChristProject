package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Getter
@Setter
@Table(name = "adm_prerequisite_settings_details_period")
public class AdmPrerequisiteSettingsDetailsPeriodDBO implements Serializable {

    private static final long serialVersionUID = 8506564136888757420L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "adm_prerequisite_settings_details_period_id")
    private Integer id;

    @Column(name = "exam_year")
    private Integer examYear;

    @Column(name = "exam_month")
    private Integer examMonth;

    @ManyToOne
    @JoinColumn(name = "adm_prerequisite_settings_details_id")
    private AdmPrerequisiteSettingsDetailsDBO admPrerequisiteSettingsDetailsDBO;

    @Column(name="created_users_id")
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;

}
