package ru.exbo.bonn.bonncapitator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MaybeATree {
    // Может дерево, а может и нет

    private final List<BlockPos> Logs;
    private final List<BlockPos> Leaves;
    private static final Vec3i[] SEARCH_BOX = {
        new Vec3i(-1, -1, -1),
        new Vec3i(-1, -1, 0),
        new Vec3i(-1, -1, 1),
        new Vec3i(-1, 0, -1),
        new Vec3i(-1, 0, 0),
        new Vec3i(-1, 0, 1),
        new Vec3i(-1, 1, -1),
        new Vec3i(-1, 1, 0),
        new Vec3i(-1, 1, 1),
        new Vec3i(0, -1, -1),
        new Vec3i(0, -1, 0),
        new Vec3i(0, -1, 1),
        new Vec3i(0, 0, -1),
        new Vec3i(0, 0, 1),
        new Vec3i(0, 1, -1),
        new Vec3i(0, 1, 0),
        new Vec3i(0, 1, 1),
        new Vec3i(1, -1, -1),
        new Vec3i(1, -1, 0),
        new Vec3i(1, -1, 1),
        new Vec3i(1, 0, -1),
        new Vec3i(1, 0, 0),
        new Vec3i(1, 0, 1),
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

    public List<BlockPos> getLogs() {
        return Logs;
    }

    public List<BlockPos> getLeaves() {
        return Leaves;
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
        this.Logs = new ArrayList<>();
        this.Leaves = new ArrayList<>();

        this.recursiveTreeFinder(lvl, blockPos);
    }

    private void recursiveTreeFinder(Level lvl, BlockPos blockPos) {
        for (Vec3i relative_position : SEARCH_BOX) {
            BlockPos blockToCheckPos = blockPos.offset(relative_position);

            // Чтобы не дублировать объекты - костылим
            if (this.Logs.contains(blockToCheckPos) || this.Leaves.contains(blockToCheckPos)) {
                continue;
            }

            Block blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

            if (BonnCapitator.isLog(BonnCapitator.getBlockName(blockToCheck))) {
                this.addLog(blockToCheckPos);
                recursiveTreeFinder(lvl, blockToCheckPos);
            }
            if (BonnCapitator.isLeaf(BonnCapitator.getBlockName(blockToCheck))) {
                this.addLeaf(blockToCheckPos);
                recursiveTreeFinder(lvl, blockToCheckPos);
            }
        }
    }

    public void breakATree(ItemStack mainTool, ItemStack offHandTool, Level lvl) {
        List<BlockPos> blocksToDestroy = new ArrayList<>(this.Logs);
        if (BonnCapitator.isShears(offHandTool.getItem().toString())) {
            blocksToDestroy.addAll(this.Leaves);
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
        int maxY = this.Logs.getFirst().getY();
        for (Vec3i block : this.Logs) {
            if (block.getY() > maxY) {
                maxY = block.getY();
            }
        }

        return maxY;
    }
}
