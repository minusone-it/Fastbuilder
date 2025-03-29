package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemHeldRecordable implements Recordable, SaveRecordable {
    private int entityId;
    private Material material;
    private byte data;

    public ItemHeldRecordable() {
    }

    public ItemHeldRecordable(int entityId, Material material, byte data) {
        this.entityId = entityId;
        this.material = material;
        this.data = data;
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        npc.setHolding(new ItemStack(material, 1, data));
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeMaterial(material);
        stream.writeByte(data);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
        material = stream.readMaterial();
        data = stream.readByte();
    }
}
