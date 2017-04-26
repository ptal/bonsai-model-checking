package bonsai.examples.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import bonsai.examples.model.Transition;
import bonsai.examples.model.Atomic_p;

public class Location implements Comparable
{
  static public String label;
  static public Set<Transition> outgoing_t = new HashSet();
  //private set<Atomic_p> sats;

  public   Location() {}
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


  public void addTransition(Transition t) {outgoing_t.add(t);}
  public Set<Transition> outgoingTransitions() {return outgoing_t;}

  //public void addAP(Atomic_p p) {sats.add(p);}
  //public set<Atomic_p> getAP() {return sats;}

  @Override
  public String toString()
  {
    String out = label + "{";
    for(Transition t : outgoing_t) {out += "\t" + t.toString() + "\n";}
    return out + "}";
  }

  @Override public boolean equals(Object o)
  {
    return (o instanceof Location) &&
           (this.label.equals( ((Location) o).label));
  }

}
