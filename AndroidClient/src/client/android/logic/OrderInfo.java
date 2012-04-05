package client.android.logic;

public class OrderInfo {
	public static int id_vehicle;
	public static int id_order;
	private String address_start;
	private String address_end;
	private OrderStatusEnum order_status;
	
	//empty class constructor
	public OrderInfo(){
		
	}
	
	//class constructor initializing fields describing an order:
	public OrderInfo(int id_order,
				 int id_vehicle,
				 String address_start,
				 String address_end,
				 String status){
		this.id_order = id_order;
		OrderInfo.id_vehicle = id_vehicle;
		this.address_start = address_start;
		this.address_end = address_end;
		this.order_status = OrderStatusEnum.getEnum(status);
	}

	public String getAddress_start() {
		return address_start;
	}

	public void setAddress_start(String address_start) {
		this.address_start = address_start;
	}

	public String getAddress_end() {
		return address_end;
	}

	public void setAddress_end(String address_end) {
		this.address_end = address_end;
	}

	public OrderStatusEnum getOrder_status() {
		return order_status;
	}

	public void setOrder_status(OrderStatusEnum order_status) {
		this.order_status = order_status;
	}
	
	
}
