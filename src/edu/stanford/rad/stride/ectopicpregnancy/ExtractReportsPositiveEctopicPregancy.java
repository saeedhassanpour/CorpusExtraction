package edu.stanford.rad.stride.ectopicpregnancy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.stanford.rad.stride.ExtractReportsDates;

public class ExtractReportsPositiveEctopicPregancy {

	public static void main(String[] args) throws FileNotFoundException,IOException, ParseException {
		long startTime = System.currentTimeMillis();
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		final String outputFolder = "files/stride/ectopicPregnancy/";
		
		List<String> ourProcedures = Arrays.asList("US R/O ECTOPIC PREGNANCY", "US PELVIS NON OB WO TRANSVAG", "US PELVIS NON OB W TRANSVAG", "US PREGNANCY UTERUS WITH TRANS VAGINAL UNDER 14 WKS", "US UTERUS PREGNANT", "US UTERUS PREGNANT LTD");

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

//					if(fields.length<6 || fields.length>14){
//						continue;
//					}
					
					boolean OurDisease = false;
					for (int i = 5; i < fields.length; ++i) {
						String stringCode = fields[i].trim();
						double code = -1;
						if (stringCode.matches("-?\\d+(\\.\\d+)?")) {
							code = Double.parseDouble(stringCode);
						}
						
						if ((int)Math.floor(code) == 633) { //Code
							OurDisease = true;
							break;
						}
					}
					
					if(OurDisease){
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
						//System.out.println(date);
						
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
			
			if (reportList.size() == 1) {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/positive/" + patID + ".txt", "UTF-8");
				pw.println(reportList.get(0));
				pw.close();

			} else {
				ArrayList<Date> dateList = patientDates.get(patID);
				Date lastDate = formatter.parse("01/02/1900");
				String report = "";

				boolean skp = false;
				for (int i = 0; i < dateList.size(); ++i) {
					Date iDate = dateList.get(i);
					
					if (iDate.equals(noDate)) {
						System.out.println("No Date for patient " + patID + ": " + iDate);
						++skipped;
						skp = true;
						break;
					} else if (iDate.after(lastDate)) {
						lastDate = iDate;
						report = reportList.get(i);
					}
					
				}
				if(!skp){
					PrintWriter pw = new PrintWriter(outputFolder + "corpus/positive/" + patID + ".txt", "UTF-8");
					pw.println(report);
					pw.close();

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
