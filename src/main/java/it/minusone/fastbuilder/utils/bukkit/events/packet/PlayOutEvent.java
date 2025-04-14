package it.minusone.fastbuilder.utils.bukkit.events.packet;

import it.minusone.fastbuilder.utils.eventbus.Event;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

//EVENT of listening PlayOutPACKETS
public class PlayOutEvent extends Event implements Cancellable {
    private boolean cancel;

    public PlayOutEvent(Player player, Packet<?> packet) {
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
