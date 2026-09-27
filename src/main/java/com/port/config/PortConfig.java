package com.port.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Configuration loader for port simulation parameters.
 * Reads from config.properties file.
 */
public class PortConfig {
    private static volatile PortConfig instance;
    private final ReentrantLock lock = new ReentrantLock();
    
    private int warehouseCapacity;
    private int berthCount;
    private int shipCount;
    private int minContainers;
    private int maxContainers;
    private int minCapacity;
    private int maxCapacity;
    private int minBerthTime;
    private int maxBerthTime;
    private int containerOperationTime;
    private boolean loaded = false;

    private PortConfig() {}

    public static PortConfig getInstance() {
        if (instance == null) {
            synchronized (PortConfig.class) {
                if (instance == null) {
                    instance = new PortConfig();
                }
            }
        }
        return instance;
    }

    public void load() throws IOException {
        lock.lock();
        try {
            if (!loaded) {
                Properties props = new Properties();
                try (InputStream input = getClass().getClassLoader()
                        .getResourceAsStream("config.properties")) {
                    if (input == null) {
                        throw new IOException("Unable to find config.properties");
                    }
                    props.load(input);
                }

                warehouseCapacity = Integer.parseInt(props.getProperty("warehouse.capacity", "100"));
                berthCount = Integer.parseInt(props.getProperty("berth.count", "3"));
                shipCount = Integer.parseInt(props.getProperty("ship.count", "5"));
                minContainers = Integer.parseInt(props.getProperty("ship.min.containers", "10"));
                maxContainers = Integer.parseInt(props.getProperty("ship.max.containers", "40"));
                minCapacity = Integer.parseInt(props.getProperty("ship.min.capacity", "50"));
                maxCapacity = Integer.parseInt(props.getProperty("ship.max.capacity", "80"));
                minBerthTime = Integer.parseInt(props.getProperty("berth.occupancy.min.time", "2"));
                maxBerthTime = Integer.parseInt(props.getProperty("berth.occupancy.max.time", "5"));
                containerOperationTime = Integer.parseInt(props.getProperty("container.operation.time", "1"));
                
                loaded = true;
            }
        } finally {
            lock.unlock();
        }
    }

    // Getters
    public int getWarehouseCapacity() { return warehouseCapacity; }
    public int getBerthCount() { return berthCount; }
    public int getShipCount() { return shipCount; }
    public int getMinContainers() { return minContainers; }
    public int getMaxContainers() { return maxContainers; }
    public int getMinCapacity() { return minCapacity; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getMinBerthTime() { return minBerthTime; }
    public int getMaxBerthTime() { return maxBerthTime; }
    public int getContainerOperationTime() { return containerOperationTime; }
}
