package Behaviours;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import Utilities.TimePeriod;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLCodec.CodecException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.StringACLCodec;

public class ABTBehaviour extends SimpleBehaviour{

	private static final long serialVersionUID = 1L;
	private boolean done = false;
	private MyAgent agent;
	private ArrayList<VirtualAgent> virtualAgents = new ArrayList<VirtualAgent>();
	
	public static class VirtualAgent {
		
		private MyAgent agent;
		private MyEvent event;
		private ABT abt = new ABT();
		private boolean agentdone = false;
		private ABTBehaviour abtbeh;
		
		public VirtualAgent(MyAgent agent, MyEvent event, ABTBehaviour abtBeh){
			
			this.agent=agent;
			this.event=event;
			this.abtbeh = abtBeh;
			
			
			abt.inferiorAgents = new TreeSet<AID>();
			
			for(AID a: event.guests){
				if(a.compareTo(agent.getAID())>0) abt.inferiorAgents.add(a);
			}
			
			abt.assigned = new Assignment();
			abt.assigned.sol = null;
			abt.assigned.agent = agent.getAID();
			abt.domain=event.getPossibilities(); 
			abt.agentView=new HashMap<AID, TimePeriod>();
			abt.noGoodStore=new ArrayList<NoGood>();
			abt.cost=0;
			abt.accurate=true;
			abt.register = new TreeSet<AID>();
			
			adjustValue();
		}

		private void adjustValue() {
			TimePeriod value = abt.assigned.sol;
			abt.cost=Integer.MAX_VALUE;
			
			
			for(TimePeriod tp : abt.domain){
				int lowerbound = 0;
				int delta = event.getSolutionCost(tp);
				boolean accurate = true;
				
				TreeSet<AID> register = new TreeSet<AID>();
				register.add(agent.getAID());
				
				for(AID ag : abt.agentView.keySet()){
					
					if(!abt.agentView.get(ag).equals(tp)){
						System.out.println(tp);
						System.out.println(agent.getAID().getName() + " increased cost by 1000" + abt.agentView);
						delta+=1000;
					}
					else System.out.println("they agreeeeeee!!!!!");
				}

				for(NoGood ng : abt.noGoodStore){
					if(ng.tp.equals(tp)){
						register.addAll(ng.register);
						lowerbound += ng.cost;
						accurate = accurate && ng.accurate;
					}
				}
				
				for(VirtualAgent ag : abtbeh.virtualAgents){
					if(event.getName().compareTo(ag.event.getName())>0 
							&& ag.abt.assigned.sol != null
							&& ag.abt.assigned.sol.isOverlapped(tp)){
						System.out.println(agent.getAID().getName() + " increased cost by 1000 because other events");
						delta += 1000;
					}
				}
				
				accurate = accurate && register.containsAll(abt.inferiorAgents);
				if(lowerbound + delta <= abt.cost){
					System.out.println(agent.getAID().getName() + " reassigned cost");
					abt.assigned.sol = tp;
					abt.cost = lowerbound + delta;
					abt.accurate=accurate;
					abt.register=register;
				}
			}
			
			if(abt.cost !=0 || abt.accurate){
				if(agent.getAID().getName().equals(event.guests.first().getName())){
					System.out.println(agent.getAID().getName() + " going to terminate");
					terminate(abt.cost);
					return;
				}
				
				if(!abt.agentView.isEmpty()){
					TreeSet<AID> ags = new TreeSet<AID>(abt.agentView.keySet());
					Assignment assign = new Assignment();
					NoGood ng = new NoGood();
					
					assign.agent=ags.last();
					assign.sol=abt.agentView.get(assign.agent);
					ng.tp = assign.sol;
					ng.register=abt.register;
					ng.accurate=abt.accurate;
					ng.cost=abt.cost;
					ng.context = new HashMap<AID,TimePeriod>(abt.agentView);
					ng.context.remove(assign.agent);
					
					System.out.println(agent.getAID().getName() + " is gonna send nogood");
					sendNoGood(ng, assign.agent);
					
				}
				
			}
			
			if(!abt.assigned.sol.equals(value)){
				System.out.println(agent.getAID().getName()+ "got here!!");
				sendOK(abt.assigned);
			}
			
			System.out.println(agent.getAID().getName()+ " ended adjustvalue");
			
		}

		private void terminate(int cost) {
			event.setAgreedCost(cost);
			event.setAgreedTimePeriod(abt.assigned.sol);
			
			sendTerminate(cost);
			agentdone = true;
			
			abtbeh.done = true;
			for(VirtualAgent ag : abtbeh.virtualAgents){
				abtbeh.done = abtbeh.done && ag.agentdone;
				
			}
			
			if(abtbeh.done) {
				agent.solutionReady();
			}
			
		}

		
		private void sendOK(Assignment asgn) {
			
			JSONObject json = new JSONObject();
			try {
				json.put("proposal", asgn.sol.toString());
				json.put("agent", asgn.agent.getName());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			
			for(AID r : abt.inferiorAgents){
				msg.addReceiver(r);
			}
		
            msg.setContent("OK?-" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            agent.send(msg);
            
            
		}
		
		private void sendOK(Assignment asgn, AID ag) {
			JSONObject json = new JSONObject();
			try {
				json.put("proposal", asgn.sol.toString());
				json.put("agent", asgn.agent.getName());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			
			msg.addReceiver(ag);
		
            msg.setContent("OK?-" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            agent.send(msg);
			
		}
		
		private void sendNoGood(NoGood ng, AID ag) {
			JSONObject json = new JSONObject();
			try {
				json.put("proposal", ng.tp.toString());
				ArrayList<String> agentnames = new ArrayList<String>();
				for(AID a : ng.register){
					agentnames.add(a.getName());
				}
				json.put("register", agentnames);
				json.put("cost", ng.cost);
				json.put("accurate", ng.accurate);
				
				JSONObject context = new JSONObject();
				for(Map.Entry<AID, TimePeriod> con : ng.context.entrySet()){
					context.put(con.getKey().getName(), con.getValue().toString());
				}
				
				json.put("context", context);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();
			
			ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			msg.addReceiver(ag);
            msg.setContent("NOGOOD-" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            
            agent.send(msg);
			
		}
		
		private void sendLink(AID ag, Assignment assigned) {
			
			JSONObject json = new JSONObject();
			try {
				json.put("proposal", assigned.sol.toString());
				json.put("agent", assigned.agent.getName());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(ag);
            msg.setContent("LINK-" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            agent.send(msg);

			
		}
		
		private void sendTerminate(int cost) {
			
			if(abt.inferiorAgents.isEmpty()){
				return;
			}
			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            for (AID a : abt.inferiorAgents) {
                msg.addReceiver(a);
            }
            msg.setContent("TERMINATE-" + event.getName() + "-" + cost);
            msg.setConversationId("schedule-event");
            agent.send(msg);
			
		}

		public void handleOK(ACLMessage msg) {
			String[] sm = msg.getContent().split("-");
			System.out.println("Received OK? from "+msg.getSender().getName());
			
			try {
				Assignment asgn = new Assignment();
				JSONObject json = new JSONObject(sm[2]);
				
				
				AID agn = agent.agentsMap.get(json.getString("agent"));

				asgn.agent=agn;
				
				String proposal = json.getString("proposal");
				TimePeriod prop = new TimePeriod(proposal);
				
				asgn.sol = prop;
				
				
				Iterator<NoGood> i = abt.noGoodStore.iterator();
				while(i.hasNext()){
					NoGood ng = i.next();
					for(Map.Entry<AID, TimePeriod> cont : ng.context.entrySet()){
						if(cont.getKey().getName().equals(asgn.agent.getName()) && !cont.getValue().equals(asgn.sol)){
							i.remove();
							break;
						}
					}
				}
				
				abt.agentView.put(asgn.agent, asgn.sol);
				adjustValue();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}

		public void handleNoGood(ACLMessage msg) {
			String[] sm = msg.getContent().split("-");
			System.out.println("Received NOGOOD from "+msg.getSender().getName());
			
			try {
				NoGood ng = new NoGood();
				JSONObject json = new JSONObject(sm[2]);
				ng.context= new HashMap<AID, TimePeriod>();
				ng.accurate = json.getBoolean("accurate");
				
				HashMap<AID, TimePeriod> con = new HashMap<AID, TimePeriod>();
				JSONObject context = json.getJSONObject("context");
				Iterator<String> itr = context.keys();
				
		        while (itr.hasNext()) {
		        	String key = itr.next();
		        	
		        	String proposal = context.getString(key);
					TimePeriod prop = new TimePeriod(proposal);
					
					AID agn = agent.agentsMap.get(key);

					ng.context.put(agn, prop);
				}
		        
		        ng.cost = json.getInt("cost");
		        ng.register = new TreeSet<AID>();
		        
		        JSONArray reg = json.getJSONArray("register");
		        for(int i=0; i<reg.length(); ++i){
		        	AID agn = agent.agentsMap.get(reg.getString(i));
		        	ng.register.add(agn);
		        }
		        
				String proposal = json.getString("proposal");
				TimePeriod prop = new TimePeriod(proposal);
				
				ng.tp = prop;
				
				
				for (Map.Entry<AID, TimePeriod> cont : ng.context.entrySet()) {
	                TimePeriod propo = abt.agentView.get(cont.getKey());
	                if (propo == null) {
	                    abt.agentView.put(cont.getKey(), cont.getValue());
	                    sendLink(cont.getKey(), abt.assigned);
	                } else {
	                    if (!propo.equals(cont.getValue()))
	                        return;
	                }
	            }
				
				Iterator<NoGood> i = abt.noGoodStore.iterator();
				while(i.hasNext()){
					NoGood oldng = i.next();
					if (oldng.tp.equals(ng.tp) && ng.register.containsAll(oldng.register)) {
	                    i.remove();
	                }
				}

	            abt.noGoodStore.add(ng);
	            adjustValue();
				
			} catch (JSONException  e) {
				e.printStackTrace();
			}
			
		}

		

		public void handleLink(ACLMessage msg) {
			String[] sm = msg.getContent().split("-");
			System.out.println("Received LINK from "+msg.getSender().getName());

			Assignment asgn = new Assignment();
			JSONObject json;
			try {
				json = new JSONObject(sm[2]);

				AID agn = agent.agentsMap.get(json.getString("agent"));

				asgn.agent=agn;

				String proposal = json.getString("proposal");
				TimePeriod prop = new TimePeriod(proposal);

				asgn.sol = prop;
				
				abt.inferiorAgents.add(asgn.agent);
				sendOK(asgn, asgn.agent);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		

		public void handleTerminate(ACLMessage msg) {
			
			String[] sm = msg.getContent().split("-");
			System.out.println("Received TERMINATE from "+msg.getSender().getName());
			
			int cost = Integer.parseInt(sm[2]);
			
			terminate(cost);
			
		}

	
	}
	
	public static class ABT {
		public Assignment assigned;
		public TreeSet<AID> inferiorAgents;
		public ArrayList<TimePeriod> domain;
		public HashMap<AID, TimePeriod> agentView;
		public ArrayList<NoGood> noGoodStore;
		public int cost;
		public boolean accurate;
		public TreeSet<AID> register;
		
	}
	
	public static class Assignment {
		public AID agent;
		public TimePeriod sol;
	}
	
	public static class NoGood {
		public TimePeriod tp;
		public int cost;
		public HashMap<AID, TimePeriod> context;
		public TreeSet<AID> register;
		public boolean accurate;
	}
	
	@Override
    public void onStart() {
		System.out.println("Starting ABT");
        agent = (MyAgent) myAgent;
        if (agent.events.isEmpty()) {
        	System.out.println("ABT: Agent has no events");
            done = true;
            agent.solutionReady();
            return;
        }

        for (MyEvent event : agent.events) {
        	System.out.println("Virtual Agent added: "+ agent.getAID().getName() + " " + event);
            virtualAgents.add(new VirtualAgent(agent, event, this));
            }
    }

	@Override
	public void action() {
		if(done) return;
		ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
		temp.setConversationId("schedule-event");
		MessageTemplate msgtemp = MessageTemplate.MatchConversationId(temp.getConversationId());
		ACLMessage msg = myAgent.receive(msgtemp);

		if (msg != null) {
			String stringmsg = msg.getContent();
			
			String[] sm = stringmsg.split("-");
			System.out.println(stringmsg);
			if (sm.length == 3) {
				ArrayList<VirtualAgent> receivers = new ArrayList<VirtualAgent>();
				for(VirtualAgent va : virtualAgents){
					if(sm[1].equals(va.event.getName())) receivers.add(va);
				}
				switch (sm[0]) {
				case "OK?":
					for(VirtualAgent va : receivers){
						va.handleOK(msg);
					}
					break;
				case "NOGOOD":
					for(VirtualAgent va : receivers){
						va.handleNoGood(msg);
					}
					break;
				case "LINK":
					for(VirtualAgent va : receivers){
						va.handleLink(msg);
					}
					break;
				case "TERMINATE":
					for(VirtualAgent va : receivers){
						va.handleTerminate(msg);
					}
					break;
				default:
					System.out.println("Invalid ABT behaviour message type");
				}
			} else {
				System.out.println("Invalid ABT behaviour message type");
			}

		}
		else{
			//block();
		}
	}


	@Override
	public boolean done() {
		return done;
	}

}
