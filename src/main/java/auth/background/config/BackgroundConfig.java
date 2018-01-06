package auth.background.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.dozer.DozerBeanMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rabbitmq.client.Channel;

import auth.background.util.BeanMapper;  
/**
 *  
 */
@Configuration
@ComponentScan("auth.background")
@EnableRabbit
@EnableRedisHttpSession 
@PropertySource(value = "db.properties", encoding = "UTF-8")
public class BackgroundConfig implements ResourceLoaderAware, EnvironmentAware{

//		@Bean
//	    public SqlSession getSqlSession(){
//	    	return DBUtils.openSqlSession();
//	    }
	
		@Bean
		DozerBeanMapper mapper()
		{
			DozerBeanMapper dzmapper = new DozerBeanMapper();
			List<String> mpFiles = new ArrayList<String>();    
			mpFiles.add("dozer-mapping.xml");    
			dzmapper.setMappingFiles(mpFiles);
		    return dzmapper;
		}
		@Bean
		BeanMapper beanmapper()
		{
			return new BeanMapper();
		}


		//------------------amqp config

	    @Bean  
	    public ConnectionFactory connectionFactory() {  
	        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();  
	        connectionFactory.setAddresses("192.168.1.115:5672,192.168.1.116:5672");  
	        connectionFactory.setUsername("clare");  
	        connectionFactory.setPassword("clare");  
	        connectionFactory.setVirtualHost("/");  
	        connectionFactory.setPublisherConfirms(true); //必须要设置  
	        return connectionFactory;  
	    }  
	    
	    @Bean  
	    //必须是prototype类型  
	    public RabbitTemplate rabbitTemplate() {  
	        RabbitTemplate template = new RabbitTemplate(connectionFactory());  
	        return template;  
	    }  
	    
	    @Bean
	    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
	        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	        factory.setConnectionFactory(connectionFactory());
	        factory.setConcurrentConsumers(1);
	        return factory;
	    }
	    
//	    @Bean
//	    public RedisClusterConfiguration getClusterConfiguration() {
//	        
//	            Map<String, Object> source = new HashMap<String, Object>();
//
//	            source.put("spring.redis.cluster.nodes", "192.168.1.111:7000,192.168.1.111:7001,192.168.1.111:7002");
//
//	            source.put("spring.redis.cluster.timeout", "120");
//
//	            source.put("spring.redis.cluster.max-redirects", "5");
//
//	           return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
//
//	          }
//
//	       @Bean
//
//	       public JedisConnectionFactory getConnectionFactory() {
//	    	   JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(getClusterConfiguration());  
//	    	   jedisConnectionFactory.afterPropertiesSet();  
//	           return jedisConnectionFactory;
//	          }
//
//	      @Bean
//	       public JedisClusterConnection getJedisClusterConnection() {
//	           return (JedisClusterConnection) getConnectionFactory().getConnection();
//	       }
//
//	       @Bean
//	       public RedisTemplate getRedisTemplate() {
//	           RedisTemplate clusterTemplate = new RedisTemplate();
//	           clusterTemplate.setConnectionFactory(getConnectionFactory());
////	           clusterTemplate.setKeySerializer(new DefaultKeySerializer());
////	           clusterTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
//	           return clusterTemplate;
//
//	          }
	     
	       @Bean
	       public JedisConnectionFactory getJedisconnectionFactory() {
	           JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration(),jedisPoolConfig());
	           jedisConnectionFactory.setUsePool(true);
	           jedisConnectionFactory.setTimeout(2000);
//	           jedisConnectionFactory.afterPropertiesSet();  
	           return jedisConnectionFactory; 
	       }
	       
	       /**
	        * redis集群配置
	        * 配置redis集群的结点及其它一些属性
	        * @return
	        */
	       private RedisClusterConfiguration redisClusterConfiguration(){
	           RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration();
	           redisClusterConfig.setClusterNodes(getClusterNodes());
	           redisClusterConfig.setMaxRedirects(3);
	           return redisClusterConfig;
	           
	       }
	       
	       /**
	        * JedisPoolConfig 配置
	        * 
	        * 配置JedisPoolConfig的各项属性
	        * 
	        * @return
	        */
	       private JedisPoolConfig jedisPoolConfig(){
	           JedisPoolConfig jedisPoolConfig= new JedisPoolConfig();
	           //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
	           jedisPoolConfig.setBlockWhenExhausted(true);
	           
	           //是否启用pool的jmx管理功能, 默认true
	           jedisPoolConfig.setJmxEnabled(true);
	           
	           //默认就好
	           //jedisPoolConfig.setJmxNamePrefix("pool");
	           
	           //jedis调用returnObject方法时，是否进行有效检查
	           jedisPoolConfig.setTestOnReturn(true);
	           
	           //是否启用后进先出, 默认true
	           jedisPoolConfig.setLifo(true);
	           
	           //最大空闲连接数, 默认8个
	           jedisPoolConfig.setMaxIdle(8);
	           
	           //最大连接数, 默认8个
	           jedisPoolConfig.setMaxTotal(8);
	           
	           //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
	           jedisPoolConfig.setMaxWaitMillis(-1);
	           
	           //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
	           jedisPoolConfig.setMinEvictableIdleTimeMillis(1800000);
	           
	           //最小空闲连接数, 默认0
	           jedisPoolConfig.setMinIdle(0);
	           
	           //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
	           jedisPoolConfig.setNumTestsPerEvictionRun(3);
	           
	           //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
	           jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
	           
	           //在获取连接的时候检查有效性, 默认false
	           jedisPoolConfig.setTestOnBorrow(false);
	           
	           //在空闲时检查有效性, 默认false
	           jedisPoolConfig.setTestWhileIdle(false);
	           
	           //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
	           jedisPoolConfig.setTimeBetweenEvictionRunsMillis(-1);
	           
	           return jedisPoolConfig;
	       }
	       
	       /**
	        * redis集群节点IP和端口的添加
	        * 节点：RedisNode redisNode = new RedisNode("127.0.0.1",6379);
	        * @return
	        */
	       private Set<RedisNode> getClusterNodes(){
	           // 添加redis集群的节点
	           Set<RedisNode> clusterNodes = new HashSet<RedisNode>();
	           // 这三个主节点是我本机的IP和端口,从节点没有加入 ，这里不是我真实的IP，虽然是内网，还是不要太直接了
	           clusterNodes.add(new RedisNode("172.16.32.139", 7000));
	           clusterNodes.add(new RedisNode("172.16.32.139", 7001));
	           clusterNodes.add(new RedisNode("172.16.32.139", 7002));
	           return clusterNodes;
	       }
	      
//		@Autowired
//	    DBConfig dbConfig;
 
//		@Bean
//		 DBConfig getDBConfig(){
//	        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyMVCConfig.class);
//	        DBConfig bean = context.getBean(DBConfig.class);
//	        return bean;
//		}
		
		 private ResourceLoader loader;
		 public void setResourceLoader(ResourceLoader resourceLoader) {
		      this.loader = resourceLoader;
		 }
		 private Environment environment;
		    
		 public void setEnvironment(Environment environment) {
		      this.environment = environment;
		 }
	    @Bean
	    public BasicDataSource dataSource() {
	       // System.out.println("driver:"+dbConfig.driver);
	        BasicDataSource dataSource = new BasicDataSource();
//	        String driver = dbConfig.driver;
//	        String url = dbConfig.url;
//	        String username = dbConfig.username;
//	        String password = dbConfig.password;
//	        int initialSize = dbConfig.initialSize;
//	        int maxActive = dbConfig.maxActive;
//	        int maxIdle = dbConfig.maxIdle;
//	        int minIdle = dbConfig.minIdle;
//	        int maxWait = dbConfig.maxWait;
//	        
//	        dataSource.setDriverClassName(driver);
//	        dataSource.setUrl(url);
//	        dataSource.setUsername(username);
//	        dataSource.setPassword(password);
//	        dataSource.setInitialSize(initialSize);
//	        dataSource.setMaxActive(maxActive);
//	        dataSource.setMaxIdle(maxIdle);
//	        dataSource.setMinIdle(minIdle);
//	        dataSource.setMaxWait(maxWait);
	        
	        //Resource resource = loader.getResource("db.properties");
	        //String url = environment.getProperty("url");
	         
	        dataSource.setDriverClassName(environment.resolvePlaceholders("${db.driver}"));//"com.mysql.jdbc.Driver"
	        dataSource.setUrl(environment.resolvePlaceholders("${db.url}"));//"jdbc:mysql://172.16.32.167:3306/Auth?characterEncoding=utf8&useSSL=false"
	        dataSource.setUsername(environment.resolvePlaceholders("${db.username}"));//"root"
	        dataSource.setPassword(environment.resolvePlaceholders("${db.password}"));//"123456Jy."
	        
	        return dataSource;
	    }
	    
	    @Bean
	    public SqlSessionFactoryBean sqlSessionFactoryBean() {
	        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
	        sqlSessionFactoryBean.setDataSource(dataSource());
	        try {
	            sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapping/*.xml"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return sqlSessionFactoryBean;
	    }
	    @Bean
	    public MapperScannerConfigurer mapperScannerConfigurer() {
	        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
	        mapperScannerConfigurer.setBasePackage("auth.background.dao");
	        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
	        return mapperScannerConfigurer;
	    }
	    @Bean
	    public DataSourceTransactionManager transactionManager() {
	        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
	        dataSourceTransactionManager.setDataSource(dataSource());
	        return dataSourceTransactionManager;
	    }
}
