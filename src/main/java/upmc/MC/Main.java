package bonsai.examples;

import bonsai.examples.model.*;

public class Main
{
  public static void main(String[] argv)
  {
    Transition_System ts = ModelFactory.createPetersonExample();
    System.out.println("Transition System created");
  }
}
