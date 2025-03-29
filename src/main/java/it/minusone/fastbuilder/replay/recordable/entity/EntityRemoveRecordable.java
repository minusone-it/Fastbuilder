package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.World;

import java.io.IOException;

public class EntityRemoveRecordable implements Recordable, SaveRecordable {
    private int entityId;

    public EntityRemoveRecordable() {
    }

    public EntityRemoveRecordable(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void play(ReplayPlayback playback) {
        if (playback.getNPC(entityId) != null) {
            playback.removeNPC(entityId);
        }
        playback.getReplay().getEntityInfo().remove(entityId);
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
