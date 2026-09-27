package com.port.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * State representing a ship docking at a berth.
 */
public class DockingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(DockingState.class);

    @Override
    public String getName() {
        return "DOCKING";
    }

    @Override
    public void onEnter(ShipContext context) {
        logger.info("Ship entered DOCKING state");
    }

    @Override
    public void onExit(ShipContext context) {
        logger.info("Ship exited DOCKING state");
    }
}
