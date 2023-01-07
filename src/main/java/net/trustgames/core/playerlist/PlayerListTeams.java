package net.trustgames.core.playerlist;

import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.WeightNode;
import net.trustgames.core.Core;
import net.trustgames.core.debug.DebugColors;
import net.trustgames.core.managers.LuckPermsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class PlayerListTeams {

    public static Scoreboard tabScoreboard;

    public PlayerListTeams(Core core) {
        tabScoreboard = core.getPlayerListScoreboard();
    }

    static TreeMap<String, Integer> groupOrder = new TreeMap<>();

    /* create all the teams by getting all groups from LuckPerms and putting each group in map
    with its corresponding weight. Then register new team with the first parameter weight, and second
    parameter the name of the group. Example: "20vip"
     */
    public static void createTeams() {

        int i = 0;

        TreeMap<String, Integer> groupWeight = new TreeMap<>();

        // get the groups and put the name and weight to the map
        for (Group y : LuckPermsManager.getGroups()){
            if (y.getWeight().isPresent()){
                groupWeight.put(y.getName(), y.getWeight().getAsInt());
            }
            else{
                Bukkit.getLogger().info(DebugColors.PURPLE + DebugColors.WHITE_BACKGROUND + "LuckPerms group " + y.getName() + " doesn't have any weight! Settings the weight to 1...");
                LuckPermsManager.getGroupManager().modifyGroup(y.getName(), group -> group.data().add(WeightNode.builder(1).build()));
                groupWeight.put(y.getName(), y.getWeight().getAsInt());
            }
        }

        /*
         order the map by highest value and put every group to a new ordered map with "i" value.
         also register a new team with (i + name). The lower "i", the highest order priority.
         Example: 1prime is lower then 0admin
        */
        for (String x : groupWeight.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).map(Map.Entry::getKey).toList()){
            groupOrder.put(x, i);
            tabScoreboard.registerNewTeam(i + "" + Objects.requireNonNull(LuckPermsManager.getGroupManager().getGroup(x)).getName());
            System.out.println(i + "" + Objects.requireNonNull(LuckPermsManager.getGroupManager().getGroup(x)).getName());
            i++;
        }
    }

    /* add player to the corresponding team by getting his primary group and its group's weight.
    set the prefix to team with luckperms cached data.
     */
    public static void addToTeam(Player player) {
        if (player == null) return;
        String team = groupOrder.get(LuckPermsManager.getPlayerPrimaryGroup(player)) + LuckPermsManager.getPlayerPrimaryGroup(player);

        Objects.requireNonNull(tabScoreboard.getTeam(team)).addPlayer(player);

        if (!team.contains("default")) {
            Objects.requireNonNull(tabScoreboard.getTeam(team)).prefix(Component.text(ChatColor.translateAlternateColorCodes('&', LuckPermsManager.getUser(player).getCachedData().getMetaData().getPrefix() + " ")));
        }

        player.setScoreboard(tabScoreboard);
    }

    // remove the player from the team
    public static void removeFromTeam(Player player) {
        Objects.requireNonNull(player.getScoreboard().getPlayerTeam(player)).removePlayer(player);
    }
}
