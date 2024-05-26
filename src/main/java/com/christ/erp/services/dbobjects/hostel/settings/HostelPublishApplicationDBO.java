package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "hostel_publish_application")
public class HostelPublishApplicationDBO implements Serializable {

    private static final long serialVersionUID = 5925166956296080109L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_publish_application_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "erp_academic_year_id")
    public ErpAcademicYearDBO erpAcademicYearDBO;

    @ManyToOne
    @JoinColumn(name="hostel_id")
    public HostelDBO hostelDBO;

    @Column(name = "is_open_for_first_year")
    public boolean isOpenForFirstYear;
    
    @Column(name = "first_year_start_date")
    public LocalDate firstYearStartDate;
    
    @Column(name = "first_year_end_date")
    public LocalDate firstYearEndDate;

    @Column(name = "is_status_for_first_year")
    public boolean isStatusForFirstYear;

    @Column(name = "is_open_for_subsequent_year")
    public boolean isOpenForSubsequentYear;

    @Column(name = "is_status_for_subsequent_year")
    public boolean isStatusForSubsequentYear;

    @Column(name = "online_application_prefix")
    public String onlineApplicationPrefix;

    @Column(name = "online_application_end_no")
    public Integer onlineApplicationEndNo;
    
    @Column(name = "online_application_start_no")
    public Integer onlineApplicationStartNo;
    
    @Column(name = "online_application_current_no")
    public Integer onlineApplicationCurrentNo;

    @Column(name = "offline_application_prefix")
    public String offlineApplicationPrefix;

    @Column(name = "offline_application_start_no")
    public Integer offlineApplicationStartNo;

    @Column(name = "offline_application_end_no")
    public Integer offlineApplicationEndNo;
    
    @ManyToOne
    @JoinColumn (name = "instruction_template_id")
    public ErpTemplateDBO instructionTemplateId;

    @ManyToOne
    @JoinColumn (name = "declaration_template_id")
    public ErpTemplateDBO  declarationTemplateId;

    @Column(name="created_users_id")
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
