package Constraints;

import Utilities.TimePeriod;

public interface Constraint {
	public boolean satisfiedBy(TimePeriod tp);
}
