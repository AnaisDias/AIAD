package Behaviours;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
			Date startdate = df.parse(start);
			Date enddate = df.parse(end);
			TimePeriod proposal = new TimePeriod(startdate, enddate);
			MyEvent event = new MyEvent(eventname,span,guests,proposal);
			
			((MyAgent) myAgent).invitations.add(event);
			((MyAgent) myAgent).setReady(false);
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void handleAccept(ACLMessage msg) {
		// check if sender is not self (if not, notify agent that he
		// successfully joined event -> later)
		// parse message data
		// add participant either in agent's created events or invitations

	}

	private void handleDecline(ACLMessage msg) {
		// parse message data
		// remove sender from event's participants

	}

	private void handleReady(ACLMessage msg) {
		// add sender to readyagents
		// if all agents are in ready agents -> finished = true

	}

	private void handleHalt(ACLMessage msg) {
		// remove sender from ready agents
		// set done to false

	}

	@Override
	public boolean done() {
		return done;
	}
}
