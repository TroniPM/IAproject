package com.pmateus.gui.util.xmlpack;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class XmlViewFactory extends Object implements ViewFactory {

    /**
     * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
     */
    @Override
    public View create(Element element) {

        return new XmlView(element);
    }

}
