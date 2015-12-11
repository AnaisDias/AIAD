package Constraints;

import java.util.Date;

import Utilities.TimePeriod;

public class AfterDateConstraint implements Constraint {
	
	private Date date;
	
	public AfterDateConstraint(Date date){
		this.date=date;
	}
	@Override
	public boolean satisfiedBy(TimePeriod tp) {
		
		return (date.getTime()<tp.getStartTime().getTime());
	}

}
