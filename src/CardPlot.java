import java.io.*;
import java.util.*;


public class CardPlot {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		List<String> agentList = new ArrayList<String>();
		agentList.add("Alarm");
		agentList.add("Contacts");
		agentList.add("HFD");
		agentList.add("Launch Apps");
		agentList.add("Memo");
		agentList.add("Music");
		agentList.add("Navigation");
		agentList.add("News");
		agentList.add("POI");
		agentList.add("Settings");
		agentList.add("System");
		agentList.add("Weather");
		agentList.add("WebSearch");
		
		// keeps a log file
		PrintStream ps = new PrintStream("log.txt");
		double averlength = 0;
		int totallength = 0;
		int queriesCount = 0;
		double maxlength = 0;
		
		// stores all the criteria handles with their default values
		Map<String, String> defaultCriteriaDict = new HashMap<String, String>();
		BufferedReader br2 = new BufferedReader(new FileReader("q/default_criteria.txt"));
		String line2 = "";		
		while ((line2 = br2.readLine()) != null) {
			line2 = line2.replaceAll("\\t", " ");
			String[] tokens = line2.split(" ");
			String criteriaName = "<" + tokens[0] + ">";
			String value = "";
			for (int i = 1; i <tokens.length - 1; i ++) {
				if (!tokens[i + 1].equals("|")) {
					value += tokens[i + 1];
				} else {
					break;
				}
			}
			defaultCriteriaDict.put(criteriaName, value);
		}
		br2.close();
		
		
		// stores all the command variations with their criteria handlers replaced by the default values, the priorities, and the string lengths
		Map<String, Map<Integer, List<String>>> commandSortedByPriorityAllDomains = new HashMap<String, Map<Integer, List<String>>>();
		Map<String, Map<String, List<String>>> commandSortedByLengthAllDomains = new HashMap<String, Map<String, List<String>>>();
		ps.println("Maximum and average lengths per each domain:");
		for (int idx = 0; idx < agentList.size(); idx ++) {
			double averlengthpd = 0;
			int totallengthpd = 0;
			int queriesCountpd = 0;
			double maxlengthpd = 0;			
			String agentName = agentList.get(idx);
			String path = "";
			path = "CARDs/" + agentName + "zh-CN0627.csv";
			PrintStream agentPs = new PrintStream("q/" + agentName);
			
			BufferedReader br = new BufferedReader(new FileReader(path));
			int lineNum = 0;
			String line = "";
			Map<Integer, List<String>> commandSortedByPrioirtyPerDomain = new HashMap<Integer, List<String>>();
			Map<String, List<String>> commandSortedByLengthPerDomain = new HashMap<String, List<String>>();
			List<String> priString5 = new ArrayList<String>();
			List<String> priString10 = new ArrayList<String>();
			List<String> priString15 = new ArrayList<String>();
			List<String> priString20 = new ArrayList<String>();
			List<String> priString25 = new ArrayList<String>();
			List<String> lenString0 = new ArrayList<String>();
			List<String> lenString1 = new ArrayList<String>();
			List<String> lenString2 = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (lineNum != 0) {
				    String[] tokens = line.split(",");
				    String query = tokens[8].replaceAll("\"", "");
				    query = query.replaceAll("[\\s]+", "");
				    queriesCount ++;
				    queriesCountpd ++;
				    
				    if (query.contains("<")) {
					    for (String s : defaultCriteriaDict.keySet()) {
					    	if (query.contains(s)) {
					    		query = query.replaceAll(s, defaultCriteriaDict.get(s));
					    	}
					    }
				    }
				    
				    agentPs.println(query);
				    String queryLength = query.length() + "";
				    int len = query.length();
				    int queryPriority = Integer.parseInt(tokens[10].replaceAll("[\"]", ""));
				    String valuesSortedByLength = query + "\t" + queryPriority + "\t" + queryLength;
				    String valuesSortedByPriority = query + "\t" + queryLength;
				    
				    totallength += len;
				    totallengthpd += len;
				    if (len > maxlength) {
				    	maxlength = len;
				    }
				    if (len > maxlengthpd) {
				    	maxlengthpd = len;
				    }
				    
				    if (Integer.parseInt(queryLength) <= 5) {
				    	priString5.add(valuesSortedByLength);
				    } else if (Integer.parseInt(queryLength) > 5 && Integer.parseInt(queryLength) <= 10) {
				    	priString10.add(valuesSortedByLength);
				    } else if (Integer.parseInt(queryLength) > 10 && Integer.parseInt(queryLength) <= 15) {
				    	priString15.add(valuesSortedByLength);
				    } else if (Integer.parseInt(queryLength) > 15 && Integer.parseInt(queryLength) <= 20) {
				    	priString20.add(valuesSortedByLength);
				    } else {
				    	priString25.add(valuesSortedByLength);
				    }
				    
				    if (queryPriority == 0) {
				    	lenString0.add(valuesSortedByPriority);
				    } else if (queryPriority == 1) {
				    	lenString1.add(valuesSortedByPriority);
				    } else {
				    	lenString2.add(valuesSortedByPriority);
				    }
				}
				lineNum ++;
			}
			br.close();
			averlengthpd = totallengthpd * 1.0 / queriesCountpd;
			ps.println(agentName + ":");
			ps.println("Maximum Character Length for " + agentName + ": " + maxlengthpd);
			ps.println("Average Character Length for " + agentName + ": " + averlengthpd);
			ps.println();
			
			commandSortedByPrioirtyPerDomain.put(0, lenString0);
			commandSortedByPrioirtyPerDomain.put(1, lenString1);
			commandSortedByPrioirtyPerDomain.put(2, lenString2);
			commandSortedByLengthPerDomain.put("5", priString5);
			commandSortedByLengthPerDomain.put("10", priString10);
			commandSortedByLengthPerDomain.put("15", priString15);
			commandSortedByLengthPerDomain.put("20", priString20);
			commandSortedByLengthPerDomain.put("20+", priString25);
			
			commandSortedByPriorityAllDomains.put(agentName, commandSortedByPrioirtyPerDomain);
			commandSortedByLengthAllDomains.put(agentName, commandSortedByLengthPerDomain);
		}
		
		ps.println("Maximum and average lengths for all domains:");
		averlength = totallength * 1.0 / queriesCount;
		ps.println("Maximum Character Length for all Domains: " + maxlength);
		ps.println("Average Character Length for all Domains: " + averlength);
		
		// reports statistics
		ps.println();
		ps.println("Numbers of command variations for each domain sorted by priorities");
		for (String domain : commandSortedByPriorityAllDomains.keySet()) {
			ps.println(domain + ":");
			Map<Integer, List<String>> temp1 = commandSortedByPriorityAllDomains.get(domain);
			for (int pr : temp1.keySet()) {
				ps.println(pr + "\t" + temp1.get(pr).size());
			}
		}
		ps.println();
		ps.println("Numbers of command variations for each domain sorted by length");
		for (String domain : commandSortedByLengthAllDomains.keySet()) {
			ps.println(domain + ":");
			Map<String, List<String>> temp1 = commandSortedByLengthAllDomains.get(domain);
			for (String len : temp1.keySet()) {
				ps.println(len + "\t" + temp1.get(len).size());
			}
		}
	
		ps.close();
	}

}
