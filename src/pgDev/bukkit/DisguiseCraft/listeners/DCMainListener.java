package pgDev.bukkit.DisguiseCraft.listeners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.*;

import pgDev.bukkit.DisguiseCraft.*;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft.ProtocolHook;
import pgDev.bukkit.DisguiseCraft.disguise.*;
import pgDev.bukkit.DisguiseCraft.listeners.attack.InvalidInteractHandler;
import pgDev.bukkit.DisguiseCraft.listeners.protocol.DCPacketInListener;
import pgDev.bukkit.DisguiseCraft.threading.NamedThreadFactory;
import pgDev.bukkit.DisguiseCraft.update.DCUpdateNotifier;

public class DCMainListener implements Listener {
	final DisguiseCraft plugin;
	
	public ExecutorService invalidInteractExecutor = Executors.newFixedThreadPool(
			DisguiseCraft.pluginSettings.pvpThreads, new NamedThreadFactory("DCInvalidInteractHandler"));
	
	public DCMainListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		// DC Attack Hack
		if (DisguiseCraft.protocolHook == ProtocolHook.DisguiseCraft) {
			DCPacketInListener.overrideConnection(player);
		}
		
		// Show disguises to newly joined players
		plugin.showWorldDisguises(player);
		
		// If he was a disguise-quitter, tell him
		if (plugin.disguiseQuitters.contains(player.getName())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You were undisguised because you left the server.");
			plugin.disguiseQuitters.remove(player.getName());
		}
		
		// Has a disguise?
		if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
			Disguise disguise = plugin.disguiseDB.get(player.getUniqueId());
			if (disguise.hasPermission(player)) {
				plugin.disguiseIDs.put(disguise.entityID, player);
				plugin.disguiseToWorld(player, player.getWorld());
				if (disguise.type.isPlayer()) {
					player.sendMessage(ChatColor.GOLD + "You were redisguised as player: " + disguise.data.getFirst());
				} else {
					player.sendMessage(ChatColor.GOLD + "You were redisguised as a " + ChatColor.DARK_GREEN + disguise.type.name());
				}
				
				// Start position updater
				plugin.setPositionUpdater(player.getUniqueId(), disguise);
			} else {
				plugin.disguiseDB.remove(player.getUniqueId());
				player.sendMessage(ChatColor.RED + "You do not have the permissions required to wear your disguise in this world.");
			}
		}
		
		// Updates?
		if (DisguiseCraft.pluginSettings.updateNotification && player.hasPermission("disguisecraft.update")) {
			// Check for new DisguiseCraft version
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new DCUpdateNotifier(plugin, player));
			
			// Bad configuration?
			if (DisguiseCraft.pluginSettings.disguisePVP && DisguiseCraft.protocolHook == ProtocolHook.None) {
				player.sendMessage(ChatColor.RED + "DisguiseCraft's configuration has " + ChatColor.GOLD + "\"disguisePVP\" " +
						ChatColor.RED + "set to " + ChatColor.GOLD + "true " + ChatColor.RED + "but ProtocolLib is not installed!");
			}
			
			if (DisguiseCraft.pluginSettings.noTabHide && DisguiseCraft.protocolHook != ProtocolHook.ProtocolLib) {
				player.sendMessage(ChatColor.RED + "DisguiseCraft's configuration has " + ChatColor.GOLD + "\"noTabHide\" " +
						ChatColor.RED + "set to " + ChatColor.GOLD + "true " + ChatColor.RED + "but ProtocolLib is not installed!");
			}
		}
	}
	
	@EventHandler
	public void onDisguiseHit(PlayerInvalidInteractEvent event) {
		invalidInteractExecutor.execute(new InvalidInteractHandler(event, plugin));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		// Undisguise them because they left
		if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
			plugin.disguiseIDs.remove(plugin.disguiseDB.get(player.getUniqueId()).entityID);
			if (DisguiseCraft.pluginSettings.quitUndisguise) {
				plugin.unDisguisePlayer(player, true);
				plugin.disguiseQuitters.add(player.getName());
			} else {
				plugin.undisguiseToWorld(player, player.getWorld(), true);
			}
		}
		
		// Undisguise others
		plugin.halfUndisguiseAllToPlayer(player);
		
		// Stop position updater
		plugin.removePositionUpdater(player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		// Handle this next tick
		plugin.getServer().getScheduler().runTask(plugin, new WorldChangeUpdater(plugin, event));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
			// Respawn disguise
			plugin.sendPacketToWorld(player.getWorld(), plugin.disguiseDB.get(player.getUniqueId()).packetGenerator.getSpawnPacket(event.getRespawnLocation()));
		}
		
		//Show the disguises to the player (in later ticks)
		plugin.getServer().getScheduler().runTaskLater(plugin, new DisguiseViewResetter(plugin, player), DisguiseCraft.pluginSettings.respawnResetDelay);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(final PlayerTeleportEvent event) {
		// Make sure they are teleporting within the same world
		if (event.getFrom().getWorld() != event.getTo().getWorld()) {
			return;
		}
		
		// Check if it was a chunk distance away
		if (event.getFrom().distanceSquared(event.getTo()) > 256) { // 16 * 16 = 256
			// As an observer, refresh disguises around you
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new DisguiseViewResetter(plugin, event.getPlayer()));
			
			// As a disguised, reset your disguise for everybody in the world
			Player teleporter = event.getPlayer();
			if (plugin.disguiseDB.containsKey(teleporter.getUniqueId())) {
				plugin.undisguiseToWorld(teleporter, event.getTo().getWorld(), false);
				plugin.disguiseToWorld(teleporter, event.getTo().getWorld());
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player player = (Player) event.getTarget();
			if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
				if (player.hasPermission("disguisecraft.notarget")) {
					if (player.hasPermission("disguisecraft.notarget.strict")) {
						event.setCancelled(true);
					} else {
						if (!plugin.disguiseDB.get(player.getUniqueId()).type.isPlayer() && (event.getReason() == TargetReason.CLOSEST_PLAYER || event.getReason() == TargetReason.RANDOM_TARGET)) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		if (plugin.disguiseDB.containsKey(event.getPlayer().getUniqueId())) {
			Disguise disguise = plugin.disguiseDB.get(event.getPlayer().getUniqueId());
			if (disguise.data != null && disguise.data.contains("nopickup")) {
				event.setCancelled(true);
			}
		}
	}
	
	/* Unnecessary workaround
	@EventHandler(ignoreCancelled = true)
	public void onPreCommand(PlayerCommandPreprocessEvent event) {
		// Split the command into its parts
		String commandArgs[] = event.getMessage().split(" ");
		if (commandArgs.length > 0 && commandArgs[0].startsWith("/")) {
			commandArgs[0] = commandArgs[0].substring(1);
		}
		
		// See if its the teleport command
		if (commandArgs[0].equalsIgnoreCase("tp")) {
			// See if they have the permission to teleport to disguised players
			if (event.getPlayer().hasPermission("disguisecraft.teleporttodisguised")) {
				String target = null;
				String destination = null;
				
				// Check the form they used
				if (commandArgs.length == 2) {
					// tp <destination player>
					destination = commandArgs[1];
				} else if (commandArgs.length == 3) {
					// tp <target player> <destination player>
					target = commandArgs[1];
					destination = commandArgs[2];
				}
				
				// See if destination player specified is valid
				Player player = plugin.getServer().getPlayerExact(destination);
				if (player != null) {
					// See if the destination player is disguised
					if (plugin.disguiseDB.containsKey(player.getUniqueId())) {
						// If the command sender can't see the disguised player, we need to run the teleport
						if (!event.getPlayer().canSee(player)) {
							// Parse the target
							Player toTeleport = null;
							if (target == null) {
								toTeleport = event.getPlayer();
							} else {
								toTeleport = plugin.getServer().getPlayerExact(target);
							}
							
							// Check that we have somebody to teleport
							if (toTeleport == null) {
								event.getPlayer().sendMessage(ChatColor.RED + "Could not find player specified to be teleported");
							} else {
								// Run the teleport
								toTeleport.teleport(player);
							}
						}
					}
				}
			}
		}
	}*/
}
