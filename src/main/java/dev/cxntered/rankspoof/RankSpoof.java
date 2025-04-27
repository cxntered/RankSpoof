package dev.cxntered.rankspoof;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RankSpoof implements ModInitializer {
	public static final String MOD_ID = "rankspoof";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("oh hi 🤎");
	}
}