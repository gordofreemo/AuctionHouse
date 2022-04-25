package mainGUI;

import AuctionHouse.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.*;

public class Main extends Application {
    public final int WIDTH = 1000;
    public final int HEIGHT = 600;

    public BorderPane border = new BorderPane();

    @Override
    public void start(Stage primaryStage){

        border.setLeft(showActionHouses());
        border.setCenter(itemPane());
        border.setRight(showBankInfo());

        Scene scene = new Scene(border, WIDTH, HEIGHT);
        primaryStage.setTitle("Auction House");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Create the vbox that will hold the available auction houses
     * @return
     */
    public VBox showActionHouses(){
        VBox vbox = new VBox();
        Text fillerText = new Text("Auction Houses");

        vbox.setPadding(new Insets(40,0,10,100));

        vbox.getChildren().add(fillerText);

        return vbox;
    }

    /**
     * The center pane that displays the item and it's info (name, bids, etc)
     * @return
     */
    public Pane itemPane(){
        StackPane pane = new StackPane();
        int canvasW = 500;
        Canvas canvas = new Canvas(canvasW,HEIGHT);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillText("Display Item and Bid Information",170,100);
        gc.strokeRect(0,-1,canvasW,HEIGHT+10);

        pane.getChildren().add(canvas);
        return pane;
    }

    /**
     * Add the vbox that will hold the agent's bank information
     * @return vbox
     */
    public VBox showBankInfo(){
        VBox vbox = new VBox();

        Text fillerText = new Text("Agent Bank Info");
        vbox.setPadding(new Insets(40,100,10,10));

        vbox.getChildren().add(fillerText);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
