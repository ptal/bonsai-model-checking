package bonsai.examples.model;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import bonsai.examples.model.Location;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.*;

public class  Program_Graph
{
  // PG defined over vars_i and AP_i sets (-> static ?)
  private Set<String> localVars;
  //private set<Atomic_p> AP_i;

  // PG defined over IAct_i (input) and OAct_i (output)
  //private set<InputAction>  IAct_i;
  //private set<OutputAction> OAct_i;

  // Current PG locations, initial locations and pc (note: there may have several initial Locations [TODO])
  private HashMap<Integer,Location> pg_locations;           //[TOKEEP?] used to check init-locations (Loc_i)
  private HashMap<Integer,Location> initialLocations;       // (I_i)
  private Location current_l = null;            // 'pc_i'

  // Internal variables (prevent from modi. PG_i after inserting in TS)
  private boolean is_in_TS;

  public Program_Graph()
  {
    localVars = new HashSet<>();
    //AP_i = new HashSet<>();
    pg_locations = new HashMap<>();
    initialLocations = new HashMap<>();
    current_l = null;

    //set<InputAction>  IAct_i = new HashSet<>();;
    //set<OutputAction> OAct_i = new HashSet<>();;

    is_in_TS = false;
  }

  public void addLocation(Location source)
  {
    assert(!is_in_TS);
    //System.out.println("----- add: " + source + "-> " + source.hashCode());

    // assert may raise cause a location has been already added as target location (the result is the same)
    // add exception in case ?
    // assert(!pg_locations.containsKey(source.hashCode()));
    //add source location
    if(!pg_locations.containsKey(source.hashCode()))  {pg_locations.put(source.hashCode(), source);}

    for(Transition t : source.outgoingTransitions().values()) ///needed ? to check ?
    {
      //add target locations [from the source transitions]
      if(!pg_locations.containsKey(t.target.hashCode()))  {pg_locations.put(t.target.hashCode(), t.target);}

        Set<String> vs = t.getVars();
        if(vs.size() > 0) localVars.addAll(vs);
    }
  }

// i should be in PG_i
  public void addInitLocation(Location i)
  {
    assert(!is_in_TS);
    assert(pg_locations.containsKey(i.hashCode()));

    initialLocations.put(i.hashCode(),i);
  }

  /** Getters **/
  //returns the initial location(s) Loc_i
  public HashMap<Integer,Location> getInitLocations()    {assert(is_in_TS); return initialLocations;}

  //to ease the ts, we merge the initial or std getters.
  //Hence it returns a set if the init. is not fixed yet (with apply)
  public HashMap<Integer,Location> getCurrentLocations()
  {
    assert(is_in_TS);

    if(null==current_l) {return initialLocations;}
    else
    {
      HashMap l = new HashMap<Integer,Location>();
      l.put(current_l.hashCode(), current_l);
      return l;
    }
  }
  //returns the variable that managed by the PG (in guards or effects)
  //should not be considered as local scope but local used variables
  public Set<String> getVars()               {return localVars;}

// Launch the Program Graph
  public void start()
  {
    assert(!is_in_TS);
    assert(null == current_l);
    is_in_TS = true;
  }

// Apply one of the transition from the current location
// retreive the pg's transition and apply it (prevent from bad transition in input)
  public Transition apply(Transition t, IModel model) throws NotEnabled_exp
  {
    assert(is_in_TS);

    Location to_fired = null;
    if(null == current_l)
    { //FIX INIT
      to_fired = initialLocations.get(t.source.hashCode());
      if(null == to_fired) {throw new NotEnabled_exp();}
    } else
    {
      if(!current_l.equals(t.source))
      {
        {throw new NotEnabled_exp();}
      }
      to_fired = current_l;
    }

    Transition fired = to_fired.fire(t, model);
    current_l = fired.target;
    return fired;
  }

  @Override
  public String toString()
  {
    String out = "Initial Locations: {";
    for(Location s : initialLocations.values()) {out += s.label + " ";}
    out += "}\n Locations : {\n";
    for(Location s : pg_locations.values()) {out += "\t" + s.toString() + "\n";}
    out += "}\n localVars : {";
    for(String v : localVars)
    {
      out += v + ", ";
    }
    out += "}\n";
    return out;
  }
}
