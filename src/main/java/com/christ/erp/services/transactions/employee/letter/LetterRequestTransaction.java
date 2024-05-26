package com.christ.erp.services.transactions.employee.letter;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestTypeDBO;
import com.christ.erp.services.dto.employee.letter.LetterRequestDTO;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class LetterRequestTransaction {

	private static volatile LetterRequestTransaction letterRequestTransaction = null;
	public static LetterRequestTransaction getInstance() {
		if(letterRequestTransaction==null) {
			letterRequestTransaction = new LetterRequestTransaction();
		}
		return  letterRequestTransaction;
	}

	public EmpLetterRequestDBO saveorUpdate(EmpLetterRequestDBO dbo, LetterRequestDTO dto) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<EmpLetterRequestDBO>() {
			@Override
			public EmpLetterRequestDBO onRun(EntityManager context) throws Exception {
				if(Utils.isNullOrEmpty(dto.id)) {
					context.persist(dbo);
				}
				else {
					context.merge(dbo);
				}
				return dbo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	

	public List<EmpLetterRequestDBO> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpLetterRequestDBO>>() {
			@Override
			public List <EmpLetterRequestDBO> onRun(EntityManager context) throws Exception  {
				Query query = context.createQuery("select bo from EmpLetterRequestDBO bo where bo.recordStatus = 'A'");
				List <EmpLetterRequestDBO> mappings = query.getResultList();
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public EmpLetterRequestDBO edit(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<EmpLetterRequestDBO>() {
			@Override
			public EmpLetterRequestDBO onRun(EntityManager context) throws Exception  {
				EmpLetterRequestDBO bo = context.find(EmpLetterRequestDBO.class, Integer.parseInt(id));
				return bo;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});  
	}

	public boolean delete(String headingId) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				EmpLetterRequestDBO heading = null;
				boolean flag=false;
				if (!Utils.isNullOrEmpty(headingId) && !Utils.isNullOrWhitespace(headingId)) {
					heading = context.find(EmpLetterRequestDBO.class, Integer.parseInt(headingId));
					if (heading.id != 0) {
						heading.recordStatus ='D';
						context.merge(heading);
						flag = true;
					}
				}	
				else {
					flag = false;
				}
				return flag;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}    

	public String getLetterType(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<String>() {
			@Override
			public String onRun(EntityManager context) throws Exception  {
				String letter=null;
				EmpLetterRequestTypeDBO bo = context.find(EmpLetterRequestTypeDBO.class, Integer.parseInt(id));
				if(bo!=null && bo.letterHelpText!=null && bo.letterHelpText!="") {
					letter = bo.letterHelpText;
				}
				return letter;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});  
	}

	public List<EmpLetterRequestDBO> getDuplicate(LetterRequestDTO data) throws Exception {	
		return DBGateway.runJPA(new ISelectGenericTransactional<List<EmpLetterRequestDBO>>() {
			@Override
			public List <EmpLetterRequestDBO> onRun(EntityManager context) throws Exception  {
				Query q = context.createQuery("select bo from EmpLetterRequestDBO bo where bo.empLetterRequestTypeDBO.id=:typeId and bo.empLetterRequestReasonDBO.id = :reasnId   and bo.recordStatus='A'");
				q.setParameter("typeId", Integer.parseInt(data.letterType.id));
				q.setParameter("reasnId", Integer.parseInt(data.reasonType.id));
				List <EmpLetterRequestDBO> list = q.getResultList();
				return list;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

}
