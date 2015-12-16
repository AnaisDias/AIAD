package Constraints;

import java.util.Calendar;

import Utilities.TimePeriod;

public class AfterDateConstraint implements Constraint {
	
	private Calendar date;
	
	public AfterDateConstraint(Calendar date){
		this.date=date;
	}
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
		return (date.getTimeInMillis()<tp.getStartTime().getTimeInMillis() || 
				(date.get(Calendar.DAY_OF_MONTH)==tp.getStartTime().get(Calendar.DAY_OF_MONTH)&&
				date.get(Calendar.MONTH) == tp.getStartTime().get(Calendar.MONTH) &&
				date.get(Calendar.YEAR) == tp.getStartTime().get(Calendar.YEAR)));
	}

}
