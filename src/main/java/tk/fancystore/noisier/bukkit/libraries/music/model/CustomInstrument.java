package tk.fancystore.noisier.bukkit.libraries.music.model;

/**
 * @author xxmicloxx, koca2000
 */
public class CustomInstrument {
	
	private final byte index;
	private final String name;
	private final String soundFileName;
	private org.bukkit.Sound sound;

	public CustomInstrument(byte index, String name, String soundFileName) {
		this.index = index;
		this.name = name;
		this.soundFileName = soundFileName.replaceAll(".ogg", "");
		if (this.soundFileName.equalsIgnoreCase("pling")){
			this.sound = NoteSound.NOTE_PLING.bukkitSound();
		}
	}

	public byte getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getSoundFileName() {
		return soundFileName;
	}

	public org.bukkit.Sound getSound() {
		return sound;
	}
}
