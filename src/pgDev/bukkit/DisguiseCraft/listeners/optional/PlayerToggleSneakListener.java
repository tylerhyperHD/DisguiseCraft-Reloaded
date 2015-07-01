package pgDev.bukkit.DisguiseCraft.listeners.optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;

public class PlayerToggleSneakListener implements Listener {
	final DisguiseCraft plugin;
	
	public PlayerToggleSneakListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		if (plugin.disguiseDB.containsKey(event.getPlayer().getUniqueId())) {
			Disguise disguise = plugin.disguiseDB.get(event.getPlayer().getUniqueId());
			if (disguise.type.isHumanoid()) {
				disguise.setCrouch(event.isSneaking());
				plugin.sendPacketToWorld(event.getPlayer().getWorld(), disguise.packetGenerator.getEntityMetadataPacket());
			}
		}
	}
}
