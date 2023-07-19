package tk.fancystore.noisier.bukkit.libraries.music.model;

/**
 * @author xxmicloxx, koca2000
 */
public class Note {

	private byte instrument;
	private byte key;
	private byte velocity;
	private int panning;
	private short pitch;

	public Note(byte instrument, byte key) {
		this(instrument, key, (byte) 100, (byte) 100, (short) 0);
	}

	public Note(byte instrument, byte key, byte velocity, int panning, short pitch) {
		this.instrument = instrument;
		this.key = key;
		this.velocity = velocity;
		this.panning = panning;
		this.pitch = pitch;
	}

	public byte getInstrument() {
		return instrument;
	}

	public void setInstrument(byte instrument) {
		this.instrument = instrument;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}

	public short getPitch() {
		return pitch;
	}

	public void setPitch(short pitch) {
		this.pitch = pitch;
	}

	public byte getVelocity() {
		return velocity;
	}

	public void setVelocity(byte velocity) {
		if (velocity < 0) velocity = 0;
		if (velocity > 100) velocity = 100;

		this.velocity = velocity;
	}

	public int getPanning() {
		return panning;
	}

	public void setPanning(int panning) {
		this.panning = panning;
	}
}
