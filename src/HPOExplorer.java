import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * A HPO data explorer class demostrates loading HPO file, loading query file and generating query results, 
 * as well as generating max path term result
 * 
 * @author cathyyan
 * @since 1.0
 *
 */
public class HPOExplorer {
	
	/**
	 * A map of all terms loaded from the HPO.txt file, key is term id, value is term
	 */
	private Map<String, Term> allTermMap = new HashMap<String, Term>();
	
	/**
	 * Loads the HPO.txt file and stores the all term objects into the map, key is id, value is Term 
	 * 
	 * @throws FileNotFoundException when HPO.txt file does not exist
	 */
	public void loadAllTerms() throws FileNotFoundException {
		File file = new File("HPO.txt");
		
		Scanner sc = new Scanner(file);
		
		Term term = null;
		String termContent = null;
		String id = null;
		String isA = null;
		
		while (sc.hasNextLine()) {
			String nextLine = sc.nextLine();
			if (nextLine.equals("[Term]")) {
				if (term != null) {
					term.setTermContent(termContent);
					term.setId(id);
					term.setIsA(isA);
					
					if (!termContent.contains("is_obsolete: true")) {
						allTermMap.put(id, term);
					}
				}
				term = new Term();
				termContent = "";
				id = null;
				isA = null;
			} else if (term != null){
				termContent = termContent + nextLine + "\r\n";
				if (nextLine.startsWith("id: ") && id == null) {
					String[] split = nextLine.split(": ");
					id = split[1];
				} 
				if (nextLine.startsWith("is_a: ") && isA == null) {
					String[] split = nextLine.split(" ");
					isA = split[1];
				}
			}
		}
		if (term != null) {
			term.setTermContent(termContent);
		}
	
		

	}
	
	/**
	 * Reads the queries.txt file and returns a Query object 
	 * 
	 * @return the query object
	 * @throws FileNotFoundException when the queries.txt file does not exist
	 */
	public Query getQueries() throws FileNotFoundException {
		File file =  new File("queries.txt");
		Query query = null;
		Scanner sc = new Scanner(file);
		
		List<String> ids = new ArrayList<>();
		
		while(sc.hasNextLine()) {
			String nextLine = sc.nextLine();
			String[] split = nextLine.split(": ");
			String queryId = split[1];
			
			ids.add(queryId);
		}
		if(ids.size() != 0) {
			query = new Query();
			query.setIds(ids);
		}
		
		sc.close();
			
		return query;
	}
	
	/**
	 * Generates and puts all the answers to each query into a text file called results.txt
	 * 
	 * @param query - a Query object
	 * @throws IOException - when file cannot be written successfully
	 */
	public void generateTermQueryResult(Query query) throws IOException {
		FileWriter fw = new FileWriter("results.txt");
		List<String> queryIds = query.getIds();
		for(String id : queryIds) { 
			fw.write("[query answer]\r\n");
			String termString = findTermString(allTermMap, id);
			fw.write(termString);
		}
		fw.close();
	}
	
	/**
	 * Generates the path with the longest length and puts all the terms within the path into a text file called mathpath.txt
	 * 
	 * @throws IOException - when file cannot be written successfully
	 */
	public void generateMaxPathTermResult() throws IOException {
		FileWriter fw = new FileWriter("maxpath.txt");
		int maxPath = 0;
		String termIdWithMaxPath = null;
		for(String id : allTermMap.keySet()) { 
			int path = findTermPath(allTermMap, id, 0);
			if (path > maxPath) {
				maxPath = path;
				termIdWithMaxPath = id;
			}
		}
		fw.write("[max_path="+ maxPath + "]\r\n");
		fw.write(findTermString(allTermMap, termIdWithMaxPath));
		fw.close();
	}
	 
	/**
	 * Finds a string with all the term content corresponding to the specific id in the parameter
	 * 
	 * @param terms - a map of all terms
	 * @param id - the term id for the search
	 * @return - all the term content specific to the id
	 */
	private String findTermString(Map<String, Term> terms, String id) {
		String termString = "";
		
		Term term = terms.get(id);
		termString = term.getTermContent();
		String isAId = term.getIsA();
		if (isAId != null) {
			String isATermContent = findTermString(terms, isAId);
			termString = termString + isATermContent;
		}
		return termString;
	}
	
	/**
	 * Finds the number of term path for a given id
	 * 
	 * @param terms - is a map of all terms
	 * @param id - is the term id for the search
	 * @param path - is the previous path from the parent level
	 * @return The new path level
	 * 
	 */
	private int findTermPath(Map<String, Term> terms, String id, int path) {
		Term term = terms.get(id);
		String isAId = term.getIsA();
		if (isAId != null) {
			return findTermPath(terms, isAId, path) + 1;
		} else {
			return path + 1;
		}
	}
	
	/**
	 * The main function
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HPOExplorer hpoExplorer = new HPOExplorer();
		
		System.out.println("Loading all terms...");
		hpoExplorer.loadAllTerms();
		
		System.out.println("Loading queries...");
		Query query = hpoExplorer.getQueries();
		
		System.out.println("Generating query results...");
		hpoExplorer.generateTermQueryResult(query);
		
		System.out.println("Generating max path term result...");
		hpoExplorer.generateMaxPathTermResult();
	}

}
