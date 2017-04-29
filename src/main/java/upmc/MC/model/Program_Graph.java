package bonsai.examples.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import bonsai.examples.model.Location;
import org.chocosolver.solver.search.strategy.selectors.variables.*;

public class  Program_Graph
{
  // PG defined over vars_i and AP_i sets (-> static ?)
  private Set<String> localVars;
  //private set<Atomic_p> AP_i;

  // PG defined over IAct_i (input) and OAct_i (output)
  //private set<InputAction>  IAct_i;
  //private set<OutputAction> OAct_i;

  // Current PG locations, initial locations and pc (note: there may have several initial Locations [TODO])
  private Set<Location> pg_locations;           //[TOKEEP?] used to check init-locations (Loc_i)
  private Set<Location> initialLocations;       // (I_i)
  private Location current_l = null;            // 'pc_i'

  // Internal variables (prevent from modi. PG_i after inserting in TS)
  private boolean is_in_TS;

  public Program_Graph()
  {
    localVars = new HashSet<>();
    //AP_i = new HashSet<>();
    pg_locations = new HashSet<>();
    initialLocations = new HashSet<>();
    current_l = null;

    //set<InputAction>  IAct_i = new HashSet<>();;
    //set<OutputAction> OAct_i = new HashSet<>();;

    is_in_TS = false;
  }

  public void addLocation(Location source)
  {
    assert(!is_in_TS);

    pg_locations.add(source);
    for(Transition t : source.outgoingTransitions()) ///needed ? to check ?
    {
        pg_locations.add(t.target);

        Set<String> vs = t.getVars();
        if(vs.size() > 0) localVars.addAll(vs);
    }
  }

// i should be in PG_i
  public void addInitLocation(Location i)
  {
    assert(!is_in_TS);
    assert(pg_locations.contains(i));

    initialLocations.add(i);
  }

  /** Getters **/
  //returns the initial location(s) Loc_i
  public Set<Location> getInitLocations()    {assert(is_in_TS); return initialLocations;}

  //to ease the ts, we merge the initial or std getters.
  //Hence it returns a set if the init. is not fixed yet (with apply)
  public Set<Location> getCurrentLocations()
  {
    assert(is_in_TS);
    if(null==current_l) {return initialLocations;}
    else
    {
      Set<Location> l = new HashSet<>();
      l.add(current_l);
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
  public Location apply(Transition t) throws NotEnabled_exp
  {
    assert(is_in_TS);

    if(null == current_l)
    { //FIX INIT
      if(!initialLocations.contains(t.source))
      {throw new NotEnabled_exp();}
    } else
    {
      if(current_l != t.source)
      {
        {throw new NotEnabled_exp();}
      }
    }

    current_l = t.target;
    return current_l;
  }

  @Override
  public String toString()
  {
    String out = "Initial Locations: {";
    for(Location s : initialLocations) {out += s.label + " ";}
    out += "}\n Locations : {\n";
    for(Location s : pg_locations) {out += "\t" + s.toString() + "\n";}
    out += "}\n localVars : {";
    for(String v : localVars)
    {
      out += v + ", ";
    }
    out += "}\n";
    return out;
  }
}
