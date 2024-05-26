package com.christ.erp.services.transactions.employee.appraisal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.appraisal.EmpAppraisalInstructionDBO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalInstructionDTO;

public class AppraisalInstructionsTransaction {
	private static volatile AppraisalInstructionsTransaction appraisalInstructionsTransaction = null;

	public static AppraisalInstructionsTransaction getInstance() {
		if (appraisalInstructionsTransaction == null) {
			appraisalInstructionsTransaction = new AppraisalInstructionsTransaction();
		}
		return appraisalInstructionsTransaction;
	}

	public boolean saveOrUpdate(EmpAppraisalInstructionDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				if (Utils.isNullOrEmpty(dbo.id)) {
					context.persist(dbo);
				} else {
					context.merge(dbo);
				}				
				return true;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getDuplicate(EmpAppraisalInstructionDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_appraisal_instruction_id from emp_appraisal_instruction where instruction_name=:instructionName" + 
						"  and record_status = 'A'";
				Query q = context.createNativeQuery(str, Tuple.class);
				q.setParameter("instructionName", data.instructionName.replace(" ", ""));
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getGridData() throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {			
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_appraisal_instruction.emp_appraisal_instruction_id as empAppraisalInstructionId,emp_appraisal_instruction.instruction_name as instructionName," 
						+" emp_appraisal_instruction.appraisal_type as appraisalType from emp_appraisal_instruction"  
						+" where emp_appraisal_instruction.record_status = 'A' order by emp_appraisal_instruction.instruction_name,emp_appraisal_instruction.appraisal_type";
				Query query = context.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> edit(EmpAppraisalInstructionDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {			
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_appraisal_instruction.emp_appraisal_instruction_id as empAppraisalInstructionId,emp_appraisal_instruction.instruction_name as instructionName," 
						+"	emp_appraisal_instruction.appraisal_type as appraisalType,emp_appraisal_instruction.instruction_content as instructionContent from emp_appraisal_instruction" 
						+" where emp_appraisal_instruction.emp_appraisal_instruction_id=:id and emp_appraisal_instruction.record_status = 'A' order by emp_appraisal_instruction.instruction_name,emp_appraisal_instruction.appraisal_type";
				Query q = context.createNativeQuery(str, Tuple.class);
				q.setParameter("id", data.id);
				return q.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public boolean delete(EmpAppraisalInstructionDTO data) throws Exception {
		return DBGateway.runJPA(new ICommitTransactional() {
			@Override
			public boolean onRun(EntityManager context) throws Exception {
				Query query = context.createNativeQuery("update emp_appraisal_instruction set record_status='D' where emp_appraisal_instruction_id=:id");
				query.setParameter("id", data.id);				
				return query.executeUpdate() > 0 ? true : false;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
}
