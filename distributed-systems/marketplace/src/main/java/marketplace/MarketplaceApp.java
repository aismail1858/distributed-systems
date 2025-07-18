package marketplace;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MarketplaceApp {
    public static void main(String[] args) {
        System.out.println("Starting Marketplace Application...");
        
        try {
            // Load configuration with environment variable override support
            Properties config = loadConfiguration();
            
            // Create and start order processor
            OrderProcessor processor = new OrderProcessor(config);
            
            // Setup shutdown hook for graceful shutdown
            CountDownLatch shutdownLatch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down marketplace...");
                processor.shutdown();
                shutdownLatch.countDown();
            }));
            
            // Start processing
            processor.start();
            
            // Keep main thread alive
            try {
                shutdownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            System.err.println("Fatal error starting marketplace: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Properties loadConfiguration() throws IOException {
        Properties config = new Properties();
        
        // Load default configuration
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
        }
        
        // Override with environment variables if present
        String marketplaceId = System.getenv("MARKETPLACE_ID");
        if (marketplaceId != null) {
            config.setProperty("marketplace.id", marketplaceId);
        }
        
        String sellerEndpoints = System.getenv("SELLER_ENDPOINTS");
        if (sellerEndpoints != null) {
            String[] endpoints = sellerEndpoints.split(",");
            for (int i = 0; i < endpoints.length && i < 5; i++) {
                config.setProperty("seller" + (i + 1) + ".endpoint", endpoints[i]);
            }
        }
        
        return config;
    }
}
