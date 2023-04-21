package capi.service.batchJob;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.batch.QRDelinkReminderModel;
import capi.service.CommonService;
import capi.service.NotificationService;

@Service("QuotationRecordDelinkReminderService")
public class QuotationRecordDelinkReminderService implements BatchJobService {

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CalendarEventDao calendarEventDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private NotificationService notifyService;

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Quotation Record Delink";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date refMonth = commonService.getReferenceMonth(new Date());	
		Date nextMonth = DateUtils.addMonths(refMonth, 1);
		
		Date nextMonth2YearsAgo = DateUtils.addYears(nextMonth, -2);
		
		Date next2Month = DateUtils.addMonths(nextMonth, 1);
		Date next2Month2YearsAgo = DateUtils.addYears(next2Month, -2);
		
		List<QRDelinkReminderModel> notDeletedQRs = quotationRecordDao.getNotDeletedQRBeforeReferenceMonth(nextMonth2YearsAgo);
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		String referenceMonth = "";
		int count = 0;
		if(notDeletedQRs != null && notDeletedQRs.size() > 0) {
			referenceMonth = notDeletedQRs.get(0).getReferenceMonth();
			count = 1;
			for(int i = 1; i < notDeletedQRs.size() && referenceMonth.length() > 0; i++) {
				QRDelinkReminderModel notDeletedQR = notDeletedQRs.get(i);
				
				if(i == notDeletedQRs.size() - 1) {
					if(referenceMonth.equals(notDeletedQR.getReferenceMonth())) {
						count++;
						Date month = commonService.getReferenceMonth(commonService.getDate(referenceMonth));
						String monthStr = commonService.formatMonth(month);
						map.put(monthStr, count);
					} else {
						Date month = commonService.getReferenceMonth(commonService.getDate(referenceMonth));
						String monthStr = commonService.formatMonth(month);
						map.put(monthStr, count);
						referenceMonth = notDeletedQR.getReferenceMonth();
						month = commonService.getReferenceMonth(commonService.getDate(referenceMonth));
						monthStr = commonService.formatMonth(month);
						count = 1;
						map.put(monthStr, count);
					}
					break;
				}
				
				if(referenceMonth.equals(notDeletedQR.getReferenceMonth())) {
					count++;
				} else {
					Date month = commonService.getReferenceMonth(commonService.getDate(referenceMonth));
					String monthStr = commonService.formatMonth(month);
					map.put(monthStr, count);
					referenceMonth = notDeletedQR.getReferenceMonth();
					count = 1;
				}
			}
		}
		
		String delinkNotDeletedURL = "";
		boolean start = true;
		if(map != null && map.size() > 0) {
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				delinkNotDeletedURL += "<tr>";
				if(start) {
					delinkNotDeletedURL += "<td>Quotation Record expired</td>";
					start = false;
				} else {
					delinkNotDeletedURL += "<td></td>";
				}
				delinkNotDeletedURL += "<td>&nbsp;&nbsp;in&nbsp;&nbsp;"
									 + entry.getKey()
									 + "&nbsp;&nbsp;("
									 + entry.getValue().toString()
									 + " records)&nbsp;&nbsp;&nbsp;&nbsp;</td>"
//									 + "<a class=\"btn btn-default\" href=\""
//									 + "../QuotationRecordDataReview/delink?refMonthStr="+ entry.getKey()
//									 + "\">Delink</a></td>"
									 + "</tr>";
			}
		}
		
		Long countqrs = quotationRecordDao.countQuotationRecordsByReferenceMonth(nextMonth2YearsAgo);
		int qrExpireInNextMonth = 0;
		if(countqrs != null) {
			qrExpireInNextMonth = countqrs.intValue();
		}
		//String delinkExpireInNextMonthURL = "../QuotationRecordDataReview/delink?refMonthStr="+ commonService.formatMonth(nextMonth2YearsAgo);
		
		countqrs = quotationRecordDao.countQuotationRecordsByReferenceMonth(next2Month2YearsAgo);
		int qrExpireInNextNextMonth = 0;
		if(countqrs != null) {
			qrExpireInNextNextMonth = countqrs.intValue();
		}
		//String delinkExpireInNext2MonthURL = "../QuotationRecordDataReview/delink?refMonthStr="+ commonService.formatMonth(next2Month2YearsAgo);
		
		Map<String, Integer> map2 = new LinkedHashMap<String, Integer>();
		if(qrExpireInNextMonth > 0)
			map2.put(commonService.formatMonth(nextMonth2YearsAgo), qrExpireInNextMonth);
		if(qrExpireInNextNextMonth > 0)
			map2.put(commonService.formatMonth(next2Month2YearsAgo), qrExpireInNextNextMonth);
		
		String delinkExpireURL = "";
		boolean start2 = true;
		if(map2 != null && map2.size() > 0) {
			for (Map.Entry<String, Integer> entry : map2.entrySet()) {
				delinkExpireURL += "<tr>";
				if(start2) {
					delinkExpireURL += "<td>Quotation Record will be expired</td>";
					start2 = false;
				} else {
					delinkExpireURL += "<td></td>";
				}
				delinkExpireURL += "<td>&nbsp;&nbsp;in&nbsp;&nbsp;"
									 + entry.getKey()
									 + "&nbsp;&nbsp;("
									 + entry.getValue().toString()
									 + " records)&nbsp;&nbsp;&nbsp;&nbsp;</td>"
//									 + "<a class=\"btn btn-default\" href=\""
//									 + "../QuotationRecordDataReview/delink?refMonthStr="+ entry.getKey()
//									 + "\">Delink</a></td>"
									 + "</tr>";
			}
		}
		
		if(map.size() > 0 || map2.size() > 0) {
			List<User> users = userDao.getActiveUsersWithAnyAuthorityLevel(256);
			for(User user : users) {
				String content = messageSource.getMessage("N00059", new Object[]{
						delinkNotDeletedURL,
						delinkExpireURL
						}, Locale.ENGLISH);
				String subject = messageSource.getMessage("N00058", null, Locale.ENGLISH);
				String contentHeader = messageSource.getMessage("N00071", new Object[]{commonService.formatMonth(refMonth) }, Locale.ENGLISH);
				notifyService.sendNotification(user, subject, contentHeader + "\n" + content, false);
			}
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date refMonth = commonService.getReferenceMonth(new Date());	
		Date nextMonth = DateUtils.addMonths(refMonth, 1);
		
		List<Date> dates = calendarEventDao.getPreviousNWorkdingDate(nextMonth, 1);
		Date date = dates.get(0);
		
		return today.equals(date);
	}

}
