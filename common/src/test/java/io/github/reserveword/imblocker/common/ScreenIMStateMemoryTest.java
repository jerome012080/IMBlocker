package io.github.reserveword.imblocker.common;

/** Dependency-free regression scenarios; also runnable with javac/java. */
public final class ScreenIMStateMemoryTest {
	private static final class Chat {}
	private static final class Terminal {}
	private static final class Search {}

	private static final class InputMethod implements ScreenIMStateMemory.Backend {
		ScreenIMStateMemory.State state;

		@Override
		public ScreenIMStateMemory.State captureState() {
			return state;
		}

		@Override
		public void restoreState(ScreenIMStateMemory.State restored) {
			state = restored;
		}
	}

	public static void main(String[] args) {
		InputMethod input = new InputMethod();
		ScreenIMStateMemory memory = new ScreenIMStateMemory(input);
		check(!memory.focus(Chat.class, false, true), "new chat uses existing defaults");
		input.state = new ScreenIMStateMemory.State(true, 9, 16);
		memory.blur(true);
		check(!memory.focus(Terminal.class, false, true), "new terminal uses existing defaults");
		input.state = new ScreenIMStateMemory.State(false, 8, 32);
		memory.blur(true);
		check(memory.focus(new Chat().getClass(), false, true), "recreated chat restores");
		state(input, true, 9, 16, "chat keeps full conversion and sentence bits");
		check(memory.command(false, true), "normal chat initialization keeps saved preference");
		state(input, true, 9, 16, "primary English defaults do not override remembered chat");
		memory.blur(true);
		check(memory.focus(Terminal.class, false, true), "terminal restores independently");
		state(input, false, 8, 32, "closed IME is remembered");
		memory.blur(true);
		memory.focus(Chat.class, false, true);
		input.state = new ScreenIMStateMemory.State(true, 17, 64);
		check(!memory.command(true, true), "command caller applies English");
		input.state = new ScreenIMStateMemory.State(true, 0, 0);
		check(memory.command(false, true), "removing command restores underlying preference");
		state(input, true, 17, 64, "command does not replace chat preference");
		memory.command(true, true);
		input.state = new ScreenIMStateMemory.State(true, 0, 0);
		memory.blur(true);
		check(memory.focus(Chat.class, false, true), "closing command retains chat memory");
		state(input, true, 17, 64, "reopening normal chat restores pre-command state");
		memory.blur(true);
		check(!memory.focus(Chat.class, true, true), "opening slash chat overrides saved mode");
		input.state = new ScreenIMStateMemory.State(true, 0, 0);
		memory.blur(true);
		memory.focus(Chat.class, false, true);
		state(input, true, 17, 64, "slash-open does not poison normal chat");
		input.state = null;
		memory.blur(true);
		memory.focus(Chat.class, false, true);
		state(input, true, 17, 64, "unavailable native context retains previous snapshot");
		memory.blur(true);
		check(!memory.focus(Search.class, false, true), "unseen screen remains unchanged");
		input.state = new ScreenIMStateMemory.State(true, 3, 2);
		check(!memory.focus(Search.class, false, false), "disabled memory uses defaults");
		memory.blur(false);
		check(!memory.focus(Chat.class, false, true), "disabling clears old history");
		input.state = new ScreenIMStateMemory.State(true, 5, 4);
		memory.blur(true);
		check(!memory.command(true, false), "disabled command path uses existing behavior");
		check(!memory.focus(Chat.class, false, true), "disabled command clears history");
		memory.focus(null, false, true);
		input.state = new ScreenIMStateMemory.State(true, 7, 6);
		memory.blur(true);
		check(!memory.focus(null, false, true), "gameplay without a screen is not remembered");
		System.out.println("PASS: screen isolation, exact modes, command override, fresh/disabled/null contexts");
	}

	private static void state(InputMethod input, boolean open, int conversion, int sentence, String message) {
		check(input.state != null && input.state.open == open
				&& input.state.conversion == conversion && input.state.sentence == sentence, message);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
