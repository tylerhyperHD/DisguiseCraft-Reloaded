package pgDev.bukkit.DisguiseCraft.listeners.optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class PlayerAnimationListener implements Listener {
	final DisguiseCraft plugin;
	
	public PlayerAnimationListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			if (plugin.disguiseDB.containsKey(event.getPlayer().getUniqueId())) {
				Disguise disguise = plugin.disguiseDB.get(event.getPlayer().getUniqueId());
				if (disguise.type.isPlayer() || disguise.type == DisguiseType.IronGolem ||
						disguise.type == DisguiseType.Zombie || disguise.type == DisguiseType.PigZombie || disguise.type == DisguiseType.Skeleton ||
						disguise.type == DisguiseType.Giant) {
					plugin.sendPacketToWorld(event.getPlayer().getWorld(), disguise.packetGenerator.getAnimationPacket(0));
				}
			}
		}
	}
}
