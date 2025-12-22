# superset_config.py

from flask_appbuilder.security.manager import AUTH_DB

# обязательно
FAB_ADD_SECURITY_API = True

# auth
AUTH_TYPE = AUTH_DB

# для API
WTF_CSRF_ENABLED = False

# если используешь curl / внешние клиенты
SESSION_COOKIE_SAMESITE = "Lax"

# опционально
PUBLIC_ROLE_LIKE = "Gamma"
