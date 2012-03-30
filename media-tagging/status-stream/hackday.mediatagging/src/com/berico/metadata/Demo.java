package com.berico.metadata;

import static java.lang.Float.parseFloat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 * Extracts some EXIF metadata from images.
 * 
 * Requires these libraries:
 * - metadata-extractor-2.5.0-RC3.jar
 * - xmpcore.jar
 * which can be found at:
 * http://code.google.com/p/metadata-extractor/downloads/detail?name=metadata-extractor-2.5.0-RC3.zip
 * 
 * @author cgreenbacker
 *
 */
public class Demo {
	
	private static float parseLatLon(String input) {
		String [] latlon = input.split("[°'\"]");
		return parseFloat(latlon[0]) + (((parseFloat(latlon[1]) * 60) + parseFloat(latlon[2])) / 3600);
	}
	
	public static void getLatLons(File imageDir) throws ImageProcessingException, IOException {
		int imgCount = 0;
		ArrayList<String> fileNames = new ArrayList<String>();
		for (File imageFile : imageDir.listFiles()) {
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
			Directory directory = metadata.getDirectory(GpsDirectory.class);
			
			if (directory != null) {
				String lat = directory.getDescription(GpsDirectory.TAG_GPS_LATITUDE);
				String lon = directory.getDescription(GpsDirectory.TAG_GPS_LONGITUDE);
				if (lat != null && lon != null) {
					float latF = parseLatLon(lat);
					float lonF = parseLatLon(lon);
					System.out.println("		var loc" + imgCount + " = new google.maps.LatLng(" + latF + ", " + lonF + ");");
					fileNames.add(imageFile.getName());
					imgCount++;
				}
			}
		}
		
		System.out.println("		...");
		for (int i = 0; i < imgCount; i++)
			System.out.println("		var marker = new google.maps.Marker({position: loc" + i + ", map: map, title: \"" + fileNames.get(i) + "\"});");
	}
	
	public static void getDates(File imageDir) throws ImageProcessingException, IOException {
		for (File imageFile : imageDir.listFiles()) {
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
			Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			if (directory != null) {
				Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				if (date != null) {
					System.out.println(imageFile.getName() + ": " + date);
				}
			}
		}
	}

	public static void main(String[] args) throws ImageProcessingException, IOException {
		
		File imageDir = new File("src/main/resources/sample-images");
		getLatLons(imageDir);
		//getDates(imageDir);
	}
	
}
