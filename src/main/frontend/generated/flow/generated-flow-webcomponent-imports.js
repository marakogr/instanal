import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import $cssFromFile_0 from '@vaadin/vaadin-lumo-styles/lumo.css?inline';

injectGlobalWebcomponentCss($cssFromFile_0.toString());
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/form-layout/src/vaadin-form-row.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/button/src/vaadin-button.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/notification/src/vaadin-notification.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import 'Frontend/generated/jar-resources/flow-component-directive.js';
import 'lit';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';
const loadOnDemand = (key) => {
  const pending = [];
  if (key === '4de78494409bd2365048e90dc529db4f723ef378570fa6b0cbd3b094c022a4ae') {
    pending.push(import('./chunks/chunk-b87008f2e665d000a4fd15ea68e49ba2cf27301cd4d37db82b9022b87629d1ca.js'));
  }
  return Promise.all(pending);
}
window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}