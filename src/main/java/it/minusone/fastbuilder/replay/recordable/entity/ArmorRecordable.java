package it.minusone.fastbuilder.replay.recordable.entity;

import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.replay.data.SaveRecordable;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.replay.npc.ReplayNPC;
import it.minusone.fastbuilder.replay.recordable.Recordable;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ArmorRecordable implements Recordable, SaveRecordable {
    private int entityId;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public ArmorRecordable() {
    }

    public ArmorRecordable(int entityId, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.entityId = entityId;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    @Override
    public void play(ReplayPlayback playback) {
        ReplayNPC npc = playback.getNPC(entityId);
        if (npc == null) return;
        npc.setHelmet(helmet);
        npc.setChestplate(chestplate);
        npc.setLeggings(leggings);
        npc.setBoots(boots);
    }

    @Override
    public void write(ReplayOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeItemStack(helmet);
        stream.writeItemStack(chestplate);
        stream.writeItemStack(leggings);
        stream.writeItemStack(boots);
    }

    @Override
    public void read(ReplayInputStream stream, World world) throws IOException {
        entityId = stream.readInt();
        helmet = stream.readItemStack();
        chestplate = stream.readItemStack();
        leggings = stream.readItemStack();
        boots = stream.readItemStack();
    }
}
