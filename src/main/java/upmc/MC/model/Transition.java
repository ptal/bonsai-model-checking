package bonsai.examples.model;

import java.util.Set;
import java.util.HashSet;

import bonsai.examples.model.Action;
import bonsai.examples.model.Location;
import org.chocosolver.solver.constraints.*;

public class Transition implements Comparable
{
  // source : g : action : alpha : target
  public final Location source;
  public final Constraint guard;
  public final Action alpha;
  public final Constraint effect; //??
  public final Location target;

  // Vars_i_t concerned by transition's guards and actions
  private Set<String> Vars_i_t = new HashSet<>();

  public Transition(Location s, Constraint g, Action a, Constraint e, Location t)
  {
    source = s; guard = g; alpha = a; effect = e; target = t;
    //Vars_i_t.add(g.);
    //Vars_i_t.add(e.);
  }

  // tau transition
  public Transition(Location s, Constraint g, Constraint e, Location t)
  {
    source = s; guard = g; alpha = Action.tau; effect = e; target = t;
    //Vars_i_t.add(g.);
    //Vars_i_t.add(e.);
  }

  // true transition
  public Transition(Location s, Action a, Constraint e, Location t)
  {
    source = s; guard = null; alpha = a; effect = e; target = t;
    //Vars_i_t.add(e.);
  }

  // true and tau transition
  public Transition(Location s, Constraint e, Location t)
  {
    source = s; guard = null; alpha = Action.tau; effect = e; target = t;
    //Vars_i_t.add(e.);
  }


  // A transition is compared with its source/target locations [TODO] check.
  // It implies no two transitions with same source/target ~~
  @Override public int compareTo(Object b)
  {
    if(this.equals(b)) return 0;
    if(b instanceof Transition)
    {
      int s = this.source.compareTo(((Transition) b).source);
      if(0 == s) return this.target.compareTo(((Transition) b).target);
      return s;
    }
    throw(new ClassCastException());
  }

  @Override public boolean equals(Object o)
  {
    return (o instanceof Transition) &&
           (this.source.equals( ((Transition) o).source)) &&
           (this.target.equals( ((Transition) o).target));
  }

  public int hashCode()
  {
    return source.hashCode() + target.hashCode();
  }

  //no effect ?

  public Set<String> getVars() {return Vars_i_t;}

  @Override
  public String toString()
  {
    return source.label + " -> " +
           (null == guard ? "true " : guard.toString()) + " : " +
           (null == alpha ? "tau " : alpha.toString()) + " : " +
           (null == effect ? "eps " : effect.toString()) +
           " -> " + target.label;
  }

}
