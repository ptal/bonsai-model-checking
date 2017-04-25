package bonsai.examples.model;

import java.util.Set;

import bonsai.examples.model.Action;
import bonsai.examples.model.Location;
import org.chocosolver.solver.constraints.*;

public class Transition
{
  // source : g : action : alpha : target
  public final Location source;
  public final Constraint guard;
  public final Action alpha;
  public final Constraint effect; //??
  public final Location target;

  // Vars_i_t concerned by transition's guards and actions
  private Set<String> Vars_i_t;

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

  //no effect ?

  public Set<String> getVars() {return Vars_i_t;}

  @Override
  public String toString()
  {
    return source.toString() + " -> " + guard.toString() + " : " + alpha.toString() + " : " + effect.toString() + " -> " + target.toString();
  }

}
