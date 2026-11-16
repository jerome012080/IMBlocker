package io.github.reserveword.imblocker.common.gui;

import io.github.reserveword.imblocker.common.IMBlockerConfig;
import io.github.reserveword.imblocker.common.IMBlockerCore;
import io.github.reserveword.imblocker.common.IMManager;

public abstract class MinecraftScreenMonitor {
	private static Object currentScreen;
	private static boolean isCharSimulationPreferred;
	
	public static void onScreenChanged(Object screen) {
		// FTB wrappers may retain their widget focus across a screen replacement.
		IMManager.onFocusLost();
		currentScreen = screen;
		
		if(!IMBlockerCore.isFTBScreen(screen)) {
			FocusContainer.MINECRAFT.clearFocus();
		}
		
		IMBlockerConfig config = IMBlockerConfig.INSTANCE;
		if(config.isScreenRecoveringEnabled() && screen != null) {
			config.recoverScreen(screen.getClass().getName());
		}
		FocusContainer.MINECRAFT.setPreferredState(config.isScreenInWhitelist(screen));
		isCharSimulationPreferred = config.isCharSimulationPreferred(screen);
	}
	
	public static Object getCurrentScreen() {
		return currentScreen;
	}
	
	public static boolean isCharSimulationPreferred() {
		return isCharSimulationPreferred;
	}
}
