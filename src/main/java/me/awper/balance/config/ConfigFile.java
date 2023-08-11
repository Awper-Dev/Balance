package me.awper.balance.config;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Qualifier
@Target({FIELD, PARAMETER, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
}
