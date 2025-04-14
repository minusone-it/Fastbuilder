package it.minusone.fastbuilder.replay.entity;

public class EntityInfo {
    private final String name;
    private final ReplaySkin skin;

    public EntityInfo(String name, ReplaySkin skin) {
        this.name = name;
        this.skin = skin;
    }

    public String getName() {
        return name;
    }

    public ReplaySkin getSkin() {
        return skin;
    }

    public boolean hasSkin() {
        return skin != null;
    }
}
