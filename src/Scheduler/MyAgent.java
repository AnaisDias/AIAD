package Scheduler;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class MyAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private boolean ready = false;
	public HashMap<String, AID> agentsMap = new HashMap<>();
	public ArrayList<String> neighbors = new ArrayList<String>();
	public ArrayList<AID> allAgents = new ArrayList<AID>();
	public ArrayList<AID> readyAgents = new ArrayList<AID>();
	public ArrayList<MyEvent> events = new ArrayList<MyEvent>();
	public ArrayList<MyEvent> invitations = new ArrayList<MyEvent>();

	public MyAgent() {
	}
	@Override
	protected void setup() {
		System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
		String serviceName = "schedule";
		
		
		// Register the service
	  	System.out.println("Agent "+getLocalName()+" registering service "+serviceName);

	  	DFAgentDescription dfd = new DFAgentDescription();
	      dfd.setName(getAID());
	      ServiceDescription sd = new ServiceDescription();
	      sd.setName(serviceName);
	      sd.setType("Scheduler");
	      dfd.addServices(sd);
	      try {
	         DFService.register(this, dfd);
	      } catch(FIPAException e) {
	         e.printStackTrace();
	      }
		// Build the description used as template for the subscription
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType("Scheduler");
		template.addServices(templateSd);

		
		addBehaviour(new SubscriptionInitiator(this, DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + getLocalName() + ": Notification received from DF");
				try {
					DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
					if (results.length > 0) {
						for (int i = 0; i < results.length; ++i) {
							DFAgentDescription dfd = results[i];
							AID provider = dfd.getName();
							if (!provider.toString().equals(getAID().toString())) {
							//Iterator it = dfd.getAllServices();
							if (dfd.getAllServices().hasNext()) {
								ServiceDescription sd = (ServiceDescription) dfd.getAllServices().next();
								if (sd.getType().equals("Scheduler")) {
									System.out.println("Scheduler found:");
									System.out.println("- Service \"" + sd.getName() + "\" provided by agent "
											+ provider.getName());
								addAgent(provider);
								System.out.println("Agent " + provider.getName() + " added to " + getAID().getName() + "'s agent list");
								}
							}
							else {
								removeAgent(dfd.getName());
								System.out.println("Agent " + provider.getName() + " removed from " + getAID().getName() + "'s agent list");
							}
						}
					}
					}
					System.out.println();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
		allAgents.add(getAID());
	}
	
	private void addAgent(AID agent){
		allAgents.add(agent);
		agentsMap.put(agent.getName(),agent);
	}
	
	private void removeAgent(AID agent){
		allAgents.remove(agent);
		agentsMap.remove(agent.getName());
	}
	
	public void sendInvitations(MyEvent event){
		try {
			invitations.add(event);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("event-creation");

			JSONObject json = new JSONObject();
			json.put("name", event.getName());
			json.put("span", event.getSpan());
			ArrayList<String> guestnames = new ArrayList<String>();
			for(AID guest : event.getGuests()){
				guestnames.add(guest.getName());
				msg.addReceiver(guest);
			}
			json.put("guests", guestnames);

			json.put("proposalStartTime", event.getDateProposal().getStartTime().toString());
			json.put("proposalEndTime", event.getDateProposal().getEndTime().toString());
			
			msg.setContent("INVITE:" + json);
			send(msg);

		} catch (JSONException e) {
			e.printStackTrace();

		}
	}
	
	public void acceptInvitation(MyEvent event){
		
		invitations.remove(event);
		events.add(event);
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId("event-creation");
		msg.setContent("ACCEPT:" + event.getName());
		event.getGuests().forEach(msg::addReceiver);
		send(msg);
		
	}
	
	public void declineInvitation(MyEvent event){
		events.remove(event);
		invitations.remove(event);
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId("event-creation");
		msg.setContent("DECLINE:"+ event.getName());
		event.getGuests().forEach(msg::addReceiver);
		
		send(msg);
	}
	
	public void sendReady(){
		if(ready){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("event-creation");
			msg.setContent("READY: ");
			allAgents.forEach(msg::addReceiver);
			send(msg);
		}
	}
	
	public void sendHalt(){
		if(!ready){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("event-creation");
			msg.setContent("HALT: ");
			allAgents.forEach(msg::addReceiver);
			send(msg);
		}
	}
	
	public boolean getReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	@Override
    protected void takeDown() {
            try {
				DFService.deregister(this);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
            System.out.println("Agent " + getAID().getName() + " terminating.");
    }
	
}
