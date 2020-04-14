import java.io.*;
import java.util.*;

class REsearch extends Thread
{
	static List<ParseTableNode> parseTable = new ArrayList<ParseTableNode>();
	
	/**
	 * Main class requires two inner classes: 
	 * one to recreate the FSM, and one for the deque navigation.
	 * @author YR
	 * @param args
	 */
	public static void main(String[] args)
	{ try { new REsearch().run(args[0]); } catch (Exception e) { System.err.println(e); } }

	public void run(String inputFile) throws IOException, InterruptedException
	{
		// Take input from REcompile - assuming form "selfIndex,char,next1,next2" "1,a,2,2" for example
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for (String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] elements = s.split(",");
			parseTable.add(Integer.parseInt(elements[0]), new ParseTableNode(elements[1], Integer.parseInt(elements[2]), Integer.parseInt(elements[3])));
		}
		reader.close();
		
		reader = new BufferedReader(new FileReader(inputFile));
		int lineNumber = 1;
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			if (matchFound(line)) System.out.println("Match found in line " + lineNumber++ + ": \n" + line);
		reader.close();
	}
	
	public boolean matchFound(String input)
	{
		for (int startIndex = 0; startIndex < input.length(); startIndex++)
		{
			Deque deque = new Deque();
			int inputIndex = startIndex;
			boolean failed = false;
			while (!failed)
			{
				for (int parseTableIndex = deque.headRemove(); parseTableIndex != -2; parseTableIndex = deque.headRemove())
				{
					// If a node in the head side points to -1, that's a successful match.
					if (parseTableIndex == -1) return true;

					ParseTableNode poppedHead = parseTable.get(parseTableIndex);
					// If the head had no condition, it's a branching statement. Expand it and add back to the head of the deque.
					if (poppedHead.ch == 0)
					{
						deque.headAdd(poppedHead.next1);
						if (poppedHead.isBranching()) deque.headAdd(poppedHead.next2);
					}
					
					// If the current character matches, then add the head's next to tail for future.
					else if (poppedHead.ch == input.charAt(inputIndex))
						deque.tailAdd(poppedHead.next1);
					
					// If the current character doesn't match, this path/branch/whatever is dead. Move on to the next iteration.
				}
				// If the deque has no possible future paths, then give up and start from the next character.
				if (deque.size == 0) failed = true;
				else deque.tailAddScan();
				inputIndex++;
			}
		}
		return false;
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
	class Deque
	{
		int size = 2;
		DequeNode head = new DequeNode(0, null, null);
		DequeNode scan = new DequeNode(-2, head, null);
		DequeNode tail = scan;

		public Deque()
		{
			size = 2;
			head.next = scan;
		}

		public int clearLastNode()
		{
			DequeNode temp = head;
			head = null;
			tail = null;
			size = 0;
			return temp.tableValue;
		}

		public void headAdd(int index)
		{
			head.prev = new DequeNode(index, null, head);
			head = head.prev;
			size++;
		}

		public int headRemove()
		{
			if (size == 1) return clearLastNode();
			DequeNode temp = head;
			head = temp.next;
			head.prev = null;
			size--;
			return temp.tableValue;
		}

		public void tailAdd(int index)
		{
			tail.next = new DequeNode(index, tail, null);
			tail = tail.next;
			size++;
		}

		public int tailRemove()
		{
			if (size == 1) return clearLastNode();
			DequeNode temp = tail;
			tail = temp.prev;
			tail.next = null;
			size--;
			return temp.tableValue;
		}
		
		public void tailAddScan()
		{
			if (size == 0)
			{
				tail = scan;
				head = scan;
				size = 1;
				return;
			}
			tail.next = scan;
			scan.prev = tail;
			tail = scan;
			tail.next = null;
			size++;
		}

		class DequeNode
		{
			DequeNode prev;
			DequeNode next;
			int tableValue;

			public DequeNode(int index, DequeNode prev, DequeNode next)
			{
				tableValue = index;
				this.prev = prev;
				this.next = next;
			}
		}
	}
}