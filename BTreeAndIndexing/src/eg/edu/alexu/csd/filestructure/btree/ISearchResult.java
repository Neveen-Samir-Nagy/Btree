package eg.edu.alexu.csd.filestructure.btree;

public interface ISearchResult {

	/**
	 * Return the document ID which is an attribute provided with each Wikipedia document, please check the sample data files for more info.
	 * @return
	 */
	public String getId();
	public void setId(String id);
	/**
	 * Return the frequency of the word in the given document.
	 * @return
	 */
	public int getRank();
	public void setRank(int rank);
}
