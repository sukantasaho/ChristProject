package com.christ.erp.services.transactions.hostel.settings;

import javax.persistence.EntityManager;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelBlockUnitDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFloorDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomsDBO;

public class RoomMasterTransaction {
	
	private static volatile RoomMasterTransaction roomMasterTransaction = null;

    public static RoomMasterTransaction getInstance() {
        if(roomMasterTransaction==null) {
        	roomMasterTransaction = new RoomMasterTransaction();
        }
        return  roomMasterTransaction;
    }
    
    public HostelFloorDBO getFloorDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelFloorDBO>() {
			@Override
			public HostelFloorDBO onRun(EntityManager context) throws Exception {
				return context.find(HostelFloorDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public HostelRoomsDBO getHostelRoomDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelRoomsDBO>() {
			@Override
			public HostelRoomsDBO onRun(EntityManager context) throws Exception {
				return context.find(HostelRoomsDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(HostelBlockUnitDBO data) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(data) || Utils.isNullOrEmpty(data.id) || data.id==0) {
                    context.persist(data);
                }
                else {
                    context.merge(data);
                } 
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
}
