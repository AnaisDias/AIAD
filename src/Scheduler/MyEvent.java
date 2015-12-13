package Scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;

import Constraints.Constraint;
import Utilities.TimePeriod;
import jade.core.AID;

public class MyEvent {
	private String name;
	private long span; //in minutes
	public TreeSet<AID> guests;

	public TimePeriod dateProposal;
	public ArrayList<TimePeriod> possibilities;
	public TimePeriod agreedTimePeriod;
	public int agreedCost;
	public ArrayList<Constraint> constraints;
	
	
	public MyEvent(String name, long span, TreeSet<AID> guests, TimePeriod proposal){
		this.setName(name);
		this.setSpan(span);
		this.guests = guests;
		this.dateProposal=proposal;
		this.constraints = new ArrayList<Constraint>();
		this.possibilities=new ArrayList<TimePeriod>();
		calculatePossibilities();
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
	
	public TreeSet<AID> getGuests() {
		return guests;
	}
	
	public void setGuests(TreeSet<AID> guests) {
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
	public int getAgreedCost() {
		return agreedCost;
	}

	public void setAgreedCost(int agreedCost) {
		this.agreedCost = agreedCost;
	}
	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public void addConstraint(Constraint con){
		constraints.add(con);
		System.out.print(con);
	}
	
	public void calculatePossibilities(){
		possibilities = new ArrayList<TimePeriod>();
		long startTime = dateProposal.getStartTime().getTimeInMillis();
		long currentTime = dateProposal.getStartTime().getTimeInMillis() + (span*60*1000);
		
		
		//cria possibilidades de 30 em 30 minutos dentro das datas permitidas
		while(currentTime<dateProposal.getEndTime().getTimeInMillis()){
			//System.out.println(st.getTime());
			Calendar st = Calendar.getInstance();
			Calendar ct = Calendar.getInstance();
			st.setTime(new Date(startTime));
			ct.setTime(new Date(currentTime));
			TimePeriod newpropo = new TimePeriod(st, ct);
			possibilities.add(new TimePeriod(st, ct));
			startTime += 3600*1000; //supostamente 60 min em millis
			currentTime += 3600*1000;
		}
		
		Collections.reverse(possibilities);
		
	}
	
	public ArrayList<TimePeriod> getPossibilities() {
		return possibilities;
	}

	public void setPossibilities(ArrayList<TimePeriod> possibilities) {
		this.possibilities = possibilities;
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
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	
	
	
	
	
}
