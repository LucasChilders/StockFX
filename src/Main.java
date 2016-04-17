/*
Lucas Childers
StockFX
April 16th, 2016
 */

import javafx.application.Application;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.text.NumberFormat;

public class Main extends Application {
    //Number Format for currency and commas
    private NumberFormat nf = NumberFormat.getInstance();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance();

    //Labels
    private Label stockName = new Label();
    private Label stockPrice = new Label();
    private Label stockPriceLabel = new Label();
    private Label stockLowHigh = new Label();
    private Label stockPrevClose = new Label();
    private Label stockChange = new Label();
    private Label stockChangeLabel = new Label();
    private Label stockVolume = new Label();

    //Loading Stock class
    private Stock stock;

    private boolean stockLoaded = false;
    private double today, yesterday;
    private final int SCENE_WIDTH = 300;
    private String company;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StocksFX");
        primaryStage.setResizable(false);

        //FlowPane wrapper
        FlowPane root = new FlowPane(10, 0);

        //taking user input
        TextField input = new TextField();

        //Horizontal separator
        Separator sep = new Separator(Orientation.HORIZONTAL);

        //GridPane for the information
        GridPane grid = new GridPane();

        //Creating HBoxes to wrap other elements in
        HBox txArea = new HBox(input);
        HBox priceBox = new HBox(stockPrice);
        HBox changeBox = new HBox(stockChange);

        //The main scene (window)
        Scene scene = new Scene(root, SCENE_WIDTH, 275);

        //Setting element attributes

        //Labels
        stockPrice.setFont(Font.font("Arial", 48));
        stockChange.setFont(Font.font("Arial", 24));
        stockName.setPadding(new Insets(0, 0, 10, 0));
        stockName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        //Separator adding 10 to the width of window (Windows bug, Mac doesn't need it)
        sep.setMinWidth(SCENE_WIDTH + 10);

        //Text box
        input.setPromptText("symbol");
        input.setStyle("-fx-min-width: 50px;" +
                "-fx-display-caret: false;" +
                "-fx-text-box-border: #a5a5a5;" +
                "-fx-focus-color: #77beff;" +
                "-fx-alignment: center;" +
                "-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        //Text box wrapper (HBox)
        txArea.setStyle("-fx-background-color: #FFFFFF;");
        txArea.setPrefWidth(SCENE_WIDTH + 10);
        txArea.setPadding(new Insets(10, 0, 10, 0));
        txArea.setAlignment(Pos.CENTER);

        //Big price label
        priceBox.setPadding(new Insets(5, 0, 0, 0));
        priceBox.setMinWidth(SCENE_WIDTH + 10);
        priceBox.setAlignment(Pos.CENTER);

        //Day change label (below price)
        changeBox.setPadding(new Insets(-5, 0, 5, 0));
        changeBox.setMinWidth(SCENE_WIDTH + 10);
        changeBox.setAlignment(Pos.CENTER);

        //Every time the user types a letter, run this
        input.setOnKeyPressed(e -> {
            txArea.setStyle("-fx-background-color: #FFFFFF;");

            //If a stock has already been displayed
            if (stockLoaded) {
                input.setText("");
                primaryStage.setTitle("StocksFX");
                stockLoaded = false;
            }

            //When ENTER is pressed
            if (e.getCode() == KeyCode.ENTER) {
                stock = StockFetcher.getStock(input.getText());
                loadStock();
                primaryStage.setTitle("StocksFX" + company);

                //If the change is positive
                if (today > yesterday) {
                    txArea.setStyle("-fx-background-color: #89ff90;");
                }

                //If the change is negative
                else if (yesterday > today) {
                    txArea.setStyle("-fx-background-color: #ff8179;");
                }

                //If nothing has changed
                else if (yesterday == today) {
                    txArea.setStyle("-fx-background-color: #FFFFFF;");
                }
            }
        });

        //Setting GridPane attributes and children
        grid.setPadding(new Insets(5, 25, 25, 15));
        grid.add(stockName, 0, 0);
        grid.add(stockPriceLabel, 0, 1);
        grid.add(stockChangeLabel, 0, 2);
        grid.add(stockVolume, 0, 3);
        grid.add(stockPrevClose, 0, 4);
        grid.add(stockLowHigh, 0, 5);

        //Add everything to the FlowPane wrapper
        root.getChildren().addAll(txArea, sep, priceBox, changeBox, grid);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadStock() {
        //If the stock couldn't be found
        if (stock.getName().equals("N/A")) {
            resetLabels();
            stockChange.setText("not found");
            company = "";
            today = 0;
            yesterday = 1;
            stockLoaded = true;
            return;
        }

        //Loading data to get colors
        today = stock.getPrice();
        yesterday = stock.getPreviousClose();

        double change = today - yesterday;
        String changeStr;

        //Labels (in order)

        //Name / Ticker
        stockName.setText(stock.getName() + " (" + stock.getSymbol() + ")");

        //Big price
        stockPrice.setText(formatter.format(stock.getPrice()).substring(1));

        //Normal price
        stockPriceLabel.setText("Price: " + formatter.format(stock.getPrice()));

        //Change in price and percentage
        if (change < 0) {
            stockChangeLabel.setText("Change: -" + formatter.format(change * -1).substring(0, 5) + " (-" + String.valueOf((change * -1) / stock.getPrice() * 100).substring(0, 4) + "%)");
            change *= -1;
            changeStr = "-" + formatter.format(change).substring(1);
        }
        else {
            changeStr = formatter.format(change).substring(1);
            stockChangeLabel.setText("Change: " + formatter.format(change)  + " (" + String.valueOf((change) / stock.getPrice() * 100).substring(0, 4) + "%)");
        }
        stockChange.setText(changeStr);

        //Volume traded
        stockVolume.setText("Volume: " + nf.format(stock.getVolume()));

        //Set stock range for the day
        stockLowHigh.setText("Day Low/High: " + formatter.format(stock.getDaylow()) + " - " + formatter.format(stock.getDayhigh()));

        //Yesterday closing price
        stockPrevClose.setText("Yesterday's Close: " + formatter.format(stock.getPreviousClose()));

        //Stock has been loaded
        stockLoaded = true;
        company = " - " + stock.getName();
    }

    private void resetLabels() {
        //Reset all labels to blank

        stockName.setText("");
        stockPrice.setText("");
        stockPriceLabel.setText("");
        stockVolume.setText("");
        stockLowHigh.setText("");
        stockPrevClose.setText("");
        stockChange.setText("");
        stockChangeLabel.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
