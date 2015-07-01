package pgDev.bukkit.DisguiseCraft.listeners.movement;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;

public class DCPlayerMoveListener implements Listener {
	final DisguiseCraft plugin;
	
	public DCPlayerMoveListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// Track player movements in order to synchronize their disguise
		Disguise disguise = plugin.disguiseDB.get(event.getPlayer().getUniqueId());
		if (disguise != null) {
			if (!disguise.data.contains("nomove")) {
				plugin.sendMovement(event.getPlayer(), null, event.getPlayer().getVelocity(), event.getTo());
			}
		}
	}
}
