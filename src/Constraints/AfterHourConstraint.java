package Constraints;

import java.util.Calendar;

import Utilities.TimePeriod;

public class AfterHourConstraint implements Constraint{

	private int hour;
	private int minutes;
	
	public AfterHourConstraint(int h, int m){
		this.hour=h;
		this.minutes=m;
	}
	
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
		if(hour==tp.getStartTime().get(Calendar.HOUR_OF_DAY)){
			return (minutes<tp.getStartTime().get(Calendar.MINUTE));
		}
		else if(hour < tp.getStartTime().get(Calendar.HOUR_OF_DAY)){
			return true;
		}
		return false;
	}

}
