import java.io.*;
import java.util.*;

class REsearch
{
	/**
	 * Main class requires two inner classes: 
	 * one to recreate the FSM, and one for the deque navigation.
	 * @author YR
	 * @param args
	 */
	public static void main(String[] args)
	{ try { new REsearch().run(args); } catch (Exception e) { System.err.println(e); } }

	public void run(String[] args) throws IOException
	{
		List<ParseNode> nodes = new ArrayList<ParseNode>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for (String s = reader.readLine(); s != null; s = reader.readLine())
		{
			
		}
		
		Deque deque = new Deque();
	}

	/**
	 * Reconstructs the FSM based on stdin. Indexes are assumed from 1.
	 * Currently one class only, may be expanded to two subclasses 
	 * (one for branching machine, one for char match)
	 * @author YR
	 */
	class ParseNode
	{
		
	}

	/**
	 * Deque for traversing the array of ParseNode for matching pattern
	 * to an input.
	 * @author YR
	 */
	static class Deque
	{
		static int size = 1;
		DequeNode head;
		DequeNode tail;
		DequeNode scan = new DequeNode();

		public Deque()
		{
			head = scan;
			tail = scan;
		}

		public void headAdd(int index)
		{
			head.prev = new DequeNode(index, null, head);
			head = head.prev;
		}

		public int headRemove()
		{
			int temp = head.value;
			head = head.next;
			head.prev = null;
			return temp;
		}

		public void tailAdd(int index)
		{
			tail.next = new DequeNode(index, tail, null);
			tail = tail.next;
		}

		public int tailRemove()
		{
			int temp = tail.value;
			tail = tail.prev;
			tail.next = null;
			return temp;
		}

		class DequeNode
		{
			DequeNode prev;
			DequeNode next;
			int value;

			public DequeNode()
			{ value = -1; }

			public DequeNode(int index, DequeNode prev_, DequeNode next_)
			{
				value = index;
				prev = prev_;
				next = next_;
			}
		}
	}
}