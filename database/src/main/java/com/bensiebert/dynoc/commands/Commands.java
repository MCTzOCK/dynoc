package com.bensiebert.dynoc.commands;

import org.reflections.Reflections;

import java.util.HashMap;

public class Commands {

    public static HashMap<String, Command> commands = new HashMap<>();

    public static void registerCommands() {
        // using reflection to register all commands
        Reflections reflections = new Reflections("com.bensiebert.dynoc.commands");
        for(Class<? extends Command> command : reflections.getSubTypesOf(Command.class)) {
            try {
                Command cmd = command.newInstance();
                commands.put(cmd.getCommand(), cmd);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
