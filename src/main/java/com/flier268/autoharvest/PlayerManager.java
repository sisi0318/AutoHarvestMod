package com.flier268.autoharvest;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public class PlayerManager {
    private final Configuration configuration;
    private ClientPlayerEntity player;
    private ItemStack lastUsedItem = ItemStack.EMPTY;

    public PlayerManager(Configuration configuration, ClientPlayerEntity player) {
        this.player = player;
        this.configuration = configuration;
    }

    ClientPlayerEntity getPlayer() {
        return player;
    }

    void setPlayer(ClientPlayerEntity player) {
        this.player = player;
        this.reset();
    }

    ItemStack getLastUsedItem() {
        return lastUsedItem;
    }

    void setLastUsedItem(ItemStack lastUsedItem) {
        this.lastUsedItem = lastUsedItem;
    }

    void minusOneInHand() {
        ItemStack st = player.getMainHandStack();
        if (st != null) {
            if (st.getCount() <= 1) {
                player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            } else {
                st.setCount(st.getCount() - 1);
                setLastUsedItem(st);
            }
        }
    }

    ItemStack tryFillItemInHand() {
        ItemStack itemStack = player.getMainHandStack();
        if (itemStack.isEmpty()) {
            if (!getLastUsedItem().isEmpty()) {
                DefaultedList<ItemStack> inv = player.getInventory().main;
                for (int idx = 0; idx < 36; ++idx) {
                    ItemStack s = inv.get(idx);
                    if (s.getItem() == lastUsedItem.getItem() &&
                            s.getDamage() == lastUsedItem.getDamage() &&
                            !s.hasNbt()) {
                        AutoHarvest.instance.taskManager.Add_MoveItem(idx, player.getInventory().selectedSlot);
                        return s;
                    }
                }
            }
            return null;
        } else {
            return itemStack;
        }
    }

    /**
     * @return -1: does't have rod; 0: no change; change
     **/
    int tryReplacingFishingRod() {
        ItemStack itemStack = player.getMainHandStack();
        if (CropManager.isRod(itemStack)
                && (!configuration.keepFishingRodAlive.value || itemStack.getMaxDamage() - itemStack.getDamage() > 1)) {
            return 0;
        } else {
            DefaultedList<ItemStack> inv = player.getInventory().main;
            for (int idx = 0; idx < 36; ++idx) {
                ItemStack s = inv.get(idx);
                if (CropManager.isRod(s)
                        && (!configuration.keepFishingRodAlive.value || s.getMaxDamage() - s.getDamage() > 1)) {
                    AutoHarvest.instance.taskManager.Add_MoveItem(idx, player.getInventory().selectedSlot);
                    return 1;
                }
            }
            return -1;
        }
    }

    void reset() {
        setLastUsedItem(ItemStack.EMPTY);
    }
}
