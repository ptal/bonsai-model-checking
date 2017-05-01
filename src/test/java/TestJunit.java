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
     // Create transitions used several times (we cannot add twice a same transition in a location)
     Transition choosed_one = ModelFactory.createTransition(li1, null/*if*/, null, null, lw1);
     Transition tiw2 = ModelFactory.createTransition(li2, null/*if*/, null, null, lw2);
     Transition tie2 = ModelFactory.createTransition(li2, null/*if*/, null, null, le2);
     expectedTrs.add(choosed_one); //to request
     expectedTrs.add(tiw2);
     expectedTrs.add(ModelFactory.createTransition(li1, null/*if*/, null, null, le1)); //to end
     expectedTrs.add(tie2);

     Set<Transition> actualTrs = ts.post();

     //test2
     //System.out.println("test2: " + actualTrs.toString() + " and " + expectedTrs.toString());
     assertTrue(actualTrs.equals(expectedTrs));

     //test3 apply
     try
     {
       actualTrs = ts.apply(choosed_one);
     } catch (NotEnabled_exp e) {assertTrue(false);}
     assertTrue(null == actualTrs);

     //test4 post again
     Location lc1 = ModelFactory.createLocation("crit_1");

     expectedTrs = new HashSet();
     expectedTrs.add(ModelFactory.createTransition(lw1, null, null, null, lc1));  //to critic
     expectedTrs.add(tiw2); //to request
     expectedTrs.add(tie2); //to end

     //System.out.println(ts.toString());
     actualTrs = ts.post();

     System.out.println("test4: " + actualTrs.toString() + " and " + expectedTrs.toString());
     assertTrue(actualTrs.equals(expectedTrs));

     System.out.println("We pass !");
   }
}
