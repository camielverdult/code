package groove.explore.util;

import java.util.Random;

/** Allows to choose a random element from a sequence 
 * without storing all the elements of the sequence.
 * On can "show" objects from the sequence to the chooser
 * and, at each moment, the chooser can give back one
 * random element among those it has seen up to now.
 * It also guarantees that all seen elements can be
 * picked with equal probability.
 * 
 * A typical usage is to show to the chooser some number of
 * elements, and then ask for a random representative. 
 * Note that two successive calls to {@link #pickRandom()} 
 * will return the same element. This cannot be avoided if
 * one does not want to store the whole sequence.
 * @author Iovka Boneva
 *
 * @param <E>
 */
public class RandomChooserInSequence<E> {

	/** Shows an element to the random chooser.
	 * @param e
	 * @throws NullPointerException if <code>e</code> is null
	 */
	public void show (E e) {
		if (e == null) { throw new NullPointerException(); }
		this.nbSeen++;
		if (rgen.nextInt(nbSeen) == 0) {
			this.current = e;
		} 
	}
	
	/** Gives back a random element among those
	 * that have been seen so far. Two successive calls
	 * to this method will give the same element.
	 * @see #show
	 * @return a random element among those that have been seen
	 * so far, or null if no element is seen.
	 */
	public E pickRandom () {
		return current;
	}
	
	/** Forgets all elements seen so far. */
	public void reset () {
		this.nbSeen = 0;
		this.current = null;
	}
	
	private int nbSeen = 0;
	private E current;
	private Random rgen = new Random();
	
//	public static void main (String[] args) {
//		RandomChooserInSequence<Integer> rc = new RandomChooserInSequence<Integer>();
//		for (int k = 0; k < 5; k++) {
//			System.out.println("---------------------------");
//			for (int i = 0; i < 10; i++) {
//				rc.reset();
//				for (int j = 0; j <= i; j++) {
//					rc.show(j);
//				}
//				System.out.println("Random in [0, " + i + "] : " + rc.pickRandom());
//			}
//		}
//	}
}
