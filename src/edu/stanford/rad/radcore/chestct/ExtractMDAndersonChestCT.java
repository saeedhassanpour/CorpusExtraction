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


public class ExtractMDAndersonChestCT {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
	
		Scanner scanner = new Scanner(new File("data/MD Anderson/MDACC_G1a_50000.txt"), "UTF-8");
		String text = scanner.useDelimiter("\\Z").next();
		scanner.close();
				
		String[] reports = text.split("\n");
		
		int counter = 0;
		String output = "";
		List<String> cct = new ArrayList<String>();
		for(String report : reports){
			report = report.replaceAll("\\s+$","");
			report = report.replaceAll("^\\s+","");
			report = report.replaceAll("%","\n");
			
			//System.out.println(report);
			//System.out.println("=======================");
			String[] lines = report.split("\n+");
			String header = "";
			for (int i =0; i<lines.length && i<2; i++)
			{
				header += lines[i] + " ";
			}
			//System.out.println(header);
			//System.out.println("=======================");
			
			
			if(header.toLowerCase().replaceAll("[.,;:]", " ").contains(" ct "))
			if(header.toLowerCase().contains("thorax") || header.toLowerCase().contains("chest"))
			{
				report = report.replaceAll("\n", "\r\n");
				report += "\r\n\r\n\r\n";
				report += "********************************************";
				report += "\r\n\r\n\r\n";
				cct.add(report);
				System.out.println(report);
				System.out.println("=======================");
			
				++counter;
				PrintWriter pw = new PrintWriter("data/MD Anderson/MDA_ChestCT/mda_report_"+counter+".txt","UTF-8");
				pw.print(report);
				pw.close();
			}
			
		}
		Random randomGenerator = new Random();;
		Set<Integer> ids = new HashSet<Integer>();
		int n = 149, i=0;
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
		
		PrintWriter pw = new PrintWriter("data/MD Anderson/mda_" + n + "_Chest_CT_reports.txt","UTF-8");
		pw.print(output);
		pw.close();
		System.out.println(counter + " " + i);

	}
}
