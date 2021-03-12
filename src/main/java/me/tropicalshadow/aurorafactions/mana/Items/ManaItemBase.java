package me.tropicalshadow.aurorafactions.mana.Items;

import me.tropicalshadow.aurorafactions.utils.FactionColours;
import org.bukkit.inventory.ItemStack;

public interface ManaItemBase {


    int getCost();
    ItemStack getItem();
    FactionColours colour = FactionColours.NON;

}
