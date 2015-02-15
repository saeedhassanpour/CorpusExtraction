package edu.stanford.rad.radcore.chestct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class ExtractMCWChestCT {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		int counter = 0;
		String output = "";
		List<String> cct = new ArrayList<String>();
		
		Scanner scanner = new Scanner(new File("data/MCW/out.x2006.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
				
		String[] reports = text.split("\n");
		
		for(String report : reports){
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("\\|","\n");
			//System.out.println(report);
			//System.out.println("=======================");

			
			String[] lines = report.split("[.:]+");
			String header = "";
			for (int i =0; i<lines.length && i<1; i++)
			{
				header += lines[i] + " ";
			}
			
			if(header.toLowerCase().replaceAll(",", " ").contains(" ct "))
				if(header.toLowerCase().contains("thorax") || header.toLowerCase().contains("chest"))
			{
				report = report.replaceAll("\n", "\r\n");
				report += "\r\n\r\n\r\n";
				report += "********************************************";
				report += "\r\n\r\n\r\n";
				cct.add(report);
				System.out.println(header);
				System.out.println("=======================");
				++counter;
				PrintWriter pw = new PrintWriter("data/MCW/MCW_ChestCT/mcw_report_"+counter+".txt","UTF-8");
				pw.print(report);
				pw.close();
			}

		}
	
		Random randomGenerator = new Random();;
		Set<Integer> ids = new HashSet<Integer>();
		int n = 100, i=0;
		while (i<n)
		{
			int index = randomGenerator.nextInt(cct.size());
			if(!ids.contains(index))
			{
				output += cct.get(index);
				ids.add(index);
				i++;
			}
		}
		
		PrintWriter pw = new PrintWriter("data/MCW/mcw_" + n + "_Chest_CT_reports.txt","UTF-8");
		pw.print(output);
		pw.close();
		System.out.println(counter + " " + i);
	}

}
