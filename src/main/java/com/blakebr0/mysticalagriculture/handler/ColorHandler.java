package com.blakebr0.mysticalagriculture.handler;

import com.blakebr0.cucumber.helper.ColorHelper;
import com.blakebr0.cucumber.iface.IColored;
import com.blakebr0.mysticalagriculture.api.util.MobSoulUtils;
import com.blakebr0.mysticalagriculture.block.InfusedFarmlandBlock;
import com.blakebr0.mysticalagriculture.init.ModItems;
import com.blakebr0.mysticalagriculture.lib.ModCrops;
import com.blakebr0.mysticalagriculture.registry.AugmentRegistry;
import com.blakebr0.mysticalagriculture.registry.CropRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ColorHandler {
    @SubscribeEvent
    public void onBlockColors(ColorHandlerEvent.Block event) {
        var colors = event.getBlockColors();

        colors.register(new IColored.BlockColors(), InfusedFarmlandBlock.FARMLANDS.toArray(new InfusedFarmlandBlock[0]));

        CropRegistry.getInstance().getCrops().forEach(crop -> {
            if (crop.isFlowerColored() && crop.getCropBlock() != null)
                colors.register((state, world, pos, tint) -> crop.getFlowerColor(), crop.getCropBlock());
        });
    }

    @SubscribeEvent
    public void onItemColors(ColorHandlerEvent.Item event) {
        var colors = event.getItemColors();

        colors.register(new IColored.ItemBlockColors(), InfusedFarmlandBlock.FARMLANDS.toArray(new InfusedFarmlandBlock[0]));
        colors.register((stack, tint) -> {
            float damage = (float) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
            return ColorHelper.saturate(0x00D9D9, damage);
        }, ModItems.INFUSION_CRYSTAL.get());

        colors.register((stack, tint) -> ModCrops.AIR.getEssenceColor(), ModItems.AIR_AGGLOMERATIO.get());
        colors.register((stack, tint) -> ModCrops.EARTH.getEssenceColor(), ModItems.EARTH_AGGLOMERATIO.get());
        colors.register((stack, tint) -> ModCrops.WATER.getEssenceColor(), ModItems.WATER_AGGLOMERATIO.get());
        colors.register((stack, tint) -> ModCrops.FIRE.getEssenceColor(), ModItems.FIRE_AGGLOMERATIO.get());

        colors.register((stack, tint) -> {
            var type = MobSoulUtils.getType(stack);
            return tint == 1 && type != null ? type.getColor() : -1;
        }, ModItems.SOUL_JAR.get());

        CropRegistry.getInstance().getCrops().forEach(crop -> {
            if (crop.isEssenceColored() && crop.getEssenceItem() != null)
                colors.register((stack, tint) -> crop.getEssenceColor(), crop.getEssenceItem());
            if (crop.isSeedColored() && crop.getSeedsItem() != null)
                colors.register((stack, tint) -> crop.getSeedColor(), crop.getSeedsItem());
        });

        AugmentRegistry.getInstance().getAugments().forEach(augment -> {
            if (augment.getItem() != null)
                colors.register((stack, tint) -> tint == 0 ? augment.getSecondaryColor() : tint == 1 ? augment.getPrimaryColor() : -1, augment.getItem());
        });
    }
}
