## 《LJL-Spring》项目设计思路

#### IOC模块

spring的ioc容器包含并管理应用对象的配置和生命周期，在这个意义上它是一种用于承载对象的容器，你可以配置你的每一个Bean对象是如何被创建的，这些Bean可以创建一个单独的实例或者每次都需要生成一个新的实例，以及关注他们是如何相互关联构建和使用的

如果一个Bean对象交给spring容器来进行管理，那么这个Bean对象就应该以类似零件的方式被拆解后存放到Bean的定义中，这样相当于一种把对象解耦的操作，可以由spring更加容易的进行管理，就像处理循环依赖等操作

当一个Bean对象被定义存放好以后，再由ioc容器进行统一装配，这个过程包括Bean的初始化，属性填充等，最终我们才可以完整的使用一个Bean实例化后的对象

ioc容器设计阶段：

对于容器，我认为凡是可以存放数据的都可以被称之为容器，比如arrylist，hashmap等，对于Bean容器的场景，我们应该需要一种用于存放和通过名称进行索引式的数据结构，那么hashmap再适合不过，确定了基础容器后，参照spring的实现，对于一个简单的spring容器的实现，还需要Bean的定义，注册和获取三个最基本的步骤，

定义：查阅源码，BeanDefinition是一个非常常见的接口，spring源码中BeanDefinition是一个接口，本项目中直接使用类

注册：这个过程就相当于把数据存放到hashmap中，只不过hashmap存放的是定义了的Bean的对象

获取：Bean的名字就是key，ioc容器初始化好Bean以后，就可以直接获取

所以最初的架构设计按照spring的结构来说，只需要一个BeanDefinition和一个获取Bean对象的工厂BeanFactory

~~~java
public class BeanDefinition {

    //BeanDefinition单纯存放Bean对象
    private Object bean;

    public BeanDefinition(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }
}
~~~

~~~java
public class BeanFactory {

    //Bean工厂定义一个成员变量ConcurrentHashMap来存放Bean的定义信息，通过Bean的名字进行一个获取，在存放的时候指定Bean
    //的名字
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public Object getBean(String name) {
        return beanDefinitionMap.get(name).getBean();
    }

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }
}
~~~

这就是一个最最基本的spring ioc容器的雏形，后面的一切的扩充都是基于这个大体架构来进行扩展，也就是Bean工厂获取beanDefinitionMap，这个map存放bean名字和BeanDefinition信息，然后通过BeanDefinition获取Bean

---

上面的步骤实现了一个非常粗糙的Bean容器，接下来需要对spring容器进行完善，比如把Bean的创建交给容器，而不是我们在调用的时候传递一个实例化好的对象，另外还需要考虑单例对象（这是spring默认的Bean的作用域），既然是单例Bean，那么对象的二次获取应该可以直接从内存中获取，这里应该想到单例池，（享元设计模式也挺像）。此外在框架的编写过程中，一定要注意完善基础结构的类结构，否则将来其他功能的扩展会非常困难

真正的容器肯定不能我们手动的放进去一个实例化好的Bean对象，那么可以放进去一个类信息，所以在BeanDefinition中需要把原来注入的object替换为Class beanClass;接下来需要做的就是在获取Bean对象时需要处理Bean对象的实例化操作以及判断当前单例对象在容器中是否已经缓存起来了

对于对象的获取，首先需要BeanFactory这样一种工厂，提供Bean的获取方法getBean(String name)，之后这个BeanFactory工厂接口肯定有一个抽象类来做模板方法设计模式的模板抽象类，这样可以统一核心方法的调用逻辑和定义标准，也就很好的控制了后续的实现者不用关心调用逻辑，按照统一的方式执行，那么抽象模板类的继承者们只需要关心具体的方法实现即可。注意源码中的AbstractBeanFactory并没有直接实现BeanFactory接口，而是实现ConfigurableBeanFactory接口，ConfigurableBeanFactory接口继承HierarchicalBeanFactory接口，HierarchicalBeanFactory接口最终继承顶级接口BeanFactory

既然有了模板抽象类，接下来重点关注的应该是模板抽象类的具体继承类，根据单一职责设计原则，注意功能的解耦，首先让AbstractAutowireCapableBeanFactory继承AbstractBeanFactory实现相应的抽象方法，它本身也是一个抽象类，所以它只会实现自己的抽象方法，其他抽象抽象方法又交给继承的类去实现，这里就体现了类实现过程中的各司其职，只需要关注自己的内容，不是自己的内容，不要参与。