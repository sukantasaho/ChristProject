package com.christ.erp.services.dbobjects.admission.settings;

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "student_appln_declarations")
@Getter
@Setter
public class StudentApplnDeclarationsDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_appln_declarations_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_academic_year_id")
    private ErpAcademicYearDBO erpAcademicYearDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erp_campus_programme_mapping_id")
    private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;

    @OneToMany(mappedBy = "studentApplnDeclarationsDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentApplnDeclarationsDetailsDBO> studentApplnDeclarationsDetailsDBOSet;
}
