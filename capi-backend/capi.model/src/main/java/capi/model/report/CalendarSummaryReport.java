package capi.model.report;

import java.util.Date;
import java.util.List;

public class CalendarSummaryReport {
	
	public static class DateJob {
		private Date date;
		
		private String jobName;
		
		private Boolean isPublicHoliday;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public Boolean getIsPublicHoliday() {
			return isPublicHoliday;
		}

		public void setIsPublicHoliday(Boolean isPublicHoliday) {
			this.isPublicHoliday = isPublicHoliday;
		}
		
	}
	
	private Date date;
	
	private String staffCode;
	
	private String staffName;
	
	private Integer session;
	
	private String jobName;
	
	private String team;
	
	private String rank;
	
	private Boolean isPublicHoliday;
	
	private List<DateJob> dateJobs;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Boolean getIsPublicHoliday() {
		return isPublicHoliday;
	}

	public void setIsPublicHoliday(Boolean isPublicHoliday) {
		this.isPublicHoliday = isPublicHoliday;
	}

	public List<DateJob> getDateJobs() {
		return dateJobs;
	}

	public void setDateJobs(List<DateJob> dateJobs) {
		this.dateJobs = dateJobs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		result = prime * result + ((staffCode == null) ? 0 : staffCode.hashCode());
		result = prime * result + ((staffName == null) ? 0 : staffName.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarSummaryReport other = (CalendarSummaryReport) obj;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		if (staffCode == null) {
			if (other.staffCode != null)
				return false;
		} else if (!staffCode.equals(other.staffCode))
			return false;
		if (staffName == null) {
			if (other.staffName != null)
				return false;
		} else if (!staffName.equals(other.staffName))
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}

}
