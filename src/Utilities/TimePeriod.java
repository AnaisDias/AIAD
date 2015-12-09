package Utilities;

import java.util.Date;

public class TimePeriod {
	private Date startTime;
	private Date endTime;
	
	public TimePeriod(Date start, Date end){
		startTime=start;
		endTime=end;
	}
	
	public Boolean isContained(Date date){
		return (startTime.getTime()<= date.getTime() && endTime.getTime()>=date.getTime());
	}
	
	public Boolean isOverlapped(TimePeriod tp){
		return tp.getStartTime().getTime() >= getStartTime().getTime() && tp.getStartTime().getTime() < getEndTime().getTime() ||
                (tp.getEndTime().getTime() > getStartTime().getTime() && tp.getEndTime().getTime() <= getEndTime().getTime());
		
	}
	
	public Date getStartTime(){
		return startTime;
	}
	
	public Date getEndTime(){
		return endTime;
	}
}
