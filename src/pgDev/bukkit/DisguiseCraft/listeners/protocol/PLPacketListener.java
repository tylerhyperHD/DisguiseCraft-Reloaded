package pgDev.bukkit.DisguiseCraft.listeners.protocol;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketAdapter.AdapterParameteters;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

import pgDev.bukkit.DisguiseCraft.*;
import pgDev.bukkit.DisguiseCraft.listeners.PlayerInvalidInteractEvent;

public class PLPacketListener {
	final DisguiseCraft plugin;
	ProtocolManager pM = DisguiseCraft.protocolManager;
	
	public ConcurrentLinkedQueue<UUID> recentlyDisguised;
	
	public PLPacketListener(final DisguiseCraft plugin) {
		this.plugin = plugin;
	}
	
	public void setupAttackListener() {
		AdapterParameteters ap = PacketAdapter.params();
		ap.plugin(plugin);
		ap.clientSide();
		ap.types(PacketType.Play.Client.USE_ENTITY);
		
		pM.addPacketListener(new PacketAdapter(ap) {
			    @Override
			    public void onPacketReceiving(PacketEvent event) {
			    	Player player = event.getPlayer();
			        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
			            try {
			            	PacketContainer packet = event.getPacket();
			                int target = packet.getSpecificModifier(int.class).read(0);
			                String action = packet.getEntityUseActions().read(0).name();
			                
			                if (packet.getEntityModifier(player.getWorld()).read(0) == null) {
			                	PlayerInvalidInteractEvent newEvent = new PlayerInvalidInteractEvent(player, target, action);
			                    plugin.getServer().getPluginManager().callEvent(newEvent);
			                }
			            } catch (FieldAccessException e) {
			                DisguiseCraft.logger.log(Level.SEVERE, "Couldn't access a field in a UseEntity packet!", e);
			            }
			        }
			    }
		});
	}
	
	public void setupTabListListener() {
		// Make database
		recentlyDisguised = new ConcurrentLinkedQueue<UUID>();
		
		// Set up listener
		AdapterParameteters ap = PacketAdapter.params();
		ap.plugin(plugin);
		ap.serverSide();
		ap.types(PacketType.Play.Server.PLAYER_INFO);
		
		pM.addPacketListener(new PacketAdapter(ap) {
			    @Override
			    public void onPacketSending(PacketEvent event) {
			        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
			        	try {
			        		// Check the first player in the list
			        		PlayerInfoData playerInfoData = (PlayerInfoData) event.getPacket().getSpecificModifier(List.class).read(0).get(0);
				        	if (recentlyDisguised.remove(playerInfoData.a().getId())) {
				        		event.setCancelled(true);
				        	}
			        	} catch (FieldAccessException e) {
			                DisguiseCraft.logger.log(Level.SEVERE, "Couldn't access a field in a PlayerInfo packet!", e);
			            }
			        }
			    }
		});
	}
}
