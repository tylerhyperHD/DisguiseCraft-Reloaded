package pgDev.bukkit.DisguiseCraft.listeners.optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

public class EntityDeathListener implements Listener {
	final DisguiseCraft plugin;
	
	public EntityDeathListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
				// Send death packets
				plugin.sendPacketToWorld(player.getWorld(), plugin.disguiseDB.get(player.getUniqueId()).packetGenerator.getStatusPacket(3));
			}
		}
	}
}
