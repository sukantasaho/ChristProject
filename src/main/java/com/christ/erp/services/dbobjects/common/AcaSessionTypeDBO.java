package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aca_session_type")
@Getter
@Setter
public class AcaSessionTypeDBO implements Serializable {
	
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="aca_session_type_id")
    private int id;

    @Column(name="session_type_name")
    private String sessionTypeName;
    
    @Column(name="total_session_intakes_in_year")
    private Integer totalSessionIntakesInYear;
    
    @Column(name="curriculum_completion_type")
    private String curriculumCompletionType;
    
    @Column(name = "record_status")
    private char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
}
