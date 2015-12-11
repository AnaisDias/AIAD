package Scheduler;

import java.util.ArrayList;

import Constraints.Constraint;
import Utilities.TimePeriod;
import jade.core.AID;

public class MyEvent {
	private String name;
	private long span; //in minutes
	public ArrayList<AID> guests;

	public TimePeriod dateProposal;
	public ArrayList<TimePeriod> possibilites;
	public TimePeriod agreedTimePeriod;
	public ArrayList<Constraint> constraints;
	
	
	public MyEvent(String name, long span, ArrayList<AID> guests, TimePeriod proposal){
		this.setName(name);
		this.setSpan(span);
		this.guests = guests;
		this.dateProposal=proposal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSpan() {
		return span;
	}

	public void setSpan(long span) {
		this.span = span;
	}
	
	public ArrayList<AID> getGuests() {
		return guests;
	}
	
	public void setGuests(ArrayList<AID> guests) {
		this.guests = guests;
	}

	public TimePeriod getDateProposal() {
		return dateProposal;
	}

	public void setDateProposal(TimePeriod dateProposal) {
		this.dateProposal = dateProposal;
	}

	public TimePeriod getAgreedTimePeriod() {
		return agreedTimePeriod;
	}

	public void setAgreedTimePeriod(TimePeriod agreedTimePeriod) {
		this.agreedTimePeriod = agreedTimePeriod;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public void addConstraint(Constraint con){
		constraints.add(con);
	}
	
	public void calculatePossibilites(){
		
	}
	
	public int getSolutionCost(TimePeriod tp){
		int cost = 0;

        for (Constraint con : constraints) {
            if (!con.satisfiedBy(tp)) {
                cost += 1;
            }
        }

        return cost;
	}
	
	
	
	
	
	
	
	
	
}
