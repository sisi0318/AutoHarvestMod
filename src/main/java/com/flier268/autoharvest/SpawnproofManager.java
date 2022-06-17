package com.flier268.autoharvest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class SpawnproofManager {
    private final Configuration configuration;
    private final PlayerManager playerManager;
    /*
    This is a set of items you can spawnproof in the nether with; specifically, they should be non-flammable.
     */
    private final Set<Item> netherSpawnproofItems = Set.of(
            // buttons
            Items.STONE_BUTTON, Items.POLISHED_BLACKSTONE_BUTTON,
            // pressure plates
            Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Items.STONE_PRESSURE_PLATE, Items.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            // slabs
            Items.STONE_SLAB, Items.STONE_BRICK_SLAB, Items.COBBLESTONE_SLAB,
            Items.COBBLED_DEEPSLATE_SLAB, Items.NETHER_BRICK_SLAB,
            Items.GRANITE_SLAB
    );
    private final Set<Item> overworld_spawnproof_items = null;
    /*
    This is a set of blocks that give dialogs when interacted with.  These will NOT be spawnproofed.
     */
    private final Set<Block> interactiveBlocks = Set.of(
            Blocks.CRAFTING_TABLE, Blocks.SMITHING_TABLE, Blocks.CARTOGRAPHY_TABLE,
            Blocks.LOOM
    );

    public SpawnproofManager(Configuration configuration, PlayerManager playerManager) {
        this.configuration = configuration;
        this.playerManager = playerManager;
    }

    void spawnproofingTick() {
        ItemStack itemsInHand = playerManager.tryFillItemInHand();
        if (null == itemsInHand || !isSpawnproofer(itemsInHand))
            return;
        ClientPlayerEntity p = playerManager.getPlayer();
        World w = p.getEntityWorld();

        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY() + 0.2D);// the "leg block" , in case in soul sand
        int Z = (int) Math.floor(p.getZ());
        BlockPos playerPos = new BlockPos(X, Y, Z);

        for (int deltaY = 3; deltaY >= -2; --deltaY)
            for (int deltaX = -configuration.effect_radius.value; deltaX <= configuration.effect_radius.value; ++deltaX)
                for (int deltaZ = -configuration.effect_radius.value; deltaZ <= configuration.effect_radius.value; ++deltaZ) {
                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);
                    // skip current location - cannot place slabs there
                    // also skip target if mobs cannot spawn inside block
                    if (playerPos.equals(pos) || !w.getBlockState(pos).getBlock().canMobSpawnInside())
                        continue;
                    BlockPos downPos = pos.down();
                    BlockState downBlock = w.getBlockState(downPos);
                    // next, the block below needs to be solid, not have entity data (eliminates things like furnaces)
                    // and not be interactive (like crafting tables)
                    if (!downBlock.isSolidBlock(w, downPos) ||
                            downBlock.hasBlockEntity() ||
                            interactiveBlocks.contains(downBlock.getBlock())
                    ) continue;
                    BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y, Z + deltaZ + 0.5),
                            Direction.UP, downPos, false);
                    System.out.println("placing "
                            + itemsInHand.getItem().toString()
                            + " on "
                            + pos.toShortString()
                            + w.getBlockState(pos.down()).getBlock().toString()
                    );
                    assert MinecraftClient.getInstance().interactionManager != null;
                    ActionResult result =
                            MinecraftClient
                                    .getInstance()
                                    .interactionManager
                                    .interactBlock(
                                            p,
                                            Hand.MAIN_HAND,
                                            blockHitResult);
                    if (result.isAccepted()) {
                        playerManager.minusOneInHand();
                        System.out.println("✅ block placed");
                        return;
                    } else {
                        System.out.println("❌ failed to set block");
                    }
                }
    }


    boolean isSpawnproofer(ItemStack itemStack) {
//        if (world.getRegistryKey() == World.NETHER) {
        return (!itemStack.isEmpty()
                && netherSpawnproofItems.contains(itemStack.getItem())
        );
    }
}
