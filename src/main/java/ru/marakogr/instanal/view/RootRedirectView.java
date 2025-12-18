package ru.marakogr.instanal.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import ru.marakogr.instanal.db.model.SuperUser;

@Route("")
public class RootRedirectView extends VerticalLayout implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        SuperUser user = VaadinSession.getCurrent().getAttribute(SuperUser.class);
        if (user == null) {
            event.rerouteTo(LoginView.class);
        } else {
            event.rerouteTo("main");
        }
    }
}
