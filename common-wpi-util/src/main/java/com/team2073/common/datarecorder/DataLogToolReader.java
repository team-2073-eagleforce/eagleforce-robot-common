package com.team2073.common.datarecorder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLogToolReader {

    private String filePath;
    private String saveLocation;

     private BufferedReader in = null;
     private CSVParser csvParser = null;
     private List<CSVRecord> records = null;

    public DataLogToolReader(String filePath, String saveLocation) {
        this.filePath = filePath;
        this.saveLocation = saveLocation;

        try {
            addColumn(filePath, saveLocation);
            in = new BufferedReader(new FileReader(saveLocation));
            csvParser = new CSVParser(in, CSVFormat.Builder.create(CSVFormat.DEFAULT).
                    setIgnoreEmptyLines(true).
                    setHeader().
                    setDelimiter(",").
                    setAutoFlush(true).
                    build()
            );
            records = csvParser != null ? csvParser.getRecords() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * DataLogTool gives data in a way unusuable by Apache Common CSV so fills in empty gaps in data with
     * "Empty" so we can read data properly
     * @param header
     * @return Arraylist of info in Doubles
     */
    public ArrayList<Double> getModifiedDataDouble(String header) {
        ArrayList<Double> temp = new ArrayList<>();
        for (CSVRecord record : records) {
            if (record.size() >= csvParser.getHeaderMap().size()) {
                String desiredHeader = record.get("\""+ header + "\"");
                if (!desiredHeader.equals("") && desiredHeader != null && !desiredHeader.equals("Empty"))
                    temp.add(Double.parseDouble(desiredHeader));
            }
        }
        return temp;
    }

    /**
     * DataLogTool gives data in a way unusuable by Apache Common CSV so fills in empty gaps in data with
     * "Empty" so we can read data properly
     * @param header
     * @return Arraylist of info in Booleans
     */
    public ArrayList<Boolean> getModifiedDataBoolean(String header) {
        ArrayList<Boolean> temp = new ArrayList<>();
        for (CSVRecord record : records) {
            if (record.size() >= csvParser.getHeaderMap().size()) {
                String desiredHeader = record.get("\""+ header + "\"");
                if (!desiredHeader.equals("") && desiredHeader != null && !desiredHeader.equals("Empty"))
                    temp.add(Boolean.parseBoolean(desiredHeader));
            }
        }
        return temp;
    }

    /**
     * DataLogTool gives data in a way unusuable by Apache Common CSV so fills in empty gaps in data with
     * "Empty" so we can read data properly
     * @param header
     * @return Arraylist of info in Strings
     */
    public ArrayList<String> getModifiedDataString(String header) {
        ArrayList<String> temp = new ArrayList<>();
        for (CSVRecord record : records) {
            if (record.size() >= csvParser.getHeaderMap().size()) {
                String desiredHeader = record.get("\""+ header + "\"");
                if (!desiredHeader.equals("") && desiredHeader != null && !desiredHeader.equals("Empty"))
                    temp.add(desiredHeader);
            }
        }
        return temp;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }


    public void addColumn(String filepath, String newPath) throws IOException {
        int amountOfHeaders = 0;
        boolean isAmountOfHeadersFound = false;
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            br = new BufferedReader(new FileReader(filepath)) ;
            String line = null;

            CSVPrinter printer = new CSVPrinter(new FileWriter(newPath), CSVFormat.DEFAULT);
            for ( line = br.readLine(); line != null; line = br.readLine())
            {
                String[] splitArray = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (!isAmountOfHeadersFound) {
                    amountOfHeaders = splitArray.length;
                    isAmountOfHeadersFound = true;
                }

                for (int i = 0; i < amountOfHeaders - splitArray.length; i++) {
                    line += ",Empty";
                }

                String[] correctedArray = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                printer.printRecord(Arrays.asList(correctedArray));
            }

        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }

    }
}
