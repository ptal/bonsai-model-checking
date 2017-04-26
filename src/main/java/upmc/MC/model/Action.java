package bonsai.examples.model;

class Action implements Comparable
{
  public final String name;
  public final int id;

  //special action
  static Action tau = new Action(0, "tau");

  //counter id
  static int cpt = 1; // 0 -> tau
  static void incr() {cpt++;}
  //counter id

  public Action(int i, String n)
  {
    id = i;
    name = n;
  }

  // For MultiSet
  @Override public int compareTo(Object b)
  {
    if(this.equals(b)) return 0;
    if(b instanceof Action)
    {
      return this.id > ((Action) b).id ? 1 : -1;
    }
    throw(new ClassCastException());
  }

  // For HashMaps
  @Override public boolean equals(Object o)
  {
    return (o instanceof Action) &&
           (this.id == ((Action) o).id);
  }

  public int hashCode()
  {
    return id;
  }


  @Override
  public String toString()
  {
    return "name";
  }
}
