package tk.fancystore.noisier.bukkit.libraries.music.model;

import java.io.File;
import java.util.HashMap;

/**
 * @author xxmicloxx, koca2000
 */
public class Song {

	private final HashMap<Integer, Layer> layerHashMap;

	private final short songHeight;
	private final short length;
	private final String title;
	private final File path;
	private final String author;
	private final String description;
	private final float speed;
	private final float delay;
	private final CustomInstrument[] customInstruments;
	private final int firstCustomInstrumentIndex;

	public Song(float speed, HashMap<Integer, Layer> layerHashMap, short songHeight, final short length, String title, String author, String description, File path, int firstCustomInstrumentIndex, CustomInstrument[] customInstruments) {
		this.speed = speed;
		this.delay = 20 / speed;
		this.layerHashMap = layerHashMap;
		this.songHeight = songHeight;
		this.length = length;
		this.title = title;
		this.author = author;
		this.description = description;
		this.path = path;
		this.firstCustomInstrumentIndex = firstCustomInstrumentIndex;
		this.customInstruments = customInstruments;
	}

	public HashMap<Integer, Layer> getLayerHashMap() {
		return layerHashMap;
	}

	public short getSongHeight() {
		return songHeight;
	}

	public short getLength() {
		return length;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public File getPath() {
		return path;
	}

	public String getDescription() {
		return description;
	}

	public float getSpeed() {
		return speed;
	}

	public float getDelay() {
		return delay;
	}

	public CustomInstrument[] getCustomInstruments() {
		return customInstruments;
	}

	public int getFirstCustomInstrumentIndex() {
		return firstCustomInstrumentIndex;
	}
}
