package mainGUI;

import Agent.Agent;
import AuctionHouse.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {
    public final int WIDTH = 1000;
    public final int HEIGHT = 600;
    public Agent agent;
    public BorderPane border = new BorderPane();
    public AuctionHouse currHouse;
    public String agentName = "Agent";
    public HashMap<AuctionHouse, String> auctionHouseNameMap = new HashMap<>();
    public List<String> allHouseNames = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        agent = new Agent(100, "Bob");
        agentName = agent.name;
        forTesting();

        pullHouseNames();

        border.setLeft(showActionHouses());
        border.setCenter(items());
        border.setRight(showBankInfo());

        Scene scene = new Scene(border, WIDTH, HEIGHT);
        primaryStage.setTitle("Auction House");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Pull all the house names from the text file. Similar to the nouns/adjectives
     */
    public void pullHouseNames(){
        InputStream s1 = ClassLoader.getSystemResourceAsStream("houseNames.txt");
        Scanner sc = new Scanner(s1);
        while(sc.hasNext()) allHouseNames.add(sc.nextLine());
    }

    /**
     * This function is implemented for testing purposes
     * It fills the agent with a fake auction house with fake items to make
     * sure the elements of the GUI are working correctly
     */
    public void forTesting() throws IOException {
        AuctionHouse testHouse = new AuctionHouse();
        AuctionHouse houseTwo = new AuctionHouse();

        agent.auctionHouses = new ArrayList<>();
        agent.auctionHouses.add(testHouse);
        agent.auctionHouses.add(houseTwo);

        currHouse = testHouse;
    }

    /**
     * Create the vbox that will hold the available auction houses
     * @return
     */
    public VBox showActionHouses(){
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40,40,10,40));
        vbox.setSpacing(30);

        Text auctionHouseTitleText = new Text("Auction Houses");
        auctionHouseTitleText.setStyle("-fx-font: 18 arial;");
        vbox.getChildren().add(auctionHouseTitleText);

        if(agent.auctionHouses == null) return vbox;

        List<Button> auctionLinks = new ArrayList<>();
        for(int i = 0; i < agent.auctionHouses.size(); i++){
            Button newButton = auctionListButton(agent.auctionHouses.get(i));
            auctionLinks.add(newButton);
        }

        // Loop through the auction house buttons we made and add them to the vbox
        for (Button auctionLink : auctionLinks) {
            vbox.getChildren().add(auctionLink);
        }

        return vbox;
    }

    /**
     * Makes the button that changes which auction house the agent is connected to
     * @return the button
     */
    public Button auctionListButton(AuctionHouse house){
        String houseName = getHouseName(house);
        Button auctionButton = new Button(houseName);
        auctionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currHouse = house;
                border.setCenter(items());
            }
        });
        return auctionButton;
    }

    /**
     * Assigns a name from the list to the auction house
     * @param house the house that needs a name
     * @return the name for the house
     */
    public String getHouseName(AuctionHouse house){
        int r1 = (int)(Math.random() * allHouseNames.size());
        if(auctionHouseNameMap.containsValue(allHouseNames.get(r1))) return getHouseName(house);
        else auctionHouseNameMap.put(house, allHouseNames.get(r1));

        return auctionHouseNameMap.get(house);
    }
    /**
     * Creates the tab panes that holds all the values from the current auciton house
     * @return
     */
    public TabPane items(){
        TabPane itemPane = new TabPane();
        itemPane.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        List<Tab> tabs = new ArrayList<>();

        // For testing, since the list of auction houses is null
        // @todo remove once list of auction houses is functional
        if(agent.auctionHouses == null) {
            for (int i = 0; i < 3; i++) {
                String name = "Item Name " + (i+1);
                tabs.add(testTab(name));
            }
            itemPane.getTabs().addAll(tabs);
        }

       if(currHouse == null) return itemPane; // Don't try to build the tabs if there are no auction houses
        for(Auction auction : currHouse.getAuctions()){
            Tab itemTab = itemTab(auction);
            itemPane.getTabs().add(itemTab);
        }

        return itemPane;
    }

    /**
     * Used for testing tab creations
     * @return the testTab
     */
    public Tab testTab(String name){
        Tab testTab = new Tab(name);
        VBox mainVBox = new VBox();
        mainVBox.setSpacing(40);

        VBox itemName = new VBox();
        Text itemDescription = new Text("Item Name will go here");
        itemName.getChildren().add(itemDescription);

        VBox itemId = new VBox();
        Text id = new Text("Item ID will go here");
        itemId.getChildren().add(id);

        VBox currBid = new VBox();
        Text currentBid = new Text("Current Bid: $60");
        currBid.getChildren().add(currentBid);

        VBox makeBid = new VBox();
        makeBid.setSpacing(5);
        TextField bidAmount = new TextField("Bid Amount");
        bidAmount.setMaxWidth(200);
        Text minBid = new Text("Make bid of $60 or more");
        Button placeBid = new Button("Place bid");
        makeBid.getChildren().addAll(bidAmount, minBid, placeBid);


        mainVBox.getChildren().addAll(
                itemName,
                itemId,
                currBid,
                makeBid
        );

        testTab.setContent(mainVBox);
        testTab.setClosable(false); // Don't allow the tab to be closed
        return testTab;
    }

    /**
     * Creates one tab that presents the individual listing
     * @param auction the individual listing
     * @return the created tab
     */
    public Tab itemTab(Auction auction){
        //@todo get these values from the auction class
        int currBid = 0;
        double bidScale = 1.25;

        String itemName = auction.getItem().description;
        Tab itemTab = new Tab(itemName);
        VBox mainVBox = new VBox();
        mainVBox.setSpacing(20);

        // Set the title of the item for sale
        VBox itemTitle = new VBox();
        itemTitle.setPadding(new Insets(30,10,0,10));
        Text itemDescription = new Text(itemName);
        itemDescription.setStyle("-fx-font: 16 arial;");
        itemTitle.getChildren().add(itemDescription);

        // Add the item ID
        VBox itemIDBox = new VBox();
        Text itemID = new Text("Item number " + auction.getAuctionID());
        itemIDBox.setPadding(new Insets(0,10,0,10));
        itemID.setStyle("-fx-font: 14 arial;");
        itemIDBox.getChildren().add(itemID);

        // Add the current bid price,
        VBox curBidVBox = new VBox();
        curBidVBox.setPadding(new Insets(0,10,0,10));
        Text curBidText = new Text("Leading bid: $" + 0); //@todo get the current bid from the auction
        curBidText.setStyle("-fx-font: 14 arial;");
        curBidVBox.getChildren().add(curBidText);

        // Add the HBox which contains the bid textfield
        HBox makeBidHBox = new HBox();
        Text makeBidText = new Text("Make a bid of $" + (currBid*bidScale) + " or more     ");
        TextField typeBid = new TextField();
        Button submitBid = new Button("Submit Bid");
        submitBid.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //@todo call make bid function after checking if agent can bid given amount
                System.out.println("Bid submitted");
            }
        });

        makeBidHBox.getChildren().addAll(makeBidText, typeBid, submitBid);

        mainVBox.getChildren().addAll(itemTitle, itemIDBox, curBidVBox, makeBidHBox);
        itemTab.setContent(mainVBox);
        return itemTab;
    }

    /**
     * Add the vbox that will hold the agent's bank information
     * @return vbox
     */
    public VBox showBankInfo(){
        VBox vbox = new VBox();
        vbox.setSpacing(30);
        vbox.setPadding(new Insets(30,50,30,30));
        vbox.setStyle("-fx-background-color: #e4ebf7;");

        // Display agent name
        Text agentNameText = new Text(agentName + "'s Bank Account");
        agentNameText.setStyle("-fx-font: 24 arial;");

        // Display Total Agent Balance
        Text totalBalanceText = new Text("Total Balance: $" + agent.balance);
        totalBalanceText.setStyle("-fx-font: 16 arial;");

        // Display the amount the agent has in pending purchases
        HBox pendingBidsHbox = new HBox();

        Text pendingBidsText = new Text("Pending Bids:  ");
        pendingBidsText.setStyle("-fx-font: 16 arial;");
        Text pendBdsAmmText = new Text("-$"+ (agent.balance - agent.avaliableBalance));
        pendBdsAmmText.setFill(Color.RED);
        pendBdsAmmText.setStyle("-fx-font: 16 arial;");
        pendingBidsHbox.getChildren().addAll(pendingBidsText, pendBdsAmmText);

        // Display the amount the agent has to spend (total - pending bids)
        Text availableFundsText = new Text("Available: $" + agent.avaliableBalance);
        availableFundsText.setStyle("-fx-font: 16 arial;");

        vbox.getChildren().addAll(agentNameText, totalBalanceText, pendingBidsHbox, availableFundsText);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
