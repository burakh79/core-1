/*
 * Copyright 2011-2015 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.primefaces.extensions.component.ckeditor;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import org.primefaces.context.RequestContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

/**
 * Renderer for the {@link CKEditor} component.
 *
 * @author Thomas Andraschko / last modified by $Author$
 * @version $Revision$
 * @since 0.2
 */
public class CKEditorRenderer extends InputRenderer {

    @Override
    public void decode(final FacesContext context, final UIComponent component) {
        final CKEditor ckEditor = (CKEditor) component;

        if (ckEditor.isReadonly()) {
            return;
        }

        // set value
        final String clientId = ckEditor.getClientId(context);
        final Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        if (params.containsKey(clientId)) {
            ckEditor.setSubmittedValue(params.get(clientId));
        }

        // decode behaviors
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final CKEditor ckEditor = (CKEditor) component;

        encodeMarkup(context, ckEditor);
        encodeScript(context, ckEditor);
    }

    protected void encodeMarkup(final FacesContext context, final CKEditor ckEditor) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = ckEditor.getClientId(context);

        writer.startElement("textarea", ckEditor);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);

        if (ckEditor.getTabindex() != null) {
            writer.writeAttribute("tabindex", ckEditor.getTabindex(), null);
        }

        renderPassThruAttributes(context, ckEditor, HTML.INPUT_TEXTAREA_ATTRS);
        renderDomEvents(context, ckEditor, HTML.INPUT_TEXT_EVENTS);
        
        final String valueToRender = ComponentUtils.getValueToRender(context, ckEditor);
        if (valueToRender != null) {
            if (ckEditor.isEscape()) {
                writer.writeText(valueToRender, null);
            } else {
                writer.write(valueToRender);
            }
        }

        writer.endElement("textarea");
    }

    protected void encodeScript(final FacesContext context, final CKEditor ckEditor) throws IOException {
        WidgetBuilder wb = RequestContext.getCurrentInstance().getWidgetBuilder();
        wb.initWithDomReady("ExtCKEditor", ckEditor.resolveWidgetVar(), ckEditor.getClientId());
        wb.attr("height", ckEditor.getHeight())
                .attr("width", ckEditor.getWidth())
                .attr("skin", ckEditor.getSkin())
                .attr("toolbar", ckEditor.getToolbar())
                .attr("readOnly", ckEditor.isReadonly())
                .attr("interfaceColor", ckEditor.getInterfaceColor())
                .attr("language", ckEditor.getLanguage())
                .attr("defaultLanguage", ckEditor.getDefaultLanguage())
                .attr("contentsCss", ckEditor.getContentsCss())
                .attr("customConfig", ckEditor.getCustomConfig())
                .attr("advancedContentFilter", ckEditor.isAdvancedContentFilter())
                .attr("tabindex", ckEditor.getTabindex());

        encodeClientBehaviors(context, ckEditor);
        wb.finish();
    }

    @Override
    public Object getConvertedValue(final FacesContext context, final UIComponent component, final Object submittedValue) {
        final CKEditor ckEditor = (CKEditor) component;
        final String value = (String) submittedValue;
        final Converter converter = ComponentUtils.getConverter(context, component);

        if (converter != null) {
            return converter.getAsObject(context, ckEditor, value);
        }

        return value;
    }
}
