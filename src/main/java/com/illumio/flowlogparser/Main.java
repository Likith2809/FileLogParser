package main.java.com.illumio.flowlogparser;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -cp out:src main.java.com.illumio.flowlogparser.Main <flow_log_file> <lookup_file> <output_prefix>");
            System.exit(1);
        }

            String flowLogFile = args[0];
            String lookupFile = args[1];
            String outputPrefix = args[2];

            FlowLogAnalyzer analyzer = new FlowLogAnalyzer(flowLogFile, lookupFile, outputPrefix);
            analyzer.run();
        }

}