package capi.service.dataImportExport;

import org.springframework.transaction.annotation.Transactional;

public interface DataExportService {

	public int getTaskNo();
	
	@Transactional
	public void runTask(Integer taskId) throws Exception;
	
}
