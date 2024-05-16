from vul import requests_config

requests_list = [
    {
        'api': name,
        'name': details['name'],
        'method': details['method'],
        'url': details['url'],
        'type': '攻击' if details['type'] == 'attack' else ('正常' if details['type'] == 'normal' else ('修复' if details['type'] == 'repair' else '误报'))
    }
    for name, details in requests_config.items()
]

# Sorting the list based on 'type'
requests_list_sorted = sorted(requests_list, key=lambda x: x['type'])

# Creating Markdown table with 'name' field included
markdown_table_with_name = "| 接口 | 漏洞名字 | 请求方法 | url | 接口类型 |\n"
markdown_table_with_name += "| :----------------------------------------: | :---------------------------------------------------------: | -------- | :----------------------------------------------------------: | :------: |\n"
for item in requests_list_sorted:
    name_with_escaped_pipe = item['name'].replace('|', '\\|')
    markdown_table_with_name += "| {api} | {name} | {method} | {url} | {type} |\n".format(api=item['api'],
                                                                                          name=name_with_escaped_pipe,
                                                                                          method=item['method'],
                                                                                          url=item['url'],
                                                                                          type=item['type'])

print(markdown_table_with_name)