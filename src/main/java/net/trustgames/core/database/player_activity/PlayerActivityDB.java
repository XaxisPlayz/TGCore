package net.trustgames.core.database.player_activity;

import net.trustgames.core.Core;
import net.trustgames.core.database.MariaDB;
import net.trustgames.core.debug.DebugColors;

import java.sql.*;

public class PlayerActivityDB {

    private final Core core;

    public PlayerActivityDB(Core core) {
        this.core = core;
    }

    public void initializePlayerActivityTable() {
        MariaDB mariaDB = new MariaDB(core);
        String preparedStatement = "CREATE TABLE IF NOT EXISTS player_activity(id INT primary key AUTO_INCREMENT, uuid varchar(40), action varchar(36), time DATETIME)";
        mariaDB.initializeDatabase("player_activity", preparedStatement);
    }

    // find the correct player's activity by his uuid
    public PlayerActivity findPlayerActivityByUUID(String uuid) {

        MariaDB mariaDB = new MariaDB(core);

        try {
            core.getLogger().info(DebugColors.PURPLE_BACKGROUND + getMaxID());
            System.out.println(uuid);
            PreparedStatement statement = mariaDB.getConnection().prepareStatement("SELECT * FROM player_activity WHERE uuid = ? ORDER BY id DESC LIMIT 1");
            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();

            if (results.next()) {

                //FIXME
                System.out.println(results.getString("id"));

                String action = results.getString("action");
                Timestamp time = results.getTimestamp("time");

                return new PlayerActivity(uuid, action, time);
            }
            else{
                System.out.println("HEHEHE");
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // method that creates the player activity
    public void createPlayerActivity(PlayerActivity playerActivity) throws SQLException {

        MariaDB mariaDB = new MariaDB(core);

        // try to create PreparedStatement
        try (PreparedStatement statement = mariaDB.getConnection().prepareStatement("INSERT INTO player_activity(uuid, action, time) VALUES (?, ?, ?)")) {
            statement.setString(1, playerActivity.getUuid());
            statement.setString(2, playerActivity.getAction());
            statement.setTimestamp(3, playerActivity.getTime());

            statement.executeUpdate();
        }
    }

    public int getMaxID(){

        MariaDB mariaDB = new MariaDB(core);

        int maxID = 0;

        try (PreparedStatement stmt = mariaDB.getConnection().prepareStatement("SELECT MAX(id) FROM player_activity")){
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                maxID = rs.getInt(1);
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return maxID;
    }
}
