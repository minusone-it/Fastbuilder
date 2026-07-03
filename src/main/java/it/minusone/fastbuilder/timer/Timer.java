package it.minusone.fastbuilder.timer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.LobbyItem;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.data.stream.ReplayOutputStream;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Timer {

    /**
     * Conta i pacchetti di movimento ricevuti per ogni giocatore.
     *
     * Il client Minecraft 1.8 manda ESATTAMENTE uno di questi quattro pacchetti
     * per ogni tick, a seconda di cosa è cambiato in quel frame:
     *
     *   FLYING        → solo flag onGround  (giocatore fermo, nessun input)
     *   POSITION      → posizione cambiata
     *   LOOK          → rotazione cambiata
     *   POSITION_LOOK → posizione + rotazione cambiate
     *
     * Ascoltando tutti e quattro otteniamo un evento per tick esatto,
     * indipendentemente dal server scheduler (risolve i salti su Paper).
     *
     * ConcurrentHashMap perché ProtocolLib chiama onPacketReceiving dal thread
     * Netty del giocatore, non dal main thread di Bukkit.
     */
    private final ConcurrentHashMap<UUID, Integer> playerTicks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> alreadyTeleported = new ConcurrentHashMap<>();

    public Timer() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        pm.addPacketListener(new PacketAdapter(
                Main.getInstance(),
                ListenerPriority.MONITOR,
                PacketType.Play.Client.FLYING,        // fermo
                PacketType.Play.Client.POSITION,      // si muove
                PacketType.Play.Client.LOOK,          // gira la testa
                PacketType.Play.Client.POSITION_LOOK  // muove + gira
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                final UUID uuid = event.getPlayer().getUniqueId();

                // compute() è atomico su ConcurrentHashMap:
                // se la chiave non esiste (timer non attivo) restituisce null e
                // non fa nulla; altrimenti incrementa di 1.
                Integer newTicks = playerTicks.compute(uuid, (k, v) -> v == null ? null : v + 1);

                // null → timer non attivo per questo giocatore, ignoriamo
                if (newTicks == null) return;

                // Aggiornamento scoreboard e action bar dal thread Netty.
                // sendPacket() è thread-safe; getConfigManager() legge dati
                // immutabili o volatili quindi va bene fuori dal main thread.
                Player player = event.getPlayer();
                String timeFormatted = getTimeFormatted(player);
                double bestTime = Main.getInstance().getConfigManager()
                        .getPB(uuid, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);

                Main.getInstance().scoreboardManager.updateScoreboard(player, timeFormatted, bestTime);

                if (Main.getInstance().getConfigManager().isActionBarEnabled()) {
                    CraftPlayer cp = (CraftPlayer) player;
                    cp.getHandle().playerConnection.sendPacket(
                            new PacketPlayOutChat(
                                    new ChatComponentText(
                                            Main.getInstance().getConfigManager().getActionBarMessage(player)
                                    ),
                                    (byte) 2
                            )
                    );
                }
            }
        });
    }

    // -------------------------------------------------------------------------
    // API pubblica
    // -------------------------------------------------------------------------

    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();
        setAlreadyTeleported(player, false);

        // Mettere 0 (e non rimuovere + rimettere) è atomico su ConcurrentHashMap.
        // Il prossimo FLYING/POSITION/LOOK/POSITION_LOOK lo porterà a 1.
        playerTicks.put(uuid, 0);

        Replay replay = new Replay(
                Main.getInstance().getConfigManager().getSpawnLocation(
                        Main.getInstance().mapWherePlayerIn.get(player).split("-")[0],
                        Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]
                )
        );
        replay.addPlayer(player);
        replay.addRecording();
        replay.start();
        Main.getInstance().replays.put(uuid, replay);
        Main.getInstance().lastReplay.put(uuid, replay);
    }

    public void stopTimer(Player player, boolean saveReplay) {
        UUID uuid = player.getUniqueId();

        // Rimuovendo l'UUID il listener lo ignorerà automaticamente
        // senza dover cancellare task o de-registrare listener.
        playerTicks.remove(uuid);

        Replay replay = Main.getInstance().replays.get(uuid);
        if (replay != null) {
            replay.removeRecording();
            replay.end();
        }

        if (!isAlreadyTeleported(player) && saveReplay && replay != null) {
            try {
                File replayFolder = new File(
                        Main.getInstance().getDataFolder()
                                + File.separator + "replays"
                                + File.separator + uuid
                                + File.separator + Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]
                );
                if (!replayFolder.exists()) replayFolder.mkdirs();

                File replayFile = new File(
                        replayFolder.toPath() + File.separator + System.currentTimeMillis() + ".replay"
                );
                ReplayOutputStream ros = new ReplayOutputStream(Files.newOutputStream(replayFile.toPath()));
                ros.writeReplay(replay);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "An error occurred while saving the replay!");
            }
        }

        Main.getInstance().replays.remove(uuid, replay);
        setAlreadyTeleported(player, false);
    }

    // -------------------------------------------------------------------------
    // Tempo
    // -------------------------------------------------------------------------

    /**
     * Ogni tick client = 0.05 s (il client 1.8 gira fisso a 20 fps).
     * Il +0.15 è mantenuto per compatibilità con i PB già salvati.
     */
    public double getTime(Player player) {
        return playerTicks.getOrDefault(player.getUniqueId(), 0) * 0.05 - 0.05;
    }

    public String getTimeFormatted(Player player) {
        return new DecimalFormat("0.000").format(getTime(player)).replace(",", ".");
    }

    public boolean isPlayerInTimer(Player player) {
        return playerTicks.containsKey(player.getUniqueId());
    }

    // -------------------------------------------------------------------------
    // Teleport guard
    // -------------------------------------------------------------------------

    public boolean isAlreadyTeleported(Player player) {
        return alreadyTeleported.getOrDefault(player.getUniqueId(), false);
    }

    public void setAlreadyTeleported(Player player, boolean state) {
        alreadyTeleported.put(player.getUniqueId(), state);
    }

    // -------------------------------------------------------------------------
    // Score
    // -------------------------------------------------------------------------

    public boolean scoreTime(Player player, boolean practiceBlocks) {
        String timeFormatted = getTimeFormatted(player);
        double time = getTime(player);
        List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());

        if (timeFormatted.equalsIgnoreCase("0.000") || timeFormatted.equalsIgnoreCase("-0.050")) {
            return false;
        }

        stopTimer(player, !practiceBlocks);

        double PB = Main.getInstance().getConfigManager()
                .getPB(player.getUniqueId(), Main.getInstance().getModeOfPlayer(player));
        boolean equalTime = PB == time;
        boolean newPB = time < PB;

        int baseCoins       = (int) (1000 / (time + 1));
        int efficiencyBonus = (int) ((double) playerBlocks.size() / (time + 1) * 10);
        int pbBonus         = newPB ? 7 : 0;
        int coins           = Math.max(10, (baseCoins + efficiencyBonus + pbBonus) / 10);

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);

        if (newPB) {
            World world = player.getWorld();
            Location loc = player.getLocation();
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                Firework firework = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .flicker(false).trail(true)
                        .withColor(Color.RED).withFade(Color.ORANGE)
                        .with(FireworkEffect.Type.BALL).build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            });
        }

        List<String> timeMessages = Main.getInstance().getConfigManager().getTimeMessage();
        List<String> pbMessages   = Main.getInstance().getConfigManager().getPersonalBestMessage();
        DecimalFormat format      = getFormat();

        boolean zeroPB = Main.getInstance().getConfigManager()
                .getPB(player.getUniqueId(),
                        Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]) == 0.000;

        if (newPB || zeroPB) {
            for (String msg : pbMessages)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg
                        .replace("%pb%",        format.format(PB))
                        .replace("%difference%",(equalTime ? "+" : "") + format.format(time - PB))
                        .replace("%time%",      timeFormatted)));
        } else {
            for (String msg : timeMessages)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg
                        .replace("%pb%",        format.format(PB))
                        .replace("%difference%","+" + format.format(time - PB))
                        .replace("%time%",      timeFormatted)));
        }

        String scoreTitle = Main.getInstance().getConfigManager().getScoreTitle()
                .replace("%time%", timeFormatted).replace("%coins%", "" + coins).replace("&", "§");
        String scoreSub = Main.getInstance().getConfigManager().getScoreSubTitle()
                .replace("%time%", timeFormatted).replace("%coins%", "" + coins).replace("&", "§");

        if (!Main.getInstance().playersInPracticeMode.get(player.getUniqueId())) {
            sendTitle(player, scoreTitle, scoreSub, 0, 70, 20);
            Main.getInstance().getConfigManager().addCoinsToPlayer(player, coins);
            Main.getInstance().getConfigManager().saveBestTime(
                    player.getUniqueId(),
                    Main.getInstance().mapWherePlayerIn.get(player).split("-")[0],
                    time);
            Main.getInstance().updateLeaderboard(
                    player,
                    Main.getInstance().mapWherePlayerIn.get(player).split("-")[0],
                    time);
        } else {
            sendTitle(player, scoreTitle, "", 0, 70, 20);
        }

        player.getInventory().clear();
        Main.getInstance().scoreboardManager.updateScoreboard(
                player, timeFormatted,
                Main.getInstance().getConfigManager()
                        .getPB(player.getUniqueId(), Main.getInstance().getModeOfPlayer(player)));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Main.getInstance().mapWherePlayerIn.containsKey(p))
                Main.getInstance().scoreboardManager.updateScoreboard(p);
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!isAlreadyTeleported(player)) {
                    player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(
                            Main.getInstance().mapWherePlayerIn.get(player).split("-")[0],
                            Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]));
                }
                ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
                block.setAmount(64);
                player.getInventory().setItem(0, block);
                player.getInventory().setItem(1, block);
                player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                if (Main.getInstance().playersInPracticeMode.get(player.getUniqueId()))
                    player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64));
                player.getInventory().setItem(4, new IslandSelectorItem());
                player.getInventory().setItem(6, new ReplayItem());
                player.getInventory().setItem(7, new SettingsItem());
                if (Main.getInstance().getConfigManager().isBungeeToggled())
                    player.getInventory().setItem(8, new LobbyItem());
            }
        }, 40L);

        for (Location loc : playerBlocks)
            loc.getBlock().setType(Material.AIR);
        Main.getInstance().placedBlocks.remove(player.getUniqueId());

        return true;
    }

    // -------------------------------------------------------------------------
    // Utilità statiche
    // -------------------------------------------------------------------------

    public static void sendTitle(Player player, String title, String subtitle,
                                 int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent titleJSON =
                IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subtitleJSON =
                IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");

        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
        cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON));
        cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON));
    }

    public static DecimalFormat getFormat() {
        return new DecimalFormat("0.000");
    }
}