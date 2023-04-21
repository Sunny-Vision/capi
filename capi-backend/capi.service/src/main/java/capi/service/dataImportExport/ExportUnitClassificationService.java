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
import capi.dal.UnitDao;
import capi.entity.Group;
import capi.entity.ImportExportTask;
import capi.entity.Item;
import capi.entity.OutletType;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.Section;
import capi.entity.SubGroup;
import capi.entity.SubItem;
import capi.entity.Unit;
import capi.service.CommonService;

@Service("ExportUnitClassificationService")
public class ExportUnitClassificationService extends DataExportServiceBase{
	
	@Autowired
	private UnitDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String []{
			"Variety Id", "Variety Code", "Variety Chinese Name", "Variety English Name", "Display Name"
			, "Cpi Base Period", "Purpose Code", "Variety Status", "Product Group Id", "Product Group Code"
			, "Section Id", "Section Code", "Section Chinese Name", "Section English Name"
			, "Group Id", "Group Code", "Group Chinese Name", "Group English Name"
			, "SubGroup Id", "SubGroup Code", "SubGroup Chinese Name", "SubGroup English Name"
			, "Item Id", "Item Code", "Item Chinese Name", "Item English Name"
			, "OutletType Id", "OutletType Code", "OutletType Chinese Name", "OutletType English Name"
			, "SubItem Id", "SubItem Code", "SubItem Chinese Name", "SubItem English Name", "SubItem Compilation Method"
			, "Obsolete Date", "Effective Date"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 12;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		String cpiBasePeriod = task.getCpiBasePeriod();
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getUnitClassificationResult(cpiBasePeriod);
		
		/**
		 *  0/Unit Id 
		 *  1/Unit Code
		 *  2/Unit Chinese Name
		 *  3/Unit English Name
		 *  4/Unit Display Name
		 *  5/Unit Cpi Base Period
		 *  
		 *  7/Unit Status
		 *  
		 *  35/Obsolete Date
		 *  36/Effective Date
		 *  
		 *  6/Purpose Code
		 *  
		 *  8/ProductGroup Id
		 *  9/ProductGroup Code
		 * 
		 *  10/Section Id
		 *  11/Section Code
		 *  12/Section Chinese Name
		 *  13/Section English Name
		 * 
		 *  14/Group Id
		 *  15/Group Code
		 *  16/Group Chinese Name
		 *  17/Group English Name
		 *  
		 *  18/SubGroup Id
		 *  19/SubGroup Code
		 *  20/SubGroup Chinese Name
		 *  21/SubGroup English Name
		 *  
		 *  22/Item Id
		 *  23/Item Code
		 *  24/Item Chinese Name
		 *  25/Item English Name
		 *  
		 *  26/OutletType Id
		 *  27/OutletType Code
		 *  28/OutletType Chinese Name
		 *  29/OutletType English Name
		 *  
		 *  30/SubItem Id
		 *  31/SubItem Code
		 *  32/SubItem Chinese Name
		 *  33/SubItem English Name
		 *  34/SubItem Compilation Method
		 *  
		 *  
		 *  
		 *  Table :
		 *  Main: Unit
		 *  Foreign: Purpose
		 *  		 SubItem
		 *  		 OutletType
		 *  		 Item
		 *  		 SubGroup
		 *  		 Group
		 *  		 Section
		 *  		 Product Group
		 **/
		
		
		int rowCnt = 1;
		while (results.next()){
			Unit code = (Unit)results.get()[0];
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			//For Unit
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(code.getId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(code.getCode());
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(code.getChineseName());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(code.getEnglishName());
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(code.getDisplayName());
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(code.getCpiBasePeriod());
			
			SXSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(code.getStatus());
			
			SXSSFCell cell35 = row.createCell(35);
			if(code.getObsoleteDate()!=null){
				cell35.setCellValue(commonService.formatDate(code.getObsoleteDate()));
			}
			SXSSFCell cell36 = row.createCell(36);
			if(code.getEffectiveDate()!=null){
				cell36.setCellValue(commonService.formatDate(code.getEffectiveDate()));
			}
			
			//For Purpose
			if(code.getPurpose()!=null){
				Purpose purpose = code.getPurpose();
				SXSSFCell cell6 = row.createCell(6);
				cell6.setCellValue(purpose.getCode());
				dao.evit(purpose);
			}
			
			//For Product Group
			if(code.getProductCategory()!=null){
				ProductGroup productGroup = code.getProductCategory();
				SXSSFCell cell8 = row.createCell(8);
				cell8.setCellValue(String.valueOf(productGroup.getProductGroupId()));
				SXSSFCell cell9 = row.createCell(9);
				cell9.setCellValue(productGroup.getCode());
				dao.evit(productGroup);
			}
			
			//For SubItem
			if(code.getSubItem()!=null){
				SubItem subItem = code.getSubItem();
				SXSSFCell cell30 = row.createCell(30);
				cell30.setCellValue(String.valueOf(subItem.getId()));
				SXSSFCell cell31 = row.createCell(31);
				cell31.setCellValue(subItem.getCode());
				SXSSFCell cell32 = row.createCell(32);
				cell32.setCellValue(subItem.getChineseName());
				SXSSFCell cell33 = row.createCell(33);
				cell33.setCellValue(subItem.getEnglishName());
				SXSSFCell cell34 = row.createCell(34);
				cell34.setCellValue(String.valueOf(subItem.getCompilationMethod()));
				
				//For OutletType
				if(subItem.getOutletType()!=null){
					OutletType outletType = subItem.getOutletType();
					SXSSFCell cell26 = row.createCell(26);
					cell26.setCellValue(String.valueOf(outletType.getId()));
					SXSSFCell cell27 = row.createCell(27);
					cell27.setCellValue(outletType.getCode());
					SXSSFCell cell28 = row.createCell(28);
					cell28.setCellValue(outletType.getChineseName());
					SXSSFCell cell29 = row.createCell(29);
					cell29.setCellValue(outletType.getEnglishName());
					
					//For Item
					
					if(outletType.getItem()!=null){
						Item item = outletType.getItem();
						SXSSFCell cell22 = row.createCell(22);
						cell22.setCellValue(String.valueOf(item.getId()));
						SXSSFCell cell23 = row.createCell(23);
						cell23.setCellValue(item.getCode());
						SXSSFCell cell24 = row.createCell(24);
						cell24.setCellValue(item.getChineseName());
						SXSSFCell cell25 = row.createCell(25);
						cell25.setCellValue(item.getEnglishName());
						
						//For SubGroup
						if(item.getSubGroup()!=null){
							SubGroup subGroup = item.getSubGroup();
							SXSSFCell cell18 = row.createCell(18);
							cell18.setCellValue(String.valueOf(subGroup.getId()));
							SXSSFCell cell19 = row.createCell(19);
							cell19.setCellValue(subGroup.getCode());
							SXSSFCell cell20 = row.createCell(20);
							cell20.setCellValue(subGroup.getChineseName());
							SXSSFCell cell21 = row.createCell(21);
							cell21.setCellValue(subGroup.getEnglishName());
							
							//For Group
							if(subGroup.getGroup()!=null){
								Group group = subGroup.getGroup();
								SXSSFCell cell14 = row.createCell(14);
								cell14.setCellValue(String.valueOf(group.getId()));
								SXSSFCell cell15 = row.createCell(15);
								cell15.setCellValue(group.getCode());
								SXSSFCell cell16 = row.createCell(16);
								cell16.setCellValue(group.getChineseName());
								SXSSFCell cell17 = row.createCell(17);
								cell17.setCellValue(group.getEnglishName());
								
								//For Section
								if(group.getSection()!=null){
									Section section = group.getSection();
									SXSSFCell cell10 = row.createCell(10);
									cell10.setCellValue(String.valueOf(section.getId()));
									SXSSFCell cell11 = row.createCell(11);
									cell11.setCellValue(section.getCode());
									SXSSFCell cell12 = row.createCell(12);
									cell12.setCellValue(section.getChineseName());
									SXSSFCell cell13 = row.createCell(13);
									cell13.setCellValue(section.getEnglishName());
									dao.evit(section);
								}
								dao.evit(group);
							}
							dao.evit(subGroup);
						}
						dao.evit(item);
					}
					dao.evit(outletType);
				}
				dao.evit(subItem);
			}
			
			dao.evit(code);
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
