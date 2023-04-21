package capi.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Service;

@Service("POIFileService")
public class POIFileService {
	public File makePasswordProtectedTempFile(File file, String password, String fileExtension) throws Exception {
		// copy input file as temp file (copy file content)
		File tempFile = File.createTempFile("tmp", fileExtension);
		FileUtils.copyFile(file, tempFile);
		
		// prepare password encryption of POI file
		POIFSFileSystem fs = new POIFSFileSystem();
		EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
		Encryptor enc = info.getEncryptor();
		enc.confirmPassword(password);
		
		// prepare password-protected POI file output stream 
		OPCPackage opc = OPCPackage.open(tempFile, PackageAccess.READ_WRITE);
		OutputStream os = enc.getDataStream(fs);
		opc.save(os);
		opc.close();
		
		// (over)write temp file as password-protected file
		FileOutputStream fos = new FileOutputStream(tempFile);
		fs.writeFilesystem(fos);
		fos.close();
		fs.close();
		
		return tempFile;
	}
}
