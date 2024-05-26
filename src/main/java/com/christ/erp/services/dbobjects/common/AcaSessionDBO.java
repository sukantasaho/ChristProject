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
@Table(name = "aca_session")
@Getter
@Setter
public class AcaSessionDBO implements Serializable {

	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_session_id")
    private int id;
    
    @Column(name="session_name")
    private String sessionName;

    @Column(name="term_number")
    private Integer termNumber;
    
    @ManyToOne
    @JoinColumn(name="aca_session_type_id")
    private AcaSessionTypeDBO acaSessionType;

    @ManyToOne
    @JoinColumn(name="aca_session_group_id")
    private  AcaSessionGroupDBO acaSessionGroup;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @Column(name = "year_number")
    private Integer yearNumber;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
}
