# Auto-generated from index/vul.py split by module.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {'sql_injection_hsqldb_attack': {'method': 'GET',
                                 'url': "http://{}:9989/hsqldb?username=1'".format(host),
                                 'headers': {'Content-Type': 'application/json'},
                                 'name': 'SQL注入-hsqldb',
                                 'type': 'attack'},
 'sql_injection_Hibernate_attack': {'method': 'GET',
                                    'url': ("http://{}:9988/Hibernate_injection?username=foobar' OR (SELECT "
                                            "COUNT(*) FROM User)>=0 OR 'foobar'='").format(host),
                                    'headers': {'Content-Type': 'application/json'},
                                    'name': 'SQL注入-Hibernate',
                                    'type': 'attack'},
 'sql_injection_hsqldb_normal': {'method': 'GET',
                                 'url': "http://{}:9989/hsqldb?username=1'".format(host),
                                 'headers': {'Content-Type': 'application/json'},
                                 'name': 'SQL注入-hsqldb',
                                 'type': 'normal'},
 'sql_injection_hsqldb_repair': {'method': 'GET',
                                 'url': "http://{}:9989/hsqldb_repair?username=1'".format(host),
                                 'headers': {'Content-Type': 'application/json'},
                                 'name': 'SQL注入-hsqldb',
                                 'type': 'repair'},
 'sql_injection_Hibernate_repair': {'method': 'GET',
                                    'url': ("http://{}:9988/Hibernate_injection_repair?username=foobar' OR "
                                            "(SELECT COUNT(*) FROM User)>=0 OR 'foobar'='").format(host),
                                    'headers': {'Content-Type': 'application/json'},
                                    'name': 'SQL注入-Hibernate',
                                    'type': 'repair'}}
