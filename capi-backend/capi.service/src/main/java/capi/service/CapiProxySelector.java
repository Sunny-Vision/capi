package capi.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import capi.model.AddressPriority;
import edu.emory.mathcs.backport.java.util.Collections;


public class CapiProxySelector extends ProxySelector{
	
	private Map<SocketAddress, AddressPriority> addresses;
	
	private boolean useProxy;
	
	public static void init(AppConfigService service){
		if (!(ProxySelector.getDefault() instanceof CapiProxySelector)){
			CapiProxySelector selector = new CapiProxySelector();
			selector.useProxy = service.isUseProxy();
			//System.out.println("init Use Proxy : " + service.isUseProxy());
			if (selector.useProxy){
				if (!StringUtils.isEmpty(service.getProxyHost())){
					AddressPriority addr = new AddressPriority();
					InetSocketAddress inAddr = new InetSocketAddress(service.getProxyHost(), service.getProxyPort());
					addr.setAddress(inAddr);
					selector.addresses.put(inAddr, addr);
					//System.out.println("init host1: " + inAddr.getHostString());
				}
				if (!StringUtils.isEmpty(service.getProxyHost2())){
					AddressPriority addr = new AddressPriority();
					InetSocketAddress inAddr = new InetSocketAddress(service.getProxyHost2(), service.getProxyPort2());
					addr.setAddress(inAddr);
					selector.addresses.put(inAddr, addr);
					//System.out.println("init host2: " + inAddr.getHostString());
				}
			}			
			ProxySelector.setDefault(selector);
		}
	}
	
	public CapiProxySelector(){
		addresses = new HashMap<SocketAddress, AddressPriority>();
	}

	@Override
	public List<Proxy> select(URI uri) {
		// TODO Auto-generated method stub
		List<Proxy> proxies = new ArrayList<Proxy>();
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		//System.out.println("Use Proxy : " + this.useProxy);
		if (this.useProxy){
			String protocol = uri.getScheme();			
	        if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {
	        	List<Proxy> normalProxy = new ArrayList<Proxy>();
				List<AddressPriority> failed = new ArrayList<AddressPriority>(); 
	        	for (AddressPriority proxy : addresses.values()){
	        		if (proxy.getFailDate() != null && today.before(DateUtils.addDays(proxy.getFailDate(), 3))){
	        			failed.add(proxy);
	        		}
	        		else{
	        			proxy.setFailDate(null);
	        			normalProxy.add(new Proxy(Proxy.Type.HTTP, proxy.getAddress()));
	        		}	        		
	        	}	 
	        	
	        	if (normalProxy.size() > 0){
	        		Collections.shuffle(normalProxy);	        		
	        	}
	        	proxies.addAll(normalProxy);
	        	if (failed.size() > 0){
	        		Collections.sort(failed, new Comparator<AddressPriority>(){
						@Override
						public int compare(AddressPriority o1, AddressPriority o2) {
							// TODO Auto-generated method stub
							return o1.getFailDate().compareTo(o2.getFailDate());
						}	        			
	        		});
		        	for (AddressPriority proxy : failed){
		        		proxies.add(new Proxy(Proxy.Type.HTTP, proxy.getAddress()));       		
		        	}	        		
	        	}
	        }
		}
				
		proxies.add(Proxy.NO_PROXY);
		
		return proxies;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		// TODO Auto-generated method stub
		System.out.println("proxy connectFailed: "+sa.toString());
		if (addresses.containsKey(sa)){
			AddressPriority p = addresses.get(sa);
			p.setFailDate(new Date());			
		}
	}

	public Map<SocketAddress, AddressPriority> getAddresses() {
		return addresses;
	}

	public void setAddresses(Map<SocketAddress, AddressPriority> addresses) {
		this.addresses = addresses;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	
	
}
