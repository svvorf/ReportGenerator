package com.svvorf.texunatest;

import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static com.svvorf.texunatest.Utils.*;

/**
 * Generates a report.
 */
public class Generator {

    private GeneratorSettings settings;
    private GeneratorSourceData sourceData;

    private Pattern wordSplitPattern = Pattern.compile("(?<=[^a-zA-Zа-яА-Я0-9])");

    /**
     * Creates new generator with provided {@link GeneratorSettings} instance.
     * @param settings a settings file
     */
    public Generator(GeneratorSettings settings) {
        this.settings = settings;
    }

    /**
     * Initializes {@link GeneratorSourceData} instance for this generator.
     * @param sourceData a source data
     */
    public void initSourceData(GeneratorSourceData sourceData) throws IllegalArgumentException {
        sourceData.createRows(settings.getColumns());
        this.sourceData = sourceData;
    }

    /**
     * Writes report to a specified file.
     * @param reportFilePath path to the file. If it isn't exist, it will be created.
     * @throws IOException
     */
    public void produceReportToFile(String reportFilePath) throws IOException {
        File reportFile = new File(reportFilePath);
        if (!reportFile.exists())
            reportFile.createNewFile();

        FileWriter fw = new FileWriter(reportFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(reportFile.getAbsoluteFile()), "UTF-16"
        ));

        String header = makeReportRow(settings.getHeaderRow());
        int headerLinesCount = getLinesCount(header);

        try {
            bw.write(header);
            int linesCount = headerLinesCount;
            for (Map<String, String> row : sourceData.getRows()) {
                String reportRow = makeReportRow(row);
                int rowLinesCount = getLinesCount(reportRow);

                if (linesCount + rowLinesCount > settings.getPageHeight()) {
                    bw.write("~" + LINE_SEPARATOR);
                    bw.write(header);
                    linesCount = headerLinesCount + rowLinesCount;
                } else {
                    linesCount += rowLinesCount;
                }
                bw.write(reportRow);
            }
        } finally {
            bw.close();
        }
    }

    /**
     * Creates textual representation of a row.
     * @param row a map with a column name as a key and cell text as a value.
     * @return textual representation of a row with borders.
     */
    private String makeReportRow(Map<String, String> row) {
        StringBuilder rowBuilder = new StringBuilder();
        Map<String, List<String>> columnsCellLines = new HashMap<String, List<String>>();
        int rowHeight = 0;
        for (GeneratorSettings.Column column : settings.getColumns()) {
            // Splitting by words
            String cellValue = row.get(column.getTitle());
            List<String> cellWords = new ArrayList<String>(Arrays.asList(wordSplitPattern.split(cellValue)));
            for (int i = 0; i < cellWords.size(); i++) {
                String cellWord = cellWords.get(i);
                if (cellWord.length() > column.getWidth()) {
                    cellWords.set(i, cellWord.substring(0, column.getWidth()));
                    cellWords.add(i + 1, cellWord.substring(column.getWidth()));
                }
            }

            // Creating lines considering column width
            List<String> cellLines = new ArrayList<String>();
            String currentLine = "";
            for (String cellWord : cellWords) {
                if (currentLine.length() + cellWord.trim().length() <= column.getWidth()) {
                    currentLine += cellWord;
                } else  {
                    cellLines.add(currentLine.trim());
                    currentLine = cellWord;
                }
            }
            cellLines.add(currentLine);
            columnsCellLines.put(column.getTitle(), cellLines);

            rowHeight = Math.max(rowHeight, cellLines.size());
        }

        // Creating actual row
        for (int i = 0; i < rowHeight; i++) {
            for (GeneratorSettings.Column column : settings.getColumns()) {
                rowBuilder.append("| ");

                List<String> cellLines = columnsCellLines.get(column.getTitle());
                String line = (i < cellLines.size()) ? cellLines.get(i) : "";
                rowBuilder.append(padToWidth(line, column.getWidth()));

                rowBuilder.append(" ");
            }
            rowBuilder.append("|").append(LINE_SEPARATOR);
        }
        rowBuilder.append(padToWidth("", settings.getPageWidth(), '-')).append(LINE_SEPARATOR);
        return rowBuilder.toString();
    }



    public static void main(String[] args) {
        validateArguments(args);

        try {
            GeneratorSettings generatorSettings = GeneratorSettings.fromFile(args[0]);
            Generator generator = new Generator(generatorSettings);
            generator.initSourceData(GeneratorSourceData.fromFile(args[1]));
            generator.produceReportToFile(args[2]);
        } catch (SAXException e) {
            exitWithError("Provided settings file is not valid: " + e.getMessage());
        } catch (Exception e) {
            exitWithError("Internal error: " + e.getMessage());
        }
    }

    private static void validateArguments(String[] args) {
        if (args.length < 3) {
            exitWithError("Generator arguments format: SETTINGS_FILE SOURCE_FILE REPORT_FILE");
        }

        File settingsFile = new File(args[0]);
        if (!settingsFile.exists()) {
            exitWithError("Provided settings file does not exist.");
        } else if (settingsFile.isDirectory()) {
            exitWithError(settingsFile + " is a directory.");
        }

        File sourceFile = new File(args[1]);
        if (!sourceFile.exists()) {
            exitWithError("Provided source file does not exist.");
        } else if (sourceFile.isDirectory()) {
            exitWithError(sourceFile.getName() + " is a directory.");
        }

        File reportFile = new File(args[2]);
        if (reportFile.isDirectory()) {
            exitWithError(reportFile.getName() + " is a directory.");
        }
    }

    private static void exitWithError(String message) {
        System.out.println(message);
        System.exit(0);
    }


}
