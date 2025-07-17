package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Loads orders from properties file.
 */
public class OrderLoader {
    
    /**
     * Loads orders from a properties file.
     * Format:
     * orders.count=5
     * order.1.id=ORDER-001
     * order.1.items.count=2
     * order.1.items.1.productId=P1
     * order.1.items.1.sellerId=seller1
     * order.1.items.1.quantity=5
     */
    public static List<OrderData> loadOrdersFromProperties(String filename) {
        List<OrderData> orders = new ArrayList<>();
        
        try {
            Properties props = new Properties();
            props.load(new FileReader(filename));
            
            int orderCount = Integer.parseInt(props.getProperty("orders.count", "0"));
            
            for (int i = 1; i <= orderCount; i++) {
                String orderId = props.getProperty("order." + i + ".id");
                if (orderId != null) {
                    OrderData order = new OrderData();
                    order.orderId = orderId;
                    order.customerId = props.getProperty("order." + i + ".customerId", "customer" + i);
                    order.items = new ArrayList<>();
                    
                    int itemCount = Integer.parseInt(props.getProperty("order." + i + ".items.count", "0"));
                    
                    for (int j = 1; j <= itemCount; j++) {
                        String productId = props.getProperty("order." + i + ".items." + j + ".productId");
                        String sellerId = props.getProperty("order." + i + ".items." + j + ".sellerId");
                        String quantityStr = props.getProperty("order." + i + ".items." + j + ".quantity");
                        
                        if (productId != null && sellerId != null && quantityStr != null) {
                            OrderItem item = new OrderItem();
                            item.productId = productId;
                            item.sellerId = sellerId;
                            item.quantity = Integer.parseInt(quantityStr);
                            order.items.add(item);
                        }
                    }
                    
                    orders.add(order);
                }
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading orders: " + e.getMessage());
            // Return default orders if file loading fails
            return generateDefaultOrders();
        }
        
        return orders;
    }
    
    /**
     * Loads orders from a simple text file format.
     * Each line: orderId|customerId|productId|sellerId|quantity
     */
    public static List<OrderData> loadOrdersFromText(String filename) {
        List<OrderData> orders = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentOrderId = null;
            OrderData currentOrder = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String orderId = parts[0].trim();
                    String customerId = parts[1].trim();
                    String productId = parts[2].trim();
                    String sellerId = parts[3].trim();
                    int quantity = Integer.parseInt(parts[4].trim());
                    
                    // New order
                    if (!orderId.equals(currentOrderId)) {
                        if (currentOrder != null) {
                            orders.add(currentOrder);
                        }
                        currentOrder = new OrderData();
                        currentOrder.orderId = orderId;
                        currentOrder.customerId = customerId;
                        currentOrder.items = new ArrayList<>();
                        currentOrderId = orderId;
                    }
                    
                    // Add item to current order
                    OrderItem item = new OrderItem();
                    item.productId = productId;
                    item.sellerId = sellerId;
                    item.quantity = quantity;
                    currentOrder.items.add(item);
                }
            }
            
            // Add last order
            if (currentOrder != null) {
                orders.add(currentOrder);
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading orders from text file: " + e.getMessage());
            return generateDefaultOrders();
        }
        
        return orders;
    }
    
    /**
     * Generates default orders when file loading fails.
     */
    public static List<OrderData> generateDefaultOrders() {
        List<OrderData> orders = new ArrayList<>();
        
        // Order 1
        OrderData order1 = new OrderData();
        order1.orderId = "ORDER-001";
        order1.customerId = "customer1";
        order1.items = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.productId = "P1";
        item1.sellerId = "seller1";
        item1.quantity = 5;
        order1.items.add(item1);
        
        OrderItem item2 = new OrderItem();
        item2.productId = "P2";
        item2.sellerId = "seller2";
        item2.quantity = 3;
        order1.items.add(item2);
        
        orders.add(order1);
        
        // Order 2
        OrderData order2 = new OrderData();
        order2.orderId = "ORDER-002";
        order2.customerId = "customer2";
        order2.items = new ArrayList<>();
        
        OrderItem item3 = new OrderItem();
        item3.productId = "P1";
        item3.sellerId = "seller1";
        item3.quantity = 10;
        order2.items.add(item3);
        
        OrderItem item4 = new OrderItem();
        item4.productId = "P3";
        item4.sellerId = "seller3";
        item4.quantity = 2;
        order2.items.add(item4);
        
        OrderItem item5 = new OrderItem();
        item5.productId = "P2";
        item5.sellerId = "seller4";
        item5.quantity = 7;
        order2.items.add(item5);
        
        orders.add(order2);
        
        // Order 3
        OrderData order3 = new OrderData();
        order3.orderId = "ORDER-003";
        order3.customerId = "customer3";
        order3.items = new ArrayList<>();
        
        OrderItem item6 = new OrderItem();
        item6.productId = "P1";
        item6.sellerId = "seller1";
        item6.quantity = 100; // Intentionally large to trigger out-of-stock
        order3.items.add(item6);
        
        orders.add(order3);
        
        // Order 4
        OrderData order4 = new OrderData();
        order4.orderId = "ORDER-004";
        order4.customerId = "customer4";
        order4.items = new ArrayList<>();
        
        OrderItem item7 = new OrderItem();
        item7.productId = "P3";
        item7.sellerId = "seller5";
        item7.quantity = 15;
        order4.items.add(item7);
        
        OrderItem item8 = new OrderItem();
        item8.productId = "P1";
        item8.sellerId = "seller3";
        item8.quantity = 8;
        order4.items.add(item8);
        
        OrderItem item9 = new OrderItem();
        item9.productId = "P2";
        item9.sellerId = "seller4";
        item9.quantity = 12;
        order4.items.add(item9);
        
        orders.add(order4);
        
        // Order 5
        OrderData order5 = new OrderData();
        order5.orderId = "ORDER-005";
        order5.customerId = "customer5";
        order5.items = new ArrayList<>();
        
        OrderItem item10 = new OrderItem();
        item10.productId = "P2";
        item10.sellerId = "seller2";
        item10.quantity = 20;
        order5.items.add(item10);
        
        OrderItem item11 = new OrderItem();
        item11.productId = "P3";
        item11.sellerId = "seller5";
        item11.quantity = 10;
        order5.items.add(item11);
        
        orders.add(order5);
        
        return orders;
    }
    
    /**
     * Data class for order information.
     */
    public static class OrderData {
        public String orderId;
        public String customerId;
        public List<OrderItem> items;
    }
    
    /**
     * Data class for order items.
     */
    public static class OrderItem {
        public String productId;
        public String sellerId;
        public int quantity;
    }
}