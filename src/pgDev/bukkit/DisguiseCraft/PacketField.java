package pgDev.bukkit.DisguiseCraft;

public class PacketField {
	public String field;
	public int superLocation = 0;
	public Object value;
	public boolean accessible;
	
	public PacketField(String field, Object value, boolean accessible) {
		this.field = field;
		this.value = value;
		this.accessible = accessible;
	}
	
	public PacketField(String field, Object value) {
		this(field, value, true);
	}
	
	public PacketField setSuper(int up) {
		superLocation = up;
		return this;
	}
}
