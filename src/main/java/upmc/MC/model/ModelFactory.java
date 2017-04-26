package bonsai.examples.model;

import bonsai.examples.model.*;

import org.chocosolver.solver.constraints.*;
import org.chocosolver.solver.variables.*;
import org.chocosolver.solver.*;

import java.util.HashSet;
import java.util.Set;

public class ModelFactory {

  /* Create a Location */
  public static Location createLocation(String lab) {return new Location(lab);}

  /* Create a Transition */
  public static Transition createTransition(Location source, Constraint g, Action a, Constraint e,  Location target)
    { Transition t = new Transition(source, g, a, e, target);
      source.addTransition(t);
      return t;
    }

// Action management and creation
  static final int INPUT = 0;
  static final int OUTPUT = 1;
  public static Action[] createIOActions(String lab)
    {
      InputAction i   = new InputAction(Action.cpt, lab);
      OutputAction o  = new OutputAction(Action.cpt, lab);
      Action.incr();
      Action[] actions = new Action[2];
      actions[INPUT] = i;
      actions[OUTPUT] = o;
      return actions;
    }

/** [TODO]
//???
  public createGuard
  public createEffect
//???
**/

  public static Program_Graph createProgram(Set<Location> locations, Set<Location> inits)
  {
    Program_Graph pg = new Program_Graph();
    for(Location l : locations)
    {
      pg.addLocation(l);
    }
    for(Location li : inits)
    {
      pg.addInitLocation(li);
    }
    return pg;
  }

// return a running ts !
  public static Transition_System createSystem(Set<Program_Graph> pgs)
  {
    Transition_System ts = new Transition_System();
    for(Program_Graph pg_i : pgs)
    {
      ts.addProgram(pg_i);
    }
    ts.start();
    return ts;
  }

  /** Manage Constraints (guards and effects) **/
  /** TODO Guards use lambda expressions (not supported 1.7) -> changed into static funcs. ?

  // x [<= >= < > !=] n
  public static void createGuard_GT(Model model, IntVar v, IntVar n) // GT
  {
    return () -> model.arithm(y,">",n).post();
  }
  public static void createGuard_GE(Model model, IntVar v, IntVar n) // GE
  {
    return () -> model.arithm(y,">=",n).post();
  }
  public static void createGuard_LT(Model model, IntVar v, IntVar n)  // LT
  {
    return () -> model.arithm(y,"<",n).post();
  }
  public static void createGuard_LT(Model model, IntVar v, IntVar n)  // LE
  {
    return () -> model.arithm(y,"<=",n).post();
  }
  public static void createGuard_EG(Model model, IntVar v, IntVar n)  // EQ
  {
    return () -> model.arithm(y,"=",n).post();
  }
  public static void createGuard_DIF(Model model, IntVar v, IntVar n)  // DIF
  {
    return () -> model.arithm(y,"!=",n).post();
  }

  TODO Guards use lambda expressions (not supported 1.7) -> changed into static funcs. ? **/

  // x' = x + n
  public static IntVar createIntIncr(Model model, IntVar v, int n)
  {
    //TODO manage stream ! link v' and v
    IntVar vp = model.intOffsetView(v, n); // [TODO] CHECK ?? (need streams ?)
    return vp;
  }
  // Add intMinusView(y); (-a) intScaleView (a * b)

  public static Program_Graph createProcessModel(String postfix, Action[] lock_Act, Action[] rel_Act)
  {

    // Locations
    Location li = createLocation("linit" + "_" + postfix);
    Location lw = createLocation("lwait" + "_" + postfix);
    Location lc = createLocation("lcrit" + "_" + postfix);
    Location lr = createLocation("lrel"  + "_" + postfix);
    Location le = createLocation("lend"  + "_" + postfix);
    // Transitions Location source, Constraint g, Action a, Constraint e,  Location target
    Transition tiw = createTransition(li, /*if*/ null, Action.tau, null, lw);
    Transition twc = createTransition(lw, null, lock_Act[OUTPUT], null, lc); /* !lock */
    Transition tcr = createTransition(lc, null, Action.tau, /*x++,*/ null, lr);
    Transition tri = createTransition(lr, null, rel_Act[OUTPUT], /*i++,*/ null, li); /* !release */
    Transition tie = createTransition(li, /*if,*/ null, Action.tau, null, le);

    // build locations with transitions
    li.addTransition(tiw);li.addTransition(tie);  // li *2
    lw.addTransition(twc);
    lc.addTransition(tcr);
    lr.addTransition(tri);
    // set
    Set<Location> locations_process = new HashSet<>();
    locations_process.add(li);
    locations_process.add(lw);
    locations_process.add(lc);
    locations_process.add(lr);
    locations_process.add(le);

    Set<Location> init_process = new HashSet<>();
    init_process.add(li);

    // *********************** ****************** ***********************
    // *********************** build PG_i  ***********************
    // *********************** ****************** ***********************
    return createProgram(locations_process, init_process);
  }

  /////////// Test with the Peterson Example
  public static Transition_System createPetersonExample()
  {
    //Actions return Pair<InputAction, OutputAction>
    Action[] lock_Act = createIOActions("lock");
    Action[] rel_Act = createIOActions("rel");

    // Process Model
    // *********************** ****************** ***********************
    // *********************** build PG_1 and PG_2 ***********************
    // *********************** ****************** ***********************
    Program_Graph process1 = createProcessModel("1", lock_Act, rel_Act);
    Program_Graph process2 = createProcessModel("2", lock_Act, rel_Act);

    // lock Model
    // Locations
    Location lu = createLocation("lunlock");
    Location ll = createLocation("llock");
    // Transitions Location source, Constraint g, Action a, Constraint e,  Location target
    Transition tul = createTransition(lu, null, lock_Act[INPUT], null, ll); /* ?lock */
    Transition tlu = createTransition(ll, null, rel_Act[INPUT], null, lu); /* ?release */
    // build locations with transitions
    lu.addTransition(tul);
    ll.addTransition(tlu);
    // set
    Set<Location> locations_lock = new HashSet<>();
    locations_lock.add(lu);
    locations_lock.add(ll);

    Set<Location> init_lock = new HashSet<>(); /* init  -- lock? --> */
    init_lock.add(lu);
    // *********************** ****************** ***********************
    // *********************** build PG_1 and PG_2 ***********************
    // *********************** ****************** ***********************
    Program_Graph lock = createProgram(locations_lock, init_lock);

    // *********************** ****************** ***********************
    // *********************** Finally the TS ! ***********************
    // *********************** ****************** ***********************
    Set<Program_Graph> pgs = new HashSet<>();
    pgs.add(process1);
    pgs.add(process2);
    pgs.add(lock);

    Transition_System ts = createSystem(pgs);
    ts.start();

    return ts;
  }
}
