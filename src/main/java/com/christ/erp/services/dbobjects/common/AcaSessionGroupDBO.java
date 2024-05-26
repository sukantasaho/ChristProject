package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_session_group")
@Getter
@Setter
public class AcaSessionGroupDBO implements Serializable {
	private static final long serialVersionUID = 68156880291631769L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_session_group_id")
    private int id;

    @Column(name="session_number")
    private Integer sessionNumber;
    
    @ManyToOne
    @JoinColumn(name="aca_session_type_id")
    private AcaSessionTypeDBO acaSessionType; 
    
    @Column(name = "record_status")
    private char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name="session_group_name")
    private String sessionGroupName;
}
