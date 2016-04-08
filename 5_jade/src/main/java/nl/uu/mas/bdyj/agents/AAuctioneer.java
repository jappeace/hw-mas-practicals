package nl.uu.mas.bdyj.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AAuctioneer serves as an abstract base for all other auctioneers.
 * It has handles a lot of Jade boilerplate code and some shared logic for auctions.
 */
abstract public class AAuctioneer extends Agent{
	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	String auctionGood;
	int startingPrice;
	int currentPrice;
	String currentBidder = "";
	AID currentBidderAid;
	public static final Logger log = LoggerFactory.getLogger(AAuctioneer.class);
	private boolean conflictDeal = true;
	boolean closeAuction = false;

	public AAuctioneer(int startingPrice, String auctionGood) {
		this.auctionGood = auctionGood;
		this.startingPrice = startingPrice;
		currentPrice = startingPrice;
	}
	protected void setup() {
		addBehaviour(new ReceiveBid());
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
	}
	public void broadcast(String str){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(auctionGood);
		template.addServices(sd);
		try {
			for (DFAgentDescription agent : DFService.search(this, template)) {
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(agent.getName());
				// content is the latest highest price and the local name of the bidder who cried this price
				// format is localName + " " + currentPrice
				cfp.setContent(str);
				cfp.setConversationId(auctionGood);
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				this.send(cfp);

			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("AuctioneerAgent " + getAID().getLocalName() + " terminating.");
	}
	protected abstract void onReceivePrice(int receivedPrice, AID bidder);
	private class ReceiveBid extends CyclicBehaviour {
		public void action() {

			MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
				MessageTemplate.MatchConversationId(auctionGood)
			);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// PROPOSE Message received. Process it
				// receivePrice is the price received from any bidder agents
				int receivedPrice = Integer.valueOf(msg.getContent());
				conflictDeal = false;
				log.info("receiving: " + receivedPrice + " from " + msg.getSender().getLocalName());
				onReceivePrice(receivedPrice, msg.getSender());

			} else {
				block();
			}
		}
	}
	public void closeAuction(){
		addBehaviour(new CloseAuction());
	}
	protected int getPrice(){
		return currentPrice;
	}
	/*
	 * behaviour for closing the acution
	 */
	private class CloseAuction extends OneShotBehaviour {
		public void action() {
			// if there is no new bid receive in 5s, then award the auction good to the
			// bidder with latest highest price.
			if (!conflictDeal) {
				closeAuction = true;
				System.out.println(" ");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("The auction is closed, the " + auctionGood + " was sold to " + currentBidder + " with the price " + getPrice() + " shekels");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(" ");
				ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				cfp.addReceiver(currentBidderAid);
				cfp.setContent(String.valueOf(currentPrice));
				cfp.setConversationId(auctionGood);
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);

				//send msg to remaining bidders that the auction ended
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(auctionGood);
				template.addServices(sd);
				try {
					for (DFAgentDescription agent : DFService.search(myAgent, template)) {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(agent.getName());
						msg.setContent("auction end");
						msg.setConversationId(auctionGood);
						msg.setReplyWith("msg" + System.currentTimeMillis()); // Unique value
						myAgent.send(msg);
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
			else {
				System.out.println(" ");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("The auction is closed with conflict deal since no bidder agent want to give a price higer than strating price");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(" ");
			}
			myAgent.doDelete();
		}
	}
}
