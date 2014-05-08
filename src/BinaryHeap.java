import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Queue;

public class BinaryHeap {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	
	/**
	 * Priority queue represented as a balanced binary heap: the two children of
	 * queue[n] are queue[2*n+1] and queue[2*(n+1)]. The priority queue is ordered
	 * by comparator, or by the elements' natural ordering, if comparator is null:
	 * For each node n in the heap and each descendant d of n, n <= d. The element
	 * with the lowest value is in queue[0], assuming the queue is nonempty.
	 */
	private transient Node[] queue;
	
	/**
	 * The number of elements in the priority queue.
	 */
	private int size = 0;
	
	/**
	 * The comparator, or null if priority queue uses elements' natural ordering.
	 */
	private final Comparator<Node> comparator;
	
	/**
	 * Creates a {@code PriorityQueue} with the default initial capacity (11) that
	 * orders its elements according to their {@linkplain Comparable natural
	 * ordering}.
	 */
	public BinaryHeap() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}
	
	/**
	 * Creates a {@code PriorityQueue} with the specified initial capacity that
	 * orders its elements according to their {@linkplain Comparable natural
	 * ordering}.
	 * 
	 * @param initialCapacity
	 *          the initial capacity for this priority queue
	 * @throws IllegalArgumentException
	 *           if {@code initialCapacity} is less than 1
	 */
	public BinaryHeap(int initialCapacity) {
		this(initialCapacity, null);
	}
	
	/**
	 * Creates a {@code PriorityQueue} with the specified initial capacity that
	 * orders its elements according to the specified comparator.
	 * 
	 * @param initialCapacity
	 *          the initial capacity for this priority queue
	 * @param comparator
	 *          the comparator that will be used to order this priority queue. If
	 *          {@code null}, the {@linkplain Comparable natural ordering} of the
	 *          elements will be used.
	 * @throws IllegalArgumentException
	 *           if {@code initialCapacity} is less than 1
	 */
	public BinaryHeap(int initialCapacity, Comparator<Node> comparator) {
		// Note: This restriction of at least one is not actually needed,
		// but continues for 1.5 compatibility
		if (initialCapacity < 1)
			throw new IllegalArgumentException();
		this.queue = new Node[initialCapacity];
		this.comparator = comparator;
	}
	
	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words
	 * in an array. Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	/**
	 * Increases the capacity of the array.
	 * 
	 * @param minCapacity
	 *          the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		int oldCapacity = queue.length;
		// Double size if small; else grow by 50%
		int newCapacity = oldCapacity
		    + ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
		// overflow-conscious code
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		queue = Arrays.copyOf(queue, newCapacity);
	}
	
	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}
	
	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws ClassCastException
	 *           if the specified element cannot be compared with elements
	 *           currently in this priority queue according to the priority
	 *           queue's ordering
	 * @throws NullPointerException
	 *           if the specified element is null
	 */
	public boolean add(Node e) {
		return offer(e);
	}
	
	public void addAll(Collection<Node> c) {
		for (Node n : c)
			add(n);
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *           if the specified element cannot be compared with elements
	 *           currently in this priority queue according to the priority
	 *           queue's ordering
	 * @throws NullPointerException
	 *           if the specified element is null
	 */
	public boolean offer(Node e) {
		if (e == null)
			throw new NullPointerException();
		int i = size;
		if (i >= queue.length)
			grow(i + 1);
		size = i + 1;
		if (i == 0) {
			setNodePos(e, 0);
			queue[0] = e;
		} else
			siftUp(i, e);
		return true;
	}
	
	private static final void setNodePos(Node n, int pos) {
		n.set("heap.idx", pos);
	}
	
	private static final void unsetNodePos(Node n) {
		n.getProps().remove("heap.idx");
	}
	
	private static final int getNodePos(Node n) {
		Integer pos = (Integer) n.get("heap.idx");
		if (pos == null)
			return -1;
		return pos;
	}
	
	public Node peek() {
		if (size == 0)
			return null;
		return queue[0];
	}
	
	private int indexOf(Node o) {
		if (o != null) {
			int idx = getNodePos(o);
			if (o == queue[idx])
				return idx;
			else
				throw new IllegalStateException("Disse que estava em " + idx
				    + ", mas não achei!");
		}
		return -1;
	}
	
	/**
	 * Removes a single instance of the specified element from this queue, if it
	 * is present. More formally, removes an element {@code e} such that
	 * {@code o.equals(e)}, if this queue contains one or more such elements.
	 * Returns {@code true} if and only if this queue contained the specified
	 * element (or equivalently, if this queue changed as a result of the call).
	 * 
	 * @param o
	 *          element to be removed from this queue, if present
	 * @return {@code true} if this queue changed as a result of the call
	 */
	public boolean remove(Node o) {
		int i = indexOf(o);
		if (i == -1)
			return false;
		else {
			removeAt(i);
			return true;
		}
	}
	
	/**
	 * Version of remove using reference equality, not equals. Needed by
	 * iterator.remove.
	 * 
	 * @param o
	 *          element to be removed from this queue, if present
	 * @return {@code true} if removed
	 */
	boolean removeEq(Node o) {
		for (int i = 0; i < size; i++) {
			if (o == queue[i]) {
				removeAt(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns {@code true} if this queue contains the specified element. More
	 * formally, returns {@code true} if and only if this queue contains at least
	 * one element {@code e} such that {@code o.equals(e)}.
	 * 
	 * @param o
	 *          object to be checked for containment in this queue
	 * @return {@code true} if this queue contains the specified element
	 */
	public boolean contains(Node o) {
		return indexOf(o) != -1;
	}
	
	public int size() {
		return size;
	}
	
	/**
	 * Removes all of the elements from this priority queue. The queue will be
	 * empty after this call returns.
	 */
	public void clear() {
		for (int i = 0; i < size; i++) {
			unsetNodePos(queue[i]);
			queue[i] = null;
		}
		size = 0;
	}
	
	public Node poll() {
		if (size == 0)
			return null;
		int s = --size;
		Node result = queue[0];
		Node x = queue[s];
		unsetNodePos(queue[s]);
		queue[s] = null;
		if (s != 0)
			siftDown(0, x);
		return result;
	}
	
	/**
	 * Removes the ith element from queue.
	 * 
	 * Normally this method leaves the elements at up to i-1, inclusive,
	 * untouched. Under these circumstances, it returns null. Occasionally, in
	 * order to maintain the heap invariant, it must swap a later element of the
	 * list with one earlier than i. Under these circumstances, this method
	 * returns the element that was previously at the end of the list and is now
	 * at some position before i. This fact is used by iterator.remove so as to
	 * avoid missing traversing elements.
	 */
	private Node removeAt(int i) {
		assert i >= 0 && i < size;
		int s = --size;
		if (s == i) { // removed last element
			unsetNodePos(queue[i]);
			queue[i] = null;
		} else {
			Node moved = queue[s];
			unsetNodePos(queue[s]);
			queue[s] = null;
			siftDown(i, moved);
			if (queue[i] == moved) {
				siftUp(i, moved);
				if (queue[i] != moved)
					return moved;
			}
		}
		return null;
	}
	
	/**
	 * Inserts item x at position k, maintaining heap invariant by promoting x up
	 * the tree until it is greater than or equal to its parent, or is the root.
	 * 
	 * To simplify and speed up coercions and comparisons. the Comparable and
	 * Comparator versions are separated into different methods that are otherwise
	 * identical. (Similarly for siftDown.)
	 * 
	 * @param k
	 *          the position to fill
	 * @param x
	 *          the item to insert
	 */
	private void siftUp(int k, Node x) {
		siftUpUsingComparator(k, x);
	}
	
	private void siftUpUsingComparator(int k, Node x) {
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Node e = queue[parent];
			if (comparator.compare(x, e) >= 0)
				break;
			setNodePos(e, k);
			queue[k] = e;
			k = parent;
		}
		setNodePos(x, k);
		queue[k] = x;
	}
	
	/**
	 * Inserts item x at position k, maintaining heap invariant by demoting x down
	 * the tree repeatedly until it is less than or equal to its children or is a
	 * leaf.
	 * 
	 * @param k
	 *          the position to fill
	 * @param x
	 *          the item to insert
	 */
	private void siftDown(int k, Node x) {
		siftDownUsingComparator(k, x);
	}
	
	private void siftDownUsingComparator(int k, Node x) {
		int half = size >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			Node c = queue[child];
			int right = child + 1;
			if (right < size && comparator.compare(c, queue[right]) > 0)
				c = queue[child = right];
			if (comparator.compare(x, c) <= 0)
				break;
			setNodePos(c, k);
			queue[k] = c;
			k = child;
		}
		setNodePos(x, k);
		queue[k] = x;
	}
	
	/**
	 * Returns the comparator used to order the elements in this queue, or
	 * {@code null} if this queue is sorted according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @return the comparator used to order this queue, or {@code null} if this
	 *         queue is sorted according to the natural ordering of its elements
	 */
	public Comparator<Node> comparator() {
		return comparator;
	}
	
}