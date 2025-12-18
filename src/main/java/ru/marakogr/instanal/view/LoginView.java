package ru.marakogr.instanal.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.service.SuperUserService;

@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final SuperUserService service;

    public LoginView(SuperUserService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Карточка контейнера
        var card = new VerticalLayout();
        card.setWidth("400px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        var title = new H2("Дружбометр");
        title.getStyle().set("margin-bottom", "20px");
        card.add(title);

        var form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.getStyle().set("row-gap", "var(--lumo-space-m)");

        var instagram = new TextField("Instagram");
        instagram.setWidthFull();
        var name = new TextField("Имя");
        name.setWidthFull();
        var instagramId = new TextField("Instagram ID");
        instagramId.setWidthFull();
        var telegram = new TextField("Telegram");
        telegram.setWidthFull();

        var submit = new Button("Войти");
        submit.setWidthFull();
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var switchMode = new Button("Создать аккаунт");
        switchMode.setWidthFull();
        switchMode.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        form.add(instagram, name, instagramId, telegram, submit, switchMode);
        card.add(form);
        add(card);

        var binder = new Binder<>(SuperUser.class);
        binder.forField(instagram).asRequired("Обязательно").bind(SuperUser::getInstagram, SuperUser::setInstagram);
        binder.forField(instagramId).asRequired("Обязательно").bind(SuperUser::getInstagramId, SuperUser::setInstagramId);
        binder.forField(name).bind(SuperUser::getName, SuperUser::setName);
        binder.forField(telegram).bind(SuperUser::getTelegram, SuperUser::setTelegram);

        Mode[] currentMode = {Mode.LOGIN};
        name.setVisible(false);
        instagramId.setVisible(false);
        telegram.setVisible(false);

        switchMode.addClickListener(e -> {
            if (currentMode[0] == Mode.LOGIN) {
                currentMode[0] = Mode.REGISTER;
                name.setVisible(true);
                instagramId.setVisible(true);
                telegram.setVisible(true);
                switchMode.setText("Уже есть аккаунт? Войти");
                submit.setText("Зарегистрироваться");
            } else {
                currentMode[0] = Mode.LOGIN;
                name.setVisible(false);
                instagramId.setVisible(false);
                telegram.setVisible(false);
                switchMode.setText("Создать аккаунт");
                submit.setText("Войти");
            }
        });

        submit.addClickListener(e -> {
            if (!binder.validate().isOk()) return;

            SuperUser loggedUser;
            if (currentMode[0] == Mode.LOGIN) {
                loggedUser = service.loginByInstagram(instagram.getValue())
                        .orElse(null);
                if (loggedUser == null) {
                    Notification.show("Пользователь не найден", 3000, Notification.Position.TOP_CENTER);
                    return;
                }
            } else {
                loggedUser = service.registerOrLogin(
                        name.getValue(), instagram.getValue(), instagramId.getValue(), telegram.getValue());
            }
            VaadinSession.getCurrent().setAttribute(SuperUser.class, loggedUser);
            UI.getCurrent().getPage().executeJs(
                    "localStorage.setItem('superUserInstagram', $0);", loggedUser.getInstagram());
            getUI().ifPresent(ui -> ui.navigate(MainView.class));
        });
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI.getCurrent().getPage().executeJs(
                "var stored = localStorage.getItem('superUserInstagram');" +
                        "if(stored) $0.$server.loginFromLocalStorage(stored);", getElement()
        );
    }

    @ClientCallable
    public void loginFromLocalStorage(String instagram) {
        service.loginByInstagram(instagram).ifPresent(user -> {
            VaadinSession.getCurrent().setAttribute(SuperUser.class, user);
            getUI().ifPresent(ui -> ui.navigate(MainView.class));
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        SuperUser user = VaadinSession.getCurrent().getAttribute(SuperUser.class);
        if (user != null) {
            // если уже залогинен — редирект на /main
            event.rerouteTo("main");
        }
    }

    public enum Mode {
        LOGIN,
        REGISTER

    }
}
