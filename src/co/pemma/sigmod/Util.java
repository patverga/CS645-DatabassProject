package co.pemma.sigmod;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Util {
//	public List<Pair<K,V>> heapSort(Map<K,V> vals, int k){
//		
//		PriorityQueue<Pair<Integer, String>> topKPairs = new PriorityQueue<>(k);
//		
//		for (String key : vals.keySet())
//		{
//			if (topKPairs.size() < k) 
//			{
//				topKPairs.add(new ImmutablePair<Integer, String>(vals.get(key), key));
//			}				
//			else if(vals.get(key) > topKPairs.peek().getLeft())
//			{
//				topKPairs.poll();
//				topKPairs.add(new ImmutablePair<Integer, String>(vals.get(key), key));
//			}
//		}
//		while(!topKPairs.isEmpty())
//		{
//			System.out.println(topKPairs.poll().getRight());
//		}
//	}
}
