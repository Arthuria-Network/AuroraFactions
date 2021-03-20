package me.tropicalshadow.aurorafactions.mana.Items;

import me.tropicalshadow.aurorafactions.utils.FactionColours;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ManaItemBase {

    void execute(Player player);
    ItemStack getItem();
    FactionColours colour = FactionColours.NON;

}
