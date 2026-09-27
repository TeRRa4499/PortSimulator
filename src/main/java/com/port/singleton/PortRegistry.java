package com.port.singleton;

import com.port.model.Berth;
import com.port.model.Warehouse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe Singleton using double-checked locking pattern.
 * Manages shared port resources (warehouse and berths).
 */
public class PortRegistry {
    private static volatile PortRegistry instance;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition berthAvailable = lock.newCondition();
    
    private Warehouse warehouse;
    private final List<Berth> berths = new ArrayList<>();
    
    private boolean initialized = false;

    private PortRegistry() {}

    public static PortRegistry getInstance() {
        if (instance == null) {
            synchronized (PortRegistry.class) {
                if (instance == null) {
                    instance = new PortRegistry();
                }
            }
        }
        return instance;
    }

    public void initialize(Warehouse warehouse, List<Berth> berths) {
        lock.lock();
        try {
            if (!initialized) {
                this.warehouse = warehouse;
                this.berths.addAll(berths);
                initialized = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public Warehouse getWarehouse() {
        lock.lock();
        try {
            return warehouse;
        } finally {
            lock.unlock();
        }
    }

    public List<Berth> getBerths() {
        lock.lock();
        try {
            return new ArrayList<>(berths);
        } finally {
            lock.unlock();
        }
    }

    public Berth getAvailableBerth() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                for (Berth berth : berths) {
                    if (berth.tryAcquire()) {
                        return berth;
                    }
                }
                berthAvailable.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseBerth(Berth berth) {
        lock.lock();
        try {
            berth.release();
            berthAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
