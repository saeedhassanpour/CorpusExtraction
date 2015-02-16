package edu.stanford.rad.stride.abdomenct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.rad.stride.ExtractReportsDates;
import edu.stanford.rad.stride.ValueComparator;

public class ExtractReports {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		long startTime = System.currentTimeMillis();
		final File folder = new File("/Users/saeedhp/Dropbox/Stanford/Data/STRIDE/Reports");
		String outputFolder = "files/stride/abdomenCT/";

		Map<Integer, ArrayList<Date>> patientDates = new HashMap<Integer, ArrayList<Date>>();
		Map<Integer, ArrayList<String>> patientProcedureDesc = new HashMap<Integer, ArrayList<String>>();
		Map<Integer, ArrayList<String>> patientReport = new HashMap<Integer, ArrayList<String>>();
		Map<Integer, Set<String>> categoryPatients = new TreeMap<Integer, Set<String>>();
		Map<String, Integer> procedureCounts = new HashMap<String, Integer>();


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

					int patID = Integer.parseInt(fields[1]);
					String report = fields[4].trim();

					// Get dates
					Date date = noDate;
					String firstLine = report.substring(0, Math.min(report.length(), 80));
					date = ExtractReportsDates.findDateinString(firstLine);
					
					if (date.equals(noDate)) {
						int trimIndex = report.indexOf(" I have personally reviewed the images for this examination and agree");
						if (trimIndex != -1) {
							String signature = report.substring(trimIndex);
							date = ExtractReportsDates.findDateinString(signature);
						}
					}

					if (date.equals(noDate)) {
						ndate++;
					}

					// add procedure Description
					ArrayList<String> procedureDescList = new ArrayList<String>();
					if (patientProcedureDesc.containsKey(patID)) {
						procedureDescList.addAll(patientProcedureDesc
								.get(patID));
					}
					procedureDescList.add(fields[3].trim());
					patientProcedureDesc.put(patID, procedureDescList);

					// add report
					ArrayList<String> reportList = new ArrayList<String>();
					if (patientReport.containsKey(patID)) {
						reportList.addAll(patientReport.get(patID));
					}
					reportList.add(fields[4].trim());
					patientReport.put(patID, reportList);

					// add date
					ArrayList<Date> dateList = new ArrayList<Date>();
					if (patientDates.containsKey(patID)) {
						dateList.addAll(patientDates.get(patID));
					}
					dateList.add(date);
					patientDates.put(patID, dateList);
				}
			}

		}

		int counter = 0;
		PrintWriter cpw = new PrintWriter(outputFolder + "corpus/procCountsPerPatient.tsv", "UTF-8");
		for (int patID : patientProcedureDesc.keySet()) {
			
			boolean skp = true;
			ArrayList<String> procList = patientProcedureDesc.get(patID);
			for (String proc : procList) {
				if (proc.toLowerCase().contains("ct abdomen")) {
					skp = false;
					break;
				}
			}
			if(skp)
			{
				continue;
			}
			
			Set<Date> sortedDateSet = new TreeSet<Date>();
			sortedDateSet.addAll(patientDates.get(patID));
			int k = sortedDateSet.size();
						
			int w = 0;
			Date first = noDate, second = noDate, third = noDate;
			for (Date date : sortedDateSet) {
				++w;
				if (w == 1) {
					first = date;
				} else if (w == 2) {
					second = date;
				} else if (w == 3) {
					third = date;
				} else {
					break;
				}
			}
			
			List<String> reportList = patientReport.get(patID);
			List<Date> dateList = patientDates.get(patID);
			if(reportList.size() != dateList.size())
			{
				System.out.println("The list sizes do not match.");
				break;
			}
			
			if(first.equals(noDate))
			{
				continue;
			}
			
			//Add patient to categories
			//int cat = (k > 20) ? 20 : k;   //Categories or Day
			int cat = k;
			Set<String> patientSet = new HashSet<String>();
			if (categoryPatients.containsKey(cat)) {
				patientSet.addAll(categoryPatients.get(cat));
			}
			patientSet.add(patID + ".txt");
			categoryPatients.put(cat, patientSet);
			
			//Add procedure counts
			for (String proc : procList) {
				int pc = 0;
				if (procedureCounts.containsKey(proc)) {
					pc = procedureCounts.get(proc);
				}
				++pc;
				procedureCounts.put(proc, pc);
			}
			
			//Write proc count for patient 
			cpw.printf("%d\n", procList.size());

			
			if (k < 20) {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/1/report" + k + "/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			} else {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/1/report20/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			}
			
			if(second.equals(noDate))
			{
				continue;
			}
			if (k < 20) {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/2/report" + k + "/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first) || dateList.get(i).equals(second)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			} else {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/2/report20/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first) || dateList.get(i).equals(second)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			}
			
			if(third.equals(noDate))
			{
				continue;
			}
			if (k < 20) {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/3/report" + k + "/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first) || dateList.get(i).equals(second) || dateList.get(i).equals(third)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			} else {
				PrintWriter pw = new PrintWriter(outputFolder + "corpus/reports/3/report20/" + patID + ".txt", "UTF-8");
				for (int i = 0; i < dateList.size(); ++i) {
					if (dateList.get(i).equals(first) || dateList.get(i).equals(second) || dateList.get(i).equals(third)){
						pw.println(reportList.get(i) + "\n");
					}
				}
				pw.close();
			}
		}
		cpw.close();

		PrintWriter pw = new PrintWriter(outputFolder + "corpus/dayCounts.tsv", "UTF-8");
		for(int cat: categoryPatients.keySet())
		{
			pw.printf("%d\t%d\n", cat, categoryPatients.get(cat).size());
		}
		pw.close();
		
		ValueComparator<String> bvc = new ValueComparator<String>(procedureCounts);
		TreeMap<String, Integer> sortedProcedureCounts = new TreeMap<String, Integer>(bvc);
		sortedProcedureCounts.putAll(procedureCounts);
		
		pw = new PrintWriter(outputFolder + "corpus/procCounts.tsv", "UTF-8");
		for (Map.Entry<String, Integer> entry : sortedProcedureCounts.entrySet()) {
			String p = entry.getKey();
			int c = entry.getValue();
			pw.printf("%s\t%d\n", p, c);
		}
		pw.close();
		
		String serFolder = "files/stride/abdomenCT/corpus/categoryPatients.ser";
		FileOutputStream fileOut = new FileOutputStream(serFolder);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(categoryPatients);
		out.close();
		fileOut.close();
		System.out.println("Serialized data is saved in " + serFolder);

		System.out.println("counter: " + counter);
		System.out.println("Records without dates: " + ndate);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Finshed in " + totalTime / 1000.0 + " seconds");
	}
}
