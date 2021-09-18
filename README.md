

## 《LJL-Spring》项目设计思路

# IOC模块

### 最简易的IOC容器

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

### 模板方法设计模式，设计Bean的定义，注册和获取，以及获取Bean的关键方法getBean的模板方法定义

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

### 基于cglib和jdk的方式并且使用策略模式实现Bean的实例化策略

至此模仿spring源码搭建出了一个比较基本的IOC容器，主要是模仿spring的模板方法架构，来对类的继承关系做一个梳理

虽然目前扩充了Bean容器的功能，把实例化对象交给容器来统一管理，但是这个时候肯定是通过拿到类的Class对象然后通过newInstance的方式来创建对象，这种方式有一个最大的问题就是如果对象有带参的构造函数，那就没有办法出想要的对象，因为newInstance只能调用无参构造。这个时候CGlib就该出场了

首先需要在BeanFactory的getBean方法中添加根据构造器参数获取Bean的方法的定义，那么拿到了构造函数的参数以后，就可以采用两种方案来创建Bean对象，一种是DeclaredConstructor（通过Class对象调用getDeclaredConstructor()获取构造器然后调用newinstance方法来创建对象），一种就是CGlib

这块玩的主要就是InstantiationStrategy接口，可以使用两种策略来对这个接口进行实现，模仿spring使用了SimpleInstantiationStrategy和CglibSubclassingInstantiationStrategy两个类来实现两种实例化策略，源码中其实是CglibSubclassingInstantiationStrategy继承SimpleInstantiationStrategy。

实例化策略这块写完以后需要在AbstractAutowireCapableBeanFactory中通过成员变量上new的方式注入InstantiationStrategy的实现类

放在AbstractAutowireCapableBeanFactory中的主要目的是这个类要实现一个非常重要的方法，就是createBean，然后调用doCreateBean，那么在doCreateBean中，第一步首先要实例化Bean，bean = createBeanInstance(beanDefinition, beanName, args);createBeanInstance方法中首先需要获取构造器，然后遍历所有的构造器并且拿到参数的长度ctor.getParameterTypes().length

使用这个长度与数组的长度进行一个比较，object[] objs，如果相等了证明匹配上了，那就把这个构造器拿去给实例化策略接口的具体实现类去做Bean对象的实例化

___

### 为Bean对象注入属性的功能实现（如果属性也是一个Bean对象，这块需要注意实现逻辑）

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

### 设计与实现资源加载器，从spring.xml中解析和注册Bean对象

目前使用这个轮子框架，用户还必须手动的创建一个个BeanDefinition，然后使用beanfactory来注册BeanDefinition，再把属性进去，然后再调用beanfactory的getBean方法，这种冗余的代码操作是不可能让用户来写的，那么需要提供一种在配置文件中配置好所有的Bean，并且能解析出配置文件中的信息，然后交给IOC容器去自动的创建

目前仿照spring的设计，可以有一个思路就是，实现一个能读取classpath（类路径），本地文件或者云文件（url的方式）中的文件信息的解析器，这个配置文件中会包含Bean对象的描述和属性信息，在读取完配置文件后，接下来需要对配置文件中对Bean的描述信息解析后进行注册，把Bean对象放到spring容器中

在spring源码中，io部分其实是相对比较独立的一部分，对于任何一个框架来说，都可以把这些部分做一个完全解耦，也就是说这部分的内容可以直接拷贝出来独立使用，相当于是一个独立的资源加载模块，可拔插式的设计。

为了能把Bean的定义，注册和初始化交给spring.xml配置化的处理，那么需要实现两大块内容：分别是资源加载器，xml资源处理类，实现过程主要是对接口Resource`、`ResourceLoader的实现，另外还需要一个接口BeanDefinitionReader来把资源做一个具体的使用，将配置信息注册到spring容器中去

在Resource的资源加载器的实现中包括了ClassPath、系统文件、云配置文件，这三种方式与spring源码的实现做了一个看齐，最终在DefaultResourceLoader 中做一个具体的调用

接口：BeanDefinitionReader、抽象类：AbstractBeanDefinitionReader、实现类：XmlBeanDefinitionReader，这三部分内容主要是合理清晰的处理了资源读取后的注册 Bean 容器操作。接口管定义，抽象类处理非接口功能外的注册Bean组件填充，最终实现类即可只关心具体的业务实现

另外这部分根据github项目small-spring的实现，加上了很多spring中的架构设计

比如ListableBeanFactory接口，是一个扩展Bean工厂的接口

HierarchicalBeanFactory：在spring源码中它提供了可以获取父类BeanFactory 的方法，也是为了扩展beanfactory接口的内容，本项目中没有实现相关父工厂的逻辑，故此接口内容为空

AutowireCapableBeanFactory：在spring源码中，这个接口定义了Bean对象的创建，注入等职能，并且提供了对Bean初始化前后进行扩展的方法，这也是两个最重要的方法，applyBeanPostProcessorsBeforeInitialization，applyBeanPostProcessorsAfterInitialization，分别是在Bean的初始化前和初始化后应用BeanPostProcessor

ConfigurableBeanFactory，可获取 BeanPostProcessor、BeanClassLoader等的一个配置化接口

ConfigurableListableBeanFactory，提供分析和修改Bean以及预先实例化的操作接口，不过目前只有一个 getBeanDefinition 方法

回到当前进度

目前的主要目的是设计资源加载模块

首先定义资源加载接口，来获取输入流

~~~java
public interface Resource {

    /**
     * Spring的IO包主要用于处理资源加载流
     * 定义好Resource接口，提供获取InputStream流的方法
     * 接下来再分别实现三种不同的流文件操作：classPath、FileSystem、URL
     * 分别是classPath，系统文件，云配置文件
     */
    InputStream getInputStream() throws IOException;
}
~~~

然后让那三种方式的具体类来实现这个接口

```java
public class ClassPathResource implements Resource {
    
    /**
     * 这一部分的实现是用于通过 ClassLoader 读取 ClassPath 下的文件信息，
     * 具体的读取过程主要是：classLoader.getResourceAsStream(path)
     */
    private final String path;

    private ClassLoader classLoader;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        Assert.notNull(path, "Path must not be null");
        this.path = path;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream inputStream = classLoader.getResourceAsStream(path);
        if (inputStream == null) {
            throw new FileNotFoundException(this.path + "cannot be opened because it does not exist");
        }
        return inputStream;
    }
}
```

核心逻辑就是通过类加载器来加载类路径下的文件，然后返回一个输入流，用户在传类路径的时候肯定不会再自己传递一个类加载器，那么需要写一个classutils，spring源码中也有一个classutils，并且也有一个getDefaultClassLoader方法，获取当前线程的上下文加载器，也就是说创建线程的人可以通过setContextClassLoader()方法将适合的类加载器设置到线程中，那么代码里面就可以getContextClassLoader来获取到这个类加载器，如果不设置的话默认就是系统类加载器

至于说FileSystemResource，它的获取输入流的方式就是直接new一个FileInputStream将文件路径传进去就ok了

最后是云文件资源加载器

~~~java
//云配置文件读取器
public class UrlResource implements Resource {

    private final URL url;

    public UrlResource(URL url) {
        //Assert：hutool工具包中的断言检查，经常用于做变量检查
        Assert.notNull(url, "URL must not be null");
        this.url = url;
    }

    /**
     * 通过 HTTP 的方式读取云服务的文件
     * 可以把文件放在github上
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection connection = this.url.openConnection();
        try {
            return connection.getInputStream();
        } catch (IOException exception) {
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).disconnect();
            }
            throw exception;
        }
    }
}
~~~

至此三大加载器都实现了获取配置文件的输入流的方法，拿到了输入流以后，就可以进行解析处理了

光有一个Reource接口还不够，还需要提供一个接口，可以按照资源加载方式的不同，来选择合适的加载器，那么就提供一个ResourceLoader 接口用来根据传入的参数来获取相应的加载器，那么提供一个getResource方法即可，由于是接口，那么来一个实现类DefaultResourceLoader，根据参数做一个判断，然后new出来相应的资源加载器实现类

~~~java
public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 将三种不同类型的资源处理方式进行了包装，判断分别为classpath，url和file
     * 这里不会让外部调用者知道过多的细节，而只关心具体调用的结果
     */
    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            // substring()光传一个参数，表示从这个位置开始截取，beginIndex
            // 这里面代码逻辑是使用类加载器来获取类路径下的输入流，不需要classpath前缀，注意截断
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                return new FileSystemResource(location);
            }
        }
    }
}
~~~

至此io包中的内容基本实现，然后需要将io包与Bean定义读取接口做一个整合，通过BeanDefinitionReader 接口定义一些方法

~~~java
//Bean定义读取接口
public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    void loadBeanDefinitions(Resource resource) throws BeansException;

    void loadBeanDefinitions(Resource... resources) throws BeansException;

    void loadBeanDefinitions(String location) throws BeansException;

    void loadBeanDefinitions(String... locations) throws BeansException;
}
~~~

BeanDefinitionReader有一个非常重要的实现类，XmlBeanDefinitionReader，当然这个实现类是通过继承AbstractBeanDefinitionReader来间接实现BeanDefinitionReader的

所以这块就是拿到资源流以后去做真正解析处理的架构设计

那么最终对资源的处理实现都是在XmlBeanDefinitionReader中loadBeanDefinitions方法，这个方法又调用doloadBeanDefinitions，这是spring框架一贯的作风哈

具体逻辑就是loadBeanDefinitions传进来的是一个Resource，那么调用Resource的getInputStream获取到了流，然后调用doloadBeanDefinitions传入流，进行真正的解析处理

那么剩下的就交给dom4j的工具类saxreader先读流，返回一个Document对象，利用Document获取顶层Element对象，然后根据名称进行获取解析就ok了

~~~java
 protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();

        // 解析 context:component-scan 标签，扫描包中的类并提取相关信息，用于组装 BeanDefinition
        Element componentScan = root.element("component-scan");
        if (null != componentScan) {
            String scanPath = componentScan.attributeValue("base-package");
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath);
        }

        List<Element> beanList = root.elements("bean");
        for (Element bean : beanList) {

            String id = bean.attributeValue("id");
            String name = bean.attributeValue("name");
            String className = bean.attributeValue("class");
            String initMethod = bean.attributeValue("init-method");
            String destroyMethodName = bean.attributeValue("destroy-method");
            String beanScope = bean.attributeValue("scope");

            // 获取 Class，方便获取类中的名称
            Class<?> clazz = Class.forName(className);
            // 优先级 id > name
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 定义Bean
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethod);
            beanDefinition.setDestroyMethodName(destroyMethodName);

            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            List<Element> propertyList = bean.elements("property");
            // 读取属性并填充
            for (Element property : propertyList) {
                // 解析标签：property
                String attrName = property.attributeValue("name");
                String attrValue = property.attributeValue("value");
                String attrRef = property.attributeValue("ref");
                // 获取属性值：引入对象、值对象
                Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;
                // 创建属性信息
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }
            // 注册 BeanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }
~~~

所有的信息解析完以后要注册到BeanDefinition中，那么就调用BeanDefinitionRegistry的registerBeanDefinition方法将这个封装了所有配置文件关于Bean信息的BeanDefinition put到BeanDefinitionMap中，key是BeanName，value就是这个封装好的BeanDefinition

___

### 实现应用上下文，自动识别，资源加载，扩展机制

观察其他基于spring开发组件或者中间件的源码，一定会有继承或者实现了spring对外暴露的类或者接口，在接口的实现中获取了BeanFactory 以及Bean对象的获取等内容，并对这些内容做一些操作：比如修改Bean的信息，添加日志打印等，处理数据库路由对数据源的切换等

在对容器中的Bean的实例化过程添加扩展机制的时候，还需要把目前关于spring.xml初始化和加载策略进行优化，因为不可能让用户手动的创建DefaultListableBeanFactory对象，然后在创建XmlBeanDefinitionReader调用loadBeanDefinitions这一套繁琐的流程。因为DefaultListableBeanFactory说到底还是提供给spring本身开发而服务的，不太可能提供给用户使用

同时还需要考虑到扩展的问题，比如在一个Bean初始化的过程中，完成对Bean对象的扩展，那么如果不采用上下文的方式，就很难做到自动化处理，所以接下来需要把Bean对象扩展机制功能和对spring框架上下文的包装融合起来，对外提供完整的服务

为了能满足于在Bean对象从注册到实例化的过程中执行用户自定义的操作，就需要在Bean的定义和初始化过程中插入接口，这个接口再有外部去实现自己需要的服务，并且始终结合spring框架上下文的处理能力

满足于对Bean对象扩展的两个接口，其实也是spring框架中非常重量级的两个接口，BeanFactoryPostProcess，BeanPostProcessor，这两个接口也是使用spring框架额外开发自己功能的两个必备接口

BeanFactoryPostProcessor，是由spring框架组件提供的容器扩展机制，允许在Bean对象注册后但尚未实例化前，对Bean的定义信息BeanDefinition执行修改操作

BeanPostProcessor也是spring提供的扩展机制，不过BeanPostProcessor是在Bean对象实例化之后修改Bean对象，也可以替换Bean对象，这部分与AOP有着密切联系

最终的重点是开发一个spring的上下文操作类，把相应的xml加载，注册，实例化以及新增的修改和扩展融合进去，让spring可以自动扫描到新增的服务，便于用户使用

整体实现的起点是以继承了ListableBeanFactory 接口的ApplicationContext 接口开始，扩展出一系列应用上下文的抽象类，完成最终的ClassPathXmlApplicationContext类的实现，这个类就是最终要交给用户使用的类，同时在实现应用上下文的过程中，通过定义接口`BeanFactoryPostProcessor`、`BeanPostProcessor` 两个接口，把关于对Bean的扩展机制串联进去

~~~java
// 允许自定义修改 BeanDefinition 属性信息
// 这个接口提供了允许Bean对象在注册以后，也就是BeanDefinition加载完成以后，
// 实例化之前，对Bean的定义信息BeanDefinition执行修改操作
public interface BeanFactoryPostProcessor {

    /**
     * 在所有的 BeanDefinition 加载完成后，实例化 Bean 对象之前，提供修改 BeanDefinition 属性的机制
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
~~~

这个接口定义与spring源码一模一样，主要是在BeanDefinition加载完成，也就是注册完成，放到了BeanDefinitionMap中以后，对象实例化之前，对BeanDefinition进行一个修改操作

具体玩的话就是创建一个实现类实现这个接口：

~~~java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        propertyValues.addPropertyValue(new PropertyValue("company", "阿里巴巴"));
    }
}
~~~

通过添加PropertyValue来对BeanDefinition做一个扩展

注意这个参数的传递是ConfigurableListableBeanFactory，这个接口继承了三大接口，自己提供了一个

void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);方法，可以把用户实现的BeanFactoryPostProcessor传进来做一个扩展

~~~java
// 对Bean对象扩展的两个接口，也是在使用Spring框架额外新增开发自己组件需求的两个必备接口
// BeanPostProcessor是在Bean对象实例化之后修改Bean对象，也可以替换Bean对象，这部分与后面要实现的AOP功能有密切联系
// Spring源码的描述：提供了修改新实例化Bean对象的扩展点
// BeanFactoryPostProcess、BeanPostProcessor这两个接口非常重要，如果以后要做一些关于 Spring 中间件的开发时，
// 如果需要用到 Bean 对象的获取以及修改一些属性信息，那么就可以使用这两个接口了。
// 同时 BeanPostProcessor 也是实现 AOP 切面技术的关键所在
public interface BeanPostProcessor {

    /**
     * 在 Bean 对象执行初始化方法之前，执行此方法
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在 Bean 对象执行初始化方法之后，执行此方法
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
~~~

BeanPostProcessor接口与spring源码定义也一致，这个接口提供的两个方法分别解决的是Bean实例化以后初始化之前的Bean的修改操作，以及初始化之后的修改

接下来是上下文顶级接口ApplicationContext ，这个接口将spring的整个上下文环境做了一个集成，是一个复杂的集成体，继承了环境接口，资源加载接口，消息发布接口等

```java
// 这个接口的顶级接口是BeanFactory，也就是说他拥有BeanFactory的方法，比如各种重载的getBean()
// 这个接口满足于自动识别，资源加载，容器事件，监听器等功能，同时例如一些国际化支持，单例Bean自动初始化等
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {

    /*
      ApplicationContext是整个容器的基本功能定义接口，继承的顶级接口是BeanFactory，说明容器也是工厂的多态实现
      运用了代理的设计方式，它的实现类相当于持有一个BeanFactory实例，这个实例替它执行BeanFactory接口定义的功能
      虽然spring在ApplicationContext中也声明了BeanFactory接口中的功能,但是Beanfactory实例只是ApplicationContext中的一个属性
      由这个属性来帮助ApplicationContext对外提供beanfactory定义的功能实现
      ApplicationContext是围绕着spring的整体来设计的，从类型上看它虽然是Beanfactoy的实现类，但比beanfactory的功能更加强大，可以
      理解为ApplicationContext接口扩展了Beanfactory接口
      ApplicationContext是一个复杂的集成体，集成了环境接口，beanfactory接口，消息发布接口，配置源信息解析接口
     */
}
```

注意，现在描述的是IOC模块，而它的顶级接口是BeanFactory，说明他是在IOC容器的基础上开始进行扩展了

ApplicationContext接口虽然有很多的实现类，但都不是直接实现它这个接口的，而是ConfigurableApplicationContext这个接口做一个方法的定义，那就是refresh方法，这也是整个spring上下文最重要的方法

~~~java
// refresh方法非常核心，需要在应用上下文中完成刷新容器的操作
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 刷新容器
     */
    void refresh() throws BeansException;

    void registerShutdownHook();

    void close();

}
~~~

ConfigurableApplicationContext将refresh()方法定义好以后，可以预想到这个方法一定是一个非常大的方法，毫不犹豫直接模板方法设计模式，那么就得整个抽象类，AbstractApplicationContext安排上，将refresh方法的实现逻辑与顺序定义好以后，需要其他职责类实现的直接打成protected abstract，交给继承类去实现即可，一些不重要的方法直接在抽象类中做一个实现

~~~java
  @Override
    public void refresh() throws BeansException {
        // 1. 创建 BeanFactory，并加载 BeanDefinition
        refreshBeanFactory();

        // 2. 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3. 添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的 ApplicationContext
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
~~~

这是框架最终的refresh方法，目前阶段还有很多功能没涉及到。

框架这里AbstractApplicationContext 继承了DefaultResourceLoader是为了处理spring.xml配置资源的加载

之后是在refresh定义实现过程：
目前需要的是创建BeanFactory，并加载BeanDefinition

然后获取BeanFactory

然后在执行Bean的实例化之前执行BeanFactoryPostProcessor中的方法

最后BeanPostProcessor 需要提前于其他Bean对象实例化之前执行注册操作

最后提前实例化单例Bean对象

另外把定义出来的抽象方法，refreshBeanFactory()、getBeanFactory() 由后面的继承此抽象类的其他抽象类实现。

上面说了refreshBeanFactory这个抽象方法需要交给一个类去实现，这个在spring源码中就是AbstractRefreshableApplicationContext 。它本身还是一个抽象类，那么一定还有抽象方法需要子类去实现，从这个类的职责可以看出，它的职责是创建Bean工厂并且加载资源，那么加载资源的方法很明显就是交给了另一个抽象类AbstractXmlApplicationContext去实现，从名字也能看出来，每个抽象类专门负责自己的业务，xml开头的负责加载并解析xml文件相关功能。

~~~java
    @Override
    protected void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }
~~~

至于说createBeanFactory方法，实现就是new一个DefaultListableBeanFactory，没什么说的，因为这个BeanFactory是一个集大成的beanfactory，是一个非常的成熟的类，用户都可以直接使用了。。

最终才到了给用户直接使用的那个上下文，也就是ClassPathXmlApplicationContext，思考一下这个类平时怎么用的，是不是直接传一个string类型的地址，然后整个ioc容器就把所有的活（资源配置加载，bean的实例化，初始化等方法执行完了）

~~~java
    /**
     * 从 XML 中加载 BeanDefinition，并刷新上下文
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this.configLocations = configLocations;
        refresh();
    }
~~~

这是这个类的核心方法，其实也就是调用它的爷爷接口中定义的refresh方法，也就是模板抽象类中的那个非常长的方法

至此基本完成了对前面已经实现的ioc容器的功能的整合，也就是都放到了上下文中了，用户现在只需要两行代码就能获取到一个Bean对象。

现在该处理Bean的扩展接口的实现，由于是在Bean的创建时完成前置和后置方法，所以肯定是在AbstractAutowireCapableBeanFactory 中处理。这块的关键方法是 bean = initializeBean(beanName, bean, beanDefinition);这个是执行Bean的初始化方法以及BeanPostProcessor 的前置方法和后置处理方法

~~~java
        // 1. 执行 BeanPostProcessor Before 处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 执行 Bean 对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        // 2. 执行 BeanPostProcessor After 处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
~~~

这就是核心逻辑，可以看到在initializeBean方法中，目前在执行Bean的初始化之前和之后都有方法，把它包围，至于说前置后置方法的实现逻辑，非常简单，拿到所有的BeanPostProcessor，循环调用它的postProcessBeforeInitialization以及postProcessAfterInitialization方法即可，这两个方法是用户自己创建一个类去实现BeanPostProcessor接口，然后实现这两个方法，就可以在Bean初始化前后对bean做扩展了

类流程：（由顶级到用户使用到顺序）

ApplicationContext -> ConfigurableApplicationContext -> AbstractApplicationContext -> AbstractRefreshableApplicationContext -> 

AbstractXmlApplicationContext -> ClassPathXmlApplicationContext

___

### 向虚拟机注册钩子，实现Bean对象的初始化和销毁方法

当我们的类创建Bean对象时，交给spring容器管理后，这个类对象就可以被赋予更多的使用能力，比如上一段已经给对象添加了修改注册的Bean定义但是还未实例化的属性信息的能力，以及对象初始化过程中的前置处理和后置处理，当然在设置属性之前还可以再次对属性进行修改，用的是BeanPostProcessor，这些额外的功能实现，可以让我们对现有工程中的对象做相应的扩展处理

除此以外还希望可以在Bean的初始化过程中，执行一些操作，比如帮我们做一些数据的加载执行，web程序关闭时执行链接断开，内存销毁等操作，也就是xml文件中配置的init-method和destory-method，虽然上面这些操作都可以通过构造函数，静态方法或者手动调用的方式实现，但这样的处理方式始终不如把这些操作统统交给spring处理来的更加舒服。

所以接下来要讨论的需求是：满足用户可以在xml配置文件中配置初始化和销毁的方法，也可以通过实现类的方式来处理，比如在spring源码中就为我们提供了两个接口InitializingBean, DisposableBean ，当然后面还应该有一种通过注解的方式来实现。

在使用spring这种庞然大物的框架的时候，要谨记 框架对外暴露的接口的使用（自己实现）或者通过xml配置的方式，完成了一系列扩展操作，看上去spring很神秘，其实对于这种在Bean初始化过程中额外添加的操作，无非就是预先执行了一个定义好的接口方法或者反射调用类中xml配置好的方法，最终只需要按照接口的定义实现，spring容器在启动的时候就会调用而已

设计思路：在 spring.xml 配置中添加 `init-method、destroy-method` 两个标签内容，在配置文件加载的过程中，把标签内容配置一并定义到 BeanDefinition 的属性当中，这样在 initializeBean 初始化操作的工程中，就可以通过反射的方式来调用配置在 BeanDefinition 中的方法信息了，另外如果是接口实现的方式，那么直接可以通过 Bean 对象调用对应接口定义的方法即可，`((InitializingBean) bean).afterPropertiesSet()`，两种方式达到的效果是一样的

在本项目中一共实现了两种初始化和销毁方法，一种是xml配置，另一种是定义接口，所以这里既有 InitializingBean、DisposableBean 也有需要 XmlBeanDefinitionReader 加载 spring.xml 配置信息到 BeanDefinition 中

另外接口 ConfigurableBeanFactory 定义了 destroySingletons 销毁方法，并由 AbstractBeanFactory 继承的父类 DefaultSingletonBeanRegistry 实现 ConfigurableBeanFactory 接口定义的 destroySingletons 方法

这块比较绕，一般都是用的谁实现接口谁完成实现类，而不是把实现接口的操作又交给继承的父类处理，这块是spring做的一种隔离分层服务的设计方式

另外需要向虚拟机注册钩子函数，保证在虚拟机关闭的时候，执行销毁操作

Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close！")))

~~~java
// 定义初始化方法的接口
// InitializingBean、DisposableBean，两个接口方法还是比较常用的，
// 在一些需要结合 Spring 实现的组件中，经常会使用这两个方法来做一些参数的初始化和销毁操作。
// 比如接口暴漏、数据库数据读取、配置文件加载
public interface InitializingBean {

    /**
     * Bean 处理了属性填充后调用
     *
     * @throws Exception
     */
    void afterPropertiesSet() throws Exception;

}
~~~

```java
// 定义销毁方法的接口
// InitializingBean、DisposableBean，两个接口方法还是比较常用的，
// 在一些需要结合 Spring 实现的组件中，经常会使用这两个方法来做一些参数的初始化和销毁操作。
// 比如接口暴漏、数据库数据读取、配置文件加载
public interface DisposableBean {

    void destroy() throws Exception;

}
```

有了这两个接口后一定要在BeanDefinition 中添加相应的属性，让Bean定义变得逐渐完整。源码中BeanDefinition 是接口，所以一定是相应的get和set方法的定义，如果不是为了扩展方便，图省事的话直接用类就ok了

在BeanDefinition 中添加了这两个属性后，那么从spring.xml文件中解析出来的信息就需要添加进来，同时解析处理还是在XmlBeanDefinitionReader中，需要加上这两项内容的解析处理

由于这个需求是在对Bean进行真正初始化的时候触发的，所以应该对应的是AbstractAutowireCapableBeanFactory 的createBean方法中的doCreateBean中的initializeBean中的invokeInitMethods方法，这个invokeInitMethods方法才是执行对象初始化的方法

~~~java
 private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 实现接口 InitializingBean
        if (bean instanceof InitializingBean) {
            // 如果实现了这个接口，那么需要调用afterPropertiesSet();
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 2. 注解配置 init-method {判断是为了避免二次执行销毁}
        // 如果没有用接口的方式，而是使用xml的方式，那么肯定通过xmlbeandereader读到了beanDefinition中
        // 那么通过beanDefinition获取出来初始化方法的名字，使用反射来调用初始化方法
        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName)) {
            // 通过反射获取这个Bean的Class对象，然后获取指定的方法
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (null == initMethod) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            // Method.invoke() 用来执行目标对象的指定方法
            initMethod.invoke(bean);
        }
    }
~~~

抽象类AbstractAutowireCapableBeanFactory 中的createBean方法是用来创建Bean对象的方法，在这个方法中之前已经通过BeanFactoryPostProcessor、BeanPostProcessor 来进行过扩展，当前是继续完善Bean对象的初始化方法的处理动作

那么在初始化方法中，主要逻辑就是一个是执行实现了 InitializingBean 接口的操作，处理 afterPropertiesSet 方法，另一个是判断配置信息 init-method 是否存在，执行反射调用 initMethod.invoke(bean)。这两种方式都可以在 Bean 对象初始化过程中进行处理加载 Bean 对象中的初始化操作，让使用者可以额外新增加自己想要的动作

至此初始化相关逻辑处理完毕，接下来是销毁方法

这块主要就是整了一个适配器，因为销毁的方法有多种，本项目只写了两种，实现接口DisposableBean和配置文件destroy-method，而这两种销毁方式都是由AbstractApplicationContext 在注册虚拟机钩子后看虚拟机关闭前执行的操作动作，那么再销毁时不太希望看都得销毁哪些类型的方法，使用上希望有一个统一的接口，所以这里用了适配器模式，来做统一处理。对于初始化方法那边一样，我认为也可以用适配器类，只不过初始化的逻辑写在了大的方法里罢了

~~~java
public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;

    private final String beanName;

    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        // 1. 实现接口 DisposableBean
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }

        // 2. 注解配置 destroy-method {判断是为了避免二次执行销毁}
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisposableBean && "destroy".equals(this.destroyMethodName))) {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            if (null == destroyMethod) {
                throw new BeansException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
            }
            destroyMethod.invoke(bean);
        }

    }
}
~~~

那么销毁的逻辑写好后需要在创建Bean的时候进行一个注册，也就是在创建Bean对象的时候，需要把销毁的方法保存起来，方便后续在执行销毁动作的时候使用

~~~java
            // 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
            // 给 Bean 填充属性
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        // 注册实现了 DisposableBean 接口的 Bean 对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

    //这里维护了一个linkedhashmap来存储有销毁方法的Bean
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }
~~~

所以核心代码就是上面的内容，在初始化Bean完成以后，需要注册销毁方法

那么这个销毁方法的具体方法信息，会被注册到 DefaultSingletonBeanRegistry 中新增加的 `Map<String, DisposableBean> disposableBeans` 属性中去，因为这个接口的方法最终可以被类 AbstractApplicationContext 的 close 方法通过 `getBeanFactory().destroySingletons()` 调用

在注册销毁方法的时候，会根据是接口类型和配置类型统一交给 DisposableBeanAdapter 销毁适配器类来做统一处理。*实现了某个接口的类可以被 instanceof 判断或者强转后调用接口方法*

在AbstractApplicationContext 中对ConfigurableApplicationContext接口定义的向虚拟机注册钩子方法做一个实现registerShutdownHook还有一个手动执行关闭的方法close

~~~java
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
~~~

这套逻辑就是销毁方法的实现

~~~java
    // 1.初始化 BeanFactory
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
    applicationContext.registerShutdownHook();      

    // 2. 获取Bean对象调用方法
    UserService userService = applicationContext.getBean("userService", UserService.class);
~~~

___

### 定义标记类型Aware接口，实现感知容器对象

接下要实现的内容是在spring框架中提供一种能感知容器操作的接口，如果谁实现了这样的一个接口，那么就可以获取接口入参中的各类能力，比如BeanNameAware接口就是为了让自身Bean能感受到自己的BeanName，这个BeanName其实就是在xml配置文件中的bean标签中设置的id属性值

思路就是定义一种标记性接口Aware，这个标记接口不需要有任何方法，它只起到标记的作用，而具体的功能由继承此接口的其他功能性接口定义具体的方法，最终这个接口可以通过instanceof的方式进行判断和调用，其实观察spirng源码，所有的xxxAware接口中都只是定义了一个方法，那就是setxxxAware，所以Bean对象可以感知到设置进来的相应的值

在本项目中最终继承Aware接口的接口总共有4个，分别是ApplicationContextAware，BeanClassLoaderAware，BeanNameAware，BeanFactoryAware，这些接口的继承都是为了继承一个标记，有了标记的存在更方便类的操作和具体判断实现。

注意一点：

在spring源码中对于向beanfactory中添加BeanPostProcessor，spring都整合在了refresh方法的prepareBeanFactory(beanFactory);方法中，在这个方法中会把整个beanfactory准备好，也就是会把ApplicationContextAwareProcessor这个实现了BeanPostProcessor的类加入到BeanPostProcessor集合中，这个集合是一个CopyOnWriteArrayList

```java
private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();
```

~~~java
// spring源码
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// Tell the internal bean factory to use the context's class loader etc.
		beanFactory.setBeanClassLoader(getClassLoader());
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

		// Configure the bean factory with context callbacks.
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
~~~

~~~java
// spring源码
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				invokeBeanFactoryPostProcessors(beanFactory);
                。。。。。。省略
~~~

可以看到spring需要对这个beanfactory添加许多的东西，所以在refresh方法中获得到了beanfactory中后单独抽取了一个方法进行准备

本项目中直接在refresh方法中进行添加处理

~~~java
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
     。。。。省略
~~~

接下来步入正轨开始标记接口的实现

很简单的定义四个接口去继承Aware接口，接下来有一个比较的特殊的是使用了一个包装处理器ApplicationContextAwareProcessor

~~~java
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    /**
     * 由于 ApplicationContext 的获取并不能直接在创建 Bean 时候就可以拿到，
     * 所以需要在 refresh 操作时，把 ApplicationContext 写入到一个包装的 BeanPostProcessor 中去，
     * 再由 AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization 方法调用
     */
    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
~~~

由于ApplicationContext 的获取并不能直接在创建Bean的时候就能拿到，所以需要在refresh方法中，把ApplicationContext 写入到一个包装的BeanPostProcessor 中，因为放到了BeanPostProcessor 中，才可以对这个Bean进行一个扩展，然后AbstractAutowireCapableBeanFactory的applyBeanPostProcessorsBeforeInitialization 就会调用BeanPostProcessors中的BeanPostProcessor ，这玩意是一个CopyOnWriteArrayList

包装好了以后需要在refresh方法中进行一个添加操作，上面已经写过了

接下来就是如何感知刚才定义的接口的核心逻辑，感知到了以后做setXXX操作

~~~java
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        // invokeAwareMethods
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }

        // 1. 执行 BeanPostProcessor Before 处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 执行 Bean 对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        // 2. 执行 BeanPostProcessor After 处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        return wrappedBean;
    }
~~~

而后面两个方法其实就是对BeanPostProcessors做一个遍历，然后调用接口的postProcessBeforeInitialization和postProcessAfterInitialization方法

~~~java
 /**
     * BeanPostProcessor的职责就是在Bean的初始化之前和之后执行相应的扩展操作
     * 所谓的扩展其实就是调用我们自己实现BeanPostProcessor接口的类的postProcessBeforeInitialization方法
     * 由于我们对这个方法做了实现，我们就可以自由的对这个Bean的属性做一些操作
     */
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    /**
     * 同上
     * 在Bean的实例化方法之后调用，做相应的扩展
     */
    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }
~~~

通过简单的instanceof就可以通知到实现了此接口的类。另外前面在refresh中还向 BeanPostProcessor 中添加了 ApplicationContextAwareProcessor，此时在这个方法中也会被调用到具体的类实现，得到一个 ApplicationContex 属性，至于其他的感知接口，只要我们这个Bean对象它实现了那些接口，那么通过instanceof都能感知到，并且由于实现了相应的感知接口，那么就得实现那些接口中定义的set方法，所以在我们的Bean对象中会直接做一个设置。那么在initializeBean方法中感知到以后我只要多态的调用set方法，就会转到实现了这个接口的Bean对象，调用它自己的实现就ok了

~~~java
public class UserServiceForAware implements BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    private String uId;

    private String company;

    private String location;

    private UserDao userDao;

    public String queryUserInfo() {
        return userDao.queryUserName(uId) + "在" + location + "的" + company;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("ClassLoader:" + classLoader);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean Name is：" + name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    。。。。。省略
~~~

至此基本实现了Aware接口的感知功能，并做了4个继承接口来测试一下，扩展了spring的功能。

那么现在springBean的生命周期已经比较完整：

xml -> BeanDefinition -> BeanFactoryPostProcessors修改BeanDefinition 信息（BeanFactoryPostProcessor接口的作用是加载完BeanDefinition 信息后，实例化之前，对BeanDefinition 进行修改）-> Bean实例化 -> 在设置Bean属性值之前，允许允许 BeanPostProcessor 修改属性值 -> 给Bean填充属性 -> Bean初始化（initializeBean）（首先Aware感知，然后执行初始化之前的扩展，然后真正执行初始化方法，然后执行初始化之后的扩展方法）-> 使用 -> 执行Bean的销毁方法

____

### 关于Bean的作用域以及FactoryBean的实现

前言：在使用spring整合mybatis框架的时候，它的核心作用是可以满足用户不需要实现Dao接口类，就可以通过xml或者注解的方式完成对数据库的CRUD操作，那么在实现这样的ORM框架中，把一个数据库操作的对象交给Spring管理就用到了自定义创建Bean对象的逻辑。

因为我们在使用 Spring、MyBatis 框架的时候都可以知道，并没有手动的去创建任何操作数据库的 Bean 对象，有的仅仅是一个接口定义，而这个接口定义竟然可以被注入到其他需要使用 Dao 的属性中去了，那么这一过程最核心待解决的问题，就是需要完成把复杂且以代理方式动态变化的对象，注册到 Spring 容器中。

关于提供一个能让使用者定义复杂的Bean对象的功能，功能点非常不错，意义也很大，因为这样做了以后spring的生态才可以成立，别的框架都可以在此标准上完成自己服务的接入

在整个spring框架的开发过程中其实已经提供了各种扩展功能的插入点，所以每扩展一项新的功能就是在合适的位置提供一个插入点接口，然后来实现相应的逻辑即可，像这里的目标实现就是对外提供一个可以二次从FactoryBean 的getObject方法中获取对象的功能，这样所有实现此接口的类，就可以扩充自己对象的功能了

最著名的案例比如mybatis就是实现了一个MapperFactoryBean类，在*getObject* 中提供*SqlSession* 对执行CRUD方法的操作

另外当前还需要实现的一个内容是单例对象和原型对象，单例还是原型主要实现的区别是单例对象创建完成以后会放到内存中，也就是单例池中，而原型不会放入，所以每次获取都会重新创建对象

AbstractAutowireCapableBeanFactory的createBean方法执行对象创建，属性填充，依赖加载，前置后置处理，初始化方法等操作，现在除了这些操作以后，还需要在最后判断是否是单例对象，来决定加不加到内存中。

另外还需要判断整个对象是不是一个FactoryBean对象，如果是这样的对象，就需要继续执行获取FactoryBean 具体对象的getObject方法，整个 getBean 过程中都会新增一个单例类型的判断factory.isSingleton()，用于决定是否使用内存存放对象信息。

实现过程：

首先加入了单例Bean与原型Bean的逻辑，那么在BeanDefinition 中就得加上相应的属性，从而可以从xml文件中解析得来的数据做一个设置

其次在AbstractAutowireCapableBeanFactory 的createBean方法中（doCreateBean）中添加判断单例的逻辑，来决定个是否加入内存

~~~java
	  if (beanDefinition.isSingleton()) {
            // 获取代理对象
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
        return exposedObject;
~~~

另外在注册销毁方法时，如果是非单例Bean，那么不需要注册

~~~java
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 非 Singleton 类型的 Bean 不执行销毁方法
        if (!beanDefinition.isSingleton()) return;

        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }
~~~

至此单例bean与原型bean的逻辑实现完毕

接下来处理FactoryBean 业务：

首先按照spring源码的定义，来创建一个FactoryBean接口

~~~java
public interface FactoryBean<T> {

    /**
     * 获取对象
     */
    T getObject() throws Exception;
    /**
     * 获取类型
     */
    Class<?> getObjectType();
    /**
     * 判断是否为单例Bean
     */
    boolean isSingleton();
}
~~~

然后实现一个FactoryBean的注册服务，FactoryBeanRegistrySupport 类主要处理的就是关于FactoryBean 类对象的注册操作，之所以抽取成单独的类，就是希望不同领域模块下只负责各自需要完成的功能，避免因为扩展导致类膨胀，同时也加上了缓存操作factoryBeanObjectCache，如果是单例类型的FactoryBean，那么需要放到缓存中。doGetObjectFromFactoryBean 是具体的获取 FactoryBean#getObject() 方法，因为既有缓存的处理也有对象的获取，所以额外提供了 getObjectFromFactoryBean 进行逻辑包装

~~~java
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    /**
     * Cache of singleton objects created by FactoryBeans: FactoryBean name --> object
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    protected Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return (object != NULL_OBJECT ? object : null);
    }

    protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName) {
        if (factory.isSingleton()) {
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = doGetObjectFromFactoryBean(factory, beanName);
                this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
            return (object != NULL_OBJECT ? object : null);
        } else {
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }

    private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName){
        try {
            //最终相当于你调用getBean方法返回了一个factory.getObject();getObject()这里面就可以做文章了，比如动态代理
            return factory.getObject();
        } catch (Exception e) {
            throw new BeansException("FactoryBean threw exception on object[" + beanName + "] creation", e);
        }
    }
}
~~~

接下来需要在获取对象的时候扩展创建对象的逻辑

~~~java
    protected <T> T doGetBean(final String name, final Object[] args) {
        //从缓存中获取单例工厂中的objectFactory单例
        Object sharedInstance = getSingleton(name);
        if (sharedInstance != null) {
            // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
            return (T) getObjectForBeanInstance(sharedInstance, name);
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        //刚开始创建的时候，单例池中为空，一二三级缓存全部获取一遍都获取不到，上面判空后来到这里
        //进行Bean的创建
        Object bean = createBean(name, beanDefinition, args);
        return (T) getObjectForBeanInstance(bean, name);
    }
~~~

~~~java
    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        //从单例池中获取的对象，如果不为空，直接到这个方法了，然后再次判断是不是工厂bean，不是工厂bean的话那么getBean到此结束
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        //从缓存中获取工厂Bean
        Object object = getCachedObjectForFactoryBean(beanName);

        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }

        return object;
    }
~~~

因为需要扩展FactoryBean 对象的能力，所以在一条链路上截出一段来处理额外的服务，然后再把链条接上

FactoryBean 补充：

~~~wiki
一般情况下，spring通过反射机制利用Bean标签中的class属性来通过反射获取Class对象，然后进行实例化，这是simple实例化策略，在某些情况下，实例化Bean过程比较复杂，如果按照传统方式，则需要在bean中配置大量信息，spring提供了一种编码方式来创建复杂对象，那就是FactoryBean接口，用户可以实现这个接口来定制的实例化Bean的逻辑。
以Bean结尾，表示它还是一个普通的Bean对象，只不过他是实现了FactoryBean接口的Bean，FactoryBean接口是一个泛型接口，getObject方法返回的就是泛型中填入的类型，那么根据BeanName获取的Bean实际上是获取FactoryBean的getObject()返回的对象。注意这里说的BeanName其实就是标签中填入的id属性

FactoryBean是一个接口，当在IOC容器中注册的Bean实现了FactoryBean后，通过getBean(String BeanName)获取到的Bean对象并不是FactoryBean的实现类对象，而是这个实现类中的getObject()方法返回的对象。要想获取FactoryBean的实现类，就要getBean(&BeanName)，在BeanName之前加上&

一般的玩法是可用通过这个getobject来做动态代理，返回一个代理对象，做一些扩展操作
~~~

____

### 基于观察者模式，实现容器事件和事件监听器

spring中有一个Event事件，他可以提供事件的定义，发布以及监听事件来完成一些自定义的动作。比如可以定义一个新用户注册的事件，当有用户执行注册完成以后，在事件监听中给用户发送一些优惠券和短信提醒，这样的操作就可以把属于基本功能的注册和对应的策略服务（策略服务是经常变换的，比如每个时间段注册的用户应该发送的优惠券是不一样的）解耦出来，降低系统的耦合，策略的改变不影响主要业务，也就是不变的业务流程。就像业务代码中使用消息队列一样，做一个服务的解耦，以及保证个别业务逻辑的最终一致性。

目前需要实现这个功能，可以参照spring的实现，其实事件的这种设计本身就是一种观察者模式，他所要解决的就是一个对象状态改变后给其他对象通知的问题，而且要考虑易用和低耦合，保证高度的协作

在功能实现上我们需要定义出事件类，事件监听，事件发布，而这些类的功能需要结合到spring的AbstractApplicationContext的refresh方法，以便于处理事件初始化和注册事件监听器的操作，这些操作应该整合到spring容器的生命周期中。

在整个功能的实现中，仍然需要在面向用户使用的应用上下文中AbstractApplicationContext中添加相应的事件，包括初始化事件发布者，注册事件监听器，发布容器刷新事件等

使用观察者模式定义事件类，监听类，发布类，同时还需要完成一个广播器的功能，接收到事件的推送以后进行分析处理符合监听事件接收者感兴趣的事情，也就是使用isAssignableFrom 来进行判断

isAssignableFrom 和 instanceof 相似，只不过isAssignableFrom是用来判断子类和父类的关系的，或者接口的实现类和接口的关系，默认所有的类的终极父类都是object类。如果A.isAssignableFrom(B)结果是true，证明B可以转换成A，也就是A可以由B转换而来

~~~java
    @Test
    public void testIsAssignableFrom() {
        // A可以由B转换而来，描述的是继承关系，或者接口与接口实现类的关系
        // 超类可以由任意类转换而来，或者可以理解为 判断A是B的父类
        System.out.println(Object.class.isAssignableFrom(UserService.class)); //true
        // B实现了A这个接口
        System.out.println(ITest.class.isAssignableFrom(ITestImpl.class)); //true
    }
~~~

接下来开始具体的事件实现：

~~~wiki
整体spring中是围绕event事件定义，发布，监听功能实现和把事件的相关内容使用AbstractApplicationContext的refresh方法进行注册和处理
在实现的过程中主要以扩展spring context包为主，事件的实现也是在这个包下进行扩展的，所以目前所有的实现内容还是以IOC容器为主
XXXApplicationContext容器都要继承事件发布接口ApplicationEventPublisher，并在实现类中提供事件监听功能ApplicationEventMulticaster 接口是注册监听器和发布事件的广播器，提供添加，移除和发布事件的功能
最后是发布容器关闭事件，这个仍然需要扩展到AbstractApplicationContext的close方法，由注册到虚拟机的钩子来实现
~~~



~~~java
public abstract class ApplicationEvent extends EventObject {

    public ApplicationEvent(Object source) {
        super(source);
    }
}
~~~

spring源码中这个类还加了时间戳，当通过构造方法创建的时候就会创建出一个时间，继承了JDK中的EventObject，所以可以定义出一个具备事件功能的抽象类，后续所有有关事件的类都需要继承这个类

~~~java
//ApplicationContextEvent是定义事件的抽象类，所有事件包括关闭，刷新，以及用户自己实现的事件，都需要继承这个类
public class ApplicationContextEvent extends ApplicationEvent {

    public ApplicationContextEvent(Object source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        // eventobject 中的方法 getSource()
        return (ApplicationContext) getSource();
    }
}
~~~

这个类与spring源码中完全一样,后面的XXevent都是继承ApplicationContextEvent，这些类中都只有一个构造方法，来把source 委派到父类，父类继续往上委派，也就是调用super方法

~~~java
//监听关闭
public class ContextClosedEvent extends ApplicationContextEvent {

    public ContextClosedEvent(Object source) {
        super(source);
    }

}
~~~

ApplicationEvent是定义事件的抽象类，所有的事件包括关闭，刷新以及用户自己实现的事件，都需要继承这个类

ContextClosedEvent、ContextRefreshedEvent，分别是 Spring 框架自己实现的两个事件类，可以用于监听刷新和关闭动作

~~~java
// 事件广播器，定义了添加事件监听和删除监听以及一个广播方法multicastEvent
// 最终推送事件要经过这个multicastEvent方法来处理推送该事件
public interface ApplicationEventMulticaster {


    void addApplicationListener(ApplicationListener<?> listener);


    void removeApplicationListener(ApplicationListener<?> listener);


    void multicastEvent(ApplicationEvent event);
}

~~~

ApplicationEventMulticaster事件广播器，这里只定义了添加监听，移除监听和广播的方法，spring中除了这些方法以外还有addApplicationListenerBean等这些带Bean的方法。广播事件方法 multicastEvent 最终推送事件消息也会经过这个接口方法来处理该接收的事件

接下来是AbstractApplicationEventMulticaster 这个抽象类，它是对事件广播器的公用方法的提取，在这个类中可以实现一些基本功能，避免直接实现接口的类需要实现过多的方法

~~~java
/*
   AbstractApplicationEventMulticaster是对事件广播器的公用方法提取，在这个类中可以实现一些基本功能，避免所有直接
   实现接口方还需要处理细节
   除了像 addApplicationListener、removeApplicationListener，这样的通用方法，
   这里这个类中主要是对 getApplicationListeners 和 supportsEvent 的处理
   getApplicationListeners()方法主要是摘取符合广播事件中的监听处理器，具体过滤动作在supportsEvent()方法中
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    private BeanFactory beanFactory;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Return a Collection of ApplicationListeners matching the given
     * event type. Non-matching listeners get excluded early.
     * @param event the event to be propagated. Allows for excluding
     * non-matching listeners early, based on cached matching information.
     * @return a Collection of ApplicationListeners
     * @see cn.bugstack.springframework.context.ApplicationListener
     */
    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
        LinkedList<ApplicationListener> allListeners = new LinkedList<ApplicationListener>();
        for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
            if (supportsEvent(listener, event)) allListeners.add(listener);
        }
        return allListeners;
    }

    /**
     * 监听器是否对该事件感兴趣
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
        Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();

        // 按照 CglibSubclassingInstantiationStrategy、SimpleInstantiationStrategy 不同的实例化类型，需要判断后获取目标 class
        // 如果是cglib的实例化策略，那么需要获取父类的Class，而simple实例化则不需要
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
        Type genericInterface = targetClass.getGenericInterfaces()[0];

        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClassName;
        try {
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("wrong event class name: " + className);
        }
        // 判定此 eventClassName 对象所表示的类或接口与指定的 event.getClass() 参数所表示的类或接口是否相同，或是否是其超类或超接口。
        // isAssignableFrom是用来判断子类和父类的关系的，或者接口的实现类和接口的关系的，
        // 默认所有的类的终极父类都是Object。如果A.isAssignableFrom(B)结果是true，证明B可以转换成为A,也就是A可以由B转换而来。
        return eventClassName.isAssignableFrom(event.getClass());
    }

}
~~~

除了像 addApplicationListener、removeApplicationListener，这样的通用方法，这里这个类中主要是对 getApplicationListeners 和 supportsEvent 的处理。

getApplicationListeners 方法主要是摘取符合广播事件中的监听处理器，具体过滤动作在 supportsEvent 方法中

在 supportsEvent 方法中，主要包括对Cglib、Simple不同实例化需要获取目标Class，Cglib代理类需要获取父类的Class，普通实例化的不需要。接下来就是通过提取接口和对应的 ParameterizedType 和 eventClassName，方便最后确认是否为子类和父类的关系，以此证明此事件归这个符合的类处理。

接下来创建事件发布者的定义和实现：

~~~java
// 是整个事件的发布接口，所有的事件都需要从这个接口发布出去
public interface ApplicationEventPublisher {

    void publishEvent(ApplicationEvent event);
}

~~~

接下来在AbstractApplicationContext 的refresh方法中将注册监听器等业务逻辑加到ioc容器的创建周期中

~~~java
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
~~~

~~~java
    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }
~~~

所谓的初始化事件发布者，也就是创建一个SimpleApplicationEventMulticaster，一个事件广播器，其实就是一个普通的Bean对象，然后把这个Bean对象注册到单例池中

~~~java
    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : applicationListeners) {
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }
~~~

注册事件监听器的原理就是获取到所有的监听器Bean对象，根据getBeansOfType(ApplicationListener.class)这种方式来获取实现了监听器接口的Bean对象，然后把这些监听者加到容器中。

最终finishRefresh（）完成容器的刷新，其实就是发布一个容器启动完成的事件

总结：

在抽象应用上下文中的refresh方法，主要新增了初始化事件发布者，注册事件监听器，发布容器刷新完成事件，三个方法用于处理事件操作

初始化事件发布者（initApplicationEventMulticaster），主要用于实例化一个SimpleApplicationEventMulticaster，这是一个事件广播器

注册事件监听器（registerListeners）通过getBeansOfType 的方式获取到所有从spring.xml中加载到的事件配置对象

发布容器刷新事件（finishRefresh）发布了第一个服务器启动完成后的事件，这个事件publishEvent 发布出去，其实也就是调用了applicationEventMulticaster的multicastEvent方法

最后一个close方法中，发布了一个容器关闭事件 publishEvent(new ContextClosedEvent(this));

~~~java
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
~~~

也就是向虚拟注册一个钩子，在虚拟机关闭的时候发布一个容器关闭事件，然后对单例Bean进行一个销毁

___

# AOP模块

### 基于JDK动态代理和CGlib动态代理，实现AOP的核心功能

AOP意味着面向切面编程，通过预编译的方式和运行期间动态代理实现程序功能的统一维护，其实AOP也是OOP的延续，使用aop可以对业务逻辑的各个部分进行隔离，从而使各模块间的业务逻辑耦合度降低，提高代码的复用度，也能提高开发效率

关于aop的核心技术实现主要是动态代理的使用，比如给一个接口的实现类，使用jdk动态代理的方式替换掉这个实现类，使用代理类来处理所需要的逻辑

思路有了以后还要考虑的问题是：怎么给方法做代理，而不是代理类，另外怎么去代理所有符合某些规则的类中的方法，如果可以替换掉所有类的符合规则的方法，那么就可以做一个方法粒度的拦截，给所有被代理的方法添加上一些自定义处理，比如打印日志，记录耗时，监控异常等

整理一下aop解决的主要问题：

1.如何给符合规则的方法做代理

2.做完代理方法的案例后，把类的职责拆分出来

这两个功能的实现，都是以切面的思想进行设计和开发。

那么在使用spring的aop的时候，只处理一些需要被拦截的方法，在拦截方法后，执行你对方法的扩展操作，所以就需要先实现一个可以代理方法的proxy，其实代理方法主要是使用到方法拦截器类处理方法的调用MethodInterceptor#invoke，而不是直接使用动态代理中的invoke方法的入参method 进行method.invoke(targetObj, args)，这是需要注意的一点差别

除了上面的核心功能（方法代理）外，还需要使用到 org.aspectj.weaver.tools.PointcutParser处理拦截表达式，"execution(* com.duanxu.lijiale-spring.test.bean.IUserService.*(..))"，有了方法代理和方法拦截外，就可以设计出一个aop雏形

~~~wiki
AspectJExpressionPointcut的核心功能主要依赖于aspectj 组件并处理Pointcut、ClassFilter,、MethodMatcher 接口实现，专门用来处理类和方法的匹配过滤操作
AopProxy是代理的抽象对象，它的主要实现基于 JDK 的代理和 Cglib 代理。在前面的策略模式实例化bean功能中，也用到了cglib
~~~

~~~java
@Test
public void test_proxy_method() {
    // 目标对象(可以替换成任何的目标对象)
    Object targetObj = new UserService();
    // AOP 代理
    IUserService proxy = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), targetObj.getClass().getInterfaces(), new InvocationHandler() {
        // 方法匹配器
        MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* cn.bugstack.springframework.test.bean.IUserService.*(..))");
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodMatcher.matches(method, targetObj.getClass())) {
                // 方法拦截器
                MethodInterceptor methodInterceptor = invocation -> {
                    long start = System.currentTimeMillis();
                    try {
                        return invocation.proceed();
                    } finally {
                        System.out.println("监控 - Begin By AOP");
                        System.out.println("方法名称：" + invocation.getMethod().getName());
                        System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                        System.out.println("监控 - End\r\n");
                    }
                };
                // 反射调用
                return methodInterceptor.invoke(new ReflectiveMethodInvocation(targetObj, method, args));
            }
            return method.invoke(targetObj, args);
        }
    });
    String result = proxy.queryUserInfo();
    System.out.println("测试结果：" + result);
}
~~~

上面的案例中把UserService 当成目标对象，对类中的所有方法进行拦截添加监控信息打印处理

从案例中可以看到有代理的实现 Proxy.newProxyInstance，有方法的匹配 MethodMatcher，有反射的调用 invoke(Object proxy, Method method, Object[] args)，也用用户自己拦截方法后的操作

这就是AOP应该做到事，spring就是把这种功能整合起来罢了，而不是每次使用的时候都需要自己写这些冗余的代码

接下来是具体的实现：

~~~java
// 切入点接口，定义用于获取 ClassFilter、MethodMatcher 的两个类，这两个接口获取的都是切点表达式提供的内容
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();

}
~~~

与spring源码中的定义基本一致

~~~java
public interface ClassFilter {

    boolean matches(Class<?> clazz);
}

~~~

这个接口是用来做类匹配或者接口匹配的，叫过滤也行，与源码定义保持一致

~~~java
public interface MethodMatcher {

    boolean matches(Method method, Class<?> targetClass);
    
}
~~~

方法匹配，找到表达式范围内匹配出来的目标类中的目标方法

接下来需要实现切点表达式：

~~~java
// 实现切点表达式类
// 切点表达式实现了Pointcut、ClassFilter、MethodMatcher三个接口定义方法，
// 同时这个类主要是对 aspectj 包提供的表达式校验方法的使用
@SuppressWarnings("all")
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    private final PointcutExpression pointcutExpression;

    public AspectJExpressionPointcut(String expression) {
        PointcutParser pointcutParser = PointcutParser.
                getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution
                        (SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());
        pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

}
~~~

对于实现的两个过滤匹配接口的match方法的实现，都使用aspectj包中提供的表达式校验方法

现在可以对这个表达式验证类做单独的测试：

~~~java
@Test
public void test_aop() throws NoSuchMethodException {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* com.duanxun.lijiale-spring.test.bean.UserService.*(..))");
    Class<UserService> clazz = UserService.class;
    Method method = clazz.getDeclaredMethod("queryUserInfo");   

    System.out.println(pointcut.matches(clazz));// true
    System.out.println(pointcut.matches(method, clazz));// true    
    
}
~~~

至此就可以使用aspectj的切点表达式来描述我们需要拦截哪些类中的哪些方法

接下来需要对切面进行一个包装通知：

~~~java
// 包装切面通知信息
// AdvisedSupport，主要是用于把代理、拦截、匹配的各项属性包装到一个类中，方便在 Proxy 实现类进行使用
// 这和业务开发中包装入参是一个道理
public class AdvisedSupport {

    /*
      AdvisedSupport主要用于管理advisor
      spring在创建代理的过程中依赖AdvisedSupport，即在执行代理时也需要这个属性，因为创建本身就是为执行做准备的
      从设置的职责来看，无论是jdk代理还是cglib代理都依赖advisor和advice，advice是最小粒度，spring代理都是围绕他们创建的
     */

    // ProxyConfig
    private boolean proxyTargetClass = false;

    // 被代理的目标对象
    private TargetSource targetSource;

    // 方法拦截器
    private MethodInterceptor methodInterceptor;

    // 方法匹配器(检查目标方法是否符合通知条件)
    private MethodMatcher methodMatcher;
    
    省略get/set方法
~~~

这个类用于把跟aop有关的那些属性，比如代理，拦截，匹配的各项属性包装到一个类中，方便在proxy类中进行使用。

TargetSource：是一个目标对象，在目标对象类中提供object入参属性，以及获取目标类TargetClass 信息

MethodInterceptor：是一个具体拦截方法的实现类，由用户自己实现MethodInterceptor#invoke方法，做具体的处理

MethodMatcher：是一个方法匹配操作，这个对象由AspectJExpressionPointcut 提供服务

接下来将做代理的抽象实现：

~~~java
public interface AopProxy {

    Object getProxy();

}
~~~

定义一个标准接口，用于获取代理类，因为具体的实现可以有JDK动态代理，也可以是CGlib动态代理，所以定义接口会更加方便管理

JDK动态代理：

~~~java
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                advised.getTargetSource().getTargetClass(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            return methodInterceptor
                    .invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        }
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }

}
~~~

invoke方法相当于嵌套代理，先拿到匹配的方法后，使用用户自己提供的方法拦截实现，做反射调用，methodInterceptor.invoke

这里还有一个 ReflectiveMethodInvocation，其他它就是一个入参的包装信息，提供了入参对象：目标对象、方法、入参

CGlib动态代理：

~~~java
public class Cglib2AopProxy implements AopProxy {

    private final AdvisedSupport advised;

    public Cglib2AopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return enhancer.create();
    }

    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation
                    (advised.getTargetSource().getTarget(), method, objects, methodProxy);
            if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                return advised.getMethodInterceptor().invoke(methodInvocation);
            }
            return methodInvocation.proceed();
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.target, this.arguments);
        }

    }
}
~~~

基于cglib使用enhancer代理的类可以在运行期间为接口使用ASM字节码增强技术处理对象的代理对象生成，因此被代理类不需要实现任何接口

关于扩展进去的用户拦截方法，主要是在 Enhancer#setCallback 中处理，用户自己的新增的拦截处理。这里可以看到 DynamicAdvisedInterceptor#intercept 匹配方法后做了相应的反射操作

~~~java
@Test
public void test_AOP() {
    // 目标对象
    IUserService userService = new UserService();     

    // 组装代理信息
    AdvisedSupport advisedSupport = new AdvisedSupport();
    advisedSupport.setTargetSource(new TargetSource(userService));
    advisedSupport.setMethodInterceptor(new UserServiceInterceptor());
    advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(*com.duanxu.lijiale-spring.test.bean.IUserService.*(..))"));
    
    // 代理对象(JdkDynamicAopProxy)
    IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advisedSupport).getProxy();
    // 测试调用
    System.out.println("测试结果：" + proxy_jdk.queryUserInfo());
    
    // 代理对象(Cglib2AopProxy)
    IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
    // 测试调用
    System.out.println("测试结果：" + proxy_cglib.register("花花"));
}
~~~

这就是基本模拟使用了aop的代码，在JdkDynamicAopProxy中，通过jdk动态代理创建代理对象，然后调用invoke方法，在invoke方法中，如果满足切点表达式，那么调用methodInterceptor.invoke方法，如果不满足切点表达式匹配，那么就普通的调用jdk动态代理的方法invoke方法正常执行目标方法就行了

cglib也一样如果匹配失败就正常使用cglib的代理类调用方式methodProxy.invoke，匹配成功了使用自己的拦截器

___

### 把AOP融入到Bean的生命周期

在前面通过基于JDK动态代理和CGlib动态代理，处理方法匹配和方法拦截，对匹配的对象进行自定义的处理操作，并把这样的技术核心内容拆解到spring中，用于实现AOP部分，通过拆分基本可以明确各个类的职责，包括代理目标的对象属性，拦截器属性，方法匹配属性等，以及两种不同的代理操作

再有了一个AOP的核心功能后，我们可以通过上面大量的编码来验证切面功能对方法的拦截，但是不太可能让用户去使用这种方式去单独的使用aop，而应该与spring结合起来，最终完成aop功能

目前需要解决的问题是怎么借助BeanPostProcessor 把动态代理融入到Bean的生命周期，以及如何组装各项切点，拦截，前置的功能和适配对应的代理器

为了可以让对象创建过程中，能把xml配置的代理对象也就是切面的一些类对象实例化，就需要用到BeanPostProcessor提供的方法，因为这个类中的方法可以分别作用于Bean对象执行初始化前和执行初始化后来修改Bean对象的扩展信息。

但因为创建的是代理对象而不是之前流程里的普通对象，所以需要前置于其他对象的创建，所以在实际开发过程中，需要在AbstractAutowireCapableBeanFactory#createBean 优先完成Bean对象的判断，是否需要代理，有则直接返回代理对象，在Spring的源码中会有 createBean 和 doCreateBean 的方法拆分

这里还包括要解决方法拦截器的具体功能，提供一些 BeforeAdvice、AfterAdvice 的实现，让用户可以更简化的使用切面功能。除此之外还包括需要包装切面表达式以及拦截方法的整合，以及提供不同类型的代理方式的代理工厂，来包装我们的切面服务

创建一个接口InstantiationAwareBeanPostProcessor继承BeanPostProcessor，对BeanPostProcessor接口来一个扩展

然后创建一个自动代理创建的类DefaultAdvisorAutoProxyCreator，这个类就是用于处理整个AOP代理融入到Bean生命周期的核心类，DefaultAdvisorAutoProxyCreator 会依赖于拦截器，代理工厂和pointcut与Advisor的包装服务。

spring的aop把Advice 细化为了BeforeAdvice、AfterAdvice、AfterReturningAdvice、ThrowsAdvice等，本项目只使用到了BeforeAdvice

接下来是具体的实现：

~~~java
public interface BeforeAdvice extends Advice {

}
~~~

创建一个空接口来继承aopalliance包中提供的advice接口，aop包中的Advice接口也是一个空接口

~~~java
public interface MethodBeforeAdvice extends BeforeAdvice{

    void before(Method method, Object[] args, Object target) throws Throwable;

}
~~~

在spring框架中，Advice都是通过方法拦截器MethodInterceptor实现的。

定义Advisor 访问者：

~~~java
public interface Advisor {

    Advice getAdvice();

}
~~~

这个Advice是aopalliance包提供的

~~~java
public interface PointcutAdvisor extends Advisor{

    Pointcut getPointcut();

}
~~~

Advisor可以获取Advice，并且还是PointcutAdvisor的父类，所以Advisor承担了pointcut和advice的组合，pointcut用于获取joinpoint，而advice决定于joinpoint执行什么操作

~~~java
// AspectJExpressionPointcutAdvisor 实现了 PointcutAdvisor 接口，把切面 pointcut、
// 拦截方法 advice 和具体的拦截表达式包装在一起。
// 这样就可以在xml的配置中定义一个pointcutAdvisor切面拦截器了
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    // 切面
    private AspectJExpressionPointcut pointcut;

    // 具体的拦截方法
    private Advice advice;

    // 表达式
    private String expression;

    public void setExpression(String expression){
        this.expression = expression;
    }

    @Override
    public Pointcut getPointcut() {
        if (null == pointcut) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice){
        this.advice = advice;
    }

}
~~~

相当于使用一个类来将切面（pointcut），拦截方法（advice ）和具体的拦截表达式（AspectJExpressionPointcut）包装在一起，这样就可以在xml中定义一个pointcutAdvisor 切面拦截器来完成所有的包装

~~~java
public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor() {
    }

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.advice.before(methodInvocation.getMethod(),
                methodInvocation.getArguments(), methodInvocation.getThis());
        return methodInvocation.proceed();
    }

    public MethodBeforeAdvice getAdvice() {
        return advice;
    }

    public void setAdvice(MethodBeforeAdvice advice) {
        this.advice = advice;
    }
}
~~~

MethodBeforeAdviceInterceptor 实现了 MethodInterceptor 接口，在 invoke 方法中调用 advice 中的 before 方法，传入对应的参数信息而这个 advice.before 则是用于自己实现 MethodBeforeAdvice 接口后做的相应处理。

所以我们现在可以自己写一个实现类来实现MethodBeforeAdvice接口，重写before方法， 在before方法中定义自己的业务逻辑，那么当调用invoke方法的时候，会先调用我们实现的before方法，然后才执行真正的业务方法

~~~java
public class ProxyFactory {

    private AdvisedSupport advisedSupport;

    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        if (advisedSupport.isProxyTargetClass()) {
            return new Cglib2AopProxy(advisedSupport);
        }
        return new JdkDynamicAopProxy(advisedSupport);
    }

}
~~~

代理工厂，主要解决关于jdk和cglib两种代理的选择问题，有了代理工厂就可以按照不同的创建需求进行控制

接下来需要将这些内容融入到Bean的生命周期

~~~java
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

        if (isInfrastructureClass(beanClass)) return null;

        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(beanClass)) continue;

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = null;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            advisedSupport.setTargetSource(targetSource);
            advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            advisedSupport.setProxyTargetClass(false);

            return new ProxyFactory(advisedSupport).getProxy();

        }

        return null;
    }
    
}
~~~

这个DefaultAdvisorAutoProxyCreator 类的主要核心实现在于postProcessBeforeInstantiation ，在实例化之前做一些操作，从通过 beanFactory.getBeansOfType 获取 AspectJExpressionPointcutAdvisor 开始

获取了 advisors 以后就可以遍历相应的 AspectJExpressionPointcutAdvisor 填充对应的属性信息，包括：目标对象、拦截方法、匹配器，之后返回代理对象即可

那么现在调用方获取到的这个 Bean 对象就是一个已经被切面注入的对象了，当调用方法的时候，则会被按需拦截，处理用户需要的信息

测试的仅需要自己实现MethodBeforeAdvice接口，实现before拦截方法

~~~java
public class UserServiceBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("拦截方法：" + method.getName());
    }

}
~~~

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="com.learn.test.bean.unit16.UserService">
        <property name="token" value="whyNot2alibaba?"/>
    </bean>

    <bean class="com.learn.myspring.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>

    <bean id="beforeAdvice" class="com.learn.test.bean.unit16.UserServiceBeforeAdvice"/>

    <bean id="methodInterceptor" class="com.learn.myspring.aop.framework.adapter.MethodBeforeAdviceInterceptor">
        <property name="advice" ref="beforeAdvice"/>
    </bean>

    <bean id="pointcutAdvisor" class="com.learn.myspring.aop.aspectj.AspectJExpressionPointcutAdvisor">
        <property name="expression" value="execution(* com.learn.test.bean.unit16.IUserService.*(..))"/>
        <property name="advice" ref="methodInterceptor"/>
    </bean>

</beans>
~~~

这回使用aop就像spring中一样了，通过在xml中配置即可，因为已经把aop的功能融合到Bean的生命周期中了，新增的拦截方法都会被自动处理

目前实现了AOP功能的外在表现主要是把以前自己在单元测试中的切面拦截，交给spring的xml去配置，那么这里非常重要的一个点就是：就是把相应的功能如何与springbean的生命周期结合起来，可以使用BeanPostProcessor，因为他可以解决在bean对象执行初始化方法之前，用于修改新实例化的bean对象的扩展点，所以可以在这块处理自己的aop代理对象逻辑了

____

# 自定义注解模块



### 通过注解配置和包自动扫描的方式Bean对象的注册

目前ioc与aop的核心功能都已经实现，只不过不论是用什么都还是在xml文件中一个一个配置，但是目前使用spring肯定会结合各种注解来减少xml配置，做到更简化的使用

这其中就包括：包的扫描注册，注解配置的使用，占位符属性填充等，所以现在的目标就是在目前的核心逻辑上填充一些自动化的功能。

首先思考：为了简化bean的配置，让整个bean对象的注册都是自动扫描的，那么需要的基本步骤是：扫描路径入口，xml解析扫描信息，给需要扫描的bean对象加上自定义注解，扫描Class对象摘取bean注册的基本信息，组装注册信息，注册成Bean对象，那么在这些元素的支撑下，就可以实现出通过自定义注解和配置扫描路径的方式，自动完成对Bean的注册。

除此之外还有一个比较好玩的功能，比如可以通过`${token}` 给 Bean 对象注入进去属性信息，那么这个操作需要用到 BeanFactoryPostProcessor，因为它可以处理 在所有的**BeanDefinition** 加载完成以后，实例化bean对象之前，提供修改**BeanDefinition** 的属性的机制。也就是@value注解的功能

结合bean的生命周期，包扫描只不过是扫描特定注解的类，提取类的相关信息组装成**BeanDefinition** 注册到容器中

在XmlBeanDefinitionReader中解析`<context:component-scan />`标签，扫描类组装BeanDefinition然后注册到容器中的操作在ClassPathBeanDefinitionScanner#doScan中实现

~~~java
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    /**
     * Default placeholder prefix: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * Default placeholder suffix: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private String location;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            // 加载属性文件
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);

            // 占位符替换属性值
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            // 由于这会BeanDefinition已经加载完毕，所以可以获取到所有的BeanDefinition
            // 然后在实例化之前，对BeanDefinition中的属性，也就是对象的属性填充 进行修改和扩展
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) continue;
                    value = resolvePlaceholder((String) value, properties);
                    //上一步解析出内容以后，现在添加到BeanDefinition的属性填充中
                    propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), value));
                }
            }

            // 向容器中添加字符串解析器，供解析@Value注解使用
            StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
            beanFactory.addEmbeddedValueResolver(valueResolver);

        } catch (IOException e) {
            throw new BeansException("Could not load properties", e);
        }
    }

    /**
     * 将${}替换掉，只保留里面的字符串内容
     */
    private String resolvePlaceholder(String value, Properties properties) {
        String strVal = value;
        StringBuilder buffer = new StringBuilder(strVal);
        int startIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int stopIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
        if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
            String propKey = strVal.substring(startIdx + 2, stopIdx);
            String propVal = properties.getProperty(propKey);
            buffer.replace(startIdx, stopIdx + 1, propVal);
        }
        return buffer.toString();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
        }

    }
}
~~~

依赖于 BeanFactoryPostProcessor 在 Bean 生命周期的属性，可以在 Bean 对象实例化之前，改变属性信息。所以这里通过实现 BeanFactoryPostProcessor 接口，完成对配置文件的加载以及摘取占位符中的在属性文件里的配置

这样就可以把从配置文件中提取到的配置信息放置到属性配置中了，`buffer.replace(startIdx, stopIdx + 1, propVal); propertyValues.addPropertyValue`

接下来先总结一下注解的原理：

在以前，xml文件是框架解耦合的杀手锏，它以松耦合的方式完成框架中所有的配置，但是随着项目越来越庞大，xml的内容也越来越多，难以维护，于是就又放弃了松耦合，采用高耦合的注解配置，在任何需要配置的的地方都可以进行注解，注解虽然和业务代码耦合在一起，但是由于使用成本很低，很简洁，也比较美观，所以牺牲了耦合性来获取高效率

对于注解的本质，用一句话来概括就是所有注解类型都继承Annotation接口，所以一个注解本质上就是一个继承了Annotation接口的接口

那么对于一个注解究竟怎么发挥作用，其实他只是一种特殊的标记，如果没有相应的解析代码，那么它连注释都不如

那么对于注解来说，重点应该关注的是解析的代码，而解析一个类或者方法的注解往往有两种方式：

1.编译期直接扫描：典型就是@Override，一旦某个方法被修饰了@Override，编译器就会检查当前方法的签名是否是真正重写了父类的方法，也就是比较父类中是否具有一个相同签名的方法，这种解析形式当然是虚拟机提供的能力，所以对于我们自定义的注解，如果不修改hotspot源码，我们的注解只能通过第二种方式进行解析使用

2.第二种就是运行期反射

元注解：

元注解就是用于修饰注解的注解，虽然普通注解的作用范围也有注解这个选项

@Target：注解的作用目标

@Retention：注解的生命周期

@Documented：注解是否应该被包含在javadoc中

@Inherited：是否允许子类继承该注解

重点说一下注解的生命周期：

1.source：当前注解编译期可见，不会写入字节码文件

2.class：类加载阶段丢弃，会写入字节码文件

3.runtime：永久保存，可以反射获取

有些注解会在编译期结束后被丢弃，还有一种是会被编译器编译进字节码文件，无论是类还是方法，乃至字段，他们都有属性表，而java虚拟机也定义了几种注解属性表用来存储注解信息，但是这种可见性不会被带到方法区，类加载时会被丢弃，由于这个注解信息不会被带入到方法区，所以反射无法获取。

@Override是一个典型的编译型注解，生命周期为source，说明主要作用是编译期间进行一个检查，编译器在对java文件编译成字节码的过程中，一旦检测到某个方法上面标注了@Override注解，就会去父类匹配是否有一个同样方法 签名的方法，如果匹配不上，那么编译不通过，一旦编译通过，这个注解就被清除，class文件中不会留下痕迹

另外注解的本质再提一次，注解本质上是继承了 Annotation 接口的接口，而当你通过反射，也就是 getAnnotation 方法去获取一个注解类实例的时候，其实 JDK 是通过动态代理机制生成一个实现我们注解（接口）的代理类

~~~java
    @LJL
    public static void main(String[] args) throws NoSuchMethodException {
        Class<?> clazz = LJLTest.class;
        Method method = clazz.getMethod("main", String[].class);
        LJL annotation = method.getAnnotation(LJL.class);
        
    }
~~~

最后总结一下注解的工作流程：

首先我们可以通过键值对的形式为注解属性赋值，其实就是为接口中的方法赋值（明明是属性操作，非要用方法来实现）

接着，用注解修饰某个元素，编译器将会在编译期扫描每个类或者方法上的注解，做一个基本的检查，看注解是否允许出现在这个位置，最后会将注解的信息写到元素的属性表中，也就是class文件的常量池表中，当然如果标记的是class类型的生命周期，那么不会进入方法区，所以无法反射

接下来，对于可以反射获取的注解，虚拟机会将所有生命周期为runtime的注解取出来放到一个map中，来创建一个AnnotationInvocationHandler 实例，因为注解他是接口，获取注解实例其实是通过jdk动态代理获取它的代理类，这个map会传给AnnotationInvocationHandler 。

这样一个注解实例就被创建出来了，他本质就是一个代理类，那么invoke方法的核心逻辑就是通过方法名返回注解属性值

注解定义完以后，项目中应该要处理注解

~~~java
// 处理对象扫描装配
public class ClassPathScanningCandidateComponentProvider {

    /**
     * 这里先要提供一个可以通过配置路径 basePackage=com.learn.myspring.test.bean
     * 解析出 classes 信息的工具方法 findCandidateComponents，
     * 通过这个方法就可以扫描到所有 @Component 注解的 Bean 对象了
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> clazz : classes) {
            candidates.add(new BeanDefinition(clazz));
        }
        return candidates;
    }

}
~~~

这里使用了hutool工具类，scanPackageByAnnotation：扫描指定包下所有包含指定注解的类，就是@component注解标注的类

通过这个findCandidateComponents方法，就可以扫描到指定包中所有含有@component注解的类，拿到他们的class对象后，就可以创建BeanDefinition了

接下来是注册的过程

```java
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private BeanDefinitionRegistry registry;

    // 拿到了所有的BeanDefinition，下一步应该将其注册到容器中，所谓的注册其实就是放到beanDefinitionMap中
    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidates) {
                // 解析 Bean 的作用域 singleton、prototype
                String beanScope = resolveBeanScope(beanDefinition);
                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }
                registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
            }
        }
        // 注册处理注解的 BeanPostProcessor（@Autowired、@Value）
        registry.registerBeanDefinition("com.learn.myspring.context.annotation.internalAutowiredAnnotationProcessor",
                new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (null != scope) return scope.value();
        return StrUtil.EMPTY;
    }

    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }
}
```

原理也非常简单，来个继承关系，就可以使用父类的查找指定包指定注解的类的方法，拿到所有的bean定义后，下一步就是注册，当然在注册前还得看一下bean的作用域，以及component注解中是否配置了value值，也就是Bean的名称，没有配置的话就是类名首字母小写

注解处理的代码写完后，还要写解析xml配置的代码，因为目前还是需要componentScan标签来指定扫描哪些包

~~~java
        // 解析 context:component-scan 标签，扫描包中的类并提取相关信息，用于组装 BeanDefinition
        Element componentScan = root.element("component-scan");
        if (null != componentScan) {
            String scanPath = componentScan.attributeValue("base-package");
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath);
        }
~~~

~~~java
    private void scanPackage(String scanPath) {
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(basePackages);
    }
~~~

可以看到核心代码就是doScan，扫描打了加到bean定义的map中

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context">

    <context:component-scan base-package="com.duanxu.lijiale-spring.test.bean"/>

</beans>
~~~

配置文件中现在这样配置就可以扫描到所有标注了@component注解的类，从而加入到IOC容器中

___

### 通过注解给属性注入配置和给Bean对象自动注入其他对象

接下来实现的功能的是spring中大名鼎鼎的@Autowired注解以及比较实用的@value注解

其实在完成 Bean 对象的基础功能后，后续陆续添加的功能都是围绕着 Bean 的生命周期进行的，比如修改 Bean 的定义 BeanFactoryPostProcessor，处理 Bean 的属性要用到 BeanPostProcessor，完成个性的属性操作则专门继承 BeanPostProcessor 提供新的接口，因为这样才能通过 instanceof 判断出具有标记性的接口。所以关于 Bean 等等的操作，以及监听 Aware、获取 BeanFactory，都需要在 Bean 的生命周期中完成。那么我们在设计属性和 Bean 对象的注入时候，也会用到 BeanPostProcessor 来完成在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值

要处理自动扫描注入，包括属性注入、对象注入，则需要在对象属性 `applyPropertyValues` 填充之前 ，把属性信息写入到 PropertyValues 的集合中去。这一步的操作相当于是解决了以前在 spring.xml 配置属性的过程，非常巧妙啊，把这个功能加入到Bean生命周期，xml文件中需要写的就越来越少

而在属性的读取中，需要依赖于对 Bean 对象的类中属性的配置了注解的扫描，`field.getAnnotation(Value.class);` 依次拿出符合的属性并填充上相应的配置信息。这里有一点 ，属性的配置信息需要依赖于BeanFactoryPostProcessor 的实现类 PropertyPlaceholderConfigurer，把值写入到 AbstractBeanFactory的embeddedValueResolvers集合中，这样才能在属性填充中利用 beanFactory 获取相应的属性值

还有一个是关于 @Autowired 对于对象的注入，其实这一个和属性注入的唯一区别是对于对象的获取 beanFactory.getBean(fieldType)

当所有的属性被设置到 PropertyValues 完成以后，接下来就到了创建对象的下一步，属性填充，而此时就会把我们一一获取到的配置和对象填充到属性上，也就实现了自动注入的功能

整体实现过程围绕实现接口 InstantiationAwareBeanPostProcessor 的类 AutowiredAnnotationBeanPostProcessor 作为入口点，被 AbstractAutowireCapableBeanFactory创建 Bean 对象过程中调用扫描整个类的属性配置中含有自定义注解 `Value`、`Autowired`、`Qualifier`，的属性值

~~~java
//解析字符串操作
public interface StringValueResolver {

    String resolveStringValue(String strVal);

}
~~~

定义一个字符串解析接口，与spring源码一致

~~~java
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            // 加载属性文件
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            
            // ... 占位符替换属性值、设置属性值

            // 向容器中添加字符串解析器，供解析@Value注解使用
            StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
            beanFactory.addEmbeddedValueResolver(valueResolver);
            
        } catch (IOException e) {
            throw new BeansException("Could not load properties", e);
        }
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
        }

    }

}
~~~

在解析属性配置的类 PropertyPlaceholderConfigurer 中，最主要的其实就是这行代码的操作 `beanFactory.addEmbeddedValueResolver(valueResolver)` 这是把属性值写入到了 AbstractBeanFactory 的 embeddedValueResolvers 中添加了一个字符串解析器。

embeddedValueResolvers 是 AbstractBeanFactory 类新增加的集合 `List<StringValueResolver> embeddedValueResolvers`

接下来自定义注解的代码省略，主要是@Value ，@component，@Autowired，用来注入对象和注入属性

而 Qualifier 一般与 Autowired 配合使用，当自动注入一个接口时，如果这个接口有很多的实现类，那么在使用的时候spring不知道到底应该调用哪个实现类中的方法，所以配合Qualifier 可以指定实现类，当然也可以直接在controller中注入想要的实现类

~~~java
// AutowiredAnnotationBeanPostProcessor 是实现接口 InstantiationAwareBeanPostProcessor 的一个用于在 Bean 对象实例化完成后，
// 设置属性操作前的处理属性信息的类和操作方法。只有实现了 BeanPostProcessor 接口才有机会在 Bean 的生命周期中处理初始化信息
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        // 1. 处理注解 @Value
        Class<?> clazz = bean.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (null != valueAnnotation) {
                String value = valueAnnotation.value();
                value = beanFactory.resolveEmbeddedValue(value);
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }

        // 2. 处理注解 @Autowired
        for (Field field : declaredFields) {
            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (null != autowiredAnnotation) {
                Class<?> fieldType = field.getType();
                String dependentBeanName = null;
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                Object dependentBean = null;
                if (null != qualifierAnnotation) {
                    dependentBeanName = qualifierAnnotation.value();
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    dependentBean = beanFactory.getBean(fieldType);
                }
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }

        return pvs;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

}
~~~

核心逻辑也非常简单，由于这个方法是在bean的实例化之后进行的，那么可以获取到这个类的类型，然后通过反射获取属性上标注的注解

最终进行相应的处理，比如对于value注解来说，解析出来后直接把这个属性值用beanutil进行设置，对于autowired，需要注意加没加Qualifier注解，总体思路就是扫描加了注解的属性，然后通过反射获取属性的类型，拿到类型就可以走getBean的流程，就相当会把autowired注解标注的对象加入到ioc容器中，然后beanutil来设置属性值。

*只有实现了 BeanPostProcessor 接口才有机会在 Bean 的生命周期中处理初始化信息*

核心方法 postProcessPropertyValues，主要用于处理类含有 @Value、@Autowired 注解的属性，进行属性信息的提取和设置。

另外需要注意如果是cglib创建的对象，那么获取Bean的类型应该是父类的类型，否则不需要

最后需要将这些功能整合到Bean的生命周期：

~~~java
 protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            // 实例化 Bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 处理循环依赖，将实例化后的Bean对象提前放入缓存中暴露出来
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }

            // 实例化后判断
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }
            // 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
            // 给 Bean 填充属性
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        // 注册实现了 DisposableBean 接口的 Bean 对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
        Object exposedObject = bean;
        if (beanDefinition.isSingleton()) {
            // 获取代理对象
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
        return exposedObject;

    }
~~~

~~~java
    /**
     * 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
     */
    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor){
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (null != pvs) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }  
~~~

可以看到，给bean填充属性之前，需要应用一个beanpostprocessors，来处理加入进来的实现类

AbstractAutowireCapableBeanFactory#createBean 方法中有这一条新增加的方法调用，就是在`设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值` 的操作 `applyBeanPostProcessorsBeforeApplyingPropertyValues`

那么这个 applyBeanPostProcessorsBeforeApplyingPropertyValues 方法中，首先就是获取已经注入的 BeanPostProcessor 集合并从中筛选出继承接口 InstantiationAwareBeanPostProcessor 的实现类

最后就是调用相应的 postProcessPropertyValues 方法以及循环设置属性值信息，`beanDefinition.getPropertyValues().addPropertyValue(propertyValue);`

从整个注解信息扫描注入的实现内容来看，一直是围绕着在 Bean 的生命周期中进行处理，就像 BeanPostProcessor 用于修改新实例化 Bean 对象的扩展点，提供的接口方法可以用于处理 Bean 对象实例化前后进行处理操作。而有时候需要做一些差异化的控制，所以还需要继承 BeanPostProcessor 接口，定义新的接口 InstantiationAwareBeanPostProcessor 这样就可以区分出不同扩展点的操作了

像是接口用 instanceof 判断，注解用 Field.getAnnotation(Value.class); 获取，都是相当于在类上做的一些标识性信息，便于可以用一些方法找到这些功能点，以便进行处理。所以在我们日常开发设计的组件中，也可以运用上这些特点。

____

### 三级缓存处理循环依赖

A的完整创建依赖于B，B的完整创建依赖于A，那么在创建对象的时候就会出现循环依赖，首先循环依赖的产生肯定是通过set方法注入产生的 ，如果构造注入，那么编译都过不去，并且对于spring来说，他解决的循环依赖问题是单例Bean的循环依赖，因为只有单例Bean才会进入缓存，可以借助缓存的思想来完成完整对象的创建，spring中默认的bean的作用域也是单例。所以在日常开发中，spring已经替我们解决了循环依赖的问题。

按照spring框架的设计，用于解决循环依赖用到了三个缓存，分别存放成品对象，半成品对象（实例创建完毕，属性没填充），代理对象分阶段存储对象内容，来解决循环依赖问题

解决循环依赖的核心原理：如果只用一级缓存，其实都能解决普通对象的循环依赖：

~~~java
public class CircleTest {

    /**
     * 此demo仅使用一级缓存来解决最普通场景中的循环依赖问题
     */
    private final static Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    @Test
    public void test() throws Exception {
        System.out.println(getBean(B.class).getA());
        System.out.println(getBean(A.class).getB());
    }

    private static <T> T getBean(Class<T> beanClass) throws Exception {
        String beanName = beanClass.getSimpleName().toLowerCase();
        System.out.println(beanName);
        if (singletonObjects.containsKey(beanName)) {
            return (T) singletonObjects.get(beanName);
        }
        // 实例化对象入缓存
        //反射，默认空参构造
        Object bean = beanClass.newInstance();
        singletonObjects.put(beanName, bean);
        // 属性填充补全对象
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            String fieldBeanName = fieldClass.getSimpleName().toLowerCase();
            // 递归创建
            field.set(bean, singletonObjects.containsKey(fieldBeanName) ? singletonObjects.get(fieldBeanName)
                    : getBean(fieldClass));
            field.setAccessible(false);
        }
        return (T) bean;
    }

}

class A {

    private B b;

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}

class B {

    private A a;

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }
}
~~~

可以看到既然一个对象完整的创建需要用到另一个对象，那么在一个对象实例化完成以后，填充属性以前，就把这个对象放到缓存中，然后开始填充属性，填充的时候发现还依赖一个对象，那么尝试从缓存中获取，第一次肯定获取不到，所以递归调用，开始创建对象B，B实例化完成以后也加入缓存，然后B开始填充属性，B发现缓存中有对象A，就把A取出来填充属性，那么这时候B就是完整的对象了，开始返回一个完整的B对象，由于是递归，返回了一个B后A继续它的流程，开始填充属性，这时候B是完整的，填充进去，最终两个对象都创建完毕

可以看到只使用一级缓存就可以解决普通对象的循环依赖问题，但是一级缓存这种写法不太优雅，我们希望一级缓存就是存放完整的单例对象，（在解决循环依赖问题前，这个一级缓存其实已经就存在了，是用来存放单例bean 的，也叫单例池，单例对象的获取都直接从这里获取），多例对象每个线程get的时候都要重新创建。

那么为了分离bean对象的完整性，引入了二级缓存，专门用来存放半成品对象，但是spring不只有ioc，还有aop，而三级缓存最主要解决的就是对aop的处理，但如果把aop代理对象的创建提前，那么二级缓存也一样可以解决问题，但是这违背了spring创建对象的原则，spring更喜欢把普通对象都初始化完成，再处理代理对象的初始化

那么现在开始模仿spring使用三级缓存处理循环依赖，主要就是对创建对象的提前暴露，如果是工厂对象则会使用getEarlyBeanReference逻辑提前将工厂对象存放到三级缓存，等到后续获取对象的时候实际拿到的是工厂对象中getobject，这个才是最终的实际对象

在创建对象的AbstractAutowireCapableBeanFactory的docreatebean方法中，提前暴露对象后，就可以接下来的流程，getsingleton方法依次从一级二级三级缓存中获取，一级二级有直接取走，如果对象在三级缓存，则会取出来放到二级缓存，并把三级缓存中的对象删除。

至于单例对象的注册操作，直接放到一级缓存中就ok了

核心流程：

循环依赖的核心功能实现主要包括DefaultSingletonBeanRegistry提供三级缓存，singleobjects，earlysingletonobjects，singletonfactories，分别存放成品，半成品和工厂对象，同时提供三个缓存包装方法，getsingleton，registersingleton，addsingletonfactiory，这样使用方就可以在不同的时间段存放和获取相应的对象

在 AbstractAutowireCapableBeanFactory 的 doCreateBean 方法中，提供了关于提前暴露对象的操作， addSingletonFactory(beanName, () ->getEarlyBeanReference(beanName, beanDefinition,finalBean));

以及后续获取对象和注册对象的操作， exposedObject =getSingleton(beanName); 、 registerSingleton(beanName,exposedObject);

经过这样的处理就可以完成对复杂场景循环依赖的操作。

另外DefaultAdvisorAutoProxyCreator提供的切面服务中，也需要实现接口InstantiationAwareBeanPostProcessor中新增的getEarlyBeanReference方法，便于把依赖的切面对象也能存放到三级缓存中，处理对应的循环依赖

~~~java
    // 一级缓存，普通对象
    /**
     * Cache of singleton objects: bean name --> bean instance
     */ 
   private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // 二级缓存，提前暴漏对象，没有完全实例化的对象
    /**
     * Cache of early singleton objects: bean name --> bean instance
     */
    protected final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();

    // 三级缓存，存放代理对象
    /**
     * Cache of singleton factories: bean name --> ObjectFactory
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();
~~~

可以看到只有一级缓存需要保证线程安全，因为这个是直接给用户提供使用的，另外一级二级缓存的key都是string，value都是object，三级缓存的key是string，value是ObjectFactory接口，里面定义一个getobject方法。

~~~java
  // 从单例池中获取单例Bean
    @Override
    public Object getSingleton(String beanName) {
        //一级缓存去拿第一个Bean对象，Bean还没创建出来，singletonObject肯定为null
        Object singletonObject = singletonObjects.get(beanName);
        if (null == singletonObject) {
            singletonObject = earlySingletonObjects.get(beanName);
            // 判断二级缓存中是否有对象，这个对象就是代理对象，因为只有代理对象才会放到三级缓存中
            if (null == singletonObject) {
                // B创建好需要填充A的时候，发现A在三级缓存中有，那么将A加入二级缓存，并从三级缓存中删掉
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    // 把三级缓存中的代理对象中的真实对象获取出来，放入二级缓存中
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    // 循环依赖的后一个Bean创建成功后，加入一级缓存，删除二三级缓存，
    // 这样折回去创建第一个Bean的时候从一级缓存直接取出这个后依赖的Bean
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory){
        // 如果一级缓存中没有要获取的对象，返回false，false取反，方法进入
        if (!this.singletonObjects.containsKey(beanName)) {
            // 一级缓存没有，那就加入三级缓存，最终B对象也会被加入三级缓存
            this.singletonFactories.put(beanName, singletonFactory);
            // 清除二级缓存
            this.earlySingletonObjects.remove(beanName);
        }
    }
~~~

在用于提供单例对象注册的操作的DefaultSingletonBeanRegistry类中，把三级缓存属性定义好，分别存放不同类型的对象，紧接着提供了获取，注册和添加对象的方法

在创建bean的时候还是谨记那个思想，对象实例化完成以后填充属性以前，一定要提前暴露

~~~java
  protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            // 实例化 Bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 处理循环依赖，将实例化后的Bean对象提前放入缓存中暴露出来
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }
                。
                。
                。
    }
~~~

~~~java
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor) beanPostProcessor)
                        .getEarlyBeanReference(exposedObject, beanName);
                if (null == exposedObject) return exposedObject;
            }
        }

        return exposedObject;
    }
~~~

~~~java
        // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
        Object exposedObject = bean;
        if (beanDefinition.isSingleton()) {
            // 获取代理对象
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
~~~

在AbstractAutowireCapableBeanFactory中的createBean方法中，主要是扩展了对象的提前暴露addSingletonFactory，和单例对象的获取getSingleton以及注册操作registerSingleton

getEarlyBeanReference就是定义在如AOP切面中这样的代理对象，参考源码：InstantiationAwareBeanPostProcessor#getEarlyBeanReference

总结一下A,B两个对象在三级缓存中的迁移：

~~~wiki
1. A创建过程中，需要使用到B，于是A将自己放到三级缓存中，开始填充属性，需要用到B，然后去实例化B
2. B实例化的过程中 (B也先将自己放到三级缓存中) ，需要A，于是B先在一级缓存中查找A，没有 ，再查找二级缓存，还是没有，再查找三级缓存，在三级缓存里面找到了A，然后把三级缓存里的A放到二级缓存，然后从三级缓存中删除A
3. B初始化完成后将B本身放到一级缓存中，此时B里面的A仍然是创建中的状态，也就是实例化完成，但并未完成初始化。
接着回去创建A，此时B已经创建完成，直接从一级缓存里面拿到B，然后完成A的创建，并将A自己放到一级缓存中
~~~

