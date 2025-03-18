package it.minusone.fastbuilder.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPC {
    private EntityPlayer npc;
    private String name;

    public NPC(Player player, String name, Location location) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), this.name);

        String[] skinData = SkinFetcher.getSkin(player.getUniqueId().toString());
        if (skinData != null) {
            profile.getProperties().put("textures", new Property("textures", skinData[0], skinData[1]));
        }

        npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void spawn(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((npc.yaw * 256) / 360)));

        // **Attiva mantelli, occhiali e dettagli della skin**
        DataWatcher dataWatcher = npc.getDataWatcher();
        dataWatcher.watch(10, (byte) 127); // 127 = Attiva tutte le parti della skin

        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
        }, 20L);
    }

    public void despawn(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
    }

    public EntityPlayer getNpc() {
        return npc;
    }
}
