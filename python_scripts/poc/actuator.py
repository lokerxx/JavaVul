# Auto-generated from index/vul.py split by module.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {'actuator2_unauthorized': {'method': 'GET',
                            'url': 'http://{}:9995/actuator'.format(host),
                            'headers': {'Content-Type': 'application/json'},
                            'name': 'SpringBoot Actuator未授权访问漏洞2.X',
                            'type': 'attack'},
 'actuator2_authorized': {'method': 'GET',
                          'url': 'http://{}:9994/actuator'.format(host),
                          'headers': {'Content-Type': 'application/json'},
                          'name': 'SpringBoot Actuator未授权访问漏洞2.X',
                          'type': 'repair'},
 'actuator1_unauthorized': {'method': 'GET',
                            'url': 'http://{}:9993/trace'.format(host),
                            'headers': {'Content-Type': 'application/json'},
                            'name': 'SpringBoot Actuator未授权访问漏洞1.X',
                            'type': 'attack'},
 'actuator1_authorized': {'method': 'GET',
                          'url': 'http://{}:9992/trace'.format(host),
                          'headers': {'Content-Type': 'application/json'},
                          'name': 'SpringBoot Actuator未授权访问漏洞1.X',
                          'type': 'repair'}}
