package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="[User]", uniqueConstraints = {@UniqueConstraint(columnNames = "Username")})
public class User  extends capi.entity.EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UserId")
	private Integer userId;
	
	@Column(name="Username")
	private String username;
	
	@Column(name="Password")
	private String password;
	
	/**
	 * 1 - field
	 * 2 - indoor
	 */
	@Column(name="StaffType")
	private Integer staffType;	
	
	@Column(name="StaffCode")
	private String staffCode;
	
	@Column(name="Destination")
	private String destination;
		
	@Column(name="EnglishName")
	private String englishName;
	
	@Column(name="ChineseName")
	private String chineseName;
	
	@Column(name="DeviceKey")
	private String deviceKey;
	
	@Column(name="AccumulatedOT")
	private Double accumulatedOT;
	
	/**
	 * - Active
	 * - Inactive
	 * - Locked
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="AttemptNumber")
	private Integer attemptNumber;
	
	@Column(name="Team")
	private String team;	
	
	@Column(name="DateOfEntry")
	private Date dateOfEntry;
	
	@Column(name="DateOfLeaving")
	private Date dateOfLeaving;
	
	@Column(name="OfficePhoneNo")
	private String officePhoneNo;
	
	@Column(name="Gender")
	private String gender;
	
	@Column(name="GIC")
	private String gic;
	
	@Column(name="HomeArea")
	private String homeArea;
	
	@Column(name="OMP")
	private String omp;
	
	
	@Column(name="IsGHS")
	private boolean isGHS;
	
	
	@Column(name="Office")
	private String office;
	
	@Column(name="Office2")
	private String office2;
	
	
	@Column(name="LastLoginTime")
	private Date lastLoginTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RankId", nullable = true)
	private Rank rank;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisorId", nullable = true)
	private User supervisor;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supervisor")
	private Set<User> subordinates = new HashSet<User>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "UserRole", 
			joinColumns = { @JoinColumn(name = "UserId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "RoleId", nullable = false, updatable = false) })
	private Set<Role> roles = new HashSet<Role>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "UserBatch", 
			joinColumns = { @JoinColumn(name = "UserId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "BatchId", nullable = false, updatable = false) })
	private Set<Batch> batches = new HashSet<Batch>();
		
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<District> districts = new HashSet<District>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<Notification> notifications = new HashSet<Notification>();
	
	// acting records that self acting other
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "replacement")
	private Set<Acting> actings = new HashSet<Acting>();
	
	// the acting records that someone are acting self
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
	private Set<Acting> actedBy = new HashSet<Acting>(); 
		
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private Set<Quotation> ruaQuotations = new HashSet<Quotation>(); 
		
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<StaffReason> reasons = new HashSet<StaffReason>(); 
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "officer")
	private Set<SpotCheckForm> spotCheckForms = new HashSet<SpotCheckForm>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<SupervisoryVisitForm> supervisoryVisitForms = new HashSet<SupervisoryVisitForm>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<IndoorVerificationHistory> indoorVerificationHistories = new HashSet<IndoorVerificationHistory>();
	
	public Integer getId(){
		return this.getUserId();
	}
	
	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Integer getStaffType() {
		return staffType;
	}


	public void setStaffType(Integer staffType) {
		this.staffType = staffType;
	}


	public String getStaffCode() {
		return staffCode;
	}


	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public String getDeviceKey() {
		return deviceKey;
	}


	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}


	public Double getAccumulatedOT() {
		return accumulatedOT;
	}


	public void setAccumulatedOT(Double accumulatedOT) {
		this.accumulatedOT = accumulatedOT;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getAttemptNumber() {
		return attemptNumber;
	}


	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}


	public Date getLastLoginTime() {
		return lastLoginTime;
	}


	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}


	public Rank getRank() {
		return rank;
	}


	public void setRank(Rank rank) {
		this.rank = rank;
	}


	public User getSupervisor() {
		return supervisor;
	}


	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}


	public Set<User> getSubordinates() {
		return subordinates;
	}


	public void setSubordinates(Set<User> subordinates) {
		this.subordinates = subordinates;
	}


	public Set<Role> getRoles() {
		return roles;
	}


	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public String getTeam() {
		return team;
	}


	public void setTeam(String team) {
		this.team = team;
	}


	public String getEnglishName() {
		return englishName;
	}


	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}


	public String getChineseName() {
		return chineseName;
	}


	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}


	public Date getDateOfEntry() {
		return dateOfEntry;
	}


	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}


	public Date getDateOfLeaving() {
		return dateOfLeaving;
	}


	public void setDateOfLeaving(Date dateOfLeaving) {
		this.dateOfLeaving = dateOfLeaving;
	}


	public String getOfficePhoneNo() {
		return officePhoneNo;
	}


	public void setOfficePhoneNo(String officePhoneNo) {
		this.officePhoneNo = officePhoneNo;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}

	public Set<District> getDistricts() {
		return districts;
	}

	public void setDistricts(Set<District> districts) {
		this.districts = districts;
	}

	public String getGic() {
		return gic;
	}

	public void setGic(String gic) {
		this.gic = gic;
	}

	public String getHomeArea() {
		return homeArea;
	}

	public void setHomeArea(String homeArea) {
		this.homeArea = homeArea;
	}

	public String getOmp() {
		return omp;
	}

	public void setOmp(String omp) {
		this.omp = omp;
	}

	public Set<Batch> getBatches() {
		return batches;
	}

	public void setBatches(Set<Batch> batches) {
		this.batches = batches;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public Set<Acting> getActings() {
		return actings;
	}

	public void setActings(Set<Acting> actings) {
		this.actings = actings;
	}

	public Set<Acting> getActedBy() {
		return actedBy;
	}

	public void setActedBy(Set<Acting> actedBy) {
		this.actedBy = actedBy;
	}

	public Set<Quotation> getRuaQuotations() {
		return ruaQuotations;
	}

	public void setRuaQuotations(Set<Quotation> ruaQuotations) {
		this.ruaQuotations = ruaQuotations;
	}

	public Set<StaffReason> getReasons() {
		return reasons;
	}

	public void setReasons(Set<StaffReason> reasons) {
		this.reasons = reasons;
	}

	public boolean isGHS() {
		return isGHS;
	}

	public void setGHS(boolean isGHS) {
		this.isGHS = isGHS;
	}

	public Set<SpotCheckForm> getSpotCheckForms() {
		return spotCheckForms;
	}

	public void setSpotCheckForms(Set<SpotCheckForm> spotCheckForms) {
		this.spotCheckForms = spotCheckForms;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getOffice2() {
		return office2;
	}

	public void setOffice2(String office2) {
		this.office2 = office2;
	}

	public Set<SupervisoryVisitForm> getSupervisoryVisitForms() {
		return supervisoryVisitForms;
	}

	public void setSupervisoryVisitForms(Set<SupervisoryVisitForm> supervisoryVisitForms) {
		this.supervisoryVisitForms = supervisoryVisitForms;
	}

	public Set<IndoorVerificationHistory> getIndoorVerificationHistories() {
		return indoorVerificationHistories;
	}

	public void setIndoorVerificationHistories(
			Set<IndoorVerificationHistory> indoorVerificationHistories) {
		this.indoorVerificationHistories = indoorVerificationHistories;
	}

	
}
