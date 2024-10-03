package com.bensiebert.dynoc.config;

import com.bensiebert.dynoc.logging.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class Config {

    public static Properties props = new Properties();

    public static void loadConfig() {
        File f = new File(System.getProperty("user.dir") + "/dynoc.props");
        if(!f.exists()) {
            FileWriter fw = null;
            try {
                Logger.info("Creating default config file at " + f.getAbsolutePath());
                Logger.info("Default credentials: admin:password");
                fw = new FileWriter(f);
                fw.write("port=8000\n");
                fw.write("http.enabled=true\n");
                fw.write("http.port=8001\n");
                fw.write("users=admin\n");
                fw.write("users.admin.password=5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8\n");
                fw.write("users.admin.permissions=ALL\n");
                fw.close();
            } catch (Exception e) {
                System.err.println("Error creating config file: " + e.getMessage());
                System.exit(1);
            }
        }
        try {
            props.load(new FileReader(f));
            Logger.info("Loaded config file: " + f.getAbsolutePath());
        } catch (Exception e) {
            Logger.error("Error loading config file: " + e.getMessage());
            System.exit(1);
        }
    }
}
