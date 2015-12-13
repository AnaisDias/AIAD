package Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePeriod {
	private Calendar startTime;
	private Calendar endTime;
	
	public TimePeriod(Calendar start, Calendar end){
		startTime=start;
		endTime=end;
	}
	
	public TimePeriod(String string) {
        String[] parts = string.split(",");
        if (parts.length != 2)
            throw new IllegalArgumentException();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
        	Date st = sdf.parse(parts[0]);
        	System.out.println(st.toString());
			startTime = Calendar.getInstance();
			endTime = Calendar.getInstance();
			startTime.setTime(st);
			endTime.setTime(sdf.parse(parts[1]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
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
	
	@Override
    public String toString() {
        return toString(false);
    }
	
	
	
    public String toString(boolean simple) {
        Calendar sd = Calendar.getInstance();
        Calendar ed = Calendar.getInstance();
        sd.setTimeInMillis(startTime.getTimeInMillis());
        ed.setTimeInMillis(endTime.getTimeInMillis());
        if (simple) {
            return startTime + "," + endTime;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return (dateFormat.format(sd.getTime())) + ", " + (dateFormat.format(ed.getTime()));
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TimePeriod))
            return false;

        if (obj == this)
            return true;

        TimePeriod tp = (TimePeriod) obj;
        return startTime == tp.startTime && endTime == tp.endTime;
    }
}
