package bonsai.examples;

import bonsai.examples.model.*;
import org.chocosolver.solver.*;

public class Main
{
  public static void main(String[] argv)
  {
    IModel model = new Model("PertersonProblem");
    Transition_System ts = ModelFactory.createPetersonExample(model);
    System.out.println("Transition System created");
  }
}
