# Flow Log Parser

## Overview

This Java application parses AWS VPC flow log data and maps each log entry to a tag based on a lookup table.

## Problem Statement

Parse a file containing flow log data and map each row to a tag based on a lookup table. The lookup table is defined as a CSV file with 3 columns: dstport, protocol, and tag.
The destination port and protocol combination determine the applicable tag.

## Features

- Parse AWS VPC flow logs (default format, version 2 only)
- Map log entries to tags using a lookup table
- Generate count reports for tags and port/protocol combinations
- Parse protocol numbers from a local CSV file -  **Protocol Numbers** (`src/protocol-numbers.csv`): CSV file with protocol numbers and names.

## Assumptions

1. The flow log file is in AWS VPC Flow Log Version 2 format (default logs only).
2. The lookup table file is a CSV with the format: dstport,protocol,tag.
3. The flow log file size can be up to 10 MB.
4. The lookup file can have up to 10,000 mappings.
5. Tags can map to more than one port/protocol combination.
6. All matching is case-insensitive.
7. The protocol numbers are parsed from a local CSV file(downloaded from https://www.iana.org/assignments/protocol-numbers/protocol-numbers.xhtml) instead of being downloaded from IANA everytime we use.

## Solution Approach

1. **ProtocolDownloader**: Parses a local CSV file to create a map of protocol numbers to names.
2. **LookupParser**: Reads the lookup table CSV and creates a map of port/protocol combinations to tags.
3. **FlowLogParser**: Processes the flow log file, mapping each entry to a tag based on the lookup table.
4. **FlowLogAnalyzer**: Do the parsing and analysis process.
5. **CsvFileWriter**: Generates the output CSV files with tag counts and port/protocol combination counts.

## Prerequisites

- Java Development Kit (JDK) 11 or higher

## Building and Running

1. Navigate to the project root directory.
2. Compile the Java files:
   ```
   javac -d out src/main/java/com/illumio/flowlogparser/*.java
   ```
3. Run the application:
   ```
   java -cp out:src main.java.com.illumio.flowlogparser.Main input/filelogs.txt input/lookup_file.txt output_prefix
   ```

**Important Note : Add your input files in the input folder & then build and run.
   After a successfull execution, you can find the output files in the output folder**  

   **Provided sample input files as input/filelogs.txt and input/lookup_file.txt**

## Input Files

1. **Flow Logs** (`input/filelogs.txt`): AWS VPC flow logs in version 2 format.
2. **Lookup Table** (`input/lookup_file.txt`): CSV file mapping port/protocol to tags.

## Output

Two CSV files in the `output/` directory:
1. `{output_prefix}_tag_counts.csv`: Counts of each tag.
2. `{output_prefix}_port_protocol_counts.csv`: Counts of each port/protocol combination.
