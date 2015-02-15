package edu.stanford.rad.ner.stride.breastcancer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.stanford.rad.ner.stride.ExtractReportsDates;

public class ExtractReportsBreastCancerWithDates {

	public static void main(String[] args) throws FileNotFoundException,IOException, ParseException {
		long startTime = System.currentTimeMillis();
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		String outputFolder = "files/stride/breastCancer180/";
		//Random rand = new Random(123);

		List<String> ourProcedures = Arrays.asList("MRI BREAST UNILATERAL", "MR BREASTS BILATERAL FOR CANCER", "MRI BREASTS BILAT FOR CANCER");
		
		Map<Integer, ArrayList<String>> patientReport = new HashMap<Integer,ArrayList<String>>();
		Map<Integer, ArrayList<Date>> patientDates = new HashMap<Integer,ArrayList<Date>>();
		
		
		int ndate = 0;
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date noDate = formatter.parse("01/01/1900");
		
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
				String fileName = fileEntry.getName();
				String filepath = fileEntry.getPath();
				System.out.println("Working on " + fileName + "...");
				
				Scanner scanner = new Scanner(new File(filepath), "UTF-8");
				String text = scanner.useDelimiter("\\Z").next();
				scanner.close();
				
				String records[] = text.split("\n(?=\\d{9}\t)");
				
				for (String record : records) {
					
					String fields[] = record.split("\t\\s*");
					String procedureDesc = fields[3].trim();
					
					if(!ourProcedures.contains(procedureDesc)){
						continue;
					}

					if(fields.length<6 || fields.length>14){ // For negative
						continue;
					}
					
					boolean OurDisease = false;
					for (int i = 5; i < fields.length; ++i) {
						String stringCode = fields[i].trim();
						double code = -1;
						if (stringCode.matches("-?\\d+(\\.\\d+)?")) {
							code = Double.parseDouble(stringCode);
						}
						if ((int)Math.floor(code) == 174) { //Code
							OurDisease = true;
							break;
						}
					}
					
					if(!OurDisease){ //
						int patID = Integer.parseInt(fields[1]);
						String report = fields[4].trim();
						
						//Get dates
						Date date = null;
						int trimIndex = report.indexOf(" I have personally reviewed the images for this examination and agree");
						if (trimIndex != -1) { //
							String signature = report.substring(trimIndex);
							date = ExtractReportsDates.findDateinString(signature);
						}
						
						if(date == null || date.equals(noDate)){
							String firstLine = report.substring(0, Math.min(report.length(), 140));
							date = ExtractReportsDates.findDateinString(firstLine);
						}
						
						if(date.equals(noDate)){
							ndate++;
						}
						
						// add report
						ArrayList<String> reportList = new ArrayList<String>();
						if(patientReport.containsKey(patID)){
							reportList.addAll(patientReport.get(patID));
						}
						reportList.add(fields[4].trim());
						patientReport.put(patID, reportList);
						
						// add date
						ArrayList<Date> dateList = new ArrayList<Date>();
						if(patientDates.containsKey(patID)){
							dateList.addAll(patientDates.get(patID));
						}
						dateList.add(date);
						patientDates.put(patID, dateList);
					}
				}	
			}
		}
		
		int skipped = 0;
		for (int patID : patientReport.keySet()) {
			ArrayList<String> reportList = patientReport.get(patID);

			if (reportList.size() == 1) //&& rand.nextDouble() < 0.33) 
			{
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/negative/" + patID + ".txt", "UTF-8");
				pw.println(reportList.get(0));
				pw.close();

			} else {
				ArrayList<Date> sortedDteList = new ArrayList<Date>();
				sortedDteList.addAll(patientDates.get(patID));
				Collections.sort(sortedDteList);
				// System.out.println(sortedDteList);

				boolean skp = true;
				int diffinDays = 0;
				Date first = sortedDteList.get(0);
				
				if (!first.equals(noDate)) {
					skp = false;
					Date last = sortedDteList.get(sortedDteList.size() - 1);
					diffinDays = (int) ((last.getTime() - first.getTime()) / (1000 * 60 * 60 * 24));
					//System.out.println(diffinDays);
				}

				if (!skp) { 
					List<Date> dateList = patientDates.get(patID);
					for (int i = 0; i < dateList.size(); ++i) {
						if (dateList.get(i).equals(first)) {
							if (diffinDays > 180) { //
								PrintWriter pw = new PrintWriter(outputFolder + "corpus/positive/"+ patID + ".txt", "UTF-8");
								pw.println(reportList.get(i));
								pw.close();
								break;
							} 
							else
								//if(rand.nextDouble() < 0.23)
								{
									PrintWriter pw = new PrintWriter(outputFolder + "corpus/negative/"+ patID + ".txt", "UTF-8");
									pw.println(reportList.get(i));
									pw.close();
									break;
								}
						}
					}
				} else {
					++skipped;
					//System.out.println("skipped: " + sortedDteList);
				}

			}
		}

		System.out.println("Records without dates: " + ndate);
		System.out.println("Number of skipped: " + skipped);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime/1000.0 + " seconds");
	}
}
