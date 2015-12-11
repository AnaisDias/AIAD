package Constraints;

import java.util.Date;

import Utilities.TimePeriod;

public class AfterHourConstraint implements Constraint{

	public Date date;
	
	public AfterHourConstraint(Date date){
		this.date=date;
	}
	
	@SuppressWarnings("deprecation")//try to replace by non deprecated functions
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
		if(date.getHours()==tp.getStartTime().getHours()){
			return (date.getMinutes()<tp.getStartTime().getMinutes());
		}
		else if(date.getHours() < tp.getStartTime().getHours()){
			return true;
		}
		return false;
	}

}
