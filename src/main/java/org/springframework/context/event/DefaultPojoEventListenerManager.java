/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package org.springframework.context.event;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

/**
 * The DefaultPojoEventListenerManager helps sending events to  event listener methods defined in beans
 * which are not manages by the Spring {@link ApplicationEventMulticaster}. The {@link ApplicationEventMulticaster}
 * for example misses sending events to spring beans with scope "Prototype".
 *
 * @author a.kohlbecker
 * @since May 29, 2017
 */
@SpringComponent
@UIScope
public class DefaultPojoEventListenerManager implements PojoEventListenerManager, ApplicationContextAware, Serializable {

    private static final long serialVersionUID = -6814417168274166953L;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    private final EventExpressionEvaluator evaluator = new EventExpressionEvaluator();

    private Map<Object, Collection<ApplicationListener<?>>> listenerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void addEventListeners(Object o){

        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(o.getClass());
        for (Method method : methods) {
            EventListener eventListener = AnnotationUtils.findAnnotation(method, EventListener.class);
            if (eventListener == null) {
                continue;
            }
            ApplicationListenerPojoMethodAdapter applicationListener = new ApplicationListenerPojoMethodAdapter(o.toString(), o.getClass(), method, o);
            applicationListener.init(this.applicationContext, this.evaluator);

            if(logger.isTraceEnabled()){
                logger.trace(String.format("Adding ApplicationListener for  %s@%s#%s", o.getClass().getSimpleName(), o.hashCode(), method.toGenericString()));
            }
            applicationEventMulticaster.addApplicationListener(applicationListener);
            addToMap(o, applicationListener);

        }
    }

    @Override
    public void removeEventListeners(Object o){
        if(listenerMap.containsKey(o)){
            listenerMap.get(o).forEach(l -> applicationEventMulticaster.removeApplicationListener(l));
            listenerMap.remove(o);
        }
    }

    private void addToMap(Object o, ApplicationListener<?> applicationListener) {
        if(!listenerMap.containsKey(o)){
            listenerMap.put(o, new ArrayList<>());
        }
        listenerMap.get(o).add(applicationListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
