package bonsai.examples.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import bonsai.examples.model.Location;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.*;

public class Transition_System
{
  //The TS has then 2 internal-states
  private enum TS_state {CREAT, RUNNING};

  // TS defined over vars and AP sets (-> static ?)
  private Set<String> Vars;
  private Set<Atomic_p> AP; //unused yet

  // Current System State (note: there may have several initial states [TODO])
  //private Set<Location> currentState;
  // Fixed point managed by SpaceTime (CSP)
  // private set<FlatLattice<Consistent>> visitedState;

  // TS defined over a set of Program_Graph (maybe more general -> Interface[TODO])
  private Set<Program_Graph> PGs;

  // Internal variables
  private TS_state internal_state = TS_state.CREAT;
  Map<InputAction, Set<Transition>> transitions_of_input; //compute the sync-transitions

  //Constructor
  public Transition_System()
  {
    //empty init
    Vars = new HashSet<>();
    //AP = new HashSet<>();
    PGs = new HashSet<>();
  }

  public void addProgram(Program_Graph pg)
  {
    assert(TS_state.CREAT == internal_state);
    PGs.add(pg);
    Vars.addAll(pg.getVars());              //addAll ?
    //AP.add(pg.getAP());                   //addAll ? [TODO]
  }

  /** ~~~~~~ ** ~~~~~~ ** ~~~~~~ Getters ~~~~~~ ** ~~~~~~ ** ~~~~~~ **/

  // State (pcs [+ vars -> in bonsai])
  public Collection<Location>  getCurrentStates()
  {
    assert(TS_state.CREAT != internal_state);
    Collection<Location> currentState = new HashSet<>();
    for(Program_Graph pg : PGs)
      {
        currentState.addAll(pg.getCurrentLocations().values());
      }
    return currentState;
  }


  /** ~~~~~~ ** ~~~~~~ ** ~~~~~~ Setters ~~~~~~ ** ~~~~~~ ** ~~~~~~ **/

  /** start runs and stops the ts creation
      The initialisation is done in post (since non-det is present) to fix initial-PG locations (when > 1)
  **/
  public void start()
  {
    assert(TS_state.CREAT == internal_state);

    for(Program_Graph pg : PGs) {pg.start();}
    internal_state = TS_state.RUNNING;
  }

  /** post computes the enabled transition for the current(s) state(s) S (in case of initial locations).
      apply should be applied to fire a transition and compute the next state S'
    [out] a set of transition which is:
          1) -> empty                   : if it is a deadlock or a final state [TODO] + exception deadlock ?
          2) -> set of transitions      : otherwise
  **/
  public Set<Transition> post()
  {
    assert(TS_state.CREAT != internal_state);

    //first post
    ArrayList<Set<Location>> sis = new ArrayList<Set<Location>>(); //in case of initial locations
    for(Program_Graph pg : PGs)
      {
        Collection<Location> loc_i = pg.getCurrentLocations().values();
        //System.out.println("+++ handle: " + toString());

        //Compute the <> SI (initial ts states)
        if(loc_i.size() > 1) //non-det ||
        {
          // || (syncr. product) [TODO] ? can be avoided if the initial locations are fixed in a first step
          ArrayList<Set<Location>> sis_n = new ArrayList<Set<Location>>();
          for(Location l_i : loc_i)
          {
            for(Set<Location> si : sis)
            {
              Set<Location> new_si = (Set<Location>) ((HashSet<Location>) si).clone();
              new_si.add(l_i);
              sis_n.add(new_si); //l_1 should not be with l_2
            }
          }
          sis = sis_n;
        } else // == 1
        {
            //System.out.println("+++ BEFORE: " + loc_i.toString());
          if(0 == sis.size()) //[TODO] up
          {
            Set<Location> si = new HashSet<>();
            si.add((Location) loc_i.toArray()[0]);
            sis.add(si);
          }
          else
          {
            for(Set<Location> si : sis) {si.add((Location) loc_i.toArray()[0]);}
          }
          //System.out.println("+++ AFTER: " + sis.toString());
        }
      }

      //now post for all SIs
      Set<Transition> out_trs = new HashSet<Transition>();
      for(Set<Location> si : sis)
      {
        out_trs.addAll(tr_post(si));
      }
    // [TODO] distinct final state and deadlocks
    return out_trs;

    // RUNNING (std post)
    //return tr_post(getCurrentStates());
  }

  /** post computes the enabled transition for the given state S.
      same interface than post except for the input set.
      [!Algo: hardpoint!]
  **/
  private Set<Transition> tr_post(Set<Location> s)
  {
    //System.out.println("Current State:" + s.toString());
    Set<Transition> resultsTr = new HashSet<>();
    // The next possible transitions are :
    //  tau transitions (without act)
    //  enabled output transitions (to)
    //  [no handled] enabled input transitions [can be compute when to is choosen] ??

    // 1) Compute all currrent outputs O [multiset] and link actions/transitions (to) [map: Act -> {tr}]
    Multiset<OutputAction> currentOutputActions = TreeMultiset.create(); //O
    Map<Location, Set<OutputAction>> output_of_location = new TreeMap<>();   //L->O'
    Map<OutputAction, Set<Transition>> transitions_of_output = new TreeMap<>(); //Act->{to}
    // sync if O_i == I_j \ I_i (for j <> i)
    // Indeed, we cannot sync on the same location

    for(Location si : s) //for all current locations
    {
      Set<OutputAction> currentOutputs = new HashSet<>(); //O'
      for(Transition t : si.outgoingTransitions().values()) //for all transitions
      {
        Action out = t.alpha;
        if      (out == Action.tau) {resultsTr.add(t);} // tau transitions
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
    transitions_of_input = new TreeMap<>(); //Act->{ti} (store in field)
    for(Location si : s)
    {
      Set<InputAction> enabledInputs = new HashSet<>();
      Multiset<OutputAction> providedOutputs = TreeMultiset.create(currentOutputActions); //O
      for(OutputAction out : output_of_location.get(si))
      {
          providedOutputs.remove(out);
      }

      //now we can sync on providedOutputs !
      for(Transition t : si.outgoingTransitions().values())
      {
        Action in = t.alpha;
        if(in instanceof InputAction && providedOutputs.contains(in)) //sync ! (TODO: check if it works)
          {
            //add the possible input to the action
            if(transitions_of_input.containsKey(in))
            {
              transitions_of_input.get(in).add(t);   // act -> ti++
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
              if(!to.source.equals(si)) {resultsTr.add(to);}
            }
          }
      }
    }
    return resultsTr;
  }

/** apply fires the transition given in input. This method compute the next state of the ts.
    Requires a previous post-call [TODO] keep?
  [in] a transition (should be enabled otherwise it fails NotEnabled_exp exception)
  [out] a set of transition which is:
        1) -> empty                   : if it was a tau-transition
        2) -> set of sync-transitions : if it was an action-transition
  [exception] NotEnabled_exp : if the input transition is not enabled at current ts-state
**/
  public Set<Transition> apply(Transition t_fired, IModel model) throws NotEnabled_exp
  {
    assert(TS_state.CREAT != internal_state);
    Transition t_pg_fired = pg_apply(t_fired, model);
    if(null == t_pg_fired) {throw new NotEnabled_exp();}

    if(! (t_pg_fired.alpha instanceof OutputAction)) return null;
    //else sync-transitions

    InputAction in = ((OutputAction) t_pg_fired.alpha).getComplement();
    Set<Transition> tis = transitions_of_input.get(in);

    //System.out.println("something here: " + tis.toString());

    if(tis.size() > 1) return tis; //non-det choice (second bonsai internal choice)
    //else no choice
    if(null == pg_apply( ((Transition) tis.toArray()[0]), model)) {throw new NotEnabled_exp();}
    return null;
  }

  //return the real fired transition (from pg)
  private Transition pg_apply(Transition t_fired, IModel model)
  {
    for(Program_Graph pg : PGs)
    {
      try
      {
        return pg.apply(t_fired, model);
      }
      catch(NotEnabled_exp e){}
    }
    return null;
  }


  @Override
  public String toString()
  {
    String out = "Transition System: {";
    for(Program_Graph pg : PGs) {out += pg.toString() + ",\n";}
    out += "}\n";
    return out;
  }

}
