package Constraints;

import java.util.Calendar;

import Utilities.TimePeriod;

public class BeforeDateConstraint implements Constraint {
	
	private Calendar date;
	
	public BeforeDateConstraint(Calendar date){
		this.date=date;
	}
	
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
	
		return ((date.getTimeInMillis() >= tp.getStartTime().getTimeInMillis() && 
				date.getTimeInMillis() >= tp.getEndTime().getTimeInMillis()) || 
				(date.get(Calendar.DAY_OF_MONTH)==tp.getEndTime().get(Calendar.DAY_OF_MONTH)&&
				date.get(Calendar.MONTH) == tp.getEndTime().get(Calendar.MONTH) &&
				date.get(Calendar.YEAR) == tp.getEndTime().get(Calendar.YEAR)));
	}

}
