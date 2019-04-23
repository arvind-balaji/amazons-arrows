# Game of Amazons AI
AI for The Game of Amazons board game


## Heuristics

**Move Count** -- number of legal moves

**Mobility** -- sum of legal moves to directly adjacent squares

**Accessibility** -- number of squares reachable within 1 move

## Method

* All moves are scored with the move count heuristic
* Top *n* moves are picked
* Each of the top moves are scored using and MiniMax algorithm with each heuristic
