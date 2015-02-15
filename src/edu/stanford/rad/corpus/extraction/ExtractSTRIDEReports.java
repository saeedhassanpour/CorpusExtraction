package edu.stanford.rad.corpus.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class ExtractSTRIDEReports {

	public static void main(String[] args) throws FileNotFoundException,IOException {
		long startTime = System.currentTimeMillis();		
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				StringBuilder output = new StringBuilder();
				int counter = 0;

				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				
				String records[] = text.split("\n(?=\\d{9}\t)");
				System.out.println("Number of records: " + records.length);
				
				for (String record : records) {
					String fields[] = record.split("\t\\s*");					
					String report = fields[4].trim();

					report = report.replaceAll("\\s+$","");
					report = report.replaceAll("^\\s+","");
					report = report.replaceAll("\\|","\n");
					report = report.replaceAll("\n", "\r\n");
					report += "\r\n\r\n\r\n";
					report += "********************************************";
					report += "\r\n\r\n\r\n";
					
					//output += report;
					output.append(report);
					++counter;
				}
				
				PrintWriter pw = new PrintWriter("data/Stride/" + fileName + "_segmented_reports.txt","UTF-8");
				pw.print(output);
				pw.close();
				System.out.println(counter);
				
			}
		}
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
