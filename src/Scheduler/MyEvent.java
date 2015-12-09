package Scheduler;

import java.util.ArrayList;

import Utilities.TimePeriod;
import jade.core.AID;

public class MyEvent {
	private String name;
	private long span; //in minutes
	public ArrayList<AID> guests;
	public ArrayList<TimePeriod> dateProposals;
	public ArrayList<TimePeriod> possibilites;
	public TimePeriod agreedTimePeriod;
	public ArrayList<TimePeriod> constraints; //type will be changed to arraylist<constraints>
	
	
	public MyEvent(String name, long span, ArrayList<AID> guests, ArrayList<TimePeriod> proposals){
		this.name=name;
		this.span=span;
		this.guests = guests;
		this.dateProposals=proposals;
	}
	
	
	
	
	
}
