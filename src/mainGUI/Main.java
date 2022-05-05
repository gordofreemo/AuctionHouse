package mainGUI;

import Agent.Agent;
import AuctionHouse.*;
import javafx.application.Application;
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
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public final int WIDTH = 1000;
    public final int HEIGHT = 600;
    public Agent agent;
    public BorderPane border = new BorderPane();
    public AuctionHouse currHouse;
    public String agentName = "Agent";

    @Override
    public void start(Stage primaryStage) throws IOException {
        agent = new Agent(100, "Test");
        // forTesting();


        border.setLeft(showActionHouses());
        border.setCenter(items());
        border.setRight(showBankInfo());

        Scene scene = new Scene(border, WIDTH, HEIGHT);
        primaryStage.setTitle("Auction House");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * This function is implemented for testing purposes
     * It fills the agent with a fake auction house with fake items to make
     * sure the elements of the GUI are working correctly
     */
    public void forTesting() throws IOException {
        AuctionHouse testHouse = new AuctionHouse();

        agent.auctionHouses.add(testHouse);
    }

    /**
     * Create the vbox that will hold the available auction houses
     * @return
     */
    public VBox showActionHouses(){
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(40,40,10,40));


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
        Button auctionButton = new Button();


        return auctionButton;
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
        if(currHouse == null) {
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
        String itemName = auction.getItem().description;
        Tab itemTab = new Tab(itemName);
        VBox mainVBox = new VBox();
        mainVBox.setSpacing(10);



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
