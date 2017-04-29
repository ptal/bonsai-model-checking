package bonsai.examples.model;

class InputAction extends Action
{
  OutputAction complement;

  public InputAction(int id, String n)
  {
    super(id, n);
  }

  public void setComplement(OutputAction in) {complement = in;}
  public OutputAction getComplement() {return complement;}

  @Override
  public String toString()
  {
    return "?" + super.toString();
  }
}
