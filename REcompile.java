import java.util.*;

class REcompile {

    private final char BRANCH_SYMBOL = '~';
    private final char END_SYMBOL = ';';
    private final char DUMMY_SYMBOL = ',';

    private int state = 1; //Start at state number 1
    private int start;
    private int position = 0;
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

            characters[s] = c;
            next1[s] = n1;
            next2[s] = n2;        
    }

    private void compile() {

        characters = new char[splitExpression.length * 2];
        next1 = new int[splitExpression.length * 2];
        next2 = new int[splitExpression.length * 2];

        int start = expression();

        setState(0, ' ', start, start);

        for (int i = 0; i < characters.length; i++) {
            System.out.println("State: " + i + " Char: " + characters[i] + " N1: " + next1[i] + " N2: " + next2[i]);
        }
    }

    private int expression() {

        int response;

            //Get the term
            response = term();

            if(position < splitExpression.length) {
                //Check if it's in the vocab, or if it's an opening bracket
                if (isInVocab(splitExpression[position]) || splitExpression[position] == '(') {
                    //Start a new expression
                    expression();
                }
    
            } else {
    
                setState(state, END_SYMBOL, 0, 0);

            }
        return response;
    }

    private int term() {

        int prevState = state - 1;
        int response = factor();
        
        
        if (position < splitExpression.length) {
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

            //Re-set the next values for whatever state was added before the disjunction
            if (next1[prevState] == response) {
                next1[prevState] = state;
            } else {
                next2[prevState] = state;
            }

            //Setup the disjunction state
            //One next points to the symbol just added, the next points to the state that is about to be created
            setState(state, BRANCH_SYMBOL, response, state + 1);
            state++;
            position++;

            //Create the first term that begins after the disjunction
            int newTerm = term();

            //Setup the dummy state
            setState(state, DUMMY_SYMBOL, state + 1, state + 1);

            //Point the end of the first part of the disjunction to the dummy state
            next1[response] = next2[response] = state;
            state++;

            return newTerm;

        }
    }


        return response;

    }

    private int factor(){

        int response = 0;

        //If it's in the vocab, set the state, and then continue to the next character
        if(isInVocab(splitExpression[position])) {
            setState(state, splitExpression[position], state + 1, state + 1);
            position++;
            response = state;
            state++;
        } else {
            //For an opening bracket, go to next char, create a new expression
            if(splitExpression[position] == '(') {
                position++;
                response = expression();
            
                //Check that the bracket was closed, and then move on
                if(splitExpression[position] == ')') {
                    position++;

                } else { throwError(splitExpression[position]); }

            } else {
                throwError(splitExpression[position]);
            }
        }

        return response;

    }

    private boolean isInVocab(char c) {
        if (Character.isLetter(c) || Character.isDigit(c)) return true;
        return false;
    }

    private void throwError(char symbol) {
        System.out.println("Error occured with Regex at symbol " + symbol);
        return;
    }




    
}