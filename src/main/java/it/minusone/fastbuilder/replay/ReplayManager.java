package it.minusone.fastbuilder.replay;

 import java.io.File;
 import java.nio.file.Files;

 import it.minusone.fastbuilder.Main;
 import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
 import org.bukkit.entity.Player;
 
 public class ReplayManager {

     public static void playReplay(Player player, File replayFile) {
         try (ReplayInputStream replayInputStream = new ReplayInputStream(Files.newInputStream(replayFile.toPath()))) {
             Replay replay = Main.getInstance().replays.get(player.getUniqueId());//replayInputStream.readReplay(player.getLocation());
             ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, replay.getOrigin());
             playback.addListener(player);
             playback.setup();
             Main.currentPlayerReplay.put(player.getUniqueId(), playback);
             System.out.println(playback.getOrigin());
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     public static void pauseReplay(Player player) {
         try {
             ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
             System.out.println(playback.getOrigin());
             playback.pause();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     public static void runReplay(Player player) {
         try {
             ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
             playback.play();
             System.out.println(playback.getOrigin());
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     public static void endReplay(Player player) {
         try {
             ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
             playback.end();
             System.out.println(playback.getOrigin());
             Main.currentPlayerReplay.remove(player.getUniqueId());
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 }