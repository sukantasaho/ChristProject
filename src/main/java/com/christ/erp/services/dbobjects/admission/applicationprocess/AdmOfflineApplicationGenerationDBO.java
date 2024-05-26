package com.christ.erp.services.dbobjects.admission.applicationprocess;

import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeMembersDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name="adm_offline_application_generation")
public class AdmOfflineApplicationGenerationDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_offline_application_generation_id")
    public int id;

    @ManyToOne
    @JoinColumn(name="erp_admission_year_id")
    public ErpAcademicYearDBO erpAcademicYearDBO;

    @ManyToOne
    @JoinColumn(name="student_appln_entries_id")
    public StudentApplnEntriesDBO studentApplnEntriesDBO;

    @Column(name = "application_issue_date")
    public LocalDate applicationIssueDate;

    @Column(name="applicant_name")
    public String applicantName;

    @Column(name="email_to_send_application_link")
    public String emailToSendApplicationLink;

    @Column(name="mobile_no_country_code")
    public String mobileNoCountryCode;

    @Column(name="mobile_no_to_send_application_link")
    public String mobileNoToSendApplicationLink;

    @ManyToOne
    @JoinColumn(name="adm_programme_batch_id")
    public AdmProgrammeBatchDBO admProgrammeBatchDBO;

    @Column(name="is_fee_paid")
    public Boolean isFeePaid;

    @ManyToOne
    @JoinColumn(name="erp_receipts_id")
    public ErpReceiptsDBO erpReceiptsDBO;

    @Column(name="receipt_reference_no")
    public String recieptReferenceNo;

    @Column(name="is_selection_process_date_allotted")
    public Boolean isSelectionProcessDateAllotted;

    @Column(name="application_link_url")
    public String applicationLinkUrl;

    @Column(name="is_link_expired")
    public Boolean isLinkExpired;

    @Column(name="application_unique_value")
    public String applicationUniqueValue;

    @Column(name="created_users_id")
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

}
