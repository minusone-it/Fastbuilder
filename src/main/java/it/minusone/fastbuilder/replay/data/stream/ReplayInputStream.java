package it.minusone.fastbuilder.replay.data.stream;

import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.ReplayAPI;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.entity.EntityInfo;
import it.minusone.fastbuilder.replay.entity.ReplaySkin;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplayInputStream extends DataInputStream {
    public ReplayInputStream(InputStream in) {
        super(in);
    }

    public Replay readReplay(Location origin) throws IOException, InstantiationException, IllegalAccessException {
        Replay replay = new Replay(origin);

        int length = readInt();
        replay.setLength(length);

        // Read metadata
        int metadataLength = readInt();
        for (int i = 0; i < metadataLength; i++) {
            String key = readString();
            String value = readString();
            replay.setMetadata(key, value);
        }

        // Read entity info
        int entityLength = readInt();
        for (int i = 0; i < entityLength; i++) {
            int id = readInt();
            EntityInfo info = readEntityInfo();

            replay.getEntityInfo().put(id, info);
        }

        // Read replay data
        int replayDataLength = readInt();
        Map<Integer, List<Recordable>> data = replay.getReplayData();

        for (int i = 0; i < replayDataLength; i++) {
            int tick = readInt();

            // Length of recordables
            int recordableLengths = readInt();
            List<Recordable> list = new ArrayList<>(recordableLengths);

            for (int j = 0; j < recordableLengths; j++) {
                boolean valid = readBoolean();
                if (!valid) continue;
                int id = readInt();
                Recordable recordable = readRecordable(id, origin.getWorld());
                list.add(recordable);
            }

            data.put(tick, list);
        }

        return replay;
    }

    /**
     * Reads a recordable based on the id.
     */
    public Recordable readRecordable(int id, World world) throws IOException, InstantiationException, IllegalAccessException {
        Map<Integer, Class<? extends SaveRecordable>> idToRecordable = ReplayAPI.getApi().getIdToRecordableMap();
        Class<? extends SaveRecordable> clazz = idToRecordable.get(id);

        if (clazz == null) {
            System.out.println("[!] Replay API null class: " + id);
        }

        SaveRecordable recordable = clazz.newInstance();
        recordable.read(this, world);

        return (Recordable) recordable;
    }

    /**
     * Reads a UTF-8 encoded string from the stream.
     */
    public String readString() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];

        int ignored = read(bytes, 0, bytes.length);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Reads an {@link ItemStack}.
     */
    public ItemStack readItemStack() throws IOException {
        Material material = readMaterial();
        byte data = readByte();

        return new ItemStack(material, 1, data);
    }

    /**
     * Reads a {@link EntityInfo} object from the stream.
     */
    public EntityInfo readEntityInfo() throws IOException {
        String name = readString();
        boolean hasSkin = readBoolean();

        ReplaySkin skin = null;
        if (hasSkin) {
            skin = readSkin();
        }

        return new EntityInfo(name, skin);
    }

    /**
     * Reads a {@link ReplaySkin} from the stream.
     */
    public ReplaySkin readSkin() throws IOException {
        String value = readString();
        String signature = readString();

        return new ReplaySkin(value, signature);
    }

    /**
     * Reads a {@link GameMode} object.
     */
    @SuppressWarnings("deprecation")
    public GameMode readGameMode() throws IOException {
        return GameMode.getByValue(readByte());
    }

    /**
     * Reads a {@link Material}.
     */
    @SuppressWarnings("deprecation")
    public Material readMaterial() throws IOException {
        return Material.getMaterial(readInt());
    }

    /**
     * Reads a {@link Location} from the stream.
     */
    public Location readLocation(World world) throws IOException {
        double x = readDouble();
        double y = readDouble();
        double z = readDouble();
        float pitch = readFloat();
        float yaw = readFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }
}
