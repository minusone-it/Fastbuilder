package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.GameMode;
import org.bukkit.World;

import java.io.IOException;

public class GamemodeRecordable implements Recordable, SaveRecordable {
    private int entityId;
    private GameMode gamemode;

    public GamemodeRecordable() {
    }

    public GamemodeRecordable(int entityId, GameMode gamemode) {
        this.entityId = entityId;
        this.gamemode = gamemode;
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        npc.setGamemode(gamemode);
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeGameMode(gamemode);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
        gamemode = stream.readGameMode();
    }
}
