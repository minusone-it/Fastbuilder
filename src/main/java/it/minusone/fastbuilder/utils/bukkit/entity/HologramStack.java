package it.minusone.fastbuilder.utils.bukkit.entity;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class HologramStack {
    public final List<Hologram> hologramList = new ArrayList<>();
    private final int count;
    private final double d;

    public HologramStack(int count, double d, boolean everyoneCanSee, boolean autoSpawn, String... name) {
        this.count = count;
        this.d = d;
        for (int i = 0; i < count; i++) {
            hologramList.add(new Hologram(name[count - (i + 1)], everyoneCanSee, autoSpawn));
        }
    }

    public void delete() {
        for (Hologram h : hologramList) {
            h.delete();
        }
    }

    public HologramStack setLocation(Location location) {
        for (int i = 0; i < count; i++) {
            location.setY(location.getY() + d);
            hologramList.get(i).setLocation(location);
        }
        return this;
    }

    public HologramStack spawn() {
        for (Hologram h : hologramList) {
            h.spawn();
        }
        return this;
    }

    public HologramStack despawn() {
        for (Hologram h : hologramList) {
            h.despawn();
        }
        return this;
    }

    public HologramStack setName(int index, String name) {
        hologramList.get(count - index - 1).setName(name);
        return this;
    }

}
