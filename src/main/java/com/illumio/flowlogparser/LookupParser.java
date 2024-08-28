package main.java.com.illumio.flowlogparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LookupParser implements Parser<Map<Pair<String, String>, Set<String>>> {
    private static final Logger LOGGER = Logger.getLogger(LookupParser.class.getName());
    private final String lookupFile;

    public LookupParser(String lookupFile) {
        this.lookupFile = lookupFile;
    }
    @Override
    public Map<Pair<String, String>, Set<String>> parse() throws Exception {
        Map<Pair<String, String>, Set<String>> lookupDict = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(lookupFile))) {
            String line;
            reader.readLine(); // Skipping the header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Pair<String, String> key = new Pair<>(parts[0].toLowerCase(), parts[1].toLowerCase());
                    lookupDict.computeIfAbsent(key, k -> new HashSet<>()).add(parts[2].toLowerCase());
                }
            }
            LOGGER.info("Parsed " + lookupDict.size() + " unique port/protocol combinations from lookup table");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing lookup file with name: " + lookupFile);
            throw e;
        }
        return lookupDict;
    }
}