package com.christ.erp.services.transactions.common;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpUsersCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.SysFunctionDBO;
import com.christ.erp.services.dbobjects.common.SysMenuDBO;
import com.christ.erp.services.dbobjects.common.SysMenuModuleDBO;
import com.christ.erp.services.dbobjects.common.SysRoleDBO;
import com.christ.erp.services.dbobjects.common.SysRoleFunctionMapDBO;
import com.christ.erp.services.dbobjects.common.SysRoleGroupDBO;
import com.christ.erp.services.dbobjects.common.SysUserFunctionOverrideDBO;
import com.christ.erp.services.dbobjects.common.SysUserRoleMapDBO;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import com.christ.erp.services.dto.common.SysRoleDTO;
import reactor.core.publisher.Mono;

@Repository
public class PrivilegeSettingsTransaction {

	private static volatile PrivilegeSettingsTransaction privilegeSettingsTransaction = null;

	public static PrivilegeSettingsTransaction getInstance() {
		if (privilegeSettingsTransaction == null) {
			privilegeSettingsTransaction = new PrivilegeSettingsTransaction();
		}
		return privilegeSettingsTransaction;
	}

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Object[]> isCheckDuplicatedRole(SysRoleDTO data) throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Object[]> onRun(EntityManager context) throws Exception {
				List<Object[]> mappings = null;
				if(!Utils.isNullOrEmpty(data.roleName)) {
					StringBuffer sqlQuery  = new StringBuffer(" select * from sys_role where role_name=:roleName and record_status='A' ");
					if(!Utils.isNullOrEmpty(data.id)) {
						sqlQuery.append(" and sys_role_id!=:id");
					}
					if(!Utils.isNullOrEmpty(data.roleGroup) && !Utils.isNullOrEmpty(data.roleGroup.value)) {
						sqlQuery.append(" and sys_role_group_id=:roleGroupId ");
					}
					Query query = context.createNativeQuery(sqlQuery.toString());
					if(!Utils.isNullOrEmpty(data.id)) {
						query.setParameter("id", Integer.parseInt(data.id));
					}
					if(!Utils.isNullOrEmpty(data.roleGroup) && !Utils.isNullOrEmpty(data.roleGroup.value)) {
						query.setParameter("roleGroupId", Integer.parseInt(data.roleGroup.value.trim()));
					}
					query.setParameter("roleName", data.roleName.trim());
					mappings = query.getResultList();
				}
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}

	public SysRoleDBO editRole(String roleId) throws Exception {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<SysRoleDBO>() {
				@Override
				public SysRoleDBO onRun(EntityManager context) throws Exception {
					SysRoleDBO dbo = context.find(SysRoleDBO.class, Integer.parseInt(roleId));
					return dbo;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean saveOrUpdateRole(SysRoleDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
			@Override
			public Boolean onRun(EntityManager context){
				boolean flag = false;
				if(Utils.isNullOrEmpty(dbo.id)){
					context.persist(dbo);
					flag = true;
				}else{
					context.merge(dbo);
					flag = true;
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	//	public boolean deleteRole(String roleId, String userId) throws Exception {
	//		return DBGateway.runJPA(new ICommitTransactional() {
	//	        @Override
	//	        public boolean onRun(EntityManager context) throws Exception {
	//	            boolean flag = false;
	//	            SysRoleDBO syRoleDBO = null;
	//	            if(!Utils.isNullOrEmpty(roleId) && !Utils.isNullOrWhitespace(roleId)){
	//	            	syRoleDBO = context.find(SysRoleDBO.class,Integer.parseInt(roleId));
	//	               if(syRoleDBO.id != 0){
	//	            	   syRoleDBO.recordStatus = 'D';
	//	            	   syRoleDBO.modifiedUsersId = Integer.parseInt(userId);
	//	                   context.merge(syRoleDBO);
	//	                   flag = true;
	//	               }
	//	            }
	//	            return flag;
	//	        }
	//	        @Override
	//	        public void onError(Exception error) throws Exception {
	//	            throw error;
	//	        }
	//	    });
	//	}

	public ErpUsersDBO editUser(String userId) {	
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<ErpUsersDBO>() {		    
				@Override
				public 	ErpUsersDBO onRun(EntityManager context) throws Exception {
					try{
						Query query = context.createQuery("select distinct bo from ErpUsersDBO bo "
								+ " left join fetch bo.sysUserRoleMapDBOs cbo "
								+ " where bo.id=:id and (bo.recordStatus='A' or bo.recordStatus='I') and (cbo is null or cbo.recordStatus='A') order by cbo.sysRoleDBO asc");
						query.setParameter("id", Integer.parseInt(userId.trim()));
						ErpUsersDBO  sysRoleDBO = (ErpUsersDBO) Utils.getUniqueResult(query.getResultList());
						return sysRoleDBO;
					} catch(Exception e) {
						return null;
					}
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean saveOrUpdateUser(ErpUsersDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
			@Override
			public Boolean onRun(EntityManager context){
				boolean flag = false;
				if(Utils.isNullOrEmpty(dbo.id)){
					context.persist(dbo);
					flag = true;
				}else{
					context.merge(dbo);
					flag = true;
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Object[]> isCheckDuplicatedUser(ErpUsersDTO data)  throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Object[]> onRun(EntityManager context) throws Exception {
				List<Object[]> mappings = null;
				if(!Utils.isNullOrEmpty(data.userName)) {
					StringBuffer sqlQuery = new StringBuffer(" select e.erp_users_id as Id, e.user_name from erp_users e where (e.record_status='A' or e.record_status='I') and e.user_name=:userName ");
					if(!Utils.isNullOrEmpty(data.id)) {
						sqlQuery.append(" and e.erp_users_id!=:id ");
					}
					Query query = context.createNativeQuery(sqlQuery.toString());
					if(!Utils.isNullOrEmpty(data.id)) {
						query.setParameter("id", Integer.parseInt(data.id));
					}
					query.setParameter("userName", data.userName.trim());
					mappings = query.getResultList();
				}
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}
	
	public List<Object[]> getExistingUser(ErpUsersDTO data)  throws Exception {
		return  DBGateway.runJPA(new ISelectGenericTransactional<List<Object[]>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Object[]> onRun(EntityManager context) throws Exception {
				List<Object[]> mappings = null;
					StringBuffer sqlQuery = new StringBuffer("select erp_users.erp_users_id from erp_users where record_status = 'A' and emp_id = :empId");
					if(!Utils.isNullOrEmpty(data.id)) {
						sqlQuery.append(" and erp_users.erp_users_id != :id ");
					}
					Query query = context.createNativeQuery(sqlQuery.toString());
					if(!Utils.isNullOrEmpty(data.id)) {
						query.setParameter("id", Integer.parseInt(data.id));
					}
					query.setParameter("empId", Integer.parseInt(data.getEmployee().getValue()));
					mappings = query.getResultList();
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});	
	}

	//	public boolean deleteUser(String idUser, String userId) throws Exception {
	//		return DBGateway.runJPA(new ICommitTransactional() {
	//	        @Override
	//	        public boolean onRun(EntityManager context) throws Exception {
	//	            boolean flag = false;
	//	            ErpUsersDBO erpUsersDBO = null;
	//	            if(!Utils.isNullOrEmpty(idUser) && !Utils.isNullOrWhitespace(idUser)){
	//	            	erpUsersDBO = context.find(ErpUsersDBO.class,Integer.parseInt(idUser));
	//	               if(erpUsersDBO.id != 0){
	//	            	   erpUsersDBO.recordStatus = 'D';
	//	            	   erpUsersDBO.modifiedUsersId = Integer.parseInt(userId);
	//	                   context.merge(erpUsersDBO);
	//	                   flag = true;
	//	               }
	//	            }
	//	            return flag;
	//	        }
	//	        @Override
	//	        public void onError(Exception error) throws Exception {
	//	            throw error;
	//	        }
	//	    });
	//	}

	public SysRoleDBO editPermissionGratedToRole(String roleId) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<SysRoleDBO>() {
				@Override
				public 	SysRoleDBO onRun(EntityManager context) throws Exception {
					try{
						Query query = context.createQuery(" select distinct dbo  from SysRoleDBO dbo where dbo.id=:id  and dbo.recordStatus='A'");
						query.setParameter("id", Integer.parseInt(roleId.trim()));
						SysRoleDBO  sysRoleDBO = (SysRoleDBO) Utils.getUniqueResult(query.getResultList());
						return sysRoleDBO;
					} catch(Exception e) {
						return null;
					}
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<SysMenuDBO> getAllSysMenuAndFuctions() {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<List<SysMenuDBO>>() {
				@SuppressWarnings("unchecked")
				@Override
				public 	List<SysMenuDBO> onRun(EntityManager context) throws Exception {
					Query query = context.createQuery(" from SysMenuDBO bo inner join fetch bo.sysMenuModuleSubDBO.sysMenuModuleDBO cbo "
							+ " where bo.recordStatus='A' and (cbo is not null and cbo.recordStatus='A' and bo.sysMenuModuleSubDBO.recordStatus = 'A') "
							+ " order by cbo.moduleDisplayOrder asc ");
					List<SysMenuDBO>  sysRoleFunctionMapDBOs = query.getResultList();
					return sysRoleFunctionMapDBOs;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean saveOrUpdatePermissionsToRole(SysRoleDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
			@Override
			public Boolean onRun(EntityManager context) {
				boolean flag = false;
				if(!Utils.isNullOrEmpty(dbo)){
					context.merge(dbo);
					flag = true;
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<SysUserRoleMapDBO> getCampusListAvailbleToUser(String id) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<List<SysUserRoleMapDBO>>() {
				@SuppressWarnings("unchecked")
				@Override
				public 	List<SysUserRoleMapDBO> onRun(EntityManager context) throws Exception {
					Query query = context.createQuery(" from SysUserRoleMapDBO bo where bo.recordStatus='A' and bo.erpUsersDBO.id=:id and (bo.erpUsersDBO.recordStatus='A' or bo.erpUsersDBO.recordStatus='I')");
					query.setParameter("id", Integer.parseInt(id.trim()));
					List<SysUserRoleMapDBO>  sysRoleFunctionMapDBOs = query.getResultList();
					return sysRoleFunctionMapDBOs;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean saveOrUpdatePermissionsToUser(List<SysUserFunctionOverrideDBO> userFunctionOverrideDBOs) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<Boolean>() {
			@Override
			public Boolean onRun(EntityManager context){
				boolean flag = false;
				if(!Utils.isNullOrEmpty(userFunctionOverrideDBOs)){
					for (SysUserFunctionOverrideDBO dbo : userFunctionOverrideDBOs) {
						if(Utils.isNullOrEmpty(dbo.id)) {
							context.persist(dbo);
							flag = true;
						}else {
							context.merge(dbo);
							flag = true;
						}
					}
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<SysUserFunctionOverrideDBO> editAditionalPermissionGratedToUser(String idForSave, Set<Integer> functionIds) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<List<SysUserFunctionOverrideDBO>>() {
				@SuppressWarnings("unchecked")
				@Override
				public 	List<SysUserFunctionOverrideDBO> onRun(EntityManager context) throws Exception {
					StringBuffer stringBuffer = new StringBuffer(" from SysUserFunctionOverrideDBO bo where bo.erpUsersDBO.id=:id and bo.recordStatus='A'");
					if(!Utils.isNullOrEmpty(functionIds)) {
						stringBuffer.append(" and bo.sysFunctionDBO.id in (:functionIds)");
					}
					stringBuffer.append(" order by bo.sysFunctionDBO.id asc");
					Query query = context.createQuery(stringBuffer.toString());
					query.setParameter("id", Integer.parseInt(idForSave.trim()));
					if(!Utils.isNullOrEmpty(functionIds)) {
						query.setParameter("functionIds", functionIds);
					}
					List<SysUserFunctionOverrideDBO>  sysRoleFunctionMapDBOs = query.getResultList();
					return sysRoleFunctionMapDBOs;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<SysUserRoleMapDBO> getSysRoleFunctionMap(String userid) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<List<SysUserRoleMapDBO>>() {
				@SuppressWarnings("unchecked")
				@Override
				public 	List<SysUserRoleMapDBO> onRun(EntityManager context) throws Exception {
					Query query = context.createQuery(" from SysRoleDBO bo left join fetch bo.sysRoleFunctionMapDBOs cbo where bo.recordStatus='A' and (cbo is null or cbo.erpUsersDBO.id=:id and cbo.recordStatus='A')  ");
					query.setParameter("id", Integer.parseInt(userid.trim()));
					List<SysUserRoleMapDBO>  sysRoleFunctionMapDBOs = query.getResultList();
					return sysRoleFunctionMapDBOs;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<SysUserRoleMapDBO> getUserRoleMapDBOs(String roleId, String userId) {
		try {
			return DBGateway.runJPA(new ISelectGenericTransactional<List<SysUserRoleMapDBO>>() {
				@SuppressWarnings("unchecked")
				@Override
				public 	List<SysUserRoleMapDBO> onRun(EntityManager context) throws Exception {
					StringBuffer sb = new StringBuffer("from SysUserRoleMapDBO bo where bo.recordStatus='A'");
					if(!Utils.isNullOrEmpty(roleId)) {
						sb.append(" and bo.sysRoleDBO.id=:roleId ");
					}
					if(!Utils.isNullOrEmpty(userId)) {
						sb.append(" and bo.erpUsersDBO.id=:userId ");
					}
					Query query = context.createQuery(sb.toString());
					if(!Utils.isNullOrEmpty(roleId)) {
						query.setParameter("roleId", Integer.parseInt(roleId.trim()));
					}
					if(!Utils.isNullOrEmpty(userId)) {
						query.setParameter("userId", Integer.parseInt(userId.trim()));
					}
					List<SysUserRoleMapDBO>  userRoleMapDBOs = query.getResultList();
					return userRoleMapDBOs;
				}
				@Override
				public void onError(Exception error) throws Exception {
					throw error;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteRoleAssignedToUsers(List<SysUserRoleMapDBO> dboList, String userId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				boolean flag = false;
				if(!Utils.isNullOrEmpty(dboList)){
					for (SysUserRoleMapDBO sysUserRoleMapDBO : dboList) {
						sysUserRoleMapDBO.recordStatus = 'D';
						sysUserRoleMapDBO.modifiedUsersId = Integer.parseInt(userId);
						context.merge(sysUserRoleMapDBO);
						flag = true;
					}
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<SysFunctionDBO> getMenuFunctions(String menuId) {
		String query = " select dbo from SysFunctionDBO dbo"
				+ " where dbo.recordStatus = 'A' and dbo.sysMenuDBO.id = :id";
		return sessionFactory.withSession(s -> s.createQuery(query, SysFunctionDBO.class)
				.setParameter("id", Integer.parseInt(menuId)).getResultList()).await().indefinitely();
	}

	public Mono<Boolean> deleteRole(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(SysRoleDBO.class, id)
				.chain(dbo -> session.fetch(dbo.getSysRoleFunctionMapDBOs())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
							dbo.setRecordStatus('D');
							dbo.setModifiedUsersId(userId);
						})	
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public Mono<Boolean> deleteUser(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(ErpUsersDBO.class, id)
				.chain(dbo -> session.fetch(dbo.getSysUserRoleMapDBOs())
						.invoke(subSet -> {
							subSet.forEach(subDbo -> {
								subDbo.setRecordStatus('D');
								subDbo.setModifiedUsersId(userId);
							});
						})
						.chain(dbo1 -> session.fetch(dbo.getSysUserFunctionOverrideDBOSet()))
						.invoke(subSet1 -> {
							subSet1.forEach(subDbo1 -> {
								subDbo1.setRecordStatus('D');
								subDbo1.setModifiedUsersId(userId);
							});
						}).chain(dbo2 -> session.fetch(dbo.getErpUsersCampusDBOSet()))	
						  .invoke(subSet2 -> {
							  subSet2.forEach(subDbo2 -> {
									subDbo2.setRecordStatus('D');
									subDbo2.setModifiedUsersId(userId);
							  });
								dbo.setRecordStatus('D');
								dbo.setModifiedUsersId(userId);
						  })
						)).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public Mono<List<SysMenuModuleDBO>> getMenus( ) {
		String query = " select distinct dbo from SysMenuModuleDBO dbo"
				+ " inner join fetch dbo.sysMenuModuleSubDBOs smm "
				+ " inner join fetch smm.sysMenuDBOs sm "
				+ " inner join fetch sm.sysFunctionDBOSet sf "
				+ " where dbo.recordStatus = 'A' and smm.recordStatus = 'A' and sm.recordStatus = 'A' and sf.recordStatus = 'A' ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, SysMenuModuleDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public List<SysMenuDBO> getMenuRoles(String menuId ) {
		String query = " select distinct dbo from SysMenuDBO dbo"
				+ " left join fetch dbo.sysFunctionDBOSet sf"
				+ " left join fetch sf.SysRoleFunctionMapDBOSet srfm"
				+ " left join fetch sf.SysUserFunctionOverrideDBOSet sufo "
				+ " where dbo.recordStatus = 'A'  and dbo.id = :id";
		return sessionFactory.withSession(s -> s.createQuery(query, SysMenuDBO.class)
				.setParameter("id", Integer.parseInt(menuId)).getResultList()).await().indefinitely();
	}

	public List<SysRoleDBO> getRoles(String menuId ) {
		String query ="select distinct dbo from SysRoleDBO dbo "
				+ " inner join fetch dbo.sysRoleFunctionMapDBOs srf"
				+ " where dbo.recordStatus = 'A' and srf.sysFunctionDBO.sysMenuDBO.id = :menuId and srf.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleDBO.class).setParameter("menuId", Integer.parseInt(menuId)).getResultList()).await().indefinitely();
	}

	public List<SysRoleGroupDBO> getRoleGroup(String roleGroupId) {
		String query ="select distinct dbo from SysRoleGroupDBO dbo "
				+ " inner join fetch dbo.sysRoleDBOSet sr"
				+ " where dbo.recordStatus = 'A' and sr.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(roleGroupId)) {
			query += " and dbo.id = :id";
		}
		String finalquery = query;
		List<SysRoleGroupDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysRoleGroupDBO> query1 = s.createQuery(finalquery, SysRoleGroupDBO.class);
		if(!Utils.isNullOrEmpty(roleGroupId)) {
			query1.setParameter("id", Integer.parseInt(roleGroupId));
		} 
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpUsersDBO> getUsers(String menuId ) {
		String query ="select distinct dbo from ErpUsersDBO dbo "
				+ " left join fetch dbo.sysUserFunctionOverrideDBOSet sufo"
				+ " left join fetch dbo.sysUserRoleMapDBOs sur"
				+ " left join fetch sur.sysRoleDBO sr"
				+ " left join fetch sr.sysRoleFunctionMapDBOs srfm"
				+ " where dbo.recordStatus = 'A'"
				+ " and  srfm.sysFunctionDBO.sysMenuDBO.id = :menuId";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpUsersDBO.class).setParameter("menuId", Integer.parseInt(menuId)).getResultList()).await().indefinitely();
	}
	public List<ErpUsersDBO> getUsersFunctionOverride(String menuId ) {
		String query ="select distinct dbo from ErpUsersDBO dbo "
				+ " left join fetch dbo.sysUserFunctionOverrideDBOSet sufo"
				+ " left join fetch dbo.sysUserRoleMapDBOs sur"
				+ " left join fetch sur.sysRoleDBO sr"
				+ " left join fetch sr.sysRoleFunctionMapDBOs srfm"
				+ " where dbo.recordStatus = 'A' and sufo.sysFunctionDBO.sysMenuDBO.id = :menuId";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpUsersDBO.class).setParameter("menuId", Integer.parseInt(menuId)).getResultList()).await().indefinitely();
	}

	public List<SysRoleFunctionMapDBO> getRolefunctions(String roleId, String menuId ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " inner join fetch dbo.sysRoleDBO sr"
				+ " left join fetch sr.SysUserRoleMapDBOSet surm"
				+ " where dbo.recordStatus = 'A'  and dbo.sysFunctionDBO.sysMenuDBO.id = :menuId and sr.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(roleId)) {
			query +=" and dbo.sysRoleDBO.id = :id";
		}
		String finalquery = query;
		List<SysRoleFunctionMapDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysRoleFunctionMapDBO> query1 = s.createQuery(finalquery, SysRoleFunctionMapDBO.class);
		if(!Utils.isNullOrEmpty(roleId)) {
			query1.setParameter("id", Integer.parseInt(roleId));
		} 
		query1.setParameter("menuId", Integer.parseInt(menuId));
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void saveRoles(List<SysRoleFunctionMapDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}

	public void saveUsers(List<SysUserFunctionOverrideDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}

	public List<SysUserFunctionOverrideDBO> getUserfunctions(Set<Integer> userIds, String menuId ) {
		String query ="select distinct dbo from SysUserFunctionOverrideDBO dbo "
				+ "where dbo.recordStatus = 'A' and dbo.sysFunctionDBO.sysMenuDBO.id = :menuId";
		if(!Utils.isNullOrEmpty(userIds)) {
			query += " and dbo.erpUsersDBO.id in (:id)";
		}
		String finalquery = query;
		List<SysUserFunctionOverrideDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysUserFunctionOverrideDBO> query1 = s.createQuery(finalquery, SysUserFunctionOverrideDBO.class);
		if(!Utils.isNullOrEmpty(userIds)) {
			query1.setParameter("id", userIds);
		} 
		query1.setParameter("menuId", Integer.parseInt(menuId));
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<ErpUsersDBO> getUsersForFunctions(String functionId ) {
		String query ="select distinct dbo from ErpUsersDBO dbo "
				//				+ " inner join fetch dbo.sysUserFunctionOverrideDBOSet sufo"
				+ " left join fetch dbo.sysUserRoleMapDBOs sur"
				+ " left join fetch sur.sysRoleDBO sr"
				+ " left join fetch sr.sysRoleFunctionMapDBOs srfm"
				+ " where dbo.recordStatus = 'A' and  "
				+ " srfm.sysFunctionDBO.id = :functionId";
//				+ " and sur.recordStatus = 'A' and srfm.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpUsersDBO.class).setParameter("functionId", Integer.parseInt(functionId)).getResultList()).await().indefinitely();
	}

	public List<SysRoleFunctionMapDBO> getRolesForFunction( String functionId ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " inner join fetch dbo.sysRoleDBO sr"
				+ " inner join fetch sr.SysUserRoleMapDBOSet srfm"
				+ " where dbo.recordStatus = 'A' and dbo.sysFunctionDBO.id = :functionId and sr.recordStatus = 'A' and  srfm.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleFunctionMapDBO.class).setParameter("functionId", Integer.parseInt(functionId))
				.getResultList()).await().indefinitely();
	}
	
	public List<SysRoleFunctionMapDBO> getRolesForFunction1( String functionId ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " inner join fetch dbo.sysRoleDBO sr"
				+ " where dbo.recordStatus = 'A' and dbo.sysFunctionDBO.id = :functionId and sr.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleFunctionMapDBO.class).setParameter("functionId", Integer.parseInt(functionId))
				.getResultList()).await().indefinitely();
	}

	public List<SysUserFunctionOverrideDBO> getUsersForFunction( Set<Integer> userIds, String functionId ) {
		String query ="select distinct dbo from SysUserFunctionOverrideDBO dbo "
				+ "where dbo.recordStatus = 'A' and dbo.sysFunctionDBO.id = :functionId ";
		if(!Utils.isNullOrEmpty(userIds)) {
			query += " and dbo.erpUsersDBO.id in (:id)";
		}
		String finalquery = query;
		List<SysUserFunctionOverrideDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysUserFunctionOverrideDBO> query1 = s.createQuery(finalquery, SysUserFunctionOverrideDBO.class);
		if(!Utils.isNullOrEmpty(userIds)) {
			query1.setParameter("id", userIds);
		} 
		query1.setParameter("functionId",  Integer.parseInt(functionId));
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
//		return sessionFactory.withSession(s -> s.createQuery(finalquery, SysUserFunctionOverrideDBO.class).setParameter("functionId", Integer.parseInt(functionId))
//				.getResultList()).await().indefinitely();
	}

	public Mono<List<Tuple>> getUserFilter(String empIdOrName, boolean isActive, boolean isNumeric) {
		String query =" select erp_users.erp_users_id ,erp_users.user_name ,emp.emp_name  from erp_users"
				+ " left join emp ON emp.emp_id = erp_users.emp_id and emp.record_status = 'A'"
				+ " where erp_users.record_status = :isActive ";
		if(isNumeric) {
			query += " and emp.emp_no = :empIdOrName";
		} else {
			query += " and (emp.emp_name = :empIdOrName or erp_users.user_name = :empIdOrName) ";
		}
		String finalquery = query;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalquery, Tuple.class);
		if(isNumeric) {
			query1.setParameter("empIdOrName", Integer.parseInt(empIdOrName));
		} else {
			query1.setParameter("empIdOrName", empIdOrName);
		} 
		if(isActive) {
			query1.setParameter("isActive", 'A');
		} else {
			query1.setParameter("isActive", 'I');
		}
		return  query1.getResultList();
		}).subscribeAsCompletionStage());
		return list;
	}

	public List<SysRoleFunctionMapDBO> getfunctionsForRole(String roleId,  List<Integer> functionIds ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ "where dbo.recordStatus = 'A' and dbo.sysRoleDBO.id = :id and dbo.sysFunctionDBO.id in (:menuId)";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleFunctionMapDBO.class).setParameter("id", Integer.parseInt(roleId))
				.setParameter("menuId", functionIds).getResultList()).await().indefinitely();
	}

	public List<SysRoleFunctionMapDBO> getfunctionsForRoleGroup(List<Integer> roleGroupId,  List<Integer> functionIds ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " where dbo.recordStatus = 'A' and dbo.sysFunctionDBO.id in (:menuId)";
		if(!Utils.isNullOrEmpty(roleGroupId)) {
			query +=" and dbo.sysRoleDBO.sysRoleGroup.id in(:roleGroupId)";
		}
		String finalquery = query;
		List<SysRoleFunctionMapDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysRoleFunctionMapDBO> query1 = s.createQuery(finalquery, SysRoleFunctionMapDBO.class);
		if(!Utils.isNullOrEmpty(roleGroupId)) {
			query1.setParameter("roleGroupId", roleGroupId);
		} 
		query1.setParameter("menuId", functionIds);
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<SysRoleDBO> getRolesForGroups(List<Integer> roleGroupId ) {
		String query ="select distinct dbo from SysRoleDBO dbo "
				+ " left join fetch dbo.sysRoleFunctionMapDBOs srf "
				+ " where dbo.recordStatus = 'A' and dbo.sysRoleGroup.id in (:groupId)";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleDBO.class).setParameter("groupId", roleGroupId).getResultList()).await().indefinitely();
	}

	public List<SysRoleGroupDBO> getRoleGroupForFunction(String functionId) {
		String query = " select distinct dbo from SysRoleGroupDBO dbo"
				+ " inner join fetch dbo.sysRoleDBOSet sr"
				+ " inner join fetch sr.sysRoleFunctionMapDBOs srf"
				+ " where dbo.recordStatus = 'A' and sr.recordStatus = 'A' and srf.sysFunctionDBO.id = :id and srf.recordStatus = 'A'";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleGroupDBO.class)
				.setParameter("id", Integer.parseInt(functionId)).getResultList()).await().indefinitely();
	}

	//	public List<SysUserFunctionOverrideDBO> getfunctionsForUser(String menuUserId,  List<Integer> functionIds ) {
	//		String query ="select distinct dbo from SysUserFunctionOverrideDBO dbo "
	//				+ "	where dbo.recordStatus = 'A' and dbo.erpUsersDBO.id = :userId and dbo.sysFunctionDBO.id in (:functionId) ";
	//		return sessionFactory.withSession(s -> s.createQuery(query, SysUserFunctionOverrideDBO.class).setParameter("userId", Integer.parseInt(menuUserId)).setParameter("functionId", functionIds)
	//				.getResultList()).await().indefinitely();
	//	}

	public Mono<List<SysMenuModuleDBO>> geFilltertMenuRoles(List<Integer> menuId) {
		String query = " select distinct dbo from SysMenuModuleDBO dbo"
				+ " inner join fetch dbo.sysMenuModuleSubDBOs smm "
				+ " inner join fetch smm.sysMenuDBOs sm "
				+ " inner join fetch sm.sysFunctionDBOSet sf "
				+ " where dbo.recordStatus = 'A' and smm.recordStatus = 'A' and sm.recordStatus = 'A' and sf.recordStatus = 'A' and sm.id in (:menuId) ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(query, SysMenuModuleDBO.class)
				.setParameter("menuId", menuId).getResultList()).subscribeAsCompletionStage());
	}

	public List<SysUserFunctionOverrideDBO> getUsersAdditionalPermissions(List<Integer> usersIds) {
		String query = "select distinct dbo from SysUserFunctionOverrideDBO dbo "
				+ "	where dbo.recordStatus = 'A' and dbo.erpUsersDBO.id in (:usersIds) "; 
		return sessionFactory.withSession(s -> s.createQuery(query, SysUserFunctionOverrideDBO.class).setParameter("usersIds", usersIds).getResultList()).await().indefinitely();
	}

	public List<SysRoleFunctionMapDBO> getAllfunctionWithRole() {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo where dbo.recordStatus = 'A' ";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleFunctionMapDBO.class).getResultList()).await().indefinitely();
	}

	public List<SysUserFunctionOverrideDBO> getUserCommonAdditionalPermissions(Set<Integer> usersIds, Set<Integer> functionsList) {
		String query = "select distinct dbo from SysUserFunctionOverrideDBO dbo "
				+ "	where dbo.recordStatus = 'A' and dbo.erpUsersDBO.id in (:usersIds) and dbo.sysFunctionDBO.id in (:functionsList)"; 
		return sessionFactory.withSession(s -> s.createQuery(query, SysUserFunctionOverrideDBO.class).setParameter("usersIds", usersIds)
				.setParameter("functionsList", functionsList)
				.getResultList()).await().indefinitely();
	}
	
	public List<SysRoleFunctionMapDBO> getFunctionsForRole(String roleId) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo where dbo.recordStatus = 'A' and dbo.sysRoleDBO.id = :roleId ";
		return sessionFactory.withSession(s -> s.createQuery(query, SysRoleFunctionMapDBO.class).setParameter("roleId", Integer.parseInt(roleId)).getResultList()).await().indefinitely();
	}
	
	public List<SysRoleFunctionMapDBO> getRolefunctions1(List<Integer> roleId, String menuId ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " inner join fetch dbo.sysRoleDBO sr"
				+ " inner join fetch sr.SysUserRoleMapDBOSet surm"
				+ " where dbo.recordStatus = 'A'  and dbo.sysFunctionDBO.sysMenuDBO.id = :menuId and sr.recordStatus = 'A' and surm.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(roleId)) {
			query +=" and dbo.sysRoleDBO.id in(:id)";
		}
		String finalquery = query;
		List<SysRoleFunctionMapDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysRoleFunctionMapDBO> query1 = s.createQuery(finalquery, SysRoleFunctionMapDBO.class);
		if(!Utils.isNullOrEmpty(roleId)) {
			query1.setParameter("id", roleId);
		} 
		query1.setParameter("menuId", Integer.parseInt(menuId));
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<SysRoleFunctionMapDBO> getRolefunctionList(List<Integer> roleId, String menuId ) {
		String query ="select distinct dbo from SysRoleFunctionMapDBO dbo "
				+ " where dbo.recordStatus = 'A'  and dbo.sysFunctionDBO.sysMenuDBO.id = :menuId";
		if(!Utils.isNullOrEmpty(roleId)) {
			query +=" and dbo.sysRoleDBO.id in(:id)";
		}
		String finalquery = query;
		List<SysRoleFunctionMapDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<SysRoleFunctionMapDBO> query1 = s.createQuery(finalquery, SysRoleFunctionMapDBO.class);
		if(!Utils.isNullOrEmpty(roleId)) {
			query1.setParameter("id", roleId);
		} 
		query1.setParameter("menuId", Integer.parseInt(menuId));
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<ErpUsersCampusDBO> getUserCampus(Set<Integer> userId) {
		String query ="select distinct dbo from ErpUsersCampusDBO dbo"
				+ " where dbo.recordStatus =  'A' and dbo.erpUsersDBO.id in(:userId)";
		return sessionFactory.withSession(s -> s.createQuery(query, ErpUsersCampusDBO.class).setParameter("userId", userId).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getUserRoleCampus(String userId) {
		String query ="select erp_users_id as userId ,erp_campus_id as campusId from sys_user_role_map where record_status = 'A' and erp_users_id = :userId ";
		return sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();
	}
	
	public List<SysUserFunctionOverrideDBO> getUserCampusForAddPermission(String userId) {
		String query ="select distinct dbo from SysUserFunctionOverrideDBO dbo"
				+ " where dbo.recordStatus =  'A' and dbo.erpUsersDBO.id = :userId";
		return sessionFactory.withSession(s -> s.createQuery(query, SysUserFunctionOverrideDBO.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();
	}
	
	public void saveOrUpdateUserCampus(List<ErpUsersCampusDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}
	
	public List<Tuple> getUserRoleCampus1(Set<Integer> userId) {
		String query ="select erp_users_id as userId ,erp_campus_id as campusId from sys_user_role_map where record_status = 'A' and erp_users_id in (:userId) ";
		return sessionFactory.withSession(s -> s.createNativeQuery(query, Tuple.class).setParameter("userId", userId).getResultList()).await().indefinitely();
	}

}
