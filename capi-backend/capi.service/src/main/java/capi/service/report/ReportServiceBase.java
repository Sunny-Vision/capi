package capi.service.report;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import capi.entity.ReportTask;
import capi.model.report.DocxExportModel;
import capi.model.report.JasperWorkbook;
import capi.model.report.PhantomTaskModel;
import capi.service.AppConfigService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ReportServiceBase  implements ReportService{
	
	public static final int PDF = 1;
	public static final int XLSX = 2;
	public static final int DOCX = 3;
	public static final int ZIP = 4;

	@Autowired
	private AppConfigService appConfig;
	
	public String exportReport(List<JasperPrint> prints, int taskType, String functionCode) throws Exception{
		return exportReport(prints, taskType, functionCode, false);
	}

	public String exportReport(List<JasperPrint> prints, int taskType, String functionCode, boolean separatedDoc) throws Exception{
		
		try{
			String path = appConfig.getReportLocation()+File.separator+functionCode;
		
			File dir = new File(path);
			if (!dir.exists()){
				dir.mkdirs();
			}
			//Date date = new Date();
			String ext = "";
			if (separatedDoc){
				ext = ".zip";
			}
			else if (taskType == PDF){
				ext = ".pdf";
			}
			else if (taskType == XLSX){
				ext = ".xlsx";
			}
			else if (taskType == DOCX){
				ext = ".docx";
			}
		
			String filename = UUID.randomUUID().toString()+ext;
		
			FileOutputStream oStream = new FileOutputStream(path+File.separator+filename);
			
			if (separatedDoc){
				ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(oStream));
				
				for (JasperPrint print : prints){
					ByteArrayOutputStream bStream = new ByteArrayOutputStream();
					if (taskType == PDF){
						printPdfStream(print, bStream);			
					}
					else if (taskType == XLSX){
						printExcelStream(print, bStream);
					}				
					ZipEntry entry = new ZipEntry(print.getName());
					out.putNextEntry(entry);
					out.write(bStream.toByteArray());
					bStream.close();
				}
				out.close();
			}
			else if (taskType == PDF){
				// pdf
				JRPdfExporter exporter = new JRPdfExporter();	
				exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(oStream));
				SimplePdfReportConfiguration config = new SimplePdfReportConfiguration();
				exporter.setConfiguration(config);
				exporter.exportReport();			
				oStream.flush();
				oStream.close();
			}
			else if(taskType == XLSX){
				// excel
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(oStream));
				exporter.setConfiguration(getExcelConfiguration());
				exporter.exportReport();
				oStream.flush();
				oStream.close();
			}
			
			
			return File.separator+functionCode+File.separator+filename;
		
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	
	public String exportReport(JasperPrint print, int taskType, String functionCode) throws Exception{
		
		String path = appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		//Date date = new Date();
		String ext = "";
		if (taskType == PDF){
			ext = ".pdf";
		}
		else if (taskType == XLSX){
			ext = ".xlsx";
		}
		else if (taskType == DOCX){
			ext = ".docx";
		}
		String filename = UUID.randomUUID().toString()+ext;
		FileOutputStream oStream = new FileOutputStream(path+File.separator+filename);
		
		if (taskType == PDF){
			// pdf
			printPdfStream(print, oStream);			
		}
		else if(taskType == XLSX){
			// excel
			printExcelStream(print, oStream);
		}
		oStream.flush();
		oStream.close();
		
		return File.separator+functionCode+File.separator+filename;
		
	}	
	
	protected void printPdfStream (JasperPrint print, OutputStream oStream) throws JRException{
		JasperExportManager.exportReportToPdfStream(print, oStream);
	}
	
	protected void printExcelStream(JasperPrint print, OutputStream oStream) throws JRException{
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(print));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(oStream));		
		exporter.setConfiguration(getExcelConfiguration());
		exporter.exportReport();
	}
	
	protected void printExcelStream(List<JasperPrint> prints, OutputStream oStream) throws JRException{
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(oStream));		
		exporter.setConfiguration(getExcelConfiguration());
		exporter.exportReport();
	}
	
	private SimpleXlsxReportConfiguration getExcelConfiguration(){
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		configuration.setDetectCellType(true);
		configuration.setCollapseRowSpan(false);
		configuration.setIgnoreGraphics(false);
		return configuration;
	}
	
	public String serializeObject(Object obj) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	public <T> T deserializeObject(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		return  mapper.readValue(str, clazz);
	}
	
	public InputStream getReportStream(String report) throws IOException{		
		Resource resource = new ClassPathResource("report/"+report+".jasper");
		if (!resource.exists()){
			resource = new ClassPathResource("report/"+report+".jrxml");
		}
//		Resource resource = new ClassPathResource("report/"+report+".jrxml");
		return resource.getInputStream();
	}
	
	public JasperReport getReport(String report) throws IOException, JRException{	
		if (Pattern.matches(".*\\.jasper$", report)){
			Resource resource = new ClassPathResource("report/"+report);
			return (JasperReport) JRLoader.loadObject(resource.getFile());
		}
		else if (Pattern.matches(".*\\.jrxml$", report)){
			Resource resource = new ClassPathResource("report/"+report);
			return JasperCompileManager.compileReport(resource.getInputStream());
		}
		
		Resource resource = new ClassPathResource("report/"+report+".jasper");
		if (!resource.exists()){
			resource = new ClassPathResource("report/"+report+".jrxml");
			return JasperCompileManager.compileReport(resource.getInputStream());
		}
		else{
			return (JasperReport) JRLoader.loadObject(resource.getFile());
		}
	}
	
	public String exportReport(ReportTask task, String report, Collection<?> data, Hashtable<String, Object> parameters) throws Exception{
		JasperPrint jasperPrint = this.getJasperPrint(task, report, data, parameters);
		return this.exportReport(jasperPrint, task.getReportType(), task.getFunctionCode());		
	}
	
	public JasperPrint getJasperPrint(ReportTask task, String report, Collection<?> data, Hashtable<String, Object> parameters) throws Exception{
		return getJasperPrint(task, report, data, parameters, "");
	}
	
	public JasperPrint getJasperPrint(ReportTask task, String report, Collection<?> data, Hashtable<String, Object> parameters, String filename) throws Exception{
		JRDataSource dataSource = new JRBeanCollectionDataSource(data, false);
		JasperReport jreport = this.getReport(report);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jreport, parameters, dataSource);
		if (!StringUtils.isEmpty(filename)){
			jasperPrint.setName(filename);
		}
		return jasperPrint;
	}
	
	public String exportWorkbooks(List<JasperWorkbook> workbooks, String functionCode) throws IOException, JRException{
		String path = appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		//Date date = new Date();
		String ext = ".zip";
		
		String filename = UUID.randomUUID().toString()+ext;
		FileOutputStream oStream = new FileOutputStream(path+File.separator+filename);
		
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(oStream));
		
		for (JasperWorkbook workbook : workbooks){
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			printExcelStream(workbook.getWorksheets(), bStream);
			
			ZipEntry entry = new ZipEntry(workbook.getWorkbookName());
			out.putNextEntry(entry);
			out.write(bStream.toByteArray());
			bStream.close();
		}
		out.close();
		
		return File.separator+functionCode+File.separator+filename;
	}
	
	private void replaceBookmarkInParagraph(List<XWPFParagraph> paraList, Map<String, String> data){
		Iterator<XWPFParagraph> paraIter = paraList.iterator();
        while (paraIter.hasNext()){
        	XWPFParagraph param = paraIter.next(); 
        	List<CTBookmark> bookmarkList = param.getCTP().getBookmarkStartList();
        	Iterator<CTBookmark>bookmarkIter = bookmarkList.iterator(); 
        	while(bookmarkIter.hasNext()) {
        		CTBookmark bookmark = bookmarkIter.next();
        		if (data.containsKey(bookmark.getName())){
        			
					Node node = param.getCTP().getDomNode().getFirstChild();
					int count = -1;
					while(! ( "bookmarkStart".equals(node.getLocalName()) && bookmark.getName().equals(node.getAttributes().getNamedItem("w:name").getNodeValue()) ) ) {
						node = node.getNextSibling();
						count++;
						// For param.getCTP().getDomNode().replaceChild
						if( "bookmarkStart".equals(node.getLocalName()) && (!bookmark.getName().equals(node.getAttributes().getNamedItem("w:name").getNodeValue())) )
							count--;
						if("bookmarkEnd".equals(node.getLocalName()))
							count--;
					}
					if( "bookmarkEnd".equals(node.getNextSibling().getLocalName()) || "bookmarkStart".equals(node.getNextSibling().getLocalName()) )
						count = -1;
        			
        			XWPFRun run = param.createRun();
        			
        			if(count >= 0) {
	        			CTRPr ctRPr = param.getCTP().getRList().get(count).getRPr();
	        			run.getCTR().setRPr(ctRPr);
        			}
        			
        			String value = data.get(bookmark.getName());
        			if(value.contains("\r\n")) {
        				String[] values = value.split("\r\n");
        				for(int i = 0; i < values.length; i++) {
        					run.setText(values[i]);
        					run.addBreak();
        					run.addCarriageReturn();
        				}
        				//param.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
        				// For param.getCTP().getDomNode().replaceChild
        				if(count >= 0)
    	        			param.getCTP().getDomNode().replaceChild(run.getCTR().getDomNode(), node.getNextSibling());
    	        		else
    	        			param.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
        			} else {
        				run.setText(value);
    	        		//param.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
        				// For param.getCTP().getDomNode().replaceChild
    	        		if(count >= 0)
    	        			param.getCTP().getDomNode().replaceChild(run.getCTR().getDomNode(), node.getNextSibling());
    	        		else
    	        			param.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), bookmark.getDomNode());
        			}
        		}
            }
        	
        	// finding text box in paragraph
//        	XmlObject[] textBoxObjects =  param.getCTP().selectPath(
//        			"declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' "
//        			+ "declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");
//        	for (int i =0; i < textBoxObjects.length; i++) {
//                XWPFParagraph embeddedPara = null;
//                try {
//	                XmlObject[] paraObjects = textBoxObjects[i].selectChildren(
//	                		new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));
//	                List<XWPFParagraph> embeddedParas = new ArrayList<XWPFParagraph>();
//	                for (int j=0; j<paraObjects.length; j++) {
//	                    embeddedPara = new XWPFParagraph(CTP.Factory.parse(paraObjects[j].xmlText()), param.getBody());
//	                    embeddedParas.add(embeddedPara);
//	                } 
//	                replaceBookmarkInParagraph(embeddedParas, data);
//                } catch (XmlException e) {}
//            }
        	
        }
	}
	
	private void replaceBookmarkInTable(List<XWPFTable>tables, Map<String, String> data){
		Iterator<XWPFTable> tableIter = tables.iterator();
		while (tableIter.hasNext()){
        	XWPFTable table = tableIter.next();
        	for (XWPFTableRow row : table.getRows()){
        		for (XWPFTableCell cell : row.getTableCells()){
        			replaceBookmarkInParagraph(cell.getParagraphs(), data);
        			replaceBookmarkInTable(cell.getTables(), data);
        		}
        	}
        } 
	}
	
	private void replaceBookmarkInHeaders(List<XWPFHeader>headers, Map<String, String> data){
		Iterator<XWPFHeader> headerIter = headers.iterator();
		while (headerIter.hasNext()){
			XWPFHeader header = headerIter.next();
			replaceBookmarkInParagraph(header.getParagraphs(), data);
			List<XWPFTable> tables = header.getTables();
			replaceBookmarkInTable(tables, data);
		}
	}
	
	private void replaceBookmarkInFooters(List<XWPFFooter>footers, Map<String, String> data){
		Iterator<XWPFFooter> footerIter = footers.iterator();
		while (footerIter.hasNext()){
			XWPFFooter footer = footerIter.next();
			replaceBookmarkInParagraph(footer.getParagraphs(), data);
			List<XWPFTable> tables = footer.getTables();
			replaceBookmarkInTable(tables, data);
		}
	}
	
	private XWPFDocument getWPFDocument(InputStream in, Map<String, String> data) throws IOException{

		XWPFDocument docx = new XWPFDocument(in);
        List<XWPFParagraph> paraList = docx.getParagraphs();
        replaceBookmarkInParagraph(paraList, data);
       
        List<XWPFTable> tables = docx.getTables();
        replaceBookmarkInTable(tables, data);
        replaceBookmarkInHeaders(docx.getHeaderList(), data);
        replaceBookmarkInFooters(docx.getFooterList(), data);
        
        return docx;
	}
	
	
	public void exportDocx(ReportTask task, String report, Map<String, String> data, String functionCode, OutputStream oStream) throws IOException{
		Resource resource = new ClassPathResource("report/"+report+".docx");
		InputStream in = new FileInputStream(resource.getFile().getPath());		
		XWPFDocument docx = getWPFDocument(in, data);        
        docx.write(oStream);
	}
	
	public void mergeDocx(OutputStream os, InputStream[] streams) throws Exception {
		if (streams.length == 0){
			return;
		}
		else if (streams.length == 1){
			IOUtils.copy(streams[0], os);
			return;
		}
		
        WordprocessingMLPackage target = WordprocessingMLPackage.load(streams[0]);
        
        for (int i = 1; i < streams.length; i++){
        	insertDocx(target.getMainDocumentPart(), IOUtils.toByteArray(streams[i]), i);
        }
        
        Save save = new Save(target);
        save.save(os);
    }

    private void insertDocx(MainDocumentPart main, byte[] bytes, int index) throws Exception {
    	String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + index + ".docx"));
        afiPart.setContentType(new ContentType(contentType));
        afiPart.setBinaryData(bytes);
        Relationship altChunkRel = main.addTargetPart(afiPart);

        CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
        chunk.setId(altChunkRel.getId());

        main.addObject(chunk);
    }
	
	public String exportDocx(ReportTask task, String report, Map<String, String> data, String functionCode) throws IOException{		
        String path = appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		//Date date = new Date();
		String filename = UUID.randomUUID().toString()+".docx";
		FileOutputStream oStream = new FileOutputStream(path+File.separator+filename);
        
		exportDocx(task, report, data, functionCode, oStream);
        return File.separator+functionCode+File.separator+filename;
	}
	
	public String exportMultiDocx(ReportTask task, String report, List<DocxExportModel> data, String functionCode) throws IOException, JRException{
		return exportMultiDocx(task, report, data, functionCode, null);
	}
	
	public String exportMultiDocx(ReportTask task, String report, List<DocxExportModel> data, String functionCode, JasperPrint print) throws IOException, JRException{
		String path = appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		
		//Date date = new Date();
		String filename = UUID.randomUUID().toString()+".zip";
		FileOutputStream dest = new FileOutputStream(path+File.separator+filename);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		
		for (DocxExportModel model : data){
			Resource resource = new ClassPathResource("report/"+report+".docx");
			InputStream in = new FileInputStream(resource.getFile().getPath());
			
			XWPFDocument docx = getWPFDocument(in, model.getData());
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			docx.write(oStream);
			docx.close();
			
			ZipEntry entry = new ZipEntry(model.getFilename());
			out.putNextEntry(entry);
			out.write(oStream.toByteArray());
			oStream.close();
			
		}
		if(print != null) {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			printExcelStream(print, bStream);
			
			ZipEntry entry = new ZipEntry(print.getName());
			out.putNextEntry(entry);
			out.write(bStream.toByteArray());
			bStream.close();
		}
		out.close();
		return File.separator+functionCode+File.separator+filename;
	}
	
	private void executePhantom(String jsReport, OutputStream outStream, List<String> parameters) throws IOException, InterruptedException{
		File temp = File.createTempFile("phantom", ".pdf"); 
		Resource phantom = new ClassPathResource("phantomjs.exe");
		String phantomPath = phantom.getFile().getPath();
		Resource resource = new ClassPathResource("report/"+jsReport+".js");
		String jsPath = resource.getFile().getPath();
		
		String command = phantomPath+" \""+jsPath+"\" \""+temp+"\"";
		if (parameters != null && parameters.size() > 0){
			for (String param : parameters){
				command += " \""+param.replace("\"", "\\\"")+"\"";
			}
		}
		
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		
		Files.copy(temp.toPath(), outStream);
		temp.delete();
	}
	
	public String exportPhantomReport(ReportTask task, String report, String webUrl, List<String> parameters, String filepath) throws IOException, InterruptedException{
		String functionCode = task.getFunctionCode();
		String path = !StringUtils.isEmpty(filepath)?filepath:appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		String ext = ".pdf";
		//Date date = new Date();
		
		
		String filename = UUID.randomUUID().toString()+ext;
		FileOutputStream oStream = !StringUtils.isEmpty(filepath)?new FileOutputStream(path):new FileOutputStream(path+File.separator+filename);
		
		String port = appConfig.getServerPort();
		String url = webUrl;
		if (url.startsWith("/")){
			url = "http://127.0.0.1:"+port+webUrl;
		}
		List<String> list = new ArrayList<String>();
		list.add(url);
		if (parameters != null && parameters.size() > 0){
			list.addAll(parameters);			
		}
		executePhantom(report, oStream, list);
		return File.separator+functionCode+File.separator+filename;
	}
	
	
	public String exportPhantomReports(ReportTask task, List<PhantomTaskModel> phantomTasks, List<String> parameters) throws IOException, InterruptedException{
		String functionCode = task.getFunctionCode();
		String path = appConfig.getReportLocation()+File.separator+functionCode;
		
		File dir = new File(path);
		if (!dir.exists()){
			dir.mkdirs();
		}
		String ext = ".zip";
		//Date date = new Date();
		String filename = UUID.randomUUID().toString()+ext;
		
		FileOutputStream dest = new FileOutputStream(path+File.separator+filename);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		
		for (PhantomTaskModel phantomTask : phantomTasks){
			
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			
			String port = appConfig.getServerPort();
			String url = phantomTask.getWebUrl();
			if (url.startsWith("/")){
				url = "http://127.0.0.1:"+port+url;
			}
			List<String> list = new ArrayList<String>();
			list.add(url);
			if (phantomTask.getParameters() != null && phantomTask.getParameters().size() > 0){
				list.addAll(phantomTask.getParameters());			
			}
			if (parameters != null && parameters.size() > 0){
				list.addAll(parameters);			
			}
			executePhantom(phantomTask.getReport(), bStream, list);
			
			ZipEntry entry = new ZipEntry(phantomTask.getFilename());
			out.putNextEntry(entry);
			out.write(bStream.toByteArray());
			bStream.close();
			
		}
		out.close();
		
		
		return File.separator+functionCode+File.separator+filename;
	}
	
	
	 
	
	
}
