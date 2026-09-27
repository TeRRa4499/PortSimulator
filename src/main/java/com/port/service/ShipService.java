package com.port.service;

import com.port.model.Berth;
import com.port.model.Ship;
import com.port.model.Warehouse;
import com.port.singleton.PortRegistry;
import com.port.state.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Service that handles ship operations as a Callable task.
 * Each ship runs in its own thread and goes through all states.
 */
public class ShipService implements Callable<Void> {
    private static final Logger logger = LogManager.getLogger(ShipService.class);
    private static final Random random = new Random();
    
    private final Ship ship;
    private final Warehouse warehouse;
    private final int minBerthTime;
    private final int maxBerthTime;
    private final int containerOperationTime;

    public ShipService(Ship ship, Warehouse warehouse, int minBerthTime, 
                       int maxBerthTime, int containerOperationTime) {
        this.ship = ship;
        this.warehouse = warehouse;
        this.minBerthTime = minBerthTime;
        this.maxBerthTime = maxBerthTime;
        this.containerOperationTime = containerOperationTime;
    }

    @Override
    public Void call() throws Exception {
        Berth berth = null;
        try {
            logger.info("{} is waiting to dock", ship);
            
            // Wait state - get a berth
            berth = PortRegistry.getInstance().getAvailableBerth();
            berth.setShip(ship);
            ship.setAssignedBerth(berth);
            
            // Transition to Docking
            ship.getStateContext().transitionTo(new DockingState());
            logger.info("{} is docking at Berth-{}", ship, berth.getId());
            TimeUnit.SECONDS.sleep(random.nextInt(maxBerthTime - minBerthTime + 1) + minBerthTime);
            
            // Transition to Loading
            ship.getStateContext().transitionTo(new LoadingState());
            logger.info("{} started loading/unloading at Berth-{}", ship, berth.getId());
            
            // Simulate container operations
            performContainerOperations(berth);
            
            TimeUnit.SECONDS.sleep(containerOperationTime);
            
            // Transition to Departing
            ship.getStateContext().transitionTo(new DepartingState());
            logger.info("{} is departing from Berth-{}", ship, berth.getId());
            TimeUnit.SECONDS.sleep(1);
            
            logger.info("{} has left the port", ship);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("{} was interrupted", ship, e);
        } catch (Exception e) {
            logger.error("{} encountered an error", ship, e);
        } finally {
            // Guarantee berth release in all cases
            if (berth != null) {
                PortRegistry.getInstance().releaseBerth(berth);
                ship.setAssignedBerth(null);
                logger.debug("{} released berth {}", ship, berth.getId());
            }
        }
        
        return null;
    }

    private void performContainerOperations(Berth berth) throws InterruptedException {
        // First unload containers from ship to warehouse
        int containersToUnload = random.nextInt(ship.getContainers() / 2 + 1);
        if (containersToUnload > 0) {
            int actualUnload = Math.min(containersToUnload, ship.getContainers());
            warehouse.addContainersBlocking(actualUnload);
            ship.removeContainers(actualUnload);
            logger.info("{} unloaded {} containers to warehouse. Warehouse now has {} containers",
                    ship, actualUnload, warehouse.getCurrentContainers());
        }

        // Then load containers from warehouse to ship
        int containersToLoad = random.nextInt(5) + 1; // Load at least 1, max 5 containers
        int spaceOnShip = ship.getCapacity() - ship.getContainers();
        // сейчас если не хватает места на корабле, то не загружаем ИСПРАВИТЬ! например сгружаем скока есть места
        if (containersToLoad > 0 && spaceOnShip > 0) {
            int actualLoad = Math.min(containersToLoad, Math.min(spaceOnShip, warehouse.getCurrentContainers()));
            if (actualLoad > 0) {
                warehouse.removeContainersBlocking(actualLoad);
                ship.addContainers(actualLoad);
                logger.info("{} loaded {} containers from warehouse. Ship now has {} containers",
                        ship, actualLoad, ship.getContainers());
            }
        }
    }

    private void resumeLoadingIfWaiting(Berth berth) {
        if ("WAITING".equals(ship.getStateName())) {
            ship.getStateContext().transitionTo(new LoadingState());
            logger.info("{} resumed loading/unloading at Berth-{}", ship, berth.getId());
        }
    }
}
