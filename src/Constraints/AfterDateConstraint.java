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
		
		tp.getStartTime();
		return (date.get(Calendar.DATE)<tp.getStartTime().get(Calendar.DATE));
	}

}
