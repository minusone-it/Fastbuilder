package it.minusone.fastbuilder.scoreboard.helpers;

import it.minusone.fastbuilder.scoreboard.helpers.bukkit.BPlayerBoard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * The main class of the Netherboard API for Bukkit,
 * you'll need to use it if you want to create boards.
 * <p>
 * To create a board, get the instance using <code>Netherboard.instance()</code>
 * and call one of the <code>createBoard()</code> methods.
 */
public class Scoreboard {

    private static Scoreboard instance;

    private final Map<Player, BPlayerBoard> boards = new HashMap<>();

    private Scoreboard() {
    }

    /**
     * Returns the instance of the Netherboard class.
     *
     * @return the instance
     */
    public static Scoreboard instance() {
        if (instance == null)
            instance = new Scoreboard();

        return instance;
    }

    /**
     * Creates a board to a player.
     *
     * @param player the player
     * @param name   the name of the board
     * @return the newly created board
     */
    public BPlayerBoard createBoard(Player player, String name) {
        return createBoard(player, null, name);
    }

    /**
     * Creates a board to a player, using a predefined scoreboard.
     *
     * @param player     the player
     * @param scoreboard the scoreboard to use
     * @param name       the name of the board
     * @return the newly created board
     */
    public BPlayerBoard createBoard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, String name) {
        deleteBoard(player);

        BPlayerBoard board = new BPlayerBoard(player, scoreboard, name);

        boards.put(player, board);
        return board;
    }

    /**
     * Deletes the board of a player.
     *
     * @param player the player
     */
    public void deleteBoard(Player player) {
        if (boards.containsKey(player))
            boards.get(player).delete();
    }

    /**
     * Removes the board of a player from the boards map.<br>
     * <b>WARNING: Do not use this to delete the board of a player!</b>
     *
     * @param player the player
     */
    public void removeBoard(Player player) {
        boards.remove(player);
    }

    /**
     * Gets the board of a player.
     *
     * @param player the player
     * @return the player board, or null if the player has no board
     */
    public BPlayerBoard getBoard(Player player) {
        return boards.get(player);
    }

}
