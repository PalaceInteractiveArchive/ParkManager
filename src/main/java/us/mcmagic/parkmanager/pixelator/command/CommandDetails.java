package us.mcmagic.parkmanager.pixelator.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDetails {

    String name();

    String usage();

    String description();

    boolean executableAsConsole();

    String permission() default "None";
}
