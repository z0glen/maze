Ateev Nahar and Zach Bauer

To play the game press “r” once the menu screen pops up.
To toggle the switch to make the paths visible press “p”
To toggle player movement press “c”
To toggle breadth first search press “b”
To toggle depth first search press “d”
To make a new random maze press “r”

To move the player square(colored yellow) use the arrow keys to move it in the corresponding direction. To beat the game make it to the reddish square. The number of moves made not on the correct path is monitored and displayed. The path is toggled off at the start, so to see your path or the searches path toggle the path. The lighter shade drawn either for the player, or searches is the cells visited, but not on the correct path.

To adjust the size of the cells or the size of the maze go into the GameConstants class and change the associated variables:
Changing ROW_VERTICES will change the number of rows, therefore how wide the game is.
Changing COL_VERTICES will change the number of columns, therefore how long the game is.
Changing the CELL_SIZE will change the size of each individual cell by pixel.
Changing the TICK will change how fast the animated searches and paths will be drawn. 

We hope you enjoy the game.
Whistles:
-Toggling the path view
-Showing the correct path overlayed on the visited tiles
-Keeping track of wrong moves
-Generating new maze in game