// Assignment 10 Part 1

// Bauer Zach
// zbauer
// Nahar Ateev
// nahar

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import tester.*;

// Constants class to make changes easy and quick
// Game generates large mazes, but tests on full maze, 
// so for large maze, will run out of memory
class GameConstants {
  final static int ROW_VERTICES = 10;
  final static int COL_VERTICES = 10;

  final static int CELL_SIZE = 40;

  final static int WINDOW_HEIGHT = (COL_VERTICES - 1) * CELL_SIZE;
  final static int WINDOW_WIDTH = (ROW_VERTICES - 1) * CELL_SIZE;

  final static int TICK = 2;
}

// represents an edge, which connects two
// has a weight which is used in determining a path
// from the start to the finish via our methods
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  // constructor for edge class
  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    from.addToOutEdges(this);
    this.to = to;
    to.addToOutEdges(this);
    this.weight = weight;
  }
}

// representation of a vertex
// used to make outside edges, and for making the path
// described above
class Vertex {
  int id;
  ArrayList<Edge> outEdges;
  int x;
  int y;

  // constructor for vertex class
  Vertex(int id, int x, int y) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.outEdges = new ArrayList<Edge>();
  }

  // adds edge e to this outEdges
  public void addToOutEdges(Edge e) {
    this.outEdges.add(e);
  }
}

// represents a cell
class Cell {
  int x;
  int y;
  int width;

  Cell topCell;
  Cell bottomCell;
  Cell leftCell;
  Cell rightCell;

  Edge topEdge;
  Edge bottomEdge;
  Edge leftEdge;
  Edge rightEdge;

  Vertex tl;
  Vertex tr;
  Vertex bl;
  Vertex br;

  // Keys / Values for Edges and Cells
  // 0 / top
  // 1 / bottom
  // 2 / left
  // 3 / right

  HashMap<Integer, Edge> edges;
  HashMap<Integer, Cell> cells;

  // Keys / Values for Vertices
  // 0 / tl
  // 1 / tr
  // 2 / bl
  // 3 / br

  HashMap<Integer, Vertex> vertices;

  // constructor for cell class
  Cell(int x, int y, int width) {
    this.x = x;
    this.y = y;
    this.width = width;

    this.edges = new HashMap<Integer, Edge>();
    this.vertices = new HashMap<Integer, Vertex>();
    this.cells = new HashMap<Integer, Cell>();
  }
}

// Comparator interface for comparing
interface IComparator<T> {
  int compare(T t1, T t2);
}

// class ByEdgeWeight

// comparator for edge weights
class ByEdgeWeight implements Comparator<Edge> {
  public int compare(Edge t1, Edge t2) {
    return t1.weight - t2.weight;
  }
}

// int comparator
class ByInt implements IComparator<Integer> {
  public int compare(Integer i1, Integer i2) {
    return i1 - i2;
  }
}

// interface for affecting the Queue/Deque
interface ICollection<T> {
  T remove();

  void add(T t);

  int size();

  ANode<T> find(IPred<T> pred);
}

// these are part two things btw
// represents a Queue/Deque
class Queue<T> implements ICollection<T> {
  Deque<T> items;

  // constructor for Queue class
  Queue() {
    this.items = new Deque<T>();
  }

  // get the size of this Queue
  public int size() {
    return this.items.size();
  }

  // add the given item to this Queue
  public void add(T t) {
    this.items.addAtTail(t);
  }

  // remove the first item in this Queue
  public T remove() {
    return this.items.removeFromHead();
  }

  public ANode<T> find(IPred<T> pred) {
    return this.items.find(pred);
  }
}

// these are part two things btw
// represents a stack
class Stack<T> implements ICollection<T> {
  Deque<T> items;

  // stack constructor
  Stack() {
    this.items = new Deque<T>();
  }

  // get the size of this Queue
  public int size() {
    return this.items.size();
  }

  // add the given item to this Queue
  public void add(T t) {
    this.items.addAtHead(t);
  }

  // remove the first item in this Queue
  public T remove() {
    return this.items.removeFromHead();
  }

  // find the first item that meets the predicate
  public ANode<T> find(IPred<T> pred) {
    return this.items.find(pred);
  }
}

// examples class for testing, examples, and getting things to actually show up
// for the user!
class ExamplesMaze {
  Vertex a;
  Vertex b;
  Vertex c;

  Edge AB;
  Edge BC;
  Edge e1;
  Edge e2;

  ArrayList<Vertex> g1Vertices;

  ArrayList<Integer> ints;

  MazeWorld mw = new MazeWorld();

  Deque<String> deque1;
  Sentinel<String> s1;
  Sentinel<String> s1Empty;
  Deque<String> deque2;
  Node<String> abc;
  Node<String> bcd;
  Node<String> cde;
  Node<String> def;
  Sentinel<String> s2;
  Deque<String> deque2AddAtHead;
  Node<String> xyz;
  Node<String> abc1;
  Node<String> bcd1;
  Node<String> cde1;
  Node<String> def1;
  Deque<String> deque3;
  Sentinel<String> s3;
  Node<String> abc2;
  Node<String> bcd2;

  IPred<String> gt5 = new LengthGreaterThan5();
  IPred<String> gl5 = new LengthLessThan5();

  ICollection<Integer> coll1;
  ICollection<Integer> coll2;
  ICollection<Integer> coll3;

  MazeWorld mwTesting;

  void init() {
    this.a = new Vertex(1, 100, 100);
    this.b = new Vertex(2, 100, 200);
    this.c = new Vertex(3, 400, 100);

    this.AB = new Edge(this.a, this.b, 10);
    this.BC = new Edge(this.b, this.c, 10);
    this.e1 = new Edge(this.b, this.c, 20);
    this.e2 = new Edge(this.b, this.c, 2);

    this.ints = new ArrayList<Integer>();

    this.ints.add(1);
    this.ints.add(5);
    this.ints.add(10);
    this.ints.add(3);
    this.ints.add(7);

    this.coll1 = new Queue<Integer>();
    this.coll2 = this.mw.listToQueue(Arrays.asList(1, 5, 9, 4));
    this.coll3 = this.mw.listToStack(Arrays.asList(1, 5, 9, 4));

    this.mwTesting = new MazeWorld();
    this.mwTesting.displayMenu = false;
    this.mwTesting.makeWorld(false);
  }

  void initDeque() {
    this.deque1 = new Deque<String>();

    this.s1 = new Sentinel<String>();
    this.s1Empty = new Sentinel<String>();
    this.deque2 = new Deque<String>(this.s1);
    this.abc = new Node<String>("abc", this.s1, this.s1);
    this.bcd = new Node<String>("bcd", this.s1, this.abc);
    this.cde = new Node<String>("cde", this.s1, this.bcd);
    this.def = new Node<String>("def", this.s1, this.cde);

    this.s2 = new Sentinel<String>();
    this.deque2AddAtHead = new Deque<String>(this.s2);
    this.xyz = new Node<String>("xyz", this.s2, this.s2);
    this.abc1 = new Node<String>("abc", this.s2, this.xyz);
    this.bcd1 = new Node<String>("bcd", this.s2, this.abc1);
    this.cde1 = new Node<String>("cde", this.s2, this.bcd1);
    this.def1 = new Node<String>("def", this.s2, this.cde1);

    this.s3 = new Sentinel<String>();
    this.deque3 = new Deque<String>(this.s3);
    this.abc2 = new Node<String>("abc", this.s3, this.s3);
    this.bcd2 = new Node<String>("bcd", this.abc2, this.s3);
  }

  void testMaze(Tester t) {
    this.mw.bigBang(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT, .1);
  }

  void testInit(Tester t) {
    this.init();
    ArrayList<Edge> aEdges = new ArrayList<Edge>();
    aEdges.add(this.AB);
    t.checkExpect(this.a.outEdges, aEdges);
    ArrayList<Edge> bEdges = new ArrayList<Edge>();
    bEdges.add(this.AB);
    bEdges.add(this.BC);
    t.checkExpect(this.b.outEdges, bEdges);
    ArrayList<Edge> cEdges = new ArrayList<Edge>();
    cEdges.add(this.BC);
    t.checkExpect(this.c.outEdges, cEdges);
  }

  void testSwap(Tester t) {
    this.init();
    t.checkExpect(this.ints.indexOf(5), 1); // checking initial index positions
    t.checkExpect(this.ints.indexOf(3), 3); // before swapping
    this.mw.swap(this.ints, this.ints.indexOf(5), this.ints.indexOf(3));
    t.checkExpect(this.ints.indexOf(5), 3); // checking index positions
    t.checkExpect(this.ints.indexOf(3), 1); // after swapping
  }

  void testSort(Tester t) {
    this.init();
    ArrayList<Integer> sortedInts = new ArrayList<Integer>(Arrays.asList(1, 3, 5, 7, 10));
    t.checkExpect(this.ints.equals(sortedInts), false);
    this.mw.sort(this.ints, new ByInt());
    t.checkExpect(this.ints.equals(sortedInts), true);
  }

  void testFindMinIndex(Tester t) {
    this.init();
    t.checkExpect(this.mw.findMinIndex(this.ints, 0, new ByInt()), 0);
    t.checkExpect(this.mw.findMinIndex(this.ints, 3, new ByInt()), 3);
    t.checkExpect(this.mw.findMinIndex(this.ints, 2, new ByInt()), 3);
  }

  void testAddToOutEdges(Tester t) {
    Vertex vert1 = new Vertex(1, 10, 10);
    t.checkExpect(vert1.outEdges.size(), 0);
    vert1.addToOutEdges(this.AB);
    t.checkExpect(vert1.outEdges.size(), 1);
    t.checkExpect(vert1.outEdges.get(0), this.AB);
  }

  void testFlatten(Tester t) {
    ArrayList<ArrayList<Edge>> edges = new ArrayList<ArrayList<Edge>>();
    edges.add(new ArrayList<Edge>(Arrays.asList(this.AB)));
    t.checkExpect(edges.size(), 1);
    t.checkExpect(edges.get(0).size(), 1);
    ArrayList<Edge> edgesFlat = this.mw.flatten(edges);
    t.checkExpect(edgesFlat.size(), 1);
    edges.add(new ArrayList<Edge>(Arrays.asList(this.BC)));
    t.checkExpect(edges.size(), 2);
    t.checkExpect(edges.get(0).size(), 1);
    edgesFlat = this.mw.flatten(edges);
    t.checkExpect(edgesFlat.size(), 2);
  }

  void testMakeCells(Tester t) {
    this.init();
    for (int i = 0; i < this.mwTesting.cells.size(); i++) {
      for (int j = 0; j < this.mwTesting.cells.get(0).size(); j++) {
        t.checkExpect(this.mwTesting.cells.get(i).get(j).x, i * GameConstants.CELL_SIZE);
        t.checkExpect(this.mwTesting.cells.get(i).get(j).y, j * GameConstants.CELL_SIZE);
        t.checkExpect(this.mwTesting.cells.get(i).get(j).width, GameConstants.CELL_SIZE);
      }
    }
  }

  void testMakeVertices(Tester t) {
    this.init();
    t.checkExpect(this.mwTesting.allVertices.get(0).get(0).id,
        0 * this.mwTesting.allVertices.size());
    t.checkExpect(this.mwTesting.allVertices.get(0).get(1).id,
        1 * this.mwTesting.allVertices.size());
    t.checkExpect(this.mwTesting.allVertices.get(0).get(2).id,
        2 * this.mwTesting.allVertices.size());
    t.checkExpect(this.mwTesting.allVertices.size(), GameConstants.ROW_VERTICES);
    t.checkExpect(this.mwTesting.allVertices.get(0).size(), GameConstants.COL_VERTICES);
    for (int i = 0; i < this.mwTesting.allVertices.size(); i++) {
      for (int j = 0; j < this.mwTesting.allVertices.get(0).size(); j++) {
        t.checkExpect(this.mwTesting.allVertices.get(i).get(j).x, i * GameConstants.CELL_SIZE);
        t.checkExpect(this.mwTesting.allVertices.get(i).get(j).y, j * GameConstants.CELL_SIZE);
        t.checkExpect(this.mwTesting.allVertices.get(i).get(j).id,
            i + j * GameConstants.ROW_VERTICES);
      }
    }
  }

  void testMakeEdges(Tester t) {
    this.init();
    for (int i = 0; i < this.mwTesting.allVertices.size() - 1; i++) {
      for (int j = 0; j < this.mwTesting.allVertices.get(0).size(); j++) {
        if (j == 0 || j == this.mwTesting.allVertices.get(0).size() - 1) {
          t.checkExpect(this.mwTesting.allEdges.get(i).get(j).weight, 0);
        }
        else {
          t.checkRange(this.mwTesting.allEdges.get(i).get(j).weight, 1, 102);
        }
      }
    }
  }

  void testICollectionAdd(Tester t) {
    this.init();
    t.checkExpect(this.coll1.size(), 0);
    this.coll1.add(1);
    t.checkExpect(this.coll1.size(), 1);
    t.checkExpect(this.coll1.find(new IsInteger<Integer>()).getData(), 1);
    this.coll1.add(2);
    t.checkExpect(this.coll1.size(), 2);
    t.checkExpect(this.coll1.remove(), 1);
    // t.checkExpect(this.mw.listToQueue(this.mw.allEdgesFlat).size(),
    // this.mw.allEdgesFlat.size());
  }

  void testAddAtHead(Tester t) {
    this.initDeque();
    this.deque2.addAtHead("xyz");
    t.checkExpect(this.deque2.header.next.getData(), "xyz");
    t.checkExpect(this.deque3.header, this.s3);
    t.checkExpect(this.deque3.header.next, this.bcd2);
    t.checkExpect(this.deque3.header.prev, this.abc2);
  }

  void testAddAtTail(Tester t) {
    this.initDeque();
    this.deque3.addAtTail("xyz");
    t.checkExpect(this.deque3.header.next.getData(), "bcd");
    t.checkExpect(this.deque3.header.prev.getData(), "xyz");
  }

  void testICollectionSize(Tester t) {
    this.init();
    t.checkExpect(this.coll1.size(), 0);
    this.coll1.add(1);
    t.checkExpect(this.coll1.size(), 1);
    this.coll1.remove();
    t.checkExpect(this.coll1.size(), 0);
    for (int i = 0; i < 100; i++) {
      this.coll1.add(i);
      t.checkExpect(this.coll1.size(), i + 1);
    }
  }

  void testSize(Tester t) {
    this.initDeque();
    t.checkExpect(this.deque1.size(), 0);
    this.deque1.addAtHead("ghi");
    t.checkExpect(this.deque1.size(), 1);
    this.deque1.removeFromHead();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    this.deque2.removeFromHead();
    t.checkExpect(this.deque2.size(), 3);
    t.checkExpect(this.deque2AddAtHead.size(), 5);
  }

  void testICollectionRemove(Tester t) {
    this.init();
    t.checkExpect(this.coll2.size(), 4);
    t.checkExpect(this.coll2.remove(), 1);
    t.checkExpect(this.coll2.size(), 3);
    t.checkExpect(this.coll3.size(), 4);
    t.checkExpect(this.coll3.remove(), 4);
    t.checkExpect(this.coll3.size(), 3);
  }

  void testRemoveFromHead(Tester t) {
    this.initDeque();
    this.deque2.removeFromHead();
    t.checkExpect(this.deque2.header.next, this.bcd);
  }

  void testRemoveFromTail(Tester t) {
    this.initDeque();
    this.deque2.removeFromTail();
    t.checkExpect(this.deque2.header.prev, this.cde);
  }

  void testUpdateNext(Tester t) {
    this.initDeque();
    this.deque1.addAtHead("first");
    t.checkExpect(this.deque1.header.next.equals(this.deque1.header), false);
    t.checkExpect(this.deque1.header.next.getData(), "first");
    this.deque1.removeFromHead();
    t.checkExpect(this.deque1.header.next.equals(this.deque1.header), true);
  }

  void testUpdatePrev(Tester t) {
    this.initDeque();
    this.deque1.addAtHead("first");
    t.checkExpect(this.deque1.header.prev.equals(this.deque1.header), false);
    t.checkExpect(this.deque1.header.next.getData(), "first");
    this.deque1.removeFromHead();
    t.checkExpect(this.deque1.header.prev.equals(this.deque1.header), true);
  }

  void testFind(Tester t) {
    this.initDeque();
    t.checkExpect(this.deque1.find(this.gt5), new Sentinel<String>());
    t.checkExpect(this.deque2.find(this.gt5), this.s1);
    t.checkExpect(this.deque2.find(this.gl5), this.abc);
  }

  void testRemoveNode(Tester t) {
    this.initDeque();
    IPred<String> sameAs = new SameNode<String>(this.abc);
    t.checkExpect(this.deque1.find(sameAs), this.s1Empty);
    t.checkExpect(this.deque2.find(sameAs), this.abc);
    this.deque1.removeNode(this.abc);
    t.checkExpect(this.deque1.find(sameAs), this.s1Empty);
    t.checkExpect(this.deque2.find(sameAs), this.abc);
    this.deque2.removeNode(this.abc);
    t.checkExpect(this.deque1.find(sameAs), this.s1Empty);
    t.checkExpect(this.deque2.find(sameAs), this.s1);
  }

  void testByEdgeWeight(Tester t) {
    this.init();
    Comparator<Edge> bew = new ByEdgeWeight();
    t.checkExpect(bew.compare(this.AB, this.BC), 0);
    t.checkExpect(bew.compare(this.AB, this.e1), -10);
    t.checkExpect(bew.compare(this.AB, this.e2), 8);
  }

  void testByInt(Tester t) {
    this.init();
    IComparator<Integer> bi = new ByInt();
    t.checkExpect(bi.compare(10, 2), 8);
    t.checkExpect(bi.compare(2, 10), -8);
    t.checkExpect(bi.compare(10, 10), 0);
  }

  void testMove(Tester t) {
    this.init();
    this.mwTesting.currentCell = new Cell(0, 0, 10);
    this.mwTesting.currentCell.cells.put(3, new Cell(10, 0, 10));
    t.checkExpect(this.mwTesting.visited.size(), 0);
    this.mwTesting.move("right");
    t.checkExpect(this.mwTesting.visited.size(), 1);
    t.checkExpect(this.mwTesting.currentCell, new Cell(10, 0, 10));
    this.mwTesting.currentCell.edges.put(1,
        new Edge(new Vertex(1, 10, 10), new Vertex(2, 20, 10), 10));
    this.mwTesting.move("down");
    t.checkExpect(this.mwTesting.currentCell.x, 10);
    t.checkExpect(this.mwTesting.currentCell.y, 0);
    t.checkExpect(this.mwTesting.visited.size(), 1);
  }

  void testMinSpan(Tester t) {
    this.init();
    this.mwTesting.makeWorld(false);
    for (int i = 0; i < this.mwTesting.allVertices.size(); i++) {
      for (int j = 0; j < this.mwTesting.allVertices.get(0).size(); j++) {
        t.checkExpect(this.mwTesting.find(this.mwTesting.allVertices.get(i).get(j)),
            this.mwTesting.find(this.mwTesting.allVertices.get(0).get(0)));
      }
    }
  }

  void testMinSpanInit(Tester t) {
    this.init();
    this.mwTesting.minSpanInit();
    for (Entry<Vertex, Vertex> entry : this.mwTesting.reps.entrySet()) {
      t.checkExpect(entry.getKey(), entry.getValue());
    }
  }

  // testing depth first search via iterating the set path to check if contains
  // the proper path
  void testDfsSearch(Tester t) {
    this.init();
    this.mwTesting.makeWorld(false);
    ArrayList<Cell> dfs = this.mwTesting.dfsSearch(this.mwTesting.cells.get(0).get(0),
        this.mwTesting.cells.get(GameConstants.ROW_VERTICES - 2)
        .get(GameConstants.COL_VERTICES - 2));
    this.mwTesting.path = dfs;
    for (int i = 0; i < mwTesting.cells.size(); i++) {
      for (int j = 0; j < this.mwTesting.cells.get(0).size(); j++) {
        ArrayList<Cell> bfs = this.mwTesting.bfsSearch(this.mwTesting.cells.get(0).get(0),
            this.mwTesting.cells.get(GameConstants.ROW_VERTICES - 2)
            .get(GameConstants.COL_VERTICES - 2));
        t.checkExpect(this.mwTesting.path.get(i), bfs.get(i));
      }
    }
  }

  // testing breadth first search via iterating the set path to check if
  // contains the proper path
  void testBfsSearch(Tester t) {
    this.init();
    this.mwTesting.makeWorld(false);
    ArrayList<Cell> bfs = this.mwTesting.bfsSearch(this.mwTesting.cells.get(0).get(0),
        this.mwTesting.cells.get(GameConstants.ROW_VERTICES - 2)
        .get(GameConstants.COL_VERTICES - 2));
    this.mwTesting.path = bfs;
    for (int i = 0; i < mwTesting.cells.size(); i++) {
      for (int j = 0; j < this.mwTesting.cells.get(0).size(); j++) {
        ArrayList<Cell> dfs = this.mwTesting.dfsSearch(this.mwTesting.cells.get(0).get(0),
            this.mwTesting.cells.get(GameConstants.ROW_VERTICES - 2)
            .get(GameConstants.COL_VERTICES - 2));
        t.checkExpect(this.mwTesting.path.get(i), dfs.get(i));
      }
    }
  }

  void testScore(Tester t) {
    this.init();
    this.mwTesting.makeWorld(false);
    t.checkExpect(this.mwTesting.score, 0);
    this.mwTesting.path = this.mwTesting.dfsSearch(this.mwTesting.cells
        .get(GameConstants.ROW_VERTICES - 2).get(GameConstants.COL_VERTICES - 2),
        this.mwTesting.cells.get(0).get(0));
    this.mwTesting.currentCell = this.mwTesting.cells.get(0).get(0);
    if (this.mwTesting.path.get(0).x > 0) {
      this.mwTesting.move("right");
      t.checkRange(this.mwTesting.score, 0,
          this.mwTesting.cells.size() * this.mwTesting.cells.get(0).size());
    }
    else {
      this.mwTesting.move("down");
      t.checkRange(this.mwTesting.score, 0,
          this.mwTesting.cells.size() * this.mwTesting.cells.get(0).size());
    }
  }

  void testUpdateCells(Tester t) {
    this.init();
    this.mwTesting.makeWorld(false);
    this.mwTesting.cells = this.mwTesting.makeCells();
    this.mwTesting.currentCell = this.mwTesting.cells.get(0).get(0);
    t.checkExpect(mwTesting.currentCell.bottomCell, null);
    t.checkExpect(mwTesting.currentCell.topCell, null);
    t.checkExpect(mwTesting.currentCell.leftCell, null);
    t.checkExpect(mwTesting.currentCell.rightCell, null);
    mwTesting.updateCells();
    t.checkExpect(mwTesting.currentCell.bottomCell, mwTesting.cells.get(0).get(1));
    t.checkExpect(mwTesting.currentCell.topCell, null);
    t.checkExpect(mwTesting.currentCell.leftCell, null);
    t.checkExpect(mwTesting.currentCell.rightCell, mwTesting.cells.get(1).get(0));
  }
}