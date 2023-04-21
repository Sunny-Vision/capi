package capi.service.batchJob;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.ScSvPlanDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckSetupDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.entity.ScSvPlan;
import capi.entity.SpotCheckForm;
import capi.entity.SpotCheckSetup;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * The service for generate the QC form of that day
 */
@Service("QCFormGenerateService")
public class QCFormGenerateService implements BatchJobService{

	@Autowired
	private ScSvPlanDao qcPlanDao;
	
	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SpotCheckSetupDao spotCheckSetupDao;
	
	@Autowired
	private NotificationService notifyService;
	
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Create QC Form";
	}
	
	@Override
	public void runTask() throws Exception{
		// TODO Auto-generated method stub
		Date date = commonService.getDateWithoutTime(new Date());	
		
		List<SpotCheckSetup> spotCheckSetups = spotCheckSetupDao.getSpotCheckSetupByNotificationDate(date);
		
		for (SpotCheckSetup setup : spotCheckSetups){
			ScSvPlan plan = new ScSvPlan();
			plan.setMandatoryPlan(true);
			plan.setOwner(setup.getUser().getSupervisor());
			plan.setUser(setup.getUser());
			plan.setQcType(1);
			plan.setVisitDate(setup.getSpotCheckDate().getDate());
			plan.setSpotCheckSetup(setup);
			qcPlanDao.save(plan);
			
			SpotCheckForm form = new SpotCheckForm();
			form.setOfficer(plan.getUser());
			form.setSupervisor(plan.getOwner());
			form.setSpotCheckDate(plan.getVisitDate());
			form.setScSvPlan(plan);
			spotCheckFormDao.save(form);
			
			String spotCheckDate = commonService.formatDate(plan.getVisitDate());
			String officer = plan.getUser().getStaffCode() + " - " + plan.getUser().getChineseName();
			
			String subject = messageSource.getMessage("N00007", null, Locale.ENGLISH);
			String content = messageSource.getMessage("N00008", new Object[]{spotCheckDate,officer}, Locale.ENGLISH);
			
			notifyService.sendNotification(plan.getOwner(), subject, content, false);
			
		}
		//qcPlanDao.flush();
		
		/*
		List<ScSvPlan> plans = qcPlanDao.getScSvPlanByDate(date, true);
		for (ScSvPlan plan : plans){
			if (plan.getQcType() == 1 && (plan.getSpotCheckForms() == null || plan.getSpotCheckForms().size() == 0)){
				SpotCheckForm form = new SpotCheckForm();
				form.setOfficer(plan.getUser());
				form.setSupervisor(plan.getOwner());
				form.setSpotCheckDate(plan.getVisitDate());
				form.setScSvPlan(plan);
				spotCheckFormDao.save(form);
			}
			else if (plan.getQcType() == 2 && (plan.getSupervisoryVisitForms() == null || plan.getSupervisoryVisitForms().size() == 0)){
				SupervisoryVisitForm form = new SupervisoryVisitForm();
				form.setSupervisor(plan.getOwner());
				form.setUser(plan.getUser());
				form.setVisitDate(plan.getVisitDate());
				form.setScSvPlan(plan);
				supervisoryVisitFormDao.save(form);
			}
		}
		*/
		qcPlanDao.flushAndClearCache();
	}


}
