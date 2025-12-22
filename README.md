# InstAnal
Приложение для анализа чатов друзей из Instagram. Позволяет преобразовать данные чата в удобный для анализа формат.
Для аналитики используется Apache Superset.

## Apache Superset

[Пример дашборда](img/superset.jpg)

## Как запускать

### Запустить
```shell
docker compose up -d --build
```
### Проинициализировать Superset
```shell
docker compose exec superset superset db upgrade
docker compose exec superset superset fab create-admin --username admin --firstname Admin --lastname User --email admin@admin.com --password admin
docker compose exec superset superset init
```
### Добавить базу в superset
localhost:8088 admin/admin
```
postgresql+psycopg2://postgres:postgres@postgres:5432/instanal
```
### Открыть http://localhost:8080/
[Страница логина](img/login.png)
### Завести пользователя
### Добавить друзей
[Главная](img/main.png)
### Импортнуть чаты, запустить расчет рейтинга
[Главная](img/chat.png)


Дашборд в superset создается автоматически