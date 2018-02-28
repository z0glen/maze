import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.LineImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import javalib.worldimages.WorldImage;

//representation of a maze
//where everything comes together as of now
class MazeWorld extends World {
  ArrayList<ArrayList<Vertex>> allVertices;
  ArrayList<ArrayList<Edge>> allEdges;
  List<Edge> allEdgesFlat;
  ArrayList<ArrayList<Cell>> cells;

  HashMap<Vertex, Vertex> reps;
  ArrayList<Edge> minSpanTree;

  long startTime;
  long endTime;
  long totalTime;

  ArrayList<Cell> path;
  ArrayList<Cell> visited;

  int tickNum;

  ArrayList<Cell> drawList;

  int drawPath; // 0 will only show current cell, 1 will show all visited cells,
  // and 2 will highlight the true path

  int searchMethod; // 0 is none, 1 is depth, 2 is breadth, 3 is custom user

  boolean displayMenu; // if menu is being displayed

  Cell currentCell; // current cell the player is at

  int score; // mistakes made in path finding

  LinkedHashMap<String, Long> eventLog; // logging timestamps in maze generation

  boolean mazeSolved; // checking for end game

  // constructor for Maze
  // also prints out when each step is happening and times
  MazeWorld() {
    this.drawPath = 0;
    this.searchMethod = 0;
    this.tickNum = 0;
    this.displayMenu = true;
    this.score = 0;
    this.mazeSolved = false;
    this.eventLog = new LinkedHashMap<String, Long>();
    if (!this.displayMenu) {
      this.makeWorld(true);
    }
  }

  // a method for abstracting the constructor
  // useful for testing and making new mazes without closing the program
  // will also log timestamps at each step, and print time taken per step, if
  // displayLogging is true
  void makeWorld(boolean displayLogging) {
    this.eventLog.put("Starting world construction...", System.currentTimeMillis());
    this.eventLog.put("Making vertices...", System.currentTimeMillis());
    this.allVertices = this.makeVertices();
    this.eventLog.put("Making edges...", System.currentTimeMillis());
    this.allEdges = this.makeEdges();
    this.eventLog.put("Flattening edges...", System.currentTimeMillis());
    this.allEdgesFlat = this.flatten(this.allEdges);
    this.eventLog.put("Sorting edges...", System.currentTimeMillis());
    Comparator<Edge> comp = new ByEdgeWeight();
    // this.sort(this.allEdgesFlat, new ByEdgeWeight());
    Collections.sort(this.allEdgesFlat, comp);
    this.eventLog.put("Making cells...", System.currentTimeMillis());
    this.cells = this.makeCells();
    this.eventLog.put("Making hashmap...", System.currentTimeMillis());
    this.reps = new HashMap<Vertex, Vertex>();
    this.eventLog.put("Initializing hashmap...", System.currentTimeMillis());
    this.minSpanInit();
    this.eventLog.put("Initializing hashmap...", System.currentTimeMillis());
    this.visited = new ArrayList<Cell>();
    this.eventLog.put("Making tree...", System.currentTimeMillis());
    this.minSpanTree = this.minSpan();
    this.eventLog.put("Linking Edges to Cells...", System.currentTimeMillis());
    this.updateCells();
    this.eventLog.put("Finding path...", System.currentTimeMillis());
    this.path = this.search();
    this.eventLog.put("Finished world construction...", System.currentTimeMillis());
    if (displayLogging) {
      Long startTime = this.eventLog.get("Starting world construction...");
      Long prevTime = startTime;
      for (Entry<String, Long> entry : this.eventLog.entrySet()) {
        System.out.println(entry.getKey());
        System.out.println("Time taken: " + (entry.getValue() - prevTime) + " millis");
        prevTime = entry.getValue();
      }
      Long totalTime = this.eventLog.get("Finished world construction...")
          - this.eventLog.get("Starting world construction...");
      System.out.println("Total time for world construction: " + totalTime + " millis");
    }
  }

  // calls the search algorithm dictated by the searchMethod field
  // can be none, breadth first, depth first, or user controlled
  ArrayList<Cell> search() {
    if (this.searchMethod == 0) {
      return new ArrayList<Cell>();
    }
    else if (this.searchMethod == 1) {
      return this.dfsSearch(
          this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2),
          this.cells.get(0).get(0));
    }
    else if (this.searchMethod == 2) {
      return this.bfsSearch(
          this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2),
          this.cells.get(0).get(0));
    }
    else if (this.searchMethod == 3) {
      return this.bfsSearch(
          this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2),
          this.cells.get(0).get(0));
    }
    else {
      return new ArrayList<Cell>();
    }
  }

  // initializes the hashmap
  // setting each key as its own value
  void minSpanInit() {
    for (ArrayList<Vertex> row : this.allVertices) {
      for (Vertex v : row) {
        reps.put(v, v);
      }
    }
  }

  // Kruskal's algorithm via union in find comes together
  // in this method
  ArrayList<Edge> minSpan() {
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    List<Edge> worklist = this.allEdgesFlat;
    // while loop which will continue until there is one tree left
    while (worklist.size() > 0) {
      // System.out.println(worklist.size() + " elements remaining in
      // worklist...");
      Edge temp = worklist.remove(0);
      Vertex fromRep = this.find(temp.from);
      Vertex toRep = this.find(temp.to);
      if (!fromRep.equals(toRep) || temp.weight == 0) {
        edgesInTree.add(temp);
        this.reps.put(fromRep, toRep);
      }
    }
    return edgesInTree;
  }

  // Currently used only for testing Deque functionality (quickly making Queues)
  <T> Queue<T> listToQueue(List<T> list) {
    Queue<T> out = new Queue<T>();
    for (T t : list) {
      out.add(t);
    }
    return out;
  }

  // Currently used only for testing Deque functionality (quickly making Stacks)
  <T> Stack<T> listToStack(List<T> list) {
    Stack<T> out = new Stack<T>();
    for (T t : list) {
      out.add(t);
    }
    return out;
  }

  // checks the key to the value, and returns
  // the top of the tree
  Vertex find(Vertex from) {
    if (this.reps.get(from).equals(from)) {
      return from;
    }
    else {
      return this.find(this.reps.get(from));
    }
  }

  // returns the ArrayList<ArrayList<Edge>> as an ArrayList<Edge>
  ArrayList<Edge> flatten(ArrayList<ArrayList<Edge>> edges) {
    ArrayList<Edge> e = new ArrayList<Edge>();
    for (int i = 0; i < edges.size(); i++) {
      for (int j = 0; j < edges.get(i).size(); j++) {
        Edge temp = edges.get(i).get(j);
        e.add(temp);
      }
    }
    return e;
  }

  // makes the cells using the constants to control size and amount
  ArrayList<ArrayList<Cell>> makeCells() {
    ArrayList<ArrayList<Cell>> c = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < GameConstants.ROW_VERTICES - 1; i += 1) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < GameConstants.COL_VERTICES - 1; j += 1) {
        Cell temp = new Cell(i * GameConstants.CELL_SIZE, j * GameConstants.CELL_SIZE,
            GameConstants.CELL_SIZE);
        row.add(temp);
      }
      c.add(row);
    }
    return c;
  }

  // links each cell to its neighboring cells, edges, and vertices
  void updateCells() {
    for (int i = 0; i < this.allVertices.size() - 1; i++) {
      for (int j = 0; j < this.allVertices.get(0).size() - 1; j++) {
        Cell tempCell = this.cells.get(i).get(j);
        Vertex tl = this.allVertices.get(i).get(j);
        tempCell.tl = tl;
        tempCell.vertices.put(0, tl);
        Vertex tr = this.allVertices.get(i + 1).get(j);
        tempCell.tr = tr;
        tempCell.vertices.put(1, tr);
        Vertex bl = this.allVertices.get(i).get(j + 1);
        tempCell.bl = bl;
        tempCell.vertices.put(2, bl);
        Vertex br = this.allVertices.get(i + 1).get(j + 1);
        tempCell.br = br;
        tempCell.vertices.put(3, br);

        if (j > 0) {
          Cell top = this.cells.get(i).get(j - 1);
          tempCell.topCell = top;
          tempCell.cells.put(0, top);
        }
        if (j < this.allVertices.get(0).size() - 2) {
          Cell bottom = this.cells.get(i).get(j + 1);
          tempCell.bottomCell = bottom;
          tempCell.cells.put(1, bottom);
        }
        if (i > 0) {
          Cell left = this.cells.get(i - 1).get(j);
          tempCell.leftCell = left;
          tempCell.cells.put(2, left);
        }
        if (i < this.allVertices.size() - 2) {
          Cell right = this.cells.get(i + 1).get(j);
          tempCell.rightCell = right;
          tempCell.cells.put(3, right);
        }

        for (Edge e : tempCell.tl.outEdges) {
          if (this.minSpanTree.contains(e)) {
            if (e.to.equals(tr)) {
              tempCell.topEdge = e;
              tempCell.edges.put(0, e);
            }
            if (e.to.equals(bl)) {
              tempCell.leftEdge = e;
              tempCell.edges.put(2, e);
            }
          }
        }
        for (Edge e : tempCell.br.outEdges) {
          if (this.minSpanTree.contains(e)) {
            if (e.from.equals(tr)) {
              tempCell.rightEdge = e;
              tempCell.edges.put(3, e);
            }
            if (e.from.equals(bl)) {
              tempCell.bottomEdge = e;
              tempCell.edges.put(1, e);
            }
          }
        }
      }
    }
  }

  // makes the vertices using the constants to control amount
  ArrayList<ArrayList<Vertex>> makeVertices() {
    ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < GameConstants.ROW_VERTICES; i++) {
      ArrayList<Vertex> row = new ArrayList<Vertex>();
      for (int j = 0; j < GameConstants.COL_VERTICES; j++) {
        row.add(new Vertex(i + j * GameConstants.ROW_VERTICES, i * GameConstants.CELL_SIZE,
            j * GameConstants.CELL_SIZE));
      }
      vertices.add(row);
    }
    return vertices;
  }

  // makes the edges for the maze, split into two sections
  // vertical and horizontal.
  ArrayList<ArrayList<Edge>> makeEdges() {
    ArrayList<ArrayList<Edge>> edges = new ArrayList<ArrayList<Edge>>();
    // horizontal edges
    for (int i = 0; i < this.allVertices.size() - 1; i++) {
      ArrayList<Edge> row = new ArrayList<Edge>();
      for (int j = 0; j < this.allVertices.get(0).size(); j++) {
        // System.out.println(i + " : " + j);
        if (j == 0 || j == this.allVertices.get(0).size() - 1) {
          row.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i + 1).get(j), 0));
        }
        else {
          row.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i + 1).get(j),
              (int) Math.round(Math.random() * 100 + 1)));
        }
      }
      edges.add(row);
    }
    // vertical edges
    for (int i = 0; i < this.allVertices.size(); i++) {
      ArrayList<Edge> row = new ArrayList<Edge>();
      for (int j = 0; j < this.allVertices.get(0).size() - 1; j++) {
        if (i == 0 || i == this.allVertices.size() - 1) {
          row.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i).get(j + 1), 0));
        }
        else {
          row.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i).get(j + 1),
              (int) Math.round(Math.random() * 100 + 1)));
        }
      }
      edges.add(row);
    }
    return edges;
  }

  // World method for making the scene, drawing it
  // also colors in the start and end point
  public WorldScene makeScene() {
    WorldScene bg = this.getEmptyScene();
    if (!this.displayMenu) {
      WorldImage start = new RectangleImage(GameConstants.CELL_SIZE, GameConstants.CELL_SIZE,
          "solid", Color.GREEN);
      bg.placeImageXY(start, GameConstants.CELL_SIZE / 2, GameConstants.CELL_SIZE / 2);
      WorldImage end = new RectangleImage(GameConstants.CELL_SIZE, GameConstants.CELL_SIZE, "solid",
          Color.PINK);
      bg.placeImageXY(end, GameConstants.WINDOW_WIDTH - GameConstants.CELL_SIZE / 2,
          GameConstants.WINDOW_HEIGHT - GameConstants.CELL_SIZE / 2);

      // using the created arraylist to create the maze per edge
      for (Edge e : this.minSpanTree) {
        bg.placeImageXY(new LineImage(new Posn(e.to.x - e.from.x, e.to.y - e.from.y), Color.BLACK),
            e.from.x + (e.to.x - e.from.x) / 2, e.from.y + (e.to.y - e.from.y) / 2);
      }

      int currScore;

      this.currentCell = this.visited.get(this.visited.size() - 1);

      if (this.drawPath == 2 && this.searchMethod != 3) {
        if (this.tickNum / GameConstants.TICK >= this.visited.size()) {
          this.drawList = this.visited;
          currScore = this.score;
          if (this.searchMethod != 0) {
            this.mazeSolved = true;
          }
        }
        else {
          this.drawList = new ArrayList<Cell>(
              this.visited.subList(0, this.tickNum / GameConstants.TICK));
          ArrayList<Cell> notInPath = this.notInPath(drawList);
          currScore = notInPath.size();
        }
        bg = this.drawNext(bg, this.drawList, new Color(75, 200, 200), new Color(133, 234, 234),
            currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
      else if (this.drawPath == 1 && this.searchMethod != 3) {
        if (this.tickNum / GameConstants.TICK >= this.visited.size()) {
          this.drawList = this.visited;
          currScore = this.score;
          if (this.searchMethod != 0) {
            this.mazeSolved = true;
          }
        }
        else {
          this.drawList = new ArrayList<Cell>(
              this.visited.subList(0, this.tickNum / GameConstants.TICK));
          ArrayList<Cell> notInPath = this.notInPath(drawList);
          currScore = notInPath.size();
        }
        bg = this.drawNext(bg, this.drawList, new Color(75, 200, 200), new Color(75, 200, 200),
            currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
      else if (this.drawPath == 0 && this.searchMethod != 3) {
        if (this.tickNum / GameConstants.TICK >= this.visited.size()) {
          this.drawList = this.visited;
          currScore = this.score;
          if (this.searchMethod != 0) {
            this.mazeSolved = true;
          }
        }
        else {
          this.drawList = new ArrayList<Cell>();
          ArrayList<Cell> notInPath = this.notInPath(drawList);
          currScore = notInPath.size();
        }
        bg = this.drawNext(bg, this.drawList, new Color(75, 200, 200), new Color(75, 200, 200),
            currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
      else if (this.drawPath == 2) {
        this.drawList = this.visited;
        ArrayList<Cell> notInPath = this.notInPath(drawList);
        if (this.currentCell.equals(
            this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2))) {
          this.mazeSolved = true;
        }
        currScore = notInPath.size();
        bg = this.drawNext(bg, this.drawList, new Color(200, 120, 40), new Color(240, 160, 90),
            currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
      else if (this.drawPath == 1) {
        this.drawList = this.visited;
        ArrayList<Cell> notInPath = this.notInPath(drawList);
        if (this.currentCell.equals(
            this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2))) {
          this.mazeSolved = true;
        }
        currScore = notInPath.size();
        bg = this.drawNext(bg, this.drawList, new Color(200, 120, 40), new Color(200, 120, 40),
            currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
      else if (this.drawPath == 0) {
        this.drawList = this.visited;
        ArrayList<Cell> notInPath = this.notInPath(drawList);
        if (this.currentCell.equals(
            this.cells.get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2))) {
          this.mazeSolved = true;
        }
        currScore = notInPath.size();
        bg = this.drawNext(bg, new ArrayList<Cell>(), new Color(200, 120, 40),
            new Color(240, 160, 90), currScore);
        bg.placeImageXY(
            new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2, "solid",
                Color.yellow),
            this.currentCell.x + GameConstants.CELL_SIZE / 2,
            this.currentCell.y + GameConstants.CELL_SIZE / 2);
      }
    }
    else {
      bg.placeImageXY(new TextImage("Maze Game", 50, Color.BLUE), GameConstants.WINDOW_WIDTH / 2,
          GameConstants.WINDOW_HEIGHT / 2);
      bg.placeImageXY(new TextImage("Press r to make a new maze", 20, Color.BLUE),
          GameConstants.WINDOW_WIDTH / 2, GameConstants.WINDOW_HEIGHT / 2 + 50);
    }
    return bg;
  }

  // moves the current cell for player controlled maze solving
  void move(String key) {
    if (key.equals("right")) {
      if (this.currentCell.edges.get(3) == null) {
        this.currentCell = this.currentCell.cells.get(3);
        this.visited.add(currentCell);
      }
    }
    else if (key.equals("left")) {
      if (this.currentCell.edges.get(2) == null) {
        this.currentCell = this.currentCell.cells.get(2);
        this.visited.add(currentCell);
      }
    }
    else if (key.equals("down")) {
      if (this.currentCell.edges.get(1) == null) {
        this.currentCell = this.currentCell.cells.get(1);
        this.visited.add(currentCell);
      }
    }
    else if (key.equals("up")) {
      if (this.currentCell.edges.get(0) == null) {
        this.currentCell = this.currentCell.cells.get(0);
        this.visited.add(currentCell);
      }
    }
  }

  // The algorithm for determining the shortest path from source to target
  // Used for both breadth first and depth first searching, depending on the
  // worklist
  // Returns an arraylist of the cells in the shortest path
  ArrayList<Cell> shortestPathCell(Cell source, Cell target, ICollection<Cell> unvisited) {
    HashMap<Cell, Integer> distance = new HashMap<Cell, Integer>();
    HashMap<Cell, Cell> predecessors = new HashMap<Cell, Cell>();
    unvisited.add(source);
    distance.put(source, 0);
    while (unvisited.size() > 0) {
      Cell c = unvisited.remove();
      if (this.searchMethod != 3) {
        this.visited.add(c);
        this.score += 1;
      }
      if (c.equals(target)) {
        break;
      }
      for (Entry<Integer, Cell> temp : c.cells.entrySet()) {
        Integer index = temp.getKey();
        Cell neighbor = temp.getValue();
        if (c.edges.get(index) == null) {
          if (!distance.containsKey(neighbor)
              || distance.get(neighbor) > distance.get(c) + GameConstants.CELL_SIZE) {
            distance.put(neighbor, distance.get(c) + GameConstants.CELL_SIZE);
            unvisited.add(neighbor);
            predecessors.put(neighbor, c);
          }
        }
      }
    }
    ArrayList<Cell> path = new ArrayList<Cell>();
    if (predecessors.get(target) != null) {
      path.add(target);
      while (path.get(0) != source) {
        path.add(0, predecessors.get(path.get(0)));
      }
    }
    return path;
  }

  // will be the depth first search
  // currently not used yet
  ArrayList<Cell> dfsSearch(Cell to, Cell from) {
    return this.shortestPathCell(from, to, new Stack<Cell>());
  }

  // will be the breadth first search but currently not used
  ArrayList<Cell> bfsSearch(Cell to, Cell from) {
    return this.shortestPathCell(from, to, new Queue<Cell>());
  }

  // Selection sorting method which we created
  // We were told by a Tutor that we can use built in sorting methods
  // but that this was a nice touch to have! (even if it is not used due to it
  // being slower)
  <T> void sort(ArrayList<T> arr, IComparator<T> comp) {
    for (int i = 0; i < arr.size(); i++) {
      int indOfMin = this.findMinIndex(arr, i, comp);
      this.swap(arr, i, indOfMin);
    }
  }

  // finds the smallest index, which is utilized in sort
  <T> int findMinIndex(ArrayList<T> list, int i, IComparator<T> comp) {
    int currentMin = i;
    for (int g = i + 1; g < list.size(); g++) {
      if (comp.compare(list.get(currentMin), list.get(g)) > 0) {
        currentMin = g;
      }
    }
    return currentMin;
  }

  // swaps two indices, which was utilized in sort
  <T> void swap(ArrayList<T> arr, int ind1, int ind2) {
    T temp = arr.get(ind1);
    arr.set(ind1, arr.get(ind2));
    arr.set(ind2, temp);
  }

  // a tick handler to increment the tick count
  public void onTick() {
    this.tickNum += 1;
  }

  // returns an image with the path drawn on the maze
  WorldScene drawNext(WorldScene bg, ArrayList<Cell> cells, Color path, Color visited, int score) {
    Color color;
    for (Cell c : cells) {
      if (c.equals(this.currentCell)) {
        color = Color.yellow;
      }
      else if (this.path.contains(c)) {
        color = path;
      }
      else {
        color = visited;
      }
      bg.placeImageXY(new RectangleImage(GameConstants.CELL_SIZE - 2, GameConstants.CELL_SIZE - 2,
          "solid", color), c.x + GameConstants.CELL_SIZE / 2, c.y + GameConstants.CELL_SIZE / 2);
      bg.placeImageXY(new TextImage("Mistakes Made: " + Integer.toString(score), 20, Color.BLUE),
          GameConstants.WINDOW_WIDTH / 2, 20);
    }
    return bg;
  }

  // a key handler to check for user input
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.tickNum = 0;
      this.displayMenu = false;
      this.score = 0;
      this.makeWorld(true);
      this.visited = new ArrayList<Cell>();
      this.path = this.search();
      this.currentCell = this.cells.get(0).get(0);
      this.visited.add(currentCell);
    }
    else if (key.equals("p")) {
      this.drawPath += 1;
      if (this.drawPath > 2) {
        this.drawPath = 0;
      }
    }
    else if (key.equals("d")) {
      this.tickNum = 0;
      this.score = 0;
      this.searchMethod = 1;
      this.visited = new ArrayList<Cell>();
      this.path = this.search();
    }
    else if (key.equals("b")) {
      this.tickNum = 0;
      this.score = 0;
      this.searchMethod = 2;
      this.visited = new ArrayList<Cell>();
      this.path = this.search();
    }
    else if (key.equals("n")) {
      this.tickNum = 0;
      this.score = 0;
      this.searchMethod = 0;
      this.visited = new ArrayList<Cell>();
      this.path = this.search();
    }
    else if (key.equals("c")) {
      this.tickNum = 0;
      this.score = 0;
      this.searchMethod = 3;
      this.visited = new ArrayList<Cell>();
      this.path = this.search();
      this.currentCell = this.cells.get(0).get(0);
      this.visited.add(currentCell);
    }
    else if (this.searchMethod == 3) {
      if (key.equals("right") || key.equals("left") || key.equals("up") || key.equals("down")) {
        this.move(key);
      }
    }
  }

  // returns cells from the given list that are not in the path
  ArrayList<Cell> notInPath(ArrayList<Cell> cells) {
    ArrayList<Cell> out = new ArrayList<Cell>();
    for (Cell c : cells) {
      if (!this.path.contains(c)) {
        out.add(c);
      }
    }
    return out;
  }

  // game ends when the maze is solved
  public WorldEnd worldEnds() {
    if (this.mazeSolved) {
      return new WorldEnd(true, this.lastScene("The maze is solved!"));
    }
    return new WorldEnd(false, this.makeScene());
  }

  // end scene representation
  public WorldScene lastScene(String s) {
    WorldScene bg = this.makeScene();
    bg.placeImageXY(new TextImage(s, 28, Color.red), GameConstants.WINDOW_WIDTH / 2,
        GameConstants.WINDOW_HEIGHT / 2);
    return bg;
  }
}