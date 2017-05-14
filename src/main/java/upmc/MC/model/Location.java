package bonsai.examples.model;

import java.util.HashMap;
import java.util.Iterator;

import bonsai.examples.model.Transition;
import bonsai.examples.model.Atomic_p;
import org.chocosolver.solver.*;

public class Location implements Comparable
{
  final public String label;
  // target:loc -> transition
  final private HashMap<Integer, Transition> outgoing_t = new HashMap();
  //private set<Atomic_p> sats;

  public   Location(String l) {label = l;}

  // For TreeMultiSet
  // Comparison on the label (add id ?) [should be unique over the PGs]
  @Override public int compareTo(Object b)
  {
    if(this.equals(b)) return 0;
    if(b instanceof Location)
    {
      return this.label.compareTo(((Location) b).label);
    }
    throw(new ClassCastException());
  }

  @Override public boolean equals(Object o)
  {
    return (o instanceof Location) &&
           (this.label.equals( ((Location) o).label));
  }

  public int hashCode()
  {
    return label.hashCode();
  }


  public void addTransition(Transition t)
  {
    //System.out.println("----- add: " + t.target);
    assert(!outgoing_t.containsKey(t.target.hashCode()));
    outgoing_t.put(t.target.hashCode(), t);
  }
  public HashMap<Integer, Transition> outgoingTransitions() {return outgoing_t;}

  //public void addAP(Atomic_p p) {sats.add(p);}
  //public set<Atomic_p> getAP() {return sats;}

  //return the fired location
  public Transition fire(Transition t, IModel model) throws NotEnabled_exp
  {
    Transition tgt = outgoing_t.get(t.target.hashCode());
    if(null == tgt) {throw new NotEnabled_exp();}
    tgt.fire(model);
    return tgt;
  }

  @Override
  public String toString()
  {
    String out = label + "{\n";
    for(Transition t : outgoing_t.values()) {out += "\t\t" + t.toString() + "\n";}
    return out + "}";
  }

}
