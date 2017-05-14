package bonsai.examples.test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import bonsai.examples.model.*;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.*;
import org.chocosolver.solver.constraints.*;

import java.util.function.Consumer;

public class TestJunit {
   @Test

   public void testPeterson() {
     //build the model
     IModel model = new Model("PertersonProblem");
     Transition_System ts = ModelFactory.createPetersonExample(model);
     //caution !
     //System.out.println("****** start ******");
     //printModel((Model) model);

     //cmp loc & transition

     //** Initial states **//
     Set<Location> inits = new HashSet();
     Location li1 = ModelFactory.createLocation("init_1");
     Location li2 = ModelFactory.createLocation("init_2");
     Location lw1 = ModelFactory.createLocation("request_1");
     Location lw2 = ModelFactory.createLocation("request_2");
     Location le1 = ModelFactory.createLocation("end_1");
     Location le2 = ModelFactory.createLocation("end_2");

     Location lunlock = ModelFactory.createLocation("unlock");
     Location llock = ModelFactory.createLocation("lock");
     inits.add(li1);
     inits.add(li2);
     inits.add(lunlock);

     //assertEquals(true, ts.getCurrentStates().equals(inits)); same
     assertTrue(ts.getCurrentStates().equals(inits));
     assertTrue(0 == ((Model) model).getNbCstrs()); //test constraints (from guard)

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
     //System.out.println(ts.toString());


     //test3 apply
     try
     {
       actualTrs = ts.apply(choosed_one, model);
     } catch (NotEnabled_exp e) {assertTrue(false);}
     assertTrue(null == actualTrs); //test locations
     assertTrue(1 == ((Model) model).getNbCstrs()); //test constraints (from guard)


     //System.out.println("****** first apply ******");
     //printModel((Model) model);

     //test4 post again
     Location lc1 = ModelFactory.createLocation("crit_1");

     expectedTrs = new HashSet();
     Transition choosed_two = ModelFactory.createTransition(lw1, null, null, null, lc1);
     expectedTrs.add(choosed_two);  //to critic
     expectedTrs.add(tiw2); //to request
     expectedTrs.add(tie2); //to end

     //System.out.println(ts.toString());
     actualTrs = ts.post();

     //System.out.println("test4: " + actualTrs.toString() + " and " + expectedTrs.toString());
     assertTrue(actualTrs.equals(expectedTrs));

     //test 5
     //apply a sync-transition (should return the set of complementary tr.)
     try
     {
       actualTrs = ts.apply(choosed_two, model);
     } catch (NotEnabled_exp e) {assertTrue(false);}
     assert(null == actualTrs); //it is null since just one solution is possible (sync-tr)

     //test 6 locations
     Set<Location> expectedLocations = new HashSet();
     expectedLocations.add(li2);   //init_2
     expectedLocations.add(lc1);   //critic_1
     expectedLocations.add(llock); //lock

     assertTrue(ts.getCurrentStates().equals(expectedLocations));


     //test 7 effect (x++)
     Location lrel1 = ModelFactory.createLocation("release_1");

     expectedTrs = new HashSet();
     Transition choosed_three = ModelFactory.createTransition(lc1, null, null, null, lrel1);
     expectedTrs.add(choosed_three);  //to incr
     expectedTrs.add(tiw2);           //to request
     expectedTrs.add(tie2);           //to end

     //System.out.println(ts.toString());
     actualTrs = ts.post();

     //System.out.println("test4: " + actualTrs.toString() + " and " + expectedTrs.toString());
     assertTrue(actualTrs.equals(expectedTrs));

     // test 8 apply
     try
     {
       actualTrs = ts.apply(choosed_three, model);
     } catch (NotEnabled_exp e) {assertTrue(false);}
     assert(null == actualTrs); //it is null (tau-tr)
     assertTrue(2 == ((Model) model).getNbCstrs()); //test constraints (from guard)

     //test 9 locations
     expectedLocations = new HashSet();
     expectedLocations.add(li2);      //init_2
     expectedLocations.add(lrel1);    //release_1
     expectedLocations.add(llock);    //lock

     assertTrue(ts.getCurrentStates().equals(expectedLocations));

     System.out.println("****** end ******");
     printModel((Model) model);

     System.out.println("We pass !");
   }

   /**
     Auxiliary debug functions
   **/
   public static void printModel(Model model)
   {
     //constraints
     System.out.println("~~~ ~~~ constraints");
     Consumer<Model> CtrPrint = (Model m) -> {
       System.out.print(m.getNbCstrs()+ ": ");
       for(Constraint c : m.getCstrs()) {System.out.print(c.toString() + " ");}
       System.out.print("\n");
     };
     CtrPrint.accept(model);

     //variables
     System.out.println("~~~ ~~~ vars");
     Consumer<Model> VarPrint = (Model m) -> {
       System.out.print(m.getNbVars()+ ": ");
       for(Variable v : m.getVars()) {System.out.print(v.getName() + " ");}
       System.out.print("\n");
     };
     VarPrint.accept(model);
   }
}
