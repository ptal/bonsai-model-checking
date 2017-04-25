package bonsai.examples.model;

import bonsai.examples.model.Atomic_p;

public class Label_p extends Atomic_p
{
  private String label;

  public Label_p(int id, String l)
  {
    super(id);
    label = l;
  }

  public String getLabel()
  {
    return label;
  }

  @Override
  public String toString()
  {
    return "l = " + label + " (" + super.toString() + ")";
  }
}
