package capi.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import capi.dal.UOMConversionDao;
import capi.entity.UOMConversion;
import capi.entity.Unit;
import capi.entity.Uom;
import capi.model.SystemConstant;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.Payload;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

@Service("CommonService")
public class CommonService {

	@Autowired
	UOMConversionDao uomConversionDao;
	@Autowired
	AppConfigService appConfig;
		
	/**
	 * Date time format in CAPI should be dd-MM-yyyy HH:mm:ss
	 * @param date
	 * @return formatted date time
	 */
	public String formatDateTime(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_TIME_FORMAT);
		return format.format(date);
	}
	
	/**
	 * Date format in CAPI should be dd/MM/yyyy
	 * @param date
	 * @return formatted date 
	 */	
	public String formatReportDate(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.REPORT_DATE_FORMAT);
		return format.format(date);
	}
	
	/**
	 * Date format in CAPI should be dd-MM-yyyy
	 * @param date
	 * @return formatted date 
	 */	
	public String formatDate(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
		return format.format(date);
	}
	
	/**
	 * Date format yyyy-MM-dd for String sql
	 * @param date
	 * @return formatted date 
	 */	
	public String formatDateStr(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.REPORT_REFERENCE_DATE_FORMAT);
		return format.format(date);
	}
	
	/**
	 * get Date object from string. String should follow CAPI format
	 * @param dateStr the format should be dd-MM-yyyy HH:mm:ss
	 * @return date object
	 * @throws ParseException 
	 */
	public Date getDateTime(String dateStr) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_TIME_FORMAT);
		return format.parse(dateStr);
	}
	
	/**
	 * get Date object from string. String should follow CAPI format
	 * @param dateStr the format should be dd-MM-yyyy
	 * @return date object
	 * @throws ParseException 
	 */
	public Date getDate(String dateStr) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
		return format.parse(dateStr);
	}
	
	/**
	 *  get Month name and Year format in MMMM yyyy
	 * @param dateStr the format should be MM-yyyy 
	 * @return formatted dateStr
	 * @throws ParseException 
	 */
	public String formatMonthName(String dateStr) throws ParseException{
		SimpleDateFormat oformat = new SimpleDateFormat(SystemConstant.MONTH_FORMAT);
		SimpleDateFormat nformat = new SimpleDateFormat(SystemConstant.SHORT_MONTH_FORMAT);
		return nformat.format(oformat.parse(dateStr));
	}
	
	/**
	 *  Time format in CAPI should be HH:mm
	 * @param date
	 * @return formatted time
	 */
	public String formatTime(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.TIME_FORMAT);
		return format.format(date);
	}
	
	/**
	 * get Date object from time string. String should follow CAPI format
	 * @param timeStr the format should be HH:mm
	 * @return date object
	 * @throws ParseException
	 */
	public Date getTime(String timeStr) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.TIME_FORMAT);
		return format.parse(timeStr);
	}
	
	/**
	 *  Month format in CAPI should be MM-yyyy
	 * @param date
	 * @return formatted time
	 */
	public String formatMonth(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.MONTH_FORMAT);
		return format.format(date);
	}
	
	/**
	 *  Report reference month format in yyyyMM
	 * @param date
	 * @return formatted time
	 */
	public String formatShortMonth(Date date){
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		return format.format(date);
	}
	
	/**
	 * get Date object from month string. String should follow CAPI format
	 * @param timeStr the format should be MM-yyyy
	 * @return date object
	 * @throws ParseException
	 */
	public Date getMonth(String monthStr) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(SystemConstant.MONTH_FORMAT);
		return format.parse(monthStr);
	}
	
	/**
	 * get the Date object and truncate the time part
	 * @param date 
	 * @return date with truncated time
	 */
	public Date getDateWithoutTime(Date date){
		return DateUtils.truncate(date, Calendar.DATE);
	}
	
	/**
	 * get the reference month from date object
	 * @param date
	 * @return the reference month of the date (set the day to be 1)
	 */
	public Date getReferenceMonth(Date date){
		return DateUtils.truncate(date, Calendar.MONTH);
	}
	
	/**
	 * Convert UOM
	 */
	public Double convertUom(Unit unit, Double price, Uom baseUom, Double quotationRecordUomValue) {
		try {
			Uom targetUom = unit.getStandardUOM();
			UOMConversion entity  = uomConversionDao.getUOMConversionByBaseAndTarget(baseUom.getId(), targetUom.getId());
			double factor = entity.getFactor();
			double uomValue = unit.getUomValue();
			double result = price * factor / quotationRecordUomValue * uomValue;
			return result;
		} catch (Exception e) {}
		return price;
	}
	
	/**
	 * get the session from time string
	 * @param time string
	 * @return the session (A, P, E)
	 */
	public String getSession(String time){
		Integer timeInMinute = Integer.parseInt(time.split(":")[0])*60 + Integer.parseInt(time.split(":")[1]);
		if (timeInMinute >= 360 && timeInMinute < 750 ) {
			return "A";
		} else if (timeInMinute >= 750 && timeInMinute < 1080 ) {
			return "P";
		} else {
			return "E";
		}
	}
	
	/**
	 * change line break to br
	 */
	public String nl2br(String text) {
		if (text == null) return null;
		return text.replaceAll("\n", "<br/>");
	}
	
	/**
	 * format collection method
	 */
	public String getCollectionMethod(Integer collectionMethodId) {
		switch (collectionMethodId) {
			case 1:
				return "Field visit";
			case 2:
				return "Telephone";
			case 3:
				return "Fax";
			default:
				return "Others";	
		}
	}
	
	
	public String jsonEncode(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public String httpGetRequest(String url) throws IOException{
		InputStream in = httpGetRequestStream(url);
		return IOUtils.toString(in, "UTF-8"); 
	}
	
	public InputStream httpGetRequestStream(String url) throws IOException{
		CapiProxySelector.init(appConfig);
		URL query = new URL(url);
		URLConnection conn = null;
		conn = query.openConnection();

		InputStream in = conn.getInputStream();
		return in;
	}
	
	public JsonNode getJSON(String url) throws IOException{
		String response = httpGetRequest(url);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(response);		
	}
	
	
	public PushedNotifications sendAPNsPushMessage(Payload payload, String certPath, String password, boolean production, Object devices) throws CommunicationException, KeystoreException, FileNotFoundException, URISyntaxException{
		PushedNotifications notifications = new PushedNotifications();
		if (payload == null) return notifications;
		PushNotificationManager pushManager = null;
		CapiProxySelector.init(appConfig);
		try {
			List<Proxy> proxies = ProxySelector.getDefault().select(new URI("https://wwww.google.com"));
			
			for (Proxy proxy : proxies){
				System.out.println(proxy.address().toString());
				FileInputStream in = new FileInputStream(certPath);				
				pushManager = new PushNotificationManager();
				AppleNotificationServer server = new AppleNotificationServerBasicImpl(in, password, production);
				
				if (proxy.equals(Proxy.NO_PROXY)){									
					pushManager.initializeConnection(server);
					break;
				}
				
				try{									
					InetSocketAddress addr= (InetSocketAddress)proxy.address();
					server.setProxy(addr.getHostName(), addr.getPort());
					pushManager.initializeConnection(server);	
					break;
				}
				catch(Exception ex){
					pushManager.stopConnection();
					String uri = "";
					if (production){
						uri = "https://"+AppleNotificationServer.PRODUCTION_HOST+":"+AppleNotificationServer.PRODUCTION_PORT;
					}
					else{
						uri = "https://"+AppleNotificationServer.DEVELOPMENT_HOST+":"+AppleNotificationServer.DEVELOPMENT_PORT;
					}
					ProxySelector.getDefault().connectFailed(new URI(uri), proxy.address(), null);
				}
			}
			List<Device> deviceList = Devices.asDevices(devices);
			notifications.setMaxRetained(deviceList.size());
			for (Device device : deviceList) {
				try {
					BasicDevice.validateTokenFormat(device.getToken());
					PushedNotification notification = pushManager.sendNotification(device, payload, false);
					notifications.add(notification);
				} catch (InvalidDeviceTokenFormatException e) {
					notifications.add(new PushedNotification(device, payload, e));
				}
			}
		} finally {
			try {
				pushManager.stopConnection();
			} catch (Exception e) {
			}
		}
		return notifications;
	}
	
	public <T> List<List<T>> splitListByMaxSize(List<T> rawList) {
		return splitListByMaxSize(2000, rawList);
	}

	public <T> List<List<T>> splitListByMaxSize(int maxSize, List<T> rawList) {
		List<List<T>> partitionList = new ArrayList<List<T>>();
		
		int lastIndex = 0;
		int rawListLen = rawList.size();
		while (lastIndex < rawListLen) {
			List<T> subList = null;
			int newLastIndex = -1;
			if (lastIndex + maxSize <= rawListLen) {
				newLastIndex = lastIndex + maxSize;
			} else {
				newLastIndex = rawListLen;
			}
			subList = rawList.subList(lastIndex, newLastIndex);
			
			// process list item logic start
			partitionList.add(subList);
			// process list item logic end
			
			lastIndex = newLastIndex;
		}
		return partitionList;
	}
	
	public Double round2dp(Double number) {
		if (number == null)
			return null;
		return BigDecimal.valueOf(number).setScale(2,RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * String equals
	 */
	public boolean stringEquals(String s1, String s2) {
		if (StringUtils.equals(StringUtils.stripToNull(s1), StringUtils.stripToNull(s2)))
			return true;
		else
			return false;
	}
	
	/**
	 * Count Difference Month without Date
	 */
	public Integer countDifferencesBetweenMonth(Date d1, Date d2){
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(d1);
		endCalendar.setTime(d2);
		
		int diffYear = startCalendar.get(Calendar.YEAR) - endCalendar.get(Calendar.YEAR);
		return diffYear * 12 + startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH);
	}
	
	/**
	 * Get Tel No. Format: XXXX XXXX
	 * @param String
	 * @return formatted tel 
	 */	
	public String formatTelNo(String telNo){
		if (telNo == null) return "";
		Iterable<String> splitTelNo = Splitter.fixedLength(4).split(telNo);
		String[] splitedTelNo = Iterables.toArray(splitTelNo, String.class);
		return (splitedTelNo.length == 2) ? splitedTelNo[0] + " " + splitedTelNo[1] : "";
	}

	// Compress image size and convert to JPG file type
	public void ImageCompression(InputStream imageStream, Path path) throws IOException {
		InputStream newInputStream = null;

		
		ImageInputStream iis = ImageIO.createImageInputStream(imageStream);
		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
		if (!iter.hasNext()) {

			throw new RuntimeException("No readers found!");

		}
		ImageReader reader = iter.next();
		
		System.out.println("Format: " + reader.getFormatName());
		 
		// Check Image type
		boolean isJPG = false;
		int fileSize = imageStream.available();
		
		System.out.println("File Size :: " + fileSize);


		BufferedImage image = ImageIO.read(iis);
		
		/*
		byte b[] = new byte[fileSize];
		imageStream.read(b);
		InputStream is = new ByteArrayInputStream(b);
		String mimeType = URLConnection.guessContentTypeFromStream(is);
		System.out.println(mimeType);
		*/
		
		if (reader.getFormatName().toUpperCase().equals("JPEG")) {
			isJPG = true;
		}
		
		File output = new File(path.toString());
		
		if (!isJPG) {
			// Convert image to JPG

			//BufferedImage image = ImageIO.read(imageStream);

			BufferedImage newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(newBufferedImage, "JPEG", os);

			newInputStream = new ByteArrayInputStream(os.toByteArray(), 0, os.size());

			int imageSize = 0;
			imageSize = newInputStream.available()/308;

			System.out.println("Size New :: " + imageSize);

			BufferedImage bufferImage = ImageIO.read(newInputStream);
			
			OutputStream out = new FileOutputStream(output);

			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			writer.setOutput(ios);

			/*
			 * Image Compression Level 500 kb or above - 0.03f ~300 kb - 0.05f ~200 kb or
			 * below - 0.1f
			 */

			float compressionLevel = 1f;
			
			/*
			if (imageSize > 800) {
				compressionLevel = 0.01f;
			} else if (imageSize > 500 && imageSize < 800) {
				compressionLevel = 0.02f;
			} else if (imageSize > 300 && imageSize < 500) {
				compressionLevel = 0.05f;
			} else {
				compressionLevel = 0.1f;
			}
			*/
			if (imageSize > 250) {

				if (imageSize >= 2500) {
					compressionLevel = 0.1f;
				} else if (imageSize >= 2300 && imageSize <= 2499) {
					compressionLevel = 0.05f;
				} else if (imageSize >= 2000 && imageSize <= 2299) {
					compressionLevel = 0.1f;
				} else if (imageSize >= 1800 && imageSize <= 1999) {
					compressionLevel = 0.2f;
				} else if (imageSize >= 1500 && imageSize <= 1799) {
					compressionLevel = 0.3f;
				} else if (imageSize >= 1300 && imageSize <= 1499) {
					compressionLevel = 0.4f;
				} else if (imageSize >= 1000 && imageSize <= 1299) {
					compressionLevel = 0.5f;
				} else if (imageSize >= 800 && imageSize <= 999) {
					compressionLevel = 0.6f;
				} else if (imageSize >= 600 && imageSize <= 799) {
					compressionLevel = 0.7f;
				} else if (imageSize >= 400 && imageSize <= 599) {
					compressionLevel = 0.75f;
				} else {
					compressionLevel = 0.8f;
				}
			}
			
			System.out.println("CompressionLevel ::  " + compressionLevel);

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				// param.setCompressionQuality(0.03f);
				param.setCompressionQuality(compressionLevel);
			}

			writer.write(null, new IIOImage(bufferImage, null, null), param);

			out.close();
			ios.close();
			writer.dispose();
		} else {

			// Check Image Size
			int imageSize = fileSize/1024;
			System.out.println(imageSize);

			// create a BufferedImage as the result of decoding the supplied InputStream
			//BufferedImage image = ImageIO.read(imageStream);
			
			OutputStream out = new FileOutputStream(output);

			float compressionLevel = 1f;
			
			if (imageSize > 250) {

				if (imageSize >= 2500) {
					compressionLevel = 0.1f;
				} else if (imageSize >= 2300 && imageSize <= 2499) {
					compressionLevel = 0.05f;
				} else if (imageSize >= 2000 && imageSize <= 2299) {
					compressionLevel = 0.1f;
				} else if (imageSize >= 1800 && imageSize <= 1999) {
					compressionLevel = 0.2f;
				} else if (imageSize >= 1500 && imageSize <= 1799) {
					compressionLevel = 0.3f;
				} else if (imageSize >= 1300 && imageSize <= 1499) {
					compressionLevel = 0.4f;
				} else if (imageSize >= 1000 && imageSize <= 1299) {
					compressionLevel = 0.5f;
				} else if (imageSize >= 800 && imageSize <= 999) {
					compressionLevel = 0.6f;
				} else if (imageSize >= 600 && imageSize <= 799) {
					compressionLevel = 0.7f;
				} else if (imageSize >= 400 && imageSize <= 599) {
					compressionLevel = 0.75f;
				} else {
					compressionLevel = 0.8f;
				}
			}

			// get all image writers for JPG format
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();

			// compress to a given quality
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			//param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
			param.setCompressionQuality(compressionLevel);

			// appends a complete image stream containing a single image and
			// associated stream and image metadata and thumbnails to the output
			writer.write(null, new IIOImage(image, null, null), param);

			// close all streams
			out.close();
			ios.close();
			writer.dispose();

		
		}
	}
}
