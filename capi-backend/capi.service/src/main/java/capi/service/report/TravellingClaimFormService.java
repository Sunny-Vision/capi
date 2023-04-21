package capi.service.report;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.FieldworkTimeLogDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.TravellingClaimFormACSO;
import capi.model.report.TravellingClaimFormAddress;
import capi.model.report.TravellingClaimFormCriteria;
import capi.model.report.TravellingClaimFormRecord;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.ZipService;
import net.sf.jasperreports.engine.JasperPrint;

@Service("TravellingClaimFormService")
public class TravellingClaimFormService extends ReportServiceBase{

	@Autowired
	private FieldworkTimeLogDao dao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private AppConfigService appConfig;

	@Autowired
	private ZipService zipService;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9036";
	}

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		TravellingClaimFormCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), TravellingClaimFormCriteria.class);
		
		Date month = commonService.getMonth(criteria.getMonth());
		
		Date date = new Date();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String today = commonService.formatDate(date);
		
		BigDecimal totalTaxiFare = BigDecimal.ZERO;
		BigDecimal totalOtherFare = BigDecimal.ZERO;
		DecimalFormat df = new DecimalFormat("0.0###"); 
		
		User user = userDao.findById(task.getUser().getUserId());
		User supervisor = null;
		if(task.getUser().getSupervisor() != null) {
			supervisor = userDao.findById(task.getUser().getSupervisor().getUserId());
		}
		
		Hashtable<String, String> firstTable = new Hashtable<String, String>();
		List<TravellingClaimFormACSO> acso = dao.getTravellingClaimFormTotalAmount(month, user.getUserId());
		
		String supervisorName = "";
		List<User> scsoUserList = userDao.findUserByRankCode("SCSO");
		if (scsoUserList.size()>0) {
			supervisorName = scsoUserList.get(0).getEnglishName();
		}
		
		if(acso != null && acso.size() > 0) {
			for(int i = 0; i < acso.size(); i++) {
				if("TX".equals(acso.get(i).getTransport())) {
					if (acso.get(i).getExpenses() != null){
						totalTaxiFare = totalTaxiFare.add(new BigDecimal(df.format(acso.get(i).getExpenses())));
					}
				} else {
					if (acso.get(i).getExpenses() != null){
						totalOtherFare = totalOtherFare.add(new BigDecimal(df.format(acso.get(i).getExpenses())));
					}
				}
			}
			
			TravellingClaimFormACSO travellingClaimForm = acso.get(0);
			
			firstTable.put("officerName", travellingClaimForm.getName());
			firstTable.put("rank", travellingClaimForm.getRank());
			
			if(!StringUtils.isEmpty(travellingClaimForm.getOffice())) {
				firstTable.put("office", travellingClaimForm.getOffice());
			}
			if(!StringUtils.isEmpty(travellingClaimForm.getOffice2())) {
				firstTable.put("office2", travellingClaimForm.getOffice2());
			}
			
			firstTable.put("taxiFare", df.format(totalTaxiFare));
			firstTable.put("otherFare", df.format(totalOtherFare));
			
			firstTable.put("firstDate", today);
			firstTable.put("supervisorRank", "SCSO (CPI)");
			firstTable.put("supervisorName", supervisorName);
		}
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("month", criteria.getMonth());
		parameters.put("officerName", user.getEnglishName());
		if(acso != null && acso.size() > 0 && !StringUtils.isEmpty(acso.get(0).getOffice())) {
			parameters.put("office", acso.get(0).getOffice());
		} else {
			parameters.put("office", "-");
		}
		if(acso != null && acso.size() > 0 && !StringUtils.isEmpty(acso.get(0).getOffice2())) {
			parameters.put("office2", acso.get(0).getOffice2());
		} else {
			parameters.put("office2", "-");
		}

		List<TravellingClaimFormRecord> allRecords = dao.getTravellingClaimFormRecord(month, user.getUserId());
		List<TravellingClaimFormRecord> progressRecord = new ArrayList<TravellingClaimFormRecord>();
		
		Hashtable<Integer, List<TravellingClaimFormRecord>> groupedRecord = new Hashtable<Integer, List<TravellingClaimFormRecord>>();
		List<String> isDirectField = new ArrayList<String>();
		String prevDate = "";
		
		for(int i = 0; i < allRecords.size(); i++) {
			if (groupedRecord.containsKey(allRecords.get(i).getTimeLogId())){
				groupedRecord.get(allRecords.get(i).getTimeLogId()).add(allRecords.get(i));
			} else {
				List<TravellingClaimFormRecord> list = new ArrayList<TravellingClaimFormRecord>();
				list.add(allRecords.get(i));
				groupedRecord.put(allRecords.get(i).getTimeLogId(), list);
			}
			
			if (allRecords.get(i).isIncludeInTransportForm()){
				progressRecord.add(allRecords.get(i));
			}
			
			if (!prevDate.equals(allRecords.get(i))){
				if (allRecords.get(i).getActivity().equals("FI")){
					isDirectField.add(allRecords.get(i).getDate());
					prevDate = allRecords.get(i).getDate();
				} else if (allRecords.get(i).getActivity().equals("TR")){
					prevDate = allRecords.get(i).getDate();
				}
			}
		}
		
		BigDecimal subTotalTaxiFare = BigDecimal.ZERO;
		BigDecimal subTotalOtherFare = BigDecimal.ZERO;
		int times = 0;
		int remainder = 0;
		if(progressRecord != null) {
			times = ( progressRecord.size() - 6 ) / 9;
			if(times < 0) times = 0;
			remainder = ( progressRecord.size() - 6 ) % 9;
			if(remainder < 0) remainder = 0;
		}
		int fromIndex = 0;
		int toIndex = 6;
		int maxSize = 9;
		
		List<TravellingClaimFormRecord> splited = new ArrayList<TravellingClaimFormRecord> ();
		if(progressRecord.size() > 6) {
			splited = progressRecord.subList(fromIndex, toIndex);
		} else {
			splited = progressRecord;
		}
		
		ByteArrayOutputStream firstBAOStream = new ByteArrayOutputStream();
		List<ByteArrayInputStream> baiStreamList = new ArrayList<ByteArrayInputStream> ();
		
		// first 6 records
		prevDate = "";
		for(int i = 0; i < splited.size(); i++) {
			TravellingClaimFormRecord record = splited.get(i);
			
			String currentDate = String.valueOf(record.getDate());
			firstTable.put("date" + (i+1), currentDate);
			firstTable.put("from" + (i+1), (prevDate.equals(currentDate)?"":record.getFrom()));
			firstTable.put("to" + (i+1), record.getTo());
			firstTable.put("transport" + (i+1), record.getTransport());
			
			if("TX".equals(record.getTransport())) {
				if(record.getExpenses() != null) {
					firstTable.put("taxiFare" + (i+1), df.format(record.getExpenses()));
					subTotalTaxiFare = subTotalTaxiFare.add(new BigDecimal(df.format(record.getExpenses())));
				}
			} else {
				if(record.getExpenses() != null) {
					firstTable.put("otherFare" + (i+1), df.format(record.getExpenses()));
					subTotalOtherFare = subTotalOtherFare.add(new BigDecimal(df.format(record.getExpenses())));
				}
			}
			String purpose = "";
			if (StringUtils.equalsIgnoreCase(record.getTo(), "office1") 
					|| StringUtils.equalsIgnoreCase(record.getTo(), "office") 
					|| StringUtils.equalsIgnoreCase(record.getTo(), "office2")) {
				purpose = "Back to office after fieldwork ";
			}
			if(!record.getTransit()) {
				if (StringUtils.isEmpty(purpose))
					purpose += "Fieldwork ";
			}
			if (!StringUtils.isEmpty(record.getRemark()))
				purpose += "(" + record.getRemark() + ") ";
			if(record.getTransit()) {
				purpose += "(Transit)";
			}
			if(!StringUtils.isEmpty(purpose)) {
				firstTable.put("purpose" + (i+1), purpose);
			}
			
			if(i == (splited.size() - 1)) {
				fromIndex = toIndex;
				toIndex += maxSize;
			}
			
			prevDate = currentDate;
		}
		
		firstTable.put("totalTaxiFare", df.format(subTotalTaxiFare));
		firstTable.put("totalOtherFare", df.format(subTotalOtherFare));
		
		byte[] firstBA = null;
		ByteArrayInputStream firstBAIStream = null;
		if(splited != null && splited.size() > 0) {
			this.exportDocx(task, "MB9036Part1", firstTable, this.getFunctionCode(), firstBAOStream);
			firstBA = firstBAOStream.toByteArray();
			firstBAIStream = new ByteArrayInputStream(firstBA);
			baiStreamList.add(firstBAIStream);
		}
		
		subTotalTaxiFare = BigDecimal.ZERO;
		subTotalOtherFare = BigDecimal.ZERO;
		
		Hashtable<String, String> table2 = null;
		
		// times
		for(int i = 0; i < times; i++) {
			splited = progressRecord.subList(fromIndex, toIndex);
			
			table2 = new Hashtable<String, String>();
			
			for(int j = 0; j < splited.size(); j++) {
				TravellingClaimFormRecord record = splited.get(j);

				String currentDate = String.valueOf(record.getDate());
				table2.put("date" + (j+1), currentDate);
				table2.put("from" + (j+1), (prevDate.equals(currentDate)?"":record.getFrom()));
				table2.put("to" + (j+1), record.getTo());
				table2.put("transport" + (j+1), record.getTransport());
				
				if("TX".equals(record.getTransport())) {
					if(record.getExpenses() != null) {
						table2.put("taxiFare" + (j+1), df.format(record.getExpenses()));
						subTotalTaxiFare = subTotalTaxiFare.add(new BigDecimal(df.format(record.getExpenses())));
					}
				} else {
					if(record.getExpenses() != null) {
						table2.put("otherFare" + (j+1), df.format(record.getExpenses()));
						subTotalOtherFare = subTotalOtherFare.add(new BigDecimal(df.format(record.getExpenses())));
					}
				}
				String purpose = "";
				if (StringUtils.equalsIgnoreCase(record.getTo(), "office1") 
						|| StringUtils.equalsIgnoreCase(record.getTo(), "office") 
						|| StringUtils.equalsIgnoreCase(record.getTo(), "office2")) {
					purpose = "Back to office after fieldwork ";
				}
				if(!record.getTransit()) {
					if (StringUtils.isEmpty(purpose))
						purpose += "Fieldwork ";
				}
				if (!StringUtils.isEmpty(record.getRemark()))
					purpose += "(" + record.getRemark() + ") ";
				if(record.getTransit()) {
					purpose += "(Transit)";
				}
				if(!StringUtils.isEmpty(purpose)) {
					table2.put("purpose" + (j+1), purpose);
				}
				
				if(j == (splited.size() - 1)) {
					fromIndex = toIndex;
					toIndex += maxSize;
					
					table2.put("totalTaxiFare", df.format(subTotalTaxiFare));
					table2.put("totalOtherFare", df.format(subTotalOtherFare));
					
					ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
					this.exportDocx(task, "MB9036Part2", table2, this.getFunctionCode(), baoStream);
					byte[] ba = baoStream.toByteArray();
					ByteArrayInputStream baiStream = new ByteArrayInputStream(ba);
					baiStreamList.add(baiStream);
					
					subTotalTaxiFare = BigDecimal.ZERO;
					subTotalOtherFare = BigDecimal.ZERO;
				}
				
				prevDate = currentDate;
			}
		}
		
		// Remainder
		if(remainder > 0) {
			splited = progressRecord.subList(fromIndex, (fromIndex + remainder));
		}
		
		table2 = new Hashtable<String, String>();
		
		for(int j = 0; ((j < splited.size()) && (remainder > 0)); j++) {
			TravellingClaimFormRecord record = splited.get(j);
			
			String currentDate = String.valueOf(record.getDate());
			table2.put("date" + (j+1), String.valueOf(record.getDate()));
			table2.put("from" + (j+1), (prevDate.equals(currentDate)?"":record.getFrom()));
			table2.put("to" + (j+1), record.getTo());
			table2.put("transport" + (j+1), record.getTransport());
			
			if("TX".equals(record.getTransport())) {
				if(record.getExpenses() != null) {
					table2.put("taxiFare" + (j+1), df.format(record.getExpenses()));
					subTotalTaxiFare = subTotalTaxiFare.add(new BigDecimal(df.format(record.getExpenses())));
				}
			} else {
				if(record.getExpenses() != null) {
					table2.put("otherFare" + (j+1), df.format(record.getExpenses()));
					subTotalOtherFare = subTotalOtherFare.add(new BigDecimal(df.format(record.getExpenses())));
				}
			}
			

			String purpose = "";
			if (StringUtils.equalsIgnoreCase(record.getTo(), "office1") 
					|| StringUtils.equalsIgnoreCase(record.getTo(), "office") 
					|| StringUtils.equalsIgnoreCase(record.getTo(), "office2")) {
				purpose = "Back to office after fieldwork ";
			}
			if(!record.getTransit()) {
				if (StringUtils.isEmpty(purpose))
					purpose += "Fieldwork ";
			}
			if (!StringUtils.isEmpty(record.getRemark()))
				purpose += "(" + record.getRemark() + ") ";
			if(record.getTransit()) {
				purpose += "(Transit)";
			}
			if(!StringUtils.isEmpty(purpose)) {
				table2.put("purpose" + (j+1), purpose);
			}
			
			if(j == (splited.size() - 1)) {
				fromIndex = toIndex;
				toIndex += maxSize;
				
				table2.put("totalTaxiFare", df.format(subTotalTaxiFare));
				table2.put("totalOtherFare", df.format(subTotalOtherFare));
				
				ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
				this.exportDocx(task, "MB9036Part2", table2, this.getFunctionCode(), baoStream);
				byte[] ba = baoStream.toByteArray();
				ByteArrayInputStream baiStream = new ByteArrayInputStream(ba);
				baiStreamList.add(baiStream);
				
				subTotalTaxiFare = BigDecimal.ZERO;
				subTotalOtherFare = BigDecimal.ZERO;
			}
			
			prevDate = currentDate;
		}
		
		ByteArrayOutputStream mergeOStream = new ByteArrayOutputStream();
		
		byte[] mergeBA = null;
		ByteArrayInputStream mergeBAIStream = null;
		List<ByteArrayInputStream> mergeBAIStreamList = new ArrayList<ByteArrayInputStream> ();
		List<String> filenames = new ArrayList<String> ();
		if(splited != null && splited.size() > 0) {
			this.mergeDocx(mergeOStream, baiStreamList.toArray(new ByteArrayInputStream[0]));
			mergeBA = mergeOStream.toByteArray();
			mergeBAIStream = new ByteArrayInputStream(mergeBA);
			mergeBAIStreamList.add(mergeBAIStream);
			filenames.add("CSBF 723.docx");
		}

		List<TravellingClaimFormAddress> progressAddresses = new ArrayList<TravellingClaimFormAddress> ();
		List<Integer> trTravellingClaimFormRecord = new ArrayList<Integer> ();
		
		prevDate = "";
		
		for (TravellingClaimFormRecord record : allRecords) {
//			boolean isDirectField = allRecords.indexOf(record) <= (trTravellingClaimFormRecord.size() > 0 ? trTravellingClaimFormRecord.get(0) : 0);
//			
//			if ( record.getActivity().equals("FI") && (!StringUtils.equals(record.getDate(), prevDate)) && isDirectField){
//				trTravellingClaimFormRecord.add(allRecords.indexOf(record));
//			} else if ( record.getActivity().equals("TR") ){
//				trTravellingClaimFormRecord.add(allRecords.indexOf(record));
//			}
//			prevDate = record.getDate();
			
			if ( record.getActivity().equals("FI") && (!StringUtils.equals(record.getDate(), prevDate)) && isDirectField.contains(record.getDate())){
				trTravellingClaimFormRecord.add(allRecords.indexOf(record));
			} else if ( record.getActivity().equals("TR") && record.isIncludeInTransportForm() ){
				trTravellingClaimFormRecord.add(allRecords.indexOf(record));
			}
			prevDate = record.getDate();
		}
				
		for (int i=0; i<trTravellingClaimFormRecord.size(); i++) {
			int lastIndex = (i+1 >= trTravellingClaimFormRecord.size()) ? allRecords.size() : trTravellingClaimFormRecord.get(i+1);
			String currentDate = allRecords.get(trTravellingClaimFormRecord.get(i)).getDate();
			
			for (int j=trTravellingClaimFormRecord.get(i) ; j<lastIndex; j++) {
				if (!StringUtils.equals(allRecords.get(j).getDate(), currentDate)) { break; }
				
				if (allRecords.get(j).getActivity().equals("FI")){
					TravellingClaimFormAddress address = new TravellingClaimFormAddress();
					address.setDate(allRecords.get(j).getDate());
					address.setAddress(StringUtils.trim(allRecords.get(j).getDestination()));
					progressAddresses.add(address);
					break;
				}
			}
		}
		
		if (progressAddresses.size() == 0) {
			TravellingClaimFormAddress address = new TravellingClaimFormAddress();
			address.setDate("");
			address.setAddress("");
			progressAddresses.add(address);	
		}
		JasperPrint jasperPrint = this.getJasperPrint(task, "TravellingClaimFormAddress", progressAddresses, parameters, ".");
		
		ByteArrayOutputStream jpBAOStream = new ByteArrayOutputStream();
		this.printExcelStream(jasperPrint, jpBAOStream);
		byte[] jpBA = jpBAOStream.toByteArray();
		ByteArrayInputStream jpBAIStream = new ByteArrayInputStream(jpBA);
		mergeBAIStreamList.add(jpBAIStream);
		filenames.add("Full address of place of work.xlsx");
		
		try{
			String insidePath = appConfig.getReportLocation()+File.separator+this.getFunctionCode();
			File dir = new File(insidePath);
			if (!dir.exists()){
				dir.mkdirs();
			}
			String filename = UUID.randomUUID().toString()+".zip";
			
			FileOutputStream dest = new FileOutputStream(insidePath+File.separator+filename);
			zipService.zipFiles(mergeBAIStreamList.toArray(new ByteArrayInputStream[0]), filenames.toArray(new String[0]), new BufferedOutputStream(dest));
			
			String path = File.separator+this.getFunctionCode()+File.separator+filename;
			
			task.setPath(path);
			reportTaskDao.save(task);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		reportTaskDao.flush();
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		TravellingClaimFormCriteria criteria = (TravellingClaimFormCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		User user = userDao.findById(userId);
		
		descBuilder.append(String.format("Staff: %s - %s", user.getStaffCode(), user.getEnglishName()));
		descBuilder.append("\n");
		descBuilder.append(String.format("Month: %s", criteria.getMonth()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
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
