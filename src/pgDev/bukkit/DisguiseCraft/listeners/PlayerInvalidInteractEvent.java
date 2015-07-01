package pgDev.bukkit.DisguiseCraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInvalidInteractEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int target;
    private final String action;

    public PlayerInvalidInteractEvent(final Player player, final int target, final String action) {
        super(player);
        this.target = target;
        this.action = action;
    }

    /**
     * Gets the entity ID that the player interacted with
     *
     * @return Entity ID of the target
     */
    public int getTarget() {
        return target;
    }

    /**
     * Gets the player's action
     *
     * @return INTERACT, ATTACK, or INTERACT_AT
     */
    public String getAction() {
        return action;
    }

	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}