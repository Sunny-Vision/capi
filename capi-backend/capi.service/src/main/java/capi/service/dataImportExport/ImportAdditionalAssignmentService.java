package capi.service.dataImportExport;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.SurveyMonthDao;
import capi.dal.TpuDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.SurveyMonth;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.masterMaintenance.OutletEditModel;
import capi.service.CommonService;
import capi.service.masterMaintenance.OutletService;

@Service("ImportAdditionalAssignmentService")
public class ImportAdditionalAssignmentService extends DataImportServiceBase{

	@Autowired
	private AssignmentDao dao;
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	@Autowired
	private DistrictDao districtDao;
	@Autowired
	private TpuDao tpuDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ImportExportTaskDao taskDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private OutletService outletService;

	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 28;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();
			Assignment assignment = fillEntity(row, rowCnt);
			dao.save(assignment);
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		dao.flush();
		workbook.close();
	}

	private Assignment fillEntity(Row row, int rowCnt) throws Exception{
		Assignment entity = null;
		int columnNo = 0;
		try{
			String [] values = getStringValues(row, 11);
			
			entity = new Assignment();
			
			String refMonthStr = values[0];
			columnNo = 0;
			SurveyMonth surveyMonth = null;
			if(StringUtils.isEmpty(refMonthStr)) {
				throw new RuntimeException("Reference Month is empty");
			} else {
				Date refMonth = commonService.getMonth(refMonthStr);
				surveyMonth = surveyMonthDao.getSurveyMonthByReferenceMonth(refMonth);
				if(surveyMonth == null) {
					throw new RuntimeException("Reference Month is not yet defined");
				} else {
					entity.setSurveyMonth(surveyMonth);
				}
			}
			//entity.setSurveyMonth(surveyMonth);
			
			String districtCode = values[1];
			columnNo = 1;
			if (StringUtils.isEmpty(districtCode)){
				throw new RuntimeException("District Code cannot be empty");
			}
			District additionalDistrict = districtDao.getDistrictByCode(districtCode);
			if (additionalDistrict == null){
				throw new RuntimeException("District not found: District code = "+districtCode);
			}
			entity.setAdditionalDistrict(additionalDistrict);
			
			String tpuCode = values[2];
			columnNo = 2;
			if (StringUtils.isEmpty(tpuCode)){
				throw new RuntimeException("Tpu Code cannot be empty");
			}
			Tpu additionalTpu = tpuDao.getTpuByCode(tpuCode);
			if (additionalTpu == null){
				throw new RuntimeException("Tpu not found: Tpu Code = "+tpuCode);
			}
			entity.setAdditionalTpu(additionalTpu);
			
			String additionalFirmNo = values[3];
			columnNo = 3;
			if (StringUtils.isEmpty(additionalFirmNo)){
				throw new RuntimeException("Firm Code should not be empty");
			}
			entity.setAdditionalFirmNo(additionalFirmNo);
			
			String additionalFirmAddress = values[4];
			columnNo = 4;
			entity.setAdditionalFirmAddress(additionalFirmAddress);
			if (!StringUtils.isEmpty(additionalFirmAddress)){
				OutletEditModel model = new OutletEditModel();
				model.setStreetAddress(additionalFirmAddress);
				try{
					outletService.fillInGps(model);
					entity.setAdditionalLatitude(model.getLatitude());
					entity.setAdditionalLongitude(model.getLongitude());
				}
				catch(Exception ex){}
			}			
			
			String additionalFirmName = values[5];
			columnNo = 5;
			entity.setAdditionalFirmName(additionalFirmName);
			
			String additionalContactPerson = values[6];
			columnNo = 6;
			entity.setAdditionalContactPerson(additionalContactPerson);
			
			String responsibleOfficer = values[7].trim();
			columnNo = 7;
			User user = userDao.getUserByStaffCode(responsibleOfficer);
			entity.setUser(user);
			
			String noOfForms = values[8];
			columnNo = 8;
			int additionalNoOfForms = (int)Double.parseDouble(noOfForms);
			entity.setAdditionalNoOfForms(additionalNoOfForms);
			
			String survey = values[9];
			columnNo = 9;
			entity.setSurvey(survey);
			
			String referenceNo = values[10];
			columnNo = 10;
			entity.setReferenceNo(referenceNo);
			
			entity.setImportedAssignment(true);
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(columnNo+1)+")");
		}
		return entity;
	}

}
