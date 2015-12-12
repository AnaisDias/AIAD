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
	
		return (date.getTimeInMillis() > tp.getStartTime().getTimeInMillis() && 
				date.getTimeInMillis() > tp.getEndTime().getTimeInMillis());
	}

}
