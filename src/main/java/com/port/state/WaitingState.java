package com.port.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * State representing a ship waiting to dock at a berth.
 */
public class WaitingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(WaitingState.class);

    @Override
    public String getName() {
        return "WAITING";
    }

    @Override
    public void onEnter(ShipContext context) {
        logger.info("Ship entered WAITING state");
    }

    @Override
    public void onExit(ShipContext context) {
        logger.info("Ship exited WAITING state");
    }
}
