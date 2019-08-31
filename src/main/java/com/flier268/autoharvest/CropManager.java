package com.flier268.autoharvest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CropManager {
    public static final Block REED_BLOCK = Blocks.SUGAR_CANE;
    public static final Block NETHER_WART = Blocks.NETHER_WART;
    public static final Block BAMBOO = Blocks.BAMBOO;
    public static final Block KELP = Blocks.KELP;

    public static final Set<Block> WEED_BLOCKS = new HashSet<Block>() {{
        add(Blocks.OAK_SAPLING);
        add(Blocks.SPRUCE_SAPLING);
        add(Blocks.BIRCH_SAPLING);
        add(Blocks.JUNGLE_SAPLING);
        add(Blocks.ACACIA_SAPLING);
        add(Blocks.DARK_OAK_SAPLING);
        add(Blocks.FERN);
        add(Blocks.GRASS);
        add(Blocks.DEAD_BUSH);
        add(Blocks.BROWN_MUSHROOM);
        add(Blocks.RED_MUSHROOM);
        add(Blocks.TALL_GRASS);
        add(Blocks.LARGE_FERN);
        add(Blocks.SEAGRASS);
        add(Blocks.TALL_SEAGRASS);
        add(Blocks.KELP);
        add(Blocks.KELP_PLANT);
    }};

    public static final BiMap<Block, Item> SEED_MAP = HashBiMap.create(
            new HashMap<Block, Item>() {{
                put(Blocks.WHEAT, Items.WHEAT_SEEDS);
                put(Blocks.POTATOES, Items.POTATO);
                put(Blocks.CARROTS, Items.CARROT);
                put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
                put(Blocks.NETHER_WART, Items.NETHER_WART);
                put(Blocks.MELON_STEM, Items.MELON_SEEDS);
                put(Blocks.PUMPKIN_STEM, Items.PUMPKIN_SEEDS);
                put(Blocks.SUGAR_CANE, Items.SUGAR_CANE);
                put(Blocks.GRASS, Items.GRASS);
            }});

    public static final Multimap<Item, Class<? extends AnimalEntity>> FEED_MAP;

    static {
        FEED_MAP = ArrayListMultimap.create();
        FEED_MAP.put(Items.GOLDEN_CARROT, HorseEntity.class);

        FEED_MAP.put(Items.WHEAT, SheepEntity.class);
        FEED_MAP.put(Items.WHEAT, CowEntity.class);
        FEED_MAP.put(Items.WHEAT, MooshroomEntity.class);

        FEED_MAP.put(Items.CARROT, PigEntity.class);
        FEED_MAP.put(Items.POTATO, PigEntity.class);
        FEED_MAP.put(Items.BEETROOT, PigEntity.class);

        FEED_MAP.put(Items.PUMPKIN_SEEDS, ChickenEntity.class);
        FEED_MAP.put(Items.MELON_SEEDS, ChickenEntity.class);
        FEED_MAP.put(Items.WHEAT_SEEDS, ChickenEntity.class);
        FEED_MAP.put(Items.BEETROOT_SEEDS, ChickenEntity.class);

        FEED_MAP.put(Items.ROTTEN_FLESH, WolfEntity.class);

        FEED_MAP.put(Items.DANDELION, RabbitEntity.class); // Dandelion
        FEED_MAP.put(Items.CARROT, RabbitEntity.class);

        FEED_MAP.put(Items.WHEAT_SEEDS, ParrotEntity.class);

        //1.13
        FEED_MAP.put(Items.SEAGRASS, TurtleEntity.class);

        //1.14
        FEED_MAP.put(Items.KELP, PandaEntity.class);
        FEED_MAP.put(Items.SWEET_BERRIES, FoxEntity.class);
        FEED_MAP.put(Items.COD, CatEntity.class);
        FEED_MAP.put(Items.SALMON, CatEntity.class);
    }

    public static boolean isWeedBlock(World w, BlockPos pos) {
        Block b = w.getBlockState(pos).getBlock();
        return WEED_BLOCKS.contains(b);
    }

    public static boolean isCropMature(World w, BlockPos pos, BlockState stat, Block b) {
        if (b instanceof CropBlock) {
            return ((CropBlock) b).isMature(stat);
        } else if (b == NETHER_WART) {
            if (b instanceof NetherWartBlock)
                return stat.get(NetherWartBlock.AGE) >= 3;
            return false;
        } else if (b == REED_BLOCK || b== BAMBOO || b== KELP) {
            Block blockDown = w.getBlockState(pos.down()).getBlock();
            Block blockDown2 = w.getBlockState(pos.down(2)).getBlock();
            return (blockDown == REED_BLOCK && blockDown2 != REED_BLOCK) ||
                    (blockDown == BAMBOO && blockDown2 != BAMBOO) ||
                    (blockDown == KELP && blockDown2 != KELP);
        }
        return false;
    }

    public static boolean isSeed(ItemStack stack) {
        return (!stack.isEmpty()
                && SEED_MAP.containsValue(stack.getItem()));
    }

    public static boolean isCocoa(ItemStack stack) {
        return (!stack.isEmpty()
                && stack.getItem() == Items.COCOA_BEANS);
    }

    public static boolean isJungleLog(BlockState s) {
        return s.getBlock() == Blocks.JUNGLE_LOG;
    }

    public static boolean isRod(ItemStack stack) {
        return (!stack.isEmpty()
                && stack.getItem() == Items.FISHING_ROD
        );
    }

    public static boolean rodIsCast(ItemStack stack, ClientPlayerEntity player) {
        if (!isRod(stack)) {
            return false;
        }
        return stack.getItem() == Items.FISHING_ROD;
    }

    public static boolean canPlantOn(Item m, World w, BlockPos p) {
        if (!SEED_MAP.containsValue(m)) return false;
        return SEED_MAP.inverse().get(m).getDefaultState().canPlaceAt(w, p);
    }
}
