package capi.service;

import java.util.Date;
import java.util.List;

import javapns.notification.PushNotificationPayload;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.NotificationDao;
import capi.dal.UserDao;
import capi.entity.Notification;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.NotifcationPreviewModel;
import capi.model.NotificationListPreviewModel;
import capi.model.NotificationTableList;
import capi.model.api.dataSync.NotificationSyncData;

@Service("NotificationService")
public class NotificationService extends BaseService {	

	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AppConfigService appConfig;
	
	@Autowired
	private CommonService commonService;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<NotificationTableList> getNotificationList(DatatableRequestModel model, Integer userId, Boolean isUnReadOnly, Boolean isFlagOnly){
		
		Order order = this.getOrder(model, "notificationId", "subject", "createdDate");
		
		String search = model.getSearch().get("value");		
		
		List<NotificationTableList> result = notificationDao.listNotification(search, model.getStart(), model.getLength(), order, userId, isUnReadOnly, isFlagOnly);

		DatatableResponseModel<NotificationTableList> response = new DatatableResponseModel<NotificationTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = notificationDao.countNotification(userId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = notificationDao.countNotification(search, userId, isUnReadOnly, isFlagOnly);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Get by ID
	 */
	public Notification getNotificationById(int id) {
		return notificationDao.findById(id);
	}
	
	public void updateReadStatus(Notification notification) {
		notification.setRead(true);
		notificationDao.save(notification);
		notificationDao.flush();
	}
	
	public Long countUnreadNotification(Integer userId){
		return notificationDao.countUnreadNotification(userId);
	}

	public NotificationListPreviewModel getNotificationPreviewList(Integer userId){
		long cnt = notificationDao.countUnreadNotification(userId);
		List<NotifcationPreviewModel> viewModels = notificationDao.getLatestUnReadMessagePreview(userId);
		NotificationListPreviewModel model = new NotificationListPreviewModel();
		model.setNotification(viewModels);
		model.setUnReadCount(cnt);
		return model;		
	}
	
	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteActivityCodes(List<Integer> id) {
		List<Notification> notifications = notificationDao.getNotificationsByIds(id);
		if (id.size() != notifications.size()){
			return false;
		}
		
		for (Notification notification : notifications){
			notificationDao.delete(notification);
		}

		notificationDao.flush();

		return true;
	}
	
	@Transactional
	public boolean markAsRead(List<Integer> id){
		List<Notification> notifications = notificationDao.getNotificationsByIds(id);
		if (id.size() != notifications.size()){
			return false;
		}
		
		for (Notification notification : notifications){
			notification.setRead(true);
		}

		notificationDao.flush();

		return true;
	}
	
	@Transactional
	public boolean toggleFlag(List<Integer> id, Boolean flag){
		List<Notification> notifications = notificationDao.getNotificationsByIds(id);
		if (id.size() != notifications.size()){
			return false;
		}
		
		for (Notification notification : notifications){
			notification.setFlag(flag!=null&&flag);
		}

		notificationDao.flush();

		return true;
	}
	
	public boolean sendNotification(User user, String subject, String content, boolean withPush) {
		return this.sendNotification(user, subject, content, null ,withPush);
	}
	
	
	public boolean sendNotification(User user, String subject, String content, String rejectedQuotationIds, boolean withPush) {
		Notification notify = new Notification();
		notify.setUser(user);
		notify.setContent(content);
		notify.setSubject(subject);
		notify.setRead(false);
		if (!StringUtils.isEmpty(rejectedQuotationIds)){
			notify.setRejectedQuotationIds(rejectedQuotationIds);
		}
		
		notificationDao.save(notify);
		if (withPush && !StringUtils.isEmpty(user.getDeviceKey())){
			boolean useSandBox = appConfig.isUseSandbox();
			try{
				PushNotificationPayload payload = PushNotificationPayload.complex();
		    	payload.addAlert(subject);
		    	payload.addCustomDictionary("id", notify.getId());
		        payload.addCustomDictionary("Subject", subject);
		        payload.addCustomDictionary("Content", content);
	
		        Resource resource = new ClassPathResource(appConfig.getPushCertificateName());
				//InputStream in = new FileInputStream(resource.getFile().getPath());
		       // Push.payload(payload, in, appConfig.getPushPassword(), !useSandBox, new String[]{user.getDeviceKey()});
				commonService.sendAPNsPushMessage(payload, resource.getFile().getPath(), appConfig.getPushPassword(), !useSandBox, new String[]{user.getDeviceKey()});
			}
			catch(Exception ex){
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 *  test
	 */
	public List<NotificationSyncData> getUpdateNotification(Date lastSyncTime){
		return notificationDao.getUpdatedNotification(lastSyncTime);
	}
}
