package bonsai.examples.model;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.*;

import java.util.ArrayList;

public class StreamVariable {

  private int counter;
  private String name;
  private ArrayList<IntVar> instances; //avoid ? (manage domains with stream only ?)

  public StreamVariable(IModel model, String n, int init_val)
  {
    name      = n;
    counter   = 1;
    instances = new ArrayList<IntVar>();

    //init
    instances.add(model.intVar(name + "$0", init_val));
  }

  public int getCurrentIndex(){return counter;}
  public IntVar IncrIndex(IModel model, int init)
  {
    //[Interface solver] ++ addVariable
    IntVar v_i = model.intVar(name + "$" + Integer.toString(getCurrentIndex()), init);
    instances.add(v_i);
    counter++;

    return v_i;
  }

  public String getName() {return name;}
  public IntVar getInstance(int i) {assert(i < counter);return instances.get(i);}

}
