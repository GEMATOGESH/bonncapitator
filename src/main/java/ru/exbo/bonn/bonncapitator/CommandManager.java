package ru.exbo.bonn.bonncapitator;

import com.mojang.brigadier.Command;
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

            if (player.getCapability(SaveManagerProvider.CASINO_SAVE).isPresent() && player.getCapability(SaveManagerProvider.CASINO_SAVE).resolve().isPresent()) {
                SaveManager sm = player.getCapability(SaveManagerProvider.CASINO_SAVE).resolve().get();
                int result = sm.getCurrentAttempt(player.getStringUUID(), id);

                context.getSource().getPlayer().sendSystemMessage(Component.literal(Integer.toString(result)));
            }
            return Command.SINGLE_SUCCESS;
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

            if (player.getCapability(SaveManagerProvider.CASINO_SAVE).isPresent() && player.getCapability(SaveManagerProvider.CASINO_SAVE).resolve().isPresent()) {
                SaveManager sm = player.getCapability(SaveManagerProvider.CASINO_SAVE).resolve().get();
                sm.resetShuffleBag(player.getStringUUID(), id);

                context.getSource().getPlayer().sendSystemMessage(Component.literal("Done!"));
            }
            return Command.SINGLE_SUCCESS;
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

            Casino.Stack loot = Casino.getRandomLoot(player, id);
            ItemStack stack = new ItemStack(BonnCapitator.getLoot(loot.id()), loot.stackSize());

            player.getInventory().add(stack);

            context.getSource().getPlayer().sendSystemMessage(Component.literal("Done!"));
            return Command.SINGLE_SUCCESS;
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

            context.getSource().getPlayer().sendSystemMessage(Component.literal("Done!"));
            return Command.SINGLE_SUCCESS;
        }
    }
}