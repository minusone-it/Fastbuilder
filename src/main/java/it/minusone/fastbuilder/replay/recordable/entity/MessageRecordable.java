package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MessageRecordable implements Recordable, SaveRecordable {
    private String message;

    public MessageRecordable() {
    }

    public MessageRecordable(String message) {
        this.message = message;
    }

    @Override
    public void play(ReplayPlayback playback) {
        for (Player listener : playback.getListeners()) {
            listener.sendMessage(message);
        }
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeString(message);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        message = stream.readString();
    }
}
