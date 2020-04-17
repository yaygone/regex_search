import java.util.*;

class REcompile {

    private final char BRANCH_SYMBOL = '~';
    private final char END_SYMBOL = ';';

    
    private int state = 1; //Start at state number 1

    //private int result = 0;
    private int start;
    private int position = 0;
    
    // private ArrayList<Character> characters = new ArrayList<Character>();
    // private ArrayList<Integer> next1 = new ArrayList<Integer>();
    // private ArrayList<Integer> next2 = new ArrayList<Integer>();

    private char[] characters;
    private int[] next1;
    private int[] next2;

    private static char[] splitExpression;


    public static void main(String[] args) {

        


        if (args.length != 1) {
            System.out.println("Usage: REcompile <\"expression\">)");
            return;
        }

        //Get input from the user
        splitExpression = args[0].toCharArray();
        REcompile compiler = new REcompile();

        compiler.compile();        


    }

    private void setState(int s, char c, int n1, int n2) {
        
            // characters.add(s, c);
            // next1.add(s, n1);
            // next2.add(s, n2);

            characters[s] = c;
            next1[s] = n1;
            next2[s] = n2;

            System.out.println("State: " + s + " Char: " + c + " N1: " + n1 + " N2: " + n2);
        
    }

    private void compile() {

        characters = new char[splitExpression.length * 2];
        next1 = new int[splitExpression.length * 2];
        next2 = new int[splitExpression.length * 2];

        int start = expression();
        


    }

    private int expression() {

        int response;

        if(position >= splitExpression.length - 1) {

            response = state;
            setState(state, END_SYMBOL, 0, 0);

        } else {
            

            //Get the term
            response = term();
    
            //Check if it's in the vocab, or if it's an opening bracket
            if (isInVocab(splitExpression[position]) || splitExpression[position] == '(') {
                //Start a new expression
                expression();
            }
    
        }

        return response;

       


    }

    private int term() {

        int t1, t2, prevState;

        int response = t1 = factor();
        
        prevState = state - 1;

        //Case for closure
        if(splitExpression[position] == '*') {
            position++;
            setState(state, BRANCH_SYMBOL, response, state + 1);
            state++;

            //Return the closures branching state
            return state - 1;
        }

        //Case for disjunction
        if(splitExpression[position] == '|') {
            int initialState = state;
            int branchState = state + 1;
            state++;
            int state2 = term();

            //Set the branching states
            setState(branchState, BRANCH_SYMBOL, response, state2);
            setState(initialState, BRANCH_SYMBOL, state, state);

            return branchState;

        }


        return response;

    }

    private int factor(){

        int response = 0;
        char currentSymbol = splitExpression[position];

        //If it's in the vocab, set the state, and then continue to the next character
        if(isInVocab(currentSymbol)) {
            setState(state, currentSymbol, state + 1, state + 1);
            position++;
            response = state;
            state++;
        } else {
            //For an opening bracket, go to next char, create a new expression
            if(currentSymbol == '(') {
                position++;
                response = expression();
            
            //Check that the bracket was closed, and then move on
            if(currentSymbol == ')') {
                position++;
            } else {
                throwError();
            }
        } else {
            throwError();
        }
        }

        return response;

    }

    private boolean isInVocab(char c) {
        if (Character.isLetter(c) || Character.isDigit(c)) return true;
        return false;
    }

    private void throwError() {
        System.out.println("Error occured with Regex");
        return;
    }




    
}