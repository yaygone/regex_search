import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class REsearch extends Thread
{
	static List<ParseTableNode> parseTable = new ArrayList<ParseTableNode>();
	static Deque deque = new Deque();
	private static final ExecutorService executor = Executors.newFixedThreadPool(16);
	
	/**
	 * Main class requires two inner classes: 
	 * one to recreate the FSM, and one for the deque navigation.
	 * @author YR
	 * @param args
	 */
	public static void main(String[] args)
	{ try { new REsearch().run(args[0]); } catch (Exception e) { System.err.println(e); } }

	public void run(String inputFile) throws IOException
	{
		// Take input from REcompile - assuming form "selfIndex,char,next1,next2" "1,a,2,2" for example
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for (String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] elements = s.split(",");
			parseTable.add(Integer.parseInt(elements[0]), new ParseTableNode(elements[1], Integer.parseInt(elements[2]), Integer.parseInt(elements[3])));
		}
		reader.close();
		
		int i = 1;
		reader = new BufferedReader(new FileReader(inputFile));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			executor.execute(new MatchFinder(line, i++));
			// findMatch(input.substring())
			// int searchedIndex = startIndex;
			// deque = new Deque();
			// String result = "";
			// while (true)
			// {
			// 	ParseNode currentNode = (Deque.size == 1) ? nodes.get(0) : nodes.get(deque.headRemove());
			// 	if (currentNode.ch == 0 || currentNode.ch == inputArray[searchedIndex])
			// 	{
			// 		deque.tailAdd(currentNode.next1);
			// 		if (currentNode.isBranching()) deque.tailAdd(currentNode.next2);
			// 		if (currentNode.ch == inputArray[searchedIndex]) result += inputArray[searchedIndex];
			// 	}
			// 	else break;
			// }
		reader.close();
	}

	class MatchFinder implements Runnable
	{
		char[] input;
		int printLineNumber;
		Deque deque = new Deque();
		
		public MatchFinder(String line, int lineNumber)
		{
			input = line.toCharArray();
			printLineNumber = lineNumber;
		}

		public void run()
		{
			for (int startIndex = 0; startIndex < input.length; startIndex++)
			{
				int inputIndex = startIndex;
				boolean failed = false;
				while (!failed)
				{
					for (int parseTableIndex = deque.headRemove(); parseTableIndex != -2; parseTableIndex = deque.headRemove())
					{
						// If a node in the head side points to -1, that's a successful match.
						if (parseTableIndex == -1)
						{
							System.out.println("Match found in line " + Integer.toString(printLineNumber) + ": \n" + new String(input));
							return;
						}

						ParseTableNode poppedHead = parseTable.get(parseTableIndex);
						// If the head had no condition, it's a branching statement. Expand it and add back to the head of the deque.
						if (poppedHead.ch == 0)
						{
							deque.headAdd(poppedHead.next1);
							if (poppedHead.isBranching()) deque.headAdd(poppedHead.next2);
						}
						
						// If the current character matches, then add the head's next to tail for future.
						// TODO check this assumption is correct: "All non-null check nodes only have one possible next pointer."
						else if (poppedHead.ch == input[inputIndex])
							deque.tailAdd(poppedHead.next1);
						
							// If the current character doesn't match, this path/branch/whatever is dead. Move on to the next iteration.
					}
					// If the deque has no possible future paths, then give up and start from the next character.
					if (deque.size == 0) failed = true;
					else
					{
						inputIndex++;
						deque.tailAddScan();
					}
				}
			}
		}
	}

	/**
	 * Reconstructs the FSM based on stdin. Indexes are assumed from 1.
	 * Currently one class only, may be expanded to two subclasses 
	 * (one for branching machine, one for char match)
	 * @author YR
	 */
	class ParseTableNode
	{
		char ch;
		int next1;
		int next2;

		public ParseTableNode(String s, int n1, int n2)
		{
			ch = (s.equals("")) ? 0 : s.charAt(0);
			next1 = n1;
			next2 = n2;
		}

		public boolean isBranching()
		{ return next1 != next2; }
	}

	/**
	 * Deque for traversing the array of ParseNode for matching pattern
	 * to an input.
	 * @author YR
	 */
	static class Deque
	{
		int size;
		DequeNode head;
		DequeNode tail;
		DequeNode scan;

		public Deque()
		{
			size = 2;
			head = new DequeNode(0, null, scan);
			scan = new DequeNode(-2, head, null);
			tail = scan;
		}

		public void headAdd(int index)
		{
			head.prev = new DequeNode(index, null, head);
			head = head.prev;
			size++;
		}

		public int headRemove()
		{
			int temp = head.tableValue;
			head = head.next;
			head.prev = null;
			size--;
			return temp;
		}

		public void tailAdd(int index)
		{
			tail.next = new DequeNode(index, tail, null);
			tail = tail.next;
			size++;
		}

		public int tailRemove()
		{
			int temp = tail.tableValue;
			tail = tail.prev;
			tail.next = null;
			size--;
			return temp;
		}
		
		public void tailAddScan()
		{ tailAdd(-2); }

		class DequeNode
		{
			DequeNode prev;
			DequeNode next;
			int tableValue;

			public DequeNode(int index, DequeNode prev_, DequeNode next_)
			{
				tableValue = index;
				prev = prev_;
				next = next_;
			}

		}
	}
}