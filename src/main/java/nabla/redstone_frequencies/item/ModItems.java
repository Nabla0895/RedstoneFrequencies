package nabla.redstone_frequencies.item;

import nabla.redstone_frequencies.Redstone_frequencies;
import nabla.redstone_frequencies.item.custom.RedstoneLinkingTool;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static void init() {
        Redstone_frequencies.LOGGER.info("Registrering Mod Items for " + Redstone_frequencies.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(ModItems.REDSTONE_LINKING_TOOL);
        });
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Redstone_frequencies.MOD_ID, name));

        Item item = itemFactory.apply(settings.registryKey(itemKey));

        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static final Item REDSTONE_LINKING_TOOL = register("redstone_linking_tool", RedstoneLinkingTool::new, new Item.Settings());
}
