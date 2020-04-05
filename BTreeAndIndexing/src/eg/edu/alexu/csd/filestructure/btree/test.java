package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.management.RuntimeErrorException;

import org.junit.Assert;

public class test {
	private static int getHeight (IBTreeNode<?, ?> node) {
		if (node.isLeaf()) return 0;

		return node.getNumOfKeys() > 0 ? 1 + getHeight(node.getChildren().get(0)) : 0;
	}
	private static boolean verifyBTree (IBTreeNode<?, ?> node, int lvl, int height, int t, IBTreeNode<?, ?> root) {
		if (!node.equals(root)) 
			if (node.getNumOfKeys() < t - 1 || node.getNumOfKeys() > 2 * t - 1)
				return false;
		boolean ans = true;
		if (!node.isLeaf()) {
			for (int i = 0; i <= node.getNumOfKeys(); i++) {
				ans = ans && verifyBTree(node.getChildren().get(i), lvl + 1, height, t, root);
				if (!ans) break;
			}

		}else {
			ans = ans && (lvl == height);
		}
		return ans;
	} 
	private static void traverseTreeInorder(IBTreeNode<Integer, String> node, List<Integer> keys, List<String> vals) {
		int i; 
		for (i = 0; i < node.getNumOfKeys(); i++) 
		{ 

			if (!node.isLeaf()) 
				traverseTreeInorder(node.getChildren().get(i), keys, vals);
			keys.add(node.getKeys().get(i));
			vals.add(node.getValues().get(i));
		} 
		if (!node.isLeaf()) 
			traverseTreeInorder(node.getChildren().get(i), keys, vals);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ISearchEngine searchEngine = (ISearchEngine) TestRunner.getImplementationInstanceForInterface(ISearchEngine.class, new Object[]{100});
		/**
		 * This test should be modified according to the testing directory and the search query.
		 * You should make sure that the test can support multiple file in the same directory.
		 * You should test your implementation against cases including:
		 * 1- word that does not exist in tree.
		 * 2- word exists.
		 * 3- lower case, upper case, mix btw lower and upper, e.g.. THE, the, ThE, tHE....
		 * According to each change you should modify the expected variable to have the expected outcome.
		 */
		try {
			searchEngine.indexDirectory("res");
			searchEngine.deleteWebPage("res\\wiki_00");
			List<ISearchResult> expected = Arrays.asList(new SearchResult[]{new SearchResult("7702780", 34), new SearchResult("7702785", 2)});
			List<ISearchResult> actual = searchEngine.searchByWordWithRanking("ThE");
			System.out.println(actual.get(0).getId());
			System.out.println(actual.get(0).getRank());
			System.out.println(actual.get(1).getId());
			System.out.println(actual.get(1).getRank());
//			for (ISearchResult searchRes : actual) {
//				System.out.println(searchRes.toString());
//			}
//			Collections.sort(actual, new Comparator<ISearchResult>() {
//				@Override
//				public int compare(ISearchResult o1, ISearchResult o2) {
//					return o1.getRank() - o2.getRank();
//				}
//			});
			for (int i = 0; i < expected.size(); i++) {
				Assert.assertEquals(expected.get(i).getId(), actual.get(i).getId());
				Assert.assertEquals(expected.get(i).getRank(), actual.get(i).getRank());
			}
		} catch (Throwable e) {
			TestRunner.fail("Fail to delete web page", e);
		}
	}

}
