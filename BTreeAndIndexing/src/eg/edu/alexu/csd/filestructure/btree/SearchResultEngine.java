package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

import javax.management.RuntimeErrorException;

public class SearchResultEngine implements ISearchEngine {

	@SuppressWarnings("unused")
	private int t = 0;
	private IBTree<String, List<ISearchResult>> btree;

	public SearchResultEngine(int t) {
		this.t = t;
		btree = new BTree<String, List<ISearchResult>>(t);
	}

	@Override
	public void indexWebPage(String filePath) {
		if (filePath == null || filePath == "" || !new File(filePath).exists()) {
			throw new RuntimeErrorException(null);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Document document = builder.parse(filePath);
			document.getDocumentElement().normalize();
			// Element root = document.getDocumentElement();
			NodeList nList = document.getElementsByTagName("doc");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				// System.out.println(""); //Just a separator
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// Print each employee's detail
					HashMap<String, Integer> map = new HashMap<String, Integer>();
					Element eElement = (Element) node;
					String id = eElement.getAttribute("id");
					String value = eElement.getTextContent();
					value = value.toLowerCase();
					ArrayList<String> words = new ArrayList<String>();
					StringTokenizer t = new StringTokenizer(value);
					String word = "";
					while (t.hasMoreTokens()) {
						word = t.nextToken();
						words.add(word.toLowerCase());
					}
					for (int j=0; j<words.size();j++) {
						Integer i = map.get(words.get(j));
						if (i == null) {
							map.put(words.get(j), 1);
						} else {
							map.put(words.get(j), i + 1);
						}
					}
					for (Entry<String, Integer> entry : map.entrySet()) {
						String k = entry.getKey();
						Integer v = entry.getValue();
						List<ISearchResult> list = btree.search(k);
						ISearchResult e = new SearchResult(id, v);
						if (list == null) {
							List<ISearchResult> newlist = new ArrayList<ISearchResult>();
							newlist.add(e);
							btree.insert(k, newlist);
						} else {
							list.add(e);
						}
					}

				}
			}

		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void indexDirectory(String directoryPath) {
		if (directoryPath == null || directoryPath == "" || !new File(directoryPath).exists()) {
			throw new RuntimeErrorException(null);
		}
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        indexWebPage(directoryPath+"\\"+file.getName());
		    }
		}
	}

	@Override
	public void deleteWebPage(String filePath) {
		if (filePath == null || filePath == "" || !new File(filePath).exists()) {
			throw new RuntimeErrorException(null);
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Document document = builder.parse(filePath);
			document.getDocumentElement().normalize();
			// Element root = document.getDocumentElement();
			NodeList nList = document.getElementsByTagName("doc");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				// System.out.println(""); //Just a separator
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// Print each employee's detail
					Element eElement = (Element) node;
					String id = eElement.getAttribute("id");
					String value = eElement.getTextContent();
					value = value.toLowerCase();
					ArrayList<String> words = new ArrayList<String>();
					StringTokenizer t = new StringTokenizer(value);
					String word = "";
					while (t.hasMoreTokens()) {
						word = t.nextToken();
						words.add(word.toLowerCase());
					}
					for(int i=0;i<words.size();i++) {
						List<ISearchResult> list = btree.search(words.get(i));
						if(list!=null) {
							for(int j=0;j<list.size();j++) {
								if(list.get(j).getId().equals(id)) {
									list.remove(j);
									break;
								}
							}
						}
					}
				}
			}
		}catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		if (word == null) {
			throw new RuntimeErrorException(null);
		} else if (word == "") {
			return new ArrayList<>();
		}
		return btree.search(word.toLowerCase());
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		ArrayList<String> words = new ArrayList<String>();
		Map<String, List<ISearchResult>> map = new HashMap<String, List<ISearchResult>>();
		SearchResult search = new SearchResult("", 0);
		if (sentence == null) {
			throw new RuntimeErrorException(null);
		} else if (sentence == "") {
			return new ArrayList<>();
		}
		StringTokenizer t = new StringTokenizer(sentence);
		String word = "";
		List<ISearchResult> mulitySearch = new ArrayList<ISearchResult>();
		int count = 0;
		while (t.hasMoreTokens()) {
			word = t.nextToken();
			words.add(word.toLowerCase());
		}
		for (int i = 0; i < words.size(); i++) {
			map.put(words.get(i), searchByWordWithRanking(words.get(i)));
			if (map.get(words.get(i)).size() == 0) {
				return new ArrayList<ISearchResult>();
			}
		}
		for (int j = 0; j < map.get(words.get(0)).size(); j++) {
			count = 1;
			search.setId(map.get(words.get(0)).get(j).getId());
			search.setRank(map.get(words.get(0)).get(j).getRank());
			for (int i = 1; i < words.size(); i++) {
				for (int k = 0; k < map.get(words.get(i)).size(); k++) {
					if (search.getId().equals(map.get(words.get(i)).get(k).getId())) {
						count++;
						if (search.getRank() > map.get(words.get(i)).get(k).getRank()) {
							search.setRank(map.get(words.get(i)).get(k).getRank());
						}
						break;
					}
				}
			}
			if (count == words.size()) {
				SearchResult sear = new SearchResult(search.getId(), search.getRank());
				sear.setId(search.getId());
				sear.setRank(search.getRank());
				mulitySearch.add(sear);
			}
		}
		return mulitySearch;
	}

}
