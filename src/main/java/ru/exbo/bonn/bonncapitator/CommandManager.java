package ru.exbo.bonn.bonncapitator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CommandManager {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("shufflebag")
                .then(ShuffleBagState.register())
                .then(ShuffleBagReset.register())
                .then(ShuffleBagRoll.register())
                .then(ShuffleBagReload.register()));
    }

    private static class ShuffleBagState {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("state")
                    .then(Commands.argument("id", StringArgumentType.string())
                            .executes(context ->
                                    execute(
                                            context,
                                            StringArgumentType.getString(context, "id")
                                    )
                            ));
        }

        private static int execute(CommandContext<CommandSourceStack> context, String id) {
            Player player = context.getSource().getPlayer();
            assert player != null;

            SaveManager sm = new SaveManager();
            int result = sm.getCurrentAttempt(player.getEncodeId(), id);

            player.displayClientMessage(Component.literal(String.valueOf(result)), true);
            return 0;
        }
    }

    private static class ShuffleBagReset {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("reset")
                    .then(Commands.argument("id", StringArgumentType.string())
                            .executes(context ->
                                    execute(
                                            context,
                                            StringArgumentType.getString(context, "id")
                                    )
                            ));
        }

        private static int execute(CommandContext<CommandSourceStack> context, String id) {
            Player player = context.getSource().getPlayer();
            assert player != null;

            SaveManager sm = new SaveManager();
            sm.resetShuffleBag(player.getEncodeId(), id);

            player.displayClientMessage(Component.literal("Done!"), true);
            return 0;
        }
    }

    private static class ShuffleBagRoll {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("roll")
                    .then(Commands.argument("id", StringArgumentType.string())
                            .executes(context ->
                                    execute(
                                            context,
                                            StringArgumentType.getString(context, "id")
                                    )
                            ));
        }

        private static int execute(CommandContext<CommandSourceStack> context, String id) {
            Player player = context.getSource().getPlayer();
            assert player != null;

            Casino.Stack loot = Casino.getRandomLoot(player.getEncodeId(), id);
            ItemStack stack = new ItemStack(BonnCapitator.getLoot(loot.id()), loot.stackSize());

            player.getInventory().add(stack);

            player.displayClientMessage(Component.literal("Done!"), true);
            return 0;
        }
    }

    private static class ShuffleBagReload {
        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("reload")
                    .executes(ShuffleBagReload::execute);
        }

        private static int execute(CommandContext<CommandSourceStack> context) {
            Player player = context.getSource().getPlayer();
            assert player != null;

            ConfigManager.loadConfig();

            player.displayClientMessage(Component.literal("Done!"), true);
            return 0;
        }
    }
}
