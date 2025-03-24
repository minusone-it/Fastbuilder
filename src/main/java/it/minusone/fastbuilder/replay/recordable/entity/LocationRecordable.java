package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationRecordable implements Recordable, SaveRecordable {
    private int entityId;
    private Location location;

    public LocationRecordable() {
    }

    public LocationRecordable(int entityId, Location location) {
        this.entityId = entityId;
        this.location = location;
    }

    @Override
    public void onRecord(Replay replay) {
        location = location.clone().subtract(replay.getOrigin());
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        if (!location.getWorld().equals(playback.getOrigin().getWorld())) {
            location.setWorld(playback.getOrigin().getWorld());
        }
        npc.setLocation(location.clone().add(playback.getOrigin()));
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeLocation(location);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
        location = stream.readLocation(world);
    }
}
