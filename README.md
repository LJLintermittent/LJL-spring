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

