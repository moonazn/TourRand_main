package com.tourbus.tourrand;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelParser {
    private Map<String, Location> locationMap = new HashMap<>();

    public void parseExcelFile(InputStream inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() < 3) {
                    continue; // 첫 번째 행과 두 번째 행을 건너뜁니다.
                }
                String city = row.getCell(3).getStringCellValue();

                Cell longitudeCell = row.getCell(4);
                Cell latitudeCell = row.getCell(5);

                double longitude = getNumericCellValue(longitudeCell);
                double latitude = getNumericCellValue(latitudeCell);

                locationMap.put(city, new Location(longitude, latitude));
                Log.d("ExcelParser", "Parsed city: " + city + ", latitude: " + latitude + ", longitude: " + longitude); // 로그 추가
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ExcelParser", "Error parsing Excel file", e); // 에러 로그 추가
        }
    }

    private double getNumericCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            return Double.parseDouble(cell.getStringCellValue());
        } else {
            throw new IllegalStateException("Unexpected cell type: " + cell.getCellType());
        }
    }

    public Location getLocation(String city) {
        return locationMap.get(city);
    }

    public static class Location {
        public double longitude;
        public double latitude;

        public Location(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
}
