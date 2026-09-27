package com.port.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * State representing a ship being loaded/unloaded at a berth.
 */
public class LoadingState implements ShipState {
    private static final Logger logger = LogManager.getLogger(LoadingState.class);

    @Override
    public String getName() {
        return "LOADING";
    }

    @Override
    public void onEnter(ShipContext context) {
        logger.info("Ship entered LOADING state");
    }

    @Override
    public void onExit(ShipContext context) {
        logger.info("Ship exited LOADING state");
    }
}
