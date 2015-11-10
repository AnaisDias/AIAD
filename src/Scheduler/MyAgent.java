package Scheduler;

import java.util.HashMap;
import java.util.Iterator;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

public class MyAgent extends Agent {

	public HashMap<String, AID> agents = new HashMap<>();

	public MyAgent() {
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
		String serviceName = "schedule";
		
		// Register the service
	  	System.out.println("Agent "+getLocalName()+" registering service "+serviceName);

		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("normal");
        sd.setName("iScheduler");
        dfd.addServices(sd);
        try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Build the description used as template for the subscription
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType("normal");
		template.addServices(templateSd);

		
		addBehaviour(new SubscriptionInitiator(this,
				DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + getLocalName() + ": Notification received from DF");
				try {
					DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
					if (results.length > 0) {
						for (int i = 0; i < results.length; ++i) {
							DFAgentDescription dfd = results[i];
							AID provider = dfd.getName();
							// The same agent may provide several services; we
							// are only interested
							// in the normal one
							//Iterator it = dfd.getAllServices();
							while (dfd.getAllServices().hasNext()) {
								ServiceDescription sd = (ServiceDescription) dfd.getAllServices().next();
								if (sd.getType().equals("normal")) {
									System.out.println("normal service for Italy found:");
									System.out.println("- Service \"" + sd.getName() + "\" provided by agent "
											+ provider.getName());
								//adicionar agente a lista
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
	}
}
