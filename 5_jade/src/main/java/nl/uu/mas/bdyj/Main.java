package nl.uu.mas.bdyj;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;
import nl.uu.mas.bdyj.agents.*;
import nl.uu.mas.bdyj.valstrat.*;
import org.slf4j.LoggerFactory;
import nl.uu.mas.bdyj.valstrat.ConstantItemValuation;
import nl.uu.mas.bdyj.valstrat.RandomScaledValuation;
import org.slf4j.Logger;

import java.util.*;

class Main{
    
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	private static final Random rng = new Random();
	public static void main(String[] argsb) {

		// please ignore some JADE hacking to retrieve the main container
		// SEE the statcontainer function for the start of the auction
		int port = rng.nextInt(1000)+1000;
		String[] args = ("-gui -local-port "+port).split(" ");
		try {
			ProfileImpl iae = null;
			if(args.length > 0) {
				if(args[0].startsWith("-")) {
					Properties pp = Boot.parseCmdLineArgs(args);
					if(pp == null) {
						return;
					}

					iae = new ProfileImpl(pp);
				} else {
					iae = new ProfileImpl(args[0]);
				}
			} else {
				iae = new ProfileImpl("leap.properties");
			}

			Runtime.instance().setCloseVM(true);
			if(iae.getBooleanProperty("main", true)) {
				startContainer(Runtime.instance().createMainContainer(iae));
			} else {
				startContainer(Runtime.instance().createAgentContainer(iae));
			}
		} catch (ProfileException var3) {
			System.err.println("Error creating the Profile [" + var3.getMessage() + "]");
			var3.printStackTrace();
			Boot.printUsage();
			System.exit(-1);
			} catch (IllegalArgumentException var4) {
			System.err.println("Command line arguments format error. " + var4.getMessage());
			var4.printStackTrace();
			Boot.printUsage();
			System.exit(-1);
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	   interface Factory{
		   void createBidder(int i) throws StaleProxyException;
		   void createAuctioneer() throws StaleProxyException;

	   }
	   static void startAuction(Factory factory, int bidderCount) throws StaleProxyException {
		   for (int i=0; i<bidderCount; i++){
			   factory.createBidder(i);
		   }
		factory.createAuctioneer();
	   }
	public static void startContainer(final jade.wrapper.AgentContainer container) throws StaleProxyException {
	final Item tem = new Item("candy");

        int auction = 2;
        int bidders = 3;
        //System.out.println("How many agents do you want:");
        //int numberOfAgents = Integer.parseInt(reader.nextLine());
		final List<NextPriceStrategy> strategies = new ArrayList<NextPriceStrategy>();
		strategies.add(new ConstantIncrease( new ConstantItemValuation(45),1));
		strategies.add(new RandomScaledValuation(new ConstantItemValuation(20))); // I know this is unsafe
		strategies.add(new ConstantIncrease( new ConstantItemValuation(100),5));
        switch (auction){
            case 0: // English
				System.out.println("Welcome to the English Auction!");
				startAuction(new Factory(){
					@Override
					public void createBidder(int i) throws StaleProxyException {
						container.acceptNewAgent("bidder" + i, new BidderAgent(strategies.get(rng.nextInt(strategies.size())), tem)).start();
					}
					@Override
					public void createAuctioneer() throws StaleProxyException {
						container.acceptNewAgent("leo", new AuctioneerAgent(0,tem.name)).start();
					}
				}, bidders);
				break;
            case 1: // Dutch
				System.out.println("Welcome to the Dutch Auction!");
				startAuction(new Factory(){
					@Override
					public void createBidder(int i) throws StaleProxyException {
						container.acceptNewAgent("bidder" + i, new BidderAgentDutch(strategies.get(rng.nextInt(strategies.size())), tem)).start();
					}
					@Override
					public void createAuctioneer() throws StaleProxyException {
						container.acceptNewAgent("leo", new AuctioneerAgentDutch(1000,tem.name,1)).start();
					}
				}, bidders);
				break ;
            case 2: // SecondPrice sealed bid
				System.out.println("Welcome to the Second Price Auction");
				startAuction(new Factory(){
					@Override
					public void createBidder(int i) throws StaleProxyException {
						container.acceptNewAgent("bidder" + i, new BidderAgentSecond(strategies.get(rng.nextInt(strategies.size())).getValuation(), tem)).start();
					}
					@Override
					public void createAuctioneer() throws StaleProxyException {
						container.acceptNewAgent("leo", new AuctioneerAgentSecond(0,tem.name)).start();
					}
				}, bidders);
				break;
        }
        }
}
