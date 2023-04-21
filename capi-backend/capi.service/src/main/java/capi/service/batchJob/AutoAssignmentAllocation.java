package capi.service.batchJob;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentDao;
import capi.dal.QuotationRecordDao;
import capi.entity.AllocationBatch;
import capi.entity.Assignment;
import capi.entity.QuotationRecord;
import capi.entity.User;
import capi.service.CommonService;

/**
 * If allocation has not been done when the allocation batch start date is reached, 
 * batch program will allocate all assignments to related district head automatically. 
 * @author stanley_tsang
 *
 */
@Service("AutoAssignmentAllocation")
public class AutoAssignmentAllocation implements BatchJobService{
	
	@Autowired
	private CommonService commonService;
		
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
		
	@Autowired
	private AssignmentDao assignmentDao;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Auto Assignment Allocation";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		//Date yesterday = DateUtils.addDays(today, -1);
		AllocationBatch allocationBatch = allocationBatchDao.getUnallocatedAllocationBatchByStartDate(today);
		if (allocationBatch != null){
			List<QuotationRecord> quotationRecords = quotationRecordDao.getQuotationRecordByAllocationBatch(allocationBatch);
			for (QuotationRecord quotationRecord : quotationRecords){
				Assignment assignment = quotationRecord.getAssignment();
				User user = assignment.getOutlet().getTpu().getDistrict().getUser();
				quotationRecord.setUser(user);
				assignment.setUser(user);
				
				quotationRecord.setModifiedDate(quotationRecord.getModifiedDate());
				quotationRecord.setByPassModifiedDate(true);
				assignment.setModifiedDate(assignment.getModifiedDate());
				assignment.setByPassModifiedDate(true);
				quotationRecordDao.save(quotationRecord);
				assignmentDao.save(assignment);
			}	
			allocationBatchDao.flush();		
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
