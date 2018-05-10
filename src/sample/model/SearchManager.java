package sample.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class SearchManager {
	
	private Node rootNode;
	
	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	public LinkedList<SearchResult> query(String s) {
		final int MAX_TERM_DISTANCE = 30;
		
		LinkedList<SearchResult> searchResults = new LinkedList<>();
		
		String[] terms = s.split(" ");
		LinkedList<TermOccurrence>[] termOccurrances = new LinkedList[terms.length];
		Iterator<TermOccurrence>[] occurranceIterators = new Iterator[terms.length];
		
		for (int i = 0; i < terms.length; i++) {
			char[] letters = terms[i].toCharArray();
			
			Node currentNode = rootNode;
			
			for (int j = 0; j < letters.length && currentNode != null; j++) {
				currentNode = currentNode.getChildNodeForLetter(letters[j]);
			}
			
			if (currentNode != null && currentNode.occurrances != null) {
				termOccurrances[i] = currentNode.occurrances;
				for (TermOccurrence termOccurrence : currentNode.occurrances) {
					//System.out.println("It occurred in document " + termOccurrence.document + " at position " + termOccurrence.position);
				}
				
				occurranceIterators[i] = termOccurrances[i].iterator();
			} else {
				//Not found
				return searchResults;
			}
		}
		
		//We are now guaranteed to have at least one occurrence of each term
		
		//Merge lists
		
		TermOccurrence[] currentTermOccurrences = new TermOccurrence[terms.length];
		
		for (int i = 0; i < terms.length; i++) {
			if (occurranceIterators[i].hasNext()) {
				currentTermOccurrences[i] = occurranceIterators[i].next();
			}
		}
		
		int document = -1;
		boolean foundNextDocument;
		
		mainloop: while (true) {
			//Find the next document that they all have in common
			
			//Find the next document
			
			foundNextDocument = false;
			while (occurranceIterators[0].hasNext()) {
				if (currentTermOccurrences[0].document != document) {
					document = currentTermOccurrences[0].document;
					foundNextDocument = true;
					break;
				}
				currentTermOccurrences[0] = occurranceIterators[0].next();
			}
			
			if (!foundNextDocument) {
				//We are finished
				break;
			}
			
			//Get each of the other iterators up to this document
			for (int i = 1; i < terms.length; i++) {
				while (currentTermOccurrences[i].document != document) {
					if (!occurranceIterators[i].hasNext()) {
						//Term i is the "limiting term" (there are no more documents containing i)
						break mainloop;
					}
					
					if (currentTermOccurrences[i].document > document) {
						//Term i does not appear in the current document
						continue mainloop;
					}
					
					currentTermOccurrences[i] = occurranceIterators[i].next();
				}
			}
			
			//Now all termIterators are on the first occurrence of each term in this document
			
			int score = 0;
			
			//Calculate the term proximity score of this document
			//Does not count repeated terms has having a penalty of 0
			
			while (true) {
				
				//Find min and second min positions
				
				int min = -1; //term index
				
				int minPosition = Integer.MAX_VALUE;
				int secondMinPosition = Integer.MAX_VALUE;
				
				for (int i = 0; i < terms.length; i++) {
					//Used to ignore terms for which all occurrences in this document have already been added to the score
					if (currentTermOccurrences[i].document != document) {
						continue;
					}
					
					if (currentTermOccurrences[i].position < minPosition) {
						secondMinPosition = minPosition;
						minPosition = currentTermOccurrences[i].position;
						min = i;
					} else if (currentTermOccurrences[i].position < secondMinPosition) {
						secondMinPosition = currentTermOccurrences[i].position;
					}
				}
				
				//If at least 2 terms are still on this document
				if (secondMinPosition != Integer.MAX_VALUE) {
					//If at least 2 terms were found
					score += Math.max(0, MAX_TERM_DISTANCE - (secondMinPosition - minPosition));
				} else if (minPosition != Integer.MAX_VALUE) {
					score++; //Add one just for having the term (A for effort! :))
				} else {
					break;
				}
				
				//Advance the iterator that contained min
				if (occurranceIterators[min].hasNext()) {
					currentTermOccurrences[min] = occurranceIterators[min].next();
				} else {
					break;
				}
			}
			
			searchResults.add(new SearchResult(document, null, score));
		}
		
		Collections.sort(searchResults);
		
		return searchResults;
	}
	
	private boolean allHaveNext(Iterator[] iterators) {
		for (int i = 0; i < iterators.length; i++) {
			if (!iterators[i].hasNext()) return false;
		}
		return true;
	}
	
	public static class Node {
		public Node[] children;
		public int childCount;
		
		public LinkedList<TermOccurrence> occurrances;
		
		public Node() {
			occurrances = null;
			children = new Node[26];
			childCount = 0;
		}
		
		public Node getChildNodeForLetter(char c) {
			return children[c - 'a'];
		}
		
		public void addChildNodeForLetter(char c) {
			children[c - 'a'] = new Node();
			childCount++;
		}
	}
	
	public static class TermOccurrence {
		
		public TermOccurrence(int document, int position) {
			this.document = document;
			this.position = position;
		}
		
		public int document;
		public int position;
	}
}
