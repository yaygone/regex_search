import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class REsearch extends Thread
{
	static List<ParseNode> nodes = new ArrayList<ParseNode>();
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

	public void run(String input) throws IOException
	{
		// Take input from REcompile - assuming form "selfIndex,char,next1,next2" "1,a,2,2" for example
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for (String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] elements = s.split(",");
			nodes.add(Integer.parseInt(elements[0]), new ParseNode(elements[1], Integer.parseInt(elements[2]), Integer.parseInt(elements[3])));
		}

		for (int startIndex = 0; startIndex < input.length(); startIndex++)
		{
			executor.execute(new MatchFinder(startIndex));
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

		}
	}

	class MatchFinder implements Runnable
	{
		public MatchFinder(int startIndex)
		{

		}
		public void run()
		{

		}
	}

	/**
	 * Reconstructs the FSM based on stdin. Indexes are assumed from 1.
	 * Currently one class only, may be expanded to two subclasses 
	 * (one for branching machine, one for char match)
	 * @author YR
	 */
	class ParseNode
	{
		char ch;
		int next1;
		int next2;

		public ParseNode(String s, int n1, int n2)
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
		static int size;
		DequeNode head;
		DequeNode tail;
		DequeNode scan = new DequeNode(-1, null, null);

		public Deque()
		{
			size = 1;
			head = scan;
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
			int temp = head.value;
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
			int temp = tail.value;
			tail = tail.prev;
			tail.next = null;
			size--;
			return temp;
		}

		class DequeNode
		{
			DequeNode prev;
			DequeNode next;
			int value;

			public DequeNode(int index, DequeNode prev_, DequeNode next_)
			{
				value = index;
				prev = prev_;
				next = next_;
			}

		}
	}
}