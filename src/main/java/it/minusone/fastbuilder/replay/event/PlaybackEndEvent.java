package it.minusone.fastbuilder.replay.event;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlaybackEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ReplayPlayback playback;

    public PlaybackEndEvent(ReplayPlayback playback) {
        this.playback = playback;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public ReplayPlayback getPlayback() {
        return playback;
    }
}
