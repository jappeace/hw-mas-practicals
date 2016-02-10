
// standard java naming, we use the domain name of uu because
// I'm not so arogant to use my own, mas for multi-agent-systems
// bdyj our first names letters (and some extra entorpy to prevent
// name clashes)
package nl.uu.mas.bdyj;

// Just let your IDE figure out the imports, I don't care
import java.util.Random;

/**
 * Mother manages all kind of usefull information about your mother.
 *
 * Classes are written in PascalCase! Also note that each class should
 * have a brief description about their purpose in life.
 */
class Mother extends MassiveEntity{

	// Constants are in all caps
	// constants can also be public
	public static final int DEFAULT_MOM_MASS = 5;

	// final atributes can be public (read only, who cares)
	public final int mass;

	// mutable fields should *always* be private
	private int crazyness = 1;

	// mutable field #2, note the camelCase
	private long moonCount = 34591495201l;

	// Make only 1 constructor that only accepts x amount of
	// arguments that *have* to be conigured always. the fewer
	// arguments the better. Constructors are poor for configuring,
	// and only should set to reasonable defaults (avoid null)
	//
	// also note that I use final wherever I cann. This is defensive
	// programming.
	public Mother(final int mass){
		this.mass = mass;
	}

	// Another constructor as copy consturctor is fine I guess
	// The reason is that Java's clone method is confusing as shit, so better
	// avoid it with a copy constructor, but you probably don't need this
	public Mother(final Mother other){
		this.mass = other.mass;
		this.crazyness = other.crazyness; // Note btw how this is allowed
	}

	// You can show the state of private fields with 'get' methods.
	// So now only this class can update the crazyness but everyone can see it.
	//
	// Do *not* comment getters and setters, unless they do something weird (in
	// which case they're probably wrong).
	public int getCrazyness(){
		return crazyness;
	}

	// the proper JAVA way of fucking around with private mutables
	// In theory you can also make a write only (leave the getter)
	// It allows you to jam some code in before the set event.
	//
	// I don't know why it has to be so crappy, this is why I wanted to do
	// Scala.
	public void setMoonCount(final long count){
		moonCount = count;
	}
	public long getMoonCount(){
		return moonCount;
	}

	/**
	 * Simulates your mom for one physics tick.
	 *
	 */
	// note, no doTick, just tick. keep method names short and imperative.
	public void tick(){
		// defensive programming again. final as much shit as you can.
		final int expectedCrazyness = mass + crazyness;
		crazyness = expectedCrazyness;
	}
}
