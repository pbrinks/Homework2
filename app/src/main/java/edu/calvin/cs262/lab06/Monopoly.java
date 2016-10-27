package edu.calvin.cs262.lab06;

/**
 * Created by plb7 on 10/26/2016.
 * Monopoly class
 */

public class Monopoly {

    private int playerID;
    private String playerName, playerEmail;

    /**
     *
     * @param id, id of monopoly player
     * @param name, name of monopoly player
     * @param email, email of monopoly player
     */
    public Monopoly(int id, String name, String email) {
        this.playerID = id;
        this.playerName = name;
        this.playerEmail = email;
    }
    // getters of Monopoly instance vars
    public int getPlayerID() { return playerID; }
    public String getPlayerName() { return playerName; }
    public String getPlayerEmail() { return playerEmail; }


}
