package com.port.model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents the port warehouse with limited capacity for containers.
 * Thread-safe implementation using ReentrantLock and Condition.
 */
public class Warehouse {
    private final int capacity;
    private int currentContainers;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition hasSpace = lock.newCondition();
    private final Condition hasContainers = lock.newCondition();

    public Warehouse(int capacity) {
        this.capacity = capacity;
        this.currentContainers = 0;
    }

    public boolean hasSpaceFor(int count) {
        lock.lock();
        try {
            return currentContainers + count <= capacity;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasContainers(int count) {
        lock.lock();
        try {
            return currentContainers >= count;
        } finally {
            lock.unlock();
        }
    }

    public boolean addContainers(int count) {
        lock.lock();
        try {
            if (currentContainers + count <= capacity) {
                currentContainers += count;
                hasContainers.signalAll();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void addContainersBlocking(int count) throws InterruptedException {
        lock.lock();
        try {
            while (currentContainers + count > capacity) {
                hasSpace.await();
            }
            currentContainers += count;
            hasContainers.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean removeContainers(int count) {
        lock.lock();
        try {
            if (currentContainers >= count) {
                currentContainers -= count;
                hasSpace.signalAll();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void removeContainersBlocking(int count) throws InterruptedException {
        lock.lock();
        try {
            while (currentContainers < count) {
                hasContainers.await();
            }
            currentContainers -= count;
            hasSpace.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentContainers() {
        lock.lock();
        try {
            return currentContainers;
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSpace() {
        lock.lock();
        try {
            return capacity - currentContainers;
        } finally {
            lock.unlock();
        }
    }
}
