package bonsai.examples.model;

import java.util.ArrayList;
import java.util.Set;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import bonsai.examples.model.Location;
import org.chocosolver.solver.search.strategy.selectors.variables.*;

public class Transition_System
{
  // TS defined over vars and AP sets (-> static ?)
  private Set<String> Vars;
  private Set<Atomic_p> AP; //unused yet

  // Current System State (note: there may have several initial states [TODO])
  private Set<Location> currentState; // State (pcs [+ vars -> in bonsai])

  // Fixed point managed by SpaceTime (CSP)
  // private set<FlatLattice<Consistent>> visitedState;

  // TS defined over a set of Program_Graph (maybe more general -> Interface[TODO])
  private Set<Program_Graph> PGs;

  // Internal variables
  private boolean is_running;

  //Constructor
  public Transition_System()
  {
    //empty init
    Vars = new HashSet<>();
    //AP = new HashSet<>();
    currentState = new HashSet<>();
    PGs = new HashSet<>();

    is_running = false;
  }

  public void addProgram(Program_Graph pg)
  {
    assert(!is_running);
    PGs.add(pg);
    Vars.addAll(pg.getVars());              //addAll ?
    //AP.add(pg.getAP());                   //addAll ? [TODO]
  }

  public void start()
  {
    assert(!is_running);

    for(Program_Graph pg : PGs)
    {
      // [Fixed] First post() returns the outgoing transition from all (I)
      currentState.addAll(pg.getInitLocations());
    }
    is_running = true;
  }

  public Set<Location>  getCurrentStates()
  {
    assert(is_running);
    return currentState;
  }

  public Set<Transition> post()
  {
    assert(is_running);

    System.out.println("Current State:" + currentState.toString());
    Set<Transition> resultsTr = new HashSet<>();
    // The next possible transitions are :
    //  tau transitions (without act)
    //  enabled output transitions (to)
    //  [no handled] enabled input transitions [can be compute when to is choosen] ??
    Set<Transition> progNextStates = new HashSet<>();

    // 1) Compute all currrent outputs O [multiset] and link actions/transitions (to) [map: Act -> {tr}]
    Multiset<OutputAction> currentOutputActions = TreeMultiset.create(); //O
    Map<Location, Set<OutputAction>> output_of_location = new TreeMap<>();   //L->O'
    Map<OutputAction, Set<Transition>> transitions_of_output = new TreeMap<>(); //Act->{to}
    // sync if O_i == I_j \ I_i (for j <> i)
    // Indeed, we cannot sync on the same location

    for(Location si : currentState) //for all current locations
    {
      Set<OutputAction> currentOutputs = new HashSet<>(); //O'
      for(Transition t : si.outgoingTransitions()) //for all transitions
      {
        Action out = t.alpha;
        if      (out == Action.tau) {progNextStates.add(t);} // tau transitions
        else if (out instanceof OutputAction)
          {
            OutputAction o = (OutputAction) out;
            currentOutputs.add(o); // O'
            if(transitions_of_output.containsKey(o))
            {
              transitions_of_output.get(o).add(t);   // act -> to++
            }
            else
            {
              HashSet<Transition> newt = new HashSet<Transition>();
              newt.add(t);
              transitions_of_output.put(o, newt);   // act -> to
            }
          }
      }
      //O'
      // note that outputs maybe disabled if no corr. input fired
      output_of_location.put(si, currentOutputs);
      // Store all outputs with redondance
      currentOutputActions.addAll(currentOutputs); //O
    }

    // Inputs
    Map<InputAction, Set<Transition>> transitions_of_input = new TreeMap<>(); //Act->{ti} (store in field)
    for(Location si : currentState)
    {
      Set<InputAction> enabledInputs = new HashSet<>();
      Multiset<OutputAction> providedOutputs = TreeMultiset.create(currentOutputActions); //O
      for(OutputAction out : output_of_location.get(si))
      {
          providedOutputs.remove(out);
      }

      //now we can sync on providedOutputs !
      for(Transition t : si.outgoingTransitions())
      {
        Action in = t.alpha;
        if(in instanceof InputAction && providedOutputs.contains(in)) //sync ! (TODO: check if it works)
          {
            //add the possible input to the action
            if(transitions_of_input.containsKey(in))
            {
              transitions_of_input.get(in).add(t);   // act -> to++
            }
            else
            {
              HashSet<Transition> newt = new HashSet<Transition>();
              newt.add(t);
              transitions_of_input.put((InputAction) in, newt);   // act -> ti
            }

            //add the provided outputs (if not from si)
            for(Transition to : transitions_of_output.get(in))  //complementary output action ?
            {
              if(to.source != si)
                {resultsTr.add(to);}
            }
          }
      }
    }
  }
}
