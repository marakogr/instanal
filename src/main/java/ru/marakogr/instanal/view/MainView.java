package ru.marakogr.instanal.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.UploadHandler;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.AnalysisService;
import ru.marakogr.instanal.service.ChatService;
import ru.marakogr.instanal.service.FriendService;
import ru.marakogr.instanal.service.SuperUserService;

@Route("main")
public class MainView extends VerticalLayout implements BeforeEnterObserver {
  private final FriendService friendService;
  private final ChatService chatService;
  private final SupersetService supersetService;
  private final Grid<FriendRelation> grid = new Grid<>(FriendRelation.class, false);
  private final SuperUserService superUserService;

  public MainView(
      FriendService friendService,
      ChatService chatService,
      AnalysisService analysisService,
      SupersetService supersetService,
      SuperUserService superUserService) {
    this.friendService = friendService;
    this.chatService = chatService;
    this.supersetService = supersetService;
    this.superUserService = superUserService;

    var user = VaadinSession.getCurrent().getAttribute(SuperUser.class);
    if (user == null) {
      getUI().ifPresent(ui -> ui.navigate(LoginView.class));
      return;
    }
    setSizeFull();

    var card = new VerticalLayout();

    // Хлебные крошки
    var breadcrumbs = new HorizontalLayout();
    breadcrumbs.setSpacing(true);
    var homeCrumb = Utils.createBreadcrumb("Главная");
    homeCrumb.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MainView.class)));
    breadcrumbs.add(homeCrumb);
    card.add(breadcrumbs);

    // Панель действий
    var actions = new HorizontalLayout();
    actions.setWidthFull();
    actions.setSpacing(true);
    actions.setAlignItems(Alignment.CENTER);

    var addFriendBtn = new Button("Добавить друга", e -> openAddFriendDialog(user));
    addFriendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    var actionDropdownBtn = new Button("Действия ▼");
    actionDropdownBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

    var actionMenu = new ContextMenu(actionDropdownBtn);
    actionMenu.setOpenOnClick(true);

    // Добавляем пункты меню
    actionMenu.addItem("Удалить выбранных", e -> deleteSelectedFriends());
    actionMenu.addItem("Импорт чата", e -> importChatSelected());
    actionMenu.addItem("Анализ в Superset", e -> analyzeSelectedSuperset());
    actionMenu.addItem(
        "Рейтинг друзей", e -> calculateRating(friendService, analysisService, user));
    actions.add(addFriendBtn, actionDropdownBtn);
    card.add(actions);

    // Грид
    grid.setWidthFull();
    grid.addColumn(relation -> relation.getFriendSuperUser().getName()).setHeader("Имя");
    grid.addColumn(relation -> relation.getFriendSuperUser().getInstagram()).setHeader("Инста");

    // Красивое отображение рейтинга (звёздочки + число)
    grid.addComponentColumn(this::createRatingComponent)
        .setHeader("Рейтинг")
        .setSortable(true)
        .setComparator(FriendRelation::getRating);

    grid.setSelectionMode(Grid.SelectionMode.MULTI);
    grid.setItems(friendService.getFriends(user));
    card.add(grid);

    add(card);

    var logoutBtn =
        new Button(
            "Выйти",
            e -> {
              VaadinSession.getCurrent().close();
              UI.getCurrent().getPage().executeJs("localStorage.removeItem('superUserInstagram');");
              getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            });
    logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
    actions.add(logoutBtn);
  }

  private void calculateRating(
      FriendService friendService, AnalysisService analysisService, SuperUser user) {
    // Показываем прогресс или уведомление
    Notification notification = new Notification("Расчёт рейтинга запущен...", 3000);
    notification.setPosition(Notification.Position.MIDDLE);
    notification.open();

    // Запускаем анализ
    analysisService.analyzeAllFriends(user);

    // Обновляем грид
    grid.setItems(friendService.getFriends(user));

    Notification success = new Notification("Рейтинг друзей обновлён!", 3000);
    success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    success.setPosition(Notification.Position.MIDDLE);
    success.open();
  }

  private Component createRatingComponent(FriendRelation relation) {
    double rating = relation.getRating();
    var layout = new HorizontalLayout();
    layout.setSpacing(false);
    layout.setAlignItems(Alignment.CENTER);

    int fullStars = (int) rating;
    boolean hasHalf = rating - fullStars >= 0.5;

    for (int i = 0; i < fullStars; i++) {
      var star = new Icon(VaadinIcon.STAR);
      star.setColor("#ffc107");
      layout.add(star);
    }
    if (hasHalf) {
      var half = new Icon(VaadinIcon.STAR_HALF_LEFT_O);
      half.setColor("#ffc107");
      layout.add(half);
    }
    int empty = 10 - (int) Math.ceil(rating);
    for (int i = 0; i < empty; i++) {
      var emptyStar = new Icon(VaadinIcon.STAR_O);
      emptyStar.setColor("#e0e0e0");
      layout.add(emptyStar);
    }

    var scoreText = new Span(String.format(" %.1f", rating));
    scoreText.getStyle().set("margin-left", "8px").set("font-weight", "bold");

    layout.add(scoreText);
    return layout;
  }

  private void deleteSelectedFriends() {
    var selected = grid.getSelectedItems();
    selected.forEach(friendService::deleteFriend);
    grid.setItems(
        friendService.getFriends(VaadinSession.getCurrent().getAttribute(SuperUser.class)));
    Notification.show("Друзья удалены", 2000, Notification.Position.TOP_CENTER);
  }

  private void importChatSelected() {
    var selected = grid.getSelectedItems();
    if (selected.isEmpty()) {
      Notification.show("Выберите друга", 3000, Notification.Position.MIDDLE);
      return;
    }
    if (selected.size() > 1) {
      Notification.show(
          "Можно выбрать только одного друга за раз", 3000, Notification.Position.MIDDLE);
      return;
    }
    grid.getSelectedItems().forEach(this::openImportDialog);
  }

  private void analyzeSelectedSuperset() {
    var selected = grid.getSelectedItems();

    if (selected.isEmpty()) {
      Notification.show("Выберите хотя бы одного друга", 3000, Notification.Position.MIDDLE);
      return;
    }

    selected.forEach(
        relation -> {
          String guestLink = supersetService.generateGuestLink(relation);

          getUI()
              .ifPresent(
                  ui -> {
                    ui.getPage().executeJs("window.open($0, '_blank');", guestLink);
                  });
        });

    Notification.show(
        "Открыто " + selected.size() + " дашборд(ов) в новых вкладках",
        3000,
        Notification.Position.MIDDLE);
  }

  private void openAddFriendDialog(SuperUser superUser) {
    var dialog = new Dialog();
    dialog.setHeaderTitle("Добавить друга");
    dialog.setWidth("500px");
    dialog.setMaxWidth("90vw");

    var content = new VerticalLayout();
    content.setSpacing(true);
    content.setPadding(false);

    var instagramField = new TextField("Instagram ник");
    instagramField.setPlaceholder("@username или username");
    instagramField.setWidthFull();

    var searchBtn =
        new Button(
            "Найти",
            e -> performSearch(superUser, instagramField.getValue().trim(), content, dialog));
    searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    var searchLayout = new HorizontalLayout(instagramField, searchBtn);
    searchLayout.setWidthFull();
    searchLayout.expand(instagramField);

    content.add(new H3("Поиск друга по Instagram"), searchLayout);

    dialog.add(content);
    dialog.open();
  }

  private void performSearch(
      SuperUser superUser, String searchQuery, VerticalLayout content, Dialog dialog) {
    if (searchQuery.isBlank()) {
      Notification.show("Введите ник Instagram", 3000, Notification.Position.MIDDLE);
      return;
    }

    var cleanQuery = searchQuery.replaceFirst("^@", "");
    var allMatches = superUserService.findByInstagramContainingIgnoreCase(cleanQuery);
    allMatches = allMatches.stream().filter(u -> !u.getId().equals(superUser.getId())).toList();
    var existingFriendIds =
        friendService.getFriends(superUser).stream()
            .map(relation -> relation.getFriendSuperUser().getId())
            .collect(Collectors.toSet());
    content.removeAll();
    content.add(new H3("Результат поиска по: @" + cleanQuery));
    if (allMatches.isEmpty()) {
      content.add(new Paragraph("Пользователь с таким Instagram не найден."));
      showCreateNewFriendForm(superUser, cleanQuery, content, dialog);
    } else {
      content.add(new Paragraph("Найдено пользователей: " + allMatches.size()));
      var list = new VerticalLayout();
      list.setSpacing(true);
      for (var user : allMatches) {
        boolean isAlreadyFriend = existingFriendIds.contains(user.getId());
        var item = new HorizontalLayout();
        item.setAlignItems(Alignment.CENTER);
        item.setWidthFull();
        var nameSpan = new Span(user.getName() + " (@" + user.getInstagram() + ")");
        nameSpan.getStyle().set("font-weight", "bold");
        var statusSpan = new Span();
        if (isAlreadyFriend) {
          statusSpan.setText("Уже в друзьях");
          statusSpan.getElement().getThemeList().add("badge error");
        } else {
          statusSpan.setText("Можно добавить");
          statusSpan.getElement().getThemeList().add("badge success");
        }
        var addBtn =
            new Button(
                "Добавить",
                e -> {
                  friendService.addFriend(superUser, user);
                  grid.setItems(friendService.getFriends(superUser));
                  dialog.close();
                  Notification.show("Друг добавлен!", 3000, Notification.Position.MIDDLE);
                });
        addBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        if (isAlreadyFriend) {
          addBtn.setEnabled(false);
          addBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        }
        item.add(nameSpan, statusSpan);
        item.expand(nameSpan);
        item.add(addBtn);
        list.add(item);
      }
      content.add(list);
    }
    var createNewBtn =
        new Button(
            "Создать нового с ником @" + cleanQuery,
            e -> {
              content.removeAll();
              showCreateNewFriendForm(superUser, cleanQuery, content, dialog);
            });
    createNewBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    content.add(new Hr(), createNewBtn);
  }

  private void showCreateNewFriendForm(
      SuperUser superUser, String prefillInstagram, VerticalLayout content, Dialog dialog) {
    content.add(new H3("Создание нового друга"));
    var form = new FormLayout();
    form.setMaxWidth("400px");
    var name = new TextField("Имя");
    var instagram = new TextField("Instagram");
    instagram.setValue(prefillInstagram);
    var instagramId = new TextField("Instagram ID");
    var telegram = new TextField("Telegram");
    form.add(name, instagram, instagramId, telegram);
    var saveBtn =
        new Button(
            "Создать и добавить",
            e -> {
              friendService.addFriend(
                  superUser,
                  name.getValue(),
                  instagram.getValue(),
                  instagramId.getValue(),
                  telegram.getValue());
              grid.setItems(friendService.getFriends(superUser));
              dialog.close();
              Notification.show(
                  "Новый друг создан и добавлен!", 3000, Notification.Position.MIDDLE);
            });
    saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    var cancelBtn = new Button("Отмена", e -> dialog.close());
    var buttons = new HorizontalLayout(saveBtn, cancelBtn);
    buttons.setWidthFull();
    content.add(form, buttons);
  }

  private void openImportDialog(FriendRelation relation) {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Импорт HAR-файла для " + relation.getFriendSuperUser().getName());

    ProgressBar progress = new ProgressBar();
    progress.setIndeterminate(true);
    progress.setVisible(false);

    Upload upload = new Upload();
    upload.setAcceptedFileTypes(".har", "application/json");

    upload.setUploadHandler(
        (UploadHandler)
            event -> {
              getUI().ifPresent(ui -> ui.setPollInterval(500));
              try {
                progress.setVisible(true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = event.getInputStream().read(buffer)) != -1) {
                  baos.write(buffer, 0, bytesRead);
                }
                byte[] harBytes = baos.toByteArray();

                chatService.importChat(harBytes, relation);

                getUI()
                    .ifPresent(
                        ui ->
                            ui.access(
                                () -> {
                                  Notification.show(
                                      "Импорт завершён! Файл: " + event.getFileName(),
                                      3000,
                                      Notification.Position.TOP_CENTER);
                                  progress.setVisible(false);
                                  dialog.close();
                                  grid.setItems(
                                      friendService.getFriends(
                                          VaadinSession.getCurrent()
                                              .getAttribute(SuperUser.class)));
                                }));

              } catch (Exception ex) {
                getUI()
                    .ifPresent(
                        ui ->
                            ui.access(
                                () -> {
                                  Notification.show(
                                      "Ошибка импорта: " + ex.getMessage(),
                                      5000,
                                      Notification.Position.TOP_END);
                                  progress.setVisible(false);
                                }));
              } finally {
                getUI().ifPresent(ui -> ui.setPollInterval(-1));
              }
            });

    dialog.add(upload, progress);
    dialog.open();
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    SuperUser user = VaadinSession.getCurrent().getAttribute(SuperUser.class);
    if (user == null) {
      event.rerouteTo(LoginView.class);
    }
  }
}
