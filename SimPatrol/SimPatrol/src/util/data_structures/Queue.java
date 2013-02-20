/* Queue.java */

/* The package of this class. */
package util.data_structures;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;

/**
 * Implements queues, obviously respecting the First In First Out access law.
 */
public final class Queue<E> {
	/* Attributes. */
	/** The linked list that holds the queue. */
	private final LinkedList<E> QUEUE;

	/* Methods. */
	/** Constructor. */
	public Queue() {
		this.QUEUE = new LinkedList<E>();
	}

	/**
	 * Inserts an element into the queue.
	 * 
	 * @param object
	 *            The element to be inserted.
	 */
	public synchronized void insert(E object) {
		this.QUEUE.addLast(object);
	}

	/**
	 * Removes the first element from the queue.
	 * 
	 * @return The first element from the queue.
	 */
	public synchronized E remove() {
		if (this.QUEUE.size() > 0)
			return this.QUEUE.remove(0);
		else
			return null;
	}

	/** Removes all the elements from the queue. */
	public synchronized void clear() {
		this.QUEUE.clear();
	}

	/**
	 * Returns the number of objects in the queue.
	 * 
	 * @return The size of the queue.
	 */
	public synchronized int getSize() {
		return this.QUEUE.size();
	}
}