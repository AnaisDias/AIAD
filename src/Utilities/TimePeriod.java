package Utilities;

import java.util.Calendar;
import java.util.Date;

public class TimePeriod {
	private Calendar startTime;
	private Calendar endTime;
	
	public TimePeriod(Calendar start, Calendar end){
		startTime=start;
		endTime=end;
	}
	
	public Boolean isContained(Calendar date){
		return (startTime.getTimeInMillis() <= date.getTimeInMillis() && endTime.getTimeInMillis()>=date.getTimeInMillis());
	}
	
	public Boolean isOverlapped(TimePeriod tp){
		return tp.getStartTime().getTimeInMillis() >= getStartTime().getTimeInMillis() && 
				tp.getStartTime().getTimeInMillis() < getEndTime().getTimeInMillis() ||
                (tp.getEndTime().getTimeInMillis() > getStartTime().getTimeInMillis() && 
                		tp.getEndTime().getTimeInMillis() <= getEndTime().getTimeInMillis());
		
	}
	
	public Calendar getStartTime(){
		return startTime;
	}
	
	public Calendar getEndTime(){
		return endTime;
	}
}
