package pgDev.bukkit.DisguiseCraft.listeners.attack;

import net.minecraft.server.v1_8_R3.EntityPlayer;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerAttack {
	public Player attacker;
	public Player victim;
	
	public PlayerAttack(Player attacker, Player victim) {
		this.attacker = attacker;
		this.victim = victim;
	}
	
	public EntityPlayer attacker() {
		return ((CraftPlayer) attacker).getHandle();
	}
	
	public EntityPlayer victim() {
		return ((CraftPlayer) victim).getHandle();
	}
}
