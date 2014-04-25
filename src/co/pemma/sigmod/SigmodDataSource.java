package co.pemma.sigmod;

import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.storage.IRelation;

public class SigmodDataSource implements IDataSource
{
	Map<String, List<String>> colsToUse;
	
	public SigmodDataSource(int queryNumber) 
	{		
		switch(queryNumber)
		{
		case 1:
			
			break;

		case 2:
			
			break;	

		case 3:
			colsToUse = Util.query3Columns;
			break;

		case 4:
			
			break;
		}
	}

	@Override
	public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
		List<ITuple> tuples = CreateRelations.getTuples(p.getPredicateSymbol(), colsToUse.get(r), from, to);
		for(ITuple t: tuples)
			r.add(t);
	}

	@Override
	public void put(IPredicate p, IRelation r) {
		CreateRelations.putTuples(p.getPredicateSymbol(), r);
	}
}
