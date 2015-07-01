package pgDev.bukkit.DisguiseCraft.listeners.optional;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.listeners.ArmorUpdater;

public class InventoryClickListener implements Listener {
	final DisguiseCraft plugin;
	
	public InventoryClickListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryChange(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
				Disguise disguise = plugin.disguiseDB.get(player.getUniqueId());
				if (!disguise.data.contains("noarmor")) {
					plugin.getServer().getScheduler().runTask(plugin, new ArmorUpdater(plugin, player, disguise));
				}
			}
		}
	}
}
