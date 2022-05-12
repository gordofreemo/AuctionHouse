# cs351-auctionhouse

## Who Did What? 

### Andrew 
Made the AuctionHouse component of the program. 
### Luke 
Made the Agent component of the program and also created the GUI. 
### Brian 
Made the Bank part of the program and had the first idea for the messaging system. 
### Collective 
The messaging system was made as a collective. 

## How To Use The Program 
#### Bank
To start the program, first run the bank program JAR, which takes a command line argument of 
which port the bank should use. After the bank starts, no more user input is needed for that program.
<br> 
After that, you may start the auction houses and the agent jars at any point in time. 
<br> 
#### Auction House 
The auction house program takes 3 command line arguments. The first argument is the hostname for the bank class,
the second is the port number that the bank is using, and the third is the port number to start the auction 
house server on.
<br> 
When running the auction house program, the only input it takes is through the command line. When anything is typed 
into the command line, the program checks to see whether it is safe to exit. If it is, the program exits, otherwise
it is prevented from closing. This is the safe way to stop an auction house. 
#### Agent/GUI 
The GUI jar file takes three command line arguments before it starts. The first argument is the hostname 
of the bank program, the second one is the port number of the bank, and the third is the "name" of the agent, 
and the fourth argument is the initial bank balance that the agent has. 
<br> 
When the GUI first starts, there are three main panel components. The leftmost panel is the auction house
panel, which lets you select which auction house to look at and to refresh and see if there are new active auction houses. 
The middle panel is the bid menu, which lets you look at the items an auction house is offering and to make bids 
on those items. Finally, the rightmost panel displays information about the agent's bank account, such as how many 
funds are left and what finds are available and which are blocks. In the bottom right corner are text status updates
which update whenever something happens. 
<br> 
To make a bid on an item, select an auction house from the left panel, and then select an item to bid on 
from the middle panel. Enter the numerical (whole number) amount you would like to bid into the text 
area and hit submit. If your bid goes through, your screen will be updated with the new bid information. If the 
bid failed, then nothing will be updated on the screen. If you are outbid, your funds will be transferred back into 
your account and you will be alerted about the outbid. 

## How the Program Works 
#### Communication / Messaging System 
Each separate component of the program functions a little differently, but the messaging 
system that we came up with served as the backbone to the rest of the program. The main 
idea going into this project was we needed a way to abstract the communication between the different 
components so that an implementation would not need to rely on any specific class or piece of code 
that somebody else was writing, but rather code to something that would be flexible to change 
and that lay outside the program. What we came up with was the messaging system 
for communicating between the agent, auction house, and bank. 
What each component is responsible for is listening for message objects and 
responding correctly to each one (or sending one out when some kind of update is needed).
The structure of the message object is fairly simple, it holds an enum type for the origin of the 
message (whether bank, auction house, or agent), an enum for the kind of message that is being sent
(such as making a bid or setting up an account with the bank), a slot for a String containing 
some sort of extra parsable information (such as a bid amount), and another random Object slot 
for anything else we might need to send that might not be easily parsable (such as a list of all the items being 
sold by the auction house). This messaging system is represented by Message.java and MessageEnums.java in the util package. 
#### Auction House 
