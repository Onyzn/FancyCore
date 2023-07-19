package tk.fancystore.noisier.bukkit.libraries.music.model;

import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xxmicloxx, koca2000
 */
public enum NoteSound {
	NOTE_PIANO("NOTE_PIANO", "BLOCK_NOTE_HARP", "BLOCK_NOTE_BLOCK_HARP"),
	NOTE_BASS("NOTE_BASS", "BLOCK_NOTE_BASS", "BLOCK_NOTE_BLOCK_BASS"),
	NOTE_BASS_DRUM("NOTE_BASS_DRUM", "BLOCK_NOTE_BASEDRUM", "BLOCK_NOTE_BLOCK_BASEDRUM"),
	NOTE_SNARE_DRUM("NOTE_SNARE_DRUM", "BLOCK_NOTE_SNARE", "BLOCK_NOTE_BLOCK_SNARE"),
	NOTE_STICKS("NOTE_STICKS", "BLOCK_NOTE_HAT", "BLOCK_NOTE_BLOCK_HAT"),
	NOTE_BASS_GUITAR("NOTE_BASS_GUITAR", "BLOCK_NOTE_GUITAR", "BLOCK_NOTE_BLOCK_GUITAR"),
	NOTE_FLUTE("NOTE_FLUTE", "BLOCK_NOTE_FLUTE", "BLOCK_NOTE_BLOCK_FLUTE"),
	NOTE_BELL("NOTE_BELL", "BLOCK_NOTE_BELL", "BLOCK_NOTE_BLOCK_BELL"),
	NOTE_CHIME("NOTE_CHIME", "BLOCK_NOTE_CHIME", "BLOCK_NOTE_BLOCK_CHIME"),
	NOTE_XYLOPHONE("NOTE_XYLOPHONE", "BLOCK_NOTE_XYLOPHONE", "BLOCK_NOTE_BLOCK_XYLOPHONE"),
	NOTE_PLING("NOTE_PLING", "BLOCK_NOTE_PLING", "BLOCK_NOTE_BLOCK_PLING"),
	NOTE_IRON_XYLOPHONE("BLOCK_NOTE_BLOCK_IRON_XYLOPHONE"),
	NOTE_COW_BELL("BLOCK_NOTE_BLOCK_COW_BELL"),
	NOTE_DIDGERIDOO("BLOCK_NOTE_BLOCK_DIDGERIDOO"),
	NOTE_BIT("BLOCK_NOTE_BLOCK_BIT"),
	NOTE_BANJO("BLOCK_NOTE_BLOCK_BANJO");

	private final String[] versionDependentNames;
	private Sound cached = null;
	private static final Map<String, Sound> cachedSoundMap = new HashMap<>();

	NoteSound(String... versionDependentNames) {
		this.versionDependentNames = versionDependentNames;
	}

	public static Sound getFromBukkitName(String bukkitSoundName) {
		org.bukkit.Sound sound = cachedSoundMap.get(bukkitSoundName.toUpperCase());
		if (sound != null)
			return sound;

		return Sound.valueOf(bukkitSoundName);
	}

	private Sound getSound() {
		if (cached != null) return cached;
		for (String name : versionDependentNames) {
			try {
				return cached = Sound.valueOf(name);
			} catch (IllegalArgumentException ignore2) {
				// try next
			}
		}
		return null;
	}

	public Sound bukkitSound() {
		if (getSound() != null) {
			return getSound();
		}
		throw new IllegalArgumentException("Found no valid sound name for " + this.name());
	}

	static {
		// Cache sound access.
		for (NoteSound noteSound : values())
			for (String soundName : noteSound.versionDependentNames)
				cachedSoundMap.put(soundName.toUpperCase(), noteSound.getSound());
	}
}