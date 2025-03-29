package it.minusone.fastbuilder.replay.recordable;

import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.ReplayPlayback;

public interface Recordable {
    /**
     * Called when event recorded
     */
    default void onRecord(Replay replay) {}

    /**
     * Called on playback
     */
    void play(ReplayPlayback playback);
}
