import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import keycloak from './keycloak';
import { setAuthToken } from '../api/client';

interface AuthContextValue {
  initialized: boolean;
  authenticated: boolean;
  token: string | undefined;
  username: string | undefined;
  login: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [initialized, setInitialized] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [token, setToken] = useState<string | undefined>();

  useEffect(() => {
    let refreshTimer: number | undefined;

    keycloak
      .init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false,
      })
      .then((auth) => {
        setAuthenticated(auth);
        setToken(keycloak.token);
        setAuthToken(keycloak.token);
        setInitialized(true);

        if (auth) {
          refreshTimer = window.setInterval(() => {
            keycloak
              .updateToken(60)
              .then((refreshed) => {
                if (refreshed) {
                  setToken(keycloak.token);
                  setAuthToken(keycloak.token);
                }
              })
              .catch(() => keycloak.logout());
          }, 30000);
        }
      })
      .catch(() => setInitialized(true));

    return () => {
      if (refreshTimer) {
        window.clearInterval(refreshTimer);
      }
    };
  }, []);

  const login = useCallback(() => {
    keycloak.login();
  }, []);

  const logout = useCallback(() => {
    setAuthToken(undefined);
    keycloak.logout({ redirectUri: window.location.origin });
  }, []);

  const value = useMemo(
    () => ({
      initialized,
      authenticated,
      token,
      username: keycloak.tokenParsed?.preferred_username as string | undefined,
      login,
      logout,
    }),
    [initialized, authenticated, token, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
