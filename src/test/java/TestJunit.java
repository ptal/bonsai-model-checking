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
     Location li1 = ModelFactory.createLocation("linit_1");
     Location li2 = ModelFactory.createLocation("linit_2");
     Location lw1 = ModelFactory.createLocation("lwait_1");
     Location lw2 = ModelFactory.createLocation("lwait_2");

     inits.add(li1);
     inits.add(li2);
     inits.add(ModelFactory.createLocation("llock"));

     assertEquals(true, ts.getCurrentStates().equals(inits));
     assertTrue(ts.getCurrentStates().equals(inits));

     //** First post **//
     Set<Transition> expectedTrs = new HashSet();
     expectedTrs.add(ModelFactory.createTransition(li1, null/*if*/, null, null, lw1));
     expectedTrs.add(ModelFactory.createTransition(li2, null/*if*/, null, null, lw2));

     Set<Transition> actualTrs = ts.post();
     assertTrue(actualTrs == expectedTrs);

     System.out.println("We pass !");
   }
}
