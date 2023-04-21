package capi.model;

import java.net.InetSocketAddress;
import java.util.Date;

public class AddressPriority {

	private InetSocketAddress address;
	
	private Date failDate;

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public Date getFailDate() {
		return failDate;
	}

	public void setFailDate(Date failDate) {
		this.failDate = failDate;
	}

	
}

