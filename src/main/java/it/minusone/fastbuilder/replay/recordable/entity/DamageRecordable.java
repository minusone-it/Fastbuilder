package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DamageRecordable implements Recordable, SaveRecordable {
    private int entityId;

    public DamageRecordable() {
    }

    public DamageRecordable(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        npc.damageAnimation();
        for (Player player : playback.getListeners()) {
            player.playSound(npc.getLocation(), Sound.HURT_FLESH, 1, 1);
        }
    }


    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
    }
}
