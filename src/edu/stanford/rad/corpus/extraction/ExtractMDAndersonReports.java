package edu.stanford.rad.corpus.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class ExtractMDAndersonReports {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
	
		Scanner scanner = new Scanner(new File("data/MD Anderson/MDACC_G1a_50000.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
				
		String[] reports = text.split("\n");
		
		int counter = 0;
		String output = "";
		
		for(String report : reports){
			
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("%","\n");					
			report = report.replaceAll("\n", "\r\n");
			report += "\r\n\r\n\r\n";
			report += "********************************************";
			report += "\r\n\r\n\r\n";
			System.out.println(report);
			System.out.println("=======================");
			
			output += report;
			++counter;			
		}
				
		PrintWriter pw = new PrintWriter("data/MD Anderson/mda_segmented_reports.txt","UTF-8");
		pw.print(output);
		pw.close();
		
		System.out.println(counter);

	}
}
