/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package org.springframework.context.event;

import java.lang.reflect.Method;

/**
 * A special ApplicationListenerMethodAdapter for prototype spring beans.
 *
 * @author a.kohlbecker
 * @since May 30, 2017
 */
public class ApplicationListenerPojoMethodAdapter extends ApplicationListenerMethodAdapter {

    private Object o;

    public ApplicationListenerPojoMethodAdapter(String beanName, Class<?> targetClass, Method method, Object o) {
        super(beanName, targetClass, method);
        this.o = o;
    }

    @Override
    protected Object getTargetBean() {
        return o;
    }
}