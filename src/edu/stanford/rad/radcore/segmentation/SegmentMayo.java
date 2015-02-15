package edu.stanford.rad.radcore.segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class SegmentMayo {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(new File("data/Mayo/report_text_search.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
		
		text = text.replaceAll("\\s*Mod\\s+Code\\s+Date\\s+Report Text\\s+Description\\s+", "");
		
		String[] reports = text.split("</REPORT_TEXT> </REPORT>");
		
		int counter = 0;
		for(String report : reports){
			report = report.replaceAll("RT_TEXT>\\s*", "");
			report = report.replaceAll("ES> <REPORT_TEXT>\\s*", "");
			report = report.replaceAll("TEXT>\\s*", "");
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("\n", "\r\n");
			report = report.replaceAll("\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{2,4}(?:,.)*", "");

			System.out.println(report);
			System.out.println("=======================");
			
			++counter;
			PrintWriter pw = new PrintWriter("data/Mayo/Mayo_Reports/mayo_report_"+counter+".txt","UTF-8");
			pw.print(report);
			pw.close();

		}
		System.out.println(reports.length);
		
	
	}

}
