
/*
 * Swapnil Kumar
 * swapnilk@ksu.edu
 * Kansas State University 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AntiFraud {

	private static class Transact {
		long src;
		long dest;

		public Transact(long src, long dest) {
			this.src = src;
			this.dest = dest;
		}
	}

	static Map<Long, Set<Long>> neighbors;
	static List<Transact> toTest;

	public static void main(String[] args) {
		String batchFile = args[0];
		String streamFile = args[1];
		String output1 = args[2];
		String output2 = args[3];
		String output3 = args[4];
		//System.out.println("Start graph generation");
		constructGraph(batchFile);
		//System.out.println("Done graph generation");
		//System.out.println("Start stream file");
		readStreamFile(streamFile);
		//System.out.println("Done stream file");
		predictResult(1, output1);
		predictResult(2, output2);
		predictResult(4, output3);
	}
	
	private static void predictResult(int level, String fileName){
		List<String> record = new ArrayList<String>();
		for(Transact item : toTest){
			record.add(breadthFirstSearch(item.src, item.dest, level)? "trusted\n" : "unverified\n");
		}
		try(BufferedWriter br = new BufferedWriter(new FileWriter(fileName))){
			for(String s : record){
				br.write(s);
			}
		    br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	    
	}

	private static void constructGraph(String fileName) {
		String line = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			neighbors = new HashMap<Long, Set<Long>>();
			br.readLine();
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if(line.equals("")) continue;
				String[] lineSplit = line.split(",");
				if(lineSplit.length > 5) continue;
				long src = Long.parseLong(lineSplit[1].trim());
				long dest = Long.parseLong(lineSplit[2].trim());
				if (neighbors.containsKey(src)) {
					Set<Long> temp = neighbors.get(src);
					temp.add(dest);
					neighbors.put(src, temp);
				} else {
					Set<Long> val = new HashSet<Long>(); // Hash vs Tree check
															// performance
					val.add(dest);
					neighbors.put(src, val);
				}
				
				if (neighbors.containsKey(dest)) {
					Set<Long> temp = neighbors.get(dest);
					temp.add(src);
					neighbors.put(dest, temp);
				} else {
					Set<Long> val = new HashSet<Long>(); // Hash vs Tree check
															// performance
					val.add(src);
					neighbors.put(dest, val);
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// System.out.println(line);
			e.printStackTrace();
		}
	}

	private static void readStreamFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			toTest = new ArrayList<>();
			br.readLine();
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				if(line.equals("")) continue;
				String[] lineSplit = line.split(",");
				if(lineSplit.length > 5) continue;
				long src = Long.parseLong(lineSplit[1].trim());
				long dest = Long.parseLong(lineSplit[2].trim());
				toTest.add(new Transact(src, dest));
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean breadthFirstSearch(long src, long dest, int level){
		Deque<Long> queue = new ArrayDeque<>();
		Deque<Long> nextQueue = new ArrayDeque<>();
		queue.offer(src);
		while(!queue.isEmpty() && level >= 0){
			long top = queue.remove();
			if(dest == top) return true;
			Set<Long> neighbor = neighbors.get(top);
			if(neighbor == null) return true;
			for(long n : neighbor){
				nextQueue.offer(n);
			}
			
			if(queue.isEmpty()){
				level -= 1;
				queue = nextQueue;
				nextQueue = new ArrayDeque<>();
			}
		}
		return false;
	}
}
