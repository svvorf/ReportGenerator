package com.svvorf.texunatest;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A source data for a report.
 */
public class GeneratorSourceData {

    private List<Map<String, String>> rows;
    private List<String[]> data;

    /**
     * Parses TSV-data from a file.
     *
     * @param filePath a path to a file.
     * @return {@link GeneratorSourceData} instance.
     */
    public static GeneratorSourceData fromFile(String filePath) {
        File file = new File(filePath);

        TsvParserSettings tsvParserSettings = new TsvParserSettings();
        tsvParserSettings.getFormat().setLineSeparator("\n");

        TsvParser tsvParser = new TsvParser(tsvParserSettings);
        List<String[]> data = tsvParser.parseAll(file, "UTF-16");

        GeneratorSourceData sourceData = new GeneratorSourceData();
        sourceData.setData(data);
        return sourceData;
    }

    /**
     * Creates rows from raw data.
     *
     * @param columns a list of {@link com.svvorf.texunatest.GeneratorSettings.Column}
     */
    public void createRows(List<GeneratorSettings.Column> columns) throws IllegalArgumentException {
        int columnsCount = columns.size();

        rows = new ArrayList<Map<String, String>>(data.size());
        for (String[] dataLine : data) {
            if (dataLine.length != columns.size()) {
                throw new IllegalArgumentException("The number of columns in the source file should match the number of columns specified in the settings.");
            }

            Map<String, String> row = new HashMap<String, String>();
            for (int i = 0; i < columnsCount; i++) {
                row.put(columns.get(i).getTitle(), dataLine[i]);
            }
            rows.add(row);
        }

    }

    // Getters and setters

    public void setData(List<String[]> data) {
        this.data = data;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

}
