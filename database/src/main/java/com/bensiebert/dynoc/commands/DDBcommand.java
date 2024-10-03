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
                            Class type = Class.forName(node.get("type").asText());
                            Object value = mapper.treeToValue(node.get("value"), type);
                            doc.put(name, value);
                        } catch (ClassNotFoundException | JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    col.addDocument(doc);
                } catch (Exception e) {
                    throw new CommandException("Invalid JSON.");
                }
                return new Response(Response.ResponseType.SUCCESS, "Document added.");
            }
            case "get-doc": {
                if (!proc.user.hasPermission(Permission.READ)) {
                    throw new CommandException("You do not have permission to read from this database.");
                }
                String colName = args[2];
                String id = args[3];
                Collection col = ddb.getCollection(colName);
                if (col == null) {
                    throw new CommandException("Collection not found.");
                }
                Document doc = col.getDocument(id);
                if (doc == null) {
                    throw new CommandException("Document not found.");
                }
                return new Response(Response.ResponseType.SUCCESS, doc.data);
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
                            Class type = Class.forName(node.get("type").asText());
                            Object value = mapper.treeToValue(node.get("value"), type);
                            doc.put(name, value);
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
        }
        return null;
    }

    @Override
    public String getUsage() {
        /*
         * Samples:
         * ddb create-col test
         * ddb add-doc test {'key': {'value': 'value', 'type': 'java.lang.String'}}
         */
        String r = "Usage: ddb <db>\n";
        r += "\tcreate-col <name>\n";
        r += "\tdelete-col <name>\n";
        r += "\tlist-cols\n";
        r += "\tadd-doc <col> <json>\n";
        r += "\tget-doc <col> <id>\n";
        r += "\tlist-docs <col>\n";
        r += "\tupdate-doc <col> <id> <json>\n";
        r += "\tdelete-doc <col> <id>\n";
        return r;
    }

    @Override
    public String getDescription() {
        return "Manages Document oriented databases.";
    }
}
