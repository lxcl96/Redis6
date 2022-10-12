# 前言

详细文档参考：

[尚硅谷_Redis6课件.docx](./尚硅谷_Redis6课件.docx)

# 1、NoSQL数据库简介

## 1.1、技术发展

技术分类：

+ 解决功能性的问题：Java、Jsp、RDBMS、Tomcat、HTML、Linux、JDBC、SVN
+ 解决扩展性的问题：Struts、Spring、SpringMVC、Hibernate、Mybatis
+ 解决性能的问题：NoSQL、Java线程、Hadoop、Nginx、MQ、ElasticSearch

## 1.2、NoSQL数据库

### 1.2.1、NoSQL数据库概述

NoSQL即Not Only SQL，意思为“不仅仅为是SQL”，泛指**非关系型的数据库**。

NoSQL不依赖业务逻辑方式存储，而是以简单的`key-value`模式存储。因此大大的增加了数据库的扩展能力。

**NoSQL特点：**

+ 不遵循SQL标准

+ 不支持ACID（但是支持事务）

  > 即事务的四个个性：原子性、一致性、隔离性、持久性

+ 远超SQL的性能

### 1.2.2、NoSQL适用场景

+ 对数据高并发的读写
+ 海量数据的读写
+ 对数据高可扩展性的

### 1.2.3、NoSQL不适用场景

+ 需要事务支持
+ 基于sql的结构化查询存储，处理复杂的关系，需要**即席**查询
+ <font color='red'>用不着sql的和用了sql也不行的情况下，请考虑用NoSQL</font>

### 1.2.4、常见的NoSQL数据库

+ Memcache
+ Redis
+ MongoDB

# 2、Redis6概述和安装

## 2.1、Redis安装

安装版本：redis-6.2.1.tar.gz

```sh
#下载安装最新版的gcc编译器 8.3.1
yum install centos-release-scl scl-utils-build
yum install -y devtoolset-8-toolchain
scl enable devtoolset-8 bash
gcc -version

#解压redis文件
tar -zvxf redis-6.2.1.tar.gz
# 编译安装
cd redis-6.2.1.tar.gz/
make
make install # 程序默认安装在 /usr/local/bin目录下
```

## 2.2、redis文件介绍

***`/usr/local/bin 目录下redis程序：`***

+ `redis-benchmark`：性能测试工具
+ `redis-check-aof`：修复有问题的AOF文件，rdb和aof后面讲
+ `redis-check-dump`：修复有问题的dump.rdb文件
+ `redis-sentinel`：Redis集群使用
+ `redis-server`：Redis服务器启动命令
+ `redis-cli`：客户端，操作入口

***`Redis加压的目录下 redis配置文件`*** 

+ 

## 2.3、Redis前台启动（不推荐）

> <img src='img\image-20221012133854486.png'></img>

## 2.4、Redis后台启动（推荐）

+ **备份`redis.conf`文件**

  > 将redis解压后目录下的`redis.conf`拷贝到`/etc`目录下（当然你可以放到其他任意目录）

+ **后台启动设置<font color='red'>daemonize no</font>改为<font color='red'>daemonize  yes</font>**

  > `redis.conf`配置文件里的`daemonize no` 改成`daemonize yes`，让服务器在后台启动（可以关键字搜索：/deam）

+ **根据redis的配置文件，启动redis**

  > ```sh
  > #需要进入redis-server所在的目录
  > redis-server /etc/redis.conf
  > ```

+ **检查redis进程**

  > ```sh
  > ps -ef|grep 'redis'|grep -v 'grep'
  > ```
  >
  > <img src='img\image-20221012135448536.png'/>

+ **通过`redis-cli`连接到redis服务**

  > ```sh
  > /usr/local/bin/redis-cli 
  > ```

## 2.5、Redis关闭

+ 进入redis客户端即`redis-cli`中，输入命令`shutdown`
+ 或者在命令行中，根据redis的pid杀掉

## 2.6、Redis相关知识

### 2.6.1、Redis端口6379来历

6379在是手机按键上MERZ对应的号码，而MERZ取自意大利歌女Alessia Merz的名字。MERZ长期以来被Redis作者antirez及其朋友当作愚蠢的代名词。后来Redis作者在开发Redis时就选用了这个端口。

### 2.6.2、==Redis数据库==

+ Redis默认16个数据库，类似于数组下标从0开始，<font color='red'>初始默认使用0号数据库</font>
+ 切换不同的数据库，通过`select 下标`
+ 16个数据统一密码管理，即所有库密码相同
+ <font color='red'>`dbsize命令`</font>查看当前数据库的key的数量
+ <font color='red'>`flushdb命令` </font>清空当前库
+ <font color='red'>`flushall命令` </font>通杀所有数据库

### 2.6.2、Redis底层原理

**Redis是单线程+多路IO复用技术**

多路复用是指使用一个线程来检查多个文件描述符（Socket）的就绪状态，比如调用select和poll函数，传入多个文件描述符，如果有一个文件描述符就绪，则返回。否则阻塞直到超时。得到就绪状态后进行真正的操作可以在同一个线程里执行，也可以启动线程执行（如使用线程池）

<img src='img\image-20221012142147131.png'/>

**举例：**

<img src='img\image-20221012142642133.png'>

# 3、常用的5大数据类型

哪里去获得redis常见数据类型操作命令http://www.redis.cn/commands.html

## 3.1、Redis键（key）常用命令

```sh
# 查看当前数据库中的所有key
keys *
# 判断某个key是否存在
exists key
# 查看某个key是什么类型
type key


# 删除指定key的数据 【立马删除】
del key
# 根据value选择非阻塞删除 【仅将key从keyspace元数据中删除，真正的删除会在后续异步操作】
unlink key


# 给key设置过期时间，默认单位为s 【不设置默认为永不过期即-1】
expire key 10 # 表示某个key 在10秒钟后过期
# 查看某个key还有多少秒过期， -1表示永不过期  -2表示已经过期（过期就自动删除了）
ttl key

# 切换数据库0-15
select 0~15
#查看当前数据库key的个数
dbsize
# 清空当前数据库
flushdb
# 通杀所有数据库
flushall


```

## 3.2、Redis字符串（String）

### 3.2.1、简介

String是Redis最基本的类型，你可以理解为与Memcache一模一样的类型，一个key对应一个value

<font color='red'>String类型是二进制安全的。这意味着Redis的string可以包含任何数据。比如jpg图片或者序列化的对象</font>

String类型是Redis最基本的数据类型，<font color='red'>一个Redis中字符串value最多可以是512M</font>

### 3.2.2、常用命令

```sh
# 存值 【如果多次存储的key相同，则只保存最新的值】
set key value
# 取值
get key

# 将值追加到该key对应的原来的value上，并返回追加后value的总长度 【如果原来不存在则，等价于set】
append k1 v100 # 如果k1原来是abc，则更新后为abcv100，并返回长度7 ，如果原来k1不存在，则就是存储set

# 获取某个key的长度，如果key不存在则返回0
strlen k1 #返回4


# 仅在key不存在时，设置生效 比如k1已经存在，再使用stenx就不会生效【区别于set】
setnx key value

# 将某个key对应的value数字+1 【仅对数字类型生效，否则会报错】，如果key不存在，则等价于set key 1
incr key
# 将某个key对应的value数字-1 【进队数字类型生效，否则会报错】，如果key不存在，则等价于set key -1
decr key
# 将值增加或减少制定步长
incrby/decrby key step # incrby key 5

# 同时设置一个或多个key-value 
mset k1 v1 k2 v2 ...
# 同时获取一个或多个value
mget k1 k2 k3 ...
# 同时设置一个或多个key-value，当且仅当所有给定的key均不存在 [根据原子性有一个失效则都失败,即只要有一个key存在则全都失效]
msetnx k1 v1 k2 v2 ...

# 获取某个key对应value的指定长度子字符串，类似java中substring
getrange key 0 3 # 获取value的指定长度，越界了会报错
# 用value覆写key所存储的字符串值，从起始位置开始，（索引从0开始）可以设置任意位置，如果越界了，中间空的用\x00表示
setrange key 0 lucy # 将key对应的value从0开始长度为lucy长度的字符串替换为lucy，如原来为aaaahello则替换成了lucyhello

# 设置值的时候设置过期时间   秒为单位
setex key 过期秒数 value


#以新值换旧值，设置新值的同时返回旧值，如果key不存在则返回空
getset key value
```

<img src="img\image-20221012151854362.png">

**与Java比较：**

<img src='img\image-20221012151702919.png'>

### 3.2.3、底层数据结构

String的数据结构为简单动态字符串(Simple Dynamic String,缩写SDS)。是可以修改的字符串，内部结构实现上类似于Java的ArrayList，采用预分配冗余空间的方式来减少内存的频繁分配.

<img src='img\image-20221012154857466.png'>

如图中所示，内部为当前字符串实际分配的空间capacity一般要高于实际字符串长度len。**当字符串长度小于1M时，扩容都是加倍现有的空间，如果超过1M，扩容时一次只会多扩1M的空间。需要注意的是字符串最大长度为512M。**



## 3.3、Redis列表（List）

### 3.3.1、简介

单键多值

Redis列表是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）

它的底层实际是一个<font color='red'>双向链表,对两端的操作性能很高，通过索引下标的操作中间的节点性能会较差。</font>

<img src='img\image-20221012160054627.png'>

### 3.3.2、常用命令

```sh
# 从左边/右边插入一个或多个值
lpush k1 v1 v2 v3 ... # 存储的效果：v3 v2 v1  【头插法】
rpush k1 v1 v2 v3... # 存储的效果：v1 v2 v3 【尾插法】

# 从key对应的list中取值，从左向右取值 包含start和stop
lrange key start stop # lrange key 0 -1表示取出所有

# 从左边或右边吐出countg个值  【值在键在，值光键就不存在了】
lpop key count
rpop key count

# 从key1列表右边吐出一个值，放到key2的左边
rpoplpush key1 key2

# 根据指定index位置，获取key的元素 【从0开始，-1表示最后一个元素】
lindex key index

# 指定key的，在value的前面面插入newvalue值
linsert key before value newvalue
# 指定key的，在value的后面插入newvalue值
linsert key after value newvalue

# 从左边开始删除n个value 【list中重复的value值，不重复也可以】
lrem key n value

# 将key对应列表的第index元素替换为newvalue 【下标从0开始】
lset key index newvalue
```



### 3.3.3、底层数据结构

List的数据结构为快速链表quickList。

首先在列表元素较少的情况下会使用一块连续的内存存储，这个结构是ziplist，也即是压缩列表。

它将所有的元素紧挨着一起存储，分配的是一块连续的内存。当数据量比较多的时候才会改成quicklist。

<img src='img\image-20221012164156848.png' >

因为普通的链表需要的附加指针空间太大，会比较浪费空间。比如这个列表里存的只是int类型的数据，结构上还需要两个额外的指针prev和next。



## 3.4、Redis集合（Set）

### 3.4.1、简介

<font color='red'>Redis set对外提供的功能与list类似是一个列表的功能，特殊之处在于set是可以自动排重的，且是无序的</font>，当你需要存储一个列表数据，又不希望出现重复数据时，set是一个很好的选择，并且set提供了判断某个成员是否在一个set集合内的重要接口，这个也是list所不能提供的。

<font color='yellow'>Redis的Set是String类型的无序集合。它的底层其实就是一个value为null的hash表，所以添加，删除，查找的复杂度都是O(1)。</font>

一个算法，随着数据的增加，执行时间的长短，如果是O(1)，数据增加，查找数据的时间不变。

### 3.4.2、常用命令

```sh
# 将一个或多个member元素加入到集合key中，已经存在的member元素将被忽略
sadd key value1 value2 value3 ...

# 取出集合中所有的值
smembers key

# 判断集合key中是否含有该value值，如果有返回1，否则返回0
sismember key value

# 返回集合key中元素的个数
scard key

# 删除集合key中一个或多个member元素，返回删除member元素个数
srem key value1 value2 ...

# 随机从集合key中吐出一个member元素，并返回【值无键无】
spop key

# 随机从该集合中取出n个值。不会从集合中删除
srandmember key n

# 把source中value值移动到destination集合 【原集合中就没有value了】
smove source destination value
```



### 3.4.3、底层数据结构

# 4、Redis6配置文件详解



# 5、Redis6的发布和订阅



# 6、Redis6新数据类型



# 7、



# 8、



# 9、Redis6的事务操作



# 10、Redis6持久化之RDB



# 11、Redis6持久化之AOF



# 12、Redis6的主从复制



# 13、Redis6集群



# 14、Redis6应用问题解决



# 15、Redis6新功能