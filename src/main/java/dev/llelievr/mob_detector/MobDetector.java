package dev.llelievr.mob_detector;

import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dev.llelievr.mob_detector.blockentities.MobDetectorEntity;
import dev.llelievr.mob_detector.blocks.MobDetectorBlock;
import dev.llelievr.mob_detector.peripheral.PeripheralHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Set;

@Mod(MobDetector.MODID)
public class MobDetector
{
    public static final String MODID = "mob_detector";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> MOB_DETECTOR = BLOCKS.register("mob_detector", MobDetectorBlock::new);
    public static final RegistryObject<Item> MOB_DETECTOR_ITEM = ITEMS.register("mob_detector", () -> new BlockItem(MOB_DETECTOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<MobDetectorEntity>> MOB_DETECTOR_TILE = BLOCK_ENTITIES.register("mob_detector", () ->  new BlockEntityType<>(MobDetectorEntity::new, Set.of(MOB_DETECTOR.get()), null));

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("mob_detector", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> MOB_DETECTOR_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(MOB_DETECTOR_ITEM.get());
            }).build());

    public MobDetector()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        ForgeComputerCraftAPI.registerPeripheralProvider(new PeripheralHandler());
    }
}
