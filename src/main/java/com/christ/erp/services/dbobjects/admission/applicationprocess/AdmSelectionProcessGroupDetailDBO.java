package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_selection_process_group_detail")
@Setter
@Getter
public class AdmSelectionProcessGroupDetailDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_selection_process_group_detail_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "adm_selection_process_group_id")
	private AdmSelectionProcessGroupDBO admSelectionProcessGroupDBO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_appln_entries_id")
	private StudentApplnEntriesDBO studentApplnEntriesDBO;
	
	@Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private char recordStatus;
}