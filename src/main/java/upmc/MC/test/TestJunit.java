package bonsai.examples.test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import bonsai.examples.model.*;

public class TestJunit {
   @Test

   public void testPeterson() {
     Transition_System ts = ModelFactory.createPetersonExample();

     //cmp loc & transition

     //** Initial states **//
     Set<Location> inits = new HashSet();
     Location li = ModelFactory.createLocation("linit");
     Location lw = ModelFactory.createLocation("lwait");

     inits.add(li);
     inits.add(li);
     inits.add(ModelFactory.createLocation("llock"));

     assertEquals(true, ts.getCurrentStates().equals(inits));

     assertTrue(ts.getCurrentStates().equals(inits));

     //** First post **//
     Set<Transition> expectedTrs = new HashSet();
     expectedTrs.add(ModelFactory.createTransition(li, null/*if*/, null, null, lw));

     Set<Transition> actualTrs = ts.post();
     assertTrue(actualTrs == expectedTrs);

     System.out.println("We pass !");
   }
}
