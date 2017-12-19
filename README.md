# Ultimate Tic-Tac-Toe
An independent final project for CS0150 at Brown University. Created in the autumn of 2016.

## Why
Instead of choosing one of the three predefined projects, I chose to design my own. This involved
a lot of independent work, since there was no helpful handout for what I was doing. I had always
enjoyed [Ultimate Tic-Tac-Toe](https://en.wikipedia.org/wiki/Ultimate_tic-tac-toe) and thought it
would be an interesting challenge to implement it.

## What
This project was written in Java, which we learned from scratch in the class. The graphical elements
are using the JavaFX library. Much of the styling is done with JavaFX custom css. The main algorithm
for the computer player is a [Monte Carlo Tree Search](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search).
Since the gamespace is so large and it is hard to rank the quality of moves, MCTS seemed to be an
appropriate algorithm.