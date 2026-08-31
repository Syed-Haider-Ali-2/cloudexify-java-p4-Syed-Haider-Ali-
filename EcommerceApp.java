
package com.ecommerce.app;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Random;

public class ECommerceApp extends Application {

    private ObservableList<Product> productCatalog = FXCollections.observableArrayList();
    private ShoppingCart cart = new ShoppingCart();
    private Customer currentCustomer = new Customer("CUST-101", "Alex Mercer");

    private TableView<Product> catalogTable = new TableView<>();
    private TableView<ShoppingCart.CartItem> cartTable = new TableView<>();
    private TableView<Order> historyTable = new TableView<>();
    private Label lblCartTotal = new Label("Total: Rs 0.00");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudExify E-Commerce Store");

        seedData();

        VBox topBanner = buildTopBanner();

        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab catalogTab = new Tab("Catalog", buildCatalogView());
        Tab cartTab = new Tab("Cart", buildCartView());
        Tab historyTab = new Tab("Order History", buildHistoryView());

        mainTabs.getTabs().addAll(catalogTab, cartTab, historyTab);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBanner);
        mainLayout.setCenter(mainTabs);

        Scene mainScene = new Scene(mainLayout, 950, 650);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private VBox buildTopBanner() {
        VBox layout = new VBox(4);
        layout.setPadding(new Insets(12, 16, 12, 16));
        layout.setStyle("-fx-background-color: #1F2937;");

        Label appTitle = new Label("CloudExify Store");
        appTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label userLabel = new Label("Logged in as: " + currentCustomer.getName() + " (" + currentCustomer.getCustomerID() + ")");
        userLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px;");

        layout.getChildren().addAll(appTitle, userLabel);
        return layout;
    }

    private VBox buildCatalogView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TableColumn<Product, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductID()));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Product, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClass().getSimpleName()));

        TableColumn<Product, Double> colBasePrice = new TableColumn<>("Base Price (Rs)");
        colBasePrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        TableColumn<Product, Double> colFinalPrice = new TableColumn<>("Final Price (Rs)");
        colFinalPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getFinalPrice()).asObject());

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        catalogTable.getColumns().addAll(colId, colName, colCategory, colBasePrice, colFinalPrice, colStock);
        catalogTable.setItems(productCatalog);
        catalogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox actionPane = new HBox(10);
        actionPane.setAlignment(Pos.CENTER_LEFT);

        Label qtyLabel = new Label("Qty:");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 50, 1);
        qtySpinner.setPrefWidth(75);

        Button btnAdd = new Button("Add to Cart");
        btnAdd.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold;");

        btnAdd.setOnAction(e -> {
            Product selected = catalogTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showNotice(Alert.AlertType.WARNING, "No Selection", "Please select an item from the table.");
                return;
            }

            try {
                cart.addItem(selected, qtySpinner.getValue());
                updateCartUI();
                showNotice(Alert.AlertType.INFORMATION, "Success", "Item added to cart.");
            } catch (OutOfStockException ex) {
                showNotice(Alert.AlertType.ERROR, "Stock Error", ex.getMessage());
            } catch (Exception ex) {
                showNotice(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        actionPane.getChildren().addAll(qtyLabel, qtySpinner, btnAdd);

        layout.getChildren().addAll(catalogTable, actionPane);
        VBox.setVgrow(catalogTable, Priority.ALWAYS);
        return layout;
    }

    private VBox buildCartView() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(15));

        TableColumn<ShoppingCart.CartItem, String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));

        TableColumn<ShoppingCart.CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        TableColumn<ShoppingCart.CartItem, Double> colPrice = new TableColumn<>("Price (Rs)");
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getProduct().getFinalPrice()).asObject());

        TableColumn<ShoppingCart.CartItem, Double> colTotal = new TableColumn<>("Total (Rs)");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getItemTotal()).asObject());

        cartTable.getColumns().addAll(colItem, colQty, colPrice, colTotal);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox bottomPane = new HBox(15);
        bottomPane.setAlignment(Pos.CENTER_RIGHT);

        lblCartTotal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Button btnCheckout = new Button("Checkout");
        btnCheckout.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold;");

        btnCheckout.setOnAction(e -> processCheckout());

        bottomPane.getChildren().addAll(lblCartTotal, btnCheckout);

        layout.getChildren().addAll(cartTable, bottomPane);
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        return layout;
    }

    private VBox buildHistoryView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TableColumn<Order, String> colOrderId = new TableColumn<>("Order ID");
        colOrderId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderID()));

        TableColumn<Order, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderDate()));

        TableColumn<Order, Double> colTotal = new TableColumn<>("Total (Rs)");
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());

        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        historyTable.getColumns().addAll(colOrderId, colDate, colTotal, colStatus);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        layout.getChildren().add(historyTable);
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        return layout;
    }

    private void processCheckout() {
        try {
            if (cart.getItems().isEmpty()) {
                throw new Exception("Cart is empty.");
            }

            double total = cart.getTotalPrice();
            cart.checkout();

            String newOrderId = "ORD-" + (1000 + new Random().nextInt(9000));
            Order newOrder = new Order(newOrderId, currentCustomer, cart.getItems(), total);
            currentCustomer.purchaseOrder(newOrder);

            cart.clearCart();
            updateCartUI();
            catalogTable.refresh();
            updateHistoryUI();

            showNotice(Alert.AlertType.INFORMATION, "Order Placed", "Order #" + newOrderId + " confirmed!");

        } catch (OutOfStockException ex) {
            showNotice(Alert.AlertType.ERROR, "Stock Error", ex.getMessage());
        } catch (Exception ex) {
            showNotice(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    private void updateCartUI() {
        cartTable.setItems(FXCollections.observableArrayList(cart.getItems()));
        lblCartTotal.setText(String.format("Total: Rs %.2f", cart.getTotalPrice()));
    }

    private void updateHistoryUI() {
        historyTable.setItems(FXCollections.observableArrayList(currentCustomer.getOrders()));
    }

    private void seedData() {
        productCatalog.add(new Electronics("E101", "Gaming Laptop", 185000.0, 5, "Asus", 24));
        productCatalog.add(new Electronics("E102", "Wireless Mouse", 3500.0, 15, "Logitech", 12));
        productCatalog.add(new Book("B201", "Java Performance Tuning", 4500.0, 8, "Scott Oaks", "978-1492056119", 400));
        productCatalog.add(new Book("B202", "Clean Code", 5200.0, 2, "Robert C. Martin", "978-0132350884", 464));
        productCatalog.add(new Clothing("C301", "Denim Jacket", 7500.0, 10, "L", "Denim", "Blue"));
        productCatalog.add(new Clothing("C302", "Cotton T-Shirt", 1800.0, 0, "M", "Cotton", "Black"));
    }

    private void showNotice(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
