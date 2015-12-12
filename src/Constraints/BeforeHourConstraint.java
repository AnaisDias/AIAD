package Constraints;

import java.util.Calendar;

import Utilities.TimePeriod;

public class BeforeHourConstraint implements Constraint {

	private int hour;
	private int minutes;
	
	public BeforeHourConstraint(int h, int m){
		hour=h;
		minutes=h;
	}
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
		if(hour==tp.getStartTime().get(Calendar.HOUR)){
			if(hour==tp.getEndTime().get(Calendar.HOUR)){
				return minutes > tp.getEndTime().get(Calendar.MINUTE);
			}
			return minutes > tp.getStartTime().get(Calendar.MINUTE);
		}
		else if(hour>tp.getStartTime().get(Calendar.HOUR) && hour>tp.getEndTime().get(Calendar.HOUR))
			return true;
		return false;
	}

}
