package nl.uu.mas.bdyj;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;
import nl.uu.mas.bdyj.agents.AuctioneerAgent;
import nl.uu.mas.bdyj.agents.BidderAgent;
import nl.uu.mas.bdyj.agents.AuctioneerAgentDutch;
import nl.uu.mas.bdyj.agents.BidderAgentDutch;
import nl.uu.mas.bdyj.valstrat.ConstantItemValuation;
import nl.uu.mas.bdyj.valstrat.RandomScaledValuation;
import nl.uu.mas.bdyj.valstrat.RandomValuation;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;

import java.util.Random;
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
	public static void startContainer(jade.wrapper.AgentContainer container) throws StaleProxyException {
		Item tem = new Item("candy");
		container.acceptNewAgent("timmy", new BidderAgentDutch(
				new RandomValuation(
						new ConstantItemValuation(120)
				), tem)
		).start();
		container.acceptNewAgent("hendrik", new BidderAgentDutch(
				new RandomScaledValuation(
						new ConstantItemValuation(100)
				), tem)
		).start();
		List<Item> items = new LinkedList<Item>();
		items.add(new Item("blah"));
		//container.acceptNewAgent("leo", new AuctioneerAgent(10,"candy")).start();
		container.acceptNewAgent("leo", new AuctioneerAgentDutch(160,"candy",3)).start();
	}

}
