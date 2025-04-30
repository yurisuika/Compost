package dev.yurisuika.compost.mixin;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CompostMixinPlugin implements IMixinConfigPlugin {

    public static final Map<String, Supplier<Boolean>> EMI = ImmutableMap.of(
            "dev.yurisuika.compost.mixin.mods.EMIMixin$EmiCompostingRecipeMixin", () -> FabricLoader.getInstance().isModLoaded("emi")
    );

    public static final Map<String, Supplier<Boolean>> ROUGHLYENOUGHITEMS = ImmutableMap.of(
            "dev.yurisuika.compost.mixin.mods.RoughlyEnoughItemsMixin$DefaultClientPluginMixin", () -> FabricLoader.getInstance().isModLoaded("roughlyenoughitems")
    );

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return EMI.getOrDefault(mixinClassName, () -> true).get() && ROUGHLYENOUGHITEMS.getOrDefault(mixinClassName, () -> true).get();
    }

    public void onLoad(String mixinPackage) {}

    public String getRefMapperConfig() {
        return null;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}