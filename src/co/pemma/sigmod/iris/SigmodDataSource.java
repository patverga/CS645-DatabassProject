package co.pemma.sigmod.iris;

import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.storage.IRelation;

import co.pemma.sigmod.Util;

public class SigmodDataSource implements IDataSource
{
	Map<String, List<String>> colsToUse;
	String dataLocation;
	
	public SigmodDataSource(int queryNumber, String dataLocation) 
	{		
		this.dataLocation = dataLocation;
		
		switch(queryNumber)
		{
		case 1:
			colsToUse = Util.query1Columns;
			break;

		case 2:
			colsToUse = Util.query2Columns;
			break;	

		case 3:
			colsToUse = Util.query3Columns;
			break;

		case 4:
			
			break;
		}
	}

	@Override
	public void get(IPredicate p, ITuple from, ITuple to, IRelation r) 
	{
		
		List<ITuple> tuples = CreateRelations.getTuples(dataLocation, p.getPredicateSymbol(), colsToUse.get(p.toString()), from, to);
		for(ITuple t: tuples)
			r.add(t);
	}

	@Override
	public void put(IPredicate p, IRelation r) 
	{
//		CreateRelations.putTuples(p.getPredicateSymbol(), r);
	}
}
