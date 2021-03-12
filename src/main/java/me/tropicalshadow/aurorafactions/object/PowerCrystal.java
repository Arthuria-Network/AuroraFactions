package me.tropicalshadow.aurorafactions.object;

import me.tropicalshadow.aurorafactions.utils.FactionColours;
import org.bukkit.entity.EnderCrystal;

public abstract class PowerCrystal implements EnderCrystal {

    private FactionColours colour = FactionColours.NON;

    public FactionColours getColour(){
        return this.colour;
    }
    public FactionColours setColour(FactionColours colour){
        this.colour = colour;
        return this.colour;
    }

    @Override
    public String getCustomName() {
        return this.colour.formatedName+" Crystal";
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }
}
