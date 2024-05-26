package com.christ.erp.services.dbobjects.admission.settings;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="adm_selection_process_type")
@Setter
@Getter
public class AdmSelectionProcessTypeDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_selection_process_type_id")
    public Integer id;
	
	@Column(name="selection_stage_name")
	public String selectionStageName;
	
	@Column(name="mode")
	public String mode;
	
	@Column(name="is_shortlist_after_this_stage")
	public Boolean isShortlistAfterThisStage;
	
	@Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;
 
    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(mappedBy = "adminSelectionProcessTypeDBO",fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    public Set<AdmSelectionProcessTypeDetailsDBO> admissionSelectionProcessTypeDetailsDBOSet = new HashSet<>();

    @Column(name="admit_card_display_name")
    private String admitCardDisplayName;
}
