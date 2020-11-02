package nirusu.nirubot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String description();
    
    String[] key();

    Context[] contexts();

    enum Context {
        PRIVATE,GUILD
    }
    
}
