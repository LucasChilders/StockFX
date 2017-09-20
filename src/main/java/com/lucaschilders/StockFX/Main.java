package com.lucaschilders.StockFX;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import yahoofinance.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

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

    //Loading YahooFinance class
    private yahoofinance.Stock stock;

    //Other variables
    private boolean stockLoaded = false;
    private double today;
    private double yesterday;
    private String company;

    //Constants
    private int SCENE_WIDTH = 300;
    private int SCENE_HEIGHT = 300;
    private int LEFT_PAD = 0;

    private ArrayList<String> tickers;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Checking for OS to change UI scale
        if (System.getProperty("os.name").contains("Mac")) {
            SCENE_WIDTH = 300;
            SCENE_HEIGHT = 310;
            LEFT_PAD = -10;
        }

        // Reading file
        tickers = new ArrayList<>();
        readFile();

        // Setting window title and size options
        primaryStage.setTitle("StocksFX");
        primaryStage.setResizable(false);

        // BorderPane main wrapper
        BorderPane borderPane = new BorderPane();

        // FlowPane wrapper
        FlowPane root = new FlowPane(10, 0);

        // Taking user input
        TextField input = new TextField();

        //Horizontal separator
        Separator sep = new Separator(Orientation.HORIZONTAL);

        // GridPane for the information
        GridPane grid = new GridPane();

        // Creating HBoxes to wrap other elements in
        HBox txArea = new HBox(input);
        HBox priceBox = new HBox(stockPrice);
        HBox changeBox = new HBox(stockChange);

        // Alert Window - ABOUT
        Alert aboutWindow = new Alert(Alert.AlertType.INFORMATION);
        aboutWindow.setTitle("About");
        aboutWindow.setHeaderText("StockFX");
        aboutWindow.setContentText("Lucas Childers\nApril 17th, 2016");

        // Alert Window - HELP
        Alert helpWindow = new Alert(Alert.AlertType.INFORMATION);
        helpWindow.setTitle("Help");
        helpWindow.setHeaderText("StockFX");
        helpWindow.setContentText("Type the symbol (ticker) of a given stock into the text field. The text field does " +
                "not have a blinking cursor, but it is selected by default. Press enter after typing in the stock and " +
                "allow the program to load in the information. Data should be loaded in very quickly, depending on your " +
                "internet connection. ");

        // MenuBar
        Menu help = new Menu("Help");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(help);
        MenuItem aboutItem = new MenuItem("About");
        MenuItem helpItem = new MenuItem("Help");
        menuBar.setStyle("-fx-background-color: white;-fx-border-style: solid;-fx-border-color: lightgrey;-fx-border-width: 1 0 0 0;");
        help.getItems().addAll(aboutItem, new SeparatorMenuItem(), helpItem);

        // MenuItem actions
        aboutItem.setOnAction(e -> aboutWindow.showAndWait());
        helpItem.setOnAction(e -> helpWindow.showAndWait());

        // Placing the nodes in the window
        borderPane.setBottom(menuBar);
        borderPane.setCenter(root);

        // The main scene (window)
        Scene scene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);

        // Setting element attributes

        // Labels
        stockPrice.setFont(Font.font("Arial", 48));
        stockChange.setFont(Font.font("Arial", 24));
        stockName.setPadding(new Insets(0, 0, 10, 0));
        stockName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Separator adding 10 to the width of window (Windows bug, Mac doesn't need it)
        sep.setMinWidth(SCENE_WIDTH + 10);

        // Text box
        input.setPromptText("symbol");
        input.setStyle("-fx-min-width: 50px;" +
                "-fx-display-caret: false;" +
                "-fx-text-box-border: #a5a5a5;" +
                "-fx-focus-color: #77beff;" +
                "-fx-alignment: center;" +
                "-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        // Text box wrapper (HBox)
        txArea.setStyle("-fx-background-color: #FFFFFF;");
        txArea.setPrefWidth(SCENE_WIDTH + 10);
        txArea.setPadding(new Insets(10, 0, 10, LEFT_PAD));
        txArea.setAlignment(Pos.CENTER);

        // Big price label
        priceBox.setPadding(new Insets(5, 0, 0, LEFT_PAD));
        priceBox.setMinWidth(SCENE_WIDTH + 10);
        priceBox.setAlignment(Pos.CENTER);

        // Day change label (below price)
        changeBox.setPadding(new Insets(-5, 0, 5, LEFT_PAD));
        changeBox.setMinWidth(SCENE_WIDTH + 10);
        changeBox.setAlignment(Pos.CENTER);

        // Every time the user types a letter, run this
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
                try {
                    stock = YahooFinance.get(input.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

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

        // Setting GridPane attributes and children
        grid.setPadding(new Insets(5, 25, 25, 15));
        grid.add(stockName, 0, 0);
        grid.add(stockPriceLabel, 0, 1);
        grid.add(stockChangeLabel, 0, 2);
        grid.add(stockVolume, 0, 3);
        grid.add(stockPrevClose, 0, 4);
        grid.add(stockLowHigh, 0, 5);

        // Add everything to the FlowPane wrapper
        root.getChildren().addAll(txArea, sep, priceBox, changeBox, grid);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadStock() {
        // If the stock couldn't be found
        if (!stock.isValid()) {
            resetLabels();
            stockChange.setText("not found");
            company = "";
            today = 0.0;
            yesterday = 1;
            stockLoaded = true;
            return;
        }

        // Loading data to get colors
        today = stock.getQuote().getPrice().doubleValue();

        yesterday = stock.getQuote().getPreviousClose().doubleValue();

        double change = today - yesterday;
        String changeStr;

        // Labels (in order)

        // Name / Ticker
        stockName.setText(stock.getName() + " (" + stock.getSymbol() + ")");

        // Big price
        stockPrice.setText(formatter.format(today).substring(1));

        // Normal price
        stockPriceLabel.setText("Price: " + formatter.format(today));

        // Change in price and percentage
        if (change < 0) {
            stockChangeLabel.setText("Change: -" + formatter.format(change * -1).substring(0, 5) + " (-" + String.valueOf((change * -1) / today * 100).substring(0, 4) + "%)");
            change *= -1;
            changeStr = "-" + formatter.format(change).substring(1);
        }
        else {
            changeStr = formatter.format(change).substring(1);
            stockChangeLabel.setText("Change: " + formatter.format(change)  + " (" + String.valueOf((change) / today * 100).substring(0, 4) + "%)");
        }
        stockChange.setText(changeStr);

        // Volume traded
        stockVolume.setText("Volume: " + nf.format(stock.getQuote().getVolume()));

        // Set stock range for the day
        stockLowHigh.setText("Day Low/High: " + formatter.format(stock.getQuote().getDayLow()) + " - " + formatter.format(stock.getQuote().getDayHigh()));

        // Yesterday closing price
        stockPrevClose.setText("Yesterday's Close: " + formatter.format(yesterday));

        //com.lucaschilders.Stock has been loaded
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

    private void readFile() {
        try {
            String str;
            BufferedReader reader = new BufferedReader(new FileReader("assets/tickers.txt"));
            while ((str = reader.readLine()) != null) {
                tickers.add(str);
            }
        } catch (IOException e) {
            System.out.println("Error reading file or file not found\n" + e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
