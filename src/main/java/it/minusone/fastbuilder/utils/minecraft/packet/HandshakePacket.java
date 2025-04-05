package it.minusone.fastbuilder.utils.minecraft.packet;

import it.minusone.fastbuilder.utils.utils.bytes.ByteArrayBuffer;

public class HandshakePacket {

    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private int nextState;

    public HandshakePacket(int protocolVersion, String serverAddress, int serverPort, int nextState) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nextState = nextState;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public byte[] toByteArray() {
        return new ByteArrayBuffer()
                .writeVarInt(protocolVersion)
                .writeString(serverAddress)
                .writeShort((short) serverPort)
                .writeVarInt(nextState).toByteArray();
    }

    @Override
    public String toString() {
        return "HandshakePacket[" + protocolVersion + "," + serverAddress + "," + serverPort + "," + nextState + "]";
    }
}
