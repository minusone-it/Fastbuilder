package it.minusone.fastbuilder.replay.recordable;

import it.minusone.fastbuilder.replay.ReplayPlayback;

public interface UndoRecordable {
    /**
     * Undoes whatever the recordable did
     */
    void undo(ReplayPlayback playback);
}
