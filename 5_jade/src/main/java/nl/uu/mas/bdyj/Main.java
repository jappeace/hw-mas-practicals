package nl.uu.mas.bdyj;

import nl.uu.mas.bdyj.agents.SecondBidderAgent;
import nl.uu.mas.bdyj.agents.SecondAuctioneerAgent;
import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;
import nl.uu.mas.bdyj.agents.AuctioneerAgent;
import nl.uu.mas.bdyj.agents.BidderAgent;
import nl.uu.mas.bdyj.agents.AuctioneerAgentDutch;
import nl.uu.mas.bdyj.agents.BidderAgentDutch;
import nl.uu.mas.bdyj.valstrat.*;
import org.slf4j.LoggerFactory;
import nl.uu.mas.bdyj.valstrat.ConstantItemValuation;
import nl.uu.mas.bdyj.valstrat.RandomScaledValuation;
import nl.uu.mas.bdyj.valstrat.RandomValuation;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;
import java.util.Scanner;
class Main{
    
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] argsb) {
	
            log.info("the JADI returns!");
		final Agent a = new Agent(){
			@Override
			protected void setup() {
				super.setup();
				log.info("agent reporting");
			}
		};
		int port = new Random().nextInt(100)+1000;
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

        int auction = 0;
        int bidders = 10;
        //System.out.println("How many agents do you want:");
        //int numberOfAgents = Integer.parseInt(reader.nextLine());        
        switch (auction){
            case 0: System.out.println("Welcome to the English Auction!");
            startAuction(new Factory(){

            @Override
            public void createBidder(int i) throws StaleProxyException {
                container.acceptNewAgent("bidder" + i, new BidderAgent(
                new ConstantIncrease(
                new ConstantItemValuation(10*i),i), tem));
                        }

            @Override
            public void createAuctioneer() throws StaleProxyException {
            container.acceptNewAgent("leo", new AuctioneerAgent(0,tem.name)).start();
            }
                
            }, bidders)
            ; break ;
            case 1: System.out.println("Welcome to the Dutch Auction!");
            ; break ;
            case 2: System.out.println("Welcome to the Second Price Auction");
            ; break ;
                
        //System.out.println("We have " + numberOfAgents + " agents today!");
        }
        
              
        }
}
