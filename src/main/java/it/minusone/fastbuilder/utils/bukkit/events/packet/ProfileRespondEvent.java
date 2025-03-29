package it.minusone.fastbuilder.utils.bukkit.events.packet;

import it.minusone.fastbuilder.utils.api.mojang.ProfileSkin;
import it.minusone.fastbuilder.utils.eventbus.CubingEvent;

public class ProfileRespondEvent extends CubingEvent {

    private final ProfileSkin profile;
    public ProfileRespondEvent(ProfileSkin profile) {
        this.profile = profile;
    }
    public ProfileSkin getProfile() {
        return profile;
    }
}