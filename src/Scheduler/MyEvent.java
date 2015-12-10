package Scheduler;

import java.util.ArrayList;

import Constraints.Constraint;
import Utilities.TimePeriod;
import jade.core.AID;

public class MyEvent {
	private String name;
	private long span; //in minutes
	public ArrayList<AID> guests;
	public ArrayList<TimePeriod> dateProposals;
	public ArrayList<TimePeriod> possibilites;
	public TimePeriod agreedTimePeriod;
	public ArrayList<Constraint> constraints;
	
	
	public MyEvent(String name, long span, ArrayList<AID> guests, ArrayList<TimePeriod> proposals){
		this.name=name;
		this.span=span;
		this.guests = guests;
		this.dateProposals=proposals;
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
