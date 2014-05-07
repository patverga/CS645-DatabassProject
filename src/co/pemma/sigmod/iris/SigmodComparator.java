package co.pemma.sigmod.iris;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.Pair;

public class SigmodComparator implements Comparator<Pair<Integer,String>>
{
	int query;

	public SigmodComparator(int query)
	{
		this.query = query;
	}

	@Override
	public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) 
	{
		if (query == 2)
		{
			if(o1.getLeft().compareTo(o2.getLeft()) != 0)
				return o1.getLeft().compareTo(o2.getLeft());
			// Integer is the same, compare the strings - return letters before digits
			if (o1.getRight().matches("[0-9]+.*") && o2.getRight().matches("[a-zA-Z]+.*"))
				return -1;
			else if  (o1.getRight().matches("[a-zA-Z]+.*") && o2.getRight().matches("[0-9]+.*"))
				return 1;
			return -o1.getRight().compareTo(o2.getRight());
		}
		else if (query == 3)
		{
			if(o1.getLeft().compareTo(o2.getLeft()) != 0)
				return o1.getLeft().compareTo(o2.getLeft());
			if(o1.getRight().length() < o2.getRight().length())
				return 1;
			if(o1.getRight().length() > o2.getRight().length())
				return -1;
			return -o1.getRight().compareTo(o2.getRight());			
		}

		else
		{
			if(o1.getLeft().compareTo(o2.getLeft()) != 0)
				return o1.getLeft().compareTo(o2.getLeft());
			return o1.getRight().compareTo(o2.getRight());				
		}
	}
}
