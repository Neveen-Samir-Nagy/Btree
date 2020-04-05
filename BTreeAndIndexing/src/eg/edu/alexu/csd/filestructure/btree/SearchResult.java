package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;

public class SearchResult implements ISearchResult {

	private String id = "";
	private int rank = 0;

	public SearchResult(String id, int rank) {
		this.id = id;
		this.rank = rank;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if (id == null) {
			throw new RuntimeErrorException(null);
		}
		this.id = id;
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public void setRank(int rank) {
		this.rank = rank;
	}

}
