package Behaviours;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import Utilities.TimePeriod;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CreateEventBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = 1L;
	private boolean done = false;

	@Override
	public void action() {
		ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
		temp.setConversationId("event-creation");
		MessageTemplate msgtemp = MessageTemplate.MatchConversationId(temp.getConversationId());
		ACLMessage msg = myAgent.receive(msgtemp);

		if (msg != null) {
			String stringmsg = msg.toString();
			String[] sm = stringmsg.split(":");
			if (sm.length == 2) {
				switch (sm[0]) {
				case "INVITE":
					handleInvite(msg);
					break;
				case "ACCEPT":
					handleAccept(msg);
					break;
				case "DECLINE":
					handleDecline(msg);
					break;
				case "READY":
					handleReady(msg);
					break;
				case "HALT":
					handleHalt(msg);
					break;
				default:
					System.out.println("Invalid create event message type");
				}
			} else {
				System.out.println("Invalid create event message type");
			}

		}

	}

	private void handleInvite(ACLMessage msg) {
		String stringmsg = msg.toString();
		String[] sm = stringmsg.split(":");
		try {
			JSONObject json = new JSONObject(sm[1]);
			String eventname = json.get("name").toString();
			String stspan = json.getString("span").toString();
			long span = Long.parseLong(stspan,10);
			JSONArray eventguests = json.getJSONArray("guests");
			ArrayList<AID> guests = new ArrayList<AID>();
			for(int i=0; i<eventguests.length();i++){
				guests.add(new AID(eventguests.getString(i),true));
			}
			String start = json.getString("proposalStartTime");
			String end = json.getString("proposalEndTime");
			DateFormat df = new SimpleDateFormat();
			Calendar startdate = Calendar.getInstance();
			startdate.setTime(df.parse(start));
			Calendar enddate = Calendar.getInstance();
			enddate.setTime(df.parse(end));
			TimePeriod proposal = new TimePeriod(startdate, enddate);
			MyEvent event = new MyEvent(eventname,span,guests,proposal);
			
			((MyAgent) myAgent).invitations.add(event);
			((MyAgent) myAgent).setReady(false);
			System.out.println("Agent invited to event " + eventname);
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void handleAccept(ACLMessage msg) {
		boolean added = false;
		if(!msg.getSender().equals(((MyAgent)myAgent).getAID())){
			String stmsg = msg.getContent();
			String[] sm = stmsg.split(":");
			for(MyEvent ev: ((MyAgent)myAgent).events){
				if(ev.getName().equals(sm[1])){
					ev.guests.add(msg.getSender());
					System.out.println(msg.getSender().getName() + " accepted event " + sm[1]);
					added=true;
				}
			}
			if(!added){
				for(MyEvent ev: ((MyAgent)myAgent).invitations){
					if(ev.getName().equals(sm[1])){
						ev.guests.add(msg.getSender());
						System.out.println(msg.getSender().getName() + " accepted event " + sm[1]);
						added=true;
					}
				}
			}
			
		}
		else {
			String stmsg = msg.getContent();
			String[] sm = stmsg.split(":");
			System.out.println("Agent successfully joined event " + sm[1]);
		}

	}

	private void handleDecline(ACLMessage msg) {
		boolean removed = false;
		String stmsg = msg.getContent();
		String[] sm = stmsg.split(":");
		for(MyEvent ev: ((MyAgent)myAgent).events){
			if(ev.getName().equals(sm[1])){
				ev.guests.remove(msg.getSender());
				System.out.println(msg.getSender().getName() + " declined event " + sm[1]);
				removed=true;
			}
		}
		if(!removed){
			for(MyEvent ev: ((MyAgent)myAgent).invitations){
				if(ev.getName().equals(sm[1])){
					ev.guests.remove(msg.getSender());
					System.out.println(msg.getSender().getName() + " declined event " + sm[1]);
					removed=true;
				}
			}
		}

	}

	private void handleReady(ACLMessage msg) {
		((MyAgent)myAgent).readyAgents.add(msg.getSender());
		if(((MyAgent)myAgent).readyAgents.containsAll(((MyAgent)myAgent).allAgents)){
			done=true;
		}
		else{
			done=false;
		}

	}

	private void handleHalt(ACLMessage msg) {
		((MyAgent)myAgent).readyAgents.remove(msg.getSender());
		done=false;

	}

	@Override
	public boolean done() {
		return done;
	}
}