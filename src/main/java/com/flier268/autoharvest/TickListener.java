package com.flier268.autoharvest;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;

public class TickListener {
    private final Configure configure;
    private ClientPlayerEntity p;

    private long fishBitesAt = 0L;
    private ItemStack lastUsedItem = null;

    public TickListener(Configure configure, ClientPlayerEntity player) {
        this.configure = configure;
        this.p = player;
        ClientTickEvents.END_CLIENT_TICK.register(e ->
        {
            if (AutoHarvest.instance.Switch)
                onTick(e.player);
        });
    }

    public void Reset() {
        lastUsedItem = null;
        fishBitesAt = 0L;
    }

    public void onTick(ClientPlayerEntity player) {
        try {
            if (player != p) {
                this.p = player;
                AutoHarvest.instance.Switch = false;
                AutoHarvest.msg("notify.turn.off");
                return;
            }
            if (AutoHarvest.instance.taskManager.Count() > 0) {
                AutoHarvest.instance.taskManager.RunATask();
                return;
            }
            switch (AutoHarvest.instance.mode) {
                case SEED:
                    seedTick();
                    break;
                case HARVEST:
                    harvestTick();
                    break;
                case PLANT:
                    plantTick();
                    break;
                case Farmer:
                    harvestTick();
                    plantTick();
                    break;
                case FEED:
                    feedTick();
                    break;
                case FISHING:
                    fishingTick();
                    break;
                case BONEMEALING:
                    bonemealingTick();
                    break;
            }
            if (AutoHarvest.instance.mode != AutoHarvest.HarvestMode.FISHING)
                AutoHarvest.instance.taskManager.Add_TickSkip(AutoHarvest.instance.configure.tickSkip.value);
        } catch (Exception ex) {
            AutoHarvest.msg("notify.tick_error");
            AutoHarvest.msg("notify.turn.off");
            ex.printStackTrace();
            AutoHarvest.instance.Switch = false;
        }
    }

    /* clear all grass on land */
    private void seedTick() {
        World w = p.getEntityWorld();
        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY());//the "leg block"
        int Z = (int) Math.floor(p.getZ());
        for (int deltaY = 3; deltaY >= -2; --deltaY)
            for (int deltaX = -configure.effect_radius.value; deltaX <= configure.effect_radius.value; ++deltaX)
                for (int deltaZ = -configure.effect_radius.value; deltaZ <= configure.effect_radius.value; ++deltaZ) {
                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);
                    if (CropManager.isWeedBlock(w, pos) || (AutoHarvest.instance.configure.flowerISseed.value && CropManager.isFlowerBlock(w, pos))) {
                        MinecraftClient.getInstance().interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
    }

    /* harvest all mature crops */
    private void harvestTick() {
        World w = p.getEntityWorld();
        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY() + 0.2D);//the "leg block", in case in soul sand
        int Z = (int) Math.floor(p.getZ());
        for (int deltaX = -configure.effect_radius.value; deltaX <= configure.effect_radius.value; ++deltaX)
            for (int deltaZ = -configure.effect_radius.value; deltaZ <= configure.effect_radius.value; ++deltaZ) {
                for (int deltaY = -1; deltaY <= 1; ++deltaY) {
                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);
                    BlockState state = w.getBlockState(pos);
                    Block b = state.getBlock();
                    if (CropManager.isCropMature(w, pos, state, b)) {
                        if (b == Blocks.SWEET_BERRY_BUSH) {
                            BlockPos downPos = pos.down();
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y, Z + deltaZ + 0.5), Direction.UP, downPos, false);
                            ActionResult a = MinecraftClient.getInstance().interactionManager.interactBlock(p, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            String ass = "";
                        } else
                            MinecraftClient.getInstance().interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
            }
    }

    private void minusOneInHand() {
        ItemStack st = p.getMainHandStack();
        if (st != null) {
            if (st.getCount() <= 1) {
                p.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            } else {
                st.setCount(st.getCount() - 1);
            }
        }
    }

    private ItemStack tryFillItemInHand() {
        ItemStack itemStack = p.getMainHandStack();
        if (itemStack.isEmpty()) {
            int supplmentIdx = -1;
            ItemStack stack = null;
            if (lastUsedItem != null && !lastUsedItem.isEmpty()) {
                DefaultedList<ItemStack> inv = p.inventory.main;
                for (int idx = 0; idx < 36; ++idx) {
                    ItemStack s = inv.get(idx);
                    if (s.getItem() == lastUsedItem.getItem() &&
                            s.getDamage() == lastUsedItem.getDamage() &&
                            !s.hasTag()) {
                        supplmentIdx = idx;
                        stack = s;
                        break;
                    }
                }
            } else {
                return null;
            }
            if (supplmentIdx < 0) {
                return null;
            }
            AutoHarvest.instance.taskManager.Add_MoveItem(supplmentIdx, p.inventory.selectedSlot);
            return null;
        } else {
            return itemStack;
        }
    }
    /**
     * @return -1: does't have rod; 0: no change; change
     **/
    private int tryReplacingFishingRod() {
        ItemStack itemStack = p.getMainHandStack();
        if (CropManager.isRod(itemStack) && (configure.keepFishingRodAlive.value == false || itemStack.getMaxDamage() - itemStack.getDamage() > 1)) {
            return 0;
        } else {
            DefaultedList<ItemStack> inv = p.inventory.main;
            for (int idx = 0; idx < 36; ++idx) {
                ItemStack s = inv.get(idx);
                if (CropManager.isRod(s) && (configure.keepFishingRodAlive.value == false || s.getMaxDamage() - s.getDamage() > 1)) {
                    AutoHarvest.instance.taskManager.Add_MoveItem(idx, p.inventory.selectedSlot);
                    return 1;
                }
            }
            return -1;
        }
    }
    private void plantTick() {
        ItemStack handItem = tryFillItemInHand();
        if (handItem == null) return;
        if (!CropManager.isSeed(handItem)) {
            if (CropManager.isCocoa(handItem)) {
                plantCocoaTick(handItem);
            }
            return;
        }

        World w = p.getEntityWorld();
        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY() + 0.2D);//the "leg block" , in case in soul sand
        int Z = (int) Math.floor(p.getZ());

        for (int deltaX = -configure.effect_radius.value; deltaX <= configure.effect_radius.value; ++deltaX)
            for (int deltaZ = -configure.effect_radius.value; deltaZ <= configure.effect_radius.value; ++deltaZ) {
                BlockPos pos = new BlockPos(X + deltaX, Y, Z + deltaZ);
                if (w.getBlockState(pos).getBlock() != Blocks.AIR) continue;
                BlockPos downPos = pos.down();
                BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y, Z + deltaZ + 0.5), Direction.UP, downPos, false);
                w.getBlockState(pos.offset(Direction.DOWN)).canPlaceAt(w, pos);
                if (CropManager.canPlantOn(handItem.getItem(), w, pos)) {
                    lastUsedItem = handItem.copy();
                    ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                    minusOneInHand();
                    return;
                }
            }
    }

    private void plantCocoaTick(ItemStack handItem) {
        World w = p.getEntityWorld();
        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY() + 0.2D);//the "leg block" , in case in soul sand
        int Z = (int) Math.floor(p.getZ());

        for (int deltaX = -configure.effect_radius.value; deltaX <= configure.effect_radius.value; ++deltaX) {
            for (int deltaZ = -configure.effect_radius.value; deltaZ <= configure.effect_radius.value; ++deltaZ) {
                for (int deltaY = 0; deltaY <= 7; ++deltaY) {
                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);
                    if (!canReachBlock(p, pos)) continue;
                    BlockState jungleBlock = w.getBlockState(pos);
                    if (CropManager.isJungleLog(jungleBlock)) {
                        BlockPos tmpPos;

                        Direction tmpFace = Direction.EAST;
                        tmpPos = pos.add(tmpFace.getVector());
                        if (w.getBlockState(tmpPos).getBlock() == Blocks.AIR) {
                            lastUsedItem = handItem.copy();
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 1, Y + deltaY + 0.5, Z + deltaZ + 0.5), tmpFace, pos, false);
                            ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            minusOneInHand();
                            return;
                        }

                        tmpFace = Direction.WEST;
                        tmpPos = pos.add(tmpFace.getVector());
                        if (w.getBlockState(tmpPos).getBlock() == Blocks.AIR) {
                            lastUsedItem = handItem.copy();
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX, Y + deltaY + 0.5, Z + deltaZ + 0.5), tmpFace, pos, false);
                            ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            minusOneInHand();
                            return;
                        }

                        tmpFace = Direction.SOUTH;
                        tmpPos = pos.add(tmpFace.getVector());
                        if (w.getBlockState(tmpPos).getBlock() == Blocks.AIR) {
                            lastUsedItem = handItem.copy();
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y + deltaY + 0.5, Z + deltaZ + 1), tmpFace, pos, false);
                            ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            minusOneInHand();
                            return;
                        }

                        tmpFace = Direction.NORTH;
                        tmpPos = pos.add(tmpFace.getVector());
                        if (w.getBlockState(tmpPos).getBlock() == Blocks.AIR) {
                            lastUsedItem = handItem.copy();
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y + deltaY + 0.5, Z + deltaZ), tmpFace, pos, false);
                            ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            minusOneInHand();
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean canReachBlock(ClientPlayerEntity playerEntity, BlockPos blockpos) {
        double d0 = playerEntity.getX() - ((double) blockpos.getX() + 0.5D);
        double d1 = playerEntity.getY() - ((double) blockpos.getY() + 0.5D) + 1.5D;
        double d2 = playerEntity.getZ() - ((double) blockpos.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return d3 <= 36D;
    }

    private void feedTick() {
        ItemStack handItem = tryFillItemInHand();
        if (handItem == null) return;
        Collection<Class<? extends AnimalEntity>> animalList = CropManager.FEED_MAP.get(handItem.getItem());
        Box box = new Box(p.getX() - configure.effect_radius.value, p.getY() - configure.effect_radius.value, p.getZ() - configure.effect_radius.value,
                p.getX() + configure.effect_radius.value, p.getY() + configure.effect_radius.value, p.getZ() + configure.effect_radius.value);
        for (Class<? extends AnimalEntity> type : animalList) {
            for (AnimalEntity e : p.getEntityWorld().getEntitiesByClass(type, box, null)) {
                if (e.getBreedingAge() >= 0 && !e.isInLove()) {
                    lastUsedItem = handItem.copy();
                    ActionResult result = MinecraftClient.getInstance().interactionManager
                            .interactEntity(p, e, Hand.MAIN_HAND);
                }
            }
        }
        if (handItem.getItem() == Items.SHEARS) {
            for (SheepEntity e : p.getEntityWorld().getEntitiesByClass(SheepEntity.class, box, null)) {
                if (!e.isBaby() && !e.isSheared()) {
                    lastUsedItem = handItem.copy();
                    MinecraftClient.getInstance().interactionManager.interactEntity(p, e, Hand.MAIN_HAND);
                    return;
                }
            }
        }
    }

    private long getWorldTime() {
        return MinecraftClient.getInstance().world.getTime();
    }

    private boolean isFishBites(ClientPlayerEntity player) {
        FishingBobberEntity fishEntity = player.fishHook;
        return fishEntity != null && (fishEntity.prevX - fishEntity.getX()) == 0 && (fishEntity.prevZ - fishEntity.getZ()) == 0 && (fishEntity.prevY - fishEntity.getY()) < -0.05d;
    }


    private void fishingTick() {
        switch (tryReplacingFishingRod()) {
            case -1:
                AutoHarvest.msg("notify.turn.off");
                AutoHarvest.instance.Switch = false;
                break;
            case 0:
                /* Reel */
                if (fishBitesAt == 0 && isFishBites(p)) {
                    fishBitesAt = getWorldTime();
                    MinecraftClient.getInstance().interactionManager.interactItem(
                            p,
                            MinecraftClient.getInstance().world,
                            Hand.MAIN_HAND);
                }

                /* Cast */
                if (fishBitesAt != 0 && fishBitesAt + 20 <= getWorldTime()) {
                    MinecraftClient.getInstance().interactionManager.interactItem(
                            p,
                            MinecraftClient.getInstance().world,
                            Hand.MAIN_HAND);
                    fishBitesAt = 0;
                }
                break;
            case 1:
                return;
        }
    }

    /* clear all grass on land */
    private void bonemealingTick() {
        ItemStack handItem = p.getMainHandStack();
        if (handItem == null || !CropManager.isBoneMeal(handItem)) {
            return;
        } else {
            handItem = tryFillItemInHand();
        }

        World w = p.getEntityWorld();
        int X = (int) Math.floor(p.getX());
        int Y = (int) Math.floor(p.getY());//the "leg block"
        int Z = (int) Math.floor(p.getZ());
        for (int deltaY = 3; deltaY >= -2; --deltaY)
            for (int deltaX = -configure.effect_radius.value; deltaX <= configure.effect_radius.value; ++deltaX)
                for (int deltaZ = -configure.effect_radius.value; deltaZ <= configure.effect_radius.value; ++deltaZ) {
                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);
                    BlockState blockState = w.getBlockState(pos);
                    Block block = blockState.getBlock();
                    if (block instanceof CropBlock) {
                        //not Mature
                        if (!((CropBlock) block).isMature(blockState)) {
                            BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(X + deltaX + 0.5, Y, Z + deltaZ + 0.5), Direction.UP, pos, false);
                            lastUsedItem = handItem.copy();
                            ActionResult report = MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);
                            minusOneInHand();
                            return;
                        }
                    }
                }
    }
}
