package com.christ.erp.services.transactions.common;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpNotificationUsersReadDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersCampusDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ScreenConfigTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<Tuple>> getSystemMenus(String userId, String campusIds) {
        String str = "select erp_users_id," +
                " sys_menu.menu_screen_display_order," +
                "  sys_menu.sys_menu_id," +
                "  sys_menu.menu_screen_name," +
                "  sys_menu.menu_component_path," +
                "  sys_menu_module_sub.sub_module_display_order as sub_module_display_order," +
                "  sys_menu_module_sub.sys_menu_module_sub_id as sys_menu_module_sub_id," +
                "  sys_menu_module_sub.sub_module_name as sub_module_name," +
                "  sys_menu_module.icon_class_name as icon_class_name," +
                "  sys_menu_module.module_display_order as module_display_order," +
                "  sys_menu_module.sys_menu_module_id as sys_menu_module_id," +
                "  sys_menu_module.module_name as module_name," +
                "  sys_function.sys_function_id,sys_function.function_name,sys_function.access_token," +
                "  erp_campus_id," +
                /* group_concat(roles),#comment this line while going to production */
                " left(group_concat(allowed order by allowed ),1) as allowed, erp_screen_config_mast.mapped_table_name, gui_menu_shortcut_link.quick_link_type as quick_link_type" +
                " from (" +
                " select sys_user_role_map.erp_users_id,sys_function.sys_function_id, erp_campus.erp_campus_id," +
                /* group_concat(sys_role.role_name) as roles,#comment this line while going to production */
                /* null as sys_user_function_override_id,#comment this line while going to production */
                " 1 as allowed" +
                " from sys_function" +
                " inner join sys_role_function_map on sys_role_function_map.sys_function_id = sys_function.sys_function_id and sys_role_function_map.record_status = 'A' and sys_role_function_map.is_authorised=1" +
                " inner join sys_role on sys_role_function_map.sys_role_id = sys_role.sys_role_id and sys_role.record_status = 'A'" +
                " inner join sys_user_role_map on sys_user_role_map.sys_role_id = sys_role.sys_role_id " +
                " and sys_user_role_map.record_status = 'A' " +
                " inner join erp_users_campus on sys_user_role_map.erp_users_id=erp_users_campus.erp_users_id" +
                " and sys_user_role_map.erp_campus_id=erp_users_campus.erp_campus_id" +
                " and erp_users_campus.record_status='A'" +
                " inner join erp_campus ON erp_campus.erp_campus_id = erp_users_campus.erp_campus_id" +
                " and sys_user_role_map.erp_campus_id = erp_users_campus.erp_campus_id and erp_campus.record_status = 'A'" +
                /*#where sys_function.sys_function_id=1*/
                " where 1=1" ;
        if(!Utils.isNullOrEmpty(userId)) {
            str += " and sys_user_role_map.erp_users_id = :userId";
        }
        if(!Utils.isNullOrEmpty(campusIds)) {
            str +=  " and erp_campus.erp_campus_id in (:campusIds)";
        }
        str += " group by sys_user_role_map.erp_users_id ,sys_function.sys_function_id,erp_campus.erp_campus_id" +
                " union all" +
                " select sys_user_function_override.erp_users_id,sys_function.sys_function_id, erp_campus.erp_campus_id ," +
                /* null as roles,#comment this line while going to production */
                /* sys_user_function_override.sys_user_function_override_id,#comment this line while going to production */
                " sys_user_function_override.is_allowed" +
                " from sys_function" +
				/*" inner join sys_menu ON sys_menu.sys_menu_id = sys_function.sys_menu_id and sys_menu.record_status = 'A' and sys_menu.is_displayed = 1 " +
				" inner join sys_menu_module_sub ON sys_menu_module_sub.sys_menu_module_sub_id = sys_menu.sys_menu_module_sub_id and sys_menu_module_sub.record_status = 'A'" +
				" inner join sys_menu_module ON sys_menu_module.sys_menu_module_id = sys_menu_module_sub.sys_menu_module_id and sys_menu_module.record_status = 'A'" +*/
                " inner join sys_user_function_override on sys_user_function_override.sys_function_id = sys_function.sys_function_id " +
                " and sys_user_function_override.record_status = 'A' " +
                " inner join erp_campus ON erp_campus.erp_campus_id=sys_user_function_override.erp_campus_id and erp_campus.record_status = 'A'" +
                " where 1=1" ;
        if(!Utils.isNullOrEmpty(userId)) {
            str += " and sys_user_function_override.erp_users_id = :userId ";
        }
        if(!Utils.isNullOrEmpty(campusIds)) {
            str += " and  erp_campus.erp_campus_id in (:campusIds)";
        }
        str += " group by sys_user_function_override.erp_users_id ,sys_function.sys_function_id,sys_user_function_override.sys_user_function_override_id" +
                " ) as derived_table" +
                " inner join sys_function on sys_function.sys_function_id=derived_table.sys_function_id and sys_function.record_status = 'A'" +
                " left join sys_menu ON sys_menu.sys_menu_id = sys_function.sys_menu_id and sys_menu.record_status = 'A' and sys_menu.is_displayed = 1 " +
				" left join gui_menu_shortcut_link on gui_menu_shortcut_link.sys_menu_id = sys_menu.sys_menu_id and gui_menu_shortcut_link.quick_link_type = 'Q' and gui_menu_shortcut_link.record_status='A' and gui_menu_shortcut_link.users_id = :userId" +
                " left join sys_menu_module_sub ON sys_menu_module_sub.sys_menu_module_sub_id = sys_menu.sys_menu_module_sub_id and sys_menu_module_sub.record_status = 'A'" +
                " left join sys_menu_module ON sys_menu_module.sys_menu_module_id = sys_menu_module_sub.sys_menu_module_id and sys_menu_module.record_status = 'A'" +
                " left join erp_screen_config_mast ON erp_screen_config_mast.erp_screen_config_mast_id = sys_menu.erp_screen_config_mast_id and erp_screen_config_mast.record_status = 'A'"	+
                " group by derived_table.erp_users_id,sys_function.sys_function_id,erp_campus_id";
        String str1 = str;
        return Mono.fromFuture(sessionFactory.withSession(s-> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(str1,Tuple.class);
            if(!Utils.isNullOrEmpty(userId)) {
                query.setParameter("userId", userId);
            }
            if(!Utils.isNullOrEmpty(campusIds)) {
                query.setParameter("campusIds", campusIds);
            }
            return query.getResultList();
        }).subscribeAsCompletionStage());
    }

    public Mono<List<ErpUsersCampusDBO>> getUserCampus(String userId) {
        String str = "select bo from ErpUsersCampusDBO bo inner join bo.erpUsersDBO udbo where bo.recordStatus = 'A' and udbo.id=:userId";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, ErpUsersCampusDBO.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());
    }

	public Mono<List<Tuple>> getUserDetails(String userId) {
		String str = "select erp_users_campus.erp_users_campus_id as erp_users_campus_id ,erp_campus.erp_campus_id as erp_campus_id ," +
				" erp_campus.campus_name as campus_name,erp_campus.short_name as short_name, erp_users_campus.is_preferred as is_preferred," +
				" erp_users_campus.erp_users_id as erp_users_id, erp_users.erp_users_name as erp_users_name  FROM erp_users_campus" +
				" inner join erp_users ON erp_users.erp_users_id = erp_users_campus.erp_users_id " +
				" inner join erp_campus ON erp_campus.erp_campus_id = erp_users_campus.erp_campus_id" +
				" and erp_users_campus.record_status = 'A'" +
				" and erp_users_campus.erp_users_id = :userId";
		return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).subscribeAsCompletionStage());
	}


    public boolean saveUserCampus(List<ErpUsersCampusDBO> list) {
        boolean istrue =  sessionFactory.withTransaction((session, tx) -> session.mergeAll(list.toArray())).subscribeAsCompletionStage().isDone();
        //System.out.println("result"+istrue);
        return true;
    }

    //@EventListener(ApplicationReadyEvent.class)
    public void getUserDetails() {
        System.out.println("hi");
        String str = "select * from user_details where erp_users_id=2";
        Tuple tuple = sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).getSingleResultOrNull()).await().indefinitely();
        System.out.println(tuple.get("erp_users_id"));
    }

    public Mono<List<Tuple>> getUnAuthMessages() {
        String str = " select distinct erp_users.erp_users_id,sys_function.sys_function_id,sys_function.access_token,sys_function.display_message from sys_role_function_map " +
                " inner join sys_user_role_map on sys_user_role_map.sys_role_id =  sys_role_function_map.sys_role_id and sys_user_role_map.record_status = 'A'" +
                " inner join erp_users ON erp_users.erp_users_id = sys_user_role_map.erp_users_id and erp_users.record_status = 'A'" +
                " inner join sys_function ON sys_function.sys_function_id = sys_role_function_map.sys_function_id and sys_function.record_status = 'A' " +
                " where sys_role_function_map.record_status = 'A' and is_authorised=0" +
                " union all" +
                " select distinct erp_users.erp_users_id, sys_function.sys_function_id,sys_function.access_token,sys_function.display_message from sys_user_function_override " +
                " inner join erp_users ON erp_users.erp_users_id = sys_user_function_override.erp_users_id and erp_users.record_status = 'A'" +
                " inner join sys_function ON sys_function.sys_function_id  and sys_user_function_override.sys_function_id and sys_function.record_status = 'A'" +
                " where sys_user_function_override.record_status = 'A' and sys_user_function_override.is_allowed = 0";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());
    }
    
    public List<Tuple> getERPNotification(String userId) {
		String str = " select '' as code,core_not.* from ("
				+ " SELECT erp_notifications.erp_notifications_id, erp_notifications.erp_users_id, erp_notification_user_entries.erp_notification_user_entries_id ,1 as cnt,"
				+ " \"U\" as Notification_type,  erp_notification_user_entries.notification_content,erp_notification_users_read.is_notification_seen, erp_notification_user_entries.notification_component_path,"
				+ " '' as notification_description,'' as  min_val,'' as max_val"
				+ " FROM erp_notifications"
				+ " inner join  erp_notification_user_entries ON erp_notification_user_entries.erp_notification_user_entries_id = erp_notifications.erp_notification_user_entries_id"
				+ " left join erp_notification_users_read on erp_notification_users_read.erp_notifications_id = erp_notifications.erp_notifications_id"
				+ " and erp_notification_users_read.erp_users_id =:userId"
				+ " where erp_notifications.erp_users_id =:userId"
				+ " and erp_notifications.record_status='A'"
				+ " and now() between erp_notifications.notification_from_date_time and erp_notifications.notification_to_date_time"
				+ " and  notification_log_time< SUBDATE(date(now()) , INTERVAL 30 DAY)"
				+ " group by erp_notifications.erp_notifications_id,erp_notifications.erp_users_id,erp_notification_user_entries.erp_notification_user_entries_id,erp_notification_users_read.erp_notification_users_read_id"
				+ " union"
				+ " SELECT  erp_notifications.erp_notifications_id,erp_notifications.erp_users_id, erp_reminder_notifications.erp_reminder_notifications_id ,1 as cnt ,"
				+ " \"R\" as Notification_type,  erp_reminder_notifications.reminder_comments,erp_notification_users_read.is_notification_seen,'' as erp_reminder_notifications,"
				+ " '' as notification_description,'' as  min_val,'' as max_val"
				+ " FROM erp_notifications"
				+ " inner join  erp_reminder_notifications  ON erp_reminder_notifications.erp_reminder_notifications_id = erp_notifications.erp_reminder_notifications_id"
				+ " left join erp_notification_users_read on erp_notification_users_read.erp_notifications_id = erp_notifications.erp_notifications_id"
				+ " and erp_notification_users_read.erp_users_id =:userId"
				+ " where erp_notifications.erp_users_id=:userId"
				+ " and erp_notifications.record_status='A'"
				+ " and now() between erp_notifications.notification_from_date_time and erp_notifications.notification_to_date_time"
				+ " and  notification_log_time< SUBDATE(date(now()) , INTERVAL 30 DAY)"
				+ " and date(notification_log_time)"
				+ " group by erp_notifications.erp_notifications_id,erp_notifications.erp_users_id,erp_reminder_notifications.erp_reminder_notifications_id,erp_notification_users_read.erp_notification_users_read_id"
				+ " ) as core_not"
				+ " union"
				+ " select erp_work_flow_process_notifications.notification_code,"
				+ " null as notification_id,"
				+ " notif_work_flow.erp_users_id,"
				+ " null as entry_id, notif_work_flow.cnt, notif_work_flow.Notification_type,"
				+ " erp_work_flow_process_notifications.notification_content  ,0 as is_notification_seen,"
				+ " erp_work_flow_process_notifications.notification_hyperlink ,"
				+ " erp_work_flow_process_notifications.notification_description as notification_description,"
				+ " min_val,max_val"
				+ " from ("
				+ " SELECT  erp_notifications.erp_users_id, erp_work_flow_process_notifications.erp_work_flow_process_notifications_id as W_U_R_id,count( erp_notifications.erp_entries_id) as cnt"
				+ " ,\"W\" as Notification_type,min(erp_notifications.erp_notifications_id) as min_val,max(erp_notifications.erp_notifications_id) as max_val"
				+ " FROM erp_notifications"
				+ " inner join erp_work_flow_process_notifications ON erp_work_flow_process_notifications.erp_work_flow_process_notifications_id = erp_notifications.erp_work_flow_process_notifications_id"
				+ " where erp_notifications.erp_users_id =:userId"
				+ " and erp_notifications.record_status='A'"
				+ " and now() between erp_notifications.notification_from_date_time and erp_notifications.notification_to_date_time"
				+ " and  notification_log_time< SUBDATE(date(now()) , INTERVAL 30 DAY)"
				+ " group by erp_notifications.erp_users_id,erp_work_flow_process_notifications.erp_work_flow_process_notifications_id"
				+ " ) as notif_work_flow"
				+ " inner join erp_work_flow_process_notifications on notif_work_flow.W_U_R_id=erp_work_flow_process_notifications.erp_work_flow_process_notifications_id";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("userId", Integer.parseInt(userId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getERPWorkFlowNotification(List<String> code, List<Integer> ids,String userId) {
		String str = " SELECT  erp_notifications.erp_users_id, erp_notifications.erp_notifications_id as W_U_R_id,\"W\" as Notification_type,erp_work_flow_process.process_code,erp_notification_users_read.is_notification_seen, erp_work_flow_process_notifications.notification_content,"
				+ " erp_work_flow_process_notifications.notification_hyperlink ,erp_work_flow_process_notifications.notification_description as notification_description"
				+ " FROM erp_notifications "
				+ " inner join erp_work_flow_process_notifications ON erp_work_flow_process_notifications.erp_work_flow_process_notifications_id = erp_notifications.erp_work_flow_process_notifications_id and erp_work_flow_process_notifications.record_status = 'A' "
				+ " inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = erp_work_flow_process_notifications.erp_work_flow_process_id and erp_work_flow_process.record_status ='A' "
				+ " left join erp_notification_users_read on erp_notification_users_read.erp_notifications_id = erp_notifications.erp_notifications_id "
				+ " where erp_notifications.erp_users_id =:userId"
				+ " and erp_notifications.record_status='A' "
				+ " and now() between erp_notifications.notification_from_date_time and erp_notifications.notification_to_date_time "
				+ " and  notification_log_time< SUBDATE(date(now()) , INTERVAL 30 DAY) "
				+ " and erp_notifications.erp_notifications_id IN (:ids) and erp_work_flow_process.process_code IN (:code) "
				+ " group by erp_notifications.erp_users_id,erp_notifications.erp_notifications_id,erp_notification_users_read.erp_notification_users_read_id";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("userId", Integer.parseInt(userId));
			query.setParameter("code", code);
			query.setParameter("ids", ids);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public ErpNotificationUsersReadDBO getData(Integer id) {
		String str = " Select dbo from ErpNotificationUsersReadDBO dbo "
				+ " left join dbo.erpNotificationsDBO "
				+ " where dbo.recordStatus='A' and  dbo.erpNotificationsDBO.recordStatus='A' and  dbo.erpNotificationsDBO.id = :id";
		ErpNotificationUsersReadDBO dbo = sessionFactory.withSession(s -> {
			Mutiny.Query<ErpNotificationUsersReadDBO> query = s.createQuery(str,ErpNotificationUsersReadDBO.class);
			query.setParameter("id", id);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return dbo;
	}

	public void saveOrUpdate(ErpNotificationUsersReadDBO s) {
		sessionFactory.withTransaction((session, tx) -> session.merge(s)).await().indefinitely();	
	}
	
	public String getBlobObjectToString(Object object) {
		String msgBody= null;
		Object content = object;
		if (content instanceof String) {
			msgBody = (String) content;
		} else if (content instanceof Blob) {
			Blob blob = (Blob) content;
			byte[] bytes;
			try {
				bytes = blob.getBytes(1, (int) blob.length());
				msgBody = new String(bytes);
			} catch (SQLException e) {
				e.printStackTrace();
			}					
		}
		return msgBody;
	}
}
