package com.flier268.autoharvest;


import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.SlotActionType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskManager {
    private ArrayList<String> taskList = new ArrayList();

    public void Add(int slotNumber, int currentHotbarSlot) {
        taskList.add(String.format("moveitem,%d,%d", slotNumber, currentHotbarSlot));
    }

    public int Count() {
        return taskList.size();
    }

    public void Clean() {
        taskList.clear();
    }

    public boolean Remove(int index) {
        return taskList.remove(index) != null;
    }

    public void RunATask() {
        if (taskList.size() == 0)
            return;
        String pattern = "(.*?),(.*?),(.*)";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(taskList.get(0));
        if (m.find()) {
            switch (m.group(1)) {
                case "moveitem":
                    MinecraftClient mc = MinecraftClient.getInstance();
                    Container container = mc.player.playerContainer;
                    mc.interactionManager.clickSlot(container.syncId, Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), SlotActionType.SWAP, mc.player);
                    taskList.remove(0);
                    break;
            }
        }

    }
}
