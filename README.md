# COMPX301-A2

#### Grammar

E -> T  
E -> T E  
T -> F  
T -> F *  
T -> F ?  
T -> F | T  
T -> . F  
T -> \ E  
F -> v  
F -> ( E )  

E = Expression  
T = Term  
F = Function  
v = Member of recognised vocabulary

Expressions not matching this grammar will result in the program terminating with an error