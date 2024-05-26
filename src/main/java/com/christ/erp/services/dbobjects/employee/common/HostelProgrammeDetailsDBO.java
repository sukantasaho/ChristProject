package com.christ.erp.services.dbobjects.employee.common;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDBO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Getter
@Setter
@Entity
@Table(name="hostel_programme_details")
public class HostelProgrammeDetailsDBO implements Serializable {
	
    private static final long serialVersionUID = 4700679691785715184L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_programme_details_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name="hostel_id")
    public HostelDBO hostelDBO;

    @ManyToOne
    @JoinColumn(name = "erp_campus_programme_mapping_id")
    public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
