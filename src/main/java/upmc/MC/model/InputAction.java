package bonsai.examples.model;

class InputAction extends Action
{

  public InputAction(int id, String n)
  {
    super(id, n);
  }

  @Override
  public String toString()
  {
    return "?" + super.toString();
  }
}
