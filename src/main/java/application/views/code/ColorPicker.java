package application.views.code;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Tag;

@Tag("input")
public class ColorPicker extends AbstractSinglePropertyField<ColorPicker, String> {

    public ColorPicker() {
        super("value", "", false);
        //i have no clue what goes on here to be honest
        getElement().setAttribute("type", "color");
        // By default AbstractSinglePropertyField listens to a "value-changed" event,
        // but input type=color fires "change"
        setSynchronizedEvent("change");
    }
}