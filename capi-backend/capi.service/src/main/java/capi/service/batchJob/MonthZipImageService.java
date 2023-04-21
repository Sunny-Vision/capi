package capi.service.batchJob;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.BufferedInputStream;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import capi.dal.CalendarEventDao;
import capi.dal.OutletDao;
import capi.dal.ProductDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UserDao;
import capi.entity.Outlet;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.ProductZipImageSyncData;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.NotificationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author stanley_tsang remind user to create survey if it reach the three
 *         working day before the end date of latest survey month
 *
 *         Reminder for defining Survey Month No Survey Month is defined after
 *         the end date of last survey month
 */
@Service("MonthZipImageService")
public class MonthZipImageService implements BatchJobService {

	private static final Logger logger = LoggerFactory.getLogger(MonthZipImageService.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private OutletDao outletDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private AppConfigService configService;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "MonthZipImageService";
	}

	@Override
	public void runTask() throws Exception {
		String end = null;
		String start = null;

		int countMonthTime = 0;
		int countfile = 1;

		LocalDate todaydate = LocalDate.now();
		//if (startJob || todaydate.equals(todaydate.withDayOfMonth(1))) {
		if (todaydate.equals(todaydate.withDayOfMonth(1))) {
			// if ("X".equals("X")) {

			logger.info("MonthZipImagServicee - Start");
			List<String> outletMinDateTime = outletDao.getOutletMinDateTime();

			String miniDateTimeString = "";

			for (String aString : outletMinDateTime) {
				miniDateTimeString = aString;
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date miniDateTime = formatter.parse(miniDateTimeString + " 00:00:00");

			Date currentDate = miniDateTime;

			SimpleDateFormat dft1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(new Date());
			calendar1.set(Calendar.DAY_OF_MONTH, calendar1.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDateOfPreviousMonth = formatter.parse(dft1.format(calendar1.getTime()) + " 23:59:59");

			String fileBaseLoc = configService.getFileBaseLoc();
			
			// Remove Pre-Zip File
			File forPreZipImageFile = new File(fileBaseLoc + "/download/");
			if (forPreZipImageFile.exists()) {
				FileUtils.deleteDirectory(forPreZipImageFile);
			}

			while (currentDate.compareTo(endDateOfPreviousMonth) < 0) {

				logger.info("Zip Count :: " + countMonthTime);
				System.out.println("Zip Count :: " + countMonthTime);
				countMonthTime++;
				if (countMonthTime > 72) {
					return;
				}

				SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				end = dft.format(calendar.getTime()) + " 23:59:59";

				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
				start = dft.format(calendar.getTime()) + " 00:00:00";

				calendar.add(Calendar.MONTH, 1);
				String nextMonthString = dft.format(calendar.getTime()) + " 23:59:59";
				currentDate = formatter.parse(nextMonthString);

				//String fileBaseLoc = configService.getFileBaseLoc();
				String zipPath = fileBaseLoc + "/zip/";
				String downloadPath = fileBaseLoc + "/download/";
				// createFolder(zipPath);
				createFolder(downloadPath);

				Boolean isFirst = false;

				// Remove Pre-Zip File
				File preZipFile = new File(downloadPath + "data.zip");
				if (preZipFile.exists()) {
					FileUtils.forceDelete(preZipFile);
					// isFirst = false;
				}

				// Outlet
				List<String> outletList = outletDao.getOutletImageMonthly(end, start, isFirst);

				if (outletList.size() > 0) {
					for (String one : outletList) {
						createFolder(zipPath + "houseKeep/outletImage/");
						String imagePath = one;
						if (one.isEmpty() || one.equals("")) {
							continue;
						}
						String ext = getFileExtension(fileBaseLoc + imagePath);
						if (ext.equals("") || ext == null) {
							continue;
						}
						String source = fileBaseLoc + imagePath;
						String[] spiltPath = imagePath.split("/");
						String dest = zipPath + "houseKeep/outletImage/" + spiltPath[spiltPath.length - 1] + ext;
						copyFileUsingChannel(source, dest);
					}
				}

				// OutletAttachment
				List<String> outletAttachmentDatasList = outletDao.getOutletAttachmentImageMonthly(end, start, isFirst);
				if (outletAttachmentDatasList.size() > 0) {
					for (String one : outletAttachmentDatasList) {
						createFolder(zipPath + "houseKeep/outletAttachment/");
						if (one.isEmpty() || one.equals("")) {
							continue;
						}
						String imagePath = one;
						String ext = getFileExtension(fileBaseLoc + imagePath);
						if (ext.equals("") || ext == null) {
							continue;
						}
						String source = fileBaseLoc + imagePath;
						String[] spiltPath = imagePath.split("/");
						String dest = zipPath + "houseKeep/outletAttachment/" + spiltPath[spiltPath.length - 1] + ext;
						copyFileUsingChannel(source, dest);

					}
				}

				List<String> productList = productDao.getProductImageMonthly(end, start, isFirst);

				if (productList.size() > 0) {
					for (String one : productList) {
						createFolder(zipPath + "perm/productImage/");
						if (one.isEmpty() || one.equals("")) {
							continue;
						}
						String imagePath = one;
						String ext = getFileExtension(fileBaseLoc + imagePath);
						if (ext.equals("") || ext == null) {
							continue;
						}
						String source = fileBaseLoc + imagePath;
						String[] spiltPath = imagePath.split("/");
						String dest = zipPath + "perm/productImage/" + spiltPath[spiltPath.length - 1] + ext;
						copyFileUsingChannel(source, dest);
		
					}
				}

				// Zip File
				File forZipProductImageFile = new File(zipPath);
				if (forZipProductImageFile.exists()) {
					zipFiles(zipPath, downloadPath + "data" + countfile + ".zip");
					FileUtils.deleteDirectory(forZipProductImageFile);
					countfile++;

				}

				logger.info("MonthZipImagServicee - End");
				/*
				 * // Remove Folder - Outlet File forZipOutletImageFile = new File(zipPath +
				 * "houseKeep/outletImage/"); if (forZipOutletImageFile.exists()) {
				 * FileUtils.deleteDirectory(forZipOutletImageFile); } // Remove Folder - Outlet
				 * Attachment File forZipOutletAttachmentImageFile = new File(zipPath +
				 * "houseKeep/outletAttachment/"); if (forZipOutletAttachmentImageFile.exists())
				 * { FileUtils.deleteDirectory(forZipOutletAttachmentImageFile); }
				 * 
				 * // Remove Folder - Product File forZipProductImageFile = new File(zipPath +
				 * "perm/productImage/"); if (forZipProductImageFile.exists()) {
				 * FileUtils.deleteDirectory(forZipProductImageFile); }
				 */
			}

		}

	}

	private void copyFileUsingChannel(String souPath, String destPath) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;

		File source = new File(souPath);
		File dest = new File(destPath);

		if (!dest.exists() && source.exists()) {
			try {
				sourceChannel = new FileInputStream(source).getChannel();
				destChannel = new FileOutputStream(dest).getChannel();
				destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("copyFileUsingChannel", e);
				throw e;
			} finally {
				sourceChannel.close();
				destChannel.close();
			}
		}
	}

	public String getFileExtension(String path) {
		String type = "";
		File input = new File(path);
		if (input.exists()) {
			try {
				InputStream is = new BufferedInputStream(new FileInputStream(input));
				type = mapMimeType(URLConnection.guessContentTypeFromStream(is));
				if (type == null) {
					return ".jpg";
				}
			} catch (Exception ex) {
				logger.error("getFileExtension", ex);
			}
		} else {
			return "";
		}

		/*
		 * // create object of Path Path fullPath = Paths.get(path + type);
		 * 
		 * // call getFileName() and get FileName path object String fileName =
		 * fullPath.getFileName().toString();
		 */
		return type;
	}

	public void createFolder(String pathString) {
		File dir = new File(pathString);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Path path = FileSystems.getDefault().getPath(dir.getAbsolutePath());

		// return path;
	}

	public String mapMimeType(String mimeType) {
		String ext = ".jpg";

		/*
		 * if (mimeType == null) { return ".jpg"; }
		 * 
		 * if (mimeType.equals("image/bmp")) { ext = ".bmp"; } else if
		 * (mimeType.equals("image/cis-cod")) { ext = ".cod"; } else if
		 * (mimeType.equals("image/gif")) { ext = ".gif"; } else if
		 * (mimeType.equals("image/ief")) { ext = ".ief"; } else if
		 * (mimeType.equals("image/jpeg")) { ext = ".jpg"; } else if
		 * (mimeType.equals("image/pipeg")) { ext = ".jfif"; } else if
		 * (mimeType.equals("image/svg+xml")) { ext = "svg"; } else if
		 * (mimeType.equals("image/tiff")) { ext = ".tiff"; } else if
		 * (mimeType.equals("image/x-cmu-raster")) { ext = ".ras"; } else if
		 * (mimeType.equals("image/x-cmx")) { ext = ".cmx"; } else if
		 * (mimeType.equals("image/x-icon")) { ext = ".ico"; } else if
		 * (mimeType.equals("image/x-portable-anymap")) { ext = ".pnm"; } else if
		 * (mimeType.equals("image/x-portable-bitmap")) { ext = ".pbm"; } else if
		 * (mimeType.equals("image/x-portable-graymap")) { ext = ".pgm"; } else if
		 * (mimeType.equals("image/x-portable-pixmap")) { ext = "ppm"; } else if
		 * (mimeType.equals("image/x-rgb")) { ext = ".rgb"; } else if
		 * (mimeType.equals("image/x-xbitmap")) { ext = ".xbm"; } else if
		 * (mimeType.equals("image/x-xpixmap")) { ext = ".xpm"; } else if
		 * (mimeType.equals("image/x-xwindowdump")) { ext = ".xwd"; } else if
		 * (mimeType.equals("image/png")) { ext = ".png"; } else { ext = ".jpg"; }
		 */
		return ext;
	}

	public void zipFiles(String source, String destit) {
		File file = new File(source);
		ZipOutputStream zipOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(destit);
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			if (file.isDirectory()) {
				directory(zipOutputStream, file, "");
			} else {
				zipFile(zipOutputStream, file, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				zipOutputStream.close();
				fileOutputStream.close();
			} catch (IOException e) {
				logger.error("zipFiles", e);
			}
		}

	}

	private void zipFile(ZipOutputStream zipOutputStream, File file, String parentFileName) {
		FileInputStream in = null;
		try {
			ZipEntry zipEntry;
			if (file.getName().equals(parentFileName)) {
				zipEntry = new ZipEntry(file.getName());
			} else {
				String path = parentFileName.replace(file.getName(), "");
				zipEntry = new ZipEntry(path + file.getName());
			}
			zipOutputStream.putNextEntry(zipEntry);
			in = new FileInputStream(file);
			int len;
			byte[] buf = new byte[8 * 1024];
			while ((len = in.read(buf)) != -1) {
				zipOutputStream.write(buf, 0, len);
			}
			zipOutputStream.closeEntry();
		} catch (FileNotFoundException e) {
			logger.error("zipFile", e);
		} catch (Exception e) {
			logger.error("zipFile", e);

		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("zipFile", e);
			}
		}
	}

	private void directory(ZipOutputStream zipOutputStream, File file, String parentFileName) {
		File[] files = file.listFiles();
		String parentFileNameTemp = null;
		for (File fileTemp : files) {
			parentFileNameTemp = (parentFileName == null || parentFileName == "") ? fileTemp.getName()
					: parentFileName + "\\" + fileTemp.getName();
			if (fileTemp.isDirectory()) {
				directory(zipOutputStream, fileTemp, parentFileNameTemp);
			} else {
				zipFile(zipOutputStream, fileTemp, parentFileNameTemp);
			}
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		LocalDate todaydate = LocalDate.now();

		if (todaydate.equals(todaydate.withDayOfMonth(1))) {
			return true;
		}

		return false;

		// return true;

	}
}
