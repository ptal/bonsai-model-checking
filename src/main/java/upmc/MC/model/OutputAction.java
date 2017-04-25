package bonsai.examples.model;

class OutputAction extends Action
{

  public OutputAction(int id, String n)
  {
    super(id, n);
  }

  @Override
  public String toString()
  {
    return "?" + super.toString();
  }
}
