package capi.service.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.SupervisoryVisitDetailDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.SupervisoryVisitDetail;
import capi.entity.SupervisoryVisitForm;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.DocxExportModel;
import capi.model.report.InformationSupervisoryVisitFormCriteria;
import capi.service.CommonService;

@Service("InformationSupervisoryVisitFormService")
public class InformationSupervisoryVisitFormService extends ReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;

	@Autowired
	private SupervisoryVisitDetailDao supervisoryVisitDetailDao;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		InformationSupervisoryVisitFormCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), InformationSupervisoryVisitFormCriteria.class);
		List<Date> visitDates = new ArrayList<Date>();
		for(String date : criteria.getSupervisoryVisitDates()) {
			visitDates.add(commonService.getDate(date));
		}
		
		List<DocxExportModel> datas = new ArrayList<DocxExportModel>();
		Hashtable<String, Integer> formCount = new Hashtable<>();
		
		List<SupervisoryVisitForm> supervisoryVisitForms = supervisoryVisitFormDao.getSVFormsByUserAndSVDate(criteria.getFieldOfficerId(), visitDates);
		for(SupervisoryVisitForm supervisoryVisitForm : supervisoryVisitForms) {
			DocxExportModel data = new DocxExportModel();
			Hashtable<String, String> table = new Hashtable<String, String>();
			
			User user = supervisoryVisitForm.getUser();
			table.put("fieldOfficerName", user.getEnglishName());
			table.put("fieldOfficerDestination", user.getDestination());
			
			User supervisor = supervisoryVisitForm.getSupervisor();
			table.put("supervisorName", supervisor.getEnglishName());
			table.put("supervisorDestination", supervisor.getDestination());
			
			String visitDate = commonService.formatDate(supervisoryVisitForm.getVisitDate());
			String fromTime = commonService.formatTime(supervisoryVisitForm.getFromTime());
			String toTime = commonService.formatTime(supervisoryVisitForm.getToTime());
			table.put("supervisoryDate", visitDate);
			table.put("fromTime", fromTime);
			table.put("toTime", toTime);
			
			String discussionDate = commonService.formatDate(supervisoryVisitForm.getDiscussionDate());
			String remark = supervisoryVisitForm.getRemark();
			table.put("discussionDate", discussionDate);
			if(remark != null)
				table.put("discussionRemark", remark);
			else
				table.put("discussionRemark", "");
			
			if(supervisoryVisitForm.getKnowledgeOfWorkResult() != null) {
				if(supervisoryVisitForm.getKnowledgeOfWorkResult() == 1) {
					if(supervisoryVisitForm.getKnowledgeOfWorkRemark() != null)
						table.put("C_1_remark", supervisoryVisitForm.getKnowledgeOfWorkRemark());
					else
						table.put("C_1_remark", "");
				} else if(supervisoryVisitForm.getKnowledgeOfWorkResult() == 2) {
					table.put("C_1_noComment", "\u2713");
				} else {
					table.put("C_1_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getInterviewTechniqueResult() != null) {
				if(supervisoryVisitForm.getInterviewTechniqueResult() == 1) {
					if(supervisoryVisitForm.getInterviewTechniqueRemark() != null)
						table.put("C_2_remark", supervisoryVisitForm.getInterviewTechniqueRemark());
					else
						table.put("C_2_remark", "");
				} else if(supervisoryVisitForm.getInterviewTechniqueResult() == 2) {
					table.put("C_2_noComment", "\u2713");
				} else {
					table.put("C_2_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getHandleDifficultInterviewResult() != null) {
				if(supervisoryVisitForm.getHandleDifficultInterviewResult() == 1) {
					if(supervisoryVisitForm.getHandleDifficultInterviewRemark() != null)
						table.put("C_3_remark", supervisoryVisitForm.getHandleDifficultInterviewRemark());
					else
						table.put("C_3_remark", "");
				} else if(supervisoryVisitForm.getHandleDifficultInterviewResult() == 2) {
					table.put("C_3_noComment", "\u2713");
				} else {
					table.put("C_3_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getDataRecordingResult() != null) {
				if(supervisoryVisitForm.getDataRecordingResult() == 1) {
					if(supervisoryVisitForm.getDataRecordingRemark() != null)
						table.put("C_4_remark", supervisoryVisitForm.getDataRecordingRemark());
					else
						table.put("C_4_remark", "");
				} else if(supervisoryVisitForm.getDataRecordingResult() == 2) {
					table.put("C_4_noComment", "\u2713");
				} else {
					table.put("C_4_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getLocalGeographyResult() != null) {
				if(supervisoryVisitForm.getLocalGeographyResult() == 1) {
					if(supervisoryVisitForm.getLocalGeographyRemark() != null)
						table.put("C_5_remark", supervisoryVisitForm.getLocalGeographyRemark());
					else
						table.put("C_5_remark", "");
				} else if(supervisoryVisitForm.getLocalGeographyResult() == 2) {
					table.put("C_5_noComment", "\u2713");
				} else {
					table.put("C_5_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getMannerWithPublicResult() != null) {
				if(supervisoryVisitForm.getMannerWithPublicResult() == 1) {
					if(supervisoryVisitForm.getMannerWithPublicRemark() != null)
						table.put("C_6_remark", supervisoryVisitForm.getMannerWithPublicRemark());
					else
						table.put("C_6_remark", "");
				} else if(supervisoryVisitForm.getMannerWithPublicResult() == 2) {
					table.put("C_6_noComment", "\u2713");
				} else {
					table.put("C_6_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getJudgmentResult() != null) {
				if(supervisoryVisitForm.getJudgmentResult() == 1) {
					if(supervisoryVisitForm.getJudgmentRemark() != null)
						table.put("C_7_remark", supervisoryVisitForm.getJudgmentRemark());
					else
						table.put("C_7_remark", "");
				} else if(supervisoryVisitForm.getJudgmentResult() == 2) {
					table.put("C_7_noComment", "\u2713");
				} else {
					table.put("C_7_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getOrganizationOfWorkResult() != null) {
				if(supervisoryVisitForm.getOrganizationOfWorkResult() == 1) {
					if(supervisoryVisitForm.getOrganizationOfWorkRemark() != null)
						table.put("C_8_remark", supervisoryVisitForm.getOrganizationOfWorkRemark());
					else
						table.put("C_8_remark", "");
				} else if(supervisoryVisitForm.getOrganizationOfWorkResult() == 2) {
					table.put("C_8_noComment", "\u2713");
				} else {
					table.put("C_8_NA", "\u2713");
				}
			}
			
			if(supervisoryVisitForm.getOtherResult() != null) {
				if(supervisoryVisitForm.getOtherResult() == 1) {
					if(supervisoryVisitForm.getOtherRemark() != null)
						table.put("C_9_remark", supervisoryVisitForm.getOtherRemark());
					else
						table.put("C_9_remark", "");
				} else if(supervisoryVisitForm.getOtherResult() == 2) {
					table.put("C_9_noComment", "\u2713");
				} else {
					table.put("C_9_NA", "\u2713");
				}
			}
			
			List<SupervisoryVisitDetail> supervisoryVisitDetails = supervisoryVisitDetailDao.getSVDetailsBySVFormId(supervisoryVisitForm.getSupervisoryVisitFormId(), criteria.getSurvey());
			for(int i = 0; i < supervisoryVisitDetails.size(); i++) {
				SupervisoryVisitDetail supervisoryVisitDetail = supervisoryVisitDetails.get(i);
				
				String survey = supervisoryVisitDetail.getSurvey();
				if(survey != null)
					table.put("assignment_" + (i+1) + "_survey", survey);
				else
					table.put("assignment_" + (i+1) + "_survey", "");
				String serialNo = supervisoryVisitDetail.getReferenceNo();
				if(serialNo != null)
					table.put("assignment_" + (i+1) + "_serial", serialNo);
				else
					table.put("assignment_" + (i+1) + "_serial", "");
				
				if(supervisoryVisitDetail.getResult() != null) {
					if(supervisoryVisitDetail.getResult() == 1) {
						table.put("assignment_" + (i+1) + "_enumerated", "\u2713");
					} else if(supervisoryVisitDetail.getResult() == 2) {
						table.put("assignment_" + (i+1) + "_nonContacted", "\u2713");
					} else {
						table.put("assignment_" + (i+1) + "_others", supervisoryVisitDetail.getOtherRemark());
					}
				}
			}
			
			if (formCount.containsKey(user.getEnglishName() + "_" + visitDate + ".docx")) {
				int count = formCount.get(user.getEnglishName() + "_" + visitDate + ".docx");
				count += 1;
				formCount.put(user.getEnglishName() + "_" + visitDate + ".docx", count);
				data.setFilename(user.getEnglishName() + "_" + visitDate + " (" + count + ")" + ".docx");
			} else {
				data.setFilename(user.getEnglishName() + "_" + visitDate + ".docx");
				formCount.put(user.getEnglishName() + "_" + visitDate + ".docx", 1);
			}
			data.setData(table);
			datas.add(data);
		}
		
		String path = this.exportMultiDocx(task, "MB9046", datas, this.getFunctionCode());
		
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9046";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		InformationSupervisoryVisitFormCriteria criteria = (InformationSupervisoryVisitFormCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
		
		if (criteria.getSurvey() != null && criteria.getSurvey().size() > 0){
			descBuilder.append("\n");
			/*List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getSurvey()){
				switch (form){
					case 1:codes.add("Market"); break;
					case 2:codes.add("Supermarket"); break;
					case 3:codes.add("Batch"); break;
					default:codes.add("Others"); break;
				}
				
			}*/
			//descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ",")));
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(criteria.getSurvey(), ", ")));
		}
		
		if(criteria.getFieldOfficerId() != null && criteria.getFieldOfficerId().size() > 0){
			descBuilder.append("\n");
			List<String> fieldOfficers = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(criteria.getFieldOfficerId());
			for(User user : users) {
				fieldOfficers.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Field Officer(s): %s", StringUtils.join(fieldOfficers, ", ")));
		}
		
		if (criteria.getSupervisoryVisitDates() != null && criteria.getSupervisoryVisitDates().size() > 0) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Supervisory Visit Date(s): %s", StringUtils.join(criteria.getSupervisoryVisitDates(), ", ")));
		}
		
		User creator = userDao.findById(userId);
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(taskType);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;
	}

}
