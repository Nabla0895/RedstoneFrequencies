package nabla.redstone_frequencies.component;

import nabla.redstone_frequencies.Redstone_frequencies;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<BlockPos> COORDINATES = register("coordinates", builder -> builder.codec(BlockPos.CODEC));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Redstone_frequencies.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        Redstone_frequencies.LOGGER.info("Registering Data Component Types for " + Redstone_frequencies.MOD_ID);
    }
}
