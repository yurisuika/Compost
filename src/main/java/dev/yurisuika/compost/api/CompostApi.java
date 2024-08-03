package dev.yurisuika.compost.api;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompostApi {

    /**
     * <p>Retrieves a produce list for the given level name.
     *
     * <p>The produce configured for the loaded level is saved as a list that is paired with the name of the respective
     * level. This level name is matched against the contextual level name upon loading the config. If no match is
     * found, a new entry is created.
     *
     * @param name the given level name {@link String}
     *
     * @return a {@link Produce} {@link List} for the given level name
     */
    public static List<Produce> getProduce(String name) {
        return Option.getProduce(name);
    }

    /**
     * <p>Adds a produce to the list for the given level name.
     *
     * @param name the given level name {@link String}
     * @param produce a produce to add {@link Produce}
     *
     * @see #getProduce(String)
     */
    public static void addProduce(String name, Produce produce) {
        Option.addProduce(name, produce);
    }

    /**
     *<p> Removes a produce from the list for the given level name.
     *
     * @param name the given level name {@link String}
     * @param produce a produce to add {@link Produce}
     *
     * @see #getProduce(String)
     */
    public static void removeProduce(String name, Produce produce) {
        Option.removeProduce(name, produce);
    }

    /**
     * <p>Retrieves an itemstack list sent from the server to the client.
     *
     * <p>An itemstack list is generated from the configured produce for the loaded level. The count for each itemstack
     * is randomly chosen from an inclusive range of the configured max and min of the produce. Upon a client joining
     * the server or the config being saved, the server sends this list to the client.
     *
     * @return an {@link ItemStack} {@link List}
     */
    public static List<ItemStack> getStacks() {
        return Network.getStacks();
    }

}