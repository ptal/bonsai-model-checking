package bonsai.examples.test;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.Test;

public class TestsRunner {

  public static void main(String[] args)
  {
        Result result = JUnitCore.runClasses(TestJunit.class);

        for (Failure failure : result.getFailures()) {
           System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
  }

}
