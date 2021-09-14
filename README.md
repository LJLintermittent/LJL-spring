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

既然有了模板抽象类，接下来重点关注的应该是模板抽象类的具体继承类，根据单一职责设计原则，注意功能的解耦，首先让AbstractAutowireCapableBeanFactory继承AbstractBeanFactory实现相应的抽象方法，它本身也是一个抽象类，所以它只会实现自己的抽象方法，其他抽象抽象方法又交给继承的类去实现，这里就体现了类实现过程中的各司其职，只需要关注自己的内容，不是自己的内容，不要参与。另外还有一个需要重点考虑的问题是关于单例SingletonBeanRegistry 接口的实现，使用DefaultSingletonBeanRegistry 对SingletonBeanRegistry进行实现后，会被AbstractBeanFactory继承，那么这时候AbstractBeanFactory就是一个非常强大的抽象类了，很好的体现了对模板方法设计模式的使用

~~~wiki
对于这块的类的架构设计基本仿照spring的实现：
BeanFactory（类，源码为接口）的定义由AbstractBeanFactory 抽象类实现接口的getBean方法
AbstractBeanFactory又继承实现了SingletonBeanRegistry的DefaultSingletonBeanRegistry类，这样AbstractBeanFactory抽象类就具备了单例Bean的注册功能。SingletonBeanRegistry接口定义了单例Bean的注册和获取功能，交给DefaultSingletonBeanRegistry做一个具体的实现，这块重点是如何通过三级缓存来处理单例Bean的循环依赖问题
AbstractBeanFactory抽象类定义了两个抽象方法，getBeanDefinition和createBean，而这两个抽象又分别由DefaultListableBeanFactory和AbstractAutowireCapableBeanFactory实现
最终DefaultListableBeanFactory还会继承抽象类AbstractAutowireCapableBeanFactory，所以也可以调用createBean方法，所以至此DefaultListableBeanFactory不仅实现了AbstractBeanFactory抽象模板类中的getBeanDefinition方法，即获取Bean的方法，也间接可以调用创建Bean的方法，所以可以说DefaultListableBeanFactory提供了对Bean容器的成熟的实现，可以直接对外使用。
而上面最最简陋的IOC容器中的beanDefinitionMap被放到了DefaultListableBeanFactory中
~~~

____

至此模仿spring源码搭建出了一个比较基本的IOC容器，主要是模仿spring的模板方法架构，来对类的继承关系做一个梳理

虽然目前扩充了Bean容器的功能，把实例化对象交给容器来统一管理，但是这个时候肯定是通过拿到类的Class对象然后通过newInstance的方式来创建对象，这种方式有一个最大的问题就是如果对象有带参的构造函数，那就没有办法出想要的对象，因为newInstance只能调用无参构造。这个时候CGlib就该出场了

首先需要在BeanFactory的getBean方法中添加根据构造器参数获取Bean的方法的定义，那么拿到了构造函数的参数以后，就可以采用两种方案来创建Bean对象，一种是DeclaredConstructor（通过Class对象调用getDeclaredConstructor()获取构造器然后调用newinstance方法来创建对象），一种就是CGlib

这块玩的主要就是InstantiationStrategy接口，可以使用两种策略来对这个接口进行实现，模仿spring使用了SimpleInstantiationStrategy和CglibSubclassingInstantiationStrategy两个类来实现两种实例化策略，源码中其实是CglibSubclassingInstantiationStrategy继承SimpleInstantiationStrategy。

实例化策略这块写完以后需要在AbstractAutowireCapableBeanFactory中通过成员变量上new的方式注入InstantiationStrategy的实现类

放在AbstractAutowireCapableBeanFactory中的主要目的是这个类要实现一个非常重要的方法，就是createBean，然后调用doCreateBean，那么在doCreateBean中，第一步首先要实例化Bean，bean = createBeanInstance(beanDefinition, beanName, args);createBeanInstance方法中首先需要获取构造器，然后遍历所有的构造器并且拿到参数的长度ctor.getParameterTypes().length

使用这个长度与数组的长度进行一个比较，object[] objs，如果相等了证明匹配上了，那就把这个构造器拿去给实例化策略接口的具体实现类去做Bean对象的实例化

___

至此实现的内容包括：仿照spring的架构实现一个容器，可以定义和注册Bean，以及使用策略模式来实例化Bean，可以按照是否含有带参的构造函数来进行实例化。到目前为止在创建Bean对象的过程中还缺少一步那就是关于类中是否包含属性的问题，如果类中包含属性那么在实例化的时候还需要把属性信息填上，这样才是一个完整对象的创建

对于属性的填充不只是int，long等还包括没有实例化的对象属性，都需要在Bean创建的过程中进行填充，特别注意Bean的循环依赖问题。

由于属性填充是在实例化Bean以后进行的操作，也就是在策略创建Bean实例的方法之后，createBeanInstance方法，而这个实例化的方法在源码中是写在createBean这个方法中，所以应该在AbstractAutowireCapableBeanFactory中完善填充属性的逻辑，这个属性填充的方法就是 applyPropertyValues(beanName, bean, beanDefinition);由于需要在创建Bean的时候填充属性，那么就需要在Bean的定义BeanDefinition 中添加PropertyValues 属性。

另外在填充属性信息时还包括了Bean的对象类型，也就是需要再定义一个BeanReference，里面其实就是就是一个BeanName，源码中BeanReference是一个接口，也就提供了一个getBeanName方法。项目中直接使用一个同名类来代替，直接实现getBeanName方法，返回构造注入的BeanName；

~~~java
public class BeanReference {

    /**
     * BeanReference用于在填充属性的时候
     * 只负责填充Bean类型的属性
     */
    private final String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }
    
    public String getBeanName() {
        return beanName;
    }
}
~~~

当然除了可能会注入Bean类型的属性外，还可能是普通的类型，那么通过封装PropertyValue，PropertyValues这两个类来对所有的非对象属性进行一个封装。PropertyValue就两个主要的属性，一个name，一个value，分别表示属性名称和属性值，另外PropertyValues在本项目中简单用类来表示，然后由于属性可能会有很多个，使用一个arraylist来对PropertyValue进行存放，另外这里源码中变化较大，它的PropertyValues是一个接口，并且继承了Iterable接口，所以他对每个属性的遍历操作是通过迭代器的方式来进行的，并且在这个接口里面有几个默认实现，主要是玩了一下stream流

那么用这三个类对属性进行封装了以后，接下来就该主要关注在AbstractAutowireCapableBeanFactory的createBean的doCreateBean方法中在实例化完Bean以后该对Bean的属性进行填充，applyPropertyValues方法的实现：

~~~java
  /**
     * Bean 属性填充
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                if (value instanceof BeanReference) {
                    // A 依赖 B，获取 B 的实例化
                    BeanReference beanReference = (BeanReference) value;
                    // 如果A中依赖B，那么在填充A的属性的时候，会走到这块，去创建B的实例
                    value = getBean(beanReference.getBeanName());
                }

                // 反射设置属性填充，循环依赖的时候到这一步，B对象拿到了需要填充的属性A
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values：" + beanName);
        }
    }
~~~

hutool对javabean的定义是只有一个类有对属性进行set和get的方法，那么就可可以称之为javabean

这里面非常好玩的是对于一个类A，它的创建过程中的填充属性阶段，会遇到要填充的属性也是一个Bean的情况，那么通过instanceof BeanReference的方式可以很容易的找出来这个value，找出来value以后放心大胆的强转成BeanReference，由于BeanReference里面封装了beanName，拿到Beanname以后再去getBean，相当于又去走一遍获取Bean的流程，也就是先从单例池中获取，如果单例池中没有，在走一遍创建Bean的流程，也就是A中依赖B，那我要创建A，得先把B创建了，这里相当于重复走了一遍A的创建流程，这里暂时没有处理循环依赖，假设不存在循环依赖，那么最终B会成功创建，所以A会把B作为一个属性来填充进来。这就是applyPropertyValues的实现

___

