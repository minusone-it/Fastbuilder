package it.minusone.fastbuilder.replay.data;

import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import org.bukkit.World;

import java.io.IOException;

public interface SaveRecordable {
    /**
     * Writes out the data of the recordable to a stream.
     */
    void write(ReplayOutputStream stream) throws IOException;

    /**
     * Reads the data into the recordables.
     */
    void read(ReplayInputStream stream, World world) throws IOException;
}
