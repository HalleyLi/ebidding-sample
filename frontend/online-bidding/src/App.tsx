import { ConfigProvider } from "antd";
import * as React from "react"
import { IntlProvider } from 'react-intl';
import { ThemeSwitcherProvider } from 'react-css-theme-switcher';
import enUS from 'antd/es/locale/en_US';
import zhCN from 'antd/es/locale/zh_CN';
import { useSelector } from "react-redux";
import RenderRouter from "./routes";
import { history, HistoryRouter } from "./routes/history";
import { localeConfig } from "./locales";

const App: React.FC = () => {
  const { locale } = useSelector(state => state.user);
  const { theme } = useSelector(state => state.global);

  const themes = {
    light: 'https://fastly.jsdelivr.net/npm/antd@4.17.2/dist/antd.css',
    dark: 'https://fastly.jsdelivr.net/npm/antd@4.17.2/dist/antd.dark.css',
  };

  // const themes = {
  //   // light: '/styles/antd/antd.css',
  //   dark: '/styles/antd/antd.dark.css',
  // };


  /**
   * handler function that passes locale
   * information to ConfigProvider for
   * setting language across text components
   */
  const getAntdLocale = () => {
    if (locale === 'en_US') {
      return enUS;
    } else if (locale === 'zh_CN') {
      return zhCN;
    }
  };

  return (
    <ConfigProvider locale={getAntdLocale()} componentSize="middle">
      <ThemeSwitcherProvider defaultTheme={theme} themeMap={themes}>
        <IntlProvider locale={locale.split('_')[0]} messages={localeConfig[locale]}>
          <HistoryRouter history={history}>
            <RenderRouter />
          </HistoryRouter>
        </IntlProvider>
      </ThemeSwitcherProvider>
    </ConfigProvider>
  );
}

export default App;