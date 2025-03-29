package it.minusone.fastbuilder.utils;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import it.minusone.fastbuilder.utils.bukkit.entity.NPC;
import it.minusone.fastbuilder.utils.bukkit.inventory.Glow;
import it.minusone.fastbuilder.utils.bukkit.inventory.SignBuilder;
import it.minusone.fastbuilder.utils.bukkit.listeners.PlayerListener;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class a extends JavaPlugin {
    public static final boolean is1_8_8 = Bukkit.getVersion().contains("(MC: 1.8.8)");
    public static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (is1_8_8) {
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
            ReflectionUtils.setStaticField(Enchantment.class, "acceptingNew", true);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Glow.glow = new Glow(100);
                Enchantment.registerEnchantment(Glow.glow);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                try {
                    for (NPC n : NPC.all) {
                        if (n.isGravity()) {
                            n.entityPlayer.g(0, 0);
                            n.updateNpcLocation();
                        }
                    }

                    for (Hologram h : Hologram.all) {
                        if (h.followEntity != null) {
                            h.setLocation(h.followEntity.getLocation().add(h.followOffset));
                            h.tracker.forEach(p -> p.sendPacket(new PacketPlayOutEntityTeleport(h.armorStand)));
                        }
                    }

                    Set<SignBuilder> toRemove = new HashSet<>();
                    for (SignBuilder b : SignBuilder.signs) {
                        if (System.currentTimeMillis() - b.getGeneratedTime() > b.getTimeout()) {
                            toRemove.add(b);
                        }
                    }
                    SignBuilder.signs.removeAll(toRemove);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 1);
        }
    }
}
