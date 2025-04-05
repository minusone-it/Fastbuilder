package it.minusone.fastbuilder.utils.minecraft.packet;

import it.minusone.fastbuilder.utils.utils.bytes.ByteArrayBuffer;

public class MinecraftPacket {
    private int length;
    private int packetID;
    private byte[] data;

    public MinecraftPacket(int packetID, byte[] data) {
        this.packetID = packetID;
        this.data = data;
        this.length = new ByteArrayBuffer().writeVarInt(packetID).write(data).toByteArray().length;
    }


    public byte[] toByteArray() {
        return new ByteArrayBuffer()
                .writeVarInt(length)
                .writeVarInt(packetID)
                .write(data)
                .toByteArray();
    }
}
