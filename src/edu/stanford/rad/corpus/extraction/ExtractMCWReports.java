package edu.stanford.rad.corpus.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class ExtractMCWReports {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		for(int i = 2001; i<=2006; i++){
			int counter = 0;
			StringBuilder output = new StringBuilder();
			//String output = "";
			Scanner scanner = new Scanner(new File("data/MCW/out.x" + i + ".txt"), "UTF-8");
			String text = scanner.useDelimiter("\\Z").next();
			scanner.close();
					
			String[] reports = text.split("\n");
			
			for(String report : reports){
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
			
			PrintWriter pw = new PrintWriter("data/MCW/mcw_segmented_reports_" + i + ".txt","UTF-8");
			pw.print(output);
			pw.close();
			System.out.println(counter);
		}
	}

}
