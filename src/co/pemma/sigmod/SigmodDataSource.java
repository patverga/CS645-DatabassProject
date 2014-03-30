package co.pemma.sigmod;

import java.util.List;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.facts.IDataSource;
import org.deri.iris.storage.IRelation;

public class SigmodDataSource implements IDataSource{

	@Override
	public void get(IPredicate p, ITuple from, ITuple to, IRelation r) {
		List<ITuple> tuples = CreateRelations.getTuples(p.getPredicateSymbol(), from, to);
		for(ITuple t: tuples)
			r.add(t);
	}

	

}
