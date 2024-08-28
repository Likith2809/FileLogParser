package main.java.com.illumio.flowlogparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ProtocolDownloader {
    private static final Logger LOGGER = Logger.getLogger(ProtocolDownloader.class.getName());
    private static final String PROTOCOL_CSV_FILE = "/resources/protocol-numbers.csv";
    private static final Map<String, String> FALLBACK_PROTOCOL_MAP = Map.of(
            "1", "icmp",
            "6", "tcp",
            "17", "udp"
    );

    public Map<String, String> parse() {
        Map<String, String> protocolMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(PROTOCOL_CSV_FILE))))) {

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skipping the header line
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String decimal = parts[0].replaceAll("[^0-9]", "").trim();
                    String keyword = parts[1].replaceAll("\"", "").trim();
                    if (!decimal.isEmpty() && !keyword.isEmpty()) {
                        protocolMap.put(decimal, keyword.toLowerCase());
                    }
                }
            }

            LOGGER.info("Parsed " + protocolMap.size() + " protocol numbers from CSV file");

            if (protocolMap.isEmpty()) {
                LOGGER.warning("No protocols parsed from CSV. Falling back to default protocol map.");
                return FALLBACK_PROTOCOL_MAP;
            }

            return protocolMap;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing protocol numbers from CSV. Using fallback map.", e);
            return FALLBACK_PROTOCOL_MAP;
        }
    }
}