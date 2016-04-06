#  MAS -- Exercise: Auction
## 1. Team member information
```sh
StudentName        StudentNo 
Jappie Klooster    5771803
Bram Zijlstra      5774284
Yunlu Chen         5684218
Deliang Wu         5622182
```

## 2. Implementation
### 2.1 English auction
We created a auctioneer agent and several bidder agents, the auctioneer agent has one auction good to sell with a specific starting price, if there is no bidder agent want to buy the good equal or higher than the starting price, the auction will be closed with conflict deal. And every Bidder agents want to bid this auction good, each bidder agent has their own valuation of the auction good, if the price if higher than their valuation, they will quit the auction. 
##### Interactions between Autioneer Agent and Bidder Agents in the English auction
* Step1. Auctioneer agent announce the starting price to every bidder agents in the acution;
* Step2. The bidder agents cry their price according to their price strategies, since the English auction is opencry, so each bidder agent can know the price given by other agents, and they will decide to give a higer price or quit the auction;
* Step3. The auction will be closed when there is no new higher price was given in a specific period, the auction good was sold to the bidder agent whit the higest bid price.

#### 2.1.1 Auctioneer Agent
we defined 3 behaviours for auctioneer agent:
* SendPrice
* ReceiveBid
* CloseAuction
##### SendPrice
The behaviour SendPrice will be acted at 2 situation:
- The auctioneer announce the starting price at the beginning of the auction;
- The auctionner received a new higher price for any bidder angent and announce the new price to all bidder agents. (Acutally, it is not needed for real English Auction due to the fact that the English Auction is opencry, every bidder agents can hear the price from any other bidder agents, but in our implementation, to make it simple and compatable with Sealed-Bid Auction, we just ask the auctioneer to transfer the current higher price to any other bidder angets to mimic the opencry).
In this behaviours, the auctioneer will check all bidder agents who still in the auction via the yellow page agent, and send the current highest price to those bidder agents who still in the acution.
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

If the latest highest price is lager than the valuation of the bidder agent, then the bidder agent would choise to quit the auction， else we defined 3 price strategies for bidder agents to give a new higher price: 
+ ConstantIncease: increase the price based on the latest highest price by a minimumn increasement which defined by the auctioneer(this is may be the best strategy for bidder agents in English Auction);
+ RandomValuation: increase the price based on the latest highest price by a random price and the result should be less or equel than the valuation of the bidder agent;
+ RandomScaledValuation: increase the price based on the latest highest price by a random price times scaled current highest price.

The price strategy was specified when the bidder agent was borned. The last two strategies is seemed not so rational for bidder agents but sometimes they can accelerate the convergency process of the auction. 

##### WinBid
In this Behaviour, if the bidder agent receive a message from auctioneer agents which accept her bid, then the bidder agent knows she win the bid and then finish and quit the auction.
