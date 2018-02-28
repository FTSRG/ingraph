package ingraph.driver.ingraph;

import ingraph.driver.data.IngraphQueryHandler;
import ingraph.ire.Indexer;
import ingraph.ire.IngraphIncrementalAdapter;
import ingraph.ire.IngraphOneTimeAdapter;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.TypeSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class IngraphSession implements Session {

	final Indexer indexer = new Indexer();
	int oneTimeQueryIndex = 0;

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public StatementResult run(String statementTemplate, Value parameters) {
		return run(statementTemplate, parameters.asMap());
	}

	@Override
	public StatementResult run(String statementTemplate, Map<String, Object> statementParameters) {
		final IngraphOneTimeAdapter adapter = new IngraphOneTimeAdapter(statementTemplate, "onetime" + oneTimeQueryIndex, indexer);
		oneTimeQueryIndex++;
		adapter.terminate();
		return null;
	}

	@Override
	public StatementResult run(String statementTemplate, Record statementParameters) {
		return run(statementTemplate, statementParameters.asMap());
	}

	@Override
	public StatementResult run(String statementTemplate) {
		return run(statementTemplate, Collections.emptyMap());
	}

	@Override
	public StatementResult run(Statement statement) {
		return run(statement.text(), statement.parameters());
	}

	@Override
	public TypeSystem typeSystem() {
		return null;
	}

	@Override
	public Transaction beginTransaction() {
		return new IngraphTransaction(this);
	}

	@Override
	public Transaction beginTransaction(String bookmark) {
		throw new UnsupportedOperationException("Bookmarks are not supported.");
	}

	@Override
	public <T> T readTransaction(TransactionWork<T> work) {
		throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public <T> T writeTransaction(TransactionWork<T> work) {
		throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public String lastBookmark() {
		throw new UnsupportedOperationException("Bookmarks are not supported.");
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public void close() {
		for (IngraphIncrementalAdapter adapter : adapters) {
			adapter.engine().shutdown();
		}
	}

	private ArrayList<IngraphIncrementalAdapter> adapters = new ArrayList<>();

	public IngraphQueryHandler registerQuery(String queryName, String querySpecification, Map<String, Object> statementParameters) {
		final IngraphIncrementalAdapter adapter = new IngraphIncrementalAdapter(querySpecification, queryName, indexer);
		adapters.add(adapter);
		return new IngraphQueryHandler(adapter);
	}

	public IngraphQueryHandler registerQuery(String queryName, String querySpecification) {
		return registerQuery(queryName, querySpecification, Collections.emptyMap());
	}

}
