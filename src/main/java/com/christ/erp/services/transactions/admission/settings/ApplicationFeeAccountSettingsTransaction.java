package com.christ.erp.services.transactions.admission.settings;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;


@Repository
public class ApplicationFeeAccountSettingsTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getGridData() {
		String camStr = " select DISTINCT erp_programme.erp_programme_id as erp_programme_id,erp_programme.programme_name as programme_name, \r\n"
				+ "				  erp_programme.programme_code as programme_code,erp_location.location_name as location_name,erp_location.erp_location_id as erp_location_id, \r\n"
				+ "				  null as erp_campus_id,null as campus_name, \r\n"
				+ "				  acc_accounts.account_no as account_no,acc_accounts.acc_accounts_id as acc_accounts_id, \r\n"
				+ "				   ac.acc_accounts_id as erp_campus_programme_mapping_acc_account_id, \r\n"
				+ "          ac.account_no as erp_campus_programme_mapping_account_no, \r\n"
				+ "				  erp_campus_programme_mapping.erp_campus_programme_mapping_id as erp_campus_programme_mapping_id \r\n"
				+ "				  from adm_programme_settings \r\n"
				+ "				  left join erp_programme ON erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id and adm_programme_settings.record_status='A' \r\n"
				+ "				  left join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id= adm_programme_settings.erp_programme_id  and  erp_campus_programme_mapping.record_status='A' \r\n"
				+ "				  left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id and erp_location.record_status='A' \r\n"
				+ "				  left join erp_campus on erp_campus.erp_location_id = erp_location.erp_location_id and erp_campus.record_status='A' \r\n"
				+ "				  left join acc_accounts on acc_accounts.erp_campus_id = erp_campus.erp_campus_id and acc_accounts.record_status='A' \r\n"
				+ "				  left join acc_accounts as ac ON ac.acc_accounts_id = erp_campus_programme_mapping.acc_account_id and acc_accounts.record_status='A' \r\n"
				+ "          where  adm_programme_settings.record_status='A' and  adm_programme_settings.preference_option='L' and erp_campus_programme_mapping.erp_campus_id IS NULL and acc_accounts.is_university_account =1\r\n"
				+ "          \r\n"
				+ "          Union\r\n"
				+ "          \r\n"
				+ "          select DISTINCT erp_programme.erp_programme_id as erp_programme_id,erp_programme.programme_name as programme_name,erp_programme.programme_code as programme_code, \r\n"
				+ "				 null as location_id,null as location_name, \r\n"
				+ "				 erp_campus.erp_campus_id as erp_campus_id,erp_campus.campus_name as campus_name, \r\n"
				+ "				 acc_accounts.account_no as account_no,acc_accounts.acc_accounts_id as acc_accounts_id, \r\n"
				+ "				 ac.acc_accounts_id as erp_campus_programme_mapping_acc_account_id, \r\n"
				+ "          ac.account_no as erp_campus_programme_mapping_account_no, \r\n"
				+ "         erp_campus_programme_mapping.erp_campus_programme_mapping_id as erp_campus_programme_mapping_id \r\n"
				+ "				 from adm_programme_settings \r\n"
				+ "				 left join erp_programme ON erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id and erp_programme.record_status='A' \r\n"
				+ "				 left join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_programme_id= adm_programme_settings.erp_programme_id  and  erp_campus_programme_mapping.record_status='A' \r\n"
				+ "				 left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status='A' \r\n"
				+ "				 left join acc_accounts on acc_accounts.erp_campus_id = erp_campus.erp_campus_id and acc_accounts.record_status='A' \r\n"
				+ "				 left join acc_accounts as ac ON ac.acc_accounts_id = erp_campus_programme_mapping.acc_account_id and acc_accounts.record_status='A' \r\n"
				+ "         where  adm_programme_settings.record_status='A' and  adm_programme_settings.preference_option='C' and erp_campus_programme_mapping.erp_location_id IS NULL and acc_accounts.is_university_account =1 order by erp_campus_programme_mapping_id";
		List<Tuple> camList = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(camStr,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return camList;
	}

	public void merge(List<ErpCampusProgrammeMappingDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}

	public List<ErpCampusProgrammeMappingDBO> getData(List<Integer> erpCampusProgrammeMappingIds) {
		String str = "from ErpCampusProgrammeMappingDBO dbo where dbo.recordStatus ='A' and dbo.id IN (:erpCampusProgrammeMappingIds)";
		String finalStr = str;
		List<ErpCampusProgrammeMappingDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpCampusProgrammeMappingDBO> query = s.createQuery(finalStr, ErpCampusProgrammeMappingDBO.class);
			query.setParameter("erpCampusProgrammeMappingIds", erpCampusProgrammeMappingIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}
}