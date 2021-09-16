package com.learn.myspring.context.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;
import com.learn.myspring.beans.factory.config.BeanFactoryPostProcessor;
import com.learn.myspring.beans.factory.config.BeanPostProcessor;
import com.learn.myspring.context.ApplicationEvent;
import com.learn.myspring.context.ApplicationListener;
import com.learn.myspring.context.ConfigurableApplicationContext;
import com.learn.myspring.context.event.ApplicationEventMulticaster;
import com.learn.myspring.context.event.ContextClosedEvent;
import com.learn.myspring.context.event.ContextRefreshedEvent;
import com.learn.myspring.context.event.SimpleApplicationEventMulticaster;
import com.learn.myspring.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/10 14:16
 * Package: com.learn.myspring.context.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 抽象应用上下文
// AbstractApplicationContext继承了DefaultResourceLoader是为了处理spring.xml配置文件的加载
// 之后定义了refresh方法的实现过程
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    /*
      AbstractApplicationContext是整个容器的核心处理类，是真正的spring的执行者，内部大量的模板方法，
      实现了高复用和高扩展，实现了spring的启动，停止，刷新，事件推送，BeanFactory方法的默认实现getBean()
      以及虚拟机注册钩子函数进行回调。
      AbstractApplicationContext是ApplicationContext的核心抽象模板类
      最重要的refresh方法就是AbstractApplicationContext做模板实现，refresh方法是通过典型的模板方法设计模式实现的，但是
      模板方法设计模式有一个最大的弊端，就是非常忌讳修改，特别是顺序不能改变，因为一旦改变已经实现好的子类就不会按照当初设计的目标
      去执行了
      spring的作者是怎么规避模板方法的弊端的？
      一是对整个spring模型的理解以及对各个模块的分工职责的明确划分
      二是在使用抽象模板的高复用性的同时支持重写及注册响应核心处理实体的回调函数来增加整个模板方法的扩展性
     */

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void refresh() throws BeansException {
        // 1. 创建 BeanFactory，并加载 BeanDefinition
        refreshBeanFactory();

        // 2. 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3. 添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean
        // 对象都能感知所属的 ApplicationContext
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        // 4. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in the context.)
        invokeBeanFactoryPostProcessors(beanFactory);

        // 5. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
        registerBeanPostProcessors(beanFactory);

        // 6. 初始化事件发布者
        initApplicationEventMulticaster();

        // 7. 注册事件监听器
        registerListeners();

        // 8. 提前实例化单例Bean对象，涉及循环依赖的处理
        beanFactory.preInstantiateSingletons();

        // 9. 发布容器刷新完成事件
        finishRefresh();
    }

    protected abstract void refreshBeanFactory() throws BeansException;

    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * 在Bean定义加载完成以后，实例化之前，使用beanFactoryPostProcessor修改Bean定义信息
     */
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 向存放BeanPostProcessor的list中添加BeanPostProcessor
     */
    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : applicationListeners) {
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }

    private void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {

        // 发布容器关闭事件
        publishEvent(new ContextClosedEvent(this));

        // 执行销毁单例bean的销毁方法
        getBeanFactory().destroySingletons();

    }
}
