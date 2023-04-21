package capi.service.userAccountManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.PasswordPolicyDao;
import capi.entity.PasswordPolicy;
import capi.model.PasswordPolicyConstant;
import capi.model.userAccountManagement.PasswordPolicyDisplayModel;
import capi.model.userAccountManagement.PasswordPolicySaveModel;

@Service("PasswordPolicyService")
public class PasswordPolicyService {
	
	@Autowired
	private PasswordPolicyDao passwordPolicyDao;

	public PasswordPolicyDisplayModel getParameters(){
		PasswordPolicyDisplayModel displayModel = new PasswordPolicyDisplayModel();
		Iterator<PasswordPolicy> configs = passwordPolicyDao.findAll().iterator();
		while(configs.hasNext()){
			PasswordPolicy config = configs.next();
			
			if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.UNLOCK_DURATION)){
				displayModel.setUnlockDuration(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MAX_ATTEMPT)){
				displayModel.setMaxAttempt(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.RESET_ATTEMPT_DURATION)){
				displayModel.setResetAttemptDuration(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.ENFORCE_PASSWORD_HISTORY)){
				displayModel.setEnforcePasswordHistory(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MAX_AGE)){
				displayModel.setMaxAge(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MIN_AGE)){
				displayModel.setMinAge(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MIN_LENGTH)){
				displayModel.setMinLength(config.getValue());
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.NOTIFICATION_DATE)){
				displayModel.setNotificationDate(config.getValue());
			}

		}
		return displayModel;
	}
	
	public void saveParameters(PasswordPolicySaveModel model) {
		Iterator<PasswordPolicy> configurations = this.passwordPolicyDao.findAll().iterator();
		List<String> updatedConfig = new ArrayList<String>();
		
		
		
		while(configurations.hasNext()){
			PasswordPolicy config = configurations.next();
			if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.UNLOCK_DURATION)){
				
				config.setValue((model.getUnlockDuration())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.UNLOCK_DURATION);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MAX_ATTEMPT)){

				config.setValue((model.getMaxAttempt())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.MAX_ATTEMPT);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.RESET_ATTEMPT_DURATION)){
				
				config.setValue((model.getResetAttemptDuration())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.RESET_ATTEMPT_DURATION);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.ENFORCE_PASSWORD_HISTORY)){

				config.setValue((model.getEnforcePasswordHistory())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.ENFORCE_PASSWORD_HISTORY);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MAX_AGE)){

				config.setValue((model.getMaxAge())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.MAX_AGE);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MIN_AGE)){
				
				config.setValue((model.getMinAge())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.MIN_AGE);
				
			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.MIN_LENGTH)){
				
				config.setValue((model.getMinLength())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.MIN_LENGTH);

			}else if(config.getName().equalsIgnoreCase(PasswordPolicyConstant.NOTIFICATION_DATE)){
				
				config.setValue((model.getNotificationDate())+"");
				this.passwordPolicyDao.save(config);
				updatedConfig.add(PasswordPolicyConstant.NOTIFICATION_DATE);

			}
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.UNLOCK_DURATION)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.UNLOCK_DURATION);
			config.setValue(model.getUnlockDuration()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.MAX_ATTEMPT)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.MAX_ATTEMPT);
			config.setValue(model.getMaxAttempt()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.RESET_ATTEMPT_DURATION)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.RESET_ATTEMPT_DURATION);
			config.setValue(model.getResetAttemptDuration()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.ENFORCE_PASSWORD_HISTORY)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.ENFORCE_PASSWORD_HISTORY);
			config.setValue(model.getEnforcePasswordHistory()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.MAX_AGE)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.MAX_AGE);
			config.setValue(model.getMaxAge()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.MIN_AGE)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.MIN_AGE);
			config.setValue(model.getMinAge()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.MIN_LENGTH)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.MIN_LENGTH);
			config.setValue(model.getMinLength()+"");
			this.passwordPolicyDao.save(config);
		}
		
		if(!updatedConfig.contains(PasswordPolicyConstant.NOTIFICATION_DATE)){
			PasswordPolicy config = new PasswordPolicy();
			config.setName(PasswordPolicyConstant.NOTIFICATION_DATE);
			config.setValue(model.getNotificationDate()+"");
			this.passwordPolicyDao.save(config);
		}
		
		
		passwordPolicyDao.flush();
	}
	
	
	public Integer getLockoutDurationInteger() {
		return  Integer.parseInt(getParameters().getUnlockDuration());
	}
	
	public Integer getMaxAttemptInteger() {
		return Integer.parseInt(getParameters().getMaxAttempt());
	}
	
	public Integer getResetAttemtDurationInteger() {
		return Integer.parseInt(getParameters().getResetAttemptDuration());
	}

	public Integer getMaxAgeInteger() {
		return Integer.parseInt(getParameters().getMaxAge());
	}
	
	public Integer getMinAgeInteger() {
		return Integer.parseInt(getParameters().getMinAge());
	}
	
	public Integer getNotificationDateInteger() {
		return Integer.parseInt(getParameters().getNotificationDate());
	}
	public Integer getEnforcePasswordHistoryInteger() {
		return Integer.parseInt(getParameters().getEnforcePasswordHistory());
	}

}
