package main.java.com.illumio.flowlogparser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FlowLogAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FlowLogAnalyzer.class.getName());
    private final String flowLogFile;
    private final String lookupFile;
    private final String outputPrefix;

    public FlowLogAnalyzer(String flowLogFile, String lookupFile, String outputPrefix) {
        this.flowLogFile = flowLogFile;
        this.lookupFile = lookupFile;
        this.outputPrefix = outputPrefix;
    }

    public void run() {
        try {
            ProtocolDownloader protocolDownloader = new ProtocolDownloader();
            Map<String, String> protocolMap = protocolDownloader.parse();
            LOGGER.info("Protocol map size: " + protocolMap.size());

            LookupParser lookupParser = new LookupParser(lookupFile);
            Map<Pair<String, String>, Set<String>> lookupDict = lookupParser.parse();
            LOGGER.info("Lookup dictionary size: " + lookupDict.size());

            FlowLogParser flowLogParser = new FlowLogParser(lookupDict, protocolMap);
            Pair<Map<String, Integer>, Map<Pair<String, String>, Integer>> result = flowLogParser.processFlowLogs(flowLogFile);

            String outputDir = "output";
            Files.createDirectories(Paths.get(outputDir));

            String tagCountsFile = Paths.get(outputDir, outputPrefix + "_tag_counts.csv").toString();
            String portProtocolCountsFile = Paths.get(outputDir, outputPrefix + "_port_protocol_counts.csv").toString();

            TextFileWriter.writeTagCounts(tagCountsFile, result.getFirst());
            TextFileWriter.writePortProtocolCounts(portProtocolCountsFile, result.getSecond());

            LOGGER.info("Analysis completed successfully.");
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Provided input file path is not found. Please provide valid file name");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred during flow log analysis", e);
        }
    }
}