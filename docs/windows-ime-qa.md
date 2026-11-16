# Owner-run Windows Korean IME checklist

Target: Sunlit Valley 4.1.5A-hg10, Minecraft 1.20.1, Forge 47.4.0.
Replace the original IMBlocker jar with the fork jar in a separate test instance.
Keep only one IMBlocker jar. Do not change the published HomeGate pack.

This lane runs on Linux. Build and state-machine checks do not prove Microsoft
Korean IME behavior; the following native checks remain for the owner to run.

Use Microsoft Korean IME, `CONVERSION_STATUS`, ConversionStatus API enabled,
and `rememberScreenState=true` (the default).

1. Press `T`, switch to Korean with the Hangul key, and type `gksrmf`.
   PASS: chat shows Korean. Press Escape without sending.
2. Press `E`, click the inventory search box, switch to English, and type `stone`.
   Press Escape. Press `T`, type `gksrmf`, then Escape. Repeat the inventory search.
   PASS: chat retains Korean while inventory search retains English.
3. Open an AE2 terminal, focus search, choose English, close it, and reopen chat.
   Reopen the terminal after chat. PASS: chat remains Korean and terminal remains
   English. Open a second terminal of the same screen class: it shares that class's
   preference.
4. In Korean chat, type `/help`, then erase the slash and command.
   PASS: command text is English; ordinary chat returns to the saved Korean mode.
   Open chat directly with `/`, close it, then reopen with `T`.
   PASS: the slash-open command does not overwrite the normal chat preference.
5. Repeat with chat deliberately set to English before entering a command.
   PASS: removing the command keeps the underlying English preference.
6. Close the IME with its open/close control while a text field is focused,
   leave that screen, then return. PASS: its closed state is restored, independently
   of another screen's open IME. Where the IME offers additional conversion modes,
   confirm those settings also survive the round trip.
7. Alt-tab away and back while chat is focused. Switch rapidly between chat,
   inventory search, and terminal, including within the first 60 ms after focus.
   PASS: no delayed command or default conversion lands on the next screen.
8. Open a screen class never used in this game session.
   PASS: its initial mode follows the existing primary English/default behavior.
9. Set `rememberScreenState=false`, save the config, and repeat the switches.
   PASS: original default behavior returns. Re-enable it.
   PASS: the old session history is gone; each screen starts from its defaults.
10. Disable the ConversionStatus API, or select `RIME`/`DISABLE_IM`.
    PASS: those existing compatibility modes retain their previous behavior.

Record Windows version, IME version, jar SHA-256, and PASS/FAIL for every row.
If an IME rejects IMM conversion changes, record the observed behavior rather
than marking it passed from the logic tests.
