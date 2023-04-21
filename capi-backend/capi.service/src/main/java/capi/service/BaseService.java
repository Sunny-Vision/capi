package capi.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Order;

import capi.model.DatatableRequestModel;

public class BaseService {
	
	protected Order getOrder(DatatableRequestModel model, String... columns) {
		List<Map<String,String>> orders = model.getOrder();
		boolean isAsc = true;
		int colnum = 0;
		try{
			colnum = Integer.parseInt(orders.get(0).get("column"));
		}catch(Exception e){
			colnum = 0;
		}
		try{
			isAsc = orders.get(0).get("dir").equals("asc");
		}catch(Exception e){
			isAsc = true;
		}

		Order order = isAsc? Order.asc(columns[colnum]) : Order.desc(columns[colnum]);
		
		return order;
	}
}
