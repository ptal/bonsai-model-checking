package bonsai.examples.model;

class OutputAction extends Action
{
  InputAction complement;

  public OutputAction(int id, String n)
  {
    super(id, n);
  }

  public void setComplement(InputAction in) {complement = in;}
  public InputAction getComplement() {return complement;}

  @Override
  public String toString()
  {
    return "!" + super.toString();
  }
}
