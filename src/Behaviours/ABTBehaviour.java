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
		private ABT abt = new ABT();
		private MyAgent agent;
		private MyEvent event;
		private boolean done;
		private ABTBehaviour abtbeh;
		
		public VirtualAgent(MyAgent agent, MyEvent event, ABTBehaviour abtBeh){
			
			this.agent=agent;
			this.event=event;
			this.abtbeh = abtBeh;
			
			abt.inferiorAgents = new ArrayList<AID>();
			
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
			abt.accurate=true;
			abt.register = new ArrayList<AID>();
			
			adjustValue();
		}

		private void adjustValue() {
			TimePeriod value = abt.assigned.sol;
			abt.cost=Integer.MAX_VALUE;
			
			
			for(TimePeriod tp : abt.domain){
				int lowerbound = 0;
				int delta = event.getSolutionCost(tp);
				boolean accurate = true;
				
				ArrayList<AID> register = new ArrayList<AID>();
				register.add(agent.getAID());
				
				for(AID ag : abt.agentView.keySet()){
					if(abt.agentView.get(ag).equals(tp)){
						delta+=1000;
					}
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
						delta += 1000;
					}
				}
				
				accurate = accurate && register.containsAll(abt.inferiorAgents);
				
				if(lowerbound + delta <= abt.cost){
					abt.assigned.sol = tp;
					abt.cost = lowerbound + delta;
					abt.accurate=accurate;
					abt.register=register;
				}
			}
			
			if(abt.cost !=0 || abt.accurate){
				if(agent.getAID().equals(event.guests.get(0))){
					terminate(abt.cost);
					return;
				}
				
				if(!abt.agentView.isEmpty()){
					ArrayList<AID> ags = new ArrayList<AID>(abt.agentView.keySet());
					Assignment assign = new Assignment();
					NoGood ng = new NoGood();
					
					assign.agent=ags.get(ags.size()-1);
					assign.sol=abt.agentView.get(assign.agent);
					ng.tp = assign.sol;
					ng.register=abt.register;
					ng.accurate=abt.accurate;
					ng.cost=abt.cost;
					ng.context = abt.agentView;
					ng.context.remove(assign.agent);
					
					sendNoGood(ng, assign.agent);
					
				}
				
			}
			
			if(!abt.assigned.sol.equals(value)){
				sendOK(abt.assigned);
			}
			
		}

		private void terminate(int cost) {
			event.setAgreedCost(cost);
			event.setAgreedTimePeriod(abt.assigned.sol);
			
			sendTerminate(cost);
			
			done = true;
			for(VirtualAgent ag : abtbeh.virtualAgents){
				done = done && ag.done;
			}
			
			if(done) agent.solutionReady();
			
		}

		

		private void sendOK(Assignment asgn) {
			
			JSONObject json = new JSONObject();
			try {
				json.put("start", asgn.sol.getStartTime().toString());
				json.put("end", asgn.sol.getEndTime().toString());
				json.put("agent", asgn.agent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			
			for(AID r : abt.inferiorAgents){
				msg.addReceiver(r);
			}
		
            msg.setContent("OK?:" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            agent.send(msg);
            
		}
		
		private void sendNoGood(NoGood ng, AID ag) {
			JSONObject json = new JSONObject();
			try {
				json.put("start", ng.tp.getStartTime().toString());
				json.put("end", ng.tp.getEndTime().toString());
				json.put("register", ng.register);
				json.put("cost", ng.cost);
				json.put("accurate", ng.accurate);
				
				JSONObject context = new JSONObject();
				for(Map.Entry<AID, TimePeriod> con : ng.context.entrySet()){
					JSONObject time = new JSONObject();
					time.put("start", con.getValue().getStartTime().toString());
					time.put("end", con.getValue().getEndTime().toString());
					context.put(con.getKey().toString(), time);
				}
				
				json.put("context", context);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();
			
			ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			msg.addReceiver(ag);
            msg.setContent("NOGOOD:" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-event");
            
            agent.send(msg);
			
		}
		
		private void sendLink(AID ag, Assignment assigned) {
			
			JSONObject json = new JSONObject();
			try {
				json.put("start", assigned.sol.getStartTime().toString());
				json.put("end", assigned.sol.getEndTime().toString());
				json.put("agent", assigned.agent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String jsonmsg = json.toString();

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(ag);
            msg.setContent("LINK:" + event.getName() + "-" + jsonmsg);
            msg.setConversationId("schedule-align");
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
            msg.setContent("TERMINATE:" + event.getName() + "-" + cost);
            msg.setConversationId("schedule-event");
            agent.send(msg);
			
		}

		public void handleOK(ACLMessage msg) {
			String[] sm = msg.toString().split(":");
			sm = sm[1].split("-");
			
			try {
				Assignment asgn = new Assignment();
				JSONObject json = new JSONObject(sm[1]);
				
				StringACLCodec codec = new StringACLCodec(new StringReader(json.get("agent").toString()), null);

				asgn.agent=codec.decodeAID();
				
				String start = json.getString("start");
				String end = json.getString("end");
				DateFormat df = new SimpleDateFormat();
				Calendar startdate = Calendar.getInstance();
				startdate.setTime(df.parse(start));
				Calendar enddate = Calendar.getInstance();
				enddate.setTime(df.parse(end));
				TimePeriod proposal = new TimePeriod(startdate, enddate);
				
				asgn.sol = proposal;
				
				for(NoGood ng : abt.noGoodStore){
					for(Map.Entry<AID, TimePeriod> cont : ng.context.entrySet()){
						if(cont.getKey().equals(asgn.agent) && !cont.getValue().equals(asgn.sol)){
							abt.noGoodStore.remove(cont);
							break;
						}
					}
				}
				
				abt.agentView.put(asgn.agent, asgn.sol);
				adjustValue();
				
			} catch (CodecException | JSONException | ParseException e) {
				e.printStackTrace();
			}
			
			
		}

		public void handleNoGood(ACLMessage msg) {
			String[] sm = msg.toString().split(":");
			sm = sm[1].split("-");
			
			try {
				NoGood ng = new NoGood();
				JSONObject json = new JSONObject(sm[1]);
				
				ng.accurate = json.getBoolean("accurate");
				
				HashMap<AID, TimePeriod> con = new HashMap<AID, TimePeriod>();
				JSONObject context = json.getJSONObject("context");
				Iterator<String> itr = context.keys();
				
		        while (itr.hasNext()) {
		        	String key = itr.next();
		        	
		        	JSONObject time = json.getJSONObject("time");
		        	String start = time.getString("start");
					String end = time.getString("end");
					DateFormat df = new SimpleDateFormat();
					Calendar startdate = Calendar.getInstance();
					startdate.setTime(df.parse(start));
					Calendar enddate = Calendar.getInstance();
					enddate.setTime(df.parse(end));
					TimePeriod proposal = new TimePeriod(startdate, enddate);
					
					StringACLCodec codec = new StringACLCodec(new StringReader(key), null);

					con.put(codec.decodeAID(), proposal);
				}
		        
		        ng.context= con;
		        ng.cost = json.getInt("cost");
		        ng.register = new ArrayList<AID>();
		        
		        JSONArray reg = json.getJSONArray("register");
		        for(int i=0; i<reg.length(); i++){
		        	StringACLCodec codec = new StringACLCodec(new StringReader(reg.getString(i)), null);
		        	ng.register.add(codec.decodeAID());
		        }
		        
				String start = json.getString("start");
				String end = json.getString("end");
				DateFormat df = new SimpleDateFormat();
				Calendar startdate = Calendar.getInstance();
				startdate.setTime(df.parse(start));
				Calendar enddate = Calendar.getInstance();
				enddate.setTime(df.parse(end));
				TimePeriod proposal = new TimePeriod(startdate, enddate);
				
				ng.tp = proposal;
				
				
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

	            for (NoGood oldng : abt.noGoodStore) {
	                if (oldng.tp.equals(ng.tp) && ng.register.containsAll(oldng.register)) {
	                    abt.noGoodStore.remove(oldng);
	                }
	            }

	            abt.noGoodStore.add(ng);
	            adjustValue();
				
			} catch (CodecException | JSONException | ParseException e) {
				e.printStackTrace();
			}
			
		}

		

		public void handleLink(ACLMessage msg) {
			String[] sm = msg.toString().split(":");
			sm = sm[1].split("-");


			Assignment asgn = new Assignment();
			JSONObject json;
			try {
				json = new JSONObject(sm[1]);

				StringACLCodec codec = new StringACLCodec(new StringReader(json.get("agent").toString()), null);

				asgn.agent=codec.decodeAID();

				String start = json.getString("start");
				String end = json.getString("end");
				DateFormat df = new SimpleDateFormat();
				Calendar startdate = Calendar.getInstance();
				startdate.setTime(df.parse(start));
				Calendar enddate = Calendar.getInstance();
				enddate.setTime(df.parse(end));
				TimePeriod proposal = new TimePeriod(startdate, enddate);

				asgn.sol = proposal;
				
				abt.inferiorAgents.add(asgn.agent);
				sendOK(asgn);

			} catch (JSONException | CodecException | ParseException e) {
				e.printStackTrace();
			}

		}

		public void handleTerminate(ACLMessage msg) {
			String[] sm = msg.toString().split(":");
			sm = sm[1].split("-");
			
			int cost = Integer.parseInt(sm[1]);
			
			terminate(cost);
			
		}

	
	}
	
	public static class ABT {
		public Assignment assigned;
		public ArrayList<AID> inferiorAgents;
		public ArrayList<TimePeriod> domain;
		public HashMap<AID, TimePeriod> agentView;
		public ArrayList<NoGood> noGoodStore;
		public int cost;
		public boolean accurate;
		public ArrayList<AID> register;
		
	}
	
	public static class Assignment {
		public TimePeriod sol;
		public AID agent;
	}
	
	public static class NoGood {
		public TimePeriod tp;
		public int cost;
		public HashMap<AID, TimePeriod> context;
		public ArrayList<AID> register;
		public boolean accurate;
	}
	
	@Override
    public void onStart() {
        agent = (MyAgent) myAgent;
        if (agent.events.isEmpty()) {
            done = true;
            return;
        }

        for (MyEvent event : agent.events) {
            virtualAgents.add(new VirtualAgent(agent, event, this));
        }
    }

	@Override
	public void action() {
		ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
		temp.setConversationId("schedule-event");
		MessageTemplate msgtemp = MessageTemplate.MatchConversationId(temp.getConversationId());
		ACLMessage msg = myAgent.receive(msgtemp);

		if (msg != null) {
			String stringmsg = msg.toString();
			String[] sm = stringmsg.split(":");
			if (sm.length == 2) {
				String[] sm2 = sm[1].split("-");
				ArrayList<VirtualAgent> receivers = new ArrayList<VirtualAgent>();
				for(VirtualAgent va : virtualAgents){
					if(sm2[0].equals(va.event.getName())) receivers.add(va);
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
			block();
		}
	}


	@Override
	public boolean done() {
		return done;
	}

}
