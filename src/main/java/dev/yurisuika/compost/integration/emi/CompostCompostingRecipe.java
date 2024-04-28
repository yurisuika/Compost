package dev.yurisuika.compost.integration.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiCompostingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Random;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

public class CompostCompostingRecipe extends EmiCompostingRecipe {

    public final EmiIngredient stack;
    public final float chance;
    public final Identifier id;

    public CompostCompostingRecipe(EmiIngredient stack, float chance, Identifier id) {
        super(stack, chance, id);
        this.stack = stack;
        this.chance = chance;
        this.id = id;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(stack, 0, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 46, 1);
        widgets.addText(EmiPort.literal(EmiRenderHelper.TEXT_FORMAT.format(chance * 100) + "%"), 32, 5, -1, true).horizontalAlign(TextWidget.Alignment.CENTER);
        widgets.addText(EmiPort.literal("x7"), 74, 5, -1, true);
        widgets.addGeneratedSlot(this::getStack, EmiUtil.RANDOM.nextInt(), 90, 0).recipeContext(this);
    }

    private EmiStack getStack(Random random) {
        Config.Group group = config.items[random.nextInt(config.items.length)];
        ItemStack stack = createItemStack(group);

        return EmiStack.of(stack).setAmount(stack.getCount()).setChance((float)group.chance);
    }

}