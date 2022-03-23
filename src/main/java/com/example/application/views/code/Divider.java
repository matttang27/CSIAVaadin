package com.example.application.views.code;

import com.vaadin.flow.component.html.Span;

public class Divider extends Span {

    public Divider() {
        this.getStyle().set("background-color", "grey");
        this.getStyle().set("flex", "0 0 5px");
        this.getStyle().set("align-self", "stretch");
    }
}
