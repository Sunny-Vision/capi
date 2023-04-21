package capi.model.report;

public class QuotationRecordProgress {
	
	public static class QRProgress {
		
		private Integer groupId;
		
		private String groupCode;
		
		private String groupChineseName;
		
		private String groupEnglishName;
		
		private Integer subGroupId;
		
		private String subGroupCode;
		
		private String subGroupChineseName;
		
		private String subGroupEnglishName;
		
		private String cpiBasePeriod;
		
		private Long qrTotal;
		
		private Long unstarted;
		
		private Long routine;
		
		private Long verification;
		
		private Long revisit;

		public Integer getGroupId() {
			return groupId;
		}

		public void setGroupId(Integer groupId) {
			this.groupId = groupId;
		}

		public String getGroupCode() {
			return groupCode;
		}

		public void setGroupCode(String groupCode) {
			this.groupCode = groupCode;
		}

		public String getGroupChineseName() {
			return groupChineseName;
		}

		public void setGroupChineseName(String groupChineseName) {
			this.groupChineseName = groupChineseName;
		}

		public String getGroupEnglishName() {
			return groupEnglishName;
		}

		public void setGroupEnglishName(String groupEnglishName) {
			this.groupEnglishName = groupEnglishName;
		}

		public Integer getSubGroupId() {
			return subGroupId;
		}

		public void setSubGroupId(Integer subGroupId) {
			this.subGroupId = subGroupId;
		}

		public String getSubGroupCode() {
			return subGroupCode;
		}

		public void setSubGroupCode(String subGroupCode) {
			this.subGroupCode = subGroupCode;
		}

		public String getSubGroupChineseName() {
			return subGroupChineseName;
		}

		public void setSubGroupChineseName(String subGroupChineseName) {
			this.subGroupChineseName = subGroupChineseName;
		}

		public String getSubGroupEnglishName() {
			return subGroupEnglishName;
		}

		public void setSubGroupEnglishName(String subGroupEnglishName) {
			this.subGroupEnglishName = subGroupEnglishName;
		}

		public String getCpiBasePeriod() {
			return cpiBasePeriod;
		}

		public void setCpiBasePeriod(String cpiBasePeriod) {
			this.cpiBasePeriod = cpiBasePeriod;
		}

		public Long getUnstarted() {
			return unstarted;
		}

		public void setUnstarted(Long unstarted) {
			this.unstarted = unstarted;
		}

		public Long getRoutine() {
			return routine;
		}

		public void setRoutine(Long routine) {
			this.routine = routine;
		}

		public Long getVerification() {
			return verification;
		}

		public void setVerification(Long verification) {
			this.verification = verification;
		}

		public Long getRevisit() {
			return revisit;
		}

		public void setRevisit(Long revisit) {
			this.revisit = revisit;
		}

		public Long getQrTotal() {
			return qrTotal;
		}

		public void setQrTotal(Long qrTotal) {
			this.qrTotal = qrTotal;
		}
		
	}
	
	public static class IndoorProgress {
		
		private Integer groupId;
		
		private String groupCode;
		
		private String groupChineseName;
		
		private String groupEnglishName;
		
		private Integer subGroupId;
		
		private String subGroupCode;
		
		private String subGroupChineseName;
		
		private String subGroupEnglishName;
		
		private String cpiBasePeriod;
		
		private Long indoorTotal;
		
		private Long allocation;
		
		private Long conversion;
		
		private Long reviewNoField;
		
		private Long reviewField;
		
		private Long noField;
		
		private Long field;

		public Integer getGroupId() {
			return groupId;
		}

		public void setGroupId(Integer groupId) {
			this.groupId = groupId;
		}

		public String getGroupCode() {
			return groupCode;
		}

		public void setGroupCode(String groupCode) {
			this.groupCode = groupCode;
		}

		public String getGroupChineseName() {
			return groupChineseName;
		}

		public void setGroupChineseName(String groupChineseName) {
			this.groupChineseName = groupChineseName;
		}

		public String getGroupEnglishName() {
			return groupEnglishName;
		}

		public void setGroupEnglishName(String groupEnglishName) {
			this.groupEnglishName = groupEnglishName;
		}

		public Integer getSubGroupId() {
			return subGroupId;
		}

		public void setSubGroupId(Integer subGroupId) {
			this.subGroupId = subGroupId;
		}

		public String getSubGroupCode() {
			return subGroupCode;
		}

		public void setSubGroupCode(String subGroupCode) {
			this.subGroupCode = subGroupCode;
		}

		public String getSubGroupChineseName() {
			return subGroupChineseName;
		}

		public void setSubGroupChineseName(String subGroupChineseName) {
			this.subGroupChineseName = subGroupChineseName;
		}

		public String getSubGroupEnglishName() {
			return subGroupEnglishName;
		}

		public void setSubGroupEnglishName(String subGroupEnglishName) {
			this.subGroupEnglishName = subGroupEnglishName;
		}

		public String getCpiBasePeriod() {
			return cpiBasePeriod;
		}

		public void setCpiBasePeriod(String cpiBasePeriod) {
			this.cpiBasePeriod = cpiBasePeriod;
		}

		public Long getAllocation() {
			return allocation;
		}

		public void setAllocation(Long allocation) {
			this.allocation = allocation;
		}

		public Long getConversion() {
			return conversion;
		}

		public void setConversion(Long conversion) {
			this.conversion = conversion;
		}

		public Long getNoField() {
			return noField;
		}

		public void setNoField(Long noField) {
			this.noField = noField;
		}

		public Long getField() {
			return field;
		}

		public void setField(Long field) {
			this.field = field;
		}

		public Long getReviewNoField() {
			return reviewNoField;
		}

		public void setReviewNoField(Long reviewNoField) {
			this.reviewNoField = reviewNoField;
		}

		public Long getReviewField() {
			return reviewField;
		}

		public void setReviewField(Long reviewField) {
			this.reviewField = reviewField;
		}

		public Long getIndoorTotal() {
			return indoorTotal;
		}

		public void setIndoorTotal(Long indoorTotal) {
			this.indoorTotal = indoorTotal;
		}

	}

	private Integer groupId;

	private String groupCode;

	private String groupChineseName;

	private String groupEnglishName;

	private Integer subGroupId;

	private String subGroupCode;

	private String subGroupChineseName;

	private String subGroupEnglishName;

	private Long qrTotal;

	private Long indoorTotal;
	
	private Long unstarted;
	
	private Long routine;
	
	private Long verification;
	
	private Long revisit;

	private Long allocation;

	private Long conversion;

	private Long reviewField;

	private Long reviewNoField;
	
	private Long noField;
	
	private Long field;
	
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupChineseName() {
		return groupChineseName;
	}

	public void setGroupChineseName(String groupChineseName) {
		this.groupChineseName = groupChineseName;
	}

	public String getGroupEnglishName() {
		return groupEnglishName;
	}

	public void setGroupEnglishName(String groupEnglishName) {
		this.groupEnglishName = groupEnglishName;
	}

	public Integer getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(Integer subGroupId) {
		this.subGroupId = subGroupId;
	}

	public String getSubGroupCode() {
		return subGroupCode;
	}

	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}

	public String getSubGroupChineseName() {
		return subGroupChineseName;
	}

	public void setSubGroupChineseName(String subGroupChineseName) {
		this.subGroupChineseName = subGroupChineseName;
	}

	public String getSubGroupEnglishName() {
		return subGroupEnglishName;
	}

	public void setSubGroupEnglishName(String subGroupEnglishName) {
		this.subGroupEnglishName = subGroupEnglishName;
	}

	public Long getIndoorTotal() {
		return indoorTotal;
	}

	public void setIndoorTotal(Long indoorTotal) {
		this.indoorTotal = indoorTotal;
	}

	public Long getUnstarted() {
		return unstarted;
	}

	public void setUnstarted(Long unstarted) {
		this.unstarted = unstarted;
	}

	public Long getAllocation() {
		return allocation;
	}

	public void setAllocation(Long allocation) {
		this.allocation = allocation;
	}

	public Long getConversion() {
		return conversion;
	}

	public void setConversion(Long conversion) {
		this.conversion = conversion;
	}

	public Long getReviewField() {
		return reviewField;
	}

	public void setReviewField(Long reviewField) {
		this.reviewField = reviewField;
	}

	public Long getReviewNoField() {
		return reviewNoField;
	}

	public void setReviewNoField(Long reviewNoField) {
		this.reviewNoField = reviewNoField;
	}

	public Long getQrTotal() {
		return qrTotal;
	}

	public void setQrTotal(Long qrTotal) {
		this.qrTotal = qrTotal;
	}

	public Long getNoField() {
		return noField;
	}

	public void setNoField(Long noField) {
		this.noField = noField;
	}

	public Long getField() {
		return field;
	}

	public void setField(Long field) {
		this.field = field;
	}

	public Long getRoutine() {
		return routine;
	}

	public void setRoutine(Long routine) {
		this.routine = routine;
	}

	public Long getVerification() {
		return verification;
	}

	public void setVerification(Long verification) {
		this.verification = verification;
	}

	public Long getRevisit() {
		return revisit;
	}

	public void setRevisit(Long revisit) {
		this.revisit = revisit;
	}

}
