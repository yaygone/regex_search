# COMPX301-20A
# Assignment 2
Ye-Gon Ryoo 1126331
Tristan Brynildsen 1348237
#### Grammar

E -> T  
E -> T E

T -> F  
T -> F *  
T -> F ?  
T -> F | T  
T -> .  
T -> \ F  
F -> v  
F -> ( E )  

E = Expression  
T = Term  
F = Factor  
v = Member of recognised vocabulary

Expressions not matching this grammar will result in the program terminating with an error

### Vocabulary
- Any printable (non-control) character
