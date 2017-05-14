package bonsai.examples.model;

import java.util.Set;
import java.util.HashSet;

import bonsai.examples.model.Action;
import bonsai.examples.model.Location;

import org.chocosolver.solver.*;
import org.chocosolver.solver.constraints.*;
import org.chocosolver.solver.variables.*;

import java.util.function.Consumer;

public class Transition implements Comparable
{
  // source : g : action : alpha : target
  public final Location source;
  public final Constraint guard;
  public final Action alpha;
  public final Consumer effect;
  public final Location target;

  // Vars_i_t concerned by transition's guards and actions
  private Set<String> Vars_i_t = new HashSet<>();

  private void addGuard(Constraint g)
  {
    if(null == g) return;
    //for guards
    for(Propagator p : g.getPropagators())
    {
      for(Variable v : p.getVars()) {Vars_i_t.add(v.getName());}
    }
  }

  // Interface -> solver
  public void fire(IModel model)
  {
    //System.out.println("***** fire fire fire : " + toString() + "*** ");
    // guards
    if(null != guard)
    {
      guard.post();
    }

    //call effect - function
    if(null == effect) return;
    effect.accept(model);
  }

  private void addEffect(Consumer e)
  {
    //for effects
    //if(null != e) Vars_i_t.add(e.getName());
  }

  public Transition(Location s, Constraint g, Action a, Consumer e, Location t)
  {
    source = s; guard = g; alpha = a; effect = e; target = t;
    addGuard(g);
    addEffect(e);
  }

  // tau transition
  public Transition(Location s, Constraint g, Consumer e, Location t)
  {
    source = s; guard = g; alpha = Action.tau; effect = e; target = t;
    addGuard(g);
    addEffect(e);
  }

  // true transition
  public Transition(Location s, Action a, Consumer e, Location t)
  {
    source = s; guard = null; alpha = a; effect = e; target = t;
    addEffect(e);
  }

  // true and tau transition
  public Transition(Location s, Consumer e, Location t)
  {
    source = s; guard = null; alpha = Action.tau; effect = e; target = t;
    addEffect(e);
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

  public Set<String> getVars()
  {
    //TODO assert(false); //"noComplete (effect variables)");
    return Vars_i_t;
  }

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
