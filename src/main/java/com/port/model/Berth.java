package com.port.model;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a berth (причал) where ships can dock.
 * Only one ship can occupy a berth at a time.
 */
public class Berth {
    private final int id;
    private final AtomicBoolean occupied = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();
    private Ship currentShip;

    public Berth(int id) {
        this.id = id;
    }

    public boolean tryAcquire() {
        return occupied.compareAndSet(false, true);
    }

    public void release() {
        lock.lock();
        try {
            currentShip = null;
            occupied.set(false);
        } finally {
            lock.unlock();
        }
    }

    public void setShip(Ship ship) {
        lock.lock();
        try {
            this.currentShip = ship;
        } finally {
            lock.unlock();
        }
    }

    public Ship getCurrentShip() {
        lock.lock();
        try {
            return currentShip;
        } finally {
            lock.unlock();
        }
    }

    public boolean isOccupied() {
        return occupied.get();
    }

    public int getId() {
        return id;
    }
}
