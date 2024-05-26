package com.christ.erp.services.transactions.hostel.settings;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dto.hostel.settings.HostelRoomTypeDTO;

public class RoomTypeTransaction {
	
	private static volatile RoomTypeTransaction roomTypeTransaction = null;

    public static RoomTypeTransaction getInstance() {
        if(roomTypeTransaction==null) {
        	roomTypeTransaction = new RoomTypeTransaction();
        }
        return  roomTypeTransaction;
    }
    
    public List<HostelRoomTypeDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<HostelRoomTypeDBO>>() {
			@SuppressWarnings("unchecked")
			@Override
	        public List<HostelRoomTypeDBO> onRun(EntityManager context) throws Exception {
				String str ="from HostelRoomTypeDBO where recordStatus='A' order by id" ;
			    Query qry = context.createQuery(str,HostelRoomTypeDBO.class);
				return qry.getResultList();
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
    
    public HostelRoomTypeDBO getRoomTypeDBO(int id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<HostelRoomTypeDBO>() {
			@Override
			public HostelRoomTypeDBO onRun(EntityManager context) throws Exception {
				return context.find(HostelRoomTypeDBO.class, id);
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
    
    public boolean saveOrUpdate(HostelRoomTypeDBO hosteRoomTypeDBO) throws Exception{
		return DBGateway.runJPA(new ICommitTransactional() {
            @Override
            public boolean onRun(EntityManager context) throws Exception {
                if(Utils.isNullOrEmpty(hosteRoomTypeDBO) || Utils.isNullOrEmpty(hosteRoomTypeDBO.id) || hosteRoomTypeDBO.id==0) {
                    context.persist(hosteRoomTypeDBO);
                }
                else {
                    context.merge(hosteRoomTypeDBO);
                } 
                return true;
            }
            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
	}
    
    public Boolean isDuplicate(HostelRoomTypeDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Boolean>() {
			@Override
	        public Boolean onRun(EntityManager context) throws Exception {
				String str ="from HostelRoomTypeDBO where recordStatus='A' and roomType=:roomType and hostelDBO.id=:hostelId" ;
				if(data.id != null && !data.id.isEmpty()) {
					str += " and id!=:id ";
				}
			    Query qry = context.createQuery(str,HostelRoomTypeDBO.class);
			    qry.setParameter("roomType", data.roomType.trim());
			    qry.setParameter("hostelId", Integer.parseInt(data.hostel.value));
			    if(data.id != null && !data.id.isEmpty()) {
				    qry.setParameter("id", Integer.parseInt(data.id));
				}
			    if(qry.getResultList().size()>0) {
			    	return true;
			    }
		        return false;
	        }
	        @Override
	        public void onError(Exception error) throws Exception {
	        	throw error;
	        }
	    });
	}
}
