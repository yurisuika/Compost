package dev.yurisuika.compost.mixin.mods;

import dev.emi.emi.VanillaPlugin;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;

public interface EMIInvoker {

    @Mixin(VanillaPlugin.class)
    interface VanillaPluginInvoker {

        @Invoker("compressRecipesToTags")
        static void invokeCompressRecipesToTags(Set<Item> stacks, Comparator<Item> comparator, Consumer<TagKey<Item>> tagConsumer, Consumer<Item> itemConsumer) {
            throw new AssertionError();
        }

        @Invoker("safely")
        static void invokeSafely(String name, Runnable runnable) {
            throw new AssertionError();
        }

        @Invoker("synthetic")
        static Identifier invokeSynthetic(String type, String name) {
            throw new AssertionError();
        }

    }

}