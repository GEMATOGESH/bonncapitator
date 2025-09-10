package ru.exbo.bonn.bonncapitator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaybeATree {
    // Может дерево, а может и нет

    private final Set<BlockPos> Logs;
    private final Set<BlockPos> Leaves;
    private static final Vec3i[] SEARCH_BOX = {
        new Vec3i(-1, -1, -1),
        new Vec3i(-1, -1, 0),
        new Vec3i(-1, -1, 1),
        new Vec3i(0, -1, -1),
        new Vec3i(0, -1, 0),
        new Vec3i(0, -1, 1),
        new Vec3i(1, -1, -1),
        new Vec3i(1, -1, 0),
        new Vec3i(1, -1, 1),
        new Vec3i(-1, 0, -1),
        new Vec3i(-1, 0, 0),
        new Vec3i(-1, 0, 1),
        new Vec3i(0, 0, -1),
        new Vec3i(0, 0, 1),
        new Vec3i(1, 0, -1),
        new Vec3i(1, 0, 0),
        new Vec3i(1, 0, 1),
        new Vec3i(-1, 1, -1),
        new Vec3i(-1, 1, 0),
        new Vec3i(-1, 1, 1),
        new Vec3i(0, 1, -1),
        new Vec3i(0, 1, 0),
        new Vec3i(0, 1, 1),
        new Vec3i(1, 1, -1),
        new Vec3i(1, 1, 0),
        new Vec3i(1, 1, 1)
    };

    public void addLog(BlockPos log) {
        Logs.add(log);
    }

    public void addLeaf(BlockPos leaf) {
        Leaves.add(leaf);
    }

    public Boolean isATree() {
        return !Logs.isEmpty() && !Leaves.isEmpty();
    }

    public int getTreeHeight() {
        if (!Logs.isEmpty()) {
            return getMaxY();
        }

        return 0;
    }

    public MaybeATree(Level lvl, BlockPos blockPos) {
        Logs = new HashSet<>();
        Leaves = new HashSet<>();

        breadthTreeFinder(lvl, blockPos);
    }

    private void breadthTreeFinder(Level lvl, BlockPos blockPos) {
        LinkedHashSet<BlockPos> queue = new LinkedHashSet<>();
        queue.add(blockPos);

        while (!queue.isEmpty()) {
            for (Vec3i relative_position : SEARCH_BOX) {
                BlockPos blockToCheckPos = queue.getFirst().offset(relative_position);

                // Чтобы не дублировать объекты
                if (Logs.contains(blockToCheckPos) || Leaves.contains(blockToCheckPos)) {
                    continue;
                }

                Block blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

                if (BonnCapitator.isLog(BonnCapitator.getBlockName(blockToCheck))) {
                    addLog(blockToCheckPos);
                    queue.add(blockToCheckPos);
                }
                if (BonnCapitator.isLeaf(BonnCapitator.getBlockName(blockToCheck))) {
                    addLeaf(blockToCheckPos);
                    queue.add(blockToCheckPos);
                }
            }

            queue.removeFirst();
        }
    }

    private void depthTreeFinder(Level lvl, BlockPos blockPos) {
        for (Vec3i relative_position : SEARCH_BOX) {
            BlockPos blockToCheckPos = blockPos.offset(relative_position);

            // Чтобы не дублировать объекты
            if (Logs.contains(blockToCheckPos) || Leaves.contains(blockToCheckPos)) {
                continue;
            }

            Block blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

            if (BonnCapitator.isLog(BonnCapitator.getBlockName(blockToCheck))) {
                addLog(blockToCheckPos);
                depthTreeFinder(lvl, blockToCheckPos);
            }
            if (BonnCapitator.isLeaf(BonnCapitator.getBlockName(blockToCheck))) {
                addLeaf(blockToCheckPos);
                depthTreeFinder(lvl, blockToCheckPos);
            }
        }
    }

    public void breakATree(ItemStack mainTool, ItemStack offHandTool, Level lvl) {
        Set<BlockPos> blocksToDestroy = new HashSet<>(Logs);
        if (BonnCapitator.isShears(offHandTool.getItem().toString())) {
            blocksToDestroy.addAll(Leaves);
        }

        for (Vec3i block : blocksToDestroy) {
            BlockPos blockToCheckPos = new BlockPos(block.getX(), block.getY(), block.getZ());
            Block blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

            ItemStack bufTool = mainTool;

            if (!BonnCapitator.isLog(BonnCapitator.getBlockName(blockToCheck))) {
                bufTool = offHandTool;

                // Спасибо Даня за прекрасные тзшки
                BlockState state = lvl.getBlockState(blockToCheckPos);
                BlockEntity entity= lvl.getBlockEntity(blockToCheckPos);
                Block.dropResources(state, lvl, blockToCheckPos, entity, null, offHandTool);
            }

            if (bufTool.getDamageValue() + 1 > bufTool.getMaxDamage()) {
                continue;
            }

            lvl.destroyBlock(blockToCheckPos, true);

            bufTool.setDamageValue(bufTool.getDamageValue() + 1);
        }
    }

    private int getMaxY() {
        // -64 минимальная высота в версии 1.21, а моды жоско привязаны
        int maxY = -65;

        var optional = Logs.stream().findFirst();
        if (optional.isPresent()) {
            maxY = optional.get().getY();
        }

        for (Vec3i block : Logs) {
            if (block.getY() > maxY) {
                maxY = block.getY();
            }
        }

        return maxY;
    }
}
