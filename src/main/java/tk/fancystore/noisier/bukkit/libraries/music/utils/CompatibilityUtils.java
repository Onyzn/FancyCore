package tk.fancystore.noisier.bukkit.libraries.music.utils;

import tk.fancystore.noisier.bukkit.libraries.music.model.CustomInstrument;
import org.bukkit.Bukkit;

import java.util.ArrayList;

/**
 * @author xxmicloxx, koca2000
 */
public class CompatibilityUtils {

	public static final String OBC_DIR = Bukkit.getServer().getClass().getPackage().getName();
	public static final String NMS_DIR = OBC_DIR.replaceFirst("org.bukkit.craftbukkit", "net.minecraft.server");

	private static float serverVersion = -1;

	public static Class<?> getMinecraftClass(String name) {
		try {
			return Class.forName(NMS_DIR + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getCraftBukkitClass(String name) {
		try {
			return Class.forName(OBC_DIR + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isPost1_12() {
		return getServerVersion() >= 0.0112f;
	}

	protected static boolean isSoundCategoryCompatible() {
		return getServerVersion() >= 0.0111f;
	}

	public static ArrayList<CustomInstrument> get1_12Instruments(){
		return getVersionCustomInstruments(0.0112f);
	}

	public static ArrayList<CustomInstrument> getVersionCustomInstruments(float serverVersion){
		ArrayList<CustomInstrument> instruments = new ArrayList<>();
		if (serverVersion == 0.0112f){
			instruments.add(new CustomInstrument((byte) 0, "Guitar", "block.note_block.guitar.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Flute", "block.note_block.flute.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Bell", "block.note_block.bell.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Chime", "block.note_block.icechime.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Xylophone", "block.note_block.xylobone.ogg"));
			return instruments;
		}

		if (serverVersion == 0.0114f){
			instruments.add(new CustomInstrument((byte) 0, "Iron Xylophone", "block.note_block.iron_xylophone.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Cow Bell", "block.note_block.cow_bell.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Didgeridoo", "block.note_block.didgeridoo.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Bit", "block.note_block.bit.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Banjo", "block.note_block.banjo.ogg"));
			instruments.add(new CustomInstrument((byte) 0, "Pling", "block.note_block.pling.ogg"));
			return instruments;
		}
		return instruments;
	}

	public static ArrayList<CustomInstrument> getVersionCustomInstrumentsForSong(int firstCustomInstrumentIndex){
		ArrayList<CustomInstrument> instruments = new ArrayList<>();

		if (getServerVersion() < 0.0112f){
			if (firstCustomInstrumentIndex == 10) {
				instruments.addAll(getVersionCustomInstruments(0.0112f));
			} else if (firstCustomInstrumentIndex == 16){
				instruments.addAll(getVersionCustomInstruments(0.0112f));
				instruments.addAll(getVersionCustomInstruments(0.0114f));
			}
		} else if (getServerVersion() < 0.0114f){
			if (firstCustomInstrumentIndex == 16){
				instruments.addAll(getVersionCustomInstruments(0.0114f));
			}
		}

		return instruments;
	}

	public static float getServerVersion(){
		if (serverVersion != -1){
			return serverVersion;
		}

		String versionInfo = Bukkit.getServer().getVersion();
		int start = versionInfo.lastIndexOf('(');
		int end = versionInfo.lastIndexOf(')');

		String[] versionParts = versionInfo.substring(start + 5, end).split("\\.");

		StringBuilder versionString = new StringBuilder("0.");
		for (String part : versionParts){
			if (part.length() == 1){
				versionString.append("0");
			}

			versionString.append(part);
		}
		serverVersion = Float.parseFloat(versionString.toString());
		return serverVersion;
	}
}
