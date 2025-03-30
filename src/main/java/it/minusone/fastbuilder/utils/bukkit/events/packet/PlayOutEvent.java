package it.minusone.fastbuilder.utils.bukkit.events.packet;

import it.minusone.fastbuilder.utils.eventbus.CubingEvent;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

//EVENT of listening PlayOutPACKETS
public class PlayOutEvent extends CubingEvent implements Cancellable {
    private final Packet<?> packet;
    private final Player player;
    private boolean cancel;

    public PlayOutEvent(Player player, Packet<?> packet) {
        this.packet = packet;
        this.player = player;
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
