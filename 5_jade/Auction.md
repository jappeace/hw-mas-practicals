#  MAS -- Exercise: Auction
## 1. Team member information
```sh
StudentName        StudentNo 
Jappie Klooster    5771803
Bram Zijlstra      5774284
Yunlu Chen         5684218
Deliang Wu         5622182
```
### 1.1 Build guide

	./gradlew run

This should fetch gradle and all dependencies (may take a while the first time)

## 2. Implementation
### 2.1 English auction
We created a auctioneer agent and several bidder agents, the auctioneer agent has one auction good to sell with a specific starting price, if there is no bidder agent want to buy the good equal or higher than the starting price, the auction will be closed with conflict deal. Every Bidder agents want to bid this auction good; each bidder agent has their own valuation of the auction good and if the price is higher than their valuation, they will quit the auction. 
##### Interactions between Auctioneer Agent and Bidder Agents in the English auction
* Step1: Auctioneer agent announce the starting price to every bidder agents in the auction;
* Step2: The bidder agents bids according to their price strategies. Since the English auction is an open auction, each bidder agent can know the price given by other agents, and they will decide to give a higer price or quit the auction.
* Step3: The auction will be closed when no new higher price was given in a specific period, the auction good was sold to the bidder agent with the highest bid price.

#### 2.1.1 Auctioneer Agent
we defined 3 behaviours for auctioneer agent:
* SendPrice
* ReceiveBid
* CloseAuction

##### SendPrice
The behaviour SendPrice will be acted at 2 situation:
- The auctioneer announces the starting price at the beginning of the auction;
- The auctioneer received a new higher price for any bidder angent and announce the new price to all bidder agents. (Acutally, it is not needed for real English Auction due to the fact that the English Auction is opencry, every bidder agents can hear the price from any other bidder agents, but in our implementation, to make it simple and compatable with Sealed-Bid Auction, we just ask the auctioneer to transfer the current higher price to any other bidder agents to mimic the opencry).
In these behaviours, the auctioneer will check all bidder agents who still in the auction via the yellow page agent, and send the current highest price to those bidder agents who still in the acution.

##### ReceiveBid
The behaviour ReceiveBid is used for auctioneer to continuously monitor the higer price compared with the current price, if there is a higher price for any bidder agent, then this behaviour will assert getNewPrice and trigger the behaviour SendPrice to send the latest highest price to all bidder agents. 

##### CloseAuction
The Auction will be closed at following 2 situations:
- No bid was given in a specific period after the auctioneer announce the starting price, that means no bidder agent want to buy the auction good with a price equal or higher than the starting price, then the auction will be closed as conflict deal;
- There is no new bid was given after a specific period of lastest higest price, then the auction will be closed, the auction good will be sold to the bidder agent with the higest price.

#### 2.1.2 Bidder Agents
 We defined 2 behaviours for bidde agents:
 * OfferBid
 * WinBid

##### OfferBid
In the OfferBid behaviour, the bidder agents receive the latest highest price from other bidder agents(transftered by auctioneer agent), and decide whether to give a higher price or quit the auction according to their own price strategy and the valuation of the auction good. 

If the latest highest price is lower than the valuation of the bidder agent, then the bidder agent would choise to quit the auction， else we defined 3 price strategies for bidder agents to give a new higher price: 
+ ConstantIncease: increase the price based on the latest highest price by a minimumn increasement which defined by the auctioneer(this is may be the best strategy for bidder agents in English Auction);
+ RandomValuation: increase the price based on the latest highest price by a random price and the result should be less or equel than the valuation of the bidder agent;
+ RandomScaledValuation: increase the price based on the latest highest price by a random price times scaled current highest price.

The price strategy was specified when the bidder agent was borned. The last two strategies is seemed not so rational for bidder agents but sometimes they can accelerate the convergency process of the auction. 

##### WinBid
In this Behaviour, if the bidder agent receive a message from auctioneer agents which accept her bid, then the bidder agent knows she win the bid and then finish and quit the auction.


### 2.2 Dutch auction
In dutch auction, the auctioneer agent has one auction good to sell with a highest specific starting price, if there is a bidder agent want to buy the good, the auction will come to a deal at once and then close. Else, the auctioneer will decrease the price with a constant value each time, until a bidder agent want to buy the good, the auction will come to a deal at once and then close. All agents quit the auction when it is closed. 
##### Interactions between Autioneer Agent and Bidder Agents in the Dutch auction
*	Auctioneer agent announce the starting price to every bidder agents in the auction, then decrease the price with a constant value at each round, until he receives a bid and sell it to the first bidder.
*	If a bidder think the current price announced by auctioneer is acceptable, then he send a bid. If 2 bids received at the same round then only the first one is accepted.
*	The auction will be closed when there is a bid.

#### 2.2.1 Auctioneer Agent
The structure is the same as in English auction, we defined 3 behaviours for auctioneer agent:
* SendPrice
* ReceiveBid
* CloseAuction

The following part would discuss the difference with regard to English auction.(that is, what we have changed based on English auction to implement Dutch auction)

##### SendPrice
- SendPrice at every round (1 second), no need to check if there is a new price (in English auction the auctioneer only SendPrice when there is a new highest bid price)
- At starting of SendPrice it decreases the current price for the current round.
- Only send price, no information for current bidder.

##### ReceiveBid
- Add if-condition for ReceiveBid: only when there is no former bid. (to avoid accepting 2 bids in a round)
- In English auction it updates the current price to the value of bid price if the bid price is higher. But here the process is not needed.
- Set logical indicator 'existBid' to be true. (instead in english auction it is logical indicator for if auctioneer get a new price)
- Timer is removed. It is not needed. 


##### CloseAuction
- In English auction the executing condition for CloseAuction is timer > 5. Here we only need to judge if existBid is true.
- Send bid-accepted message to every bidder(in English auction only send to the winner).


#### 2.2.2 Bidder Agents
The structure is the similar to English auction We defined 2 behaviours for bidder agents:
 * OfferBid
 * EndBid
 
Different to English auction, we use EndBid instead of WinBid.

The following part would discuss the difference with regard to English auction.(that is, what we have changed based on English auction to implement Dutch auction)

##### OfferBid

- Only receive current price, no information for current bidder.
- Here bidder doesn't quit when the price is higher than valuation. (quiting function is moved to EndBid for all bidders)
- if the current price is acceptable, then send a bid. (In english auction the condition is if the bidder doesn't quit and the current bidder is not himself. Both of them are not applicable here.)


##### EndBid

-When received ACCEPT_PROPOSAL message, all bidders quit.

### 2.3 Second-price auction

For the second-price auction, we used the framework of the English auction and edited the behavior of the agents and auctioneer. Where in the English auction we have multiple rounds and bid strategies, we don't need this in the second-price auction. The agents will do only one bid, where the highest bid will get the item for the price of the second highest bid. Since in a second-price aution truth-telling is the dominant strategy, we can use their valuation as the price strategy.

We also make a function for storing the second highest price, so that we can use that at the end of the auction as the selling price. We implement this as following:

```sh
if (receivedPrice > currentPrice) {
	secondPrice = currentPrice;
	currentPrice = receivedPrice;
	currentBidder = sender;
	currentBidderAid = msg.getSender();
```

Now to prevent the (unlikely) case where the first bid that the auctioneer evaluates is also the highest (and so no secondPrice function is made), we made a function the other way around as well:

```sh
} else {
	if (receivedPrice > secondPrice) {
	secondPrice = receivedPrice;

