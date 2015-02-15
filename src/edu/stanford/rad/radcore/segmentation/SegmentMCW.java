package edu.stanford.rad.radcore.segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class SegmentMCW {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		int counter = 0;
		for(int i = 1995; i<=2006; i++){
			Scanner scanner = new Scanner(new File("data/MCW/out.x" + i + ".txt"), "UTF-8");
			String text = scanner.useDelimiter("\\Z").next();
			scanner.close();
					
			String[] reports = text.split("\n");
			
			for(String report : reports){
				report = report.replaceAll("\\s+$","");
				report = report.replaceAll("^\\s+","");
				report = report.replaceAll("\\|","\n");
				report = report.replaceAll("\n", "\r\n");
				report = report.replaceAll("\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{2,4}(?:,.)*", "");
				report = report.replaceAll("^MCW\r\n", "");
				report = report.replaceAll("^\\d+\r\n", "");

				//System.out.println(report);
				//System.out.println("=======================");
				
				++counter;
				PrintWriter pw = new PrintWriter("data/MCW/MCW_Reports/mcw_report_"+counter+".txt","UTF-8");
				pw.print(report);
				pw.close();
	
			}
			System.out.println(reports.length);
		}
		System.out.println("Ttotal: " + counter);
	}

}
