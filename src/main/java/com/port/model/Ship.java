package com.port.model;

import com.port.state.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a ship that comes to the port for loading/unloading containers.
 */
public class Ship {
    private final int id;
    private final int capacity;
    private int containers;
    private final ReentrantLock lock = new ReentrantLock();
    private final ShipContext stateContext;
    private Berth assignedBerth;

    public Ship(int id, int capacity, int initialContainers) {
        this.id = id;
        this.capacity = capacity;
        this.containers = initialContainers;
        this.stateContext = new ShipContext(new WaitingState());
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getContainers() {
        lock.lock();
        try {
            return containers;
        } finally {
            lock.unlock();
        }
    }

    public void setContainers(int count) {
        lock.lock();
        try {
            if (count >= 0 && count <= capacity) {
                this.containers = count;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean addContainers(int count) {
        lock.lock();
        try {
            if (containers + count <= capacity) {
                containers += count;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeContainers(int count) {
        lock.lock();
        try {
            if (containers >= count) {
                containers -= count;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public ShipContext getStateContext() {
        return stateContext;
    }

    public String getStateName() {
        return stateContext.getStateName();
    }

    public void setAssignedBerth(Berth berth) {
        lock.lock();
        try {
            this.assignedBerth = berth;
        } finally {
            lock.unlock();
        }
    }

    public Berth getAssignedBerth() {
        lock.lock();
        try {
            return assignedBerth;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Ship-" + id + " [containers: " + containers + "/" + capacity + ", state: " + getStateName() + "]";
    }
}
