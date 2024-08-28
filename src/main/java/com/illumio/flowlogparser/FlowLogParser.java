package main.java.com.illumio.flowlogparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FlowLogParser {
    private static final Logger LOGGER = Logger.getLogger(FlowLogParser.class.getName());
    private final Map<Pair<String, String>, Set<String>> lookupDict;
    private final Map<String, String> protocolMap;
    private final Map<String, Integer> tagCounts = new HashMap<>();
    private final Map<Pair<String, String>, Integer> portProtocolCounts = new HashMap<>();

    public FlowLogParser(Map<Pair<String, String>, Set<String>> lookupDict, Map<String, String> protocolMap) {
        this.lookupDict = lookupDict;
        this.protocolMap = protocolMap;
    }

    private void parseLine(String line, int lineNum) {
        String[] fields = line.trim().split("\\s+");
        if (fields.length < 14) {
            LOGGER.warning("Skipping malformed row at line " + lineNum);
            return;
        }

        String dstport = fields[5].toLowerCase();
        String protocolNum = fields[7];
        String protocol = protocolMap.getOrDefault(protocolNum, protocolNum).toLowerCase();
        Pair<String, String> key = new Pair<>(dstport, protocol);

        Set<String> tags = lookupDict.getOrDefault(key, Set.of("untagged"));
        for (String tag : tags) {
            if (tagCounts.containsKey(tag)) {
                tagCounts.put(tag, tagCounts.get(tag) + 1);
            } else {
                tagCounts.put(tag, 1);
            }
        }

        if (portProtocolCounts.containsKey(key)) {
            portProtocolCounts.put(key, portProtocolCounts.get(key) + 1);
        } else {
            portProtocolCounts.put(key, 1);
        }
    }

    public Pair<Map<String, Integer>, Map<Pair<String, String>, Integer>> processFlowLogs(String flowLogFile) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                parseLine(line, lineNum);
            }
            LOGGER.info("Processed " + lineNum + " lines from flow log file");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing flow log file with name: " + flowLogFile);
            throw e;
        }

        // Capitalizing 'untagged' to 'Untagged'
        if (tagCounts.containsKey("untagged")) {
            tagCounts.put("Untagged", tagCounts.remove("untagged"));
        }

        return new Pair<>(tagCounts, portProtocolCounts);
    }
}
