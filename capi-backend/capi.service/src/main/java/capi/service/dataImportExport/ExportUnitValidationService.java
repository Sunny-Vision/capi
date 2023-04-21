package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.OnSpotValidationDao;
import capi.entity.ImportExportTask;
import capi.entity.OnSpotValidation;
import capi.entity.Unit;

@Service("ExportUnitValidationService")
public class ExportUnitValidationService extends DataExportServiceBase{
	
	@Autowired
	private OnSpotValidationDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;

	private static final String[] headers = new String []
			{"Variety Id", "Variety Code", "Variety Chinese Name", "Variety English Name",
			"Is Uom1 Reported", "Is Uom2 >= 0", "Is N Price > 0",
			"Is S Price > 0",
			"Is Reason Provided if Change of N Price > a%", "% Change of N Price Upper Threshold (a)",
			"Is Reason Provided if Change of N Price < b%", "% Change of N Price Lower Threshold (b)",
			"Is Reason Provided if Change of S Price > c%", "% Change of S Price Upper Threshold (c)",
			"Is Reason Provided if Change of S Price < d%", "% Change of S Price Lower Threshold (d)",
			"Is Reason Provided if S Price > Max or < Min", 
			"Is Reason Provided if N Price > Max or < Min",
			"Is N Price >= S Price", 
			"Is Remark Provided if Not Applicable is selected",
			"Lock fields if Not Available is selected", 
			"Reminder For Pricing Cycle",
			"Is Reason Provided if % Change of S Price exceeds interval","S Price Positive SD Multiplier", "S Price Negative SD Multiplier","S Price No. of Previous Months",
			"Is Reason Provided if % Change of N Price exceeds interval","N Price Positive SD Multiplier", "N Price Negative SD Multiplier","N Price No. of Previous Months",
			"Is Reason Provided if S price exceed the ranges","S Price Upper Bound","S Price Lower Bound",
			"Is Reason Provided if N price exceed the ranges","N Price Upper Bound","N Price Lower Bound"};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 11;
	}

	/**
	 * 0/UnitId
	 * 
	 * 4/Is Uom1 Reported
	 * 5/Is Uom2 Greater Zero
	 * 6/Is N Price Greater Zero
	 * 7/Is S Price Greater Zero
	
	 *	8/Is Provide Reason % Change of N Price
	 *	9/% Change of N Price Threshold
	 *	10/Is Provide Reason % Change of N Price Lower
	 *	11/% Change of N Price Lower Threshold
	
	 *	12/Is Provide Reason % Change of S Price
	 *	13/% Change of S Price Threshold
	 *  14/Is Provide Reason % Change of S Price Lower
	 *  15/% Change of S Price LowerThreshold
	
	 *	16/Is Provide Reason S Price > Max or < Min
	 *	17/Is Provide Reason N Price > Max or < Min
	
	 *  18/Is N Price Greater Than S Price
	 *  19/Is Provide Remark For Not Suitable Price
	 *	20/Is Provide Remark For Not Available Quotation
	 *  21/Is Reminder For Pricing Cycle
	
	 *  22/Is Provide Reason % Change of S Price SD
	 *  23/S Price SD Positive
	 *  24/S Price SD Negative
	 *	25/S Price Month
	    	
	 *  26/Is Provide Reason % Change of N Price SD
	 *  27/N Price SD Positive
	 *  28/N Price SD Negative
	 *	29/N Price Month
    		
	 *	30/Is Provide Reason if S price exceed the ranges
	 *	31/S Price SD Positive
	 *	32/S Price SD Negative
	
	 *	33/Is Provide Reason if N price exceed the ranges
	 *	34/N Price SD Positive
	 *	35/N Price SD Negative
	 *
	 * 1/ Unit Code
	 * 2/ Unit Chinese Name
	 * 3/ Unit English Name
	 *
	 */
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		ImportExportTask task = taskDao.findById(taskId);

		ScrollableResults results = dao.getAllOnSpotValidationResult(task.getCpiBasePeriod());

		
		int rowCnt = 1;
		while (results.next()){
			OnSpotValidation entity = (OnSpotValidation)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(entity.getId()));
			
			//1	Unit of measurement (UOM1) should be reported
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(entity.isUom1Reported());
			
			//2	Unit of measurement (UOM2) >= 0
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(entity.isUom2GreaterZero());

			//3	Normal Price (N Price) > 0
			SXSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(entity.isNPriceGreaterZero());

			//4	Special Price (S Price) > 0
			SXSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(entity.isSPriceGreaterZero());

			//5	Provide Reason if percentage change of N Price >
			SXSSFCell cell8 = row.createCell(8);
			cell8.setCellValue(entity.isProvideReasonPRNPrice());
			SXSSFCell cell9 = row.createCell(9);
			if (entity.getPrNPriceThreshold() != null){
				cell9.setCellValue(String.valueOf(entity.getPrNPriceThreshold()));
			}
			
			//7	Provide Reason if percentage change of N Price <
			SXSSFCell cell10 = row.createCell(10);
			cell10.setCellValue(entity.isProvideReasonPRNPriceLower());
			SXSSFCell cell11 = row.createCell(11);
			if (entity.getPrNPriceLowerThreshold() != null){
				cell11.setCellValue(String.valueOf(entity.getPrNPriceLowerThreshold()));
			}
			
			//9 Provide Reason if percentage change of S Price >
			SXSSFCell cell12 = row.createCell(12);
			cell12.setCellValue(entity.isProvideReasonPRSPrice());
			SXSSFCell cell13 = row.createCell(13);
			if (entity.getPrSPriceThreshold() != null){
				cell13.setCellValue(String.valueOf(entity.getPrSPriceThreshold()));
			}
			
			//11 Provide Reason if percentage change of S Price <
			SXSSFCell cell14 = row.createCell(14);
			cell14.setCellValue(entity.isProvideReasonPRSPriceLower());
			SXSSFCell cell15 = row.createCell(15);
			if (entity.getPrSPriceLowerThreshold() != null){
				cell15.setCellValue(String.valueOf(entity.getPrSPriceLowerThreshold()));
			}
			
			//13 Provide Reason if S price is > max or < min S price of the same Unit in the last month
			SXSSFCell cell16 = row.createCell(16);
			cell16.setCellValue(entity.isProvideReasonSPriceMaxMin());
			
			//14 Provide Reason if N price is > max or < min N price of the same Unit in the last month
			SXSSFCell cell17 = row.createCell(17);
			cell17.setCellValue(entity.isProvideReasonNPriceMaxMin());
			
			//15 Normal price (N Price) >= Special Price (S Price)
			SXSSFCell cell18 = row.createCell(18);
			cell18.setCellValue(entity.isnPriceGreaterSPrice());
			
			
			//16 If "Not suitable" is chosen for N and S price, remarks have to be provided.
			SXSSFCell cell19 = row.createCell(19);
			cell19.setCellValue(entity.isProvideRemarkForNotSuitablePrice());

			//17 If the quotation record is "Not available", no field except remarks should be filled.
			SXSSFCell cell20 = row.createCell(20);
			cell20.setCellValue(entity.isProvideRemarkForNotAvailableQuotation());
			
			//18 If the pricing cycle of a product is longer than the specified product cycle for different Units, reminder will be provided.
			SXSSFCell cell21 = row.createCell(21);
			cell21.setCellValue(entity.isReminderForPricingCycle());

			//19 Provide Reason if percentage change of S price exceed the ranges of (mean +/- S.D.) in last y month
			SXSSFCell cell22 = row.createCell(22);
			cell22.setCellValue(entity.isProvideReasonPRSPriceSD());
			SXSSFCell cell23 = row.createCell(23);
			if ( entity.getPrSPriceSDPositive() != null){
				cell23.setCellValue(String.valueOf(entity.getPrSPriceSDPositive()));
			}
			SXSSFCell cell24 = row.createCell(24);
			if ( entity.getPrSPriceSDNegative() != null){
				cell24.setCellValue(String.valueOf(entity.getPrSPriceSDNegative()));
			}
			SXSSFCell cell25 = row.createCell(25);
			if ( entity.getPrSPriceMonth() != null){
				cell25.setCellValue(String.valueOf(entity.getPrSPriceMonth()));
			}
			
			//Provide Reason if percentage change of N price exceed the ranges of (mean +/- S.D.) in last y month
			SXSSFCell cell26 = row.createCell(26);
			cell26.setCellValue(entity.isProvideReasonPRNPriceSD());
			SXSSFCell cell27 = row.createCell(27);
			if ( entity.getPrNPriceSDPositive() != null){
				cell27.setCellValue(String.valueOf(entity.getPrNPriceSDPositive()));
			}
			if ( entity.getPrNPriceSDNegative() != null){
			SXSSFCell cell28 = row.createCell(28);
				cell28.setCellValue(String.valueOf(entity.getPrNPriceSDNegative()));
			}
			SXSSFCell cell29 = row.createCell(29);
			if ( entity.getPrNPriceMonth() != null){
				cell29.setCellValue(String.valueOf(entity.getPrNPriceMonth()));
			}

			//Provide Reason if S price exceed the ranges of (x - y) in last k months
			SXSSFCell cell30 = row.createCell(30);
			cell30.setCellValue(entity.isProvideReasonSPriceSD());
			SXSSFCell cell31 = row.createCell(31);
			if ( entity.getsPriceSDPositive() != null){
				cell31.setCellValue(String.valueOf(entity.getsPriceSDPositive()));
			}
			SXSSFCell cell32= row.createCell(32);
			if ( entity.getsPriceSDNegative() != null){
				cell32.setCellValue(String.valueOf(entity.getsPriceSDNegative()));
			}
			
			//Provide Reason if N price exceed the ranges of (x - y) in last k months
			SXSSFCell cell33 = row.createCell(33);
			cell33.setCellValue(entity.isProvideReasonNPriceSD());
			SXSSFCell cell34 = row.createCell(34);
			if ( entity.getnPriceSDPositive() != null){
				cell34.setCellValue(String.valueOf(entity.getnPriceSDPositive()));
			}
			SXSSFCell cell35 = row.createCell(35);
			if ( entity.getnPriceSDNegative() != null){
				cell35.setCellValue(String.valueOf(entity.getnPriceSDNegative()));
			}
			
			//For Unit
			Unit unit = entity.getUnit();
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(unit.getCode());
			
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(unit.getChineseName());
			
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(unit.getEnglishName());
			
			dao.evit(entity);
			rowCnt++;
			
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}	
		results.close();

		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			task.setFilePath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		taskDao.flush();
	}
	
	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
}
