package com.port;

import com.port.config.PortConfig;
import com.port.model.Berth;
import com.port.model.Ship;
import com.port.model.Warehouse;
import com.port.service.ShipService;
import com.port.singleton.PortRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Main entry point for the Port Simulator application.
 * Demonstrates concurrent ship operations using threads and synchronization.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            // Load configuration from file
            PortConfig config = PortConfig.getInstance();
            config.load();
            
            logger.info("=== Port Simulator Started ===");
            logger.info("Configuration loaded: {} berths, warehouse capacity: {}, {} ships",
                    config.getBerthCount(), config.getWarehouseCapacity(), config.getShipCount());
            
            // Create warehouse
            Warehouse warehouse = new Warehouse(config.getWarehouseCapacity());
            
            // Create berths
            List<Berth> berths = new ArrayList<>();
            for (int i = 1; i <= config.getBerthCount(); i++) {
                berths.add(new Berth(i));
            }
            
            // Initialize singleton registry
            PortRegistry.getInstance().initialize(warehouse, berths);
            
            // Create ships with random initial containers
            Random random = new Random();
            List<Ship> ships = new ArrayList<>();
            for (int i = 1; i <= config.getShipCount(); i++) {
                int capacity = random.nextInt(config.getMaxCapacity() - config.getMinCapacity() + 1) 
                        + config.getMinCapacity();
                int initialContainers = random.nextInt(config.getMaxContainers() - config.getMinContainers() + 1) 
                        + config.getMinContainers();
                initialContainers = Math.min(initialContainers, capacity);
                ships.add(new Ship(i, capacity, initialContainers));
            }
            
            // Create executor service for managing threads
            ExecutorService executor = Executors.newFixedThreadPool(config.getShipCount());
            List<Future<Void>> futures = new ArrayList<>();
            
            logger.info("Submitting {} ships for processing", ships.size());
            
            // Submit all ships as tasks
            for (Ship ship : ships) {
                ShipService service = new ShipService(
                        ship, 
                        warehouse,
                        config.getMinBerthTime(),
                        config.getMaxBerthTime(),
                        config.getContainerOperationTime()
                );
                Future<Void> future = executor.submit(service);
                futures.add(future);
            }
            
            // Wait for all tasks to complete
            for (int i = 0; i < futures.size(); i++) {
                try {
                    futures.get(i).get(60, TimeUnit.SECONDS);
                    logger.info("Ship-{} completed processing", i + 1);
                } catch (TimeoutException e) {
                    logger.error("Ship-{} timed out", i + 1);
                }
            }
            
            // Shutdown executor
            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            
            // Print final status
            logger.info("=== Final Status ===");
            logger.info("Warehouse containers: {}/{}", warehouse.getCurrentContainers(), warehouse.getCapacity());
            for (Ship ship : ships) {
                logger.info("{} - Final state: {}", ship, ship.getStateName());
            }
            logger.info("=== Port Simulator Completed ===");
            
        } catch (Exception e) {
            logger.error("Error during simulation", e);
        }
    }
}
