package com.yurisuika.compost;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Compost implements ModInitializer {

	public static final String MOD_ID = "compost";

	public static final Logger LOGGER = LogManager.getLogger("Compost");

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Compost!");
	}

}
