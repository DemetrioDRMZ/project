package project;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class SupplierAgent extends Agent {
	private String product;

    protected void setup() {

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			product = (String) args[0];
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("product-supplier");
			sd.setName("JADE-product-supplier");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}

			// Add the behaviour serving queries from buyer agents
			addBehaviour(new OfferRequestsServer());

			// Add the behaviour serving purchase orders from buyer agents
			addBehaviour(new PurchaseOrdersServer());
		}
		else{
			// Make the agent terminate
			System.out.println("No target producto to supplier specified");
			doDelete();
		}
		
    }

    protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

        // connec.desconectar();
		// Printout a dismissal message
		System.out.println("Supplier-agent "+getAID().getName()+" terminating.");
	}

    private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String producto = msg.getContent();
				ACLMessage reply = msg.createReply();
				String cantidad = "5";
		
				Integer quantity = (Integer) Integer.parseInt(cantidad);
				if (product.equals(producto)) {
					// The requested is available for suplier. Reply with the quantity of product to suplier
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(quantity.intValue()));
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

    private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				System.out.println(product +" supplier to agent "+msg.getSender().getName());
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer    
}