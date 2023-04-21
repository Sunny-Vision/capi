package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.QuotationRecordDao;
import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.QuotationRecord;
import capi.entity.User;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * Notify corresponding user the assignment has been beyond deadline.
 * set the corresponding quotation record to IP and submit the assignment
 * 
 * Assignments/Quotations passing the deadline
 */
@Service("AssignmentDeadlineService")
public class AssignmentDeadlineService implements BatchJobService{
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private NotificationService notifyService;
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Assignment Deadline";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date previousDay = calendarEventDao.getPreviousNWorkdingDate(DateUtils.addDays(new Date(), -1),1).get(0);
		List<QuotationRecord> quotations = quotationRecordDao.getDeadlineQuotationRecords(today, previousDay);
		Hashtable<User, List<QuotationRecord>> userDeadlineLookup = new Hashtable<User, List<QuotationRecord>>();
		for (QuotationRecord quotation : quotations){
			if (userDeadlineLookup.containsKey(quotation.getUser())){
				userDeadlineLookup.get(quotation.getUser()).add(quotation);
				quotation.setStatus("Submitted");
				quotation.setAvailability(2);
				if(!quotation.getAssignment().isLockFirmStatus()){
					quotation.getAssignment().setStatus(9);
				}
//				Updated by User
//				quotation.getAssignment().setLockFirmStatus(true);
				quotation.setQuotationState("IP");
			}
			else{
				List<QuotationRecord> list = new ArrayList<QuotationRecord>();
				list.add(quotation);
				quotation.setStatus("Submitted");
				quotation.setAvailability(2);
				quotation.setQuotationState("IP");
				userDeadlineLookup.put(quotation.getUser(), list);
				if(!quotation.getAssignment().isLockFirmStatus()){
					quotation.getAssignment().setStatus(9);
				}
//				Updated by User
//				quotation.getAssignment().setLockFirmStatus(true);
			}
			
			quotation.setModifiedDate(quotation.getModifiedDate());
			quotation.setByPassModifiedDate(true);
			quotation.getAssignment().setModifiedDate(quotation.getAssignment().getModifiedDate());
			quotation.getAssignment().setByPassModifiedDate(true);
			Set<QuotationRecord> records = quotation.getOtherQuotationRecords();
			for (QuotationRecord record : records){
				record.setStatus("Submitted");
				quotation.setQuotationState("IP");
				record.setAvailability(2);
				record.setModifiedDate(record.getModifiedDate());
				record.setByPassModifiedDate(true);
			}			
		}
		
		for (User user : userDeadlineLookup.keySet()){
			// notification to user			
			Hashtable<Assignment, List<QuotationRecord>> assignmentQuotationLookup = new Hashtable<Assignment, List<QuotationRecord>>();
			List<QuotationRecord> userQuotations = userDeadlineLookup.get(user);
			StringBuilder detail = new StringBuilder();
			for (QuotationRecord record : userQuotations){
				if (assignmentQuotationLookup.containsKey(record.getAssignment())){
					assignmentQuotationLookup.get(record.getAssignment()).add(record);					
				}
				else{
					List<QuotationRecord> list = new ArrayList<QuotationRecord>();
					list.add(record);
					record.setStatus("Submitted");
					record.setAvailability(2);
					
					Outlet outlet = record.getAssignment().getOutlet();
					detail.append(outlet.getFirmCode() + "-" + outlet.getName());
					detail.append("\n");
					
					assignmentQuotationLookup.put(record.getAssignment(), list);
				}
			}
			int assignmentCnt = assignmentQuotationLookup.keySet().size();
			int quotationCnt = userQuotations.size();
			
			String content = messageSource.getMessage("N00017", new Object[]{assignmentCnt,quotationCnt,"\n"+detail.toString()}, Locale.ENGLISH);
			notifyService.sendNotification(user, "Assignment Deadline", content, false);
						
			// notification to supervisor
			String username = String.format("%s-%s", user.getStaffCode(), user.getChineseName());
			String subject = "Assignment Deadline for " + username;
			String content2 = messageSource.getMessage("N00018",
					new Object[]{assignmentCnt,quotationCnt,"\n"+detail.toString(),	username}, Locale.ENGLISH);
			notifyService.sendNotification(user, subject, content2, false);
		}
		
		quotationRecordDao.flushAndClearCache();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}

}
