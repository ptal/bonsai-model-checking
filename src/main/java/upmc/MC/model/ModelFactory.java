package bonsai.examples.model;

import bonsai.examples.model.*;

import org.chocosolver.solver.constraints.*;
import org.chocosolver.solver.variables.*;
import org.chocosolver.solver.*;

import java.util.HashSet;
import java.util.Set;

import java.util.function.Consumer;

public class ModelFactory {

  /* Create a Location */
  public static Location createLocation(String lab) {return new Location(lab);}

  /* Create a Transition */
  public static Transition createTransition(Location source, Constraint g, Action a, Consumer e, Location target)
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
      i.setComplement(o);
      o.setComplement(i);
      Action.incr();
      Action[] actions = new Action[2];
      actions[INPUT] = i;
      actions[OUTPUT] = o;
      return actions;
    }

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
  // x [<= >= < > !=] n
  public static Constraint createGuard_GT(IModel model, IntVar v, IntVar n) // GT
  {
    return model.arithm(v,">",n);
  }
  public static Constraint createGuard_GE(IModel model, IntVar v, IntVar n) // GE
  {
    return model.arithm(v,">=",n);
  }
  public static Constraint createGuard_LT(IModel model, IntVar v, IntVar n)  // LT
  {
    return model.arithm(v,"<",n);
  }
  public static Constraint createGuard_LE(IModel model, IntVar v, IntVar n)  // LE
  {
    return model.arithm(v,"<=",n);
  }
  public static Constraint createGuard_EG(IModel model, IntVar v, IntVar n)  // EQ
  {
    return model.arithm(v,"=",n);
  }
  public static Constraint createGuard_DIF(IModel model, IntVar v, IntVar n)  // DIF
  {
    return model.arithm(v,"!=",n);
  }

  // x' = x + n
  public static Consumer<IModel> createIntIncr(StreamVariable i, int n)
  {
    //TODO manage stream ! link v' and v
    return (IModel model) ->
      {
        //System.out.println("I get index " + Integer.toString(i.getCurrentIndex()-1));

        //choco x_i+1
        IntVar i_i = i.getInstance(i.getCurrentIndex()-1);              // Current i
        IntVar i_p = i.IncrIndex(model, 0);                             // New i'
        model.arithm(i_p, "=", model.intOffsetView(i_i, n)).post();
      };
  }
  // Add intMinusView(y); (-a) intScaleView (a * b)

  public static Program_Graph createProcessModel(String postfix, Action[] lock_Act, Action[] rel_Act, IModel model, StreamVariable x)
  {
    //local Variables -->> new variable
    StreamVariable i = new StreamVariable(model, "i" + "_" + postfix, 0);
    IntVar i_var = i.getInstance(0);

    // Locations
    Location li = createLocation("init" + "_" + postfix);
    Location lw = createLocation("request" + "_" + postfix);
    Location lc = createLocation("crit" + "_" + postfix);
    Location lr = createLocation("release"  + "_" + postfix);
    Location le = createLocation("end"  + "_" + postfix);
    // Transitions Location source, Constraint g, Action a, Constraint e,  Location target
    Transition tiw = createTransition(li, createGuard_GE(model, i_var, N), Action.tau, null, lw);
    Transition twc = createTransition(lw, null, lock_Act[OUTPUT], null, lc); /* !lock */
    Transition tcr = createTransition(lc, null, Action.tau,       createIntIncr(x, 1), lr);
    Transition tri = createTransition(lr, null, rel_Act[OUTPUT],  createIntIncr(i, 1), li); /* !release */
    Transition tie = createTransition(li, createGuard_GE(model, i_var, N), Action.tau, null, le);

    //System.out.println("~~~~test: " + tiw.target.toString());

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
  static IntVar N = null;
  public static Transition_System createPetersonExample(IModel model)
  {
    //Global variable
    StreamVariable x = new StreamVariable(model, "x", 0);
    N = model.intVar("N", 10);

    //Actions return Pair<InputAction, OutputAction>
    Action[] lock_Act = createIOActions("req");
    Action[] rel_Act = createIOActions("rel");

    // Process Model
    // *********************** ****************** ***********************
    // *********************** build PG_1 and PG_2 ***********************
    // *********************** ****************** ***********************
    Program_Graph process1 = createProcessModel("1", lock_Act, rel_Act, model, x);
    Program_Graph process2 = createProcessModel("2", lock_Act, rel_Act, model, x);

    // lock Model
    // Locations
    Location lu = createLocation("unlock");
    Location ll = createLocation("lock");
    // Transitions Location source, Constraint g, Action a, Constraint e,  Location target
    Transition tul = createTransition(lu, null, lock_Act[INPUT], null, ll); /* ?lock */
    Transition tlu = createTransition(ll, null, rel_Act[INPUT], null, lu); /* ?release */
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

    //start is done in the factory ... keep ?
    return createSystem(pgs);
  }
}
