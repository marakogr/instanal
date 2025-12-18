package ru.marakogr.instanal.view;

import com.vaadin.flow.component.html.Span;

public class Utils {
    public static Span createBreadcrumb(String text) {
        var span = new Span(text);
        span.getStyle()
                .set("font-weight", "500")
                .set("cursor", "pointer")
                .set("color", "#007bff")
                .set("text-decoration", "underline");
        return span;
    }
}
