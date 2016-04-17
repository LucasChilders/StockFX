import javafx.application.Application;
import javafx.geometry.*;
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
    NumberFormat nf = NumberFormat.getInstance();

    Label stockName = new Label();
    Label stockPrice = new Label();
    Label stockPriceLabel = new Label();
    Label stockLowHigh = new Label();
    Label stockPrevClose = new Label();
    Label stockChange = new Label();
    Label stockChangeLabel = new Label();
    Label stockVolume = new Label();

    Stock stock;
    boolean stockLoaded = false;
    double today, yesterday;

    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StocksFX");
        primaryStage.setResizable(false);

        FlowPane root = new FlowPane(10, 0);
        Scene scene = new Scene(root, 300, 275);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setMinWidth(scene.getWidth() + 10);
        sep.setPadding(new Insets(0, 0, 0, 0));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 25, 25, 15));

        TextField input = new TextField();
        input.setPromptText("symbol");
        input.setStyle("-fx-display-caret: false;-fx-text-box-border: #a5a5a5;-fx-focus-color: #77beff;-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        input.setMinWidth(50);
        input.setAlignment(Pos.CENTER);

        HBox txArea = new HBox(input);
        txArea.setStyle("-fx-background-color: #FFFFFF;");
        txArea.setPrefWidth(scene.getWidth() + 10);
        txArea.setMargin(input, new Insets(10, 0, 10, 0));
        txArea.setAlignment(Pos.CENTER);

        HBox priceBox = new HBox(stockPrice);
        priceBox.setPadding(new Insets(5, 0, 0, 0));
        priceBox.setMinWidth(scene.getWidth() + 10);
        priceBox.setAlignment(Pos.CENTER);
        stockPrice.setFont(Font.font("Arial", 48));

        HBox changeBox = new HBox(stockChange);
        changeBox.setPadding(new Insets(-5, 0, 5, 0));
        changeBox.setMinWidth(scene.getWidth() + 10);
        changeBox.setAlignment(Pos.CENTER);
        stockChange.setFont(Font.font("Arial", 24));

        input.setOnKeyPressed(e -> {
            txArea.setStyle("-fx-background-color: #FFFFFF;");

            if (stockLoaded) {
                input.setText("");
                stockLoaded = false;
            }

            if (e.getCode() == KeyCode.ENTER) {
                stock = StockFetcher.getStock(input.getText());
                loadStock();

                if (today > yesterday) {
                    txArea.setStyle("-fx-background-color: #89ff90;");
                }

                else if (yesterday > today) {
                    txArea.setStyle("-fx-background-color: #ff8179;");
                }

                else if (yesterday == today) {
                    txArea.setStyle("-fx-background-color: #FFFFFF;");
                }
            }
        });

        stockName.setPadding(new Insets(0, 0, 10, 0));
        stockName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        grid.add(stockName, 0, 0);
        grid.add(stockPriceLabel, 0, 1);
        grid.add(stockChangeLabel, 0, 2);
        grid.add(stockVolume, 0, 3);
        grid.add(stockPrevClose, 0, 4);
        grid.add(stockLowHigh, 0, 5);

        root.getChildren().addAll(txArea, sep, priceBox, changeBox, grid);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadStock() {
        if (stock.getName().equals("N/A")) {
            resetLabels();
            stockChange.setText("not found");
            stockLoaded = true;
            return;
        }

        stockName.setText(stock.getName() + " (" + stock.getSymbol() + ")");
        stockPrice.setText(formatter.format(stock.getPrice()).substring(1));
        stockPriceLabel.setText("Price: " + formatter.format(stock.getPrice()));
        stockVolume.setText("Volume: " + nf.format(stock.getVolume()));
        stockLowHigh.setText("Day Low/High: " + formatter.format(stock.getDaylow()) + " - " + formatter.format(stock.getDayhigh()));
        stockPrevClose.setText("Yesterday's Close: " + formatter.format(stock.getPreviousClose()));
        today = stock.getPrice();
        yesterday = stock.getPreviousClose();

        double change = today - yesterday;
        String changeStr;

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
        stockLoaded = true;
    }

    public void resetLabels() {
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
