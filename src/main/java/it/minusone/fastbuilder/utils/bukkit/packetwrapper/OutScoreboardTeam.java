package it.minusone.fastbuilder.utils.bukkit.packetwrapper;

import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import java.util.Collection;

public class OutScoreboardTeam {
    public final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

    public OutScoreboardTeam() {
    }

    public OutScoreboardTeam a(String name) {
        ReflectionUtils.setField(packet, "a", name);
        return this;
    }

    public OutScoreboardTeam e(String nametag) {
        ReflectionUtils.setField(packet, "e", nametag);
        return this;
    }

    public OutScoreboardTeam g(Collection<String> players) {
        ReflectionUtils.setField(packet, "g", players);
        return this;
    }

    public OutScoreboardTeam h(int h) {
        ReflectionUtils.setField(packet, "h", h);
        return this;
    }

}
