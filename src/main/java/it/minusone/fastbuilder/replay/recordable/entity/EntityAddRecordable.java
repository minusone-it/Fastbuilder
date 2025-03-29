package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.entity.EntityInfo;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.World;

import java.io.IOException;

public class EntityAddRecordable implements Recordable, SaveRecordable {
    private int entityId;
    private EntityInfo info;

    public EntityAddRecordable() {
    }

    public EntityAddRecordable(int entityId, EntityInfo info) {
        this.entityId = entityId;
        this.info = info;
    }

    @Override
    public void play(ReplayPlayback playback) {
        playback.spawnNpc(entityId, info);
        playback.getReplay().getEntityInfo().put(entityId, info);
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeEntityInfo(info);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
        info = stream.readEntityInfo();
    }
}
