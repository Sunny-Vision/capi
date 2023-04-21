package capi.service.batchJob;

import org.springframework.transaction.annotation.Transactional;

public interface BatchJobService {

	public String getJobName();
	
	@Transactional
	public void runTask() throws Exception;
	
	@Transactional
	public boolean canRun();
}
