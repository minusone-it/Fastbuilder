package it.minusone.fastbuilder.utils.bukkit.entity;

import it.minusone.fastbuilder.utils.bukkit.events.entity.ClickEvent;
import it.minusone.fastbuilder.utils.utils.collection.Sets;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Hologram {

    public static final Set<Hologram> all = new HashSet<>();
    public final Set<String> world = new HashSet<>();
    public final boolean autoSpawn;
    public final boolean everyoneCanSee;
    public final EntityArmorStand armorStand;
    public Set<PlayerConnection> tracker = new HashSet<>();
    public Entity followEntity;
    public Vector followOffset;
    Set<PlayerConnection> temp = new HashSet<>();
    private Consumer<ClickEvent> event;

    public Hologram(String name, boolean everyoneCanSee, boolean autoSpawn) {
        this.everyoneCanSee = everyoneCanSee;
        this.autoSpawn = autoSpawn;
        armorStand = new EntityArmorStand(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle());
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(true);
        armorStand.setInvisible(true);
        armorStand.n(true);
        armorStand.setCustomName(name);
        all.add(this);
        for (Player p : Bukkit.getOnlinePlayers()) {
            addListener(p);
        }
    }

    public void delete() {
        all.remove(this);
    }

    public Consumer<ClickEvent> getClickEvent() {
        return event;
    }

    public Hologram setClickEvent(Consumer<ClickEvent> e) {
        armorStand.n(false);
        this.event = e;
        return this;
    }

    public void spawnFor(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(this.armorStand));
    }


    public Hologram addListener(Player... players) {
        for (Player p : players) {
            tracker.add(((CraftPlayer) p).getHandle().playerConnection);
        }
        return this;
    }

    public Hologram addListener(Collection<Player> players) {
        for (Player p : players) {
            tracker.add(((CraftPlayer) p).getHandle().playerConnection);
        }
        return this;
    }

    public Hologram setListener(Player... players) {
        temp = tracker;
        tracker = new HashSet<>();
        for (Player p : players) {
            tracker.add(((CraftPlayer) p).getHandle().playerConnection);
        }
        return this;
    }

    public Hologram setListener(Collection<Player> players) {
        temp = tracker;
        tracker = new HashSet<>();
        for (Player p : players) {
            tracker.add(((CraftPlayer) p).getHandle().playerConnection);
        }
        return this;
    }

    public boolean hasListener(Player player) {
        return tracker.contains(((CraftPlayer) player).getHandle().playerConnection);
    }

    public Hologram removeListener(Player... players) {
        for (Player p : players) {
            tracker.remove(((CraftPlayer) p).getHandle().playerConnection);
        }
        return this;
    }

    public Hologram undoSetListener() {
        tracker = temp;
        temp = null;
        return this;
    }

    public Hologram world(String... world) {
        this.world.clear();
        this.world.addAll(Sets.hashSet(world));
        if (everyoneCanSee) {
            this.tracker.clear();
            for (String s : world) {
                Bukkit.getWorld(s).getPlayers().forEach(a -> this.tracker.add(((CraftPlayer) a).getHandle().playerConnection));
            }
        }
        return this;
    }

    public Hologram setLocation(double x, double y, double z, float yaw, float pitch) {
        armorStand.setLocation(x, y, z, yaw, pitch);
        return this;
    }

    public Hologram setLocation(Location location) {
        world.add(location.getWorld().getName());
        return setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Hologram spawn() {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
        tracker.forEach(a -> a.sendPacket(packet));
        return this;
    }

    public Hologram despawn() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.getId());
        tracker.forEach(a -> a.sendPacket(packet));
        return this;
    }

    public Hologram setName(String name) {
        armorStand.setCustomName(name);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
        tracker.forEach(a -> a.sendPacket(packet));
        return this;
    }

}
