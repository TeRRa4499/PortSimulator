package com.port.state;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Context class for the Ship State pattern.
 * Manages the current state of a ship and handles state transitions.
 */
public class ShipContext {
    private ShipState currentState;
    private final ReentrantLock stateLock = new ReentrantLock();

    public ShipContext(ShipState initialState) {
        this.currentState = initialState;
    }

    public void transitionTo(ShipState newState) {
        stateLock.lock();
        try {
            if (currentState != null) {
                currentState.onExit(this);
            }
            currentState = newState;
            if (currentState != null) {
                currentState.onEnter(this);
            }
        } finally {
            stateLock.unlock();
        }
    }

    public ShipState getCurrentState() {
        stateLock.lock();
        try {
            return currentState;
        } finally {
            stateLock.unlock();
        }
    }

    public String getStateName() {
        stateLock.lock();
        try {
            return currentState != null ? currentState.getName() : "Unknown";
        } finally {
            stateLock.unlock();
        }
    }
}
