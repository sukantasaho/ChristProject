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
@Table(name="erp_university_board")
@Getter
@Setter
public class ErpUniversityBoardDBO implements Serializable {

    private static final long serialVersionUID = 7076741529206722045L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_university_board_id")
    private int id;

    @Column(name="university_board_name")
    private String universityBoardName;

    @Column(name="board_type")
    private String boardType;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
    
    @ManyToOne
    @JoinColumn(name = "erp_country_id")
    public ErpCountryDBO erpCountryDBO;
    
    @ManyToOne
	@JoinColumn(name = "erp_state_id")
	public ErpStateDBO erpStateDBO;
}
