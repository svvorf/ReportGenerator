package com.svvorf.texunatest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Settings for {@link Generator}: page width, page height and columns.
 */
public class GeneratorSettings {
    private int pageWidth;
    private int pageHeight;

    private List<Column> columns;

    /**
     * Instantiate settings from an XML file.
     * @param filePath a path of a settings XML file. It should be valid XML document.
     * @return {@link GeneratorSettings} instance
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException if document is not valid generator settings XML or something else is wrong with parsing.
     */
    public static GeneratorSettings fromFile(String filePath) throws ParserConfigurationException, IOException, SAXException {
        GeneratorSettings settings = new GeneratorSettings();

        File file = new File(filePath);
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        validateXml(doc);

        Element pageNode = (Element) doc.getElementsByTagName("page").item(0);

        settings.setPageWidth(Integer.parseInt(pageNode.getElementsByTagName("width").item(0).getTextContent()));
        settings.setPageHeight(Integer.parseInt(pageNode.getElementsByTagName("height").item(0).getTextContent()));

        List<Column> columns = new ArrayList<Column>();
        NodeList columnsNodes = doc.getElementsByTagName("column");

        for (int i = 0; i < columnsNodes.getLength(); i++) {
            Element columnNode = (Element) columnsNodes.item(i);
            Column column = new Column(
                    columnNode.getElementsByTagName("title").item(0).getTextContent(),
                    Integer.parseInt(columnNode.getElementsByTagName("width").item(0).getTextContent())
            );
            columns.add(column);
        }

        settings.setColumns(columns);
        settings.verifyWidths();

        return settings;
    }

    private static void validateXml(Document doc) throws SAXException, IOException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(GeneratorSettings.class.getResource("/settings_schema.xsd"));
        Validator validator = schema.newValidator();

        validator.validate(new DOMSource(doc));
    }

    private void verifyWidths() throws SAXException {
        int columnsWidth = 0;
        for (Column column : columns) {
            columnsWidth += column.getWidth();
        }

        columnsWidth += (columns.size() - 1) * 3 + 4; // 3 for inner "walls", and 2 outer "walls"

        if (columnsWidth != getPageWidth()) {
            throw new SAXException("The width of the page should match the total width of the columns.");
        }
    }

    public Map<String, String> getHeaderRow() {
        Map<String, String> row = new HashMap<String, String>();
        for (Column column : columns) {
            row.put(column.getTitle(), column.getTitle());
        }
        return row;
    }

    /**
     * A column of the source data and a report.
     */
    public static class Column {
        private String title;
        private int width;

        private Column(String title, int width) {
            this.title = title;
            this.width = width;
        }

        public String getTitle() {
            return title;
        }

        public int getWidth() {
            return width;
        }


    }

    // Getters and setters

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

}
