
//represents either a Sentinel or a Node
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // constructor with no params, sets both to null to be overwritten later
  ANode() {
    this.next = null;
    this.prev = null;
  }

  // constructor with two params, updates links accordingly
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    next.prev = this;
    this.prev = prev;
    prev.next = this;
  }

  /*
   * TEMPLATE fields: ... this.next ... ANode<T> ... this.prev ... ANode<T>
   * methods: ... this.updatePrev(ANode<T>) ... void ...
   * this.updateNext(ANode<T>) ... void ... this.removeFromHeadHelp(Sentinel<T>)
   * ... void ... this.removeFromTailHelp(Sentinel<T>) ... void ...
   * this.findHelp(IPred<T>, Sentinel<T>) ... ANode<T> ... this.size() ... int
   * ... this.size(Sentinel<T>, int) ... int ... this.getData() ... T
   */

  // updates links for the given (previous) node
  public void updatePrev(ANode<T> node) {
    this.prev = node;
    node.next = this;
  }

  // updates links for the given (next) node
  public void updateNext(ANode<T> node) {
    this.next = node;
    node.prev = this;
  }

  // removes the first node in list
  public void removeFromHeadHelp(Sentinel<T> header) {
    this.next.updatePrev(header);
    this.prev.updateNext(this.next);
  }

  // removes the last node in list
  public void removeFromTailHelp(Sentinel<T> header) {
    this.next.updatePrev(this.prev);
    this.prev.updateNext(header);
  }

  // finds the first node that satisfies the given pred
  public ANode<T> findHelp(IPred<T> pred, Sentinel<T> s) {
    return this;
  }

  // returns the number of nodes in this list
  public abstract int size(Sentinel<T> s, int count);

  // returns the number of nodes in this list
  public abstract int size();

  // returns the data for this ANode, if any
  public abstract T getData();
}

// represents a Deque
class Deque<T> {
  Sentinel<T> header;

  // constructor with no params, creates a new sentinel for the header
  Deque() {
    this.header = new Sentinel<T>();
  }

  // constructor with header as param
  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // adds new node with given value at beginning of list
  void addAtHead(T value) {
    this.header.updateNext(new Node<T>(value, this.header.next, this.header));
  }

  // adds new node with given value at end of list
  void addAtTail(T value) {
    this.header.updatePrev(new Node<T>(value, this.header, this.header.prev));
  }

  // removes first element in list, returns element
  T removeFromHead() {
    if (this.header.next != this.header) {
      ANode<T> temp = this.header.next;
      this.header.next.removeFromHeadHelp(this.header);
      return temp.getData();
    }
    else {
      throw new RuntimeException();
    }
  }

  // removes last element in list, returns element
  T removeFromTail() {
    if (this.header.next != this.header) {
      ANode<T> temp = this.header.prev;
      this.header.prev.removeFromTailHelp(this.header);
      return temp.getData();
    }
    else {
      throw new RuntimeException();
    }
  }

  // returns the first node that satisfies the given predicate
  ANode<T> find(IPred<T> pred) {
    return this.header.findHelp(pred);
  }

  // removes the given Node from the list, ignores if Sentinel
  void removeNode(ANode<T> node) {
    ANode<T> found = this.find(new SameNode<T>(node));
    found.next.updatePrev(found.prev);
    found.prev.updateNext(found.next);
  }

  // returns the number of Nodes in this list, used to start cycling through the
  // list
  int size() {
    return this.header.size();
  }
}

// represents a Sentinel
class Sentinel<T> extends ANode<T> {

  // constructor with no params, sets next and prev to this
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // returns the first Node in the list that satsfies the given predicate
  // if no match is found, returns the Sentinel
  public ANode<T> findHelp(IPred<T> pred) {
    if (this.next != this) {
      return this.next.findHelp(pred, this);
    }
    else {
      return this;
    }
  }

  // returns the number of Nodes in the list, used to start cycling through the
  // list
  public int size() {
    return this.next.size(this, 0);
  }

  // returns the number of Nodes in the list,
  // takes in the starting Sentinal and the current number
  public int size(Sentinel<T> s, int count) {
    return count;
  }

  // returns null, shouldn't be called on a Sentinel, since they don't have a
  // data field
  public T getData() {
    return null;
  }
}

// represents an individual Node in the list
class Node<T> extends ANode<T> {
  T data;

  // constructor with data param, sets next and prev to null
  Node(T data) {
    super();
    this.data = data;
  }

  // constructor with three params, updates links
  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.data = data;
    next.updatePrev(this);
    prev.updateNext(this);
    this.checkForNull(next, prev);
  }

  // checks if next or prev is null, throws exception if true
  void checkForNull(ANode<T> next, ANode<T> prev) {
    if (next == null || prev == null) {
      throw new IllegalArgumentException();
    }
  }

  // returns first Node to satisfy given predicate
  // if returns back to the Sentinel, returns the Sentinel
  public ANode<T> findHelp(IPred<T> pred, Sentinel<T> s) {
    if (pred.apply(this.data)) {
      return this;
    }
    else if (this.next != s) {
      return this.next.findHelp(pred, s);
    }
    else {
      return s;
    }
  }

  // returns the number of Nodes in the list,
  // takes in the beginning/ending Sentinel and the current number
  public int size(Sentinel<T> s, int count) {
    if (this.next != s) {
      return this.next.size(s, count + 1);
    }
    else {
      return 1 + count;
    }
  }

  // returns -1, shouldn't be called on a Node
  public int size() {
    return -1;
  }

  // returns the data for the Node
  public T getData() {
    return this.data;
  }
}

// Represents a boolean-valued question over values of type T
interface IPred<T> {
  boolean apply(T t);
}

// asks if given string is longer than 5 characters
class LengthGreaterThan5 implements IPred<String> {
  public boolean apply(String str) {
    return str.length() > 5;
  }
}

// asks if given string is shorter than 5 characters
class LengthLessThan5 implements IPred<String> {
  public boolean apply(String str) {
    return str.length() < 5;
  }
}

// asks if the given Node is the same as the field Node
class SameNode<T> implements IPred<T> {
  ANode<T> node;

  // constructor that takes in a Node for comparison
  SameNode(ANode<T> node) {
    this.node = node;
  }

  public boolean apply(T t) {
    return t.equals(this.node.getData());
  }
}

class EdgeWeight0 implements IPred<Edge> {
  public boolean apply(Edge e) {
    return e.weight == 0;
  }
}

// TODO remove this nonsense
class IsInteger<T> implements IPred<T> {
  public boolean apply(T t) {
    return t instanceof Integer;
  }
}