package it.minusone.fastbuilder.utils.bukkit.packetwrapper;

import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;

public class OutScoreboardScore {
    public final PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();

    public OutScoreboardScore() {
    }

    public OutScoreboardScore a(String playerName) {
        ReflectionUtils.setField(packet, "a", playerName);
        return this;
    }

    public OutScoreboardScore b(String objectiveName) {
        ReflectionUtils.setField(packet, "b", objectiveName);
        return this;
    }

    public OutScoreboardScore c(int score) {
        ReflectionUtils.setField(packet, "c", score);
        return this;
    }

    public OutScoreboardScore d(PacketPlayOutScoreboardScore.EnumScoreboardAction action) {
        ReflectionUtils.setField(packet, "d", action);
        return this;
    }
}
