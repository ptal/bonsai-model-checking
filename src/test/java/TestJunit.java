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
     //caution !
     //System.out.println(ts.toString());

     //cmp loc & transition

     //** Initial states **//
     Set<Location> inits = new HashSet();
     Location li1 = ModelFactory.createLocation("init_1");
     Location li2 = ModelFactory.createLocation("init_2");
     Location lw1 = ModelFactory.createLocation("request_1");
     Location lw2 = ModelFactory.createLocation("request_2");
     Location le1 = ModelFactory.createLocation("end_1");
     Location le2 = ModelFactory.createLocation("end_2");

     inits.add(li1);
     inits.add(li2);
     inits.add(ModelFactory.createLocation("unlock"));

     //assertEquals(true, ts.getCurrentStates().equals(inits)); same
     assertTrue(ts.getCurrentStates().equals(inits));

     //** First post **//
     Set<Transition> expectedTrs = new HashSet();
     expectedTrs.add(ModelFactory.createTransition(li1, null/*if*/, null, null, lw1)); //to request
     expectedTrs.add(ModelFactory.createTransition(li2, null/*if*/, null, null, lw2));
     expectedTrs.add(ModelFactory.createTransition(li1, null/*if*/, null, null, le1)); //to end
     expectedTrs.add(ModelFactory.createTransition(li2, null/*if*/, null, null, le2));

     Set<Transition> actualTrs = ts.post();

     //test2
     System.out.println("test2: " + actualTrs.toString() + " and " + expectedTrs.toString());
     assertTrue(actualTrs.equals(expectedTrs));

     System.out.println("We pass !");
   }
}
