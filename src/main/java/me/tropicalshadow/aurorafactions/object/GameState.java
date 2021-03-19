package me.tropicalshadow.aurorafactions.object;

import org.bukkit.GameMode;

public enum GameState {
    NON(0,"Null"),
    BEFORE(1,"Before"),
    WAR(2,"War"),
    WINNER(3,"Winner");


    private final int id;
    private final String name;


    GameState(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static GameState fromID(int id){
        GameState output = null;
        for (GameState value : GameState.values()) {
            if(value.getId()==id)output=value;
        }
        return output;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
