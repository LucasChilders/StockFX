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
    Label stockName = new Label();
    Label stockPrice = new Label();
    Label stockLowHigh = new Label();
    Stock stock;

    NumberFormat formatter = NumberFormat.getCurrencyInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StocksFX");
        primaryStage.setResizable(false);

        FlowPane root = new FlowPane(10, 0);
        Scene scene = new Scene(root, 300, 275);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setMinWidth(300);
        sep.setPadding(new Insets(0, 0, 7, 0));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 25, 25, 15));

        TextField input = new TextField();
        input.setPromptText("symbol");
        input.setStyle("-fx-display-caret: false;-fx-text-box-border: #a5a5a5;-fx-focus-color: #77beff;-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        input.setMinWidth(50);
        input.setAlignment(Pos.CENTER);

        HBox txArea = new HBox(input);
        txArea.setStyle("-fx-background-color: #FFFFFF;");

        txArea.setMargin(input, new Insets(10, 0, 10, 0));
        txArea.setMinWidth(scene.getWidth());
        txArea.setAlignment(Pos.CENTER);

        input.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                 stock = StockFetcher.getStock(input.getText());
                loadStock();
            }
        });

        stockName.setPadding(new Insets(0, 0, 10, 0));
        stockName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        grid.add(stockName, 0, 0);
        grid.add(stockPrice, 0, 1);
        grid.add(stockLowHigh, 0, 2);

        root.getChildren().addAll(txArea, sep, grid);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadStock() {
        stockName.setText(stock.getName() + " (" + stock.getSymbol() + ")");
        stockPrice.setText("Price: " + formatter.format(stock.getPrice()));
        stockLowHigh.setText("Day Low/High: " + formatter.format(stock.getDaylow()) + " - " + formatter.format(stock.getDayhigh()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
