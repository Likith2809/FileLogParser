package main.java.com.illumio.flowlogparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TextFileWriter {
    private static final Logger LOGGER = Logger.getLogger(TextFileWriter.class.getName());

    public static void writeTagCounts(String outputFile, Map<String, Integer> tagCounts) throws IOException {
        List<Map.Entry<String, Integer>> sortedData = tagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toList());

        writeCsv(outputFile, List.of("Tag", "Count"), sortedData);
    }

    public static void writePortProtocolCounts(String outputFile, Map<Pair<String, String>, Integer> portProtocolCounts) throws IOException {
        List<List<String>> sortedData = portProtocolCounts.entrySet().stream()
                .sorted((e1, e2) -> {
                    int portCompare = comparePortStrings(e1.getKey().getFirst(), e2.getKey().getFirst());
                    return portCompare != 0 ? portCompare : e1.getKey().getSecond().compareTo(e2.getKey().getSecond());
                })
                .map(e -> List.of(e.getKey().getFirst(), e.getKey().getSecond(), e.getValue().toString()))
                .collect(Collectors.toList());

        writeCsv(outputFile, List.of("Port", "Protocol", "Count"), sortedData);
    }

    private static void writeCsv(String outputFile, List<String> headers, List<?> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(String.join(",", headers));
            writer.newLine();

            for (Object row : data) {
                if (row instanceof Map.Entry) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) row;
                    writer.write(entry.getKey() + "," + entry.getValue());
                } else if (row instanceof List) {
                    writer.write(String.join(",", (List<String>) row));
                }
                writer.newLine();
            }

            LOGGER.info("Wrote data to " + outputFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to " + outputFile, e);
            throw e;
        }
    }

    private static int comparePortStrings(String port1, String port2) {
        try {
            return Integer.compare(Integer.parseInt(port1), Integer.parseInt(port2));
        } catch (NumberFormatException e) {
            return port1.compareTo(port2);
        }
    }
}
