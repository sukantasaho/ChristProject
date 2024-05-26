package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "erp_users_campus")
public class ErpUsersCampusDBO implements Serializable {

    private static final long serialVersionUID = 3102529556859250073L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_users_campus_id")
    public Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_users_id")
    public ErpUsersDBO erpUsersDBO;

    @ManyToOne
    @JoinColumn(name="erp_campus_id")
    public ErpCampusDBO erpCampusDBO;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

    @Column(name="is_preferred")
    private Boolean isPreferred;
}
