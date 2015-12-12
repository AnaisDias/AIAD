package Constraints;

import java.util.Calendar;

import Utilities.TimePeriod;

public class SpecificDateConstraint implements Constraint {

	private Calendar date;
	
	public SpecificDateConstraint(Calendar date){
		this.date=date;
	}
	@Override //maybe add duration and check if endtime is correct as well
	public boolean satisfiedBy(TimePeriod tp) {
		
		return date.get(Calendar.DAY_OF_MONTH) == tp.getStartTime().get(Calendar.DAY_OF_MONTH)&&
				date.get(Calendar.MONTH)==tp.getStartTime().get(Calendar.MONTH) &&
				date.get(Calendar.YEAR)==tp.getStartTime().get(Calendar.YEAR) &&
				date.get(Calendar.HOUR_OF_DAY) == tp.getStartTime().get(Calendar.HOUR_OF_DAY) &&
				date.get(Calendar.MINUTE) == tp.getStartTime().get(Calendar.MINUTE);
	}

}
