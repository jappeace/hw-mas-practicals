## Exercise 1

Give a detailed specification of one cycle of the contract-net protocol for computer hardware configuration. Specify the process, the message types and the possible content of messages. The content of messages depends on how computers and components are specified.

### Specification

In a cycle of the contract-net protocol a node generates a task, after which he advertises that task to other nodes in the net with a **task
announcement**. In our case we already specified the manager, named *manager.2apl*. The other nodes in our setting are called *contractor1.2apl*,
*contractor2.2apl* and *contractor3.2apl*. Since the manager doesn't have any knowledge of the other nodes beforehand, we let him issue
a **general broadcast** to all other nodes.

#### Task announcement

First the manager decides what computers he is looking for, which will be part ofhis inital beliefbase. The manager has the following
beliefs:
1. The desired pcs
2. The announcement of the pcs he wants
3. Whether or not he awards the contract to contractor x.

Beliefs about needed computers take the structure of:

    desired_pc(pc(number, case, motherboard, processor, graphics))
Each of these items can contain several items or can be unspecified. 

In the task announcement, the manager can state what he is looking for. These messages will be send to the contractors, and a deadline time
is set. This waiting time is also in the managers beliefbase, along with the beliefupdate to update until the deadline has passed.

### Bid round

As explained before, each contractor has its own .2apl file, consisting of

    include: contractorbase.2apl;
    
    beliefs:
	    include: components1.pl;
	    
Since all contractors work in the same way, we build one contractorbase file for all contractors. The beliefs each node has is a different components
file, with the number corresponding to the contractor. The contractors will receive an announcement from the manager and they will search
through there own beliefbase. These announcements consist of the previously described requirements. If one of the parts is unspecified,
the contractor will ignore it. If the contractor finds a matched item in its beliefbase, he will send a bid to the manager:
    sendresponse(propose,
					pc_case(
						MANUF_R,
						TYPE_R,
						SHAPE_R,
						ATX_R,
						POWER_R,
						COL_R,
						SUITFOR_R,
						PRICE_R
					)
With all the specified parts and the bid price. If the contractor doens't have a corresponding pc_case, he will send a ```sendresponse(refuse, SPEC).

	### Selection and award message
Now the manager may receive several bids, and can select the best bid. The best bid in this case is just the cheapest price, so the manager
can start with the first bid and then moving on to the next, while keep rejecting the worst offer every time. The manager will send
accept_proposal to the the contractor with the best bid, and reject_proposals to the others.

