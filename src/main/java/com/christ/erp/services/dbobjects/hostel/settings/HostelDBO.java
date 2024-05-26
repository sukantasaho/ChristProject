package com.christ.erp.services.dbobjects.hostel.settings;

import com.christ.erp.services.dbobjects.common.*;
import com.christ.erp.services.dbobjects.employee.common.HostelProgrammeDetailsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name="hostel")
public class HostelDBO implements Serializable {

    private static final long serialVersionUID = -6719584823226995689L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hostel_id")
    public Integer id;

    @Column(name = "hostel_name")
    public String hostelName;

    @Column(name = "hostel_address_1")
    public String addressLineOne;

    @Column(name = "hostel_address_2")
    public String addressLineTwo;

    @Column(name = "hostel_phone_number_1")
    public String phoneNumberOne;

    @Column(name = "hostel_phone_number_2")
    public String phoneNumberTwo;

    @Column(name = "hostel_fax_number")
    public String faxNo;

    @Column(name = "hostel_email")
    public String email;

    @Column(name = "online_cancellation_days")
    public Integer onlineCancellationDays;

    @ManyToOne
    @JoinColumn(name = "erp_gender_id")
    public ErpGenderDBO erpGenderDBO;

//    @ManyToOne
//    @JoinColumn(name = "erp_pincode_id")
//    public ErpPincodeDBO erpPincodeDBO;
    
    @Column(name = "erp_pincode")
    private Integer erpPincode;

    @ManyToOne
    @JoinColumn(name = "erp_city_id")
    public ErpCityDBO erpCityDBO;

    @ManyToOne
    @JoinColumn(name = "erp_state_id")
    public ErpStateDBO erpStateDBO;

    @ManyToOne
    @JoinColumn(name = "erp_country_id")
    public ErpCountryDBO erpCountryDBO;

    @ManyToOne
    @JoinColumn(name = "erp_campus_id")
    public ErpCampusDBO erpCampusDBO;
    
    @Column(name ="hostel_information")
    private String hostelInformation;
    
    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @ManyToOne
    @JoinColumn(name = "student_declaration_template_id")
    public ErpTemplateDBO erpTemplateDBO;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "hostelDBO", cascade = CascadeType.ALL)
    public Set<HostelProgrammeDetailsDBO> hostelProgrammeDetailsDBO =  new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "hostelDBO", cascade = CascadeType.ALL)
    public Set<HostelBlockDBO> hostelBlockDBOSet =  new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "hostelDBO", cascade = CascadeType.ALL)
    private Set<HostelImagesDBO> hostelImagesDBOSet =  new HashSet<>();
    
}
