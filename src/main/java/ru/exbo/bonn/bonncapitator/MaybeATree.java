package ru.exbo.bonn.bonncapitator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
    private int height;
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
            var optional = Logs.stream().findFirst();
            int maxY = optional.get().getY();
            int minY = optional.get().getY();

            for (Vec3i block : Logs) {
                if (block.getY() > maxY) {
                    maxY = block.getY();
                }
                if (block.getY() < minY) {
                    minY = block.getY();
                }
            }

            height = maxY - minY + 1;
        }

        return height;
    }

    public MaybeATree(Level lvl, BlockPos blockPos) {
        Logs = new HashSet<>();
        Leaves = new HashSet<>();
        height = -1;

        breadthTreeFinder(lvl, blockPos);
    }

    private void breadthTreeFinder(Level lvl, BlockPos blockPos) {
        LinkedHashSet<DepthBlock> queue = new LinkedHashSet<>();
        queue.add(new DepthBlock(blockPos, 0));

        while (!queue.isEmpty()) {
            for (Vec3i relative_position : SEARCH_BOX) {
                DepthBlock blockCurr = queue.getFirst();
                BlockPos blockToCheckPos = blockCurr.pos().offset(relative_position);

                // Чтобы не дублировать объекты
                if (Logs.contains(blockToCheckPos) || Leaves.contains(blockToCheckPos)) {
                    continue;
                }

                Block blockToCheck = lvl.getBlockState(blockToCheckPos).getBlock();

                if (BonnCapitator.isLog(BonnCapitator.getBlockName(blockToCheck))) {
                    // Другое бревно добавляем только если оно соседствует с бревном
                    if (blockCurr.depth() == 0) {
                        addLog(blockToCheckPos);
                        queue.add(new DepthBlock(blockToCheckPos, 0));
                    }
                }
                if (BonnCapitator.isLeaf(BonnCapitator.getBlockName(blockToCheck))) {
                    int depth = blockCurr.depth() + 1;

                    if (!BonnCapitator.isLeafTooFar(depth)) {
                        addLeaf(blockToCheckPos);
                        queue.add(new DepthBlock(blockToCheckPos, depth));
                    }
                }
            }

            queue.removeFirst();
        }

        height = getTreeHeight();
    }

    @Deprecated
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

    public void breakATree(Player player, ItemStack mainTool, ItemStack offHandTool, Level lvl) {
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

                // Спасибо Даня за прекрасные задачи
                BlockState state = lvl.getBlockState(blockToCheckPos);
                BlockEntity entity= lvl.getBlockEntity(blockToCheckPos);
                Block.dropResources(state, lvl, blockToCheckPos, entity, null, offHandTool);
            }
            else {
                if (BonnCapitator.isCasinoAllowed(height)) {
                    String logId = BonnCapitator.getBlockName(blockToCheck);

                    if (BonnCapitator.isCasinoWon()) {
                        if (ConfigManager.getShuffleBagName(logId) != null) {
                            String shuffleBagId = ConfigManager.getShuffleBagName(logId);

                            Casino.Stack loot = Casino.getRandomLoot(player.getStringUUID(), shuffleBagId);
                            ItemStack stack = new ItemStack(BonnCapitator.getLoot(loot.id()), loot.stackSize());

                            ItemEntity entity = new ItemEntity(lvl, block.getX(), block.getY(), block.getZ(), stack);
                            lvl.addFreshEntity(entity);
                        }
                    }
                }
            }

            if (bufTool.getDamageValue() + 1 >= bufTool.getMaxDamage() ) {
                continue;
            }

            lvl.destroyBlock(blockToCheckPos, true);

            bufTool.setDamageValue(bufTool.getDamageValue() + 1);
        }
    }

    private record DepthBlock(BlockPos pos, int depth) { }
}
