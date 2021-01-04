package org.scijava.command;

import org.junit.Test;
import org.scijava.Context;
import org.scijava.ItemIO;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class CommandModuleListArrayGenericsTest {

    // No issue, this works
    @Test
    public void testCommandArrayParameterSpecified() throws InterruptedException,
            ExecutionException
    {
        final Context context = new Context(CommandService.class);
        final CommandService commandService = context.service(CommandService.class);
        final ObjectService objectService = context.service(ObjectService.class);

        InputsTest.UserClass[] userObjects = new InputsTest.UserClass[2];
        userObjects[0] = new InputsTest.UserClass("User Object 0");
        userObjects[1] = new InputsTest.UserClass("User Object 1");
        objectService.addObject(userObjects);

        final CommandModule module = //
                commandService.run(CommandWithUserClassArray.class, true, "userObjects", userObjects  ).get(); //
        assertEquals("User Object 0;User Object 1;", module.getOutput("result"));
    }

    // This fails :
    //  a single object of the proper type is put into the object service
    //  however an object of class UserClass[] is passed to the command,
    //  but it's not the one expected
    @Test
    public void testCommandArrayParameterInjection() throws InterruptedException,
            ExecutionException
    {
        final Context context = new Context(CommandService.class);
        final CommandService commandService = context.service(CommandService.class);
        final ObjectService objectService = context.service(ObjectService.class);

        InputsTest.UserClass[] userObjects = new InputsTest.UserClass[2];
        userObjects[0] = new InputsTest.UserClass("User Object 0");
        userObjects[1] = new InputsTest.UserClass("User Object 1");
        objectService.addObject(userObjects);

        Future<CommandModule> fcm = commandService.run(CommandWithUserClassArray.class, true );

        final CommandModule module = fcm.get(); //

        assertEquals("User Object 0;User Object 1;", module.getOutput("result"));
    }

    /** A command which uses a UserClass Array parameter. */
    @Plugin(type = Command.class)
    public static class CommandWithUserClassArray implements Command {

        @Parameter
        private InputsTest.UserClass[] userObjects;

        @Parameter(type = ItemIO.OUTPUT)
        private String result = "default";

        @Override
        public void run() {
            final StringBuilder sb = new StringBuilder();
            System.out.println("userObjects length : "+userObjects.length);
            System.out.println("class : "+userObjects.getClass());
            for (InputsTest.UserClass obj : userObjects) {
                System.out.println("\t"+obj);
                sb.append(obj.toString()+";");
            }
            result = sb.toString();
        }
    }



}
