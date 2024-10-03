package com.bensiebert.dynoc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedCommand {

    public String command;
    public String[] args;

    public static ParsedCommand parse(String command) {
        ParsedCommand pc = new ParsedCommand();

        String[] args = parseArguments(command);

        pc.command = args[0];
        pc.args = Arrays.copyOfRange(args, 1, args.length);

        return pc;
    }

    private static String[] parseArguments(String input) {
        // Regular expression to handle quoted strings and separate arguments
        List<String> arguments = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                arguments.add(matcher.group(1));
            } else {
                arguments.add(matcher.group(2));
            }
        }
        return arguments.toArray(new String[0]);
    }
}
