package edu.stanford.rad.corpus.extraction;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class ExtractMayoReports {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(new File("data/Mayo/report_text_search.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
		
		text = text.replaceAll("\\s*Mod\\s+Code\\s+Date\\s+Report Text\\s+Description\\s+", "");
		
		String[] reports = text.split("</REPORT_TEXT> </REPORT>");
		
		int counter = 0;
		String output = "";
		for(String report : reports){
			report = report.replaceAll("RT_TEXT>\\s*", "");
			report = report.replaceAll("ES> <REPORT_TEXT>\\s*", "");
			report = report.replaceAll("TEXT>\\s*", "");
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("\n", "\r\n");
			

				System.out.println(report);
				System.out.println("=======================");
				
				output += report;
				output += "\r\n\r\n\r\n";
				output += "********************************************";
				output += "\r\n\r\n\r\n";
				
				++counter;
		}
		
		PrintWriter pw = new PrintWriter("data/Mayo/mayo_segmented_reports.txt","UTF-8");
		pw.print(output);
		pw.close();
		System.out.println(counter);
		
	}

}
