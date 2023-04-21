package capi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service("ZipService")
public class ZipService {
	public void unZip(InputStream zipStream, String outputFolder) throws Exception{

		byte[] buffer = new byte[1024];

		// create output directory is not exists
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}

		// get the zip file content
		ZipInputStream zis = new ZipInputStream(zipStream);
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {

			String fileName = ze.getName();
			File newFile = new File(outputFolder + File.separator + fileName);

			System.out.println("file unzip : " + newFile.getAbsoluteFile());
			
			if (ze.isDirectory()){
				newFile.mkdirs();
				ze = zis.getNextEntry();
				continue;
			}
			else{
				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();
			}
							
			FileOutputStream fos = new FileOutputStream(newFile);

			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();

	}
	
	public void zipFiles(File[] files, final OutputStream outputStream) throws IOException{
		 try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
			 for (File file: files){
				 final ZipEntry zipEntry = new ZipEntry(file.getName());
	             zipOutputStream.putNextEntry(zipEntry);
	             try (FileInputStream inputStream = new FileInputStream(file)) {
	                 IOUtils.copy(inputStream, zipOutputStream);
	             }
	             zipOutputStream.closeEntry();
			 }
		 }
	}
	
	public void zipFiles(InputStream[] streams, String[] filenames, final OutputStream outputStream) throws IOException{
		 try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
			 for (int i = 0; i < filenames.length; i++){
				 final ZipEntry zipEntry = new ZipEntry(filenames[i]);
				 zipOutputStream.putNextEntry(zipEntry);
				 IOUtils.copy(streams[i], zipOutputStream);
	             zipOutputStream.closeEntry();
			 }
		 }
	}
	
	public void zipFolder(final File folder, final File zipFile) throws IOException {
        zipFolder(folder, new FileOutputStream(zipFile));
    }

    public void zipFolder(final File folder, final OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            processFolder(folder, zipOutputStream, folder.getPath().length() + 1);
        }
    }

    private void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength)
            throws IOException {
        for (final File file : folder.listFiles()) {
            if (file.isFile()) {
                final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    IOUtils.copy(inputStream, zipOutputStream);
                }
                zipOutputStream.closeEntry();
            } else if (file.isDirectory()) {
                processFolder(file, zipOutputStream, prefixLength);
            }
        }
    }

}
