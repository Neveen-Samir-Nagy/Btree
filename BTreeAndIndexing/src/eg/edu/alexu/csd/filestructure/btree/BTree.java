package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
	private IBTreeNode<K, V> BNode = new BTreeNode<K, V>(0, true, new ArrayList<K>(), new ArrayList<V>(),
			new ArrayList<IBTreeNode<K, V>>());
	private int t = 0;

	public BTree(int t) {
		if (t < 2) {
			throw new RuntimeErrorException(null);
		}
		BNode.setNumOfKeys(0);
		this.t = t;
	}

	@Override
	public int getMinimumDegree() {
		return t;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		if (BNode.getNumOfKeys() == 0 && BNode.isLeaf() && BNode.getKeys().size() == 0
				&& BNode.getValues().size() == 0) {
			return null;
		}
		return BNode;
	}

	@Override
	public void insert(K key, V value) {
		if (key == null || value == null) {
			throw new RuntimeErrorException(null);
		}
		if (search(key) != null) {
			return;
		}
		IBTreeNode<K, V> x = BNode;
		if (x.getNumOfKeys() == 0) {
			x.getKeys().add(key);
			x.getValues().add(value);
			x.setNumOfKeys(x.getNumOfKeys() + 1);
			BNode = x;
		} else if (x.getNumOfKeys() < (2 * t - 1)) {
			insertInNonFull(x, key, value);
		} else {
			IBTreeNode<K, V> s = new BTreeNode<K, V>(0, true, new ArrayList<K>(), new ArrayList<V>(),
					new ArrayList<IBTreeNode<K, V>>());
			s.setLeaf(false);
			s.getChildren().add(x);
			s.setNumOfKeys(0);
			split_Child(s, 1, x);
			insertInNonFull(s, key, value);
			BNode = s;
		}
	}

	private void insertInNonFull(IBTreeNode<K, V> x, K key, V value) {
		int i = x.getNumOfKeys() - 1;
		@SuppressWarnings("unused")
		boolean flag = false;
		if (x.isLeaf()) {

			while (i > 0 && key.compareTo(x.getKeys().get(i)) < 0) {
				flag = true;
				i--;
			}
			if (key.compareTo(x.getKeys().get(i)) < 0) {
				x.getKeys().add(i, key);
				x.getValues().add(i, value);
			} else {
				x.getKeys().add(i + 1, key);
				x.getValues().add(i + 1, value);
			}
			x.setNumOfKeys(x.getNumOfKeys() + 1);
		} else {
			i = x.getNumOfKeys() - 1;
			while (i > 0 && key.compareTo(x.getKeys().get(i)) < 0) {
				i--;
			}
			if (key.compareTo(x.getKeys().get(i)) > 0) {
				i++;
			}
			if (x.getChildren().get(i).getNumOfKeys() == (2 * t - 1)) {
				split_Child(x, i, x.getChildren().get(i));
				if (key.compareTo(x.getKeys().get(i)) > 0) {
					i++;
				}
			}
			insertInNonFull(x.getChildren().get(i), key, value);
		}
	}

	private void split_Child(IBTreeNode<K, V> x, int i, IBTreeNode<K, V> y) {
		IBTreeNode<K, V> z = new BTreeNode<K, V>(0, true, new ArrayList<K>(), new ArrayList<V>(),
				new ArrayList<IBTreeNode<K, V>>());
		z.setNumOfKeys(t - 1);
		z.setLeaf(y.isLeaf());
		for (int j = 0; j < t - 1; j++) {
			z.getKeys().add(y.getKeys().get(j + t));
			z.getValues().add(y.getValues().get(j + t));
		}
		if (!y.isLeaf()) {
			for (int j = 0; j < t; j++) {
				z.getChildren().add(j, y.getChildren().get(j + t));
			}
		}
		for (int k = 0; k < t - 1; k++) {
			y.getKeys().remove(y.getKeys().size() - 1);
			y.getValues().remove(y.getValues().size() - 1);
			if (!y.isLeaf()) {
				y.getChildren().remove(y.getChildren().size() - 1);
			}
		}
		y.setNumOfKeys(t - 1);
		if (i == x.getChildren().size()) {
			x.getChildren().add(i, z);
		} else {
			x.getChildren().add(i + 1, z);
		}
		if (i == 0) {
			if (y.getKeys().get(t - 1).compareTo(x.getKeys().get(i)) > 0) {
				x.getKeys().add(i + 1, y.getKeys().get(t - 1));
				x.getValues().add(i + 1, y.getValues().get(t - 1));
			} else {
				x.getKeys().add(i, y.getKeys().get(t - 1));
				x.getValues().add(i, y.getValues().get(t - 1));
			}
		} else if (x.getNumOfKeys() != 0) {
			if (y.getKeys().get(t - 1).compareTo(x.getKeys().get(i - 1)) > 0) {
				x.getKeys().add(i, y.getKeys().get(t - 1));
				x.getValues().add(i, y.getValues().get(t - 1));
			} else {
				x.getKeys().add(i - 1, y.getKeys().get(t - 1));
				x.getValues().add(i - 1, y.getValues().get(t - 1));
			}
		} else {
			x.getKeys().add(y.getKeys().get(t - 1));
			x.getValues().add(y.getValues().get(t - 1));
		}
		y.getKeys().remove(t - 1);
		y.getValues().remove(t - 1);
		x.setNumOfKeys(x.getNumOfKeys() + 1);
	}

	@Override
	public V search(K key) {
		if (key == null) {
			throw new RuntimeErrorException(null);
		}
		IBTreeNode<K, V> x = BNode;
		int i = 0;
		for (i = 0; i < x.getNumOfKeys(); i++) {
			if (key.compareTo(x.getKeys().get(i)) > 0) {
				continue;
			} else if (key.compareTo(x.getKeys().get(i)) == 0) {
				return x.getValues().get(i);
			} else {
				if (!x.isLeaf()) {
					return searchWithNode(x.getChildren().get(i), key);
				}
			}
		}
		if (x.isLeaf()) {
			return null;
		} else {
			return searchWithNode(x.getChildren().get(i), key);
		}
	}

	private V searchWithNode(IBTreeNode<K, V> x, K key) {
		int i = 0;
		for (i = 0; i < x.getNumOfKeys(); i++) {
			if (key.compareTo(x.getKeys().get(i)) > 0) {
				continue;
			} else if (key.compareTo(x.getKeys().get(i)) == 0) {
				return x.getValues().get(i);
			} else {
				if (!x.isLeaf()) {
					return searchWithNode(x.getChildren().get(i), key);
				}
			}
		}
		if (x.isLeaf()) {
			return null;
		} else {
			return searchWithNode(x.getChildren().get(i), key);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean delete(K key) {
		if (getRoot() == null) {
			throw new RuntimeErrorException(null);
		}

		if (key == null) {
			throw new RuntimeErrorException(null);
		}
		// Call the remove function for root
		boolean r = ((BTreeNode) BNode).remove(key);

		// If the root node has 0 keys, make its first child as the new root
		// if it has a child, otherwise set root as NULL
		if (BNode.getNumOfKeys() == 0) {
			@SuppressWarnings("unused")
			BTreeNode<K, V> tmp = (BTreeNode<K, V>) BNode;
			if (BNode.isLeaf()) {
				BNode = new BTreeNode<K, V>(0, true, new ArrayList<K>(), new ArrayList<V>(),
						new ArrayList<IBTreeNode<K, V>>());
			} else
				BNode = BNode.getChildren().get(0);

			// Free the old root
			// delete tmp;
		}

		return r;
	}

	@SuppressWarnings("hiding")
	private class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

		private int NumOfKeys = 0;
		private boolean isLeaf = false;
		private List<K> Keys = new ArrayList<K>();
		private List<V> Values = new ArrayList<V>();
		private List<IBTreeNode<K, V>> Children = new ArrayList<IBTreeNode<K, V>>();

		public BTreeNode(int NumOfKeys, boolean isLeaf, List<K> Keys, List<V> Values, List<IBTreeNode<K, V>> Children) {
			// TODO Auto-generated constructor stub
			this.NumOfKeys = NumOfKeys;
			this.isLeaf = isLeaf;
			this.Keys = Keys;
			this.Values = Values;
			this.Children = Children;
		}

		@Override
		public int getNumOfKeys() {

			return NumOfKeys;
		}

		@Override
		public void setNumOfKeys(int numOfKeys) {
			// TODO Auto-generated method stub
			this.NumOfKeys = numOfKeys;
		}

		@Override
		public boolean isLeaf() {
			// TODO Auto-generated method stub
			return isLeaf;
		}

		@Override
		public void setLeaf(boolean isLeaf) {
			// TODO Auto-generated method stub
			this.isLeaf = isLeaf;
		}

		@Override
		public List<K> getKeys() {
			// TODO Auto-generated method stub
			return Keys;
		}

		@Override
		public void setKeys(List<K> keys) {
			// TODO Auto-generated method stub
			this.Keys = keys;
		}

		@Override
		public List<V> getValues() {
			// TODO Auto-generated method stub
			return Values;
		}

		@Override
		public void setValues(List<V> values) {
			// TODO Auto-generated method stub
			this.Values = values;
		}

		@Override
		public List<IBTreeNode<K, V>> getChildren() {
			// TODO Auto-generated method stub
			return Children;
		}

		@Override
		public void setChildren(List<IBTreeNode<K, V>> children) {
			// TODO Auto-generated method stub
			this.Children = children;
		}

		private int findKey(K k) {
			int idx = 0;
			while (idx < NumOfKeys && (Keys.get(idx)).compareTo(k) < 0) {
				idx++;
			}
			return idx;

		}

		public boolean remove(K k) {
			int idx = findKey(k);
			boolean re = true;
			// The key to be removed is present in this node
			if (idx < NumOfKeys && Keys.get(idx).compareTo(k) == 0) {

				// If the node is a leaf node - removeFromLeaf is called
				// Otherwise, removeFromNonLeaf function is called

				if (isLeaf()) {
					removeFromLeaf(idx);

				}

				else
					re = removeFromNonLeaf(idx);

			} else {

				// If this node is a leaf node, then the key is not present in tree
				if (isLeaf()) {

					return false;
				}

				// The key to be removed is present in the sub-tree rooted with this node
				// The flag indicates whether the key is present in the sub-tree rooted
				// with the last child of this node
				boolean flag = false;
				if (idx == NumOfKeys) {
					flag = true;
				}

				// If the child where the key is supposed to exist has less that t keys,
				// we fill that child

				if (Children.get(idx).getNumOfKeys() < t)
					fill(idx);
				// If the last child has been merged, it must have merged with the previous
				// child and so we recurse on the (idx-1)th child. Else, we recurse on the
				// (idx)th child which now has atleast t keys
				if (flag && idx > NumOfKeys) {

					re = ((BTree<K, V>.BTreeNode<K, V>) Children.get(idx - 1)).remove(k);
				} else {

					re = ((BTree<K, V>.BTreeNode<K, V>) Children.get(idx)).remove(k);
				}

			}

			return re;
		}

		private void removeFromLeaf(int idx) {
			// Move all the keys after the idx-th pos one place backward
			for (int i = idx + 1; i < NumOfKeys; i++) {
				Keys.set(i - 1, Keys.get(i));
				Values.set(i - 1, Values.get(i));
			}

			// Reduce the count of keys
			NumOfKeys--;

			return;
		}

		// A function to remove the idx-th key from this node - which is a non-leaf node
		private boolean removeFromNonLeaf(int idx) {
			boolean r = true;
			K k = Keys.get(idx);

			// If the child that precedes k (C[idx]) has atleast t keys,
			// find the predecessor 'pred' of k in the subtree rooted at
			// C[idx]. Replace k by pred. Recursively delete pred
			// in C[idx]
			if (Children.get(idx).getNumOfKeys() >= t) {
				K pred = getPred(idx);
				V p = getPredV(idx);

				Keys.set(idx, pred);
				Values.set(idx, p);
				r = ((BTree<K, V>.BTreeNode<K, V>) Children.get(idx)).remove(pred);

			}

			// If the child C[idx] has less that t keys, examine C[idx+1].
			// If C[idx+1] has atleast t keys, find the successor 'succ' of k in
			// the subtree rooted at C[idx+1]
			// Replace k by succ
			// Recursively delete succ in C[idx+1]
			else if (Children.get(idx + 1).getNumOfKeys() >= t) {
				K succ = getSucc(idx);
				V s = getSuccV(idx);
				Keys.set(idx, succ);
				Values.set(idx, s);
				r = ((BTree<K, V>.BTreeNode<K, V>) Children.get(idx + 1)).remove(succ);
			}

			// If both C[idx] and C[idx+1] has less that t keys,merge k and all of C[idx+1]
			// into C[idx]
			// Now C[idx] contains 2t-1 keys
			// Free C[idx+1] and recursively delete k from C[idx]
			else {
				merge(idx);
				r = ((BTree<K, V>.BTreeNode<K, V>) Children.get(idx)).remove(k);
			}
			return r;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private K getPred(int idx) {
			// Keep moving to the right most node until we reach a leaf
			BTreeNode cur = (BTreeNode) Children.get(idx);
			while (!cur.isLeaf)
				cur = (BTreeNode) cur.Children.get(cur.NumOfKeys);

			// Return the last key of the leaf
			return (K) cur.Keys.get(cur.NumOfKeys - 1);
		}

		@SuppressWarnings("unchecked")
		private V getPredV(int idx) {
			// Keep moving to the right most node until we reach a leaf
			@SuppressWarnings("rawtypes")
			BTreeNode cur = (BTreeNode) Children.get(idx);
			while (!cur.isLeaf)
				cur = (BTreeNode<K, V>) cur.Children.get(cur.NumOfKeys);

			// Return the last key of the leaf
			return (V) cur.Values.get(cur.NumOfKeys - 1);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private K getSucc(int idx) {

			// Keep moving the left most node starting from C[idx+1] until we reach a leaf
			BTreeNode cur = (BTreeNode) Children.get(idx + 1);
			while (!cur.isLeaf)
				cur = (BTreeNode) cur.Children.get(0);

			// Return the first key of the leaf
			return (K) cur.Keys.get(0);
		}

		private V getSuccV(int idx) {

			// Keep moving the left most node starting from C[idx+1] until we reach a leaf
			BTreeNode<K, V> cur = (BTreeNode<K, V>) Children.get(idx + 1);
			while (!cur.isLeaf)
				cur = (BTreeNode<K, V>) cur.Children.get(0);

			// Return the first key of the leaf
			return (V) cur.Values.get(0);
		}

		// A function to fill child C[idx] which has less than t-1 keys
		private void fill(int idx) {

			// If the previous child(C[idx-1]) has more than t-1 keys, borrow a key
			// from that child
			if (idx != 0 && Children.get(idx - 1).getNumOfKeys() >= t)
				borrowFromPrev(idx);

			// If the next child(C[idx+1]) has more than t-1 keys, borrow a key
			// from that child
			else if (idx != NumOfKeys && Children.get(idx + 1).getNumOfKeys() >= t)
				borrowFromNext(idx);

			// Merge C[idx] with its sibling
			// If C[idx] is the last child, merge it with with its previous sibling
			// Otherwise merge it with its next sibling
			else {
				if (idx != NumOfKeys)
					merge(idx);
				else
					merge(idx - 1);
			}
			return;
		}

		private void borrowFromPrev(int idx) {

			BTreeNode<K, V> child = (BTreeNode<K, V>) Children.get(idx);
			BTreeNode<K, V> sibling = (BTreeNode<K, V>) Children.get(idx - 1);

			// The last key from C[idx-1] goes up to the parent and key[idx-1]
			// from parent is inserted as the first key in C[idx]. Thus, the loses
			// sibling one key and child gains one key

			// Moving all key in C[idx] one step ahead
			for (int i = child.NumOfKeys - 1; i >= 0; --i) {
				if (i >= child.NumOfKeys - 1) {
					child.Keys.add(i + 1, child.Keys.get(i));
					child.Values.add(i + 1, child.Values.get(i));
				} else {
					child.Keys.set(i + 1, child.Keys.get(i));
					child.Values.set(i + 1, child.Values.get(i));
				}
			}

			// If C[idx] is not a leaf, move all its child pointers one step ahead
			if (!child.isLeaf) {
				for (int i = child.NumOfKeys; i >= 0; --i) {
					if (i >= child.NumOfKeys) {
						child.Children.add(i + 1, child.Children.get(i));
					} else {
						child.Children.set(i + 1, child.Children.get(i));
					}
				}
			}

			// Setting child's first key equal to keys[idx-1] from the current node
			if (child.NumOfKeys == 0) {
				child.Keys.add(0, Keys.get(idx - 1));
				child.Values.add(0, Values.get(idx - 1));
			} else {
				child.Keys.set(0, Keys.get(idx - 1));
				child.Values.set(0, Values.get(idx - 1));
			}

			// Moving sibling's last child as C[idx]'s first child
			if (!child.isLeaf) {
				if (child.Children.size() == 0) {
					child.Children.add(0, sibling.Children.get(sibling.NumOfKeys));
				} else
					child.Children.set(0, sibling.Children.get(sibling.NumOfKeys));
			}
			// Moving the key from the sibling to the parent
			// This reduces the number of keys in the sibling
			if (idx - 1 >= NumOfKeys) {
				Keys.add(idx - 1, (K) sibling.Keys.get(sibling.NumOfKeys - 1));
				Values.add(idx - 1, (V) sibling.Values.get(sibling.NumOfKeys - 1));
			} else {
				Keys.set(idx - 1, (K) sibling.Keys.get(sibling.NumOfKeys - 1));
				Values.set(idx - 1, (V) sibling.Values.get(sibling.NumOfKeys - 1));
			}

			child.NumOfKeys++;
			sibling.NumOfKeys--;

			return;
		}

		// A function to borrow a key from the C[idx+1] and place
		// it in C[idx]
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void borrowFromNext(int idx) {

			BTreeNode child = (BTreeNode) Children.get(idx);
			BTreeNode sibling = (BTreeNode) Children.get(idx + 1);

			// keys[idx] is inserted as the last key in C[idx]

			child.Keys.add(child.NumOfKeys, Keys.get(idx));
			child.Values.add(child.NumOfKeys, Values.get(idx));

			// Sibling's first child is inserted as the last child
			// into C[idx]
			if (!(child.isLeaf))
				child.Children.add(child.NumOfKeys + 1, sibling.Children.get(0));

			// The first key from sibling is inserted into keys[idx]

			if (idx >= NumOfKeys) {
				Keys.add(idx, (K) sibling.Keys.get(0));
				Values.add(idx, (V) sibling.Values.get(0));
			} else {
				Keys.set(idx, (K) sibling.Keys.get(0));
				Values.set(idx, (V) sibling.Values.get(0));
			}

			// Moving all keys in sibling one step behind
			for (int i = 1; i < sibling.NumOfKeys; ++i) {
				sibling.Keys.set(i - 1, sibling.Keys.get(i));
				sibling.Values.set(i - 1, sibling.Values.get(i));
			}

			// Moving the child pointers one step behind
			if (!sibling.isLeaf) {
				for (int i = 1; i <= sibling.NumOfKeys; ++i)
					sibling.Children.set(i - 1, sibling.Children.get(i));
			}

			// Increasing and decreasing the key count of C[idx] and C[idx+1]
			// respectively
			child.NumOfKeys++;
			sibling.NumOfKeys--;

			return;
		}

		// A function to merge C[idx] with C[idx+1]
		// C[idx+1] is freed after merging
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void merge(int idx) {
			BTreeNode child = (BTreeNode) Children.get(idx);
			BTreeNode sibling = (BTreeNode) Children.get(idx + 1);

			// Pulling a key from the current node and inserting it into (t-1)th
			// position of C[idx]
			if (t - 1 >= child.NumOfKeys) {
				child.Keys.add(t - 1, Keys.get(idx));
				child.Values.add(t - 1, Values.get(idx));
			} else {
				child.Keys.set(t - 1, Keys.get(idx));
				child.Values.set(t - 1, Values.get(idx));
			}

			// Copying the keys from C[idx+1] to C[idx] at the end
			for (int i = 0; i < sibling.NumOfKeys; ++i) {
				if (i + t >= child.NumOfKeys) {
					child.Keys.add(i + t, sibling.Keys.get(i));
					child.Values.add(i + t, sibling.Values.get(i));
				} else {
					child.Keys.set(i + t, sibling.Keys.get(i));
					child.Values.set(i + t, sibling.Values.get(i));
				}

			}

			// Copying the child pointers from C[idx+1] to C[idx]
			if (!child.isLeaf) {
				for (int i = 0; i <= sibling.NumOfKeys; ++i) {
					if (i + t > child.NumOfKeys) {
						child.Children.add(i + t, sibling.Children.get(i));
					} else {
						child.Children.set(i + t, sibling.Children.get(i));
					}

				}

			}

			// Moving all keys after idx in the current node one step before -
			// to fill the gap created by moving keys[idx] to C[idx]
			for (int i = idx + 1; i < NumOfKeys; ++i) {
				Keys.set(i - 1, Keys.get(i));
				Values.set(i - 1, Values.get(i));
			}

			// Moving the child pointers after (idx+1) in the current node one
			// step before
			for (int i = idx + 2; i <= NumOfKeys; ++i)
				Children.set(i - 1, Children.get(i));

			// Updating the key count of child and the current node
			child.NumOfKeys = child.NumOfKeys + sibling.NumOfKeys + 1;
			NumOfKeys--;

			// Freeing the memory occupied by sibling
			// delete(sibling);
			return;
		}

	}

}
