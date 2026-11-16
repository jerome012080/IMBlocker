package io.github.reserveword.imblocker.common;

import java.util.HashMap;
import java.util.Map;

/** Screen preferences, owned by the window thread. Command English is temporary. */
final class ScreenIMStateMemory {
	static final class State {
		final boolean open;
		final int conversion;
		final int sentence;

		State(boolean open, int conversion, int sentence) {
			this.open = open;
			this.conversion = conversion;
			this.sentence = sentence;
		}
	}

	interface Backend {
		default State captureState() { return null; }
		default void restoreState(State state) {}
	}

	private final Backend backend;
	private final Map<Class<?>, State> screens = new HashMap<>();
	private Class<?> activeScreen;
	private boolean forcedEnglish;

	ScreenIMStateMemory(Backend backend) {
		this.backend = backend;
	}

	boolean focus(Class<?> screen, boolean forceEnglish, boolean enabled) {
		if (!enabled) {
			clear();
			return false;
		}
		activeScreen = screen;
		forcedEnglish = forceEnglish;
		return !forceEnglish && restore();
	}

	void blur(boolean enabled) {
		if (enabled) {
			save();
		} else {
			screens.clear();
		}
		activeScreen = null;
		forcedEnglish = false;
	}

	boolean command(boolean forceEnglish, boolean enabled) {
		if (!enabled) {
			clear();
			return false;
		}
		if (forceEnglish == forcedEnglish) {
			return !forceEnglish && restore();
		}
		if (forceEnglish) {
			save();
		}
		forcedEnglish = forceEnglish;
		return !forceEnglish && restore();
	}

	private void save() {
		if (activeScreen != null && !forcedEnglish) {
			State state = backend.captureState();
			if (state != null) {
				screens.put(activeScreen, state);
			}
		}
	}

	private boolean restore() {
		State state = screens.get(activeScreen);
		if (state == null) {
			return false;
		}
		backend.restoreState(state);
		return true;
	}

	private void clear() {
		screens.clear();
		activeScreen = null;
		forcedEnglish = false;
	}
}
