package Scheduler;

import java.util.HashMap;
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
	public HashMap<String, AID> agents = new HashMap<>();

	public MyAgent() {
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		System.out.println("Hello. I, agent " + getAID().getName() + " am alive now.");
		String serviceName = "Scheduler";

		// Register the service
		System.out.println("Agent " + getLocalName() + " registering service " + serviceName);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(serviceName);
		sd.setType("Scheduler");
		dfd.addServices(sd);

		// Build the description used as template for the subscription
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		sd.setName(serviceName);
		templateSd.setType("Scheduler");
		template.addServices(templateSd);

		addBehaviour(new SubscriptionInitiator(this,
				DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
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
							// Iterator it = dfd.getAllServices();
							if (dfd.getAllServices().hasNext()) {
								ServiceDescription sd = (ServiceDescription) dfd.getAllServices().next();
								if (sd.getType().equals("Scheduler")) {
									System.out.println("Scheduler found:");
									System.out.println("- Service \"" + sd.getName() + "\" provided by agent "
											+ provider.getName());
									// adicionar agente a lista
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
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
