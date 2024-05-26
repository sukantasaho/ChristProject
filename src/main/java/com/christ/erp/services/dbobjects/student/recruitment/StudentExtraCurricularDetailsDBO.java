package com.christ.erp.services.dbobjects.student.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpExtraCurricularDBO;
import com.christ.erp.services.dbobjects.common.ErpSportsDBO;
import com.christ.erp.services.dbobjects.common.ErpSportsLevelDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="student_extra_curricular_details")
@Setter
@Getter
public class StudentExtraCurricularDetailsDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_extra_curricular_details_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_extra_curricular_id")
    private ErpExtraCurricularDBO erpExtraCurricularDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_sports_id")
    private ErpSportsDBO erpSportsDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_sports_level_id")
    private ErpSportsLevelDBO erpSportsLevelDBO;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;

}
