package pt.lsts.accl.settings;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Simple Annotation with category and description.
 * The type, name and values are infered by the field this annotation is associated with.
 *
 * Created by jloureiro on 02-09-2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ACCLSetting {

    /**
     * The category it belongs to.
     * Used to have organization in the {@link pt.lsts.accl.androidlib.AcclSettingsActivity}.
     *
     * @return The name of the category, "other" by default.
     */
    public String category() default "other";

    /**
     * The Description of the setting.
     * Please provide a full description of what the setting is supposed to hold as value and how and why its value is needed and where it is used.
     * This description can also include the default value so users can backtrack to it.
     * You may include functional whitespace control like \n for newline.
     *
     * @return the full string with the description.
     */
    public String description() default "";

}
