package co.pemma.sigmod.iris;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.Pair;

public class SigmodComparator implements Comparator<Pair<Integer,String>>{

	@Override
	public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
		if(o1.getLeft().compareTo(o2.getLeft()) != 0)
			return o1.getLeft().compareTo(o2.getLeft());
		if(o1.getRight().length() < o2.getRight().length())
			return 1;
		if(o1.getRight().length() > o2.getRight().length())
			return -1;
		return -o1.getRight().compareTo(o2.getRight());
	}

}
