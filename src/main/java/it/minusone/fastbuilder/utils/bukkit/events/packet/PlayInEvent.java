package it.minusone.fastbuilder.utils.bukkit.events.packet;

import it.minusone.fastbuilder.utils.eventbus.CubingEvent;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

//EVENT of listening PlayInPACKETS
public class PlayInEvent extends CubingEvent implements Cancellable {
    private final Player player;
    private final Packet<?> packet;
    private boolean cancel;

    public PlayInEvent(Player player, Packet<?> packet) {
        this.player = player;
        this.packet = packet;
    }

    public Player getPlayer() {
        return player;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
