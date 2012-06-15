package me.adpuckey.plugins.aprilonirc.irc;

/**
 * This enum contains all of the supported colors.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public enum IrcColor {
	WHITE("\u000300", "f"), BLACK("\u000301", "0"), DARK_BLUE("\u000302", "1"), 
        DARK_GREEN("\u000303", "2"), RED("\u000304", "4"), BROWN("\u000305", "8"),
        PURPLE("\u000306", "5"), OLIVE("\u000307", "6"), YELLOW("\u000308", "e"),
        GREEN("\u000309", "a"), TEAL("\u000310", "3"), CYAN("\u000311", "b"),
        BLUE("\u000312", "9"), MAGENTA("\u000313", "d"), DARK_GRAY("\u000314","7"),
        LIGHT_GRAY("\u000315", "7"), NORMAL("\u000f", "f"), OTHER_RED("\u000304", "c"); //Color bug, had to be fixed by adding OTHER_RED

	/**
	 * Colors in minecraft and IRC.
	 * 
	 * @param IRCColor
	 *            The color code in IRC.
	 * @param MinecraftColor
	 *            The color code in Minecraft.
	 */
	IrcColor(String IRCColor, String MinecraftColor) {
		this.IRCColor = IRCColor;
		this.MinecraftColor = MinecraftColor;
	}

	/**
	 * Fetches the color in minecraft.
	 * 
	 * @return The minecraft color code.
	 */
	public String getMinecraftColor() {
		return "§" + MinecraftColor;
	}

	/**
	 * Fetches the color in IRC.
	 * 
	 * @return The IRC color code.
	 */
	public String getIRCColor() {
		return IRCColor;
	}

	/**
	 * Creates a formatted message with proper colors.
	 * 
	 * @param message
	 *            The inital message to format.
	 * @return The formatted message.
	 */
	public static String formatIRCMessage(final String message) {
		String msg = message;
		if (true) { //TODO:  Implement option for enable/disable colors
			for (IrcColor c : values()) {
				if (msg.contains(c.getIRCColor())) {
					msg = msg.replace(c.getIRCColor(), c.getMinecraftColor());
				}
			}
		} else {
			for (IrcColor c : values()) {
				if (msg.contains(c.getIRCColor())) {
					msg = msg.replace(c.getIRCColor(), "");
				}
			}
		}
		return msg;
	}

	/**
	 * Creates a formatted message with proper colors.
	 * 
	 * @param message
	 *            The inital message to format.
	 * @return The formatted message.
	 */
	public static String formatMCMessage(final String message) {
		String msg = message;
		if (true) { //TODO:  Implement option to enable/disable colros
			for (IrcColor c : values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), c.getIRCColor());
				}
			}
		} else {
			for (IrcColor c : values()) {
				if (msg.contains(c.getMinecraftColor())) {
					msg = msg.replace(c.getMinecraftColor(), "");
				}
			}
		}
		return msg;
	}

	private final String IRCColor;

	private final String MinecraftColor;

}