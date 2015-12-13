package Scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import Behaviours.ABTBehaviour;
import Behaviours.CreateEventBehaviour;
import gui.AgentFxController;
import gui.Main;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private boolean ready = false;
	public ObservableList<AID> neighbors =  FXCollections.observableArrayList();
	public  ObservableList<AID> allAgents = FXCollections.observableArrayList();
	public ObservableList<AID> readyAgents = FXCollections.observableArrayList();
	public ObservableList<MyEvent> events = FXCollections.observableArrayList();
	public ObservableList<MyEvent> invitations = FXCollections.observableArrayList();
	public HashMap<String, AID> agentsMap = new HashMap<String, AID>();
	public SimpleBooleanProperty allReady= new SimpleBooleanProperty(false);
	private static boolean solutionBlock=false;

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
		agentsMap.put(getName(), getAID());
		addBehaviour(new CreateEventBehaviour());
	}
	
	private void addAgent(AID agent){
		allAgents.add(agent);
		neighbors.add(agent);
		agentsMap.put(agent.getName(),agent);
	}
	
	private void removeAgent(AID agent){
		allAgents.remove(agent);
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

			json.put("proposal", event.getDateProposal().toString());
			
			msg.setContent("INVITE-" + json);
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
		msg.setContent("ACCEPT-" + event.getName());
		event.getGuests().forEach(msg::addReceiver);
		send(msg);
		if(invitations.isEmpty()){
			ready=true;
			sendReady();
		}
		
	}
	
	public void declineInvitation(MyEvent event){
		events.remove(event);
		invitations.remove(event);
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId("event-creation");
		msg.setContent("DECLINE-"+ event.getName());
		event.getGuests().forEach(msg::addReceiver);
		
		send(msg);
	}
	
	public void sendReady(){
		if(invitations.isEmpty()) ready=true;
		if(ready){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("event-creation");
			msg.setContent("READY-no more invitations");
			allAgents.forEach(msg::addReceiver);
			send(msg);
		}
	}
	
	public void sendHalt(){
		if(!ready){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("event-creation");
			msg.setContent("HALT-new invitation");
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
            System.out.println(getAID().getName() + ":  bye");
    }
	
	//precisa de ser chamado pela interface quando todos os agentes estiverem ready (readyAgents contains all allAgents)
	public void startAlgorithm(){
		addBehaviour(new ABTBehaviour());
	}
	public void solutionReady() {
		// c�digo para avisar interface que algoritmo acabou
		System.out.println("called ");
		if(MyAgent.solutionBlock){
			return;
		}
		MyAgent.solutionBlock=true;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/template/init.fxml"));

		Stage stage = new Stage();
		stage.setTitle("treta");
		Scene scene;
		try {
			scene = new Scene(loader.load());
			stage.setScene(scene);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stage.show();
	}
	
}
