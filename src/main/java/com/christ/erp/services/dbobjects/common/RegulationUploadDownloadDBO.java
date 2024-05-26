package com.christ.erp.services.dbobjects.common;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name ="regulation_entries")
@Setter
@Getter
public class RegulationUploadDownloadDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_entries_id")
    private int id;

    @Column(name = "regulation_doc_reference_no")
    private String regulationDocReferenceNo;

    @Column(name = "regulation_entries_description")
    private String regulationEntriesDescription;

    @Column(name = "regulation_doc_category")
    private String regulationDocCategory;

    @Column(name = "regulation_search_tags")
    private String regulationSearchTags;


    @Column(name = "regulation_doc_publish_date  ")
    private LocalDateTime regulationDocPublishDate;

    @Column(name = "regulation_doc_valid_from")
    private LocalDateTime regulationDocValidFrom;

    @Column(name = "regulation_doc_valid_till")
    private LocalDateTime regulationDocValidTill;

    @Column(name = "regulation_doc_title")
    private String regulationDocTitle;

    @Column(name = "regulation_doc_version")
    private String regulationDocVersion;

    @Column(name = "regulation_doc_access_policy")
    private String regulationDocAccessPolicy;

    @Column(name = "record_status")
    private char  recordStatus;

    @Column(name = "created_users_id")
    private Integer createdUserId;

    @Column(name = "modified_users_id")
    private Integer modifiedUserId;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="url_access_link_id")
    public UrlAccessLinkDBO urlAccessLinkDBO;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="emp_id")
    private EmpDBO empId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="regulation_category_id")
    private RegulationCategoryDBO regulationCategoryDBO;

}
