package com.port.state;

/**
 * Interface representing the state of a ship in the port simulation.
 * Implements the State design pattern.
 */
public interface ShipState {
    String getName();
    void onEnter(ShipContext context);
    void onExit(ShipContext context);
}
