package pgDev.bukkit.DisguiseCraft.update;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

public class DCUpdateNotifier  implements Runnable {
	final DisguiseCraft plugin;
	CommandSender toNotify;
	
	boolean notifyNone;
	
	public DCUpdateNotifier(final DisguiseCraft plugin, CommandSender player, boolean notifyNone) {
		this.plugin = plugin;
		this.toNotify = player;
		this.notifyNone = notifyNone;
	}
	
	public DCUpdateNotifier(final DisguiseCraft plugin, CommandSender player) {
		this(plugin, player, false);
	}
	
	@Override
	public void run() {
		boolean success = true;
		String latestVersion = null;
		try {
			latestVersion = DCUpdateChecker.getLatestVersion();
			
			try {
				if (DCUpdateChecker.isUpToDate(DisguiseCraft.pdfFile.getVersion(), latestVersion)) {
					// Up to date
					if (notifyNone) {
						notify("There are no new DisguiseCraft updates", false);
					}
				} else {
					// Out of date
					notify("There is a new update for DisguiseCraft available: " + latestVersion, false);
				}
			} catch (NumberFormatException e) {
				DisguiseCraft.logger.log(Level.WARNING, "Could not parse version updates.");
				success = false;
			}
		} catch (DCUpdateException e) {
			DisguiseCraft.logger.log(Level.WARNING, e.getMessage());
			success = false;
		}
		
		if (!success) {
			notify("DisguiseCraft failed to check for updates, see the console log for more information", true);
		}
	}
	
	public void notify(String message, boolean error) {
		if (toNotify instanceof Player && !((Player) toNotify).isOnline()) {
			// Player went offline
			DisguiseCraft.logger.log(Level.INFO, "Player " + ((Player) toNotify).getDisplayName() + " went offline before we could tell him: " + message);
		} else {
			// Player is still online
			toNotify.sendMessage(((error) ? ChatColor.RED : ChatColor.BLUE) + message);
		}
	}
}
