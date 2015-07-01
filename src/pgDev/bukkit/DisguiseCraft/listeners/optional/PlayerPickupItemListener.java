package pgDev.bukkit.DisguiseCraft.listeners.optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

public class PlayerPickupItemListener implements Listener {
	final DisguiseCraft plugin;
	
	public PlayerPickupItemListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
			if (!plugin.disguiseDB.get(player.getUniqueId()).type.isObject()) {
				plugin.sendPacketToWorld(player.getWorld(), plugin.disguiseDB.get(player.getUniqueId()).packetGenerator.getPickupPacket(event.getItem().getEntityId()));
			}
		}
	}
}
