package Behaviours;

import java.util.ArrayList;
import java.util.HashMap;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import Utilities.TimePeriod;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ABTBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 1L;
	private boolean done = false;
	private MyAgent agent;
	private ArrayList<VirtualAgent> virtualAgents = new ArrayList<VirtualAgent>();
	
	public static class VirtualAgent {
		private ABT abt;
		private MyAgent agent;
		private MyEvent event;
		private boolean done;
		
		public VirtualAgent(MyAgent agent, MyEvent event){
			this.agent=agent;
			this.event=event;
			
			for(AID a: event.guests){
				if(a.compareTo(agent.getAID())<0) abt.inferiorAgents.add(a);
			}
			
			abt.assigned = new Assignment();
			abt.assigned.sol = null;
			abt.assigned.agent = agent.getAID();
			abt.domain=event.getPossibilities(); 
			abt.agentView=new HashMap<AID, TimePeriod>();
			abt.noGoodStore=new ArrayList<NoGood>();
			abt.cost=0;
			
			adjustValue();
		}

		private void adjustValue() {
			TimePeriod value = abt.assigned.sol;
			abt.cost=Integer.MAX_VALUE;
			
			
		}
	
	}
	
	public static class ABT {
		public Assignment assigned;
		public ArrayList<AID> inferiorAgents;
		public ArrayList<TimePeriod> domain;
		public HashMap<AID, TimePeriod> agentView;
		public ArrayList<NoGood> noGoodStore;
		public int cost;
		
	}
	
	public static class Assignment {
		public TimePeriod sol;
		public AID agent;
	}
	
	public static class NoGood {
		public TimePeriod tp;
		public int cost;
		public HashMap<AID, TimePeriod> idk;
	}
	
	@Override
    public void onStart() {
        agent = (MyAgent) myAgent;
        if (agent.events.isEmpty()) {
            done = true;
            return;
        }

        for (MyEvent event : agent.events) {
            virtualAgents.add(new VirtualAgent(agent, event));
        }
    }

	@Override
	public void action() {
		ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
		temp.setConversationId("schedule-align");
		MessageTemplate msgtemp = MessageTemplate.MatchConversationId(temp.getConversationId());
		ACLMessage msg = myAgent.receive(msgtemp);

		if (msg != null) {
			String stringmsg = msg.toString();
			String[] sm = stringmsg.split(":");
			if (sm.length == 2) {
				switch (sm[0]) {
				case "OK?":
					handleOk(msg);
					break;
				case "NOGOOD":
					handleNoGood(msg);
					break;
				case "LINK":
					handleLink(msg);
					break;
				case "TERM":
					handleTerm(msg);
					break;
				default:
					System.out.println("Invalid ABT behaviour message type");
				}
			} else {
				System.out.println("Invalid ABT behaviour message type");
			}

		}
		else{
			block();
		}
	}

	private void handleTerm(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	private void handleLink(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	private void handleNoGood(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	private void handleOk(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean done() {
		return done;
	}

}
