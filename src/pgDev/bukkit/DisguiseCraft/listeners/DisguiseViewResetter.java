package pgDev.bukkit.DisguiseCraft.listeners;

import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

public class DisguiseViewResetter implements Runnable {
	final DisguiseCraft plugin;
	
	Player player;
	
	public DisguiseViewResetter(DisguiseCraft plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void run() {
		if (player.isOnline()) {
			plugin.resetWorldDisguises(player);
		}
	}
}
