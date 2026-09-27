package com.port.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * State representing a ship departing from the port.
 */
public class DepartingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(DepartingState.class);

    @Override
    public String getName() {
        return "DEPARTING";
    }

    @Override
    public void onEnter(ShipContext context) {
        logger.info("Ship entered DEPARTING state");
    }

    @Override
    public void onExit(ShipContext context) {
        logger.info("Ship exited DEPARTING state");
    }
}
