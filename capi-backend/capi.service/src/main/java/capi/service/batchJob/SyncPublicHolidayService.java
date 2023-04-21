package capi.service.batchJob;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.service.assignmentAllocationAndReallocation.CalendarEventService;

/**
 * 
 * @author stanley_tsang
 *
 * Synchronize the public holiday yearly
 */
@Service("SyncPublicHolidayService")
public class SyncPublicHolidayService implements BatchJobService{

	@Autowired
	private CalendarEventService calendarEventService; 
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Synchronize Public Holiday";
	}

	@Override
	public void runTask() throws Exception{
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DATE) == 1 && calendar.get(Calendar.MONTH) == 0){
			calendarEventService.syncPublicCalendar();
		}		
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
