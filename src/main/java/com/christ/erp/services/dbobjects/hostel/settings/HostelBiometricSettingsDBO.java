package com.christ.erp.services.dbobjects.hostel.settings;

import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;

import javax.persistence.*;

@Entity
@Table(name = "hostel_biometric_settings")
public class HostelBiometricSettingsDBO {

    private static final long serialVersionUID = -4937980800529251060L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "hostel_biometric_settings_id")
    public Integer id;

    @Column(name = "machine_name")
    public String machineName;

    @Column(name = "machine_no")
    public String machineNum;

    @Column(name = "machine_ip_address")
    public String machineIpAddress;

    @ManyToOne
    @JoinColumn(name = "hostel_block_unit_id")
    public HostelBlockUnitDBO hostelBlockUnitDBO;

    @Column(name="created_users_id")
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
