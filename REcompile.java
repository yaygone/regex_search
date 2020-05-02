import java.util.*;

class REcompile {

    private final char BRANCH_SYMBOL = '^';
    private final char END_SYMBOL = '?';
    private final char DUMMY_SYMBOL = '*';
    private final char[] specialChars = {'.', '?', '*', '|'};

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
            return; }

        //Get input from the user
        splitExpression = args[0].toCharArray();
        REcompile compiler = new REcompile();

        compiler.compile(); 
    }

    private void setState(int s, char c, int n1, int n2) {

            //If arrays are full, extend the arrays
            if(s >= characters.length) {
                char[] newChar = new char[s + 1];
                int[] newN1 = new int[s + 1];
                int[] newN2 = new int[s + 1];
                
                System.arraycopy(characters, 0, newChar, 0, characters.length);
                System.arraycopy(next1, 0, newN1, 0, next1.length);
                System.arraycopy(next2, 0, newN2, 0, next2.length);

                characters = newChar;
                next1 = newN1;
                next2 = newN2;

            }

            characters[s] = c;
            next1[s] = n1;
            next2[s] = n2;        
    }

    private void compile() {

        characters = new char[splitExpression.length];
        next1 = new int[splitExpression.length];
        next2 = new int[splitExpression.length];

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
                //Make sure an expression doesn't start with a special character
                // if (isSpecialCharacter(splitExpression[position])) {
                //     throwError(splitExpression[position]);
                // } 

                //Check if it's in the vocab, or if it's an opening bracket
                if (isInVocab(splitExpression[position]) || splitExpression[position] == '(' || splitExpression[position] == '|') {
                    //Start a new expression
                    expression();
                }

                // else {
                //     throwError(splitExpression[position]);
                // }
    
            }
            
            else {
    
                setState(state, END_SYMBOL, 0, 0);

            }
        return response;
    }

    private int term() {

        //Case for escape character
        if(splitExpression[position] == '\\') {
            //Set whatever character comes after the \ as a literal, and add to the 
            position++;

        }
        int response, t1;
        int prevState = state - 1;
        t1 = response = factor();
        
        
        if (position < splitExpression.length) {
        //Case for closure
        if(splitExpression[position] == '*') {
            position++;
            setState(state, BRANCH_SYMBOL, response, state + 1);
            state++;

            //Return the closures branching state
            //return state - 1;
            response = state - 1;
        }

        //Case for zero or one times
        if(position < splitExpression.length && splitExpression[position] == '?') {

            //Get the state of the symbol that was just added
            int stateJustAdded = state - 1;

            //Create the new branching state, one pointing towards the symbol just added, and another pointing to the next symbol to be added
            position++;
            setState(state, BRANCH_SYMBOL, stateJustAdded, state + 1);
            state++;
            
            //Set the symbol preceding the ? symbol to point towards the next state
            setState(stateJustAdded, characters[stateJustAdded], state, state);

            //Return the zero/one branching state
            response = state - 1;

        }

        //Case for disjunction
        if(position < splitExpression.length && splitExpression[position] == '|') {

            if (next1[prevState] == next2[prevState]) {
                next1[prevState] = state;
                
            }

            next2[prevState] = state;

            int f = state - 1;

            
            int returnVal = state;
            state++;
            position++;

            //Create the first term that begins after the disjunction
            int newTerm = term();

            //Setup the disjunction state
            //One next points to the symbol just added, the next points to the state that is about to be created
            setState(returnVal, BRANCH_SYMBOL, response, newTerm);

            //Setup the dummy state
            setState(state, DUMMY_SYMBOL, state + 1, state + 1);

            //Point the end of the first part of the disjunction to the dummy state
            if (next1[f] == next2[f]) {
                next1[f] = next2[f] = state;
            }

            //Special case for zero or one occurances
            // if (response - 1 > 0) {
            //     if (next1[response - 1] == returnVal) {
            //         next1[response - 1] = state;
            //     }
            //     if (next2[response - 1] == returnVal) {
            //         next2[response - 1] = state;
            //     }
            // }

            state++;

            response = returnVal;

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

    private boolean isSpecialCharacter(char c) {
        for(int i = 0; i < specialChars.length; i++) {
            if(c == specialChars[i]) return true;
        }
        return false;
    }

    private boolean isInVocab(char c) {
        //Check it's a printable character
        // if (Character.isISOControl(c)) return false;
        // return true;
        if (Character.isLetter(c) || Character.isDigit(c)) return true;
        return false;
    }

    private void throwError(char symbol) {
        System.out.println("Error occured with Regex at symbol " + symbol);
        System.exit(0);
    }




    
}