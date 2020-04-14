/**
 * Used for outputting FSM table to REsearch. Pipe as following:
 * java REsearchTest | java REsearch matchingTest.txt
 */
class REsearchTest
{
	/**
	 * Passes to next program the expression (a+b)*c
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("0,,1,1");
		System.out.println("1,,2,3");
		System.out.println("2,,4,5");
		System.out.println("3,c,6,6");
		System.out.println("4,a,1,1");
		System.out.println("5,b,1,1");
		System.out.println("6,,-1,-1");
	}
}