package bonsai.examples.model;

/**
  The class atomic_p modelise the possible atomic propositions in AP, which can be :
    - A location label
    - A boolean constraint [predicat] in a variable
    - An abstract label

  -> The label are used depending on the formula and might ease the complexity of the model by abstracting some label
  It might be usefull for some problem modelisation.

  This class seams to be used just in the third case (since constraints are stored in transitions and std labels in locations)
**/

public class Atomic_p
{
  private int id;

  public Atomic_p(int id)
  {
    this.id = id;
  }

  public int getID() {return id;}

  @Override
  public String toString()
  {
    return Integer.toString(id);
  }
}
