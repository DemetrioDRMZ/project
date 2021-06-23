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

import java.util.ArrayList;

import project.BassicDemo;
import project.ProductoDAO;
import project.ProductoVO;

public class SellerAgent extends Agent{

	private String table;
	public BassicDemo clips;
	public ProductoDAO dao;
	public ProductoVO vo, tmp;
	public int cliente;
	public int orden;
	public String nombreP;
	public boolean supplier;

	private AID[] supplierAgents;
    
    protected void setup(){

        Object[] args = getArguments();
		if (args != null && args.length > 0) {
			table = (String) args[0];

			clips = new BassicDemo();
			dao = new ProductoDAO(table);
			vo = new ProductoVO();
			tmp = new ProductoVO();
			cliente = 0;
			orden = 0;
			supplier = false;
			nombreP = "";

			System.out.println("los productos son de " + table);
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("product-selling");
			sd.setName("JADE-product-trading");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}

			TELL(1);

			// Add the behaviour serving queries from buyer agents
			addBehaviour(new OfferRequestsServer());

			// Add the behaviour serving purchase orders from buyer agents
			addBehaviour(new PurchaseOrdersServer());
		}
		else{
			// Make the agent terminate
			System.out.println("No target table specified");
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
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName()+" terminating.");
	}

	public void TELL(int momento){
		switch(momento){
			case 1:
			    //Instanciamos nuestra KB con las plantillas, los clientes y las reglas;
				clips.cargarPlantillas("C:/Users/cuatr/Downloads/agent-repo-main/clips/project/"+table+"/templates.clp");
				clips.cargarPlantillas("C:/Users/cuatr/Downloads/agent-repo-main/clips/project/"+table+"/facts.clp");
				clips.cargarPlantillas("C:/Users/cuatr/Downloads/agent-repo-main/clips/project/"+table+"/rules.clp");
				clips.inicializarEI();
				ArrayList<ProductoVO> list = dao.Listar_ProductoVO();
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						vo = list.get(i);
						clips.cargarProducto(vo.getNombre(), vo.getPart_number(), vo.getCategoria(), vo.getPrice());
					}
				}
			break;
			case 2:
			    //Agregamos las ordenes a la KB
				clips.cargarOrder(Integer.toString(orden), Integer.toString(cliente));
				clips.cargarLineItem(Integer.toString(orden), vo.getPart_number(), Integer.toString(cliente), "1");
			break;
			default: System.out.println("algo pasa");
		} 
	}

	public void ASK(int momento){
		switch(momento){
			case 1: //Ejecuta las reglas de la IE
			    clips.ejecutarReglas();
			break;
			case 2: // Verifica si todavia hay productos para vender
			    int cantidad = Integer.parseInt(tmp.getCantidad());
				cantidad = cantidad -1;
			    if(cantidad == 0){ //Busca al supplier para rellenar el stock
				    supplier = true;
					addBehaviour(new TickerBehaviour(this, 60000) {
						protected void onTick() {
							if(supplier == true){
								System.out.println("try to supplier "+ tmp.getNombre());
								// Update the list of seller agents
								DFAgentDescription template = new DFAgentDescription();
								ServiceDescription sd = new ServiceDescription();
								sd.setType("product-supplier");
								template.addServices(sd);
								try { 
									DFAgentDescription[] result = DFService.search(myAgent, template); 
									System.out.println("Found the following supplier agents:");
									supplierAgents = new AID[result.length];
									for (int i = 0; i < result.length; ++i) {
										supplierAgents[i] = result[i].getName();
										System.out.println(supplierAgents[i].getName());
									}
								}
								catch (FIPAException fe) {
									fe.printStackTrace();
								}

								// Perform the request
								myAgent.addBehaviour(new RequestPerformer());
						   }
						   else{
							   stop();
						   }
						}
					} ); 
				}
			break;
			case 3: // verificiar si el producto no disponible es del agente
			    if(dao.ProductoVO_Existe(nombreP)){
					tmp.setNombre(nombreP);
					tmp.setCantidad("1");
					ASK(2);	
				}
				
			break;
			case 4:
				if(!dao.ProductoVO_Disponible(vo.getNombre())){
					tmp.setNombre(vo.getNombre());
					tmp.setCantidad("1");
					ASK(2);
				}
			break;
			default: System.out.println("algo pasa 2");
		}
	}

    private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String producto = msg.getContent();
				ACLMessage reply = msg.createReply();
				ProductoVO vo = new ProductoVO();

				// The requested product is available for sale. Reply with the price
				if(dao.ProductoVO_Disponible(producto)){
					orden++;
					cliente++;
					vo = dao.obtener_producto(producto);
				    Integer price = (Integer) Integer.parseInt(vo.getPrice());
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(price.intValue()));
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
					nombreP = producto;
					ASK(3);
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
				String product = msg.getContent();
				ACLMessage reply = msg.createReply();
				vo = dao.obtener_producto(product);

				Integer price = (Integer) Integer.parseInt(vo.getPrice());
				if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					TELL(2);
					System.out.println(product +" sold to agent "+ msg.getSender().getName());
					int x = Integer.parseInt(vo.getCantidad()) - 1;
					dao.Rellenar_ProductoVO(vo.getNombre(), Integer.toString(x));
					ASK(4);
					
					

					if(cliente >= 3){
						ASK(1);
					}
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

    private class RequestPerformer extends Behaviour {
		private AID bestSupplier; // The agent who provides the suply 
		private MessageTemplate mt; // The template to receive replies
		private int canti = 0;
		private int repliesCnt = 0;
		private int step = 0;

		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < supplierAgents.length; ++i) {
					cfp.addReceiver(supplierAgents[i]);
				} 
				cfp.setContent(vo.getNombre());
				cfp.setConversationId("supplier-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("supplier-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int cantidad = Integer.parseInt(reply.getContent());
						if (bestSupplier == null || cantidad < canti) {
							// This is the best offer at present
							canti = cantidad;
							bestSupplier = reply.getSender();
						}
					}
					repliesCnt++;
					if (repliesCnt >= supplierAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSupplier);
				order.setContent(nombreP);
				order.setConversationId("supplier-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("supplier-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(tmp.getNombre() +" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Quantity = "+ canti);
						String numero = Integer.toString(canti);
						dao.Rellenar_ProductoVO(tmp.getNombre(), numero);

					}
					else {
						System.out.println("Attempt failed.");
					}

					step = 4;
					supplier = false;
				}
				else {
					block();
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && bestSupplier == null) {
				System.out.println("Attempt failed: "+tmp.getNombre()+" not available for supplier");
			}
			return ((step == 2 && bestSupplier == null) || step == 4);
		}
	}  // End of inner class RequestPerformer
    
} 