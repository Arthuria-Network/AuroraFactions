package me.tropicalshadow.aurorafactions.claims;

import me.tropicalshadow.aurorafactions.utils.FactionColours;
import org.bukkit.Chunk;
import org.bukkit.World;

public class Claim{
    FactionColours faction;
    Chunk chunk;

    public Claim(FactionColours faction, World world, int x, int y){
        this.faction = faction;
        chunk = world.getChunkAt(x,y);
    }
    public Claim(FactionColours faction, Chunk chunk){
        this.faction = faction;
        this.chunk = chunk;
    }
    public FactionColours getFaction() {
        return faction;
    }

    public Chunk getChunk() {
        return chunk;
    }
}