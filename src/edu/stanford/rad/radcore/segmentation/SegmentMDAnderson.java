package edu.stanford.rad.radcore.segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class SegmentMDAnderson {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
	
		Scanner scanner = new Scanner(new File("data/MD Anderson/MDACC_G1a_50000.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
				
		String[] reports = text.split("\n");
		
		int counter = 0;
		for(String report : reports){
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("%","\n");
			report = report.replaceAll("\n", "\r\n");
			report = report.replaceAll("\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{2,4}(?:,.)*", "");
			report = report.replaceAll("^\\d+\\s+FULL RESULT:", "FULL RESULT:");


			//System.out.println(report);
			//System.out.println("=======================");
			
			++counter;
			PrintWriter pw = new PrintWriter("data/MD Anderson/MD Anderson_Reports/mda_report_"+counter+".txt","UTF-8");
			pw.print(report);
			pw.close();

		}
		System.out.println(reports.length);

	}
}
