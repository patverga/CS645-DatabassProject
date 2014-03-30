package co.pemma.sigmod;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.Pair;

public class SigmodComparator implements Comparator<Pair<Integer,String>>{

	@Override
	public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
		if(o1.getLeft().compareTo(o2.getLeft()) != 0)
			return o1.getLeft().compareTo(o2.getLeft());
		return -o1.getRight().compareTo(o2.getRight());
	}

}
