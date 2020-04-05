package eg.edu.alexu.csd.filestructure.btree;

public interface IBTree<K extends Comparable<K>, V> {

	/**
	 * Return the minimum degree of the given Btree. 
	 * The minimum degree of the Btree is sent as a parameter t the constructor.
	 * @return
	 */
	public int getMinimumDegree();
	/**
	 * Return the root of the given Btree.
	 * @return
	 */
	public IBTreeNode<K, V> getRoot();
	/**
	 * Insert the given key in the Btree. If the key is already in the Btree, ignore the call of this method.
	 * @param key
	 * @param value
	 */
	public void insert(K key, V value);
	/**
	 * Search for the given key in the BTree.
	 * @param key
	 * @return
	 */
	public V search(K key);
	/**
	 * Delete the node with the given key from the Btree.
	 * Return true in case of success and false otherwise.
	 * @param key
	 * @return
	 */
	public boolean delete(K key);
	
}
