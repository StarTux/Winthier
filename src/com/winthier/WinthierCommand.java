package com.winthier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class WinthierCommand extends Command {
        private final Component instance;
        private final Method method;

        public WinthierCommand(String name, Method method, Component instance) {
                super(name);
                this.method = method;
                this.instance = instance;
        }

        public boolean execute(CommandSender sender, String label, String args[]) {
                if (!instance.isEnabled()) {
                        sender.sendMessage("Unknown command. Type \"help\" for help.");
                        return false;
                }
                if (!testPermission(sender)) {
                        return false;
                }
                boolean result = false;
                try {
                        result = (Boolean)method.invoke(instance, sender, this, label, args);
                } catch (Exception e) {
                        sender.sendMessage("" + ChatColor.RED + "An error occured while executing this command. See console.");
                        e.printStackTrace();
                        return false;
                }
                if (!result) {
                        sender.sendMessage("Usage: " + getUsage().replaceAll("<command>", label));
                }
                return false;
        }

        private static Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
                Class<?> clazz = object.getClass();
                Field objectField = clazz.getDeclaredField(field);
                objectField.setAccessible(true);
                Object result = objectField.get(object);
                objectField.setAccessible(false);
                return result;
        }

        public static SimpleCommandMap getCommandMap() {
                try {
                        Object result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
                        SimpleCommandMap commandMap = (SimpleCommandMap)result;
                        return commandMap;
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }

        private static boolean checkMethod(Method method) {
                if (method.getReturnType() != boolean.class) return false;
                Class params[] = method.getParameterTypes();
                if (params.length != 4) return false;
                if (params[0] != CommandSender.class) return false;
                if (params[1] != Command.class) return false;
                if (params[2] != String.class) return false;
                if (params[3] != String[].class) return false;
                return true;
        }

        public static void registerCommands(Component o) {
                for (Method method : o.getClass().getMethods()) {
                        CommandHandler cmdh = method.getAnnotation(CommandHandler.class);
                        if (cmdh == null) continue;
                        if (!checkMethod(method)) {
                                System.err.println("[Winthier] Method " + o.getClass().getName() + "." + method.getName() + " with CommandHandler annotation has bad signature");
                                continue;
                        }
                        String name = cmdh.name();
                        if (name.length() == 0) name = method.getName();
                        WinthierCommand cmd = new WinthierCommand(name, method, o);
                        cmd.setDescription(cmdh.description());
                        cmd.setUsage(cmdh.usage());
                        cmd.setAliases(Arrays.asList(cmdh.aliases()));
                        if (cmdh.permissionMessage().length() > 0) {
                                cmd.setPermissionMessage(cmdh.permissionMessage());
                        } else {
                                cmd.setPermissionMessage("" + ChatColor.RED + "You don't have permission!");

                        }
                        if (cmdh.permission().length() > 0) {
                                cmd.setPermission(cmdh.permission());
                                if (Bukkit.getServer().getPluginManager().getPermission(cmdh.permission()) == null) {
                                        try {
                                                PermissionDefault pd;
                                                if (cmdh.permissionDefault().length() == 0) {
                                                        pd = PermissionDefault.FALSE;
                                                } else {
                                                        pd = PermissionDefault.valueOf(cmdh.permissionDefault().toUpperCase());
                                                }
                                                Bukkit.getServer().getPluginManager().addPermission(new Permission(cmdh.permission(), pd));
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                        if (!getCommandMap().register("winthier", cmd)) {
                                System.err.println("[Winthier] Failed to register command " + cmd.getName());
                        } else {
                                System.out.println("[Winthier] Registered command " + cmd.getName());
                        }
                }
        }
}
