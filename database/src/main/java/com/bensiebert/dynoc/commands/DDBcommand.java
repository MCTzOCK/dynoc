package com.bensiebert.dynoc.commands;

import com.bensiebert.dynoc.auth.Permission;
import com.bensiebert.dynoc.server.Connection;
import com.bensiebert.dynoc.server.Response;
import com.bensiebert.dynoc.storage.DBManager;
import com.bensiebert.dynoc.storage.Database;
import com.bensiebert.dynoc.storage.DocumentDatabase;
import com.bensiebert.dynoc.storage.ddb.Collection;
import com.bensiebert.dynoc.storage.ddb.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;

public class DDBcommand implements Command {

    @Override
    public String getCommand() {
        return "ddb";
    }

    @Override
    public Response execute(Connection proc, String[] args) throws CommandException {
        if(proc.user == null){
            throw new CommandException("You must be logged in to use this command.");
        }
        if(args.length < 2){
            throw new CommandException("Invalid number of arguments. Usage: ddb <db> [command]");
        }
        Database db = DBManager.getDatabase(args[0]);
        if(db == null){
            throw new CommandException("Database not found.");
        }
        if((!(db instanceof DocumentDatabase))) {
            throw new CommandException("Database is not a document database.");
        }
        DocumentDatabase ddb = (DocumentDatabase) db;

        String subCommand = args[1];

        switch(subCommand) {
            case "create-col": {
                if (!proc.user.hasPermission(Permission.CREATE)) {
                    throw new CommandException("You do not have permission to create collections.");
                }
                String name = args[2];
                Collection col = new Collection(name);
                if (!ddb.addCollection(col)) {
                    throw new CommandException("Collection already exists.");
                }
                ddb.addCollection(col);
                return new Response(Response.ResponseType.SUCCESS, "Collection created.");
            }
            case "delete-col": {
                if (!proc.user.hasPermission(Permission.DELETE)) {
                    throw new CommandException("You do not have permission to delete collections.");
                }
                String name = args[2];
                if (!ddb.removeCollection(name)) {
                    throw new CommandException("Collection not found.");
                }
                return new Response(Response.ResponseType.SUCCESS, "Collection deleted.");
            }
            case "list-cols": {
                if (!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to list collections.");
                }
                String[] cols = ddb.getCollections().stream().map((c) -> c.name).toArray(String[]::new);
                return new Response(Response.ResponseType.SUCCESS, cols);
            }
            case "add-doc": {
                if (!proc.user.hasPermission(Permission.WRITE)) {
                    throw new CommandException("You do not have permission to write to this database.");
                }
                String colName = args[2];
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                String json = Arrays.stream(args).skip(3).reduce((a, b) -> a + " " + b).orElse("");
                json = json.replaceAll("'", "\"");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode root = mapper.readTree(json);
                    Document doc = new Document(col);
                    root.fields().forEachRemaining(entry -> {
                        String name = entry.getKey();
                        JsonNode node = entry.getValue();
                        try {
                            if(node.get("type").asText().equals("#relation")) {
                                String foreignId = node.get("value").asText();
                                Document foreign = ddb.getDocument(foreignId);
                                if(foreign == null) {
                                    return;
                                }
                                doc.put(name, "$" + foreign.getID());
                            } else {
                                Class type = Class.forName(node.get("type").asText());
                                Object value = mapper.treeToValue(node.get("value"), type);
                                doc.put(name, value);
                            }
                        } catch (ClassNotFoundException | JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    col.addDocument(doc);
                } catch (Exception e) {
                    throw new CommandException("Invalid JSON: " + e.getMessage() + "\n" + json);
                }
                return new Response(Response.ResponseType.SUCCESS, "Document added.");
            }
            case "get-doc": {
                if (!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from this database.");
                }
                String colName = args[2];
                String id = args[3];
                boolean populate = false;
                if(args.length > 4) {
                    populate = Boolean.parseBoolean(args[4]);
                }
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                Document doc = col.getDocument(id);
                if (doc == null) {
                    throw new CommandException("Document not found.");
                }
                HashMap<String, Object> data = new HashMap<>(doc.data);
                if(populate) {
                    data.forEach((key, value) -> {
                        if(value instanceof String && ((String) value).startsWith("$")) {
                            String foreignId = ((String) value).substring(1);
                            Document foreign = ddb.getDocument(foreignId);
                            if(foreign != null) {
                                data.put(key, foreign.data);
                            }
                        }
                    });
                }
                return new Response(Response.ResponseType.SUCCESS, data);
            }
            case "list-docs": {
                if (!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from this database.");
                }
                String colName = args[2];
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                return new Response(Response.ResponseType.SUCCESS, col.getDocuments());
            }
            case "delete-doc": {
                if (!proc.user.hasPermission(Permission.DELETE)) {
                    throw new CommandException("You do not have permission to delete from this database.");
                }
                String colName = args[2];
                String id = args[3];
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                col.removeDocument(id);
                return new Response(Response.ResponseType.SUCCESS, "Document deleted.");
            }
            case "update-doc": {
                if (!proc.user.hasPermission(Permission.WRITE)) {
                    throw new CommandException("You do not have permission to write to this database.");
                }
                String colName = args[2];
                String id = args[3];
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                String json = Arrays.stream(args).skip(4).reduce((a, b) -> a + " " + b).orElse("");
                json = json.replaceAll("'", "\"");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode root = mapper.readTree(json);
                    Document doc = col.getDocument(id);
                    if (doc == null) {
                        throw new CommandException("Document not found.");
                    }
                    root.fields().forEachRemaining(entry -> {
                        String name = entry.getKey();
                        if(name.startsWith("#")) {
                            return;
                        }
                        JsonNode node = entry.getValue();
                        try {
                            if(node.get("type").asText().equals("#relation")) {
                                String foreignId = node.get("value").asText();
                                Document foreign = ddb.getDocument(foreignId);
                                if(foreign == null) {
                                    return;
                                }
                                doc.put(name, "$" + foreign.getID());
                            } else {
                                Class type = Class.forName(node.get("type").asText());
                                Object value = mapper.treeToValue(node.get("value"), type);
                                doc.put(name, value);
                            }
                        } catch (ClassNotFoundException | JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    doc.put("#updated", System.currentTimeMillis());
                    doc.put("#version", (Integer) doc.data.get("#version") + 1);
                } catch (Exception e) {
                    throw new CommandException("Invalid JSON.");
                }
                return new Response(Response.ResponseType.SUCCESS, "Document updated.");
            }
            case "find": {
                if (!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from this database.");
                }
                try {
                    String colName = args[2];
                    String key = args[3];
                    String operator = args[4];
                    String value = args[5];
                    Collection col = ddb.getCollection(colName);
                    if (col == null) {
                        throw new CommandException("Collection not found.");
                    }

                    switch(operator) {
                        case "eq": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryEquals(col, key, value));
                        }
                        case "ne": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryNotEquals(col, key, value));
                        }
                        case "contains": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryContains(col, key, value));
                        }
                        case "starts": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryStartsWith(col, key, value));
                        }
                        case "ends": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryEndsWith(col, key, value));
                        }
                        case "matches": {
                            return new Response(Response.ResponseType.SUCCESS, ddb.queryMatches(col, key, value));
                        }
                    }
                } catch (Exception ex) {
                    throw new CommandException(ex.getMessage());
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public String getUsage() {
        /*
         * Samples:
         * ddb create-col test
         * ddb add-doc test {'key': {'value': 'value', 'type': 'java.lang.String'}}
         * ddb find test key eq value
         * ddb find test key contains value
         */
        String r = "Usage: ddb <db>\n";
        r += "\tcreate-col <name>\n";
        r += "\tdelete-col <name>\n";
        r += "\tlist-cols\n";
        r += "\tadd-doc <col> <json>\n";
        r += "\tget-doc <col> <id> <populate(true|false)>\n";
        r += "\tlist-docs <col>\n";
        r += "\tupdate-doc <col> <id> <json>\n";
        r += "\tdelete-doc <col> <id>\n";
        r += "\tfind <col> <key> <operator> <value>\n";
        return r;
    }

    @Override
    public String getDescription() {
        return "Manages Document oriented databases.";
    }
}
