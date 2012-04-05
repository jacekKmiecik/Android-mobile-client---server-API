package client.android.logic;

public enum OrderStatusEnum{
	NEW("NEW"), 
	IN_PROGRESS("IN_PROGRESS"), 
	COMPLETED("COMPLETED"), 
	REJECTED("REJECTED");
	
    private String value;

    private OrderStatusEnum(String value) {
            this.value = value;
    }
    
    public String getValue(){
    	return value;
    }
    
    public static OrderStatusEnum getEnum(String s){
    	if(s.equalsIgnoreCase("new")) return OrderStatusEnum.NEW;
    	if(s.equalsIgnoreCase("in_progress")) return OrderStatusEnum.IN_PROGRESS;
    	if(s.equalsIgnoreCase("completed")) return OrderStatusEnum.COMPLETED;
    	if(s.equalsIgnoreCase("rejected")) return OrderStatusEnum.REJECTED;
    	
    	return null;
    }
    
    public String getValueForGui(){
    	switch(this){
    	case NEW:
    		return "Unassigned";
    	case IN_PROGRESS:
    		return "In progress";
    	case REJECTED:
    		return "Rejected";
    	case COMPLETED:
    		return "Completed";
		default: 
			return "";
    	}
    }
}
