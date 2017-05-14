package bonsai.examples.model;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.*;

import java.util.ArrayList;

public class StreamVariable {

  private int counter;
  private String name;
  private ArrayList<IntVar> instances; //avoid ? (manage domains with stream only ?)

  public StreamVariable(String n)
  {
    name      = n;
    counter   = 0;
    instances = new ArrayList<IntVar>();
  }

  public int getCurrentIndex(){return counter;}
  public IntVar IncrIndex(IModel model, int init)
  {
    //[Interface solver] ++ addVariable
    IntVar v_i = model.intVar(name + "_" + Integer.toString(getCurrentIndex()), init);
    instances.add(v_i);
    counter++;

    return v_i;
  }

  public String getName() {return name;}
  public IntVar getInstance(int i) {assert(i < counter);return instances.get(i);}

}
