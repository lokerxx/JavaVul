# Auto-generated from index/vul.py split by module.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {'druid_unauthorized': {'method': 'GET',
                        'url': 'http://{}:9997/druid'.format(host),
                        'headers': {'Content-Type': 'application/json'},
                        'name': 'druid未授权漏洞',
                        'type': 'attack'},
 'druid_authorized': {'method': 'GET',
                      'url': 'http://{}:9996/druid'.format(host),
                      'headers': {'Content-Type': 'application/json'},
                      'name': 'druid未授权漏洞',
                      'type': 'repair'},
 'druid_sqlwall': {'method': 'GET',
                   'url': 'http://{}:9997/druid_sql?id=1'.format(host),
                   'headers': {'Content-Type': 'application/json'},
                   'name': 'druid-SQL防火墙',
                   'type': 'mistake'}}
