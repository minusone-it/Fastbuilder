package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.World;

import java.io.IOException;

public class SwingRecordable implements Recordable, SaveRecordable {
    private int entityId;

    public SwingRecordable() {
    }

    public SwingRecordable(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        npc.swingArm();
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
