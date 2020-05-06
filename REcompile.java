/**
 * @author Tristan Brynildsen
 */
class REcompile {

    private final char BRANCH_SYMBOL = 0;
    private final char END_SYMBOL = 0;
    private final char DUMMY_SYMBOL = 0;
    private final char WILDCARD_PLACEHOLDER = (char)26;
    private final char[] specialChars = {'.', '?', '*', '|', '(', ')'};

    private int state = 1; //Start at state number 1
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

        characters = new char[1];
        next1 = new int[1];
        next2 = new int[1];

        int start = expression();

        setState(0, (char)0, start, start);

        for (int i = 0; i < characters.length; i++)
            System.out.println(i + "," + (int)characters[i] + "," + next1[i] + "," + next2[i]);
    }

    private int expression() {

        int response = 0;
		//Get the term
		response = term();

		if(position < splitExpression.length) {
			//Check if it's in the vocab, or if it's an opening bracket
			if (isInVocab(splitExpression[position]) || splitExpression[position] == '(' || splitExpression[position] == '|') {
				//Start a new expression
				expression();
			}
		}
		else setState(state, END_SYMBOL, -1, -1);

        return response;
    }

    private int term() {

        int response, t1;
        int prevState = state - 1;
        t1 = response = factor();
        
        //Case for escape char
        if(position < splitExpression.length && splitExpression[position] == '\\') response = factor() - 1;
        
        //Case for wildcard
        if(position < splitExpression.length && splitExpression[position] == '.') response = factor() - 1;

        //Case for closure
        if(position < splitExpression.length && splitExpression[position] == '*') {
            
            position++;
            int closureState = state;
            setState(state, BRANCH_SYMBOL, response, state + 1);
            if (prevState > 0) setState(prevState, characters[prevState], state, state);
            state++;

            //If this is the end of the expression
            if(position + 2 >= splitExpression.length) {
                if (position < splitExpression.length && splitExpression[position] == ')') {
                    return closureState;
                }
            }

            if(position < splitExpression.length) {
				if (splitExpression[position] != ')') {
					t1 = term();
					setState(closureState, BRANCH_SYMBOL, response, t1);
				} else {
					position++;
					t1 = term();
					setState(closureState, BRANCH_SYMBOL, response, t1);
					return closureState;
				}
			}
			
            //Return the closures branching state
            response = closureState;
        }

        //Case for zero or one times
        if(position < splitExpression.length && splitExpression[position] == '?') {

            //Get the state of the symbol that was just added
            int stateJustAdded = state - 1;
            int closureState = state;

            //Create the new branching state, one pointing towards the symbol just added, and another pointing to the next symbol to be added
            position++;
            setState(state, BRANCH_SYMBOL, t1, state + 1);
            setState(stateJustAdded, characters[stateJustAdded], state + 1, state + 1);
            state++;


            if(position < splitExpression.length)  {
                if (splitExpression[position] != ')') {
                    t1 = term();
                    setState(closureState, BRANCH_SYMBOL, response, t1);
                    setState(stateJustAdded, characters[stateJustAdded], t1, t1);
                } else {
                    position++;
                    t1 = term();
                    setState(closureState, BRANCH_SYMBOL, response, t1);
                    setState(stateJustAdded, characters[stateJustAdded], t1, t1);
                    return closureState;
                }
			} else
                setState(state, BRANCH_SYMBOL, t1, state + 1);

            //If this is the end of the expression
            if(position + 2 >= splitExpression.length && position < splitExpression.length && splitExpression[position] == ')') return closureState;
            //Return the zero/one branching state
            response = closureState;
        }

        //Case for disjunction
        if(position < splitExpression.length && splitExpression[position] == '|') {

            if (next1[prevState] == next2[prevState]) next1[prevState] = state;
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
            if (next1[f] == next2[f]) next1[f] = next2[f] = state;

            state++;
            response = returnVal;
        }
        return response;
    }

    private int factor(){

        int response = state;

		//Case for escape character
		if(position < splitExpression.length && splitExpression[position] == '\\') {
			//Set whatever character comes after the \ as a literal
			position++;
			setState(state, splitExpression[position], state + 1, state + 1);
			position++;
			response = state;
			state++;
			return response;
		}

		//If it's a wildcard, insert the wildcard placeholder
		if(position < splitExpression.length && splitExpression[position] == '.') {
			setState(state, WILDCARD_PLACEHOLDER, state + 1, state + 1);
			position++;
			response = state;
			state++;
			return response;
		}

		if(position < splitExpression.length) {
			//If it's in the vocab, set the state, and then continue to the next character
			if(isInVocab(splitExpression[position])) {
				
				setState(state, splitExpression[position], state + 1, state + 1);
				position++;
				response = state;
				state++;
			} 
			
			else {
				//For an opening bracket, go to next char, create a new expression
				if(splitExpression[position] == '(') {
					position++;
					response = expression();
					if (position >= splitExpression.length) return response;
					//Check that the bracket was closed, and then move on
					if(splitExpression[position] == ')') position++;
					else throwError(splitExpression[position]);
				}
				else
					throwError(splitExpression[position]);
			}
		}
        return response;
    }

    private boolean isSpecialCharacter(char c) {
        for(int i = 0; i < specialChars.length; i++)
            if(c == specialChars[i]) return true;
        return false;
    }

    private boolean isInVocab(char c) {
        //Check it's a printable character
        return (!Character.isISOControl(c) && !isSpecialCharacter(c));
    }

    private void throwError(char symbol) {
        System.out.println("Error occured with Regex at symbol " + symbol);
        System.exit(0);
    }
}